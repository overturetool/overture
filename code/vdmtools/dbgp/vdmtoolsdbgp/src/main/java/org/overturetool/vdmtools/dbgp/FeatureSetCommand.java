package org.overturetool.vdmtools.dbgp;
/**
 * 
 */
final class FeatureSetCommand extends CommandResponse {

	protected String parseAndExecute(DBGPCommand command) {
		
		return "<response command=\"feature_set\"\r\n"
				+ "          feature_name=\""+ command.getOption(DBGPOptionType.N).value +"\"\r\n"
				+ "          success=\"1\"\r\n"
				+ "          transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value
				+ "\">\r\n" + "</response>\r\n" + "";
	}
}