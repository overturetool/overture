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
package org.overture.typechecker.assistant.statement;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//FIXME: all methods used in same class. move them
public class ACallObjectStatementAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public ACallObjectStatementAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PType> getArgTypes(List<PExp> args,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		List<PType> types = new LinkedList<PType>();

		for (PExp e : args)
		{
			types.add(e.apply(rootVisitor, question));
		}

		return types;
	}

	public void checkArgTypes(PType type, List<PType> ptypes, List<PType> atypes)
	{
		if (ptypes.size() != atypes.size())
		{
			TypeCheckerErrors.report(3211, "Expecting " + ptypes.size()
					+ " arguments", type.getLocation(), type);
		} else
		{
			int i = 0;

			for (PType atype : atypes)
			{
				PType ptype = ptypes.get(i++);

				if (!af.getTypeComparator().compatible(ptype, atype))
				{
					TypeCheckerErrors.report(3212, "Unexpected type for argument "
							+ i, atype.getLocation(), atype);
					TypeCheckerErrors.detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

}
