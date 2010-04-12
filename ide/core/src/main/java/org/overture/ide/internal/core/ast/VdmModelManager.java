package org.overture.ide.internal.core.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.core.IVdmModel;

import org.overture.ide.core.ast.VdmModel;
import org.overture.ide.core.resources.IVdmProject;

public class VdmModelManager implements IVdmModelManager {
	
	
	
	
	Map<IVdmProject, Map<String, IVdmModel>> asts;

	protected VdmModelManager() {
		asts = new HashMap<IVdmProject, Map<String, IVdmModel>>();
	}

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private VdmModelManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public IVdmModelManager getInstance() {
		if (null == _instance) {
			_instance = new VdmModelManager();
		}
		return _instance;
	}

//	private static String getNames(List<IAstNode> modules) {
//		String s = "";
//		for (Object ss : modules) {
//			if (ss instanceof ClassDefinition)
//				s += ((ClassDefinition) ss).name + ", ";
//			if (ss instanceof Module)
//				s += ((Module) ss).name + ", ";
//		}
//		return s;
//	}



	public synchronized IVdmModel getModel(IVdmProject project) {
		IVdmModel model = null;
		Map<String, IVdmModel> natureAst = asts.get(project);
		String nature = project.getVdmNature();
		if (natureAst != null) {
			model = natureAst.get(nature );
			if (model != null ) {
				return model;
			} else
			{
//				model = new VdmModel();
//				natureAst.put(nature, model);
			}
		} else {
			
		}
		
		return model;
		
	}

	

	public void clean(IVdmProject project) {
//		if (asts.get(project) != null)
//			asts.remove(project);
		project.getModel().clean();

	}

	public List<IProject> getProjects() {
		List<IProject> projects = new Vector<IProject>();
		projects.addAll(asts.keySet());
		return projects;
	}

	public List<String> getNatures(IProject project) {
		List<String> natures = new Vector<String>();

		Map<String, IVdmModel> roots = asts.get(project);
		if (roots != null) {
			natures.addAll(roots.keySet());
		}
		return natures;
	}
	
	
	
	public  void refreshProjects()
	{
		Job refreshJob = new Job("AST Refresh"){

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				final String VDM_PP_NATURE = "org.overture.ide.vdmpp.core.nature";
				final String VDM_SL_NATURE = "org.overture.ide.vdmsl.core.nature";
				final String VDM_RT_NATURE = "org.overture.ide.vdmrt.core.nature";
				for (IProject project : ResourcesPlugin.getWorkspace()
						.getRoot()
						.getProjects())
				{
					try
					{
						if (project.isAccessible()&& project.isOpen()&&( project.hasNature(VDM_SL_NATURE)
								|| project.hasNature(VDM_PP_NATURE)
								|| project.hasNature(VDM_RT_NATURE)))
						{
							if(!getProjects().contains(project) )
							project.build(IncrementalProjectBuilder.FULL_BUILD,null);
							
						}
					} catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
				return new Status(IStatus.OK,"org.overture.ide.ast","AST Refresh completed");
			}
			
		};
		refreshJob.schedule();
		
	}

	public IVdmModel createModel(IVdmProject project)
	{
		HashMap<String, IVdmModel> astModules = new HashMap<String, IVdmModel>();
	IVdmModel	model = new VdmModel();
		astModules.put(project.getVdmNature(), model);
		asts.put(project, astModules);
		return model;
	}

	





}
