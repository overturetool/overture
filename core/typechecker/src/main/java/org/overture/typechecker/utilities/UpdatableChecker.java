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
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to check if a definition is updatable from a node in the AST
 * 
 * @author kel
 */

public class UpdatableChecker extends AnswerAdaptor<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public UpdatableChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAAssignmentDefinition(AAssignmentDefinition node)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public Boolean caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return node.getSuperdef().apply(THIS);
	}

	@Override
	public Boolean caseALocalDefinition(ALocalDefinition node)
			throws AnalysisException
	{
		return node.getNameScope().matches(NameScope.STATE)
				|| af.createPTypeAssistant().isClass(af.createPDefinitionAssistant().getType(node), null);
	}

	@Override
	public Boolean caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}

}
