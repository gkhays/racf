package org.gkh.racf.test;
import static org.junit.Assert.*;

import java.io.IOException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gkh.racf.JSONUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestRACFParser {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseDataRecord() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseRecordWithOffset() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testRACFGroupTypes() {
		String path = "/racf-group-types.json";
		String[] testKeys = { "0100", "0101", "0102", "0102", "0110", "0150",
				"0151" };
		JSONObject json;
		try {
			JSONObject group = JSONUtil.getJSONAsResource(path);
			JSONArray groupTypes = group.getJSONArray("racf-group");
			assertEquals(11, groupTypes.length());

			// TODO: I had figured out a way to use a JSONArray like a map so I
			// could access keys regardless of index; that would come in handy
			// here.
			for (String key : testKeys) {
				for (int i = 0; i < groupTypes.length(); i++) {
					json = groupTypes.getJSONObject(i);
					System.out.println(json.toString(2));
					assertTrue(json.has(key));
				}
			}
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRacfRecordsToJSONArrayStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testRacfRecordsToJSONArrayStringArrayString() {
		fail("Not yet implemented");
	}

}
