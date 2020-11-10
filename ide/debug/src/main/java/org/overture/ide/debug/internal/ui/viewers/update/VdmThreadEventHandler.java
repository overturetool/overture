/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.internal.ui.viewers.update;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ModelDelta;
import org.eclipse.debug.internal.ui.viewers.provisional.AbstractModelProxy;
import org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler;
import org.overture.ide.debug.core.dbgp.IDbgpStatusInterpreterThreadState.InterpreterThreadStatus;
import org.overture.ide.debug.core.model.IVdmThread;

@SuppressWarnings({ "restriction" })
public class VdmThreadEventHandler extends ThreadEventHandler
{
	/**
	 * Map of previous TOS per thread
	 */
	public final static boolean DEBUG = false;

	private Map<IThread, IStackFrame> fLastTopFrame = new HashMap<IThread, IStackFrame>();

	public VdmThreadEventHandler(AbstractModelProxy proxy)
	{
		super(proxy);
	}

	@Override
	public synchronized void dispose()
	{
		fLastTopFrame.clear();
		super.dispose();
	}

	@Override
	protected void handleSuspend(DebugEvent event)
	{

		IThread thread = (IThread) event.getSource();
		if (DEBUG)
		{
			System.out.println("handleSuspend " + thread);
		}

		// TODO temporary fix. Collapses all threads except the running one
		if (!isRunning(thread))
		{
			fireDeltaUpdatingThread(thread, IModelDelta.COLLAPSE);
		}

		if (event.isEvaluation() || !isRunning(thread))
		{
			ModelDelta delta = buildRootDelta();
			ModelDelta node = addPathToThread(delta, thread);
			node = node.addNode(thread, IModelDelta.NO_CHANGE);
			try
			{
				IStackFrame frame = thread.getTopStackFrame();
				if (frame != null)
				{
					int flag = IModelDelta.NO_CHANGE;
					if (event.getDetail() == DebugEvent.EVALUATION)
					{
						// explicit evaluations can change content
						flag = flag | IModelDelta.CONTENT;
					} else if (event.getDetail() == DebugEvent.EVALUATION_IMPLICIT)
					{
						// implicit evaluations can change state
						flag = flag | IModelDelta.STATE;
					}
					node.addNode(frame, flag);
					// fireDelta(delta);
				}
			} catch (DebugException e)
			{
			}
		} else
		{
			queueSuspendedThread(event);
			int extras = IModelDelta.STATE;
			switch (event.getDetail())
			{
				case DebugEvent.BREAKPOINT:
					// on breakpoint also position thread to be top element
					extras = IModelDelta.EXPAND | IModelDelta.REVEAL;
					break;
				case DebugEvent.CLIENT_REQUEST:
					extras = IModelDelta.EXPAND;
					break;
			}
			fireDeltaUpdatingSelectedFrame(thread, IModelDelta.NO_CHANGE
					| extras, event);
		}
	}

	@Override
	protected void handleResume(DebugEvent event)
	{
		IThread thread = removeSuspendedThread(event);
		if (DEBUG)
		{
			System.out.println("handleResume " + thread);
		}
		fireDeltaAndClearTopFrame(thread, IModelDelta.STATE
				| IModelDelta.CONTENT);// | IModelDelta.SELECT
		thread = getNextSuspendedThread();
		if (thread != null)
		{
			fireDeltaUpdatingSelectedFrame(thread, IModelDelta.NO_CHANGE, event);// | IModelDelta.REVEAL
		}
	}

	private void fireDeltaUpdatingSelectedFrame(IThread thread, int flags,
			DebugEvent event)
	{

		ModelDelta delta = buildRootDelta();
		ModelDelta node = addPathToThread(delta, thread);
		IStackFrame prev = null;
		synchronized (this)
		{
			prev = (IStackFrame) fLastTopFrame.get(thread);
		}
		IStackFrame frame = null;
		try
		{
			Object frameToSelect = event.getData();
			if (frameToSelect == null
					|| !(frameToSelect instanceof IStackFrame))
			{
				frame = thread.getTopStackFrame();
			} else
			{
				frame = (IStackFrame) frameToSelect;
			}
		} catch (DebugException e)
		{
		}
		int threadIndex = indexOf(thread);
		int childCount = childCount(thread);
		if (isEqual(frame, prev))
		{
			if (frame == null)
			{
				if (thread.isSuspended())
				{
					// no frames, but suspended - update & select
					node = node.addNode(thread, threadIndex, flags
							| IModelDelta.STATE | IModelDelta.SELECT, childCount);
				}
			} else
			{
				node = node.addNode(thread, threadIndex, flags, childCount);
			}
		} else
		{
			if (event.getDetail() == DebugEvent.STEP_END)
			{
				if (prev == null)
				{
					// see bug 166602 - expand the thread if this is a step end with no previous top frame
					flags = flags | IModelDelta.EXPAND;
				} else if (frame == null)
				{
					// there was a previous frame and current is null on a step: transient state
					return;
				}
			}
			node = node.addNode(thread, threadIndex, flags
					| IModelDelta.CONTENT, childCount);
		}
		if (frame != null)
		{
			node.addNode(frame, indexOf(frame), IModelDelta.STATE
					| IModelDelta.SELECT, childCount(frame));
		}
		synchronized (this)
		{
			if (!isDisposed())
			{
				fLastTopFrame.put(thread, frame);
			}
		}
		fireDelta(delta);
	}

