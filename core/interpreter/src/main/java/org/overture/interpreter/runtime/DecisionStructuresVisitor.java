package org.overture.interpreter.runtime;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AWhileStm;

public class DecisionStructuresVisitor extends DepthFirstAnalysisAdaptor{
	 private GenerateTestCases gtc; 
	 
	 public DecisionStructuresVisitor(String fname){
		 this.gtc = new GenerateTestCases(fname);
	 }
	 
	 public GenerateTestCases getGTC(){
		 return this.gtc;
	 }
	 
	 @Override
		public void caseAElseIfStm(AElseIfStm node)
				throws AnalysisException {
			node.apply(gtc);
			super.caseAElseIfStm(node);
			
		}
		@Override
		public void caseAIfStm(AIfStm node)
				throws AnalysisException {
			node.apply(gtc);
			super.caseAIfStm(node);
		}

		@Override
		public void caseAWhileStm(AWhileStm node)
				throws AnalysisException {
			node.apply(gtc);
			super.caseAWhileStm(node);
		}
		@Override
		public void caseAElseIfExp(AElseIfExp node)
				throws AnalysisException {
			node.apply(gtc);
			super.caseAElseIfExp(node);
		}
		@Override
		public void caseAIfExp(AIfExp node)
				throws AnalysisException {
			node.apply(gtc);
			super.caseAIfExp(node);
		}
		
}
