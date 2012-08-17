/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   tdCPU.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;


import org.overture.interpreter.messages.rtlog.nextgen.NextGenBus;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenCpu;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenObject;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData, tdThread, tdObject
@SuppressWarnings({"unchecked","rawtypes"})
public class tdCPU extends tdResource
{
	static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
	private NextGenRTLogger rtLogger;
    private String name;
    private Boolean expl;
    private Vector<Long> threads;
    private Vector<Long> objects;
    private Long current_thread;
    
    public tdCPU(int cpuID)
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
    	NextGenCpu cpu = cpus.get(cpuID);
    	threads = new Vector<Long>();
    	objects = new Vector<Long>();
    	current_thread = null;
    	
    	if(cpu != null)
    	{
    		id = new Long(cpu.id);
            name = cpu.name;
            expl = cpu.expl;
    	}
    	else
    	{
    		//TODO MAA: Exception?
    	}

    }

    public Long getId()
    {
        return id;
    }
 
    public String getName()
    {
        return name;
    }

    public Boolean isExplicit()
    {
        return expl;
    }


    
    public void setCurrentThread(Long curthrd)
    {
    	current_thread = curthrd;
    	this.setIdle(current_thread == null);
    }

    public Boolean hasCurrentThread()
    {
        return current_thread != null;
    }

    public Long getCurrentThread()
    {
    	return current_thread;
    }
    
    public void addThreadId(Long thrid)
    {
    	threads.add(thrid);
    }
    
    public Vector<Long> getThreadIds()
    {
    	return threads;
    }
    
    public void addObject(Long obj)
    {
    	objects.add(obj);
    }
    
    public Vector<Long> getObjectIds()
    {
    	return objects;
    }
    
    
    public Boolean hasObject(Long pobjid)
    {
    	return objects.contains(pobjid);
    }

}