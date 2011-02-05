package org.overture.ide.debug.internal.ui.viewers.update;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler;
import org.eclipse.debug.internal.ui.viewers.update.DebugTargetEventHandler;
import org.eclipse.debug.internal.ui.viewers.update.DebugTargetProxy;
import org.eclipse.debug.internal.ui.viewers.update.StackFrameEventHandler;
import org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler;

@SuppressWarnings("restriction")
public class VdmDebugTargetProxy extends DebugTargetProxy
{

	public VdmDebugTargetProxy(IDebugTarget target)
	{
		super(target);
	}
	
	@Override
	protected DebugEventHandler[] createEventHandlers()
	{
		 ThreadEventHandler threadEventHandler = new VdmThreadEventHandler(this);
			return new DebugEventHandler[] { new DebugTargetEventHandler(this), threadEventHandler,
					new StackFrameEventHandler(this, threadEventHandler) };
	}

}
