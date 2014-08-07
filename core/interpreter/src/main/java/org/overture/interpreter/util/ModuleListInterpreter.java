package org.overture.interpreter.util;

import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.modules.ModuleList;

public class ModuleListInterpreter extends ModuleList
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4806203437270855897L;

	public ModuleListInterpreter(ModuleList modules)
	{
		super();
		addAll(modules);
	}

	public ModuleListInterpreter()
	{
		super();
	}

	public void setLoaded()
	{
		for (AModuleModules m : this)
		{
			m.setTypeChecked(true);
		}
	}

	public int notLoaded()
	{
		int count = 0;

		for (AModuleModules m : this)
		{
			if (!m.getTypeChecked())
			{
				count++;
			}
		}

		return count;
	}

}
