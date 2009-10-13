package org.overture.ide.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.overture.ide.ast.dltk.DltkAstConverter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.modules.Module;

public class AstManager implements IAstManager {
	Map<IProject, Map<String, RootNode>> asts;

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
	public synchronized ModuleDeclaration addAstModuleDeclaration(
			IProject project, String nature, char[] fileName, char[] source,
			List modules) {
		
		
		

		updateAst(project, nature, modules);
		
		System.out.println("addAstModuleDeclaration : " + project.getName()
				+ "(" + nature + ") - " + new Path(new String( fileName)).toString()+" Modules: "+ getNames(modules));

		try {
			return new DltkAstConverter(fileName, source).parse(modules);
		} catch (Exception e) {
			System.out.println("DLTK AST convertion error");
			e.printStackTrace();
			return new ModuleDeclaration(0);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void updateAst(IProject project, String nature,
			List modules) {
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null) {
			RootNode root = natureAst.get(nature);
			if (root != null && root.getRootElementList() != null) {
				root.update(modules);
			} else
				natureAst.put(nature, new RootNode(modules));
		} else {
			HashMap<String, RootNode> astModules = new HashMap<String, RootNode>();
			astModules.put(nature, new RootNode(modules));
			asts.put(project, astModules);
		}
//		System.out.println("addAstModuleDeclaration : " + project.getName()
//				+ "(" + nature + ") - " + getNames(modules));
	}

	@SuppressWarnings("unchecked")
	private static String getNames(List modules)
	{
		String s = "";
		for(Object ss:modules)
		{
			if(ss instanceof ClassDefinition)
			s+=((ClassDefinition)ss).name+ ", ";
			if(ss instanceof Module)
				s+=((Module)ss).name+ ", ";
		}
		return s;
	}
	
	public Object getAstList(IProject project, String nature) {
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature))
			return natureAst.get(nature).getRootElementList();
		else
			return null;
	}

	public RootNode getRootNode(IProject project, String nature) {
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature)) {
			return natureAst.get(nature);
		}
		return null;
	}

	public void setAstAsTypeChecked(IProject project, String nature) {

	}

}
