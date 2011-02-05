package org.overture.ide.debug.core.model.internal;

import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IVdmStack;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.logging.LogItem;

public class VdmStack implements IVdmStack {
	public static final IVdmStackFrame[] NO_STACK_FRAMES = new IVdmStackFrame[0];

	private IVdmStackFrame[] frames;
	private final Object framesLock = new Object();

	private final VdmThread thread;

	public VdmStack(VdmThread thread) {
		this.thread = thread;
		this.frames = NO_STACK_FRAMES;
	}

	public void update(boolean logErrors) {
		try {
			readFrames();
			//updateFrames();
		} catch (DbgpException e) {
			if (logErrors) {
				VdmDebugPlugin.log(e);
			}
		}
	}

	protected IDbgpStackLevel[] requrestStackLevels() throws DbgpException {
		return thread.getDbgpSession().getCoreCommands().getStackLevels();
	}

	protected void readFrames() throws DbgpException {
		thread.getVdmDebugTarget().printLog(new LogItem(
				((VdmThread)thread).getDbgpSession().getInfo(),
				"REQUEST", 			
				true, "Stack Levels"
				));
		final IDbgpStackLevel[] levels = requrestStackLevels();
		thread.getVdmDebugTarget().printLog(new LogItem(
				((VdmThread)thread).getDbgpSession().getInfo(),
				"RESPONSE", 			
				false, "Stack Levels"
				));
		
		
		synchronized (framesLock) {
			final int newSize = levels.length;
			final int oldSize = frames.length;
			final int numToRebind = Math.min(newSize, oldSize);
			final VdmStackFrame[] newFrames = new VdmStackFrame[newSize];
			for (int depth = 0; depth < numToRebind; ++depth) {
				final VdmStackFrame oldFrame = (VdmStackFrame) frames[oldSize
						- depth - 1];
				newFrames[newSize - depth - 1] = oldFrame.bind(levels[newSize
						- depth - 1]);
			}
			final int newCount = newSize - oldSize;
			for (int i = 0; i < newCount; ++i) {
				newFrames[i] = new VdmStackFrame(this, levels[i]);
			}
			frames = newFrames;
			DebugEventHelper.fireChangeEvent(getThread());//todo FOLLOWUP added for debug view
		}
	}

	public VdmThread getThread() {
		return thread;
	}

	public int size() {
		synchronized (framesLock) {
			return frames.length;
		}
	}

	public boolean hasFrames() {
		synchronized (framesLock) {
			return frames.length > 0;
		}
	}

	public IVdmStackFrame[] getFrames() {
		synchronized (framesLock) {
			return frames;
		}
	}

	public IVdmStackFrame getTopFrame() {
		synchronized (framesLock) {
			return frames.length > 0 ? frames[0] : null;
		}
	}

	public void updateFrames() {
		synchronized (framesLock) {
			for (int i = 0; i < frames.length; i++) {
				((VdmStackFrame) frames[i]).updateVariables();
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isInitialized() {
		synchronized (framesLock) {
			return frames != NO_STACK_FRAMES;
		}
	}
}
