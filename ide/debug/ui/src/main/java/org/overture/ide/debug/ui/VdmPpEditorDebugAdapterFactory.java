package org.overture.ide.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.dltk.debug.ui.breakpoints.IScriptBreakpointLineValidator;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptBreakpointLineValidatorFactory;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter;
import org.eclipse.dltk.internal.debug.ui.ScriptRunToLineAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.overture.ide.vdmpp.debug.VDMPPDebugConstants;

public class VdmPpEditorDebugAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IRunToLineTarget.class){
			return new ScriptRunToLineAdapter();
		}
		else if (adapterType == IToggleBreakpointsTarget.class) {
			// specific breakpoint adapter for the VDM language
			return new ScriptToggleBreakpointAdapter() {
				
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
					return VDMPPDebugConstants.VDMPP_DEBUG_MODEL;
				}
			};
		}	
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IRunToLineTarget.class, IToggleBreakpointsTarget.class};
	}

}
