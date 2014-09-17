/*
 * #%~
 * org.overture.ide.core
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.internal.core.ast;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;



public interface IVdmModelManager
{
	



	/**
	 * Get the RootNode from a project and the corresponding nature
	 * @param project The project to select
	 * @param nature The nature if filter the AST
	 * @return The rootnode for the current project + nature
	 */
	IVdmModel getModel(IVdmProject project);
	
	
	

	
	
	/**
	 * Removed all AST info from the current project 
	 * @param project the project which should be cleaned
	 */
	void clean(IVdmProject project);
	
	/**
	 * Returns all known projects of the Manager
	 * @return a list of known projects which might have zero or more natures mapped
	 */
	public List<IProject> getProjects();
	
	/**
	 * Returns all known natures of the manager from the selected project
	 * @param project the project to look up
	 * @return a list of known natures
	 */
	public List<String> getNatures(IProject project);
	
	
	/**
	 * Refreshed the AST for all VDM projects
	 */
//	public  void refreshProjects();
	
	
	public IVdmModel createModel(IVdmProject project);
}
