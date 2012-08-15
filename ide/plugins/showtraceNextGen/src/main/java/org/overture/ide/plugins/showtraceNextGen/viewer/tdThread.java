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

import java.util.Map;
import java.util.Stack;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenObject;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdCPU, TraceData, tdObject
@SuppressWarnings({"unchecked"})
public class tdThread extends tdHistory
{
	
    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
//    private tdCPU theCpu;
    private Long id;
    private Stack<Long> curobj;
    private Boolean blocked;

    public tdThread()
    {
        curobj = new Stack<Long>();
        blocked = new Boolean(false);
    }

    public tdThread(Long pthrid)
    {
        this();
//        theCpu = cpu;
        id = pthrid;
    }

    public Long getId()
    {
        return id;
    }

    public void setStatus(Boolean pblocked)
    {
        blocked = pblocked;
    }

    public Boolean getStatus()
    {
        return blocked;
    }

    public void pushCurrentObject(Long pobjid)
    {
    	curobj.push(pobjid);
    }

    public void popCurrentObject()
    {
    	curobj.pop();
    }

    public Boolean hasCurrentObject()
    {
        return !curobj.isEmpty();
    }

    public tdObject getCurrentObject()
    {
    	tdObject result = null;
    	
        if(hasCurrentObject())
        {
        	Integer currentObjectId = curobj.peek().intValue();
        	
        	Map<Integer, NextGenObject> objMap = NextGenRTLogger.getInstance().getObjectMap();
        	
            for (Integer objectKey : objMap.keySet()) 
            {
            	if(objMap.get(objectKey).id == currentObjectId);
            	{
            		result = null; //TODO MVQ: Get tdboject from tdCPU
            	}
    		}
        }

        return result;
    }

	@Override
	public void reset() throws CGException {
		// TODO MVQ: Edit tdHistory to remove this
		
	}
}