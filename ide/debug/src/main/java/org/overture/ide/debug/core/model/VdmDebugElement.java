package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.overture.ide.debug.core.IDebugConstants;

public class VdmDebugElement extends PlatformObject implements IDebugElement
{

	protected VdmDebugTarget fTarget;
	protected ILaunch fLaunch;

	public VdmDebugElement(VdmDebugTarget target)
	{
		fTarget = target;
	}

	// IDebugElement
	public String getModelIdentifier()
	{
		return IDebugConstants.ID_VDM_DEBUG_MODEL;
	}

	public IDebugTarget getDebugTarget()
	{
		return fTarget;
	}

	public ILaunch getLaunch()
	{
		if (fLaunch == null && !getDebugTarget().equals(this))//no recursion
		{
			return getDebugTarget().getLaunch();
		}
		return fLaunch;
	}

	// END IDebugElement

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IDebugElement.class)
		{
			return this;
		}
		// This is the fix for the Perspective change on breakpoint hit... refer to (PerspectiveManager and
		// LauchSuspendTrigger)
		if (adapter == ILaunch.class)
		{
			return getLaunch();
		}
		return super.getAdapter(adapter);
	}

	protected void abort(String message, Throwable e) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, message, e));
	}

	/**
	 * Fires a debug event
	 * 
	 * @param event
	 *            the event to be fired
	 */
	protected void fireEvent(DebugEvent event)
	{
		if (DebugPlugin.getDefault() != null)
		{
			DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
		}
	}

	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * Fires a <code>RESUME</code> event for this element with the given detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	public void fireResumeEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a <code>SUSPEND</code> event for this element with the given detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	public void fireSuspendEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}

	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	public void fireChangeEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}
	
	public void fireChangeEvent(int details)
	{
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE,details));
	}

	public void fireExtendedEvent(Object eventSource, int details)
	{
		fireEvent(new DebugEvent(eventSource, DebugEvent.MODEL_SPECIFIC, details));

	}
}
