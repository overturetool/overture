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
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to find the kind of a node in the AST
 * 
 * @author kel
 */
public class KindFinder extends AnswerAdaptor<String>
{

	protected ITypeCheckerAssistantFactory af;

	public KindFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public String caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return "assignable variable";
	}

	@Override
	public String defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return "class";
	}

	@Override
	public String caseAClassInvariantDefinition(AClassInvariantDefinition node)
			throws AnalysisException
	{
		return "invariant";
	}

	@Override
	public String caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		return "equals";
	}

	@Override
	public String caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		return "explicit function";
	}

	@Override
	public String caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		return "explicit operation";
	}

	@Override
	public String caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return "external";
	}

	@Override
	public String caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		return "implicit function";
	}

	@Override
	public String caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		return "implicit operation";
	}

	@Override
	public String caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return "import";
	}

	@Override
	public String caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return node.getSuperdef().apply(THIS);
	}

	@Override
	public String caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return "instance variable";
	}

	@Override
	public String caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		return "local";
	}

	@Override
	public String caseAMultiBindListDefinition(AMultiBindListDefinition node)
			throws AnalysisException
	{
		return "bind";
	}

	@Override
	public String caseAMutexSyncDefinition(AMutexSyncDefinition node)
			throws AnalysisException
	{
		return "mutex predicate";
	}

	@Override
	public String caseANamedTraceDefinition(ANamedTraceDefinition node)
			throws AnalysisException
	{
		return "trace";
	}

	@Override
	public String caseAPerSyncDefinition(APerSyncDefinition node)
			throws AnalysisException
	{
		return "permission predicate";
	}

	@Override
	public String caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public String caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return "state";
	}

	@Override
	public String caseAThreadDefinition(AThreadDefinition node)
			throws AnalysisException
	{
		return "thread";
	}

	@Override
	public String caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		return "type";
	}

	@Override
	public String caseAUntypedDefinition(AUntypedDefinition node)
			throws AnalysisException
	{
		return "untyped";
	}

	@Override
	public String caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		return "value";
	}

	@Override
	public String defaultPDefinition(PDefinition node) throws AnalysisException
	{
		return null;
	}

	@Override
	public String createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public String createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}

}
