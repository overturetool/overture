package org.overture.core.tests.example;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;

public class IdTestResult extends Vector<String> implements Serializable,
		List<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IdTestResult convert(List<INode> ast)
	{
		IdTestResult r = new IdTestResult();
		for (INode n : ast)
		{
			if (n instanceof AModuleModules) // ModuleModules prints the file path so we skip it
			{
				for (PDefinition p : ((AModuleModules) n).getDefs())
				{
					r.add(p.toString());
				}

			} else
			{
				r.add(n.toString());
			}
		}
		return r;
	}
}
