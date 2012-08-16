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
// Source File Name:   tdResource.java

package org.overture.ide.plugins.showtraceNextGen.viewer;



// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, TraceData
public abstract class tdResource
{
    protected Long xpos;
    protected Long ypos;
    protected Boolean idle;
    
    public tdResource()
    {
        xpos = new Long(0L);
        ypos = new Long(0L);
        idle = new Boolean(true);
    }

    public void setX(Long px)
    {
        xpos = px;
    }

    public void setY(Long py)
    {
        ypos = py;
    }
    
    public void setIdle(Boolean pidle)
    {
        idle = pidle;
    }

    public Long getX()
    {
        return xpos;
    }

    public Long getY()
    {
        return ypos;
    }
    
    public Boolean isIdle()
    {
        return idle;
    }


}