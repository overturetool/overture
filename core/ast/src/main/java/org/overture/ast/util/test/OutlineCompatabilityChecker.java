/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util.test;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PType;

public class OutlineCompatabilityChecker extends
		DepthFirstAnalysisAdaptorAnswer<Boolean>
{
	List<INode> invalidNodes = new Vector<INode>();

	public String getInvalidNodesDescription()
	{
		String tmp = "";
		for (Iterator<INode> iterator = invalidNodes.iterator(); iterator.hasNext();)
		{
			tmp += iterator.next().getClass().getName();
			if (iterator.hasNext())
			{
				tmp += ", ";
			}

		}
		return tmp;
	}

	public static boolean isNotNull(Object o)
	{
		return o != null;
	}

	public boolean isValidName(ILexIdentifierToken name)
	{
		return isNotNull(name) && isNotNull(name.getName());
	}

	public static boolean isValidModule(ILexLocation location)
	{
		return isNotNull(location) && isNotNull(location.getModule());
	}

	@SuppressWarnings("rawtypes")
	public boolean check(Object o)
	{
		if (o instanceof List)
		{
			return check((List) o);
		} else if (o instanceof INode)
		{
			return check((INode) o);
		}
		return true;
	}

	public boolean check(@SuppressWarnings("rawtypes") List l)
	{
		boolean r = true;
		for (Object e : l)
		{
			if (e instanceof INode)
			{
				r = mergeReturns(r, check((INode) e));
			}
		}

		return r;
	}

	public boolean check(INode n)
	{
		try
		{
			return n.apply(this);
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	@Override
	public Boolean caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		if (!(node.getPattern() != null
				&& node.getPattern() instanceof AIdentifierPattern && isValidName(((AIdentifierPattern) node.getPattern()).getName())))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean defaultInPDefinition(PDefinition node)
			throws AnalysisException
	{
		if (!isValidName(node.getName()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		if (!isValidName(node.getName()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean caseAModuleModules(AModuleModules node)
			throws AnalysisException
	{
		if (!isValidName(node.getName()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean defaultInPStm(PStm node) throws AnalysisException
	{
		if (!isValidModule(node.getLocation()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean defaultInPExp(PExp node) throws AnalysisException
	{
		if (!isValidModule(node.getLocation()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean defaultInPType(PType node) throws AnalysisException
	{
		if (!isValidModule(node.getLocation()))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean caseAFieldField(AFieldField node) throws AnalysisException
	{
		if (!(isNotNull(node.getTagname()) && isNotNull(node.getTagname().getName())))
		{
			invalidNodes.add(node);
			return false;
		}
		return true;
	}

	@Override
	public Boolean mergeReturns(Boolean original, Boolean new_)
	{
		return original ? new_ : original;
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		return true;
	}

}
