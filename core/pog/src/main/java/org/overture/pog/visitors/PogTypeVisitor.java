package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;

public class PogTypeVisitor extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;
	
	public PogTypeVisitor(PogVisitor pogVisitor) {
		this.rootVisitor = pogVisitor;
	}
	
	@Override
	public ProofObligationList caseSBasicType(SBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSBasicType(node, question);
	}

	@Override
	public ProofObligationList defaultSBasicType(SBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSBasicType(node, question);
	}

	@Override
	public ProofObligationList caseABracketType(ABracketType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABracketType(node, question);
	}

	@Override
	public ProofObligationList caseAClassType(AClassType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassType(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionType(AFunctionType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionType(node, question);
	}

	@Override
	public ProofObligationList caseSInvariantType(SInvariantType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSInvariantType(node, question);
	}

	@Override
	public ProofObligationList defaultSInvariantType(SInvariantType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseSMapType(SMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSMapType(node, question);
	}

	@Override
	public ProofObligationList defaultSMapType(SMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSMapType(node, question);
	}

	@Override
	public ProofObligationList caseAOperationType(AOperationType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationType(node, question);
	}

	@Override
	public ProofObligationList caseAOptionalType(AOptionalType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOptionalType(node, question);
	}

	@Override
	public ProofObligationList caseAParameterType(AParameterType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAParameterType(node, question);
	}

	@Override
	public ProofObligationList caseAProductType(AProductType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAProductType(node, question);
	}

	@Override
	public ProofObligationList caseAQuoteType(AQuoteType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAQuoteType(node, question);
	}

	@Override
	public ProofObligationList caseSSeqType(SSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSeqType(node, question);
	}

	@Override
	public ProofObligationList defaultSSeqType(SSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSeqType(node, question);
	}

	@Override
	public ProofObligationList caseASetType(ASetType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetType(node, question);
	}

	@Override
	public ProofObligationList caseAUndefinedType(AUndefinedType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUndefinedType(node, question);
	}

	@Override
	public ProofObligationList caseAUnionType(AUnionType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnionType(node, question);
	}

	@Override
	public ProofObligationList caseAUnknownType(AUnknownType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnknownType(node, question);
	}

	@Override
	public ProofObligationList caseAUnresolvedType(AUnresolvedType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnresolvedType(node, question);
	}

	@Override
	public ProofObligationList caseAVoidReturnType(AVoidReturnType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAVoidReturnType(node, question);
	}

	@Override
	public ProofObligationList caseAVoidType(AVoidType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAVoidType(node, question);
	}

	@Override
	public ProofObligationList caseASeqSeqType(ASeqSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqSeqType(node, question);
	}

	@Override
	public ProofObligationList caseASeq1SeqType(ASeq1SeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeq1SeqType(node, question);
	}

	@Override
	public ProofObligationList caseAInMapMapType(AInMapMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInMapMapType(node, question);
	}

	@Override
	public ProofObligationList caseAMapMapType(AMapMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapMapType(node, question);
	}

	@Override
	public ProofObligationList caseANamedInvariantType(
			ANamedInvariantType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANamedInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseARecordInvariantType(
			ARecordInvariantType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseABooleanBasicType(ABooleanBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABooleanBasicType(node, question);
	}

	@Override
	public ProofObligationList caseACharBasicType(ACharBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACharBasicType(node, question);
	}

	@Override
	public ProofObligationList caseSNumericBasicType(SNumericBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList defaultSNumericBasicType(SNumericBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseATokenBasicType(ATokenBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATokenBasicType(node, question);
	}

	@Override
	public ProofObligationList caseAIntNumericBasicType(
			AIntNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIntNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseANatOneNumericBasicType(
			ANatOneNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANatOneNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseANatNumericBasicType(
			ANatNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANatNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseARationalNumericBasicType(
			ARationalNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARationalNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseARealNumericBasicType(
			ARealNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARealNumericBasicType(node, question);
	}
}
