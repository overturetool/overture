/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.utils;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;

public class ExecutableAnalysis extends DepthFirstAnalysisAdaptor
{
	private final int searchLine;

	private String module = null;

	private ExecutableAnalysis(int searchLine, String module)
	{
		this.searchLine = searchLine;
		this.module = module;
	}

	public static boolean isExecutable(INode node, int line, boolean findModule)
	{

		String nodeModule = findModule ? searchForModule(node) : null;

		ExecutableAnalysis analysis = new ExecutableAnalysis(line, nodeModule);

		try
		{
			node.apply(analysis);
		} catch (ExecutableAnalysisException e)
		{
			return e.isExecutable();

		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return false;
		}

		return false;
	}

	private static String searchForModule(INode node)
	{

		String nodeModule = null;

		SClassDefinition classDef = node.getAncestor(SClassDefinition.class);

		if (classDef != null)
		{
			nodeModule = classDef.getLocation().getModule();
		} else
		{
			AModuleModules slModule = node.getAncestor(AModuleModules.class);

			if (slModule != null)
			{
				nodeModule = slModule.getName().getName();
			}
		}

		return nodeModule;
	}

	private boolean isValidModule(PStm node)
	{

		return module == null
				|| node.getLocation().getStartLine() == searchLine
				&& module.equals(node.getLocation().getModule());
	}

	private boolean isValidModule(PExp node)
	{

		return module == null
				|| node.getLocation().getStartLine() == searchLine
				&& module.equals(node.getLocation().getModule());
	}

	public void defaultInPStm(PStm node) throws ExecutableAnalysisException
	{
		if (isValidModule(node))
		{
			throw new ExecutableAnalysisException(true);
		}
	}

	public void defaultInPExp(PExp node) throws ExecutableAnalysisException
	{
		if (isValidModule(node))
		{
			throw new ExecutableAnalysisException(true);
		}
	}
}
