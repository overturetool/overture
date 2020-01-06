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
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
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
import org.overture.ast.statements.ANotYetSpecifiedStm;
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
import org.overture.config.Settings;
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
		return statement.getExp().apply(THIS, env);
	}

	@Override
	public PTypeSet caseACallStm(ACallStm statement, Environment env) throws AnalysisException
	{
		PTypeSet result = af.createPExpAssistant(af).exitCheck(statement.getArgs(), env);

		PDefinition opdef = env.findName(statement.getName(), NameScope.GLOBAL);
		boolean overridable = Settings.dialect != Dialect.VDM_SL &&
				opdef != null && !(opdef.getAccess().getAccess() instanceof APrivateAccess);

		if (opdef != null && !overridable)
		{
			if (opdef instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition explop = (AExplicitOperationDefinition)opdef;

				if (explop.getPossibleExceptions() == null)
				{
					explop.setPossibleExceptions(new PTypeSet(af));
					explop.setPossibleExceptions(explop.getBody().apply(THIS, env));
				}

				result.addAll(explop.getPossibleExceptions());
				return result;
			}
			else if (opdef instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition implop = (AImplicitOperationDefinition)opdef;

				if (implop.getPossibleExceptions() == null)
				{
					if (implop.getBody() != null)
					{
						implop.setPossibleExceptions(new PTypeSet(af));
						implop.setPossibleExceptions(implop.getBody().apply(THIS, env));
					}
					else
					{
						return new PTypeSet(af);
					}
				}

				result.addAll(implop.getPossibleExceptions());
				return result;
			}
		}

		result.add(AstFactory.newAUnknownType(statement.getLocation()));
		return result;
	}

	@Override
	public PTypeSet caseACallObjectStm(ACallObjectStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet result = af.createPExpAssistant(af).exitCheck(statement.getArgs(), env);

		PDefinition fdef = statement.getFieldDef();
		boolean overridable = Settings.dialect != Dialect.VDM_SL &&
				fdef != null && !(fdef.getAccess().getAccess() instanceof APrivateAccess);

		if (fdef != null && !overridable)
		{
			if (fdef instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition explop = (AExplicitOperationDefinition)fdef;

				if (explop.getPossibleExceptions() == null)
				{
					explop.setPossibleExceptions(new PTypeSet(af));
					explop.setPossibleExceptions(explop.getBody().apply(THIS, env));
				}

				result.addAll(explop.getPossibleExceptions());
				return result;
			}
			else if (fdef instanceof AImplicitOperationDefinition)
			{
				AImplicitOperationDefinition implop = (AImplicitOperationDefinition)fdef;

				if (implop.getPossibleExceptions() == null)
				{
					if (implop.getBody() != null)
					{
						implop.setPossibleExceptions(new PTypeSet(af));
						implop.setPossibleExceptions(implop.getBody().apply(THIS, env));
					}
					else
					{
						return new PTypeSet(af);
					}
				}

				result.addAll(implop.getPossibleExceptions());
				return result;
			}
		}

		result.add(AstFactory.newAUnknownType(statement.getLocation()));
		return result;
	}

	@Override
	public PTypeSet caseACasesStm(ACasesStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = statement.getExp().apply(THIS, env);

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
		PTypeSet result = statement.getElseIf().apply(THIS, env);
		result.addAll(statement.getThenStm().apply(THIS, env));
		return result;
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
		PTypeSet result = statement.getSet().apply(THIS, env);
		result.addAll(statement.getStatement().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAForIndexStm(AForIndexStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet result = statement.getFrom().apply(THIS, env);
		result.addAll(statement.getTo().apply(THIS, env));
		if (statement.getBy() != null) result.addAll(statement.getBy().apply(THIS, env));
		result.addAll(statement.getStatement().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAForPatternBindStm(AForPatternBindStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet result = statement.getExp().apply(THIS, env);
		result.addAll(statement.getStatement().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAIfStm(AIfStm statement, Environment env) throws AnalysisException
	{
		PTypeSet types = statement.getIfExp().apply(THIS, env);
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
	public PTypeSet caseANotYetSpecifiedStm(ANotYetSpecifiedStm statement,
			Environment question) throws AnalysisException
	{
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()), af);
	}

	@Override
	public PTypeSet caseALetBeStStm(ALetBeStStm statement, Environment env)
			throws AnalysisException
	{
		PTypeSet results = statement.getStatement().apply(THIS, env);
		if (statement.getSuchThat() != null) results.addAll(statement.getSuchThat().apply(THIS, env));

		if (statement.getDef() != null && statement.getDef().getDefs() != null)
		{
			for (PDefinition d: statement.getDef().getDefs())
			{
				if (d instanceof AValueDefinition)
				{
					AValueDefinition vd = (AValueDefinition)d;
					results.addAll(vd.apply(THIS, env));
				}
			}
		}

		return results;
	}

	@Override
	public PTypeSet caseALetStm(ALetStm statement, Environment env) throws AnalysisException
	{
		PTypeSet results = statement.getStatement().apply(THIS, env);

		if (statement.getLocalDefs() != null)
		{
			for (PDefinition d: statement.getLocalDefs())
			{
				if (d instanceof AValueDefinition)
				{
					AValueDefinition vd = (AValueDefinition)d;
					results.addAll(vd.getExpression().apply(THIS, env));
				}
			}
		}

		return results;
	}

	@Override
	public PTypeSet caseAReturnStm(AReturnStm statement, Environment env)
			throws AnalysisException
	{
		if (statement.getExpression() != null)
		{
			return statement.getExpression().apply(THIS, env);
		}
		else
		{
			return new PTypeSet(af);
		}
	}

	@Override
	public PTypeSet caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			Environment question) throws AnalysisException
	{
		PTypeSet types = super.caseABlockSimpleBlockStm(node, question);

		for (PDefinition d: node.getAssignmentDefs())
		{
			if (d instanceof AAssignmentDefinition)
			{
				AAssignmentDefinition ad = (AAssignmentDefinition)d;
				types.addAll(ad.getExpression().apply(THIS, question));
			}
		}

		return types;
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
		PTypeSet result = statement.getExp().apply(THIS, env);
		result.addAll(statement.getStatement().apply(THIS, env));
		return result;
	}
	
	/**
	 * Expressions below here...
	 */

	@Override
	public PTypeSet caseAApplyExp(AApplyExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getRoot().apply(THIS, env);
		result.addAll(af.createPExpAssistant(af).exitCheck(exp.getArgs(), env));
		return result;
	}

	@Override
	public PTypeSet defaultSBinaryExp(SBinaryExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getLeft().apply(THIS, env);
		result.addAll(exp.getRight().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseACasesExp(ACasesExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getExpression().apply(THIS, env);

		for (ACaseAlternative c : exp.getCases())
		{
			result.addAll(c.getResult().apply(THIS, env));
		}

		if (exp.getOthers() != null) result.addAll(exp.getOthers().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseADefExp(ADefExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);

		for (PDefinition def: exp.getLocalDefs())
		{
			if (def instanceof AEqualsDefinition)
			{
				AEqualsDefinition eqdef = (AEqualsDefinition)def;
				result.addAll(eqdef.getTest().apply(THIS, env));
			}
		}

		result.addAll(exp.getExpression().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAElseIfExp(AElseIfExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getElseIf().apply(THIS, env);
		result.addAll(exp.getThen().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAExistsExp(AExistsExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = af.createPExpAssistant(af).exitCheckMultipleBindList(exp.getBindList(), env);
		result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAExists1Exp(AExists1Exp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getBind().apply(THIS, env);
		result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAFieldExp(AFieldExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getObject().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAFieldNumberExp(AFieldNumberExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getTuple().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAForAllExp(AForAllExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = af.createPExpAssistant(af).exitCheckMultipleBindList(exp.getBindList(), env);
		result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAFuncInstatiationExp(AFuncInstatiationExp exp,
			Environment env) throws AnalysisException
	{
		return exp.getFunction().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAIfExp(AIfExp exp, Environment env) throws AnalysisException
	{
		PTypeSet result = exp.getTest().apply(THIS, env);
		result.addAll(exp.getThen().apply(THIS, env));

		for (AElseIfExp stmt : exp.getElseList())
		{
			result.addAll(stmt.apply(THIS, env));
		}

		if (exp.getElse() != null)
		{
			result.addAll(exp.getElse().apply(THIS, env));
		}

		return result;
	}

	@Override
	public PTypeSet caseAIotaExp(AIotaExp exp, Environment env)
			throws AnalysisException
	{

		PTypeSet result = exp.getBind().apply(THIS, env);
		result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAIsExp(AIsExp exp, Environment env) throws AnalysisException
	{
		return exp.getTest().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAIsOfBaseClassExp(AIsOfBaseClassExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getExp().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAIsOfClassExp(AIsOfClassExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getExp().apply(THIS, env);
	}

	@Override
	public PTypeSet caseALambdaExp(ALambdaExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getExpression().apply(THIS, env);
		result.addAll(exp.getExpression().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseALetBeStExp(ALetBeStExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getBind().apply(THIS, env);

		if (exp.getSuchThat() != null)
		{
			result.addAll(exp.getSuchThat().apply(THIS, env));
		}

		result.addAll(exp.getValue().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseALetDefExp(ALetDefExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet results = exp.getExpression().apply(THIS, env);

		if (exp.getLocalDefs() != null)
		{
			for (PDefinition d: exp.getLocalDefs())
			{
				if (d instanceof AValueDefinition)
				{
					AValueDefinition vd = (AValueDefinition)d;
					results.addAll(vd.getExpression().apply(THIS, env));
				}
			}
		}

		return results;
	}

	@Override
	public PTypeSet caseAMapletExp(AMapletExp node, Environment env) throws AnalysisException
	{
		PTypeSet result = node.getLeft().apply(THIS, env);
		result.addAll(node.getRight().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseAMapCompMapExp(AMapCompMapExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getFirst().apply(THIS, env);
		result.addAll(af.createPExpAssistant(af).exitCheckMultipleBindList(exp.getBindings(), env));
		if (exp.getPredicate() != null) exp.getPredicate().apply(THIS, env);
		return result;
	}

	@Override
	public PTypeSet caseAMapEnumMapExp(AMapEnumMapExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);

		for (AMapletExp m : exp.getMembers())
		{
			result.addAll(m.apply(THIS, env));
		}

		return result;
	}

	@Override
	public PTypeSet caseAMkBasicExp(AMkBasicExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getArg().apply(THIS, env);
	}

	@Override
	public PTypeSet caseAMkTypeExp(AMkTypeExp exp, Environment env)
			throws AnalysisException
	{
		return af.createPExpAssistant(af).exitCheck(exp.getArgs(), env);
	}

	@Override
	public PTypeSet caseAMuExp(AMuExp exp, Environment env) throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);

		for (ARecordModifier rm: exp.getModifiers())
		{
			result.addAll(rm.apply(THIS, env));
		}

		result.addAll(exp.getRecord().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseANarrowExp(ANarrowExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getTest().apply(THIS, env);
	}

	@Override
	public PTypeSet caseANewExp(ANewExp exp, Environment env)
			throws AnalysisException
	{
		return af.createPExpAssistant(af).exitCheck(exp.getArgs(), env);
	}

	@Override
	public PTypeSet caseAPostOpExp(APostOpExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getPostexpression().apply(THIS, env);
	}

	@Override
	public PTypeSet caseASameBaseClassExp(ASameBaseClassExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getLeft().apply(THIS, env);
		result.addAll(exp.getRight().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseASameClassExp(ASameClassExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getLeft().apply(THIS, env);
		result.addAll(exp.getRight().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseASeqCompSeqExp(ASeqCompSeqExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getFirst().apply(THIS, env);

		if (exp.getSeqBind() != null)
		{
			result.addAll(exp.getSeqBind().apply(THIS, env));
		}
		else
		{
			result.addAll(exp.getSetBind().apply(THIS, env));
		}

		if (exp.getPredicate() != null) result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseASeqEnumSeqExp(ASeqEnumSeqExp exp, Environment env)
			throws AnalysisException
	{
		return af.createPExpAssistant(af).exitCheck(exp.getMembers(), env);
	}

	@Override
	public PTypeSet caseASetCompSetExp(ASetCompSetExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getFirst().apply(THIS, env);

		result.addAll(af.createPExpAssistant(af).exitCheckMultipleBindList(exp.getBindings(), env));

		if (exp.getPredicate() != null) result.addAll(exp.getPredicate().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseASetEnumSetExp(ASetEnumSetExp exp, Environment env)
			throws AnalysisException
	{
		return af.createPExpAssistant(af).exitCheck(exp.getMembers(), env);
	}

	@Override
	public PTypeSet caseASetRangeSetExp(ASetRangeSetExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getFirst().apply(THIS, env);
		result.addAll(exp.getLast().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseASubseqExp(ASubseqExp exp, Environment env)
			throws AnalysisException
	{
		PTypeSet result = exp.getSeq().apply(THIS, env);
		result.addAll(exp.getFrom().apply(THIS, env));
		result.addAll(exp.getTo().apply(THIS, env));
		return result;
	}

	@Override
	public PTypeSet caseATupleExp(ATupleExp exp, Environment env)
			throws AnalysisException
	{
		return af.createPExpAssistant(af).exitCheck(exp.getArgs(), env);
	}
	
	@Override
	public PTypeSet caseASetMultipleBind(ASetMultipleBind node,	Environment env) throws AnalysisException
	{
		return node.getSet().apply(this, env);
	}
	
	@Override
	public PTypeSet caseASeqMultipleBind(ASeqMultipleBind node,	Environment env) throws AnalysisException
	{
		return node.getSeq().apply(this, env);
	}

	@Override
	public PTypeSet caseASetBind(ASetBind node, Environment env) throws AnalysisException
	{
		return node.getSet().apply(THIS, env);
	}
	
	@Override
	public PTypeSet caseASeqBind(ASeqBind node, Environment env) throws AnalysisException
	{
		return node.getSeq().apply(THIS, env);
	}
	
	@Override
	public PTypeSet defaultSUnaryExp(SUnaryExp exp, Environment env)
			throws AnalysisException
	{
		return exp.getExp().apply(THIS, env);
	}
	
	@Override
	public PTypeSet defaultPStm(PStm statement, Environment env) throws AnalysisException
	{
		return new PTypeSet(af);
	}

	@Override
	public PTypeSet defaultPExp(PExp expression, Environment env) throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);
		result.add(AstFactory.newAUnknownType(expression.getLocation()));
		return result;
	}
	
	@Override
	public PTypeSet createNewReturnValue(INode node, Environment env)
	{
		return new PTypeSet(af);
	}

	@Override
	public PTypeSet createNewReturnValue(Object node, Environment env)
	{
		return new PTypeSet(af);
	}
}
