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

import java.util.Vector;

public class TraceBus extends TraceResource
{
    private Long id;
    private String name;
    private Boolean isVirtual;
    private Vector<Long> connectedCPUs;

    public TraceBus(Long id, String name, Boolean isVirtual, Vector<Long> connectedCPUs)
    {
		this.id = id;
		this.name = name;
		this.isVirtual = isVirtual;    	
		this.connectedCPUs = connectedCPUs;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Boolean isVirtual()
    {
        return isVirtual;
    }
    
    public Vector<Long> getConnectedCpuIds()
    {
    	return connectedCPUs;
    }

}
