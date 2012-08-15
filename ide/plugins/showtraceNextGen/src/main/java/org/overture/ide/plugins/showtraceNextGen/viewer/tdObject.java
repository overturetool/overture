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
// Source File Name:   tdObject.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.Map;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenObject;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;


// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdResource, TraceData
@SuppressWarnings({"unchecked"})
public class tdObject extends tdResource
{
    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long id;
    private String name;
    private NextGenRTLogger rtLogger;
    
    public tdObject(Long objectId)
    {
    	rtLogger = NextGenRTLogger.getInstance();
        id = objectId;
        Map<Integer, NextGenObject> objects = rtLogger.getObjectMap();
        name = objects.get(objectId.intValue()).classDef.name; //TODO MAA: Check if this is the correct place to get the name?
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}