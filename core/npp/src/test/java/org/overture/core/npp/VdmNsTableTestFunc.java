package org.overture.core.npp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * General functionality tests for {@link VdmNsTable}
 * 
 * @author ldc
 * 
 */
public class VdmNsTableTestFunc {

	VdmNsTable table;

	@Before
	public void setup() {
		table = VdmNsTable.getInstance();
		table.reset();
	}
	
	@Test
	public void testVdmNsTable_EntryCount() throws IOException {
		// silly test to ensure no attribute gets
		// added without a test
		int expected = IOUtils.readLines(
				new FileReader(VdmNsTableTestContent.ATTRIB_FILE)).size();
		int actual = table.getAttribCount();

		assertEquals(expected, actual);
	}


	@Test
	public void testVdmNsTable_Constructor_NoParams() {
		table = new VdmNsTable();

		// just make sure it works. should not be called
		assertNotNull(table);
	}

	@Test
	public void testGetInstance_SingleCall() {
		assertNotNull(table);
	}

	@Test
	public void testGetInstance_2Calls() {
		VdmNsTable table2 = VdmNsTable.getInstance();
		assertSame(table, table2);
	}

	@Test
	public void testGetAttribute_ValidInput() {
		String actual = table.getAttribute("TAIL");
		String expected = "tl";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetAttribute_InvalidInput() {
		String actual = table.getAttribute("missing");
		String expected = "NOATTRIB";

		assertEquals(expected, actual);
	}

	@Test
	public void testInsertAttribute_New() {
		String key = "testkey";
		String attrib = "testattrib";
		table.insertAttribute(key, attrib);

		String actual = table.getAttribute(key);
		String expected = attrib;

		assertEquals(expected, actual);
	}

	@Test
	public void testInsertAttribute_Update() {
		// check if attribute exits
		String key = "TAIL";
		String actual = table.getAttribute(key);
		String expected = "tl";
		assertEquals(expected, actual);

		// now update and test
		String attrib = "testtl";
		table.insertAttribute(key, attrib);
		actual = table.getAttribute(key);
		expected = attrib;
		assertEquals(expected, actual);
	}


	
}
