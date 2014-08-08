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

import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContext;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.utility.UniqueNameGenerator;

@SuppressWarnings("serial")
public class POContextStack extends Stack<IPOContext> implements
		IPOContextStack
{
	private UniqueNameGenerator gen;

	@Override
	public void setGenerator(UniqueNameGenerator gen)
	{
		this.gen = gen;
	}

	@Override
	public UniqueNameGenerator getGenerator()
	{
		return gen;
	}

	/**
	 * Pop a non-stateful context from the Stack. Stateful contexts can be removed with {@link #clearStateContexts()}
	 */
	@Override
	public synchronized IPOContext pop()
	{

		IPOContext obj = peek();
		int len = size();
		for (int i = len - 1; i >= 0; i--)
		{
			if (!this.get(i).isStateful())
			{
				removeElementAt(i);
				return obj;
			}
		}

		return obj;
	}

	public PExp getPredWithContext(PExp initialPredicate)
	{
		return getContextNode(initialPredicate);
	}

	private PExp getContextNode(PExp stitchPoint)
	{

		for (int i = this.size() - 1; i >= 0; i--)
		{
			IPOContext ctxt = this.get(i);
			if (!(ctxt instanceof PONameContext))
			{
				stitchPoint = ctxt.getContextNode(stitchPoint);
			}
		}
		return stitchPoint;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.pog.IPOContextStack#getName()
	 */
	@Override
	public String getName()
	{
		StringBuilder result = new StringBuilder();
		String prefix = "";

		for (IPOContext ctxt : this)
		{
			String name = ctxt.getName();

			if (name.length() > 0)
			{
				result.append(prefix);
				result.append(name);
				prefix = ", ";
			}
		}

		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.pog.IPOContextStack#getObligation(java.lang.String)
	 */
	@Override
	public String getObligation(String root)
	{
		StringBuilder result = new StringBuilder();
		String spacing = "  ";
		String indent = "";
		StringBuilder tail = new StringBuilder();

		for (IPOContext ctxt : this)
		{
			String po = ctxt.getContext();

			if (po.length() > 0)
			{
				result.append(indent);
				result.append("(");
				result.append(indentNewLines(po, indent));
				result.append("\n");
				indent = indent + spacing;
				tail.append(")");
			}
		}

		result.append(indent);
		result.append(indentNewLines(root, indent));
		result.append(tail);
		result.append("\n");

		return result.toString();
	}

	private String indentNewLines(String line, String indent)
	{
		StringBuilder sb = new StringBuilder();
		String[] parts = line.split("\n");
		String prefix = "";

		for (int i = 0; i < parts.length; i++)
		{
			sb.append(prefix);
			sb.append(parts[i]);
			prefix = "\n" + indent;
		}

		return sb.toString();
	}

	public void noteType(PExp exp, PType PType)
	{
		this.peek().noteType(exp, PType);
	}

	public PType checkType(PExp exp, PType expected)
	{
		ListIterator<IPOContext> p = this.listIterator(size());

		while (p.hasPrevious())
		{
			IPOContext c = p.previous();

			if (c.isScopeBoundary())
			{
				break; // Change of name scope for expressions.
			}

			PType t = c.checkType(exp);

			if (t != null)
			{
				return t;
			}
		}

		return expected;
	}

	@Override
	public void clearStateContexts()
	{
		int len = size();

		for (int i = len - 1; i > 0; i--)
		{
			if (this.get(i).isStateful())
			{
				removeElementAt(i);
			}
		}

	}

	@Override
	public Map<ILexNameToken, AVariableExp> getLast_Vars()
	{
		ListIterator<IPOContext> p = this.listIterator(size());

		while (p.hasPrevious())
		{
			IPOContext c = p.previous();
			if (c instanceof StatefulContext)
			{
				return ((StatefulContext) c).getLast_vars();
			}

		}
		return null;
	}

}
