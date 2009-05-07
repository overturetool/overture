/**
 * 
 */
package org.overturetool.vdmtools.dbgp;


final class StackDepthCommand extends CommandResponse {
	StackDepthCommand() {
	}


	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		return 
			"<response " +
				"command=\"stack_depth\" " + 
				"depth=\"0\" " + 
				"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\">" + 
			"</response>\r\n" + "";
	}
}