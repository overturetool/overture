package org.overture.ide.debug.core.model;

import org.overture.ide.debug.core.model.internal.VdmThread;


public interface IVdmStack {
	VdmThread getThread();

	int size();

	boolean hasFrames();

	IVdmStackFrame[] getFrames();

	IVdmStackFrame getTopFrame();
}
