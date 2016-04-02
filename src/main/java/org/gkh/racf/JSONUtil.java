/*******************************************************************************
 * Copyright (c) 2015 Unpublished Work of NetIQ, Inc. All Rights Reserved.
 * 
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *  
 * ========================================================================
 ******************************************************************************/
package org.gkh.racf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	public static JSONObject getJSONAsResource(String path)
			throws JSONException {
		InputStream in = JSONParserTemplateBuilder.class
				.getResourceAsStream(path);

		Scanner scanner = new Scanner(in, "UTF-8");
		String json = scanner.useDelimiter("\\A").next();
		scanner.close();

		if (isValidJSON(json)) {
			return new JSONObject(json);
		}

		return null;
	}
	
	public static JSONObject getJSONFromFile(String path) throws IOException,
			JSONException {
		// (1) Read JSON file into a string and attempt to parse it.
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		String json = new String(encoded, "UTF-8");

		if (isValidJSON(json)) {
			return new JSONObject(json);
		}
		
		return null;
	}
	
	/**
	 * Note: Uses Jackson.
	 * 
	 * @see <a herf=
	 *      "http://stackoverflow.com/questions/15791878/reliable-json-string-validator-in-java"
	 *      >Reliable JSON String Validator</a>
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isValidJSONUsingJackson(String text) {
		try {
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createJsonParser(text);
			JsonParser jp = new ObjectMapper().getJsonFactory().createJsonParser(text);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Using Jettison...
	 * 
	 * @see <a
	 *      href="http://stackoverflow.com/questions/10174898/how-to-check-whether-a-given-string-is-valid-json-in-java">Check
	 *      That Given String is Valid JSON</a>
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isValidJSON(String text) {
		try {
			new JSONObject(text);
		} catch (JSONException e) {
			// in case JSONArray is valid as well...
			try {
				new JSONArray(text);
			} catch (JSONException e1) {
				return false;
			}
		}
		return true;
	}
}
