
package org.overture.ide.plugins.showtraceNextGen.draw;

import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class TraceEventViewer extends TraceViewer {

	protected final Long RESOURCE_VINTERVAL = new Long(50L);
	
	//Threads
	public abstract void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu, TraceThread thread);
	
	public abstract void drawDelayedThreadSwapIn(GenericTabItem tab,TraceCPU cpu, TraceThread thread);

	public abstract void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread thread);
	
	public abstract void drawThreadKill(GenericTabItem tab, TraceCPU cpu, TraceThread thread);

	public abstract void drawThreadCreate(GenericTabItem tab, TraceCPU cpu, TraceThread thread);

	//Messages
	public abstract void drawReplyRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op);
	
	public abstract void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op);

	public abstract void drawMessageRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op);
	
	public abstract void drawMessageActivated(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op);

	//Operations
	public abstract void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject sourceObj, TraceOperation operation);

	public abstract void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation);

	public abstract void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation);

	public abstract void drawTimeMarker(GenericTabItem tab, Long markerTime);
	
}
