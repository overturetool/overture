package org.overture.ide.plugins.showtraceNextGen.view;

import java.util.Vector;


public interface ITraceRunner 
{
	 void drawArchitecture(GenericTabItem tab) throws Exception;
	 void drawOverview(GenericTabItem tab, Long eventStartTime) throws Exception;
	 
	 Vector<Long> getCpuIds();
	 String getCpuName(Long cpuId);
	 void drawCpu(GenericTabItem tab, Long cpuId, Long eventStartTime) throws Exception;
}
