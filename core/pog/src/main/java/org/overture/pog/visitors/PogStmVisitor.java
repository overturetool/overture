package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ANonDeterministicSimpleBlockStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;

public class PogStmVisitor extends	QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;
	
	public PogStmVisitor(PogVisitor pogVisitor) {
		this.rootVisitor = pogVisitor;
	}
	
	@Override
	public ProofObligationList caseAAlwaysStm(AAlwaysStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAlwaysStm(node, question);
	}

	@Override
	public ProofObligationList caseAAssignmentStm(AAssignmentStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAssignmentStm(node, question);
	}

	@Override
	public ProofObligationList caseAAtomicStm(AAtomicStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAtomicStm(node, question);
	}

	@Override
	public ProofObligationList caseACallObjectStm(ACallObjectStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACallObjectStm(node, question);
	}

	@Override
	public ProofObligationList caseACallStm(ACallStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACallStm(node, question);
	}

	@Override
	public ProofObligationList caseACasesStm(ACasesStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACasesStm(node, question);
	}

	@Override
	public ProofObligationList caseAClassInvariantStm(AClassInvariantStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassInvariantStm(node, question);
	}

	@Override
	public ProofObligationList caseACyclesStm(ACyclesStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACyclesStm(node, question);
	}

	@Override
	public ProofObligationList caseADurationStm(ADurationStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADurationStm(node, question);
	}

	@Override
	public ProofObligationList caseAElseIfStm(AElseIfStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAElseIfStm(node, question);
	}

	@Override
	public ProofObligationList caseAErrorStm(AErrorStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAErrorStm(node, question);
	}

	@Override
	public ProofObligationList caseAExitStm(AExitStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExitStm(node, question);
	}

	@Override
	public ProofObligationList caseAForAllStm(AForAllStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForAllStm(node, question);
	}

	@Override
	public ProofObligationList caseAForIndexStm(AForIndexStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForIndexStm(node, question);
	}

	@Override
	public ProofObligationList caseAForPatternBindStm(AForPatternBindStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForPatternBindStm(node, question);
	}

	@Override
	public ProofObligationList caseAIfStm(AIfStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIfStm(node, question);
	}

	@Override
	public ProofObligationList caseALetBeStStm(ALetBeStStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetBeStStm(node, question);
	}

	@Override
	public ProofObligationList caseSLetDefStm(SLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSLetDefStm(node, question);
	}

	@Override
	public ProofObligationList defaultSLetDefStm(SLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSLetDefStm(node, question);
	}

	@Override
	public ProofObligationList caseANotYetSpecifiedStm(
			ANotYetSpecifiedStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotYetSpecifiedStm(node, question);
	}

	@Override
	public ProofObligationList caseAReturnStm(AReturnStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAReturnStm(node, question);
	}

	@Override
	public ProofObligationList caseSSimpleBlockStm(SSimpleBlockStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList defaultSSimpleBlockStm(SSimpleBlockStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList caseASkipStm(ASkipStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASkipStm(node, question);
	}

	@Override
	public ProofObligationList caseASpecificationStm(ASpecificationStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASpecificationStm(node, question);
	}

	@Override
	public ProofObligationList caseAStartStm(AStartStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStartStm(node, question);
	}

	@Override
	public ProofObligationList caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubclassResponsibilityStm(node, question);
	}

	@Override
	public ProofObligationList caseATixeStm(ATixeStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATixeStm(node, question);
	}

	@Override
	public ProofObligationList caseATrapStm(ATrapStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATrapStm(node, question);
	}

	@Override
	public ProofObligationList caseAWhileStm(AWhileStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAWhileStm(node, question);
	}

	@Override
	public ProofObligationList caseAPeriodicStm(APeriodicStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPeriodicStm(node, question);
	}

	@Override
	public ProofObligationList caseADefLetDefStm(ADefLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADefLetDefStm(node, question);
	}

	@Override
	public ProofObligationList caseABlockSimpleBlockStm(
			ABlockSimpleBlockStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABlockSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList caseANonDeterministicSimpleBlockStm(
			ANonDeterministicSimpleBlockStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANonDeterministicSimpleBlockStm(node, question);
	}
	
	
}
