package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;

public class PogPatternVisitor extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;
	
	public PogPatternVisitor(PogVisitor pogVisitor) {
		this.rootVisitor = pogVisitor;
	}
	
	@Override
	public ProofObligationList caseABooleanPattern(ABooleanPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABooleanPattern(node, question);
	}

	@Override
	public ProofObligationList caseACharacterPattern(ACharacterPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACharacterPattern(node, question);
	}

	@Override
	public ProofObligationList caseAConcatenationPattern(
			AConcatenationPattern node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAConcatenationPattern(node, question);
	}

	@Override
	public ProofObligationList caseAExpressionPattern(AExpressionPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExpressionPattern(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierPattern(AIdentifierPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierPattern(node, question);
	}

	@Override
	public ProofObligationList caseAIgnorePattern(AIgnorePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIgnorePattern(node, question);
	}

	@Override
	public ProofObligationList caseAIntegerPattern(AIntegerPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIntegerPattern(node, question);
	}

	@Override
	public ProofObligationList caseANilPattern(ANilPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANilPattern(node, question);
	}

	@Override
	public ProofObligationList caseAQuotePattern(AQuotePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAQuotePattern(node, question);
	}

	@Override
	public ProofObligationList caseARealPattern(ARealPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARealPattern(node, question);
	}

	@Override
	public ProofObligationList caseARecordPattern(ARecordPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordPattern(node, question);
	}

	@Override
	public ProofObligationList caseASeqPattern(ASeqPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqPattern(node, question);
	}

	@Override
	public ProofObligationList caseASetPattern(ASetPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetPattern(node, question);
	}

	@Override
	public ProofObligationList caseAStringPattern(AStringPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStringPattern(node, question);
	}

	@Override
	public ProofObligationList caseATuplePattern(ATuplePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATuplePattern(node, question);
	}

	@Override
	public ProofObligationList caseAUnionPattern(AUnionPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnionPattern(node, question);
	}
}
