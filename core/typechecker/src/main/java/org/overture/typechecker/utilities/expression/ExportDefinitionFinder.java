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
package org.overture.typechecker.utilities.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the definition of an exported expression from its actualType.
 * 
 * @author kel
 */
public class ExportDefinitionFinder
		extends
		QuestionAnswerAdaptor<LinkedList<PDefinition>, Collection<? extends PDefinition>>
{
	protected ITypeCheckerAssistantFactory af;

	public ExportDefinitionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Collection<? extends PDefinition> caseAAllExport(AAllExport exp,
			LinkedList<PDefinition> actualDefs) throws AnalysisException
	{
		return actualDefs; // The lot!
	}

	@Override
	public Collection<? extends PDefinition> caseAFunctionExport(
			AFunctionExport exp, LinkedList<PDefinition> actualDefs)
			throws AnalysisException
	{
		List<PDefinition> list = new ArrayList<PDefinition>();
		for (ILexNameToken name : exp.getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(actualDefs, name, NameScope.NAMES);

			if (def == null)
			{
				TypeCheckerErrors.report(3183, "Exported function " + name
						+ " not defined in module", name.getLocation(), exp);
			} else
			{
				PType act = af.createPDefinitionAssistant().getType(def);
				PType type = exp.getExportType();

				if (exp.getTypeParams() != null && !exp.getTypeParams().isEmpty())
				{
					if (def instanceof AExplicitFunctionDefinition)
					{
						AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)def;
						
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
				else if (act != null && !af.createPTypeAssistant().equals(act, type))
				{
					TypeCheckerErrors.report(3184, "Exported " + name
							+ " function type incorrect", name.getLocation(), exp);
					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
				}

				list.add(def);
			}
		}
		
		return list;
	}

	@Override
	public Collection<? extends PDefinition> caseAOperationExport(
			AOperationExport exp, LinkedList<PDefinition> actualDefs)
			throws AnalysisException
	{
		List<PDefinition> list = new ArrayList<PDefinition>();
		for (ILexNameToken name : ((AOperationExport) exp).getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(actualDefs, name, NameScope.NAMES);

			if (def == null)
			{
				TypeCheckerErrors.report(3185, "Exported operation " + name
						+ " not defined in module", name.getLocation(), exp);
			} else
			{
				PType act = def.getType();
				PType type = ((AOperationExport) exp).getExportType();

				if (act != null && !af.createPTypeAssistant().equals(act, type))
				{
					TypeCheckerErrors.report(3186, "Exported operation type does not match actual type", name.getLocation(), exp);
					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
				}

				list.add(def);
			}
		}
		return list;
	}

	@Override
	public Collection<? extends PDefinition> caseATypeExport(ATypeExport exp,
			LinkedList<PDefinition> actualDefs) throws AnalysisException
	{
		ILexNameToken name = ((ATypeExport) exp).getName();
		List<PDefinition> list = new ArrayList<PDefinition>();
		PDefinition def = af.createPDefinitionListAssistant().findType(actualDefs, name, name.getModule());
		if (def == null)
		{
			TypeCheckerErrors.report(3187, "Exported type " + name
					+ " not defined in module", name.getLocation(), exp);
		} else
		{
			if (((ATypeExport) exp).getStruct())
			{
				list.add(def);
			} else
			{
				PType type = af.createPDefinitionAssistant().getType(def);

				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantType ntype = (ANamedInvariantType) type;
					SInvariantType copy = AstFactory.newANamedInvariantType(ntype.getName().clone(), ntype.getType());
					// new ANamedInvariantType(ntype.getName().getLocation(),false,list, false, null,
					// ntype.getName().clone(), ntype.getType());
					copy.setOpaque(true);
					copy.setInvDef(ntype.getInvDef());
					list.add(AstFactory.newATypeDefinition(def.getName(), copy, null, null));
					// list.add(new ATypeDefinition(def.getName().location,
					// NameScope.TYPENAME,false,null,PAccessSpecifierAssistant.getDefault(),null, copy,
					// null,null,null,false,def.getName()));
				} else if (type instanceof ARecordInvariantType)
				{
					ARecordInvariantType rtype = (ARecordInvariantType) type;
					@SuppressWarnings("unchecked")
					SInvariantType copy = AstFactory.newARecordInvariantType(rtype.getName().clone(), (List<AFieldField>) rtype.getFields().clone());
					// new ARecordInvariantType(rtype.getName().location,false, rtype.getName().clone(), (List<?
					// extends AFieldField>) rtype.getFields().clone());
					copy.setOpaque(true);
					copy.setInvDef(rtype.getInvDef());
					list.add(AstFactory.newATypeDefinition(def.getName(), copy, null, null));
					// new ATypeDefinition(def.getName().location,
					// NameScope.TYPENAME,false,null,PAccessSpecifierAssistant.getDefault(),null,
					// copy,null,null,null,false,def.getName()));
				} else
				{
					TypeCheckerErrors.report(67, "Exported type " + name
							+ " not structured", name.getLocation(), exp);
				}
			}
		}
		return list;
	}

	@Override
	public Collection<? extends PDefinition> caseAValueExport(AValueExport exp,
			LinkedList<PDefinition> actualDefs) throws AnalysisException
	{
		List<PDefinition> list = new ArrayList<PDefinition>();
		for (ILexNameToken name : ((AValueExport) exp).getNameList())
		{
			PDefinition def = af.createPDefinitionListAssistant().findName(actualDefs, name, NameScope.NAMES);
			PType type = ((AValueExport) exp).getExportType().clone();

			if (def == null)
			{
				TypeCheckerErrors.report(3188, "Exported value " + name
						+ " not defined in module", name.getLocation(), exp);
			} else if (def instanceof AUntypedDefinition)
			{
				AUntypedDefinition untyped = (AUntypedDefinition) def;
				list.add(AstFactory.newALocalDefinition(untyped.getLocation(), untyped.getName(), NameScope.GLOBAL, type));
				// new ALocalDefinition(untyped.getLocation(), NameScope.GLOBAL,
				// false,null,PAccessSpecifierAssistant.getDefault(),type, false,untyped.getName()));
			} else
			{
				PType act = def.getType();

				if (act != null && !act.equals(type))
				{
					TypeCheckerErrors.report(3189, "Exported type does not match actual type", act.getLocation(), act);
					TypeCheckerErrors.detail2("Exported", type, "Actual", act);
				}

				list.add(def);
			}
		}
		return list;
	}

	@Override
	public Collection<? extends PDefinition> createNewReturnValue(INode node,
			LinkedList<PDefinition> question) throws AnalysisException
	{
		assert false;// "No match in switch";
		return null;
	}

	@Override
	public Collection<? extends PDefinition> createNewReturnValue(Object node,
			LinkedList<PDefinition> question) throws AnalysisException
	{
		assert false;// "No match in switch";
		return null;
	}
}
