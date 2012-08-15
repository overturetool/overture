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
// Source File Name:   tdHistory.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlTraceEvent;
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class tdHistory
{

    public tdHistory()
    {

    }

    protected Vector insert(Long pval, Vector pseq) 
    {
        Vector res = new Vector();
        
        return res; //TODO MAA:
    }

    public void addHistory(IOmlTraceEvent pite, Long ptime)
    {
       
    }

    public Vector getHistory(Long ptime)
    {
    	//TODO MAA: This function is invoked by TraceFileVisitor to get all INextGenEvent associated with derived td class!
    	return new Vector();
    }

    public Vector getTimes()
    {
        return new Vector(); //TODO MAA
    }


}