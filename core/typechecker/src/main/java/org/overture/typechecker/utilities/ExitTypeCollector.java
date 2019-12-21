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
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
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
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way collect the exit types of a statement. This is to be used with exception handling in VDM
 * 
 * @author kel
 */
public class ExitTypeCollector extends QuestionAnswerAdaptor<Environment, PTypeSet>
{
	protected ITypeCheckerAssistantFactory af;

	public ExitTypeCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PTypeSet caseAAlwaysStm(AAlwaysStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS, env));
		types.addAll(statement.getAlways().apply(THIS, env));
		return types;
	}

	@Override
	public PTypeSet caseAAssignmentStm(AAssignmentStm statement, Environment env)
			throws AnalysisException
	{
		// TODO We don't know what an expression call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACallStm(ACallStm statement, Environment env) throws AnalysisException
	{
		PDefinition opdef = env.findName(statement.getName(), NameScope.GLOBAL);

		if (opdef != null)
		{
			if (opdef instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition explop = (AExplicitOperationDefinition)opdef;

				if (explop.getPossibleExceptions() == null)
				{
					explop.setPossibleExceptions(new PTypeSet(af));
					IQuestionAnswer<Environment, PTypeSet> collector = af.getExitTypeCollector();
					explop.setPossibleExceptions(explop.getBody().apply(collector, env));
				}

				return explop.getPossibleExceptions();
			}
			else if (opdef instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition implop = (AImplicitOperationDefinition)opdef;

				if (implop.getPossibleExceptions() == null)
				{
					if (implop.getBody() != null)
					{
						implop.setPossibleExceptions(new PTypeSet(af));
						IQuestionAnswer<Environment, PTypeSet> collector = af.getExitTypeCollector();
						implop.setPossibleExceptions(implop.getBody().apply(collector, env));
					}
					else
					{
						return new PTypeSet(af);
					}
				}

				return implop.getPossibleExceptions();
			}
		}

		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACallObjectStm(ACallObjectStm statement, Environment env)
			throws AnalysisException
	{
		PDefinition fdef = statement.getFieldDef();

		if (fdef != null)
		{
			if (fdef instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition explop = (AExplicitOperationDefinition)fdef;

				if (explop.getPossibleExceptions() == null)
				{
					explop.setPossibleExceptions(new PTypeSet(af));
					IQuestionAnswer<Environment, PTypeSet> collector = af.getExitTypeCollector();
					explop.setPossibleExceptions(explop.getBody().apply(collector, env));
				}

				return explop.getPossibleExceptions();
			}
			else if (fdef instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition implop = (AImplicitOperationDefinition)fdef;

				if (implop.getPossibleExceptions() == null)
				{
					if (implop.getBody() != null)
					{
						implop.setPossibleExceptions(new PTypeSet(af));
						IQuestionAnswer<Environment, PTypeSet> collector = af.getExitTypeCollector();
						implop.setPossibleExceptions(implop.getBody().apply(collector, env));
					}
					else
					{
						return new PTypeSet(af);
					}
				}

				return implop.getPossibleExceptions();
			}
		}

		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseACasesStm(ACasesStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		for (ACaseAlternativeStm c : statement.getCases())
		{
			types.addAll(c.apply(THIS, env));
		}

		return types;
	}

	@Override
	public PTypeSet caseACaseAlternativeStm(ACaseAlternativeStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getResult().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAElseIfStm(AElseIfStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getThenStm().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAExitStm(AExitStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		if (statement.getExpression() == null)
		{
			types.add(AstFactory.newAVoidType(statement.getLocation()));
		}
		else if (statement.getExpType() == null)
		{
			types.add(AstFactory.newAUnknownType(statement.getLocation()));
		}
		else
		{
			types.add(statement.getExpType());
		}

		return types;
	}

	@Override
	public PTypeSet caseAForAllStm(AForAllStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAForIndexStm(AForIndexStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAForPatternBindStm(AForPatternBindStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAIfStm(AIfStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getThenStm().apply(THIS, env));

		for (AElseIfStm stmt : statement.getElseIf())
		{
			types.addAll(stmt.apply(THIS, env));
		}

		if (statement.getElseStm() != null)
		{
			types.addAll(statement.getElseStm().apply(THIS, env));
		}

		return types;
	}
	
	@Override
	public PTypeSet caseASubclassResponsibilityStm(ASubclassResponsibilityStm statement, Environment question)
			throws AnalysisException
	{
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseALetBeStStm(ALetBeStStm statement, Environment env)
			throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseALetStm(ALetStm statement, Environment env) throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAReturnStm(AReturnStm statement, Environment env)
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
	public PTypeSet defaultSSimpleBlockStm(SSimpleBlockStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);

		for (PStm stmt : statement.getStatements())
		{
			types.addAll(stmt.apply(THIS, env));
		}

		return types;
	}

	@Override
	public PTypeSet caseATixeStm(ATixeStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS, env));

		for (ATixeStmtAlternative tsa : statement.getTraps())
		{
			types.addAll(exitCheck(tsa, env));
		}

		return types;
	}

	private PTypeSet exitCheck(ATixeStmtAlternative tsa, Environment env)
			throws AnalysisException
	{
		return tsa.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet caseATrapStm(ATrapStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = new PTypeSet(af);
		types.addAll(statement.getBody().apply(THIS, env));
		types.addAll(statement.getWith().apply(THIS, env));
		return types;
	}

	@Override
	public PTypeSet caseAWhileStm(AWhileStm statement, Environment env) throws AnalysisException
	{
		return statement.getStatement().apply(THIS, env);
	}

	@Override
	public PTypeSet defaultPStm(PStm statement, Environment env) throws AnalysisException
	{
		return new PTypeSet(af);
	}

	@Override
	public PTypeSet createNewReturnValue(INode node, Environment env)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public PTypeSet createNewReturnValue(Object node, Environment env)
	{
		assert false : "should not happen";
		return null;
	}
}
