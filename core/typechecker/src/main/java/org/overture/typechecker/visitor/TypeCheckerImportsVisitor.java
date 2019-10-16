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

import org.overture.ast.analysis.AnalysisException;
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
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

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
	public PType caseATypeImport(ATypeImport node, TypeCheckInfo question) throws AnalysisException
	{
		PDefinition expdef = null; 
		
		if (node.getFrom() != null)
		{
			expdef = question.assistantFactory.createPDefinitionListAssistant().findType(node.getFrom().getExportdefs(), node.getName(), null);
			
			if (expdef != null)
			{
				boolean istype = question.assistantFactory.createPDefinitionAssistant().isTypeDefinition(expdef);
				checkKind(question.assistantFactory, expdef, istype, "type", node);
			}
		}

		if (node.getDef() != null && node.getFrom() != null)
		{
			PDefinition def = node.getDef();
			ILexNameToken name = node.getName();
			AModuleModules from = node.getFrom();
			def.setType((SInvariantType) question.assistantFactory.createPTypeAssistant().typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(def), null, THIS, question));

			if (expdef != null)
			{
				boolean istype = question.assistantFactory.createPDefinitionAssistant().isTypeDefinition(expdef);
				checkKind(question.assistantFactory, expdef, istype, "type", node);
				PType exptype = question.assistantFactory.createPTypeAssistant().typeResolve(expdef.getType(), null, THIS, question);

				// if (!question.assistantFactory.getTypeComparator().compatible(def.getType(), exptype))
				String detail1 = question.assistantFactory.createPTypeAssistant().toDetailedString(def.getType());
				String detail2 = question.assistantFactory.createPTypeAssistant().toDetailedString(exptype);
				
				if (!detail1.equals(detail2))
				{
					TypeCheckerErrors.report(3192, "Type import of " + name
							+ " does not match export from " + from.getName(), node.getLocation(), node);
					TypeCheckerErrors.detail2("Import", detail1, "Export", detail2);
				}
			}
		}
		return null;
	}

	@Override
	public PType defaultSValueImport(SValueImport node, TypeCheckInfo question) throws AnalysisException
	{
		PDefinition expdef = null;
		
		if (node.getFrom() != null)
		{
			expdef = question.assistantFactory.createPDefinitionListAssistant().findName(node.getFrom().getExportdefs(), node.getName(), NameScope.NAMES);
    
    		if (expdef != null)
    		{
    			boolean expected = false;
    			String expkind = ""; 
    			
    			if (node instanceof SValueImport)
    			{
    				expected = question.assistantFactory.createPDefinitionAssistant().isValueDefinition(expdef);
    				expkind = "value";
    			}
    			if (node instanceof AFunctionValueImport)
    			{
    				expected = question.assistantFactory.createPTypeAssistant().isFunction(expdef.getType(), question.fromModule);
    				expkind = "function";
    			}
    			else if (node instanceof AOperationValueImport)
    			{
    				expected = question.assistantFactory.createPTypeAssistant().isOperation(expdef.getType(), question.fromModule);
    				expkind = "operation";
    			}
    			
    			checkKind(question.assistantFactory, expdef, expected, expkind, node);
    		}
		}
		
		PType type = node.getImportType();
		AModuleModules from = node.getFrom();
		ILexNameToken name = node.getName();

		if (type != null && from != null && expdef != null)
		{
			type = question.assistantFactory.createPTypeAssistant().typeResolve(type, null, THIS, question);
				
			PType exptype = question.assistantFactory.createPTypeAssistant().typeResolve(expdef.getType(), null, THIS, question);
			
			// Temporarily tweak the module to look like it is the exporting module
			String m = question.assistantFactory.getTypeComparator().getCurrentModule();
			question.assistantFactory.getTypeComparator().setCurrentModule(exptype.getLocation().getModule());

			if (!question.assistantFactory.getTypeComparator().compatible(type, exptype))
			{
				TypeCheckerErrors.report(3194, "Type of import "
						+ name + " does not match export from "
						+ from.getName(), node.getLocation(), node);
				TypeCheckerErrors.detail2("Import", type.toString(), "Export", exptype.toString());
			}
			
			question.assistantFactory.getTypeComparator().setCurrentModule(m);		// Restore it
		}
	
		return null;
	}

	@Override
	public PType caseAFunctionValueImport(AFunctionValueImport node,
			TypeCheckInfo question) throws AnalysisException
	{
		PDefinition expdef = null;
		
		if (node.getFrom() != null)
		{
			expdef = question.assistantFactory.createPDefinitionListAssistant().findName(node.getFrom().getExportdefs(), node.getName(), NameScope.NAMES);
			
			if (expdef != null)
			{
				boolean isfunc = question.assistantFactory.createPDefinitionAssistant().isFunction(expdef);
				checkKind(question.assistantFactory, expdef, isfunc, "function", node);
			}
		}

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
			
			if (expdef instanceof AExplicitFunctionDefinition)
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)expdef;
				
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
			else if (expdef instanceof AImplicitFunctionDefinition)
			{
				AImplicitFunctionDefinition ifd = (AImplicitFunctionDefinition)expdef;
				
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
			TypeCheckInfo question) throws AnalysisException
	{
		return defaultSValueImport(node, question);
	}
	
	private void checkKind(ITypeCheckerAssistantFactory af, PDefinition actual, boolean expected, String expkind, PImport node) throws AnalysisException
	{
		if (actual != null && !expected)
		{
    		String actkind = actual.apply(af.getKindFinder());
   			TypeCheckerErrors.report(3356, "Import of " + expkind + " " + actual.getName() + " is " + actkind, node.getLocation(), node);
		}
	}
}
