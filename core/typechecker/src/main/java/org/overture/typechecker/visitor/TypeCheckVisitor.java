package org.overture.typechecker.visitor;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPatternBind;
import org.overture.ast.statements.PAlternativeStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.PStmtAlternative;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;

public class TypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7872917857726484626L;
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcStm = new TypeCheckerStmVisitor(
			this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcExp = new TypeCheckerExpVisitor(
			this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcDefinition = new TypeCheckerDefinitionVisitor(
			this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> patternDefinition = new TypeCheckerPatternVisitor(
			this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcImports = new TypeCheckerImportsVisitor(
			this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcOthers = new TypeCheckerOthersVisitor(
			this);

	public TypeCheckerErrors tcErrors = new TypeCheckerErrors();
	
	@Override
	public PType defaultPPatternBind(PPatternBind node, TypeCheckInfo question) {
		return node.apply(tcOthers, question);
	}

	@Override
	public PType defaultPStateDesignator(PStateDesignator node,
			TypeCheckInfo question) {
		return node.apply(tcOthers, question);
	}

	@Override
	public PType defaultPObjectDesignator(PObjectDesignator node,
			TypeCheckInfo question) {
		return node.apply(tcOthers, question);
	}

	@Override
	public PType defaultPImport(PImport node, TypeCheckInfo question) {
		return node.apply(tcImports, question);
	}

	@Override
	public PType defaultPStm(PStm node, TypeCheckInfo question) {
		return node.apply(tcStm, question);
	}

	@Override
	public PType defaultPAlternativeStm(PAlternativeStm node,
			TypeCheckInfo question) {
		return node.apply(tcStm, question);
	}

	@Override
	public PType defaultPExp(PExp node, TypeCheckInfo question) {
		return node.apply(tcExp, question);
	}

	@Override
	public PType defaultPDefinition(PDefinition node, TypeCheckInfo question) {
		return node.apply(tcDefinition, question);
	}

	@Override
	public PType caseAModuleModules(AModuleModules node, TypeCheckInfo question) {
		for (PDefinition def : node.getDefs()) {
			def.apply(this, question);
		}

		return null;
	}

	@Override
	public PType defaultPMultipleBind(PMultipleBind node, TypeCheckInfo question) {
		return node.apply(patternDefinition, question);
	}

	@Override
	public PType defaultPStmtAlternative(PStmtAlternative node,
			TypeCheckInfo question) {
		return node.apply(tcStm, question);
	}
	
	public PType defaultPTraceDefinition(PTraceDefinition node, TypeCheckInfo question)
	{
		return node.apply(tcDefinition,question);
	}
	
	public PType defaultPTraceCoreDefinition(PTraceCoreDefinition node, TypeCheckInfo question)
	{
		return node.apply(tcDefinition,question);
	}
}
