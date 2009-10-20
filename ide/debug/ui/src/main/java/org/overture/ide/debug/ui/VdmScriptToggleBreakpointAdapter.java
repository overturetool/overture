package org.overture.ide.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.debug.ui.breakpoints.IScriptBreakpointLineValidator;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptBreakpointLineValidatorFactory;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

public class VdmScriptToggleBreakpointAdapter extends ScriptToggleBreakpointAdapter{

	private String debugModel;
	
	public VdmScriptToggleBreakpointAdapter(String debugModel) {
	this.debugModel= debugModel;
	}
	
	private final IScriptBreakpointLineValidator validator = ScriptBreakpointLineValidatorFactory.createNonEmptyNoCommentValidator("--");
	
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}
	
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection);
	}
	
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}
	
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return canToggleBreakpoints(part, selection);
	}
	
	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection);
	}
	
	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		return canToggleLineBreakpoints(part, selection);
	}
	
	@Override
	protected IScriptBreakpointLineValidator getValidator() {
		return validator;
	}
	
	@Override
	protected String getDebugModelId() {
		return debugModel;
	}
}
