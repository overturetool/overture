package org.overturetool.vdmtools.dbgp;
/**
 * 
 */

final class FeatureGetCommand extends CommandResponse {

	@Override
	protected String parseAndExecute(DBGPCommand command) {
		System.out.println("com-get: " + command);
		return "<response command=\"feature_get\"\r\n"
		+ "          feature_name=\""+ command.getOption(DBGPOptionType.N).value +"\"\r\n"
		+ "          supported=\"1\"\r\n"
		+ "          transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\">"
		+ "</response>\r\n" + "";
	}
}