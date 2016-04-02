package org.gkh.racf.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gkh.racf.Configuration;
import org.gkh.racf.JSONUtil;
import org.gkh.racf.RACFParser;
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

	/**
	 * Make sure the configuration object loads the offsets look-up table.
	 */
	@Test
	public void testConfiguraton() {
		String groupKey = "group-basic-data-record";
		String userKey = "user-basic-data-record";
		JSONArray offsetArray;
		JSONObject recordType, recordOffsets;

		try {
			JSONObject offsets = Configuration.getRecordOffsetsAsJSON();
			assertNotNull(offsets);
			assertTrue(offsets.has(groupKey));
			assertTrue(offsets.has(userKey));

			recordType = offsets.getJSONObject(groupKey);
			assertTrue(recordType.has("record-type"));
			assertTrue(recordType.has("offsets"));
			assertThat(recordType.getString("record-type"), isIn(new String[] {
					"0100", "0200" }));

			offsetArray = recordType.getJSONArray("offsets");
			for (int i = 0; i < offsetArray.length(); i++) {
				recordOffsets = offsetArray.getJSONObject(i);
				assertTrue(recordOffsets.has("field-name"));
				assertTrue(recordOffsets.has("type"));
				assertTrue(recordOffsets.has("start"));
				assertTrue(recordOffsets.has("end"));
			}			
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParseDataRecord() {
		RACFParser parser = new RACFParser();
		JSONObject jsonRecord;
		try {
			jsonRecord = parser.parseDataRecord(
					"0100 GRP2     SYS0     2016-04-02 GPID1    NONE     NO",
					RACFParser.GROUP_RECORD_TYPE);
			System.out.println(jsonRecord.toString(2));
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseRecordWithOffset() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testRACFGroupTypes() {
		String path = "/racf-group-types.json";
		String recordTypeKey = "record-type";
		String recordNameKey = "record-name";
		
		String[] groupRecordTypes = { "0100", "0101", "0102", "0103", "0110",
				"0120", "0130", "0140", "0141", "0150", "0151" };
		JSONObject json;
		
		try {
			JSONObject group = JSONUtil.getJSONAsResource(path);
			JSONArray groupTypes = group.getJSONArray("racf-group-types");
			assertEquals(11, groupTypes.length());

			// TODO: I had figured out a way to use a JSONArray like a map so I
			// could access keys regardless of index; that would come in handy
			// here.
			for (int i = 0; i < groupTypes.length(); i++) {
				json = groupTypes.getJSONObject(i);
				System.out.println(json.toString(2));
				assertTrue(json.has(recordTypeKey));
				assertTrue(json.has(recordNameKey));
				assertThat(json.getString(recordTypeKey),
						isIn(groupRecordTypes));
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
