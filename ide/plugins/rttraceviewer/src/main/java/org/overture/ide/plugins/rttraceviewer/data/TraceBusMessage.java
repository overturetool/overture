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
package org.overture.ide.plugins.rttraceviewer.data;

public class TraceBusMessage extends TraceResource
{
    private Long id;
    private Long busId;
    private Long fromCpuId;
    private Long toCpuId;
    private Long callerThreadId;
    
    public TraceBusMessage(Long id, Long busId, Long fromCpuId, Long toCpuId, Long callerThreadId)
    {
    	this.id = id;
    	this.busId = busId;
    	this.fromCpuId = fromCpuId;
    	this.toCpuId = toCpuId;
    	this.callerThreadId = callerThreadId;	    	
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


}
