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

	String getCOMMA();
	
	String getCASES();
	
	String getCOLON();
	
	String getEND();
	
	String getOTHERS();
	
	String getIF();
	
	String getTHEN();
	
	String getELSEIF();
	
	String getELSE();
	
	String getIN();
	
	String getDEFINE();
	
	String getLET();
	
	String getBESUCH();
	
	String getARROW();
	
	String getISTYPE();
	
	String getISBASECLASS();
	
	String getISCLASS();
	
	String getSAMECLASS();
	
	String getSAMEBASECLASS();
	
	String getPRE();
	
	String getTUPLE();
	

}
