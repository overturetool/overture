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
// Source File Name:   tdBUS.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenBus;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessage;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData, tdMessage
@SuppressWarnings({"unchecked","rawtypes"})
public class tdBUS extends tdResource
{

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private String name;
    private Boolean expl;
    
    private NextGenRTLogger rtLogger;
	
    public tdBUS(int busId)
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	NextGenBus bus = rtLogger.getBusMap().get(busId);
    	
    	if(bus != null)
    	{
    		id = new Long(bus.id);
    		name = bus.name;
    		
    		//Explicit false => virtual
    		expl = this.id.intValue() != rtLogger.getvBus().id;

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


    public tdMessage getMessage(Long pmsgid)
    {
        
        NextGenBusMessage message = rtLogger.getBusMessage().get(pmsgid);
        
        if(message != null)
        {
        	return new tdMessage(pmsgid.intValue());
        }
        else
        {
        	//TODO Peter
        	return null;
        }
        
    }

}