	// @Override
	// protected void handleSuspend(DebugEvent event)
	// {
	// IThread thread = (IThread) event.getSource();
	// if (thread instanceof IVdmThread)
	// {
	// if (((IVdmThread) thread).getInterpreterState().getState() == InterpreterThreadStatus.RUNNING)
	// {
	// //super.handleSuspend(new DebugEvent(event.getSource(), event.getKind(), DebugEvent.BREAKPOINT));
	// return;
	// } else
	// {
	// //super.handleSuspend(new DebugEvent(event.getSource(), DebugEvent.EVALUATION));
	// return;
	// }
	//
	// }
	// super.handleSuspend(event);
	// // IThread thread = (IThread) event.getSource();
	// // System.out.println("handleSuspend "+thread);
	// // if(thread instanceof IVdmThread)
	// // {
	// // if(((IVdmThread)thread).getInterpreterState().getState()==InterpreterThreadStatus.RUNNING)
	// // {
	// //
	// //
	// // if (event.isEvaluation()) {
	// // ModelDelta delta = buildRootDelta();
	// // ModelDelta node = addPathToThread(delta, thread);
	// // node = node.addNode(thread, IModelDelta.NO_CHANGE);
	// // try {
	// // IStackFrame frame = thread.getTopStackFrame();
	// // if (frame != null) {
	// // int flag = IModelDelta.NO_CHANGE;
	// // if (event.getDetail() == DebugEvent.EVALUATION) {
	// // // explicit evaluations can change content
	// // flag = flag | IModelDelta.CONTENT;
	// // } else if (event.getDetail() == DebugEvent.EVALUATION_IMPLICIT) {
	// // // implicit evaluations can change state
	// // flag = flag | IModelDelta.STATE;
	// // }
	// // node.addNode(frame, flag);
	// // fireDelta(delta);
	// // }
	// // } catch (DebugException e) {
	// // }
	// // } else {
	// // queueSuspendedThread(event);
	// // int extras = IModelDelta.STATE;
	// // switch (event.getDetail()) {
	// // case DebugEvent.BREAKPOINT:
	// // // on breakpoint also position thread to be top element
	// // extras = IModelDelta.EXPAND | IModelDelta.REVEAL;
	// // break;
	// // case DebugEvent.CLIENT_REQUEST:
	// // extras = IModelDelta.EXPAND;
	// // break;
	// // }
	// // fireDeltaUpdatingSelectedFrame(thread, IModelDelta.NO_CHANGE | extras, event);
	// // } }else{
	// // System.out.println("--- skipped");
	// // fireDeltaAndClearTopFrame(thread, IModelDelta.COLLAPSE);
	// // }
	// // }
	// }

	// @Override
	// protected void handleResume(DebugEvent event)
	// {
	// IThread thread = removeSuspendedThread(event);
	// System.out.println("handleResume " + thread);
	// fireDeltaAndClearTopFrame(thread, IModelDelta.STATE
	// | IModelDelta.CONTENT);// | IModelDelta.SELECT
	// thread = getNextSuspendedThread();
	// if (thread != null) {
	// fireDeltaUpdatingSelectedFrame(thread, IModelDelta.NO_CHANGE , event);//| IModelDelta.REVEAL
	// }
	// }

