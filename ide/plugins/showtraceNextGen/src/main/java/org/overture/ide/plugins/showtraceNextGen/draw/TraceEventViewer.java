
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
	public abstract void drawReplyRequest(GenericTabItem tab,INextGenEvent pitrr);
	
	public abstract void drawMessageCompleted(GenericTabItem tab,INextGenEvent pitmc);

	public abstract void drawMessageRequest(GenericTabItem tab,INextGenEvent pitmr);
	
	public abstract void drawMessageActivated(GenericTabItem tab, INextGenEvent event);

	//Operations
	public abstract void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject sourceObj);

	public abstract void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation);

	public abstract void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation);

	public abstract void drawTimeMarker(GenericTabItem tab, Long markerTime);
	
}
