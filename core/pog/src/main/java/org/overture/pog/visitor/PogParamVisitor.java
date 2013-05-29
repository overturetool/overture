package org.overture.pog.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.PModifier;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PExports;
import org.overture.ast.modules.PImports;
import org.overture.ast.modules.PModules;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PPair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.PPatternBind;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.PCase;
import org.overture.ast.statements.PClause;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PField;
import org.overture.ast.types.PType;
import org.overture.pog.assistant.PDefinitionAssistantPOG;
import org.overture.pog.obligation.POCaseContext;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.PONotCaseContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SeqApplyObligation;
import org.overture.pog.util.POException;

/**
 * This is the proof obligation visitor climbs through the AST and builds the
 * list of proof obligations the given program exhibits. References: [1]
 * http://wiki.overturetool.org/images/9/95/VDM10_lang_man.pdf for BNF
 * definitions. This work is based on previous work by Nick Battle in the VDMJ
 * package.
 * 
 * @author Overture team
 * @param <Q>
 * @param <A>
 * @since 1.0
 */
public class PogParamVisitor<Q extends POContextStack, A extends ProofObligationList>
		extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	/**
     * 
     */
	private static final long serialVersionUID = 1671456307479822942L;
	private PogExpVisitor pogExpVisitor = new PogExpVisitor(this);
	private PogStmVisitor pogStmVisitor = new PogStmVisitor(this);
	private PogDefinitionVisitor pogDefinitionVisitor = new PogDefinitionVisitor(
			this);

	@Override
	// See [1] pg. 167 for the definition
	public ProofObligationList caseAModuleModules(AModuleModules node,
			POContextStack question) throws AnalysisException {
		return PDefinitionAssistantPOG.getProofObligations(node.getDefs(),
				pogDefinitionVisitor, question);

	}

	@Override
	public ProofObligationList defaultPExp(PExp node, POContextStack question)
			throws AnalysisException {

		return node.apply(pogExpVisitor, question);
	}

	@Override
	public ProofObligationList defaultPModifier(PModifier node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseACaseAlternative(ACaseAlternative node,
			POContextStack question) throws AnalysisException {
		try {
			ProofObligationList obligations = new ProofObligationList();

			question.push(new POCaseContext(node.getPattern(), node.getType(),
					node.getCexp()));
			obligations.addAll(node.getResult().apply(this.pogExpVisitor,
					question));
			question.pop();
			question.push(new PONotCaseContext(node.getPattern(), node
					.getType(), node.getCexp()));

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public ProofObligationList defaultPType(PType node, POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPField(PField node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPAccessSpecifier(PAccessSpecifier node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPPattern(PPattern node,
			POContextStack question) {
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPPair(PPair node, POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPBind(PBind node, POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseASetBind(ASetBind node,
			POContextStack question) throws AnalysisException {
		try {
			return node.getSet().apply(this.pogExpVisitor, question);
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public ProofObligationList caseASetMultipleBind(ASetMultipleBind node,
			POContextStack question) throws AnalysisException {
		try {
			return node.getSet().apply(this.pogExpVisitor, question);
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public ProofObligationList caseATypeMultipleBind(ATypeMultipleBind node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPPatternBind(PPatternBind node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPDefinition(PDefinition node,
			POContextStack question) throws AnalysisException {

		return node.apply(pogDefinitionVisitor, question);
	}

	@Override
	public ProofObligationList defaultPModules(PModules node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPImports(PImports node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPExports(PExports node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPExport(PExport node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPStm(PStm node, POContextStack question)
			throws AnalysisException {

		return node.apply(pogStmVisitor, question);
	}

	@Override
	public ProofObligationList defaultPStateDesignator(PStateDesignator node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, POContextStack question)
			throws AnalysisException {
		try {
			ProofObligationList list = new ProofObligationList();

			if (node.getSeqType() != null) {
				list.add(new SeqApplyObligation(node.getMapseq(),
						node.getExp(), question));
			}

			// Maps are OK, as you can create new map domain entries

			return list;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public ProofObligationList defaultPObjectDesignator(PObjectDesignator node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseATixeStmtAlternative(
			ATixeStmtAlternative node, POContextStack question)
			throws AnalysisException {
		try {
			ProofObligationList list = new ProofObligationList();

			if (node.getPatternBind().getPattern() != null) {
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ATypeBind) {
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ASetBind) {
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(this.pogExpVisitor, question));
			}

			list.addAll(node.getStatement().apply(this.pogStmVisitor, question));
			return list;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public ProofObligationList defaultPClause(PClause node,
			POContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultPCase(PCase node, POContextStack question) {

		return new ProofObligationList();
	}

}
