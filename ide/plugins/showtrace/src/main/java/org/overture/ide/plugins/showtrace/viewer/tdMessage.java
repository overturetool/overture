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

package org.overture.ide.plugins.showtrace.viewer;

import jp.co.csk.vdm.toolbox.VDM.*;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            tdHistory, tdBUS

public class tdMessage extends tdHistory
{

    public tdMessage()
        throws CGException
    {
        theBus = null;
        id = null;
        from_ucpu = null;
        from_uthr = null;
        to_ucpu = null;
        to_uthr = null;
        to_uobj = null;
        descr = null;
    }

    public tdMessage(tdBUS bus, Long pmsgid, Long pfrom, Long pfromthr, Long pto, Long ptothr, Long ptoobj, 
            String pdescr)
        throws CGException
    {
        this();
        theBus = (tdBUS)UTIL.clone(bus);
        id = UTIL.NumberToLong(UTIL.clone(pmsgid));
        from_ucpu = UTIL.NumberToLong(UTIL.clone(pfrom));
        from_uthr = UTIL.NumberToLong(UTIL.clone(pfromthr));
        to_ucpu = UTIL.NumberToLong(UTIL.clone(pto));
        to_uthr = UTIL.NumberToLong(UTIL.clone(ptothr));
        to_uobj = UTIL.NumberToLong(UTIL.clone(ptoobj));
        descr = UTIL.ConvertToString(UTIL.clone(pdescr));
    }

    public Long getMsgId()
        throws CGException
    {
        return id;
    }

    public Long getBusId()
        throws CGException
    {
        Long rexpr_1 = null;
        rexpr_1 = theBus.getId();
        return rexpr_1;
    }

    public Long getFromCpu()
        throws CGException
    {
        return from_ucpu;
    }

    public Long getFromThread()
        throws CGException
    {
        return from_uthr;
    }

    public Long getToCpu()
        throws CGException
    {
        return to_ucpu;
    }

    public Boolean hasToThread()
        throws CGException
    {
        return new Boolean(!UTIL.equals(to_uthr, null));
    }

    public Long getToThread()
        throws CGException
    {
        if(!pre_getToThread().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getToThread");
        return to_uthr;
    }

    public Boolean pre_getToThread()
        throws CGException
    {
        return hasToThread();
    }

    public Long getToObj()
        throws CGException
    {
        if(!pre_getToObj().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getToObj");
        return to_uobj;
    }

    public Boolean pre_getToObj()
        throws CGException
    {
        return hasToObj();
    }

    public Boolean hasToObj()
        throws CGException
    {
        return new Boolean(!UTIL.equals(to_uobj, null));
    }

    public String getDescr()
        throws CGException
    {
        return descr;
    }

    @Override
	public void reset()
        throws CGException
    {
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private tdBUS theBus;
    private Long id;
    private Long from_ucpu;
    private Long from_uthr;
    private Long to_ucpu;
    private Long to_uthr;
    private Long to_uobj;
    private String descr;

}