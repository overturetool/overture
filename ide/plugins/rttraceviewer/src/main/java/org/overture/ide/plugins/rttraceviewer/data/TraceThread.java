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

import java.util.Stack;

public class TraceThread extends TraceResource
{
    private Long id;
    private Boolean blocked;
    private Stack<TraceObject> objectStack;

    public TraceThread(Long id)
    {
    	objectStack = new Stack<TraceObject>();

    	this.id = id;
    	
    	blocked = new Boolean(false);   	
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
      
    public void pushCurrentObject(TraceObject currentObjectId)
    {
    	objectStack.push(currentObjectId);
    }
    
    public void popCurrentObject()
    {
    	objectStack.pop();
    }
    
    public TraceObject getCurrentObject()
    {	
    	return objectStack.peek();
    }
    
    public boolean hasCurrentObject()
    {
    	
    	return !objectStack.isEmpty();
    }

}
