/*
 * #%~
 * New Pretty Printer
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
