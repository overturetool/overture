/**
 * 
 */
package org.overturetool.vdmtools.dbgp;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.interpreter.Interpreter.DebugState;

final class StepIntoCommand extends CommandResponse {

	private VDMToolsProject project;
	
	StepIntoCommand() {
		project = VDMToolsProject.getInstance();
	}


	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		DebugState debugState = DebugState.ERROR;
		try {
			debugState = project.GetInterpreter().DebugStepIn();
		} catch (Exception e) {
			throw new DBGPException(DBGPErrorCode.INVALID_STACK_DEPTH, e.getMessage() );
		}
		return StackManager.getInstance().getResponse(debugState, command.getOption(DBGPOptionType.TRANSACTION_ID).value);
	}
}