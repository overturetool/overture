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

public class TraceCPU extends TraceResource
{
    private Long id;
    private String name;
    private Boolean isVirtual;
    private Long current_thread;
    protected Boolean idle;
    
    public TraceCPU(Long id, String name, boolean isVirtual)
    {
    	current_thread = null;

		this.id = id;
        this.name = name;
        this.isVirtual = isVirtual;
        idle = new Boolean(true);
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
  
    public void setCurrentThread(Long curthrd)
    {
    	current_thread = curthrd;
    	//this.setIdle(current_thread == null);
    }

    public void setIdle(Boolean pidle)
    {
        idle = pidle;
    }
    
    public Boolean isIdle()
    {
        return idle;
    }
    
    public Boolean hasCurrentThread()
    {
        return current_thread != null;
    }

    public Long getCurrentThread()
    {
    	return current_thread;
    }
    
}
