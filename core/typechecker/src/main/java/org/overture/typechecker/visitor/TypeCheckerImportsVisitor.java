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

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;

public class TypeCheckerImportsVisitor extends AbstractTypeCheckVisitor
{

	public TypeCheckerImportsVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}

	@Override
	public PType caseAAllImport(AAllImport node, TypeCheckInfo question)
	{
		return null; // Implicitly OK.
	}

	@Override
	public PType caseATypeImport(ATypeImport node, TypeCheckInfo question)
	{
		if (node.getDef() != null && node.getFrom() != null)
		{
			PDefinition def = node.getDef();
			ILexNameToken name = node.getName();
			AModuleModules from = node.getFrom();
			def.setType((SInvariantType) question.assistantFactory.createPTypeAssistant().typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(def), null, THIS, question));
			PDefinition expdef = question.assistantFactory.createPDefinitionListAssistant().findType(from.getExportdefs(), name, null);

			if (expdef != null)
			{
				PType exptype = question.assistantFactory.createPTypeAssistant().typeResolve(expdef.getType(), null, THIS, question);

				if (!question.assistantFactory.getTypeComparator().compatible(def.getType(), exptype))
				{
					TypeCheckerErrors.report(3192, "Type import of " + name
							+ " does not match export from " + from.getName(), node.getLocation(), node);
					TypeCheckerErrors.detail2("Import", def.getType().toString() // TODO: .toDetailedString()
					, "Export", exptype.toString()); // TODO:
														// .toDetailedString());
				}
			}
		}
		return null;
	}

	@Override
	public PType defaultSValueImport(SValueImport node, TypeCheckInfo question)
	{
		PType type = node.getImportType();
		AModuleModules from = node.getFrom();
		ILexNameToken name = node.getName();

		if (type != null && from != null)
		{
			type = question.assistantFactory.createPTypeAssistant().typeResolve(type, null, THIS, question);
			PDefinition expdef = question.assistantFactory.createPDefinitionListAssistant().findName(from.getExportdefs(), name, NameScope.NAMES);

			if (expdef != null)
			{
				PType exptype = question.assistantFactory.createPTypeAssistant().typeResolve(expdef.getType(), null, THIS, question);

				if (!question.assistantFactory.getTypeComparator().compatible(type, exptype))
				{
					TypeCheckerErrors.report(3194, "Type of value import "
							+ name + " does not match export from "
							+ from.getName(), node.getLocation(), node);
					TypeCheckerErrors.detail2("Import", type.toString(), // TODO:
																			// .toDetailedString(),
							"Export", exptype.toString()); // TODO:
															// .toDetailedString());
				}
			}
		}
		return null;
	}

	@Override
	public PType caseAFunctionValueImport(AFunctionValueImport node,
			TypeCheckInfo question)
	{
		if (node.getTypeParams().size() == 0)
		{
			return defaultSValueImport(node, question);
		}
		else
		{
			List<PDefinition> defs = new Vector<PDefinition>();

			for (ILexNameToken pname : node.getTypeParams())
			{
				ILexNameToken pnameClone = pname.clone();
				PDefinition p = AstFactory.newALocalDefinition(pname.getLocation(), pnameClone, NameScope.NAMES, AstFactory.newAParameterType(pnameClone));

				question.assistantFactory.createPDefinitionAssistant().markUsed(p);
				defs.add(p);
			}

			FlatCheckedEnvironment params = new FlatCheckedEnvironment(question.assistantFactory, defs, question.env, NameScope.NAMES);
			PType rtype = question.assistantFactory.createPTypeAssistant().typeResolve(node.getImportType(), null, THIS, question.newInfo(params));
			node.setImportType(rtype);
			PDefinition def = question.assistantFactory.createPDefinitionListAssistant().findName(node.getFrom().getExportdefs(), node.getName(), NameScope.NAMES);
			
			if (def instanceof AExplicitFunctionDefinition)
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)def;
				
				if (efd.getTypeParams() == null || efd.getTypeParams().isEmpty())
				{
					TypeCheckerErrors.report(3352, "Imported " + node.getName() + " function has no type paramaters", node.getLocation(), node);
				}
				else if (!efd.getTypeParams().toString().equals(node.getTypeParams().toString()))
				{
					TypeCheckerErrors.report(3353, "Imported " + node.getName() + " function type parameters incorrect", node.getLocation(), node);
					TypeCheckerErrors.detail2("Imported", node.getTypeParams(), "Actual", efd.getTypeParams());
				}
				
				if (efd.getType() != null && !efd.getType().toString().equals(node.getImportType().toString()))
				{
					TypeCheckerErrors.report(3184, "Imported " + node.getName() + " function type incorrect", node.getLocation(), node);
					TypeCheckerErrors.detail2("Imported", node.getImportType(), "Actual", efd.getType());
				}
				
				node.setImportType(efd.getType().clone());
			}
			else if (def instanceof AImplicitFunctionDefinition)
			{
				AImplicitFunctionDefinition ifd = (AImplicitFunctionDefinition)def;
				
				if (ifd.getTypeParams() == null || ifd.getTypeParams().isEmpty())
				{
					TypeCheckerErrors.report(3352, "Imported " + node.getName() + " function has no type paramaters", node.getLocation(), node);
				}
				else if (!ifd.getTypeParams().toString().equals(node.getTypeParams().toString()))
				{
					TypeCheckerErrors.report(3353, "Imported " + node.getName() + " function type parameters incorrect", node.getLocation(), node);
					TypeCheckerErrors.detail2("Imported", node.getTypeParams(), "Actual", ifd.getTypeParams());
				}
				
				if (ifd.getType() != null && !ifd.getType().toString().equals(node.getImportType().toString()))
				{
					TypeCheckerErrors.report(3184, "Imported " + node.getName() + " function type incorrect", node.getLocation(), node);
					TypeCheckerErrors.detail2("Imported", node.getImportType(), "Actual", ifd.getType());
				}
				
				node.setImportType(ifd.getType().clone());
			}
		}

		return null;
	}

	@Override
	public PType caseAOperationValueImport(AOperationValueImport node,
			TypeCheckInfo question)
	{
		return defaultSValueImport(node, question);
	}
}
