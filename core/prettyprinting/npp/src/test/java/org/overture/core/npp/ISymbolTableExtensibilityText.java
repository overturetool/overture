package org.overture.core.npp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the extensibility of InsTable and VdmNsTable
 * @author ldc
 *
 */
public class ISymbolTableExtensibilityText {

	class ExtTable extends VdmSymbolTable implements ISymbolTable {

		public ExtTable() {
			super();
		}
		
		@Override
		public String getHEAD()
		{
			return "exthd";

		}
		
		public String getEXTKEY(){
			return "extkey";
		}
	}

	ExtTable eTable;

	@Before
	public void setup() {
		eTable = new ExtTable();
	}

	@Test
	public void testExtTable_Constructor_NoParams() {
		assertNotNull(eTable);
	}

	@Test
	public void testGetAttribute_Inherit() {
		String actual = eTable.getTAIL();
		String expected = "tl";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetAttribute_Override() {
		String actual = eTable.getHEAD();
		String expected = "exthd";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetAttribute_NewAttrib() {
		String actual = eTable.getEXTKEY();
		String expected = "extattrib";

		assertEquals(expected, actual);
	}

}
