package org.overturetool.vdmtools.dbgp;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.interpreter.Interpreter.DebugState;

final class RunCommand extends CommandResponse {
	/**
	 * 
	 */
	private VDMToolsProject vdmToolsProject;
	private String expression;
	private StackManager stackManager;
//	private DBGPDebugger debugger;


	RunCommand(String expression) {
		vdmToolsProject = VDMToolsProject.getInstance();
		this.expression = expression;
		this.stackManager = StackManager.getInstance();
//		this.debugger = debugger;
	}

	public String parseAndExecute(DBGPCommand command) throws DBGPException {
		DebugState debugState = DebugState.ERROR;
		try {
			if (stackManager.getDebugState() == DebugState.BREAKPOINT){
				debugState = vdmToolsProject.GetInterpreter().DebugContinue();
			}
			else
			{		
				debugState = vdmToolsProject.GetInterpreter().StartDebugging(expression);
			}
		} catch (Exception e) {
			throw new DBGPException(DBGPErrorCode.UNKNOWN_ERROR, e.getMessage());
		}
		return stackManager.getResponse(debugState, command.getOption(DBGPOptionType.TRANSACTION_ID).value);
	}
}