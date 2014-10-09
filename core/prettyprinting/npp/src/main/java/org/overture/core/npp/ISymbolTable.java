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
 * The Interface InsTable lists all available VDM symbols and is a contract for any symbol table to be used with the new
 * pretty printer.
 * 
 * @author ldc
 */
public interface ISymbolTable
{

	String getTAIL();

	String getAND();

	String getOR();

	String getPLUS();

	String getMINUS();

	String getDIVIDE();

	String getTIMES();

	String getLT();

	String getLE();

	String getGT();

	String getGE();

	String getNE();

	String getEQUALS();

	String getEQUIV();

	String getIMPLIES();

	String getSETDIFF();

	String getPLUSPLUS();

	String getSTARSTAR();

	String getCONCATENATE();

	String getMAPLET();

	String getRANGE();

	String getDOMRESTO();

	String getDOMRESBY();

	String getRANGERESTO();

	String getRANGERESBY();

	String getLAMBDA();

	String getIOTA();

	String getEXISTS1();

	String getEXISTS();

	String getPOINT();

	String getHEAD();

	String getFORALL();

	String getCOMPOSITION();

	String getINDS();

	String getDISTCONC();

	String getDUNION();

	String getFLOOR();

	String getMERGE();

	String getDINTER();

	String getABSOLUTE();

	String getELEMS();

	String getRNG();

	String getPOWER();

	String getLEN();

	String getDOM();

	String getCARD();

	String getINVERSE();

	String getINTER();

	String getUNION();

	String getMUNION();

	String getREM();

	String getMOD();

	String getDIV();

	String getSUBSET();

	String getPSUBSET();

	String getINSET();

	String getNOTINSET();

	String getPRED();

	String getSEP();

	String getDEF();

	String getOPENQUOTE();

	String getCLOSEQUOTE();

	String getCHARDELIM();

	String getSTRINGDELIM();

}
