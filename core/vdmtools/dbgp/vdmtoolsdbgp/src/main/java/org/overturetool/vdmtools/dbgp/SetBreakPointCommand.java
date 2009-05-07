package org.overturetool.vdmtools.dbgp;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


public class SetBreakPointCommand extends CommandResponse {

	
	/**
	 * @param debugger
	 */
	SetBreakPointCommand() {
	}
	
	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		// type
		String type = command.getOption(DBGPOptionType.T).value; //	line,  call, return, exception, conditional or watch
		DBGPBreakpointType bpType = DBGPBreakpointType.lookup(type);
		if (bpType == DBGPBreakpointType.LINE){
			// get options
			String lineNumber = command.getOption(DBGPOptionType.N).value;
			String fileuri = command.getOption(DBGPOptionType.F).value; // when
			String state = command.getOption(DBGPOptionType.S).value;
			

			// expression
			String expression = ""; 
			if (command.getOption(DBGPOptionType.DATA) != null)
			{
				expression = command.getOption(DBGPOptionType.DATA).value;
				expression = Base64Helper.decodeString(expression);
				if (expression != null)
					expression = expression.trim();
			}
			// hitvalue
			int hitValue = 0;
			if (command.getOption(DBGPOptionType.H) != null){
				String hitValueString = command.getOption(DBGPOptionType.H).value;
				if (hitValueString != null) {
					hitValue = Integer.parseInt(hitValueString);
				}
			}
			String hitCondition = "";
			if (command.getOption(DBGPOptionType.O) != null){
				hitCondition = command.getOption(DBGPOptionType.O).value;
			}

			// isTemporary
			boolean isTemp = false;
			if (command.getOption(DBGPOptionType.R) != null)
			{
				String tm = command.getOption(DBGPOptionType.R).value;
				if (tm != null) {
					isTemp = tm.equals("1");
				}
			}
			
			String filename;
			try {
				filename = new File(new URI(fileuri)).getAbsolutePath();
			} catch (URISyntaxException e1) {
				throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT, fileuri + ":" + lineNumber);
			}
			boolean boolState = true; //defaults true
			if (state.equals("disabled")){
				boolState = false;
			}
			
			BreakpointManager bpm = BreakpointManager.getInstance();
			try {
				int id = bpm.addBreakpoint(filename, Integer.parseInt(lineNumber), boolState, bpType,hitCondition,expression,hitValue, isTemp);
				
				return "<response " +
				"command=\"breakpoint_set\"\r\n"  + 
				" transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " + 
				" id=\"" + id + "\" state=\"enabled\" > " + "</response>\r\n" + "";
			} catch (DBGPException e) {
				throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT, fileuri + ":" + lineNumber);
				
			}
		}
		return "";
	}

}
