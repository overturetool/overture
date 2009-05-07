package org.overturetool.vdmtools.dbgp;
///**
// * 
// */



final class StdErrCommand extends CommandResponse {

	@Override
	protected String parseAndExecute(DBGPCommand command) {
		return "<response command=\"stderr\"\r\n"
		+ "          success=\"1\"\r\n"
		+ "          transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value
		+ "\">\r\n" + "</response>\r\n" + "";
	}
}