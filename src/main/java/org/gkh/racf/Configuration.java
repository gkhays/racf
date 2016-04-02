package org.gkh.racf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Configuration {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static JSONObject getRecordOffsetsAsJSON() throws JSONException {
		String configString = inputStreamToString(
				loadResourceAsStream("/offsets.json"));
		JSONObject jsonOffsets = new JSONObject(configString);
		return jsonOffsets;
	}
	
	public static String inputStreamToString(InputStream is) {
		return inputStreamToString(is, false);
    }
	
	public static String inputStreamToString(InputStream is,
			boolean appendNewLine) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				if (appendNewLine) {
					sb.append(LINE_SEPARATOR);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
    }
	
	static InputStream loadResourceAsStream(String name) {
		return Configuration.class.getResourceAsStream(name);
	}
	
	/**
	 * In the high school programming class, they are teaching them to use
	 * Scanner, e.g.
	 * 
	 * <pre>
	 * {
	 * 	{@code
	 * 	String content = new Scanner(new File("filename"))
	 * 			.useDelimiter("\\n")
	 * 			.next();
	 * 	System.out.println(content);
	 * }
	 * </pre>
	 * 
	 * @see <a
	 *      href="http://stackoverflow.com/questions/3402735/what-is-simplest-way-to-read-a-file-into-string">What
	 *      is simplest way to read a file into String?</a>
	 * 
	 *      This is how I've always done it.
	 * 
	 *      <pre>
	 * {@code
	 * private String readFile( String file ) throws IOException {
	 *     BufferedReader reader = new BufferedReader( new FileReader (file));
	 *     String         line = null;
	 *     StringBuilder  stringBuilder = new StringBuilder();
	 *     String         ls = System.getProperty("line.separator");
	 * 
	 *     try {
	 *         while( ( line = reader.readLine() ) != null ) {
	 *             stringBuilder.append( line );
	 *             stringBuilder.append( ls );
	 *         }
	 * 
	 *         return stringBuilder.toString();
	 *     } finally {
	 *         reader.close();
	 *     }
	 * }
	 * }
	 * 
	 * </pre>
	 * @see <a
	 *      href="http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file">How
	 *      to create a Java String from the contents of a file?</a>
	 * 
	 * @see <a
	 *      href="http://stackoverflow.com/questions/16027229/reading-from-a-text-file-and-storing-in-a-string">Reading
	 *      from a text file and storing in a String [duplicate]</a>
	 * 
	 * @see <a
	 *      href="http://www.mkyong.com/java/how-to-read-file-in-java-fileinputstream/">How
	 *      to read file in Java – FileInputStream</a>
	 * 
	 * @param file
	 * @return 
	 */
	public static String readFileToString(String path) {
		File file = new File(path);
		FileInputStream fis = null;
		
		String delimitedString = null;
		
		try {
			fis = new FileInputStream(file);
			delimitedString = inputStreamToString(fis, true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return delimitedString;
	}
}
