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

package org.overture.pog.obligation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;

public class POFunctionDefinitionContext extends POContext
{
	public final ILexNameToken name;
	public final AFunctionType deftype;
	public final List<List<PPattern>> paramPatternList;
	public final boolean addPrecond;
	public final PExp precondition;

	public POFunctionDefinitionContext(AExplicitFunctionDefinition definition,
			boolean precond)
	{
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.paramPatternList = definition.getParamPatternList();
		this.addPrecond = precond;
		this.precondition = definition.getPrecondition();
	}

	public POFunctionDefinitionContext(AImplicitFunctionDefinition definition,
			boolean precond)
	{
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.addPrecond = precond;
		this.paramPatternList = AImplicitFunctionDefinitionAssistantTC.getParamPatternList(definition);
		this.precondition = definition.getPrecondition();
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		AForAllExp forAllExp = new AForAllExp();
		forAllExp.setBindList(makeBinds());
		
		if (deftype.getParameters().isEmpty()){
			return stitch;
		}
		
		if (addPrecond && precondition != null)
		{
			
			AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(precondition.clone(), stitch);
			
			forAllExp.setPredicate(implies);
		}
		else
		{
			forAllExp.setPredicate(stitch);
		}

		return forAllExp;
	}

	private List<PMultipleBind> makeBinds()
	{
		List<PMultipleBind> result = new LinkedList<PMultipleBind>();
		AFunctionType ftype = deftype;

		for (List<PPattern> params: paramPatternList)
		{
			Iterator<PType> types = ftype.getParameters().iterator();
		
			
			for (PPattern param: params)
			{
				ATypeMultipleBind typeBind = new ATypeMultipleBind();
				List<PPattern> one = new Vector<PPattern>();
				one.add(param.clone());
				typeBind.setPlist(one);
				typeBind.setType(types.next().clone());
				result.add(typeBind);
			}
		}
	
		return result;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!deftype.getParameters().isEmpty())
		{
			sb.append("forall ");
			String sep = "";
			AFunctionType ftype = deftype;

			for (List<PPattern> pl : paramPatternList)
			{
				Iterator<PType> types = ftype.getParameters().iterator();

				for (PPattern p : pl)
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

				if (ftype.getResult() instanceof AFunctionType)
				{
					ftype = (AFunctionType) ftype.getResult();
				} else
				{
					break;
				}
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
}
