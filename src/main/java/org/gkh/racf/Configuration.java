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
	 * Reads a file on disk into a single {@link String}.
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
