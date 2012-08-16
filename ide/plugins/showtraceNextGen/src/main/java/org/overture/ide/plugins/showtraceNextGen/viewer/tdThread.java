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
// Source File Name:   tdThread.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.Stack;

import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThread;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdCPU, TraceData, tdObject
public class tdThread 
{
	
    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private Boolean blocked;
    private Stack<Long> objectIds;


    private NextGenRTLogger rtLogger;
    
    public tdThread(Long threadId)
    {
    	objectIds = new Stack<Long>();
    	rtLogger = NextGenRTLogger.getInstance();
    	NextGenThread thread = rtLogger.getThreadMap().get(threadId);
    	blocked = new Boolean(false);
    	
    	if(thread != null)
    	{
    		id = thread.id;
    		
    	}else
    	{
    		//TODO Peter
    	}
    	
    }

    public Long getId()
    {
        return id;
    }
    
    public void setStatus(Boolean pblocked)
    {
        blocked = (Boolean)UTIL.clone(pblocked);
    }

    public Boolean getStatus()
    {
        return blocked;
    }
    
    
     
    public void pushCurrentObjectId(Long currentObjectId)
    {
    	objectIds.push(currentObjectId);
    }
    
    public void popCurrentObjectId()
    {
    	objectIds.pop();
    }
    
    public Long getCurrentObjectId(){
    	
    	return objectIds.peek();
    }
    
    public boolean hasCurrentObject(){
    	
    	return objectIds.isEmpty();
    }

}