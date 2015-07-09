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

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;

public class POFunctionResultContext extends POContext
{
	public final ILexNameToken name;
	public final AFunctionType deftype;
	public final PExp precondition;
	public final PExp body;
	public final APatternTypePair result;
	public final boolean implicit;
	final PDefinition function;

	public POFunctionResultContext(AExplicitFunctionDefinition definition)
	{
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.precondition = definition.getPrecondition();
		this.body = definition.getBody();
		this.implicit = false;
		this.result = AstFactory.newAPatternTypePair(AstFactory.newAIdentifierPattern(new LexNameToken(definition.getName().getModule(), "RESULT", definition.getLocation())), ((AFunctionType) definition.getType()).getResult().clone());
		this.function = definition.clone();
		function.setLocation(null);
	}

	public POFunctionResultContext(AImplicitFunctionDefinition definition)
	{
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.precondition = definition.getPrecondition();
		this.body = definition.getBody();
		this.implicit = true;
		this.result = definition.getResult();
		this.function = definition;

	}

	@Override
	public PExp getContextNode(PExp stitch)
	{

		PExp stitched = getContextNodeMain(stitch);

		if (precondition == null)
		{
			return stitched;
		} else
		{
			AImpliesBooleanBinaryExp imp = AstExpressionFactory.newAImpliesBooleanBinaryExp(precondition.clone(), stitched);
			return imp;
		}

	}

	private PExp getContextNodeMain(PExp stitch)
	{
		if (implicit)
		{
			AExistsExp exists_exp = new AExistsExp();
			exists_exp.setType(new ABooleanBasicType());
			List<PMultipleBind> binds = new LinkedList<PMultipleBind>();
			ATypeMultipleBind tmBind = new ATypeMultipleBind();
			List<PPattern> patternList = new LinkedList<PPattern>();
			patternList.add(result.getPattern().clone());
			tmBind.setPlist(patternList);
			tmBind.setType(result.getType().clone());
			binds.add(tmBind);
			exists_exp.setBindList(binds);
			exists_exp.setPredicate(stitch);
			return exists_exp;
		}

		else
		{
			ALetDefExp letDefExp = new ALetDefExp();
			AEqualsDefinition localDef = new AEqualsDefinition();
			localDef.setPattern(result.getPattern().clone());
			localDef.setType(result.getType().clone());
			localDef.setTest(body.clone());
			List<PDefinition> defs = new LinkedList<PDefinition>();
			defs.add(localDef);
			letDefExp.setLocalDefs(defs);
			letDefExp.setExpression(stitch);
			return letDefExp;
		}

	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (precondition != null)
		{
			sb.append(precondition);
			sb.append(" => ");
		}

		if (implicit)
		{
			sb.append("forall ");
			sb.append(result);
			sb.append(" & ");
		} else
		{
			sb.append("let ");
			sb.append(result);
			sb.append(" = ");
			sb.append(body);
			sb.append(" in ");
		}

		return sb.toString();
	}
}
