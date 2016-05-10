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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.VdmModel;
import org.overture.ide.core.resources.IVdmProject;

public class VdmModelManager implements IVdmModelManager
{
	Map<IVdmProject, Map<String, IVdmModel>> asts;

	protected VdmModelManager()
	{
		asts = new HashMap<IVdmProject, Map<String, IVdmModel>>();
	}

	/**
	 * A handle to the unique Singleton instance.
	 */
	static private volatile VdmModelManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public IVdmModelManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new VdmModelManager();
		}
		return _instance;
	}

	public synchronized IVdmModel getModel(IVdmProject project)
	{
		IVdmModel model = null;
		Map<String, IVdmModel> natureAst = asts.get(project);
		String nature = project.getVdmNature();
		if (natureAst != null)
		{
			model = natureAst.get(nature);
			if (model != null)
			{
				return model;
			} else
			{
				// model = new VdmModel();
				// natureAst.put(nature, model);
			}
		} else
		{

		}

		return model;

	}

	public void clean(IVdmProject project)
	{
		// if (asts.get(project) != null)
		// asts.remove(project);
		project.getModel().clean();

	}

	public List<IProject> getProjects()
	{
		List<IProject> projects = new ArrayList<IProject>();
		for (IVdmProject vdmProject : asts.keySet())
		{
			IProject project = (IProject) vdmProject.getAdapter(IProject.class);
			if (project != null)
			{
				projects.add(project);
			}
		}

		// projects.addAll(asts.keySet());
		return projects;
	}

	public List<String> getNatures(IProject project)
	{
		List<String> natures = new ArrayList<String>();

		IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
		
		Map<String, IVdmModel> roots = asts.get(p);
		if (roots != null)
		{
			natures.addAll(roots.keySet());
		}
		return natures;
	}

	public IVdmModel createModel(IVdmProject project)
	{
		HashMap<String, IVdmModel> astModules = new HashMap<String, IVdmModel>();
		IVdmModel model = new VdmModel();
		astModules.put(project.getVdmNature(), model);
		asts.put(project, astModules);
		return model;
	}

}
