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
	public  void refreshProjects();
	
	
	public IVdmModel createModel(IVdmProject project);
}
