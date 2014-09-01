/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer.draw;

import java.util.Vector;

import org.overture.ide.plugins.rttraceviewer.data.TraceBus;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceOperation;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;

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
			TraceThread currentThread, TraceThread swappedThread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawDelayedThreadSwapIn(GenericTabItem tab, TraceCPU cpu,
			TraceThread currentThread, TraceThread swappedThread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu,
			TraceThread currentThread, TraceThread swappedThread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu,
			TraceThread currentThread, TraceThread killedThread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu,
			TraceThread currentThread, TraceThread newthread) {
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
