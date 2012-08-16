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
// Source File Name:   tdMessage.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.Map;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessage;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

import jp.co.csk.vdm.toolbox.VDM.*;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdBUS

public class tdMessage
{

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();

    private Long id;
    private Long busId;
    private Long fromCpuId;
    private Long toCpuId;
    private Long callerThreadId;
    private Long objectId;
    private NextGenRTLogger rtLogger;
	
    public tdMessage(Long messageId)
    {
    	rtLogger = NextGenRTLogger.getInstance();
    	Map<Long, NextGenBusMessage> messages = rtLogger.getBusMessage();
    	NextGenBusMessage message = messages.get(messageId);
    	
    	//TODO MAA: Check for message = null
    	
    	id = messageId;
    	busId = new Long(message.bus.id);
    	fromCpuId = new Long(message.fromCpu.id);
    	toCpuId = new Long(message.toCpu.id);
    	callerThreadId = new Long(message.callerThread.id);
    	
    	//TODO MAA: Check for null reference. Object is allowed to be null? 
    	
    	//objectId = new Long(message.object.id);
    	
    }

    public Long getMsgId()
    {
        return id;
    }

    public Long getBusId()
    {
    	return busId;
    }

    public Long getFromCpu()
    {
        return fromCpuId;
    }

    public Long getFromThread()
    {
        return callerThreadId;
    }

    public Long getToCpu()
    {
        return toCpuId;
    }

//    public Long getToObj()
//    {
//        return objectId;
//    }


}