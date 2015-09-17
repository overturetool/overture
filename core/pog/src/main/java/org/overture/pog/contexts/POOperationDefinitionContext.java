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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPogAssistantFactory;

public class POOperationDefinitionContext extends POContext
{
	public final ILexNameToken name;
	public final AOperationType deftype;
	public final List<PPattern> paramPatternList;
	public final boolean addPrecond;
	public final PExp precondition;
	public final PDefinition stateDefinition;
	final PDefinition opDef;

	protected POOperationDefinitionContext(ILexNameToken name,
			AOperationType deftype, LinkedList<PPattern> paramPatternList,
			boolean addPrecond, PExp precondition, PDefinition stateDefinition,
			PDefinition opDef)
	{
		super();
		this.name = name;
		this.deftype = deftype;
		this.paramPatternList = paramPatternList;
		this.addPrecond = addPrecond;
		this.precondition = precondition;
		this.stateDefinition = stateDefinition;
		this.opDef = opDef;
	}

	public POOperationDefinitionContext(
			AExplicitOperationDefinition definition, boolean precond,
			PDefinition stateDefinition)
	{
		this.name = definition.getName();
		this.deftype = (AOperationType) definition.getType();
		this.addPrecond = precond;
		this.paramPatternList = cloneList(definition.getParameterPatterns());
		this.precondition = definition.getPrecondition();
		this.stateDefinition = stateDefinition;
		this.opDef = definition;

	}

	private List<PPattern> cloneList(LinkedList<PPattern> parameterPatterns)
	{
		List<PPattern> r = new LinkedList<PPattern>();

		for (PPattern p : parameterPatterns)
		{
			r.add(p.clone());
		}
		return r;
	}

	public POOperationDefinitionContext(
			AImplicitOperationDefinition definition, boolean precond,
			PDefinition stateDefinition, IPogAssistantFactory assistantFactory)
	{
		this.name = definition.getName();
		this.deftype = (AOperationType) definition.getType();
		this.addPrecond = precond;
		this.paramPatternList = assistantFactory.createAImplicitOperationDefinitionAssistant().getParamPatternList(definition);
		this.precondition = definition.getPrecondition();
		this.stateDefinition = stateDefinition;
		this.opDef = definition;
	}

	protected boolean anyBinds()
	{
		return !deftype.getParameters().isEmpty();
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		if (anyBinds())
		{
			AForAllExp forAllExp = new AForAllExp();
			forAllExp.setType(new ABooleanBasicType());
			forAllExp.setBindList(makeBinds());

			if (addPrecond && precondition != null)
			{
				AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(precondition.clone(), stitch);
				forAllExp.setPredicate(impliesExp);
			} else
			{
				forAllExp.setPredicate(stitch);
			}

			return forAllExp;

		} else
		{
			if (addPrecond && precondition != null)
			{
				AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(precondition.clone(), stitch);
				return impliesExp;
			}
		}
		return stitch;

	}

	private static final ILexNameToken OLD_STATE_ARG = new LexNameToken("", "oldstate", null);
	private static final ILexNameToken OLD_SELF_ARG = new LexNameToken("", "oldself", null);

	private void addParameterBinds(LinkedList<PMultipleBind> r)
	{
		Iterator<PType> types = deftype.getParameters().iterator();
		for (PPattern p : paramPatternList)
		{
			ATypeMultipleBind tmBind = new ATypeMultipleBind();
			List<PPattern> pats = new LinkedList<PPattern>();

			pats.add(p.clone());
			tmBind.setType(types.next().clone());
			tmBind.setPlist(pats);
			r.add(tmBind);
		}
	}

	protected void addStateBinds(LinkedList<PMultipleBind> r)
	{
		if (stateDefinition != null)
		{
			ATypeMultipleBind tmBind2 = new ATypeMultipleBind();
			AIdentifierPattern pattern = new AIdentifierPattern();

			if (stateDefinition instanceof AStateDefinition)
			{
				AStateDefinition def = (AStateDefinition) stateDefinition;

				tmBind2.setType(def.getRecordType().clone());
				pattern.setName(OLD_STATE_ARG.clone());
			} else
			{
				SClassDefinition def = (SClassDefinition) stateDefinition;
				tmBind2.setType(def.getClasstype().clone());
				pattern.setName(OLD_SELF_ARG.clone());
			}

			List<PPattern> plist = new LinkedList<PPattern>();
			plist.add(pattern);
			tmBind2.setPlist(plist);
			r.add(tmBind2);
		}
	}

	private List<? extends PMultipleBind> makeBinds()
	{
		LinkedList<PMultipleBind> r = new LinkedList<PMultipleBind>();

		addParameterBinds(r);

		addStateBinds(r);

		return r;

	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!deftype.getParameters().isEmpty())
		{
			sb.append("forall ");
			String sep = "";
			Iterator<PType> types = deftype.getParameters().iterator();

			for (PPattern p : paramPatternList)
			{
				if (!(p instanceof AIgnorePattern))
				{
					sb.append(sep);
					sb.append(p.toString());
					sb.append(":");
					sb.append(types.next());
					sep = ", ";
				}
			}

			if (stateDefinition != null)
			{
				appendStatePatterns(sb);
			}

			sb.append(" &");

			if (addPrecond && precondition != null)
			{
				sb.append(" ");
				sb.append(precondition);
				sb.append(" =>");
			}
		}

		return sb.toString();
	}

	private void appendStatePatterns(StringBuilder sb)
	{
		if (stateDefinition == null)
		{
			return;
		} else if (stateDefinition instanceof AStateDefinition)
		{
			AStateDefinition def = (AStateDefinition) stateDefinition;
			sb.append(", oldstate:");
			sb.append(def.getName().getName());
		} else
		{
			SClassDefinition def = (SClassDefinition) stateDefinition;
			sb.append(", oldself:");
			sb.append(def.getName().getName());
		}
	}

}
