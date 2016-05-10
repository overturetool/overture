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
package org.overture.typechecker.utilities;

import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//TODO Add assistant Javadoc

/**
 * A refactored assistant/ functionality visitor. This class implements a way to collect definitions from a node in the
 * AST
 * <p>
 * Note that this class may be generalized to a TypeFinder class if this kind of functionality also exists for
 * non-definition nodes
 * </p>
 * 
 * @author kel
 */
public class DefinitionTypeFinder extends AnswerAdaptor<PType>
{

	protected static ITypeCheckerAssistantFactory af;

	public DefinitionTypeFinder(ITypeCheckerAssistantFactory af)
	{
		DefinitionTypeFinder.af = af;
	}

	@Override
	public PType caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return af.createSClassDefinitionAssistant().getType((SClassDefinition) node);
	}

	@Override
	public PType caseAClassInvariantDefinition(AClassInvariantDefinition node)
			throws AnalysisException
	{
		return AstFactory.newABooleanBasicType(node.getLocation());
	}

	@Override
	public PType caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		return node.getDefType() != null ? node.getDefType()
				: AstFactory.newAUnknownType(node.getLocation());
	}

	@Override
	public PType caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return af.createPDefinitionAssistant().getType(node.getState());
	}

	@Override
	public PType caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return ((AImportedDefinition) node).getDef().apply(THIS);
	}

	public static void checkSuperDefinition(AInheritedDefinition d)
	{
		// This is used to get over the case where an inherited definition
		// is a ValueDefinition that has since been replaced with a new
		// LocalDefinition. It would be better to somehow list the
		// inherited definitions that refer to a LocalDefinition and update
		// them...

		if (d.getSuperdef() instanceof AUntypedDefinition)
		{
			if (d.getClassDefinition() != null)
			{
				d.setSuperdef(af.createPDefinitionAssistant().findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
			}
		}
	}

	@Override
	public PType caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{

		checkSuperDefinition(node);
		return af.createPDefinitionAssistant().getType(node.getSuperdef());
	}

	@Override
	public PType caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return node.getType();
	}

	@Override
	public PType caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		return node.getType() == null ? AstFactory.newAUnknownType(node.getLocation())
				: node.getType();
	}

	@Override
	public PType caseAMultiBindListDefinition(AMultiBindListDefinition node)
			throws AnalysisException
	{
		PTypeList types = new PTypeList();

		for (PDefinition definition : node.getDefs())
		{
			types.add(definition.getType());
		}

		AUnionType result = AstFactory.newAUnionType(node.getLocation(), types);

		return result;
	}

	@Override
	public PType caseAMutexSyncDefinition(AMutexSyncDefinition node)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(node.getLocation());
	}

	@Override
	public PType caseANamedTraceDefinition(ANamedTraceDefinition node)
			throws AnalysisException
	{
		return AstFactory.newAOperationType(node.getLocation(), new ArrayList<PType>(), AstFactory.newAVoidType(node.getLocation()));
	}

	@Override
	public PType caseAPerSyncDefinition(APerSyncDefinition node)
			throws AnalysisException
	{
		return AstFactory.newABooleanBasicType(node.getLocation());
	}

	@Override
	public PType caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return ((ARenamedDefinition) node).getDef().apply(THIS);
	}

	@Override
	public PType caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return ((AStateDefinition) node).getRecordType();
	}

	@Override
	public PType caseAThreadDefinition(AThreadDefinition node)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(node.getLocation());
	}

	@Override
	public PType caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		return ((ATypeDefinition) node).getInvType();
	}

	@Override
	public PType caseAUntypedDefinition(AUntypedDefinition node)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(node.getLocation());
	}

	@Override
	public PType caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		// return AValueDefinitionAssistantTC.getType((AValueDefinition) node);
		return node.getType() != null ? node.getType()
				: node.getExpType() != null ? node.getExpType()
						: AstFactory.newAUnknownType(node.getLocation());
	}

	@Override
	public PType createNewReturnValue(INode node)
	{
		assert false : "getDefinitions should never hit the default case";
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node)
	{
		assert false : "getDefinitions should never hit the default case";
		return null;
	}
}
