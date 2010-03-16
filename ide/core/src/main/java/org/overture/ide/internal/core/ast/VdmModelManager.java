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
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.ast.VdmModel;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.modules.Module;

public class VdmModelManager implements IVdmModelManager {
	
	
	
	
	@SuppressWarnings("unchecked")
	Map<IVdmProject, Map<String, IVdmModel>> asts;

	@SuppressWarnings("unchecked")
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

//	@SuppressWarnings("unchecked")
//	public synchronized void addAstModuleDeclaration(
//			IVdmProject project, String nature, char[] fileName, char[] source,
//			List modules) {
//
//		updateAst(project, nature, modules);
//		System.out.println("AST update : " + project.getName() + "("
//				+ nature + ") - "
//				+ new Path(new String(fileName)).lastSegment().toString()
//				+ " Modules: " + getNames(modules));
//
//		try {
//		//	return new DltkAstConverter(source).parse(modules);
//		} catch (Exception e) {
//			System.out.println("DLTK AST convertion error");
//			e.printStackTrace();
//		//	return new ModuleDeclaration(0);
//		}
//	}

	@SuppressWarnings("unchecked")
	public synchronized void update(IVdmProject project, List modules) {
		Map<String, IVdmModel> natureAst = asts.get(project);
		String nature = project.getVdmNature();
		if (natureAst != null) {
			IVdmModel root = natureAst.get(nature );
			if (root != null && root.getRootElementList() != null) {
				root.update(modules);
			} else
				natureAst.put(nature, new VdmModel(modules));
		} else {
			HashMap<String, IVdmModel> astModules = new HashMap<String, IVdmModel>();
			astModules.put(nature, new VdmModel(modules));
			asts.put(project, astModules);
		}
		
		if(VdmCore.DEBUG)
		{
			String names = "";
			for (Object object : modules)
			{
				if(object instanceof ClassDefinition)
					names+=" "+ ((ClassDefinition)object).name.name;
				if(object instanceof Module)
					names+=" "+ ((Module)object).name.name;
			}
			System.out.println("AstManager.update: "+ project.getName()+"->"+ names);
		}
		// System.out.println("addAstModuleDeclaration : " + project.getName()
		// + "(" + nature + ") - " + getNames(modules));
	}

	@SuppressWarnings("unchecked")
	private static String getNames(List modules) {
		String s = "";
		for (Object ss : modules) {
			if (ss instanceof ClassDefinition)
				s += ((ClassDefinition) ss).name + ", ";
			if (ss instanceof Module)
				s += ((Module) ss).name + ", ";
		}
		return s;
	}

	// public Object getAstList(IProject project, String nature) {
	// Map<String, RootNode> natureAst = asts.get(project);
	// if (natureAst != null && natureAst.containsKey(nature))
	// return natureAst.get(nature).getRootElementList();
	// else
	// return null;
	// }

	@SuppressWarnings("unchecked")
	public IVdmModel getRootNode(IVdmProject project, String nature) {
		Map<String, IVdmModel> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature)) {
			return natureAst.get(nature);
		}else
		{
			update(project, new Vector());
			return getRootNode(project);
		}
		
	}

	

	public void clean(IVdmProject project) {
		if (asts.get(project) != null)
			asts.remove(project);

	}

	public List<IProject> getProjects() {
		List<IProject> projects = new Vector<IProject>();
		projects.addAll(asts.keySet());
		return projects;
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public IVdmModel getRootNode(IVdmProject project)
	{
		return getRootNode(project, project.getVdmNature());
	}





}
