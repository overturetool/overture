package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.AValueValueImport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;

public class PogImportVisitor extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {
	
	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;
	
	public PogImportVisitor(PogVisitor pogVisitor) {
		this.rootVisitor = pogVisitor;
		
	}
	
	@Override
	public ProofObligationList caseAModuleImports(AModuleImports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAModuleImports(node, question);
	}

	@Override
	public ProofObligationList caseAFromModuleImports(AFromModuleImports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFromModuleImports(node, question);
	}

	@Override
	public ProofObligationList defaultPImport(PImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPImport(node, question);
	}

	@Override
	public ProofObligationList caseAAllImport(AAllImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAllImport(node, question);
	}

	@Override
	public ProofObligationList caseATypeImport(ATypeImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeImport(node, question);
	}

	@Override
	public ProofObligationList caseSValueImport(SValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSValueImport(node, question);
	}

	@Override
	public ProofObligationList defaultSValueImport(SValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAValueValueImport(AValueValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAValueValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionValueImport(
			AFunctionValueImport node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAOperationValueImport(
			AOperationValueImport node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationValueImport(node, question);
	}
	
}
