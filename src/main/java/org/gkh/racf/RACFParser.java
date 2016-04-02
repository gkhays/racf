package org.gkh.racf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RACFParser {
	
	public static final String GROUP_RECORD_TYPE = "group-basic-data-record";
	public static final String IDENTITY_RECORD_TYPE = "user-basic-data-record";
	
	public void addRecord(JSONArray recordArray, String record)
			throws JSONException {
		recordArray.put(parseDataRecord(record, IDENTITY_RECORD_TYPE));
	}
	
	/**
	 * <pre>
	 * User basic data record (0200)
	 * Field Name			Start	End
	 * USBD_RECORD_TYPE		1		4
	 * USBD_NAME			6		13
	 * USBCREATE_DATE		15		24
	 * </pre>
	 * 
	 * Generalization: Pass the starting offset (-1) and the ending offset.
	 * @throws JSONException 
	 */
	public JSONObject parseDataRecord(String record,
			String recordType) throws JSONException {
		JSONObject jsonOffsets = Configuration.getRecordOffsetsAsJSON();
		JSONObject userRecordType = jsonOffsets.getJSONObject(recordType);
		JSONArray offsetArray = userRecordType.getJSONArray("offsets");

		int end, start;
		String fieldName, parsedValue;
		JSONObject offset, jsonRecord = new JSONObject();
		for (int i = 0; i < offsetArray.length(); i++) {
			offset = offsetArray.getJSONObject(i);
			fieldName = offset.getString("field-name");
			start = offset.getInt("start");
			end = offset.getInt("end");
			parsedValue = parseRecordWithOffset(record, start - 1, end);
			// TODO - Log instead of printf.
			//System.out.printf("%s: %s\n", fieldName, parsedValue);
			jsonRecord.put(fieldName, parsedValue.trim());
		}
		return jsonRecord;
	}
	
	/**
	 * IndexOutOfBoundsException - if the beginIndex is negative, or endIndex is
	 * larger than the length of this String object, or beginIndex is larger
	 * than endIndex.
	 * 
	 * @param record
	 *            a single line from the offloaded database file
	 * @param start
	 *            <strong>Note</strong>: the starting offset is 1 less than what
	 *            is documented in the data records
	 * @param end
	 *            the index of the last character position for the field
	 * @return the value found at the given offset
	 */
	public String parseRecordWithOffset(String record, int start, int end) {
		// Check for out-of-bounds exception.
		if ((start < 0) || (end > record.length()) || (start > end)) {
			return "";
		}
		return record.substring(start, end);
	}
	
	/**
	 * The compiler didn't like that the lambda function was throwing an
	 * exception.
	 * 
	 * @see <a
	 *      href="http://codingjunkie.net/functional-iterface-exceptions/">Java
	 *      8 Functional Interfaces and Checked Exceptions</a>
	 * 
	 * @param path
	 *            the relative file name
	 * @param desiredType
	 *            a 4-digit code indicating record type
	 * @return a {@link JSONArray} containing the parsed records
	 * @throws JSONException
	 */
	public JSONArray racfRecordsToJSONArray(String path,
			String desiredType) throws JSONException {
		JSONArray identityArray = new JSONArray();
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(fis));
			reader.lines()
					.filter(f -> f.startsWith(desiredType))
					.forEach(
							record -> {
								try {
									identityArray.put(parseDataRecord(record,
											IDENTITY_RECORD_TYPE));
								} catch (JSONException j) {
									try {
										reader.close();
										fis.close();
									} catch (Exception e) {
										// Do nothing.
									}
									throw new RuntimeException(j);
								}
							});			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return identityArray;
	}
	
	/**
	 * Parse and collect basic user data records into a {@link JSONArray} of
	 * {@link JSONObject} identity records.
	 * 
	 * @param records
	 *            an array of RACF offline database records
	 * @throws JSONException
	 */
	public JSONArray racfRecordsToJSONArray(String[] records,
			String desiredType) throws JSONException {
		JSONArray identityArray = new JSONArray();
		for (String record : records) {
			if (record.startsWith(desiredType)) {
				identityArray
						.put(parseDataRecord(record, IDENTITY_RECORD_TYPE));
			}
		}
		return identityArray;
	}
}
