package org.gkh.racf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Main {
	
	public static void printJSONArray(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			System.out.println(jsonArray.getJSONObject(i).toString(2));
		}
	}
	
	public static void printPath() {
		String path = String.format("%s%s%s", System.getProperty("user.dir"),
				File.separator,
				Main.class.getPackage().getName().replace(".", File.separator));
		System.out.println(path);
	}
	
	public static void saveJSONFile(String fileName, String jsonText)
			throws IOException {
		FileWriter writer = new FileWriter(
				String.format("test/%s.json",
				fileName));
		writer.write(jsonText);
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		
		String racfRecords = Configuration.readFileToString("test/racf.txt");
		RACFParser parser = new RACFParser();

		// (1) First test: how many lines do we have?
		// TODO - Splitting on "\\n" will work, but should I use
		// System.getProperty("line.separator") instead?
		String[] records = racfRecords.split(Configuration.LINE_SEPARATOR);

		// (2) Second test: parse group basic data records (0100).
		JSONArray groupArray = parser.racfRecordsToJSONArray(records, "0100");
		printJSONArray(groupArray);

		// (3) Try and parse basic user data records (0200).
		JSONArray identityArray = parser.racfRecordsToJSONArray(records, "0200");
		saveJSONFile("users2", identityArray.toString(2));
		printJSONArray(identityArray);
		
		identityArray = parser.racfRecordsToJSONArray("test/racf.txt", "0200");
		printPath();
		saveJSONFile("users", identityArray.toString(2));
		printJSONArray(identityArray);

		// Quick and dirty! I like it! I wanted to verify the line separator on
		// Windows. I expected 0xD 0xA (for CRLF).
		// See: http://stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
		String lineSeparator = Configuration.LINE_SEPARATOR;
		System.out.printf(
				"Line separator: %s\n",
				String.format("%040x",
						new BigInteger(1, lineSeparator.getBytes("UTF-8"))));

		System.out.printf("Found %d group basic data records\n",
				groupArray.length());
		System.out.printf("Found %d user basic data records\n",
				identityArray.length());		
		System.out.printf("Found %d total records\n", records.length);
		
		// Test look-up tables.
		System.out.println("Testing look-up tables...");
		
		// (A) User types.
		testUserTypes();
		
		// (B) Group types.
		testGroupTypes();
	}
	
	// Line separator: 0000000000000000000000000000000000000d0a
	// Found 317 group basic data records
	// Found 1072 user basic data records
	// Found 21023 total records
	// Testing look-up tables...
	// Found 33 user types
	// Found 11 group types

	/**
	 * Test the group types look-up table.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void testGroupTypes() throws IOException, JSONException {
		String path = "test/racf-group-types.json";
		JSONObject group = JSONUtil.getJSONFromFile(path);
		JSONArray groupTypes = group.getJSONArray("racf-group");
		System.out.printf("Found %d group types\n", groupTypes.length());
	}
	
	/**
	 * Test the user types look-up table.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void testUserTypes() throws IOException, JSONException {
		String path = "test/racf-user-types.json";
		JSONObject user = JSONUtil.getJSONFromFile(path);
		JSONArray userTypes = user.getJSONArray("racf-user");
		System.out.printf("Found %d user types\n", userTypes.length());
	}
}
