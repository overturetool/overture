package org.overturetool.vdmtools.dbgp;

import java.io.File;

public class GetBreakPointCommand extends CommandResponse {


	public GetBreakPointCommand() {
	}



	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String stringID = command.getOption(DBGPOptionType.D).value;
		Breakpoint breakpoint; 
		int id = -1;
		try {
			id = Integer.parseInt(stringID);
			breakpoint = BreakpointManager.getInstance().getBreakpoint(id);
		} catch (Exception e) {
			throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT,"Could not set breakpoint");
		}

		return "<response " +
					"command=\"breakpoint_get\"\r\n" + 
					" transaction_id=\"" +command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\"" +
				">\r\n" +
				// breakpoint:
				"<breakpoint " + 
					"id=\"" + id + "\" " + 
					"type=\"" + breakpoint.getType() + "\" " + 
					"state=\"" + breakpoint.getState() + "\" " + 
					"filename=\"" + new File(breakpoint.getFilename()).toURI().toASCIIString() + "\" " + 
//					"filename=\"file:/" + breakpoint.getFilename() + "\" " +
					"lineno=\"" + breakpoint.getLineNumber() + "\" " + 
					"exception=\"" + "\" " + 
					"hit_value=\""  + breakpoint.getHitValue() + "\" " + 
					"hit_condition=\"" + breakpoint.getHitCondition() + "\" " + 
					"hit_count=\"" + breakpoint.getCurrentHitCount() + "\"" + 
				">" + 
					"<expression>"+
						Base64Helper.encodeString(breakpoint.getExpression())+ 
					"</expression>"
				+ "</breakpoint>"
				+ "</response>\r\n" + "";
	}

}
