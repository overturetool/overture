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
//package org.overture.ide.plugins.latex.utility;
//
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.runtime.CoreException;
//import org.overture.ide.core.resources.VdmProject;
//
//
//public class LatexProject
//{
//	final static String BUILDER_ID = "org.overture.ide.plugins.latex.builder";
//	final static String MAIN_DOCUMENT_KET = "DOCUMENT";
//	private IProject project;
//
//	public LatexProject(IProject project) {
//		this.setProject(project);
//	}
//	
//	public void setMainDocument(String docuemnt) throws CoreException
//	{
//		VdmProject.addBuilder(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET, docuemnt);
//	}
//	
//	public String getMainDocument() throws CoreException
//	{
//		Object tmp = VdmProject.getBuilderArgument(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET);
//		if(tmp!=null)
//			return tmp.toString();
//		else
//			return null;
//	}
//	
//	public boolean hasDocument() throws CoreException
//	{
//		Object tmp = VdmProject.getBuilderArgument(getProject(), BUILDER_ID, MAIN_DOCUMENT_KET);
//		if(tmp!=null && tmp.toString().length()>0)
//			return true;
//		else
//			return false;
//	}
//
//	public void setProject(IProject project)
//	{
//		this.project = project;
//	}
//
//	public IProject getProject()
//	{
//		return project;
//	}
//
//}
