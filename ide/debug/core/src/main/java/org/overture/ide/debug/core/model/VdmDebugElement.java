package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

public class VdmDebugElement extends PlatformObject implements IDebugElement {

	
	protected VdmDebugTarget fTarget;
	
	public VdmDebugElement(VdmDebugTarget target) {
		fTarget = target;
	}
	
	public IDebugTarget getDebugTarget() {		
		return fTarget;
	}

	public ILaunch getLaunch() {		
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

}
