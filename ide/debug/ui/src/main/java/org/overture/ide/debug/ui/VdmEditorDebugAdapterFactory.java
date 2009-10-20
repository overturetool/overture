package org.overture.ide.debug.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.dltk.internal.debug.ui.ScriptRunToLineAdapter;

/***
 * 
 * @author Christian Thillemann
 * 
 * extension point: org.eclipse.core.runtime.adapters
 *
 */
public abstract class VdmEditorDebugAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IRunToLineTarget.class){
			return new ScriptRunToLineAdapter();
		}
		else if (adapterType == IToggleBreakpointsTarget.class) {
			// specific breakpoint adapter for the VDM language
			return new VdmScriptToggleBreakpointAdapter(getDebugModelId()) ;
		}	
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[]{IRunToLineTarget.class, IToggleBreakpointsTarget.class};
	}
	
	
	
	
	protected abstract String getDebugModelId();

}
