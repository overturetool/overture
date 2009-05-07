/**
 * 
 */
package org.overturetool.vdmtools.dbgp;


final class ContextNamesCommand extends CommandResponse {

	/**
	 * 
	 */
	public ContextNamesCommand() {
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		return 
			"<response " +
				"command=\"context_names\"\r\n" + 
				"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + 
			"\">" + 
					"<context name=\"Local\" id=\"0\"/> " + 
					"<context name=\"Global\" id=\"1\"/> " + 
					"<context name=\"Class\" id=\"2\"/> " + 
			"</response> ";
	}
}