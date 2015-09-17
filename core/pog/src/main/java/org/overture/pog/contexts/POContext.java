/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.pog.contexts;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContext;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.UniqueNameGenerator;
import org.overture.pog.visitors.PatternToExpVisitor;

abstract public class POContext implements IPOContext
{
	abstract public String getContext();

	// abstract public List<INode> getContextNodes(int n);
	abstract public PExp getContextNode(PExp stitch);

	private Map<PExp, PType> knownTypes = new HashMap<PExp, PType>();

	@Override
	/**
	 * Any contexts that can be preserved regardless of state need not override this
	 */
	public boolean isStateful()
	{
		return false;
	}

	/**
	 * Any context that is not stateful need no override this method as it is not used.
	 */
	@Override
	public void lastStmt()
	{
		// do nothing. Only relevant for statefuls
	}

	public String getName()
	{
		return ""; // Overridden in PONameContext
	}

	public boolean isScopeBoundary()
	{
		return false;
	}

	public void noteType(PExp exp, PType type)
	{
		knownTypes.put(exp, type);
	}

	public PType checkType(PExp exp)
	{
		return knownTypes.get(exp);
	}

	/**
	 * Create an expression equivalent to a pattern.
	 */
	protected PExp patternToExp(PPattern pattern, IPogAssistantFactory af,
			UniqueNameGenerator unq)
	{
		PatternToExpVisitor visitor = new PatternToExpVisitor(unq, af);
		try
		{
			return pattern.apply(visitor);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
