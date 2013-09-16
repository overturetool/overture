package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
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
import org.overture.pog.obligation.LetBeExistsObligation;
import org.overture.pog.obligation.PONameContext;
import org.overture.pog.obligation.POScopeContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.StateInvariantObligation;
import org.overture.pog.obligation.SubTypeObligation;
import org.overture.pog.obligation.WhileLoopObligation;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.POException;
import org.overture.pog.utility.PogAssistantFactory;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PogParamStmVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends QuestionAnswerAdaptor<IPOContextStack, IProofObligationList> {

	/**
     * 
     */
	private static final long serialVersionUID = -7303385814876083304L;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor;

    final private IPogAssistantFactory assistantFactory;
	
	public PogParamStmVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor, IPogAssistantFactory assistantFactory) {
		this.rootVisitor = parentVisitor;
		this.mainVisitor = mainVisitor;
		this.assistantFactory=assistantFactory;
	}

	/**
	 * <b>Warning!</b> This constructor is not for use with Overture extensions as it sets several customisable
	 * fields to Overture defaults. Use  {@link #PogParamStmVisitor(QuestionAnswerAdaptor, QuestionAnswerAdaptor, IPogAssistantFactory)} instead
	 * @param parentVisitor
	 */
	
	public PogParamStmVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor) {
		this.rootVisitor = parentVisitor;
		this.mainVisitor = this;
		this.assistantFactory = new PogAssistantFactory();
	}

	@Override
	public IProofObligationList defaultPStm(PStm node, IPOContextStack question) {

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAAlwaysStm(AAlwaysStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = node.getAlways().apply(
					mainVisitor, question);
			obligations.addAll(node.getBody().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAAssignmentStm(AAssignmentStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			if (!node.getInConstructor()
					&& (node.getClassDefinition() != null && node
							.getClassDefinition().getInvariant() != null)
					|| (node.getStateDefinition() != null && node
							.getStateDefinition().getInvExpression() != null)) {
				obligations.add(new StateInvariantObligation(node, question));
			}

			obligations.addAll(node.getTarget().apply(rootVisitor, question));
			obligations.addAll(node.getExp().apply(rootVisitor, question));

			if (!TypeComparator.isSubType(
					question.checkType(node.getExp(), node.getExpType()),
					node.getTargetType())) {
				obligations.add(new SubTypeObligation(node.getExp(), node
						.getTargetType(), node.getExpType(), question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAAtomicStm(AAtomicStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (AAssignmentStm stmt : node.getAssignments()) {
				obligations.addAll(stmt.apply(mainVisitor, question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACallObjectStm(ACallObjectStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (PExp exp : node.getArgs()) {
				obligations.addAll(exp.apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACallStm(ACallStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (PExp exp : node.getArgs()) {
				obligations.addAll(exp.apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACasesStm(ACasesStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();
			boolean hasIgnore = false;

			for (ACaseAlternativeStm alt : node.getCases()) {
				if (alt.getPattern() instanceof AIgnorePattern) {
					hasIgnore = true;
				}

				obligations.addAll(alt.apply(mainVisitor, question));
			}

			if (node.getOthers() != null && !hasIgnore) {
				obligations.addAll(node.getOthers()
						.apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACaseAlternativeStm(
			ACaseAlternativeStm node, IPOContextStack question)
			throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();
			obligations.addAll(node.getResult().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAElseIfStm(AElseIfStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = node.getElseIf().apply(
					rootVisitor, question);
			obligations.addAll(node.getThenStm().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAExitStm(AExitStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			if (node.getExpression() != null) {
				obligations.addAll(node.getExpression().apply(rootVisitor,
						question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForAllStm(AForAllStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = node.getSet().apply(rootVisitor,
					question);
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForIndexStm(AForIndexStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = node.getFrom().apply(rootVisitor,
					question);
			obligations.addAll(node.getTo().apply(rootVisitor, question));

			if (node.getBy() != null) {
				obligations.addAll(node.getBy().apply(rootVisitor, question));
			}

			question.push(new POScopeContext());
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForPatternBindStm(AForPatternBindStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList list = node.getExp().apply(rootVisitor,
					question);

			if (node.getPatternBind().getPattern() != null) {
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ATypeBind) {

				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ASetBind) {
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(rootVisitor, question));
			}

			list.addAll(node.getStatement().apply(mainVisitor, question));
			return list;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAIfStm(AIfStm node, IPOContextStack question)
			throws AnalysisException {
		try {
			IProofObligationList obligations = node.getIfExp().apply(
					rootVisitor, question);
			obligations.addAll(node.getThenStm().apply(mainVisitor, question));

			for (AElseIfStm stmt : node.getElseIf()) {
				obligations.addAll(stmt.apply(mainVisitor, question));
			}

			if (node.getElseStm() != null) {
				obligations.addAll(node.getElseStm().apply(mainVisitor,
						question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseALetBeStStm(ALetBeStStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();
			obligations.add(new LetBeExistsObligation(node, question));
			obligations.addAll(node.getBind().apply(rootVisitor, question));

			if (node.getSuchThat() != null) {
				obligations.addAll(node.getSuchThat().apply(rootVisitor,
						question));
			}

			question.push(new POScopeContext());
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultSLetDefStm(SLetDefStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();
			obligations.addAll(assistantFactory.createPDefinitionAssistant().getProofObligations(
					node.getLocalDefs(), rootVisitor, question));

			question.push(new POScopeContext());
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAReturnStm(AReturnStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			if (node.getExpression() != null) {
				obligations.addAll(node.getExpression().apply(rootVisitor,
						question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	// @Override
	// public IProofObligationList caseSSimpleBlockStm(SSimpleBlockStm node,
	// IPOContextStack question) {
	//
	// IProofObligationList obligations = new ProofObligationList();
	//
	// for (PStm stmt: node.getStatements())
	// {
	// obligations.addAll(stmt.apply(mainVisitor,question));
	// }
	//
	// return obligations;
	// }

	@Override
	public IProofObligationList caseASpecificationStm(ASpecificationStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			if (node.getErrors() != null) {
				for (AErrorCase err : node.getErrors()) {
					obligations.addAll(err.getLeft().apply(rootVisitor,
							question));
					obligations.addAll(err.getRight().apply(rootVisitor,
							question));
				}
			}

			if (node.getPrecondition() != null) {
				obligations.addAll(node.getPrecondition().apply(rootVisitor,
						question));
			}

			if (node.getPostcondition() != null) {
				obligations.addAll(node.getPostcondition().apply(rootVisitor,
						question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAStartStm(AStartStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			return node.getObj().apply(rootVisitor, question);
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATixeStm(ATixeStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (ATixeStmtAlternative alt : node.getTraps()) {
				obligations.addAll(alt.apply(rootVisitor, question));
			}

			obligations.addAll(node.getBody().apply(rootVisitor, question));
			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATrapStm(ATrapStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList list = new ProofObligationList();

			if (node.getPatternBind().getPattern() != null) {
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ATypeBind) {
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ASetBind) {
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(rootVisitor, question));
			}

			list.addAll(node.getWith().apply(rootVisitor, question));
			list.addAll(node.getBody().apply(rootVisitor, question));
			return list;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAWhileStm(AWhileStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();
			obligations.add(new WhileLoopObligation(node, question));
			obligations.addAll(node.getExp().apply(rootVisitor, question));
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseADefLetDefStm(ADefLetDefStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (PDefinition localDef : node.getLocalDefs()) {
				// PDefinitionAssistantTC.get
				question.push(new PONameContext(PDefinitionAssistantTC
						.getVariableNames(localDef)));
				obligations.addAll(localDef.apply(rootVisitor, question));
				question.pop();
			}

			question.push(new POScopeContext());
			obligations
					.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	public IProofObligationList defaultSSimpleBlockStm(SSimpleBlockStm node,
			IPOContextStack question) throws AnalysisException {
		try {
			IProofObligationList obligations = new ProofObligationList();

			for (PStm stmt : node.getStatements()) {
				obligations.addAll(stmt.apply(mainVisitor, question));
			}

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseABlockSimpleBlockStm(
			ABlockSimpleBlockStm node, IPOContextStack question)
			throws AnalysisException {
		try {
			IProofObligationList obligations = assistantFactory.createPDefinitionAssistant()
					.getProofObligations(node.getAssignmentDefs(), rootVisitor,
							question);

			question.push(new POScopeContext());
			obligations.addAll(defaultSSimpleBlockStm(node, question));
			question.pop();

			return obligations;
		} catch (Exception e) {
			throw new POException(node, e.getMessage());
		}
	}

}
