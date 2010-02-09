package org.overture.ide.plugins.latex.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.utility.VdmProject;

public class LatexProject
{
	final static String BUILDER_ID = "org.overture.ide.plugins.latex.builder";
	final static String MAIN_DOCUMENT_KET = "DOCUMENT";
	private IProject project;

	public LatexProject(IProject project) {
		this.setProject(project);
	}
	
	public void setMainDocument(String docuemnt) throws CoreException
	{
		VdmProject.addBuilder(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET, docuemnt);
	}
	
	public String getMainDocument() throws CoreException
	{
		Object tmp = VdmProject.getBuilderArgument(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET);
		if(tmp!=null)
			return tmp.toString();
		else
			return null;
	}
	
	public boolean hasDocument() throws CoreException
	{
		Object tmp = VdmProject.getBuilderArgument(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET);
		if(tmp!=null && tmp.toString().length()>0)
			return true;
		else
			return false;
	}

	public void setProject(IProject project)
	{
		this.project = project;
	}

	public IProject getProject()
	{
		return project;
	}

}
