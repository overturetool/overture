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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VdmSymbolTableTest
{

	VdmSymbolTable table;

	@Before
	public void setup()
	{
		table = VdmSymbolTable.getInstance();
	}

	@Test
	public void testVdmNsTable_Constructor_NoParams()
	{
		table = new VdmSymbolTable();

		// just make sure it works. should not be called
		assertNotNull(table);
	}

	@Test
	public void testGetInstance_SingleCall()
	{
		assertNotNull(table);
	}

	@Test
	public void testGetInstance_2Calls()
	{
		VdmSymbolTable table2 = VdmSymbolTable.getInstance();
		assertSame(table, table2);
	}

	@Test
	public void testGetAND()
	{
		String actual = table.getAND();
		String expected = "and";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetOR()
	{
		String actual = table.getOR();
		String expected = "or";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPLUS()
	{
		String actual = table.getPLUS();
		String expected = "+";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetMINUS()
	{
		String actual = table.getMINUS();
		String expected = "-";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDIVIDE()
	{
		String actual = table.getDIVIDE();
		String expected = "/";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetTAIL()
	{
		String actual = table.getTAIL();
		String expected = "tl";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetTIMES()
	{
		String actual = table.getTIMES();
		String expected = "*";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetLT()
	{
		String actual = table.getLT();
		String expected = "<";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetLE()
	{
		String actual = table.getLE();
		String expected = "<=";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetGT()
	{
		String actual = table.getGT();
		String expected = ">";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetGE()
	{
		String actual = table.getGE();
		String expected = ">=";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetNE()
	{
		String actual = table.getNE();
		String expected = "<>";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetEQUALS()
	{
		String actual = table.getEQUALS();
		String expected = "=";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetEQUIV()
	{
		String actual = table.getEQUIV();
		String expected = "<=>";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetIMPLIES()
	{
		String actual = table.getIMPLIES();
		String expected = "=>";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSETDIFF()
	{
		String actual = table.getSETDIFF();
		String expected = "\\";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPLUSPLUS()
	{
		String actual = table.getPLUSPLUS();
		String expected = "++";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSTARSTAR()
	{
		String actual = table.getSTARSTAR();
		String expected = "**";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetCONCATENATE()
	{
		String actual = table.getCONCATENATE();
		String expected = "^";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetMAPLET()
	{
		String actual = table.getMAPLET();
		String expected = "|->";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetRANGE()
	{
		String actual = table.getRANGE();
		String expected = "...";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDOMRESTO()
	{
		String actual = table.getDOMRESTO();
		String expected = "<:";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDOMRESBY()
	{
		String actual = table.getDOMRESBY();
		String expected = "<-:";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetRANGERESTO()
	{
		String actual = table.getRANGERESTO();
		String expected = ":>";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetRANGERESBY()
	{
		String actual = table.getRANGERESBY();
		String expected = ":->";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetLAMBDA()
	{
		String actual = table.getLAMBDA();
		String expected = "lambda";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetIOTA()
	{
		String actual = table.getIOTA();
		String expected = "iota";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetEXISTS1()
	{
		String actual = table.getEXISTS1();
		String expected = "exists1";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetEXISTS()
	{
		String actual = table.getEXISTS();
		String expected = "exists";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPOINT()
	{
		String actual = table.getPOINT();
		String expected = ".";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetHEAD()
	{
		String actual = table.getHEAD();
		String expected = "hd";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetFORALL()
	{
		String actual = table.getFORALL();
		String expected = "forall";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetCOMPOSITION()
	{
		String actual = table.getCOMPOSITION();
		String expected = "comp";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetINDS()
	{
		String actual = table.getINDS();
		String expected = "inds";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDISTCONC()
	{
		String actual = table.getDISTCONC();
		String expected = "conc";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDUNION()
	{
		String actual = table.getDUNION();
		String expected = "dunion";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetFLOOR()
	{
		String actual = table.getFLOOR();
		String expected = "floor";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetMERGE()
	{
		String actual = table.getMERGE();
		String expected = "merge";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDINTER()
	{
		String actual = table.getDINTER();
		String expected = "dinter";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetABSOLUTE()
	{
		String actual = table.getABSOLUTE();
		String expected = "abs";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetELEMS()
	{
		String actual = table.getELEMS();
		String expected = "elems";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetRNG()
	{
		String actual = table.getRNG();
		String expected = "rng";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPOWER()
	{
		String actual = table.getPOWER();
		String expected = "power";
		// POWA!
		assertEquals(expected, actual);
	}

	@Test
	public void testGetLEN()
	{
		String actual = table.getLEN();
		String expected = "len";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDOM()
	{
		String actual = table.getDOM();
		String expected = "dom";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetCARD()
	{
		String actual = table.getCARD();
		String expected = "card";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetINVERSE()
	{
		String actual = table.getINVERSE();
		String expected = "inverse";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetINTER()
	{
		String actual = table.getINTER();
		String expected = "inter";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetUNION()
	{
		String actual = table.getUNION();
		String expected = "union";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetMUNION()
	{
		String actual = table.getMUNION();
		String expected = "munion";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetREM()
	{
		String actual = table.getREM();
		String expected = "rem";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetMOD()
	{
		String actual = table.getMOD();
		String expected = "mod";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDIV()
	{
		String actual = table.getDIV();
		String expected = "div";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSUBSET()
	{
		String actual = table.getSUBSET();
		String expected = "subset";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPSUBSET()
	{
		String actual = table.getPSUBSET();
		String expected = "psubset";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetINSET()
	{
		String actual = table.getINSET();
		String expected = "in set";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetNOTINSET()
	{
		String actual = table.getNOTINSET();
		String expected = "not in set";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetPRED()
	{
		String actual = table.getPRED();
		String expected = "&";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSEP()
	{
		String actual = table.getSEP();
		String expected = ";";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetDEF()
	{
		String actual = table.getDEF();
		String expected = "==";

		assertEquals(expected, actual);}

	@Test
	public void testGetOPENQUOTE()
	{
		String actual = table.getOPENQUOTE();
		String expected = "<";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetCLOSEQUOTE()
	{
		String actual = table.getCLOSEQUOTE();
		String expected = ">";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetCHARDELIM()
	{
		String actual = table.getCHARDELIM();
		String expected = "'";

		assertEquals(expected, actual);
	}

	@Test
	public void testGetSTRINGDELIM()
	{
		String actual = table.getSTRINGDELIM();
		String expected = "\"";

		assertEquals(expected, actual);
	}

}
