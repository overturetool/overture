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

import java.util.Collection;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way collect the exit types of a statement. This is to be used with exception handling in VDM
 * 
 * @author kel
 */
public class ExitTypeCollector extends AnswerAdaptor<PTypeSet>
{

	protected ITypeCheckerAssistantFactory af;

	public ExitTypeCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PTypeSet caseAAlwaysStm(AAlwaysStm statement)
			throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS));
		types.addAll(statement.getAlways().apply(THIS));
		return types;
	}

	@Override
	public PTypeSet caseAAssignmentStm(AAssignmentStm statement)
			throws AnalysisException
	{
		// TODO We don't know what an expression call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACallStm(ACallStm statement) throws AnalysisException
	{
		// TODO We don't know what an operation call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACallObjectStm(ACallObjectStm statement)
			throws AnalysisException
	{
		// TODO We don't know what an operation call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACasesStm(ACasesStm statement) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		for (ACaseAlternativeStm c : statement.getCases())
		{
			types.addAll(c.apply(THIS));
		}

		return types;
	}

	@Override
	public PTypeSet caseACaseAlternativeStm(ACaseAlternativeStm statement)
			throws AnalysisException
	{
		return statement.getResult().apply(THIS);
	}

	@Override
	public PTypeSet caseAElseIfStm(AElseIfStm statement)
			throws AnalysisException
	{
		return statement.getThenStm().apply(THIS);
	}

	@Override
	public PTypeSet caseAExitStm(AExitStm statement) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		if (statement.getExpression() == null)
		{
			types.add(AstFactory.newAVoidType(statement.getLocation()));
		} else
		{
			types.add(statement.getExpType());
		}

		return types;
	}

	@Override
	public PTypeSet caseAForAllStm(AForAllStm statement)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseAForIndexStm(AForIndexStm statement)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseAForPatternBindStm(AForPatternBindStm statement)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseAIfStm(AIfStm statement) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getThenStm().apply(THIS));

		for (AElseIfStm stmt : statement.getElseIf())
		{
			types.addAll(stmt.apply(THIS));
		}

		if (statement.getElseStm() != null)
		{
			types.addAll(statement.getElseStm().apply(THIS));
		}

		return types;
	}

	@Override
	public PTypeSet caseALetBeStStm(ALetBeStStm statement)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseALetStm(ALetStm statement) throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseAReturnStm(AReturnStm statement)
			throws AnalysisException
	{
		if (statement.getExpression() != null)
		{
			// TODO We don't know what an expression will raise
			return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
		} else
		{
			return new PTypeSet(af);
		}
	}

	@Override
	public PTypeSet defaultSSimpleBlockStm(SSimpleBlockStm statement)
			throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		for (PStm stmt : statement.getStatements())
		{
			types.addAll(stmt.apply(THIS));
		}

		return types;
	}

	@Override
	public PTypeSet caseATixeStm(ATixeStm statement) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS));

		for (ATixeStmtAlternative tsa : statement.getTraps())
		{
			types.addAll(exitCheck(tsa));
		}

		return types;
	}

	private Collection<? extends PType> exitCheck(ATixeStmtAlternative tsa)
			throws AnalysisException
	{
		return tsa.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet caseATrapStm(ATrapStm statement) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS));
		types.addAll(statement.getWith().apply(THIS));
		return types;
	}

	@Override
	public PTypeSet caseAWhileStm(AWhileStm statement) throws AnalysisException
	{
		return statement.getStatement().apply(THIS);
	}

	@Override
	public PTypeSet defaultPStm(PStm statement) throws AnalysisException
	{
		return new PTypeSet(af);
	}

	@Override
	public PTypeSet createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public PTypeSet createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}

}
