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
package org.overture.typechecker.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeCheckerPatternVisitor extends AbstractTypeCheckVisitor
{

	public TypeCheckerPatternVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}

	@Override
	public PType caseASetMultipleBind(ASetMultipleBind node,
			TypeCheckInfo question) throws AnalysisException
	{

		question.assistantFactory.createPPatternListAssistant().typeResolve(node.getPlist(), THIS, question);
		question.qualifiers = null;
		PType type = node.getSet().apply(THIS, question);
		PType result = AstFactory.newAUnknownType(node.getLocation());

		if (!question.assistantFactory.createPTypeAssistant().isSet(type))
		{
			TypeCheckerErrors.report(3197, "Expression matching set bind is not a set", node.getSet().getLocation(), node.getSet());
			TypeCheckerErrors.detail("Actual type", type);
		} else
		{
			ASetType st = question.assistantFactory.createPTypeAssistant().getSet(type);

			if (!st.getEmpty())
			{
				result = st.getSetof();
				PType ptype = question.assistantFactory.createPMultipleBindAssistant().getPossibleType(node);

				if (!question.assistantFactory.getTypeComparator().compatible(ptype, result))
				{
					TypeCheckerErrors.report(3264, "At least one bind cannot match set", node.getSet().getLocation(), node.getSet());
					TypeCheckerErrors.detail2("Binds", ptype, "Set of", st);
				}
			} else
			{
				TypeCheckerErrors.warning(3264, "Empty set used in bind", node.getSet().getLocation(), node.getSet());
			}
		}

		return result;
	}

	@Override
	public PType caseATypeMultipleBind(ATypeMultipleBind node,
			TypeCheckInfo question) throws AnalysisException
	{

		question.assistantFactory.createPPatternListAssistant().typeResolve(node.getPlist(), THIS, question);
		PType type = question.assistantFactory.createPTypeAssistant().typeResolve(node.getType(), null, THIS, question);
		PType ptype = question.assistantFactory.createPPatternListAssistant().getPossibleType(node.getPlist(), node.getLocation());

		if (!question.assistantFactory.getTypeComparator().compatible(ptype, type))
		{
			TypeCheckerErrors.report(3265, "At least one bind cannot match this type", type.getLocation(), type);
			TypeCheckerErrors.detail2("Binds", ptype, "Type", type);
		}

		node.setType(type);
		return type;
	}

	@Override
	public PType caseAObjectPattern(AObjectPattern pattern, TypeCheckInfo question) throws AnalysisException
	{
		PTypeAssistantTC typeAssistant = question.assistantFactory.createPTypeAssistant();
		
		if (!typeAssistant.isClass(pattern.getType(), question.env))
		{
			TypeCheckerErrors.report(3331, "obj_ expression is not an object type", pattern.getLocation(), pattern);
			TypeCheckerErrors.detail("Type", pattern.getType());
		}
		else
		{
			// Check whether the field access is permitted from here.
			AClassType cls = typeAssistant.getClassType(pattern.getType(), question.env);
	
			for (ANamePatternPair npp: pattern.getFields())
			{
				PDefinition fdef = question.assistantFactory.createAClassTypeAssistant().findName(cls, npp.getName(), NameScope.STATE);
	
				if (fdef == null)
				{
					TypeCheckerErrors.report(3091, "Unknown member " + npp.getName() + " of class " + cls.getName().getName(), npp.getName().getLocation(), npp.getName());
				}
				else if (!question.assistantFactory.createSClassDefinitionAssistant().isAccessible(question.env, fdef, false))
				{
					TypeCheckerErrors.report(3092, "Inaccessible member " + npp.getName() + " of class " + cls.getName().getName(), npp.getName().getLocation(), npp.getName());
				}
			}
	
			if (question.env.isFunctional())
			{
				TypeCheckerErrors.report(3332, "Object pattern cannot be used from a function", pattern.getLocation(), pattern);
			}
		}
		
		return pattern.getType();
	}
}
