package org.overturetool.vdmtools.dbgp;

final class UpdateBreakPointCommand extends CommandResponse {

	BreakpointManager bpManager;
	UpdateBreakPointCommand() {
		bpManager = BreakpointManager.getInstance();
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String idStr = command.getOption(DBGPOptionType.D).value;
		int id = Integer.parseInt(idStr);
		String newState = command.getOption(DBGPOptionType.S).value;
		String newLineStr = command.getOption(DBGPOptionType.N).value;
		int newLine = Integer.parseInt(newLineStr);
		String hitValueStr = command.getOption(DBGPOptionType.H).value;
		int hitValue = Integer.parseInt(hitValueStr);
		String hitCondition = command.getOption(DBGPOptionType.O).value;
		String condEString = command.data;

		if (condEString != null) {
			condEString = Base64Helper.decodeString(condEString);
		}

		try {
			bpManager.updateBreakpoint(id, newState, newLine, hitValue, hitCondition, condEString);
			return 
			  "<response " +
				"command=\"breakpoint_update\" " + 
				"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " + 
				"id=\"" + id + "\" " +
				"state=\"" + bpManager.getBreakpoint(id).getState() + "\" >"
			+ "</response>";
		} catch (Exception e) {
			throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT, e.getMessage() + " command: " + command.toString());
		}
		
	}
}