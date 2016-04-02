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

	static final String GROUP_TEST_RECORD = "0100 GRP2     SYS0     2016-04-02 GPID1    NONE     NO";

	private String expectedRecordType = "0100";
	private String expectedGroupName = "GRP2";
	private String expectedParentGroup = "SYS0";
	private String expectedDate = "2016-04-02";
	
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
	
	/**
	 * A group data record should parse as follows.
	 * 
	 * <pre>
	 * {
	 *   "GPBD_RECORD_TYPE": "0100",
	 *   "GPBD_NAME": "GRP2",
	 *   "GPBD_SUPGRP_ID": "SYS0",
	 *   "GPBD_CREATE_DATE": "2016-04-02",
	 *   "GPBD_OWNER_ID": "GPID1",
	 *   "GPBD_UACC": "NONE",
	 *   "GPBD_NOTERMUACC": "",
	 *   "GPBD_UNIVERSAL": ""
	 * }
	 * </pre>
	 */
	@Test
	public void testParseDataRecord() {
		RACFParser parser = new RACFParser();
		JSONObject jsonRecord;
		try {
			jsonRecord = parser.parseDataRecord(
					GROUP_TEST_RECORD,
					RACFParser.GROUP_RECORD_TYPE);
			assertNotNull(jsonRecord);
			assertTrue(jsonRecord.has("GPBD_RECORD_TYPE"));
			assertTrue(jsonRecord.has("GPBD_NAME"));
			assertTrue(jsonRecord.has("GPBD_SUPGRP_ID"));
			assertTrue(jsonRecord.has("GPBD_CREATE_DATE"));
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Use (index - 1) when passing in starting position.
	 * 
	 * <pre>
	 * Group basic data record (0100)
	 * Field Name			Start	End
	 * GPBD_RECORD_TYPE		1		4
	 * GPBD_NAME			6		13
	 * GPBD_SUPGRP_ID		15		22
	 * GPBD_CREATE_DATE		24		33
	 * </pre>
	 */
	@Test
	public void testParseRecordWithOffset() {
		RACFParser parser = new RACFParser();

		// TODO: Should the caller really have to trim these?
		assertEquals(expectedRecordType,
				parser.parseRecordWithOffset(GROUP_TEST_RECORD, 1 - 1, 4)
						.trim());
		assertEquals(expectedGroupName,
				parser.parseRecordWithOffset(GROUP_TEST_RECORD, 6 - 1, 13)
						.trim());
		assertEquals(expectedParentGroup,
				parser.parseRecordWithOffset(GROUP_TEST_RECORD, 15 - 1, 22)
						.trim());
		assertEquals(expectedDate,
				parser.parseRecordWithOffset(GROUP_TEST_RECORD, 24 - 1, 33)
						.trim());
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
