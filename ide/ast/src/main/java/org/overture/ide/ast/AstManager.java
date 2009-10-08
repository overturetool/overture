package org.overture.ide.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.overture.ide.ast.dltk.DltkAstConverter;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.modules.ModuleList;

public class AstManager implements IAstManager
{
	Map<IProject, Map<String, RootNode>> asts;

	protected AstManager()
	{
		asts = new HashMap<IProject, Map<String, RootNode>>();
	}

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private AstManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public IAstManager instance()
	{
		if (null == _instance)
		{
			_instance = new AstManager();
		}
		return _instance;
	}

	public ModuleDeclaration addAstModuleDeclaration(IProject project,
			String nature, char[] fileName, char[] source, List modules)
	{
		// TODO Auto-generated method stub
		System.out.println("addAstModuleDeclaration ");
		System.out.println(fileName);

		updateAst(project, nature, modules);

		return new DltkAstConverter(fileName, source).parse(modules);
	}

	
	
	public void updateAst(IProject project, String nature, List modules)
	{
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null)
		{
			RootNode root = natureAst.get(nature);
			if (root != null && root.getRootElementList() != null)
			{
				List ast = root.getRootElementList();
				boolean okForInsert = true;
				for (int i = 0; i < ast.size(); i++)
				{
					for (int j = 0; j < modules.size(); j++)
					{
						if (ast instanceof ModuleList
								&& modules instanceof ModuleList)
						{
							ModuleList astModules = (ModuleList) ast;
							if (astModules.elementAt(i).name.equals(((ModuleList) modules).elementAt(j).name))
							{
								astModules.remove(i);
								astModules.add(((ModuleList) modules).elementAt(j));
								okForInsert = false;
								break;

							}
						} else if (ast instanceof ClassList
								&& modules instanceof ClassList)
						{
							ClassList astModules = (ClassList) ast;
							if (astModules.elementAt(i).name.equals(((ClassList) modules).elementAt(j).name))
							{
								astModules.remove(i);
								astModules.add(((ClassList) modules).elementAt(j));
								okForInsert = false;
								break;

							}
						}
					}
				}
				if (okForInsert)
					if (ast instanceof ModuleList)
						((ModuleList) ast).addAll((ModuleList) modules);
					else if (ast instanceof ClassList)
						((ClassList) ast).addAll((ClassList) modules);
			} else
				natureAst.put(nature, new RootNode(modules));
		} else
		{
			HashMap<String, RootNode> astModules = new HashMap<String, RootNode>();
			astModules.put(nature, new RootNode(modules));
			asts.put(project, astModules);
		}

	}

	public Object getAstList(IProject project, String nature)
	{
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature))
			return natureAst.get(nature).getRootElementList();
		else
			return null;
	}
	
	public RootNode getRootNode(IProject project, String nature){
		Map<String, RootNode> natureAst = asts.get(project);
		if (natureAst != null && natureAst.containsKey(nature)){
			return natureAst.get(nature);
		}
		return null;
	}

	public void setAstAsTypeChecked(IProject project, String nature)
	{
		
	}

}
