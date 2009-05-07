/**
 * 
 */
package org.overturetool.vdmtools.dbgp;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.interpreter.Interpreter.DebugState;

final class StepOverCommand extends CommandResponse {

	private VDMToolsProject vdmProject;
	private StackManager stackManager;
	
	
	StepOverCommand() {
		vdmProject = VDMToolsProject.getInstance();
		this.stackManager = StackManager.getInstance();
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		DebugState debugState = DebugState.ERROR;
		try {
			debugState = vdmProject.GetInterpreter().DebugStep();
		} catch (Exception e) {
			System.out.println("step exception: " + e.getMessage());
			e.printStackTrace();
		}
		return stackManager.getResponse(debugState, command.getOption(DBGPOptionType.TRANSACTION_ID).value);
	}
}