package org.overture.ide.plugins.showtraceNextGen.draw;

import java.util.Vector;

import org.overture.ide.plugins.showtraceNextGen.data.TraceBus;
import org.overture.ide.plugins.showtraceNextGen.data.TraceCPU;
import org.overture.ide.plugins.showtraceNextGen.data.TraceObject;
import org.overture.ide.plugins.showtraceNextGen.data.TraceOperation;
import org.overture.ide.plugins.showtraceNextGen.data.TraceThread;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;

public class DummyViewer extends TraceEventViewer {

	@Override
	public void drawTimelines(GenericTabItem tab) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawStaticItems(GenericTabItem tab, Vector<TraceCPU> cpus,
			Vector<TraceBus> buses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawDelayedThreadSwapIn(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawReplyRequest(GenericTabItem tab, TraceCPU cpu,
			TraceObject object, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu,
			TraceThread callerThread, TraceBus bus, TraceOperation op,
			TraceObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageRequest(GenericTabItem tab, TraceCPU cpu,
			TraceObject object, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageActivated(GenericTabItem tab, TraceCPU cpu,
			TraceObject object, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawOpCompleted(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceObject sourceObj, TraceOperation operation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawOpActivate(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceObject destinationObj,
			TraceOperation operation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceObject destinationObj,
			TraceOperation operation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime) {
		// TODO Auto-generated method stub
		
	}

}
