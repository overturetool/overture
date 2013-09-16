package org.overture.pog.utility;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;

public class StateDesignatorToExpVisitor extends AnswerAdaptor<PExp>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public PExp caseAFieldStateDesignator(AFieldStateDesignator node)
			throws AnalysisException {
		
		// pack this with the output of the visitor recursive calls.
		PExp value = node.getObject().apply(this);
		AFieldExp fieldExp =  new AFieldExp();
		fieldExp.setObject(value);
		fieldExp.setField(node.getField());
		// Not 100%  sure these 2 match
		fieldExp.setMemberName(node.getObjectfield());
		fieldExp.setType(node.getType());
		
		return fieldExp;
	}

	@Override
	public PExp caseAIdentifierStateDesignator(AIdentifierStateDesignator node)
			throws AnalysisException {

		ILexNameToken nameTok = node.getName().clone();
		AVariableExp varExp = new AVariableExp();
		varExp.setName(nameTok);
		varExp.setOriginal(nameTok.getFullName());
		
		return varExp;

	}

	@Override
	public PExp caseAMapSeqStateDesignator(AMapSeqStateDesignator node)
			throws AnalysisException {
			
		AApplyExp applyExp = new AApplyExp();
		applyExp.setRoot(node.getMapseq().apply(this));
		
		List<PExp> args = new LinkedList<PExp>();
		args.add(node.getExp());
		
		applyExp.setArgs(args);
		
		return applyExp;
	}


	

	
	
}
