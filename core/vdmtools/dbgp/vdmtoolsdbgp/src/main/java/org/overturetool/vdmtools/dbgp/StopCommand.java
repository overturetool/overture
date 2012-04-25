/**
 * 
 */
package org.overturetool.vdmtools.dbgp;

final class StopCommand extends CommandResponse {
	StopCommand() {
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		return "<response command=\"run\" " + 
					"status=\"stopped\" " + 
					"reason=\"ok\" " + 
					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\">"
				+ "</response>";
	}
}