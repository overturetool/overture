package org.overturetool.vdmtools.dbgp;


final class RemoveBreakPointCommand extends CommandResponse {

	RemoveBreakPointCommand() {
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String breakpointIDStr = command.getOption(DBGPOptionType.D).value;
		int breakpointID = Integer.parseInt(breakpointIDStr);
		
		BreakpointManager.getInstance().removeBreakpooint(breakpointID);
		return "<response " +
					"command=\"breakpoint_remove\" " +
					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " +
				"/>";
	}
}