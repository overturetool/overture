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
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name: TracefileMarker.java

package org.overture.ide.plugins.showtraceNextGen.view;

import java.util.HashSet;
import java.util.Iterator;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.utility.FileUtility;

@SuppressWarnings({"unchecked","rawtypes"})
public class TracefileMarker
{
	IFile file;
	static VDMCompare vdmComp = new VDMCompare();
	private HashSet markers;
	private Long errors;
	private Long warnings;

	public TracefileMarker(IFile file) throws CGException
	{
		this.file = file;
		markers = new HashSet();
		errors = null;
		warnings = null;
		try
		{
			markers = new HashSet();
			errors = new Long(0L);
			warnings = new Long(0L);
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
			System.out.println(e.getMessage());
		}
	}

	public void addError(String var_1_1, Integer var_2_2) throws CGException
	{
		try
		{
			IMarker theMarker = null;
			if (file == null)
			{
				theMarker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IMarker.PROBLEM);
				theMarker.setAttribute(IMarker.MESSAGE, var_1_1);
				if (var_2_2 != null)
				{
					theMarker.setAttribute(IMarker.LINE_NUMBER, new Integer(var_2_2.intValue()+1));
				}
				theMarker.setAttribute(IMarker.SEVERITY, new Integer(2));
				theMarker.setAttribute(IMarker.SOURCE_ID, TracefileViewerPlugin.PLUGIN_ID);
				markers.add(theMarker);
				errors = Long.valueOf(errors.longValue() + 1L);
			} else
			{
				int lineNumber = 0;
				if (var_2_2 != null)
				{
					lineNumber = var_2_2;
				}
				FileUtility.addMarker(file, var_1_1, lineNumber, IMarker.SEVERITY_ERROR,TracefileViewerPlugin.PLUGIN_ID);
			}

		} catch (CoreException ce)
		{
			ce.printStackTrace();
		}
	}

	public void addWarning(String var_1_1, Integer var_2_2) throws CGException
	{
		try
		{
			IMarker theMarker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IMarker.PROBLEM);
			theMarker.setAttribute(IMarker.MESSAGE, var_1_1);
			if (var_2_2 != null)
			{
				theMarker.setAttribute(IMarker.LINE_NUMBER, new Integer(var_2_2.intValue()));
			}
			theMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			theMarker.setAttribute(IMarker.SOURCE_ID, TracefileViewerPlugin.PLUGIN_ID);
			markers.add(theMarker);
			warnings = Long.valueOf(warnings.longValue() + 1L);
		} catch (CoreException ce)
		{
			ce.printStackTrace();
		}
	}

	public Long errorCount() throws CGException
	{
		return errors;
	}

	public Long warningCount() throws CGException
	{
		return warnings;
	}

	public void dispose() throws CGException
	{
		IMarker mark = null;
		for (Iterator enum_6 = markers.iterator(); enum_6.hasNext();)
		{
			IMarker elem_2 = (IMarker) enum_6.next();
			mark = elem_2;
			try
			{
				mark.delete();
			} catch (CoreException ce)
			{
				ce.printStackTrace();
			}
		}

	}

	

}