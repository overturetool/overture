/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2016 Overture
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
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class TypeCheckerExportsVisitor extends AbstractTypeCheckVisitor
{
	public TypeCheckerExportsVisitor(IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}
	
	public PType caseAAllExport(AAllExport node, TypeCheckInfo question) throws AnalysisException
	{
		return null; // Implicitly OK.
	}

	public PType caseAFunctionExport(AFunctionExport exp, TypeCheckInfo question) throws AnalysisException
	{
		ITypeCheckerAssistantFactory af = question.assistantFactory;
		ModuleEnvironment menv = (ModuleEnvironment)question.env;
		
		for (ILexNameToken name : exp.getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(menv.getDefinitions(), name, NameScope.NAMES);

			if (def == null)
			{
				TypeCheckerErrors.report(3183, "Exported function " + name
						+ " not defined in module", name.getLocation(), exp);
			}
			else
			{
				PType act = af.createPDefinitionAssistant().getType(def);
				if (exp.getTypeParams() != null && !exp.getTypeParams().isEmpty())
				{
					if (def instanceof AExplicitFunctionDefinition)
					{
						AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)def;
						FlatCheckedEnvironment params = new FlatCheckedEnvironment(af, af.createAExplicitFunctionDefinitionAssistant().getTypeParamDefinitions(efd), question.env, NameScope.NAMES);
						TypeCheckInfo newQuestion = question.newInfo(params);
						PType type = question.assistantFactory.createPTypeAssistant().typeResolve(exp.getExportType(), null, THIS, newQuestion);
					
						if (efd.getTypeParams() == null)
						{
							TypeCheckerErrors.report(3352, "Exported " + name + " function has no type paramaters", name.getLocation(), exp);
						}
						else if (!efd.getTypeParams().equals(exp.getTypeParams()))
						{
							TypeCheckerErrors.report(3353, "Exported " + name + " function type parameters incorrect", name.getLocation(), exp);
							TypeCheckerErrors.detail2("Exported", exp.getTypeParams(), "Actual", efd.getTypeParams());
						}
						
						if (act != null && !act.toString().equals(type.toString()))
						{
							TypeCheckerErrors.report(3184, "Exported " + name
									+ " function type incorrect", name.getLocation(), exp);
							TypeCheckerErrors.detail2("Exported", type, "Actual", act);
						}
					}
					else if (def instanceof AImplicitFunctionDefinition)
					{
						AImplicitFunctionDefinition ifd = (AImplicitFunctionDefinition)def;
						FlatCheckedEnvironment params = new FlatCheckedEnvironment(af, af.createAImplicitFunctionDefinitionAssistant().getTypeParamDefinitions(ifd), question.env, NameScope.NAMES);
						TypeCheckInfo newQuestion = question.newInfo(params);
						PType type = question.assistantFactory.createPTypeAssistant().typeResolve(exp.getExportType(), null, THIS, newQuestion);
						
						if (ifd.getTypeParams() == null)
						{
							TypeCheckerErrors.report(3352, "Exported " + name + " function has no type paramaters", name.getLocation(), exp);
						}
						else if (!ifd.getTypeParams().equals(exp.getTypeParams()))
						{
							TypeCheckerErrors.report(3353, "Exported " + name + " function type parameters incorrect", name.getLocation(), exp);
							TypeCheckerErrors.detail2("Exported", exp.getTypeParams(), "Actual", ifd.getTypeParams());
						}
						
						if (act != null && !act.toString().equals(type.toString()))
						{
							TypeCheckerErrors.report(3184, "Exported " + name
									+ " function type incorrect", name.getLocation(), exp);
							TypeCheckerErrors.detail2("Exported", type, "Actual", act);
						}
					}
				}
				else
				{
					PType type = question.assistantFactory.createPTypeAssistant().typeResolve(exp.getExportType(), null, THIS, question);

					if (act != null && !af.createPTypeAssistant().equals(act, type))
    				{
    					TypeCheckerErrors.report(3184, "Exported " + name
    							+ " function type incorrect", name.getLocation(), exp);
    					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
    				}
				}
			}
		}
		
		return null;
	}

	public PType caseAOperationExport(AOperationExport exp, TypeCheckInfo question) throws AnalysisException
	{
		ITypeCheckerAssistantFactory af = question.assistantFactory;
		ModuleEnvironment menv = (ModuleEnvironment)question.env;

		for (ILexNameToken name : exp.getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(menv.getDefinitions(), name, NameScope.NAMES);

			if (def == null)
			{
				TypeCheckerErrors.report(3185, "Exported operation " + name
						+ " not defined in module", name.getLocation(), exp);
			}
			else
			{
				PType act = def.getType();
				PType type = question.assistantFactory.createPTypeAssistant().typeResolve(exp.getExportType(), null, THIS, question);
				
				if (act != null && !af.createPTypeAssistant().equals(act, type))
				{
					TypeCheckerErrors.report(3186, "Exported operation type does not match actual type", name.getLocation(), exp);
					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
				}
			}
		}
		
		return null;
	}

	public PType caseATypeExport(ATypeExport exp, TypeCheckInfo question) throws AnalysisException
	{
		ILexNameToken name = exp.getName();
		ITypeCheckerAssistantFactory af = question.assistantFactory;
		ModuleEnvironment menv = (ModuleEnvironment)question.env;
		
		PDefinition def = af.createPDefinitionListAssistant().findType(menv.getDefinitions(), name, name.getModule());
		
		if (def == null)
		{
			TypeCheckerErrors.report(3187, "Exported type " + name
					+ " not defined in module", name.getLocation(), exp);
		}
		else
		{
			if (exp.getStruct())
			{
				PType type = af.createPDefinitionAssistant().getType(def);

				if (!(type instanceof ANamedInvariantType) && !(type instanceof ARecordInvariantType))
				{
					TypeCheckerErrors.report(67, "Exported type " + name
							+ " not structured", name.getLocation(), exp);
				}
			}
		}
		
		return null;
	}

	public PType caseAValueExport(AValueExport exp, TypeCheckInfo question) throws AnalysisException
	{
		ITypeCheckerAssistantFactory af = question.assistantFactory;
		ModuleEnvironment menv = (ModuleEnvironment)question.env;
		PType type = question.assistantFactory.createPTypeAssistant().typeResolve(exp.getExportType().clone(), null, THIS, question);

		for (ILexNameToken name : exp.getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(menv.getDefinitions(), name, NameScope.NAMES);

			if (def == null)
			{
				TypeCheckerErrors.report(3188, "Exported value " + name
						+ " not defined in module", name.getLocation(), exp);
			}
			else if (def instanceof AUntypedDefinition)
			{
				// OK
			}
			else
			{
				PType act = question.assistantFactory.createPTypeAssistant().typeResolve(def.getType(), null, THIS, question);

				if (act != null && !question.assistantFactory.getTypeComparator().compatible(act, type))
				{
					TypeCheckerErrors.report(3189, "Exported type does not match actual type", act.getLocation(), act);
					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
				}
			}
		}

		return null;
	}
}
