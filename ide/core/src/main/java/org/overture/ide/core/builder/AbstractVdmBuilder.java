package org.overture.ide.core.builder;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.utility.FileUtility;
import org.overturetool.vdmj.lex.LexLocation;

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
		FileUtility.addMarker(file,message,lineNumber,IMarker.SEVERITY_WARNING);
	}

	protected void addErrorMarker(IFile file, String message, int lineNumber)
	{
		FileUtility.addMarker(file, message, lineNumber, IMarker.SEVERITY_ERROR);
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
