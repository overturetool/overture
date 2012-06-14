package org.overture.pog.visitor;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.pog.assistant.PDefinitionAssistantPOG;
import org.overture.pog.obligation.LetBeExistsObligation;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.PONameContext;
import org.overture.pog.obligation.POScopeContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.StateInvariantObligation;
import org.overture.pog.obligation.SubTypeObligation;
import org.overture.pog.obligation.WhileLoopObligation;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PogStmVisitor extends
		QuestionAnswerAdaptor<POContextStack, ProofObligationList>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6212386772005972258L;
	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;

	public PogStmVisitor(PogVisitor pogVisitor)
	{
		this.rootVisitor = pogVisitor;
	}

	@Override
	public ProofObligationList defaultPStm(PStm node, POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAAlwaysStm(AAlwaysStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = node.getAlways().apply(this, question);
		obligations.addAll(node.getBody().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseAAssignmentStm(AAssignmentStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		if (!node.getInConstructor()
				&& (node.getClassDefinition() != null && node.getClassDefinition().getInvariant() != null)
				|| (node.getStateDefinition() != null && node.getStateDefinition().getInvExpression() != null))
		{
			obligations.add(new StateInvariantObligation(node, question));
		}

		obligations.addAll(node.getTarget().apply(rootVisitor, question));
		obligations.addAll(node.getExp().apply(rootVisitor, question));

		if (!TypeComparator.isSubType(question.checkType(node.getExp(), node.getExpType()), node.getTargetType()))
		{
			obligations.add(new SubTypeObligation(node.getExp(), node.getTargetType(), node.getExpType(), question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAAtomicStm(AAtomicStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		for (AAssignmentStm stmt : node.getAssignments())
		{
			obligations.addAll(stmt.apply(this, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseACallObjectStm(ACallObjectStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PExp exp : node.getArgs())
		{
			obligations.addAll(exp.apply(rootVisitor, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseACallStm(ACallStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PExp exp : node.getArgs())
		{
			obligations.addAll(exp.apply(rootVisitor, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseACasesStm(ACasesStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();
		boolean hasIgnore = false;

		for (ACaseAlternativeStm alt : node.getCases())
		{
			if (alt.getPattern() instanceof AIgnorePattern)
			{
				hasIgnore = true;
			}

			obligations.addAll(alt.apply(this, question));
		}

		if (node.getOthers() != null && !hasIgnore)
		{
			obligations.addAll(node.getOthers().apply(rootVisitor, question));
		}

		return obligations;

	}

	@Override
	public ProofObligationList caseACaseAlternativeStm(
			ACaseAlternativeStm node, POContextStack question) throws Throwable
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(node.getResult().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseAElseIfStm(AElseIfStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = node.getElseIf().apply(rootVisitor, question);
		obligations.addAll(node.getThenStm().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseAExitStm(AExitStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		if (node.getExpression() != null)
		{
			obligations.addAll(node.getExpression().apply(rootVisitor, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAForAllStm(AForAllStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = node.getSet().apply(rootVisitor, question);
		obligations.addAll(node.getStatement().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseAForIndexStm(AForIndexStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = node.getFrom().apply(rootVisitor, question);
		obligations.addAll(node.getTo().apply(rootVisitor, question));

		if (node.getBy() != null)
		{
			obligations.addAll(node.getBy().apply(rootVisitor, question));
		}

		question.push(new POScopeContext());
		obligations.addAll(node.getStatement().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList caseAForPatternBindStm(AForPatternBindStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList list = node.getExp().apply(rootVisitor, question);

		if (node.getPatternBind().getPattern() != null)
		{
			// Nothing to do
		} else if (node.getPatternBind().getBind() instanceof ATypeBind)
		{

			// Nothing to do
		} else if (node.getPatternBind().getBind() instanceof ASetBind)
		{
			ASetBind bind = (ASetBind) node.getPatternBind().getBind();
			list.addAll(bind.getSet().apply(rootVisitor, question));
		}

		list.addAll(node.getStatement().apply(this, question));
		return list;
	}

	@Override
	public ProofObligationList caseAIfStm(AIfStm node, POContextStack question) throws Throwable
	{

		ProofObligationList obligations = node.getIfExp().apply(rootVisitor, question);
		obligations.addAll(node.getThenStm().apply(this, question));

		for (AElseIfStm stmt : node.getElseIf())
		{
			obligations.addAll(stmt.apply(this, question));
		}

		if (node.getElseStm() != null)
		{
			obligations.addAll(node.getElseStm().apply(this, question));
		}

		return obligations;

	}

	@Override
	public ProofObligationList caseALetBeStStm(ALetBeStStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new LetBeExistsObligation(node, question));
		obligations.addAll(node.getBind().apply(rootVisitor, question));

		if (node.getSuchThat() != null)
		{
			obligations.addAll(node.getSuchThat().apply(rootVisitor, question));
		}

		question.push(new POScopeContext());
		obligations.addAll(node.getStatement().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList defaultSLetDefStm(SLetDefStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		obligations.addAll(PDefinitionAssistantPOG.getProofObligations(node.getLocalDefs(), rootVisitor, question));

		question.push(new POScopeContext());
		obligations.addAll(node.getStatement().apply(this, question));
		question.pop();

		return obligations;

	}

	@Override
	public ProofObligationList caseAReturnStm(AReturnStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		if (node.getExpression() != null)
		{
			obligations.addAll(node.getExpression().apply(rootVisitor, question));
		}

		return obligations;
	}

	// @Override
	// public ProofObligationList caseSSimpleBlockStm(SSimpleBlockStm node,
	// POContextStack question) {
	//
	// ProofObligationList obligations = new ProofObligationList();
	//
	// for (PStm stmt: node.getStatements())
	// {
	// obligations.addAll(stmt.apply(this,question));
	// }
	//
	// return obligations;
	// }

	@Override
	public ProofObligationList caseASpecificationStm(ASpecificationStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		if (node.getErrors() != null)
		{
			for (AErrorCase err : node.getErrors())
			{
				obligations.addAll(err.getLeft().apply(rootVisitor, question));
				obligations.addAll(err.getRight().apply(rootVisitor, question));
			}
		}

		if (node.getPrecondition() != null)
		{
			obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
		}

		if (node.getPostcondition() != null)
		{
			obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
		}

		return obligations;

	}

	@Override
	public ProofObligationList caseAStartStm(AStartStm node,
			POContextStack question) throws Throwable
	{

		return node.getObj().apply(rootVisitor, question);
	}

	@Override
	public ProofObligationList caseATixeStm(ATixeStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		for (ATixeStmtAlternative alt : node.getTraps())
		{
			obligations.addAll(alt.apply(rootVisitor, question));
		}

		obligations.addAll(node.getBody().apply(rootVisitor, question));
		return obligations;

	}

	@Override
	public ProofObligationList caseATrapStm(ATrapStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList list = new ProofObligationList();

		if (node.getPatternBind().getPattern() != null)
		{
			// Nothing to do
		} else if (node.getPatternBind().getBind() instanceof ATypeBind)
		{
			// Nothing to do
		} else if (node.getPatternBind().getBind() instanceof ASetBind)
		{
			ASetBind bind = (ASetBind) node.getPatternBind().getBind();
			list.addAll(bind.getSet().apply(rootVisitor, question));
		}

		list.addAll(node.getWith().apply(rootVisitor, question));
		list.addAll(node.getBody().apply(rootVisitor, question));
		return list;

	}

	@Override
	public ProofObligationList caseAWhileStm(AWhileStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new WhileLoopObligation(node, question));
		obligations.addAll(node.getExp().apply(rootVisitor, question));
		obligations.addAll(node.getStatement().apply(this, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseADefLetDefStm(ADefLetDefStm node,
			POContextStack question) throws Throwable
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition localDef : node.getLocalDefs())
		{
			// PDefinitionAssistantTC.get
			question.push(new PONameContext(PDefinitionAssistantTC.getVariableNames(localDef)));
			obligations.addAll(localDef.apply(rootVisitor, question));
			question.pop();
		}

		question.push(new POScopeContext());
		obligations.addAll(node.getStatement().apply(this, question));
		question.pop();

		return obligations;
	}

	public ProofObligationList defaultSSimpleBlockStm(SSimpleBlockStm node,
			POContextStack question) throws Throwable
	{
		ProofObligationList obligations = new ProofObligationList();

		for (PStm stmt : node.getStatements())
		{
			obligations.addAll(stmt.apply(this, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseABlockSimpleBlockStm(
			ABlockSimpleBlockStm node, POContextStack question) throws Throwable
	{
		ProofObligationList obligations = PDefinitionAssistantPOG.getProofObligations(node.getAssignmentDefs(), rootVisitor, question);

		question.push(new POScopeContext());
		obligations.addAll(defaultSSimpleBlockStm(node, question));
		question.pop();

		return obligations;
	}

}
