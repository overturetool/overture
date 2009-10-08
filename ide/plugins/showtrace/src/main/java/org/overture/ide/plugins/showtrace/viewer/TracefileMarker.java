// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileMarker.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.HashSet;
import java.util.Iterator;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
@SuppressWarnings("unchecked")
public class TracefileMarker
{

    public TracefileMarker()
        throws CGException
    {
        markers = new HashSet();
        errors = null;
        warnings = null;
        try
        {
            markers = new HashSet();
            errors = new Long(0L);
            warnings = new Long(0L);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public void addError(String var_1_1, Integer var_2_2)
        throws CGException
    {
        try
        {
            IMarker theMarker = ResourcesPlugin.getWorkspace().getRoot().createMarker("org.eclipse.core.resources.problemmarker");
            theMarker.setAttribute("message", var_1_1);
            if(var_2_2 != null)
                theMarker.setAttribute("lineNumber", new Integer(var_2_2.intValue()));
            theMarker.setAttribute("severity", new Integer(2));
            markers.add(theMarker);
            errors = Long.valueOf(errors.longValue() + 1L);
        }
        catch(CoreException ce)
        {
            ce.printStackTrace();
        }
    }

    public void addWarning(String var_1_1, Integer var_2_2)
        throws CGException
    {
        try
        {
            IMarker theMarker = ResourcesPlugin.getWorkspace().getRoot().createMarker("org.eclipse.core.resources.problemmarker");
            theMarker.setAttribute("message", var_1_1);
            if(var_2_2 != null)
                theMarker.setAttribute("lineNumber", new Integer(var_2_2.intValue()));
            theMarker.setAttribute("severity", new Integer(1));
            markers.add(theMarker);
            warnings = Long.valueOf(warnings.longValue() + 1L);
        }
        catch(CoreException ce)
        {
            ce.printStackTrace();
        }
    }

    public Long errorCount()
        throws CGException
    {
        return errors;
    }

    public Long warningCount()
        throws CGException
    {
        return warnings;
    }

    public void dispose()
        throws CGException
    {
        IMarker mark = null;
        for(Iterator enum_6 = markers.iterator(); enum_6.hasNext();)
        {
            IMarker elem_2 = (IMarker)enum_6.next();
            mark = elem_2;
            try
            {
                mark.delete();
            }
            catch(CoreException ce)
            {
                ce.printStackTrace();
            }
        }

    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashSet markers;
    private Long errors;
    private Long warnings;

}