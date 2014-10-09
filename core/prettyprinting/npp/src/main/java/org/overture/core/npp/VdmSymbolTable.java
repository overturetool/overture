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

/**
 * The VdmNsTable Class provides a default VDM-based implementation of {@link ISymbolTable}. It provides attributes for all
 * VDM language constructs needed to print out the Overture AST. <br>
 * Pretty Printers for language extensions may subclass VdmNsTable and override methods as necessary
 * 
 * @author ldc
 */

public class VdmSymbolTable implements ISymbolTable
{

	// singleton instance
	private static VdmSymbolTable instance = null;

	protected VdmSymbolTable()
	{

	}

	// create initial mapping of vdm elements and attributes
	public static VdmSymbolTable getInstance()
	{
		if (instance == null)
		{
			instance = new VdmSymbolTable();
		}
		return instance;
	}

	@Override
	public String getTAIL()
	{
		return "tl";
	}

	@Override
	public String getAND()
	{
		return "and";
	}

	@Override
	public String getOR()
	{
		return "or";
	}

	@Override
	public String getPLUS()
	{
		return "+";
	}

	@Override
	public String getMINUS()
	{
		return "-";
	}

	@Override
	public String getDIVIDE()
	{
		return "/";
	}

	@Override
	public String getTIMES()
	{
		return "*";
	}

	@Override
	public String getLT()
	{
		return "<";
	}

	@Override
	public String getLE()
	{
		return "<=";
	}

	@Override
	public String getGT()
	{
		return ">";
	}

	@Override
	public String getGE()
	{
		return ">=";
	}

	@Override
	public String getNE()
	{
		return "<>";
	}

	@Override
	public String getEQUALS()
	{
		return "=";
	}

	@Override
	public String getEQUIV()
	{
		return "<=>";
	}

	@Override
	public String getIMPLIES()
	{
		return "=>";
	}

	@Override
	public String getSETDIFF()
	{
		return "\\";
	}

	@Override
	public String getPLUSPLUS()
	{
		return "++";
	}

	@Override
	public String getSTARSTAR()
	{
		return "**";
	}

	@Override
	public String getCONCATENATE()
	{
		return "^";
	}

	@Override
	public String getMAPLET()
	{
		return "|->";
	}

	@Override
	public String getRANGE()
	{
		return "...";
	}

	@Override
	public String getDOMRESTO()
	{
		return "<:";
	}

	@Override
	public String getDOMRESBY()
	{
		return "<-:";
	}

	@Override
	public String getRANGERESTO()
	{
		return ":>";
	}

	@Override
	public String getRANGERESBY()
	{
		return ":->";
	}

	@Override
	public String getLAMBDA()
	{
		return "lambda";
	}

	@Override
	public String getIOTA()
	{
		return "iota";
	}

	@Override
	public String getEXISTS1()
	{
		return "exists1";
	}

	@Override
	public String getEXISTS()
	{
		return "exists";
	}

	@Override
	public String getPOINT()
	{
		return ".";
	}

	@Override
	public String getHEAD()
	{
		return "hd";
	}

	@Override
	public String getFORALL()
	{
		return "forall";
	}

	@Override
	public String getCOMPOSITION()
	{
		return "comp";
	}

	@Override
	public String getINDS()
	{
		return "inds";
	}

	@Override
	public String getDISTCONC()
	{
		return "conc";
	}

	@Override
	public String getDUNION()
	{
		return "dunion";
	}

	@Override
	public String getFLOOR()
	{
		return "floor";
	}

	@Override
	public String getMERGE()
	{
		return "merge";
	}

	@Override
	public String getDINTER()
	{
		return "dinter";
	}

	@Override
	public String getABSOLUTE()
	{
		return "abs";
	}

	@Override
	public String getELEMS()
	{
		return "elems";
	}

	@Override
	public String getRNG()
	{
		return "rng";
	}

	@Override
	public String getPOWER()
	{
		return "power";
	}

	@Override
	public String getLEN()
	{
		return "len";
	}

	@Override
	public String getDOM()
	{
		return "dom";
	}

	@Override
	public String getCARD()
	{
		return "card";
	}

	@Override
	public String getINVERSE()
	{
		return "inverse";
	}

	@Override
	public String getINTER()
	{
		return "inter";
	}

	@Override
	public String getUNION()
	{
		return "union";
	}

	@Override
	public String getMUNION()
	{
		return "munion";
	}

	@Override
	public String getREM()
	{
		return "rem";
	}

	@Override
	public String getMOD()
	{
		return "mod";
	}

	@Override
	public String getDIV()
	{
		return "div";
	}

	@Override
	public String getSUBSET()
	{
		return "subset";
	}

	@Override
	public String getPSUBSET()
	{
		return "psubset";
	}

	@Override
	public String getINSET()
	{
		return "in set";
	}

	@Override
	public String getNOTINSET()
	{
		return "not in set";
	}

	@Override
	public String getPRED()
	{
		return "&";
	}

	@Override
	public String getSEP()
	{
		return ";";
	}

	@Override
	public String getDEF()
	{
		return "==";
	}

	@Override
	public String getOPENQUOTE()
	{
		return "<";
	}

	@Override
	public String getCLOSEQUOTE()
	{
		return ">";
	}

	@Override
	public String getCHARDELIM()
	{
		return "'";
	}

	@Override
	public String getSTRINGDELIM()
	{
		return "\"";
	}

}
