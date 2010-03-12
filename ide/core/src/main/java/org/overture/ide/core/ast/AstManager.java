package org.overture.ide.core.ast;

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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.core.utility.IVdmProject;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.modules.Module;

public class AstManager implements IAstManager {
	@SuppressWarnings("unchecked")
	Map<IProject, Map<String, RootNode>> asts;

	@SuppressWarnings("unchecked")
	protected AstManager() {
		asts = new HashMap<IProject, Map<String, RootNode>>();
	}

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private AstManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public IAstManager instance() {
		if (null == _instance) {
			_instance = new AstManager();
		}
		return _instance;
	}

	@SuppressWarnings("unchecked")
	public synchronized void addAstModuleDeclaration(
			IProject project, String nature, char[] fileName, char[] source,
			List modules) {

		updateAst(project, nature, modules);
		System.out.println("AST update : " + project.getName() + "("
				+ nature + ") - "
				+ new Path(new String(fileName)).lastSegment().toString()
				+ " Modules: " + getNames(modules));

		try {
		//	return new DltkAstConverter(source).parse(modules);
		} catch (Exception e) {
			System.out.println("DLTK AST convertion error");
			e.printStackTrace();
		//	return new ModuleDeclaration(0);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void updateAst(IProject project, String nature, List modules) {
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null) {
			IVdmElement root = natureAst.get(nature);
			if (root != null && root.getRootElementList() != null) {
				root.update(modules);
			} else
				natureAst.put(nature, new RootNode(modules));
		} else {
			HashMap<String, RootNode> astModules = new HashMap<String, RootNode>();
			astModules.put(nature, new RootNode(modules));
			asts.put(project, astModules);
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
	public IVdmElement getRootNode(IProject project, String nature) {
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature)) {
			return natureAst.get(nature);
		}
		return null;
	}

	public void setAstAsTypeChecked(IProject project, String nature) {

	}

	public void clean(IProject project) {
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

		Map<String, RootNode> roots = asts.get(project);
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
	public IVdmElement getRootNode(IVdmProject project)
	{
		return getRootNode(project, project.getVdmNature());
	}

}
