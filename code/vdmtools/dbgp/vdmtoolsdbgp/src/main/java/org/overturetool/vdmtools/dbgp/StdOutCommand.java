package org.overturetool.vdmtools.dbgp;

/**
 * 
 */
final class StdOutCommand extends CommandResponse {

	protected String parseAndExecute(DBGPCommand command) {
		return "<response command=\"stdout\"\r\n"
				+ "          success=\"1\"\r\n"
				+ "          transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value
				+ "\">\r\n" + "</response>\r\n" + "";
	}
}