	@Override
	protected void handleChange(DebugEvent event)
	{
		// System.out.println("handleChange " + event.getSource());
		//
		IThread thread = (IThread) event.getSource();
		if (DEBUG)
		{
			System.out.println("handleChange " + thread);
		}
		if (isRunning(thread) || isWaiting(thread))
		{
			IStackFrame frame = null;
			try
			{
				frame = thread.getTopStackFrame();
				int threadIndex = indexOf(thread);
				ModelDelta delta = buildRootDelta();
				ModelDelta node = addPathToThread(delta, thread);
				int childCount = childCount(thread);
				if (DEBUG)
				{
					System.out.println("child: " + childCount);
				}
				node = node.addNode(thread, threadIndex, IModelDelta.CONTENT
						| IModelDelta.STATE | IModelDelta.SELECT, childCount);
				if (frame != null)
				{
					node.addNode(frame, indexOf(frame), IModelDelta.STATE
							| IModelDelta.SELECT, childCount(frame));

				}
				if (DEBUG)
				{
					System.out.println(delta);
				}
				fireDelta(delta);
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			if (event.getDetail() == DebugEvent.STATE)
			{
				fireDeltaUpdatingThread((IThread) event.getSource(), IModelDelta.STATE);
			} else
			{
				fireDeltaUpdatingThread((IThread) event.getSource(), IModelDelta.CONTENT);
			}
		}

		// IThread thread = (IThread) event.getSource();
		// if(thread instanceof IVdmThread)
		// {
		// if(((IVdmThread)thread).getInterpreterState().getState()==InterpreterThreadStatus.RUNNING)
		// {
		// System.out.println(((IVdmThread)thread).getInterpreterState());
		// //fireDeltaUpdatingSelectedFrame2(thread,flags ,event);//new
		// DebugEvent(event.getSource(),event.getKind(),DebugEvent.STEP_END));
		// IStackFrame frame = null;
		// try
		// {
		// frame = thread.getTopStackFrame();
		// int threadIndex = indexOf(thread);
		// ModelDelta delta = buildRootDelta();
		// ModelDelta node = addPathToThread(delta, thread);
		// int childCount = childCount(thread);
		// System.out.println("child: "+childCount);
		// node = node.addNode(thread, threadIndex, IModelDelta.CONTENT | IModelDelta.STATE | IModelDelta.SELECT,
		// childCount);
		// if (frame != null) {
		// node.addNode(frame, indexOf(frame), IModelDelta.STATE | IModelDelta.SELECT, childCount(frame));
		//
		// }
		// System.out.println(delta);
		// fireDelta(delta);
		// } catch (DebugException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }}
	}

	@Override
	protected void handleCreate(DebugEvent event)
	{
		fireDeltaAndClearTopFrame((IThread) event.getSource(), IModelDelta.ADDED
				| IModelDelta.STATE);
	}

	@Override
	protected void handleLateSuspend(DebugEvent suspend, DebugEvent resume)
	{
		// IThread thread = queueSuspendedThread(suspend);
		// if (suspend.isEvaluation() && suspend.getDetail() == DebugEvent.EVALUATION_IMPLICIT) {
		// // late implicit evaluation - update thread and frame
		// ModelDelta delta = buildRootDelta();
		// ModelDelta node = addPathToThread(delta, thread);
		// node = node.addNode(thread, IModelDelta.STATE);
		// try {
		// IStackFrame frame = thread.getTopStackFrame();
		// if (frame != null) {
		// node.addNode(frame, IModelDelta.STATE);
		// fireDelta(delta);
		// }
		// } catch (DebugException e) {
		// }
		// } else {
		// fireDeltaUpdatingSelectedFrame(thread, IModelDelta.STATE | IModelDelta.EXPAND, suspend);
		// }

		// super.handleLateSuspend(suspend, resume);
		// IThread thread = queueSuspendedThread(suspend);
		// System.out.println("handleLateSuspend "+thread);
		// if (suspend.isEvaluation() && suspend.getDetail() == DebugEvent.EVALUATION_IMPLICIT) {
		// // late implicit evaluation - update thread and frame
		// ModelDelta delta = buildRootDelta();
		// ModelDelta node = addPathToThread(delta, thread);
		// node = node.addNode(thread, IModelDelta.STATE);
		// try {
		// IStackFrame frame = thread.getTopStackFrame();
		// if (frame != null) {
		// node.addNode(frame, IModelDelta.STATE);
		// fireDelta(delta);
		// }
		// } catch (DebugException e) {
		// }
		// } else {
		// fireDeltaUpdatingSelectedFrame(thread, IModelDelta.STATE | IModelDelta.EXPAND, suspend);
		// }
	}

	// private void fireDeltaUpdatingSelectedFrame(IThread thread, int flags, DebugEvent event) {
	// if(thread instanceof IVdmThread)
	// {
	// if(((IVdmThread)thread).getInterpreterState().getState()==InterpreterThreadStatus.RUNNING)
	// {
	// System.out.println(((IVdmThread)thread).getInterpreterState());
	// //fireDeltaUpdatingSelectedFrame2(thread,flags ,event);//new
	// DebugEvent(event.getSource(),event.getKind(),DebugEvent.STEP_END));
	// IStackFrame frame = null;
	// try
	// {
	// frame = thread.getTopStackFrame();
	// int threadIndex = indexOf(thread);
	// ModelDelta delta = buildRootDelta();
	// ModelDelta node = addPathToThread(delta, thread);
	// int childCount = childCount(thread);
	// System.out.println("child: "+childCount);
	// node = node.addNode(thread, threadIndex, flags | IModelDelta.STATE | IModelDelta.SELECT, childCount);
	// if (frame != null) {
	// node.addNode(frame, indexOf(frame), IModelDelta.STATE | IModelDelta.SELECT, childCount(frame));
	//
	// }
	// System.out.println(delta);
	// fireDelta(delta);
	// } catch (DebugException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }else
	// {
	// // ModelDelta delta = buildRootDelta();
	// // ModelDelta node = addPathToThread(delta, thread);
	// // node = node.addNode(thread, IModelDelta.COLLAPSE);
	// // fireDelta(delta);
	// }
	// }
	// }

	// private void fireDeltaUpdatingSelectedFrame2(IThread thread, int flags, DebugEvent event) {
	// ModelDelta delta = buildRootDelta();
	// ModelDelta node = addPathToThread(delta, thread);
	// IStackFrame prev = null;
	// synchronized (this) {
	// prev = (IStackFrame) fLastTopFrame.get(thread);
	// }
	// IStackFrame frame = null;
	// try {
	// Object frameToSelect = event.getData();
	// if (frameToSelect == null || !(frameToSelect instanceof IStackFrame)) {
	// frame = thread.getTopStackFrame();
	// } else {
	// frame = (IStackFrame)frameToSelect;
	// }
	// } catch (DebugException e) {
	// }
	// int threadIndex = indexOf(thread);
	// int childCount = childCount(thread);
	// if (isEqual(frame, prev)) {
	// if (frame == null) {
	// if (thread.isSuspended()) {
	// // no frames, but suspended - update & select
	// node = node.addNode(thread, threadIndex, flags | IModelDelta.STATE | IModelDelta.SELECT, childCount);
	// }
	// } else {
	// node = node.addNode(thread, threadIndex, flags, childCount);
	// }
	// } else {
	// if (event.getDetail() == DebugEvent.STEP_END) {
	// if (prev == null) {
	// // see bug 166602 - expand the thread if this is a step end with no previous top frame
	// flags = flags | IModelDelta.EXPAND;
	// } else if (frame == null) {
	// // there was a previous frame and current is null on a step: transient state
	// return;
	// }
	// }
	// node = node.addNode(thread, threadIndex, flags | IModelDelta.CONTENT, childCount);
	// }
	// if (frame != null) {
	// node.addNode(frame, indexOf(frame), IModelDelta.STATE | IModelDelta.SELECT, childCount(frame));
	//
	// }
	// synchronized (this) {
	// if (!isDisposed()) {
	// fLastTopFrame.put(thread, frame);
	// }
	// }
	// System.out.println(delta);
	// fireDelta(delta);
	// }
	//
	private boolean isEqual(Object o1, Object o2)
	{
		if (o1 == o2)
		{
			return true;
		}
		if (o1 == null)
		{
			return false;
		}
		return o1.equals(o2);
	}

	// private void fireDeltaAndClearTopFrame(IThread thread, int flags)
	// {
	// ModelDelta delta = buildRootDelta();
	// ModelDelta node = addPathToThread(delta, thread);
	// node.addNode(thread, indexOf(thread), flags);
	// synchronized (this)
	// {
	// fLastTopFrame.remove(thread);
	// }
	// fireDelta(delta);
	// }
	private void fireDeltaAndClearTopFrame(IThread thread, int flags)
	{
		ModelDelta delta = buildRootDelta();
		ModelDelta node = addPathToThread(delta, thread);
		node.addNode(thread, indexOf(thread), flags);
		synchronized (this)
		{
			fLastTopFrame.remove(thread);
		}
		fireDelta(delta);
	}

	//
	// //
	private void fireDeltaUpdatingThread(IThread thread, int flags)
	{
		ModelDelta delta = buildRootDelta();
		ModelDelta node = addPathToThread(delta, thread);
		node = node.addNode(thread, flags);
		fireDelta(delta);
	}

	public boolean isRunning(IThread thread)
	{
		if (thread instanceof IVdmThread)
		{
			IVdmThread t = (IVdmThread) thread;
			return t.getInterpreterState() != null
					&& t.getInterpreterState().getState() == InterpreterThreadStatus.RUNNING;
		}
		return false;

	}
	
	public boolean isWaiting(IThread thread)
	{
		if (thread instanceof IVdmThread)
		{
			IVdmThread t = (IVdmThread) thread;
			return t.getInterpreterState() != null
					&& t.getInterpreterState().getState() == InterpreterThreadStatus.WAITING;
		}
		return false;

	}
}
