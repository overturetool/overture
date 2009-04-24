package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.debug.ui.breakpoints.IScriptBreakpointLineValidator;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptBreakpointLineValidatorFactory;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;


public class OvertureToggleBreakpointAdapter extends ScriptToggleBreakpointAdapter {
	// Line breakpoints
	private static final IScriptBreakpointLineValidator validator = ScriptBreakpointLineValidatorFactory
		.createNonEmptyNoCommentValidator("--"); //$NON-NLS-1$
	
	protected String getDebugModelId() {
		return OvertureDebugConstants.DEBUG_MODEL_ID;
	}
	
	protected IScriptBreakpointLineValidator getValidator() {
		return validator;
	}

	// Method breakpoints
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) throws CoreException {

	}

	// Watchpoints
	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection) {		
		return false;
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {		
	}

	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		toggleLineBreakpoints(part, selection);
	}

	public boolean canToggleBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return canToggleLineBreakpoints(part, selection);
	}
}
