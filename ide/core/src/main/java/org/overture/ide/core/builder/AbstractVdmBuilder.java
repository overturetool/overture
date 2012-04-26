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
package org.overture.ide.core.builder;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;

import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.utility.FileUtility;
import org.overturetool.vdmjV2.lex.LexLocation;

public abstract class AbstractVdmBuilder
{
	private IVdmProject project;
	
	public IStatus buildModel(IVdmProject project,IVdmModel rooList)
	{
		this.setProject(project);
		return buildModel(rooList);
	}
	
	public abstract IStatus buildModel(IVdmModel rooList);

//	public abstract String getNatureId();
	
	

	protected void addWarningMarker(File file, String message, LexLocation location,
			String sourceId)
	{
		FileUtility.addMarker(project.findIFile( file),message,location,IMarker.SEVERITY_WARNING,sourceId);
	}
	
	protected void addErrorMarker(File file, String message, LexLocation location,
			String sourceId)
	{
		FileUtility.addMarker(project.findIFile(file), message, location, IMarker.SEVERITY_ERROR,sourceId);
	}

	protected void addWarningMarker(IFile file, String message, int lineNumber)
	{
		FileUtility.addMarker(file,message,lineNumber,IMarker.SEVERITY_WARNING,ICoreConstants.PLUGIN_ID);
	}

	protected void addErrorMarker(IFile file, String message, int lineNumber)
	{
		FileUtility.addMarker(file, message, lineNumber, IMarker.SEVERITY_ERROR,ICoreConstants.PLUGIN_ID);
	}

	private void setProject(IVdmProject project)
	{
		this.project = project;
	}

	protected IVdmProject getProject()
	{
		return project;
	}
	
	



}
