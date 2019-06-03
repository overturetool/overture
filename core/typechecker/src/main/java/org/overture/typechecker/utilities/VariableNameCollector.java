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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
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
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//TODO Add assistant Javadoc

/**
 * A refactored assistant / functionality visitor. This class implements a way to collect variable names from any node
 * in the AST
 * 
 * @author kel
 */
public class VariableNameCollector extends AnswerAdaptor<LexNameList>
{

	protected ITypeCheckerAssistantFactory af;

	public VariableNameCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public LexNameList caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return af.createPDefinitionListAssistant().getVariableNames(node.getDefinitions());
	}

	@Override
	public LexNameList caseAClassInvariantDefinition(
			AClassInvariantDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		return node.getDefs() == null ? new LexNameList()
				: af.createPDefinitionListAssistant().getVariableNames(node.getDefs());
	}

	@Override
	public LexNameList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return node.getState().apply(THIS); // TODO: Is this applicable?
	}

	@Override
	public LexNameList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public LexNameList caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		// return AInheritedDefinitionAssistantTC.getVariableNames((AInheritedDefinition) node);
		LexNameList names = new LexNameList();
		// TODO:What About Here, how to I need to handle it. like I have it or Bring the method to this class?
		DefinitionTypeFinder.checkSuperDefinition(node);

		for (ILexNameToken vn : node.getSuperdef().apply(THIS))
		{
			names.add(vn.getModifiedName(node.getName().getModule()));
		}

		return names;
	}

	@Override
	public LexNameList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAMultiBindListDefinition(
			AMultiBindListDefinition node) throws AnalysisException
	{
		return node.getDefs() == null ? new LexNameList()
				: af.createPDefinitionListAssistant().getVariableNames(node.getDefs());
	}

	@Override
	public LexNameList caseAMutexSyncDefinition(AMutexSyncDefinition node)
			throws AnalysisException
	{
		return new LexNameList();
	}

	@Override
	public LexNameList caseANamedTraceDefinition(ANamedTraceDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAPerSyncDefinition(APerSyncDefinition node)
			throws AnalysisException
	{
		return new LexNameList();
	}

	@Override
	public LexNameList caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		LexNameList both = new LexNameList(node.getName());
		both.add(node.getDef().getName());
		return both;
	}

	@Override
	public LexNameList caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return af.createPDefinitionListAssistant().getVariableNames(node.getStateDefs());
	}

	@Override
	public LexNameList caseAThreadDefinition(AThreadDefinition node)
			throws AnalysisException
	{
		return node.getOperationDef() == null ? null
				: new LexNameList(node.getOperationDef().getName());
	}

	@Override
	public LexNameList caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAUntypedDefinition(AUntypedDefinition node)
			throws AnalysisException
	{
		return new LexNameList(node.getName());
	}

	@Override
	public LexNameList caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		return af.createPPatternAssistant(node.getLocation().getModule()).getVariableNames(node.getPattern());
	}

	@Override
	public LexNameList createNewReturnValue(INode node)
	{
		assert false : "default case should never happen in getVariableNames";
		return null;
	}

	@Override
	public LexNameList createNewReturnValue(Object node)
	{
		assert false : "default case should never happen in getVariableNames";
		return null;
	}

}
