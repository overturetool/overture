package org.overture.interpreter.runtime;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AWhileStm;

public class DecisionStructuresVisitor extends DepthFirstAnalysisAdaptor {
	private GenerateTestCases gtc = null;

	public DecisionStructuresVisitor(String fname) {
		this.gtc = new GenerateTestCases(fname);
	}

	public GenerateTestCases getGTC() {
		return this.gtc;

	}

	@Override
	public void caseAElseIfStm(AElseIfStm node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAElseIfStm(node);

	}

	@Override
	public void caseAIfStm(AIfStm node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAIfStm(node);
	}

	@Override
	public void caseAExists1Exp(AExists1Exp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAExists1Exp(node);
	}

	@Override
	public void caseAExistsExp(AExistsExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAExistsExp(node);
	}

	@Override
	public void caseAForAllExp(AForAllExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAForAllExp(node);
	}

	@Override
	public void caseAPostOpExp(APostOpExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAPostOpExp(node);
	}

	@Override
	public void caseAPreOpExp(APreOpExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAPreOpExp(node);
	}

	@Override
	public void caseAForAllStm(AForAllStm node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAForAllStm(node);
	}

	@Override
	public void caseAWhileStm(AWhileStm node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAWhileStm(node);
	}

	@Override
	public void caseAElseIfExp(AElseIfExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAElseIfExp(node);
	}

	@Override
	public void caseAIfExp(AIfExp node) throws AnalysisException {
		if (!gtc.visited_nodes.contains(node.getLocation()))
			node.apply(gtc);
		super.caseAIfExp(node);
	}

}
