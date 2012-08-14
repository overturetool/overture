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

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, TraceData
public class tdResource extends tdHistory
{

    public tdResource()
    {
        xpos = null;
        ypos = null;
        ctime = null;
        idle = null;
        data = null;
        try
        {
            xpos = new Long(0L);
            ypos = new Long(0L);
            ctime = new Long(0L);
            idle = new Boolean(true);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public void setX(Long px)
        throws CGException
    {
        xpos = UTIL.NumberToLong(UTIL.clone(px));
    }

    public void setY(Long py)
        throws CGException
    {
        ypos = UTIL.NumberToLong(UTIL.clone(py));
    }

    public void setTime(Long ptime)
        throws CGException
    {
        ctime = UTIL.NumberToLong(UTIL.clone(ptime));
    }

    public void setIdle(Boolean pidle)
        throws CGException
    {
        idle = (Boolean)UTIL.clone(pidle);
    }

    public Long getX()
        throws CGException
    {
        return xpos;
    }

    public Long getY()
        throws CGException
    {
        return ypos;
    }

    public Long getTime()
        throws CGException
    {
        return ctime;
    }

    public Boolean isIdle()
        throws CGException
    {
        return idle;
    }

    public TraceData getTraceData()
        throws CGException
    {
        return data;
    }

    @Override
	public void reset()
        throws CGException
    {
        xpos = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        ypos = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        ctime = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        idle = (Boolean)UTIL.clone(new Boolean(true));
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    protected Long xpos;
    protected Long ypos;
    protected Long ctime;
    protected Boolean idle;
    protected TraceData data;

}