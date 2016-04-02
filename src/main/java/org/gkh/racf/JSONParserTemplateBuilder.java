package org.gkh.racf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Parses the RACF data formats, documented on the web pages of the IBM support
 * knowledge center, into a JSON template.
 * 
 * @see <a
 *      href="https://www-01.ibm.com/support/knowledgecenter/SSLTBW_1.13.0/com.ibm.zos.r13.icha300/usr.htm">Record
 *      formats produced by the database unload utility</a>
 *      
 * @author Garve Hays
 *
 */
public class JSONParserTemplateBuilder {

	public static JSONObject parseToJSONRecord(String line)
			throws Exception {
		String[] offsetInfo;
		JSONObject recordType = null;
		offsetInfo = line.split("\\t"); // ("\\s+"); <--- all whitespace

		if (offsetInfo.length != 5) {
			throw new Exception(String.format(
					"Problems parsing, skipping this line. Found length = %d\n",
					offsetInfo.length));
		}

		recordType = new JSONObject();
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 0: // Field Name
				recordType.put("field-name", offsetInfo[0]);
				break;
			case 1: // Type
				recordType.put("type", offsetInfo[1]);
				break;
			case 2: // Start
				recordType.put("start", offsetInfo[2]);
				break;
			case 3: // End
				recordType.put("end", offsetInfo[3]);
				break;
			case 4: // Comment
				recordType.put("comments", offsetInfo[4]);
				break;
			default:
				break;
			}
		}

		return recordType;
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		String path = "/user-basic-data-record.txt";
		String lineSeparator = System.getProperty("line.separator");
		InputStream in = JSONParserTemplateBuilder.class
				.getResourceAsStream(path);
		
		Scanner scanner = new Scanner(in, "UTF-8");
		String blob = scanner.useDelimiter("\\A").next();
		scanner.close();

		String[] testLine;
		JSONObject recordType = null;
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		String previousComments;

		// (1) Split on CR/LF.
		int count = 0;
		String[] directives = blob.split(lineSeparator);
		for (String line : directives) {
			// Skip comments, i.e. lines preceded with '#'.
			if (line.startsWith("#")) {
				continue;
			}
			
			// (2) Now split on tabs. We should have 5 columns.
			testLine = line.split("\\t");
			if (testLine.length == 1) {
				// Comment column line wraps? Append the wrapped portion to the
				// previous 5th column element. In one case, we have a column
				// element that wraps for several lines! E.g. see USBD_NOPWD in
				// th user basic data record. Note: Here we benefit from a
				// side-effect; the continue statement takes us back to the top
				// of the loop without incrementing the counter. Consequently,
				// we continue to append to the last successfully inserted array
				// list element.
				if (count >= 1) {
					recordType = jsonList.get(count - 1);
					previousComments = recordType.getString("comments");
					recordType.put("comments", previousComments + testLine[0]);
					jsonList.set(count - 1, recordType);
				}
				continue;
			}

			try {
				recordType = parseToJSONRecord(line);
				jsonList.add(recordType);

				// Some fact finding for my little hack (see line #83, above --
				// in what position did the record type JSON object get
				// inserted?
				// jsonList.indexOf(recordType);
				// System.out.printf(
				// "Inserted at index: %d, current count is: %d\n",
				// jsonList.indexOf(recordType), count);
			} catch (Exception e) {
				if (e instanceof JSONException) {
					System.out.printf("System exiting: %s\n", e.getMessage());
					System.exit(1);
				} else {
					break;
				}
			}
			count++;
		}

		for (JSONObject json : jsonList) {
//			System.out.printf("%-20sstart:%3d end:%3d %s\n",
//					json.get("fieldName"), json.getInt("startPosition"),
//					json.getInt("endPosition"), json.getString("comments"));
			System.out.println(json.toString(2));
		}
	}
}
