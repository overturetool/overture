/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.assistant.type;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//FIXME: all methods used in same class. move them.
public class AApplyObjectDesignatorAssistantTC implements IAstAssistant
{

	protected ITypeCheckerAssistantFactory af;

	public AApplyObjectDesignatorAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PType mapApply(AApplyObjectDesignator node, SMapType map,
			Environment env, NameScope scope, boolean unique,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{

		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(unique, 3250, "Map application must have one argument", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		PType argtype = node.getArgs().get(0).apply(rootVisitor, new TypeCheckInfo(af, env, scope));

		if (!af.getTypeComparator().compatible(map.getFrom(), argtype))
		{
			TypeCheckerErrors.concern(unique, 3251, "Map application argument is incompatible type", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Map domain", map.getFrom(), "Argument", argtype);
		}

		return map.getTo();
	}

	public PType seqApply(AApplyObjectDesignator node, SSeqType seq,
			Environment env, NameScope scope, boolean unique,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{

		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(unique, 3252, "Sequence application must have one argument", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		PType argtype = node.getArgs().get(0).apply(rootVisitor, new TypeCheckInfo(af, env, scope));

		if (!env.af.createPTypeAssistant().isNumeric(argtype))
		{
			TypeCheckerErrors.concern(unique, 3253, "Sequence argument is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail(unique, "Type", argtype);
		}

		return seq.getSeqof();
	}

	public PType functionApply(AApplyObjectDesignator node,
			AFunctionType ftype, Environment env, NameScope scope,
			boolean unique, IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{

		LinkedList<PType> ptypes = ftype.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3254, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return ftype.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3255, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return ftype.getResult();
		}

		int i = 0;

		for (PExp a : node.getArgs())
		{
			PType at = a.apply(rootVisitor, new TypeCheckInfo(af, env, scope));
			PType pt = ptypes.get(i++);

			if (!af.getTypeComparator().compatible(pt, at))
			{

				// TypeCheckerErrors.concern(unique, 3256, "Inappropriate type for argument " + i
				// +". (Expected: "+pt+" Actual: "+at+")" ,node.getLocation(),node);
				TypeCheckerErrors.concern(unique, 3256, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return ftype.getResult();
	}

	public PType operationApply(AApplyObjectDesignator node,
			AOperationType optype, Environment env, NameScope scope,
			boolean unique, IQuestionAnswer<TypeCheckInfo, PType> rootVisitor)
			throws AnalysisException
	{
		LinkedList<PType> ptypes = optype.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3257, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return optype.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(unique, 3258, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(unique, "Args", node.getArgs(), "Params", ptypes);
			return optype.getResult();
		}

		int i = 0;

		for (PExp a : node.getArgs())
		{
			PType at = a.apply(rootVisitor, new TypeCheckInfo(af, env, scope));
			PType pt = ptypes.get(i++);

			if (!af.getTypeComparator().compatible(pt, at))
			{ // + ". (Expected: "+pt+" Actual: "+at+")"
				TypeCheckerErrors.concern(unique, 3259, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return optype.getResult();
	}

}
