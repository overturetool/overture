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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenCpu;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

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
    
    public tdCPU(int cpuID)
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	Map<Integer, NextGenCpu> cpus = rtLogger.getCpuMap();
    	NextGenCpu cpu = cpus.get(cpuID);
    	
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
        throws CGException
    {
        return id;
    }

    public String getName()
        throws CGException
    {
        return name;
    }

    public Boolean isExplicit()
        throws CGException
    {
        return expl;
    }

    public HashSet connects()
        throws CGException
    {
        return new HashSet(); //TODO
    }

    public void connect(Long pbid)
        throws CGException
    {
        //bus_uconnect.add(pbid); //TODO
    	
    }

    public tdThread getThread(Long pthrid)
        throws CGException
    {

        //return (tdThread)threads.get(pthrid);
        return new tdThread(); //TODO
    }

    public void setCurrentThread(Long pthr)
        throws CGException
    {
        //TODO
    }

    public Boolean hasCurrentThread()
        throws CGException
    {
        return false; //TODO
    }

    public tdThread getCurrentThread()
        throws CGException
    {
        return new tdThread(); //TODO
    }

    public void addObject(tdObject pobj)
        throws CGException
    {
       //TODO
    }

    public Boolean hasObject(Long pobjid)
        throws CGException
    {
        return false; //TODO
    }

    public Boolean hasObjectAt(Long objectId, Long time)
    {
    	return false; //TODO
    }
    
    public tdObject getObject(Long pobjid)
        throws CGException
    {
        return new tdObject(); //TODO
    }

    public HashSet getObjects()
        throws CGException
    {
        return new HashSet(); //TODO
    }

    @Override
	public void reset()
        throws CGException
    {
       //TODO
    }

}