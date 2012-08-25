
package org.overture.ide.plugins.showtraceNextGen.draw;

import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class TraceEventViewer extends TraceViewer {

	protected final Long RESOURCE_VINTERVAL = new Long(50L);
	
	//Threads
	public abstract void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu);
	
	public abstract void drawDelayedThreadSwapIn(GenericTabItem pgti,TraceCPU cpu);

	public abstract void drawThreadSwapIn(GenericTabItem pgti, TraceCPU cpu);
	
	public abstract void drawThreadKill(GenericTabItem pgti, TraceCPU cpu);

	public abstract void drawThreadCreate(GenericTabItem pgti, TraceCPU cpu);

	//Messages
	public abstract void drawReplyRequest(GenericTabItem pgti,INextGenEvent pitrr);
	
	public abstract void drawMessageCompleted(GenericTabItem pgti,INextGenEvent pitmc);

	public abstract void drawMessageRequest(GenericTabItem pgti,INextGenEvent pitmr);
	
	public abstract void drawMessageActivated(GenericTabItem tab, INextGenEvent event);

	//Operations
	public abstract void drawOpCompleted(GenericTabItem pgti, INextGenEvent pioc);

	public abstract void drawOpActivate(GenericTabItem pgti, INextGenEvent pioa);

	public abstract void drawOpRequest(GenericTabItem pgti, INextGenEvent pior);

	public abstract void drawTimeMarker(GenericTabItem tab, Long markerTime);
	
}
