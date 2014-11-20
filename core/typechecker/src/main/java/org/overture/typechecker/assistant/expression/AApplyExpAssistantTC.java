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
package org.overture.typechecker.assistant.expression;

import java.util.List;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.Utils;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//FIXME only used in 1 class. move it
public class AApplyExpAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AApplyExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PType functionApply(AApplyExp node, boolean isSimple,
			AFunctionType ft)
	{
		List<PType> ptypes = ft.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3059, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3060, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		}

		int i = 0;

		for (PType at : node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!af.getTypeComparator().compatible(pt, at))
			{
				// TypeCheckerErrors.concern(isSimple, 3061, "Inappropriate type for argument " + i +
				// ". (Expected: "+pt+" Actual: "+at+")",node.getLocation(),node);
				TypeCheckerErrors.concern(isSimple, 3061, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ft.getResult();
	}

	public PType operationApply(AApplyExp node, boolean isSimple,
			AOperationType ot)
	{
		List<PType> ptypes = ot.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3062, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3063, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		}

		int i = 0;

		for (PType at : node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!af.getTypeComparator().compatible(pt, at))
			{
				// TypeCheckerErrors.concern(isSimple, 3064, "Inappropriate type for argument " + i
				// +". (Expected: "+pt+" Actual: "+at+")",node.getLocation(),node);
				TypeCheckerErrors.concern(isSimple, 3064, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ot.getResult();
	}

	public PType sequenceApply(AApplyExp node, boolean isSimple, SSeqType seq)
	{
		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3055, "Sequence selector must have one argument", node.getLocation(), node);
		} else if (!af.createPTypeAssistant().isNumeric(node.getArgtypes().get(0)))
		{
			TypeCheckerErrors.concern(isSimple, 3056, "Sequence application argument must be numeric", node.getLocation(), node);
		} else if (seq.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3268, "Empty sequence cannot be applied", node.getLocation(), node);
		}

		return seq.getSeqof();
	}

	public PType mapApply(AApplyExp node, boolean isSimple, SMapType map)
	{
		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3057, "Map application must have one argument", node.getLocation(), node);
		} else if (map.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3267, "Empty map cannot be applied", node.getLocation(), node);
		}

		PType argtype = node.getArgtypes().get(0);

		if (!af.getTypeComparator().compatible(map.getFrom(), argtype))
		{
			TypeCheckerErrors.concern(isSimple, 3058, "Map application argument is incompatible type", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Map domain", map.getFrom(), "Argument", argtype);
		}

		return map.getTo();
	}

	public PDefinition getRecursiveDefinition(AApplyExp node,
			TypeCheckInfo question)
	{
		ILexNameToken fname = null;
		PExp root = node.getRoot();

		if (root instanceof AApplyExp)
		{
			AApplyExp aexp = (AApplyExp) root;
			return getRecursiveDefinition(aexp, question);
		} else if (root instanceof AVariableExp)
		{
			AVariableExp var = (AVariableExp) root;
			fname = var.getName();
		} else if (root instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp) root;

			if (fie.getExpdef() != null)
			{
				fname = fie.getExpdef().getName();
			} else if (fie.getImpdef() != null)
			{
				fname = fie.getImpdef().getName();
			}
		}

		if (fname != null)
		{
			return question.env.findName(fname, question.scope);
		} else
		{
			return null;
		}
	}

	/**
	 * Create a measure application string from this apply, turning the root function name into the measure name passed,
	 * and collapsing curried argument sets into one.
	 * 
	 * @param node
	 * @param measure
	 * @param close
	 * @return
	 */
	public String getMeasureApply(AApplyExp node, ILexNameToken measure,
			boolean close)
	{
		String start = null;
		PExp root = node.getRoot();

		if (root instanceof AApplyExp)
		{
			AApplyExp aexp = (AApplyExp) root;
			start = getMeasureApply(aexp, measure, false);
		} else if (root instanceof AVariableExp)
		{
			start = measure.getFullName() + "(";
		} else if (root instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp) root;
			start = measure.getFullName() + "["
					+ Utils.listToString(fie.getActualTypes()) + "](";
		} else
		{
			start = root.toString() + "(";
		}

		return start + Utils.listToString(node.getArgs())
				+ (close ? ")" : ", ");
	}
}
