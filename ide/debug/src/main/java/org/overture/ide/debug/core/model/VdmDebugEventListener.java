package org.overture.ide.debug.core.model;

import java.util.Arrays;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.overture.ide.debug.core.Activator;

/**
 * Event listener for a VdmDebug target. Used to distribute and sync event among threads. All control events will be
 * picked up and send to the debug target for distribution to the approbate threads etc.
 * 
 * @author kela
 */
public class VdmDebugEventListener implements IDebugEventSetListener
{
	private VdmDebugTarget target = null;

	/**
	 * Constructor of the event listener. The debug target parsed is used to delegate updates
	 * 
	 * @param vdmDebugTarget
	 */
	public VdmDebugEventListener(VdmDebugTarget vdmDebugTarget)
	{
		this.target = vdmDebugTarget;
	}

	public void handleDebugEvents(DebugEvent[] events)
	{
		for (DebugEvent debugEvent : events)
		{

			if (hasSource(debugEvent.getSource()))
			{
				switch (debugEvent.getKind())
				{
					case DebugEvent.RESUME:
						handleResume(debugEvent);
						break;

					case DebugEvent.SUSPEND:
						handleSuspend(debugEvent);
						break;

					case DebugEvent.CREATE:
						handleCreate(debugEvent);
						break;

					case DebugEvent.TERMINATE:
						handleTerminate(debugEvent);
						break;

					case DebugEvent.CHANGE:
						handleChange(debugEvent);
						break;

					case DebugEvent.MODEL_SPECIFIC:
						handleModelSpecific(debugEvent);
						break;
					case DebugEvent.UNSPECIFIED:
						handleUnspecified(debugEvent);
						break;
				}
			}
		}
	}

	private void handleUnspecified(DebugEvent debugEvent)
	{

	}

	private void handleModelSpecific(DebugEvent debugEvent)
	{
		try
		{
			switch (debugEvent.getDetail())
			{
				case VdmDebugElement.MODEL_DEADLOCKED:
					//target.doSuspend(debugEvent.getSource());
					target.markDeadlocked(debugEvent.getSource());
					//MessageDialog.openError(Activator.getActiveWorkbenchShell(), "Deadlock detected", "Model suspended for inspection do to a DEADLOCK.");
					break;
				case VdmDebugElement.PRE_SUSPEND_REQUEST:
					if(debugEvent.getSource() instanceof VdmThread)
					{
						((VdmThread)debugEvent.getSource()).doPreSuspendRequest(debugEvent.getSource());
					}
					break;
			}
		} catch (DebugException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles delegation of update events
	 * 
	 * @param debugEvent
	 *            debug event with kind set to CHANGE
	 */
	private void handleChange(DebugEvent debugEvent)
	{
		switch (debugEvent.getDetail())
		{
			case DebugEvent.STATE:
				break;

			case DebugEvent.CONTENT:
				break;
			case DebugEvent.UNSPECIFIED:
				break;
		}

	}

	/**
	 * Handles delegation of termination events
	 * 
	 * @param debugEvent
	 *            debug event with kind set to TERMINATED
	 */
	private void handleTerminate(DebugEvent debugEvent)
	{
		try
		{
			target.handleTerminate(debugEvent.getSource());
		} catch (DebugException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
	}

	private void handleCreate(DebugEvent debugEvent)
	{
	}

	/**
	 * Handles delegation of suspended events
	 * 
	 * @param debugEvent
	 *            debug event with kind set to SUSPENDED
	 */
	private void handleSuspend(DebugEvent debugEvent)
	{
		try
		{
			target.doSuspend(debugEvent.getSource());
		} catch (DebugException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
		switch (debugEvent.getDetail())
		{
			case DebugEvent.STEP_END:
				break;

			case DebugEvent.BREAKPOINT:
				break;

			case DebugEvent.CLIENT_REQUEST:
				break;

			case DebugEvent.EVALUATION:
				break;

			case DebugEvent.EVALUATION_IMPLICIT:
				break;
			case DebugEvent.UNSPECIFIED:
				break;
		}

	}

	/**
	 * Handles delegation of resume events
	 * 
	 * @param debugEvent
	 *            debug event with kind set to RESUME
	 */
	private void handleResume(DebugEvent debugEvent)
	{
		try
		{

			switch (debugEvent.getDetail())
			{
				case DebugEvent.STEP_INTO:
					target.doStepInto(debugEvent.getSource());
					break;
				case DebugEvent.STEP_OVER:
					target.doStepOver(debugEvent.getSource());
					break;
				case DebugEvent.STEP_RETURN:
					target.doStepReturn(debugEvent.getSource());
					break;
				case DebugEvent.EVALUATION:
					break;
				case DebugEvent.EVALUATION_IMPLICIT:
					break;
				case DebugEvent.CLIENT_REQUEST:
				case DebugEvent.UNSPECIFIED:
					target.doResume(debugEvent.getSource());
					break;

			}
		} catch (DebugException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * Checks if a given source is a part of the debug target set as target. This is used to insure that other debug
	 * sessions do not fire events which will affect this session
	 * 
	 * @param source
	 *            the source how fires the given debug event
	 * @return true if source is included in the target
	 */
	private boolean hasSource(Object source)
	{
		try
		{
			return (source != null && (target.equals(source) || Arrays.asList(target.getThreads()).contains(source)));
		} catch (DebugException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
			return false;
		}
	}
}
