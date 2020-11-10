package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAnnotatedStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.pog.contexts.AssignmentContext;
import org.overture.pog.contexts.OpPostConditionContext;
import org.overture.pog.contexts.POForAllContext;
import org.overture.pog.contexts.PONameContext;
import org.overture.pog.contexts.POScopeContext;
import org.overture.pog.obligation.LetBeExistsObligation;
import org.overture.pog.obligation.OperationCallObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SeqMembershipObligation;
import org.overture.pog.obligation.SetMembershipObligation;
import org.overture.pog.obligation.StateInvariantObligation;
import org.overture.pog.obligation.TypeCompatibilityObligation;
import org.overture.pog.obligation.WhileLoopObligation;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.POException;
import org.overture.pog.utility.PogAssistantFactory;

public class PogParamStmVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends AbstractPogParamVisitor
{

	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor;
	final private IPogAssistantFactory aF;

	public PogParamStmVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor,
			IPogAssistantFactory assistantFactory)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = mainVisitor;
		this.aF = assistantFactory;
	}

	/**
	 * <b>Warning!</b> This constructor is not for use with Overture extensions as it sets several customisable fields
	 * to Overture defaults. Use
	 * {@link #PogParamStmVisitor(QuestionAnswerAdaptor, QuestionAnswerAdaptor, IPogAssistantFactory)} instead
	 * 
	 * @param parentVisitor
	 */

	public PogParamStmVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = this;
		this.aF = new PogAssistantFactory();
	}

	@Override
	public IProofObligationList defaultPStm(PStm node, IPOContextStack question)
	{

		return new ProofObligationList();
	}
	
	@Override
	public IProofObligationList caseAAnnotatedStm(AAnnotatedStm node, IPOContextStack question)
		throws AnalysisException
	{
		IProofObligationList obligations = beforeAnnotation(node.getAnnotation(), node, question);
		obligations.addAll(node.getStmt().apply(this, question));
		return afterAnnotation(node.getAnnotation(), node, obligations, question);
	}
	

	@Override
	public IProofObligationList caseAAlwaysStm(AAlwaysStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = node.getAlways().apply(mainVisitor, question);
			obligations.addAll(node.getBody().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAAssignmentStm(AAssignmentStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			if (!node.getInConstructor() && node.getClassDefinition() != null
					&& node.getClassDefinition().getInvariant() != null
					|| node.getStateDefinition() != null
					&& node.getStateDefinition().getInvExpression() != null)
			{
				obligations.add(new StateInvariantObligation(node, question, aF));
			}

			obligations.addAll(node.getTarget().apply(rootVisitor, question));
			obligations.addAll(node.getExp().apply(rootVisitor, question));

			if (!aF.getTypeComparator().isSubType(question.checkType(node.getExp(), node.getExpType()), node.getTargetType()))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node.getExp(), node.getTargetType(), node.getExpType(), question, aF);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}

			question.push(new AssignmentContext(node, aF, question));

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAAtomicStm(AAtomicStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			boolean needsInv = false;

			for (AAssignmentStm stmt : node.getAssignments())
			{
				stmt.apply(mainVisitor, question); // collect the assignments
				if (!stmt.getInConstructor()
						&& stmt.getClassDefinition() != null
						&& stmt.getClassDefinition().getInvariant() != null
						|| stmt.getStateDefinition() != null
						&& stmt.getStateDefinition().getInvExpression() != null)
				{
					needsInv = true;
				}
			}
			if (needsInv)
			{
				// FIXME State Inv For Atomic assignments
				obligations.add(new StateInvariantObligation(node, question, aF));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACallObjectStm(ACallObjectStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			for (PExp exp : node.getArgs())
			{
				obligations.addAll(exp.apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACallStm(ACallStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			for (PExp exp : node.getArgs())
			{
				obligations.addAll(exp.apply(rootVisitor, question));
			}

			// stick possible op post_condition in the context
			SOperationDefinitionBase calledOp = node.apply(new GetOpCallVisitor());
			if (calledOp != null)
			{
				if (calledOp.getPrecondition() != null)
				{
					obligations.add(new OperationCallObligation(node, calledOp, question, aF));
				}
				question.push(new OpPostConditionContext(calledOp.getPostdef(), node, calledOp, aF, question));
			}
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACasesStm(ACasesStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			boolean hasIgnore = false;

			for (ACaseAlternativeStm alt : node.getCases())
			{
				if (alt.getPattern() instanceof AIgnorePattern)
				{
					hasIgnore = true;
				}

				obligations.addAll(alt.apply(mainVisitor, question));
			}

			if (node.getOthers() != null && !hasIgnore)
			{
				obligations.addAll(node.getOthers().apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseACaseAlternativeStm(
			ACaseAlternativeStm node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			obligations.addAll(node.getResult().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAElseIfStm(AElseIfStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = node.getElseIf().apply(rootVisitor, question);
			obligations.addAll(node.getThenStm().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAExitStm(AExitStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			if (node.getExpression() != null)
			{
				obligations.addAll(node.getExpression().apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForAllStm(AForAllStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = node.getSet().apply(rootVisitor, question);
			obligations.addAll(node.getStatement().apply(mainVisitor, question));
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForIndexStm(AForIndexStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = node.getFrom().apply(rootVisitor, question);
			obligations.addAll(node.getTo().apply(rootVisitor, question));

			if (node.getBy() != null)
			{
				obligations.addAll(node.getBy().apply(rootVisitor, question));
			}

			question.push(new POScopeContext());
			obligations.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAForPatternBindStm(AForPatternBindStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList list = node.getExp().apply(rootVisitor, question);

			if (node.getPatternBind().getPattern() != null)
			{
				// Nothing to do
			}
			else if (node.getPatternBind().getBind() instanceof ATypeBind)
			{
				// Nothing to do
			}
			else if (node.getPatternBind().getBind() instanceof ASetBind)
			{
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(rootVisitor, question));
				
				question.push(new POForAllContext(bind, node.getExp()));
				list.add(new SetMembershipObligation(bind.getPattern(), bind.getSet(), question, aF));
				question.pop();
			}
			else if (node.getPatternBind().getBind() instanceof ASeqBind)
			{
				ASeqBind bind = (ASeqBind) node.getPatternBind().getBind();
				list.addAll(bind.getSeq().apply(rootVisitor, question));

				question.push(new POForAllContext(bind, node.getExp()));
				list.add(new SeqMembershipObligation(bind.getPattern(), bind.getSeq(), question, aF));
				question.pop();
			}

			list.addAll(node.getStatement().apply(mainVisitor, question));
			return list;
		}
		catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAIfStm(AIfStm node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = node.getIfExp().apply(rootVisitor, question);
			obligations.addAll(node.getThenStm().apply(mainVisitor, question));

			for (AElseIfStm stmt : node.getElseIf())
			{
				obligations.addAll(stmt.apply(mainVisitor, question));
			}

			if (node.getElseStm() != null)
			{
				obligations.addAll(node.getElseStm().apply(mainVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseALetBeStStm(ALetBeStStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			obligations.add(new LetBeExistsObligation(node, question, aF));
			obligations.addAll(node.getBind().apply(rootVisitor, question));

			if (node.getSuchThat() != null)
			{
				obligations.addAll(node.getSuchThat().apply(rootVisitor, question));
			}

			question.push(new POScopeContext());
			obligations.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	// @Override
	// public IProofObligationList caseALetStm(ALetStm node,
	// IPOContextStack question) throws AnalysisException
	// {
	// try
	// {
	// IProofObligationList obligations = new ProofObligationList();
	// obligations.addAll(assistantFactory.createPDefinitionAssistant().getProofObligations(node.getLocalDefs(),
	// rootVisitor, question));
	//
	// question.push(new POScopeContext());
	// obligations.addAll(node.getStatement().apply(mainVisitor, question));
	// question.pop();
	//
	// return obligations;
	// } catch (Exception e)
	// {
	// throw new POException(node, e.getMessage());
	// }
	// }

	@Override
	public IProofObligationList caseAReturnStm(AReturnStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			if (node.getExpression() != null)
			{
				obligations.addAll(node.getExpression().apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
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
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			if (node.getErrors() != null)
			{
				for (AErrorCase err : node.getErrors())
				{
					obligations.addAll(err.getLeft().apply(rootVisitor, question));
					obligations.addAll(err.getRight().apply(rootVisitor, question));
				}
			}

			if (node.getPrecondition() != null)
			{
				obligations.addAll(node.getPrecondition().apply(rootVisitor, question));
			}

			if (node.getPostcondition() != null)
			{
				obligations.addAll(node.getPostcondition().apply(rootVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAStartStm(AStartStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			return node.getObj().apply(rootVisitor, question);
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATixeStm(ATixeStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			for (ATixeStmtAlternative alt : node.getTraps())
			{
				obligations.addAll(alt.apply(rootVisitor, question));
				
				if (alt.getPatternBind().getBind() instanceof ASetBind)
				{
					ASetBind bind = (ASetBind) alt.getPatternBind().getBind();
					obligations.addAll(bind.getSet().apply(rootVisitor, question));
					obligations.add(new SetMembershipObligation(bind.getPattern(), bind.getSet(), question, aF));
				}
				else if (alt.getPatternBind().getBind() instanceof ASeqBind)
				{
					ASeqBind bind = (ASeqBind) alt.getPatternBind().getBind();
					obligations.addAll(bind.getSeq().apply(rootVisitor, question));
					obligations.add(new SeqMembershipObligation(bind.getPattern(), bind.getSeq(), question, aF));
				}
			}

			obligations.addAll(node.getBody().apply(rootVisitor, question));
			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATrapStm(ATrapStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			if (node.getPatternBind().getPattern() != null)
			{
				// Nothing to do
			}
			else if (node.getPatternBind().getBind() instanceof ATypeBind)
			{
				// Nothing to do
			}
			else if (node.getPatternBind().getBind() instanceof ASetBind)
			{
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(rootVisitor, question));
				list.add(new SetMembershipObligation(bind.getPattern(), bind.getSet(), question, aF));
			}
			else if (node.getPatternBind().getBind() instanceof ASeqBind)
			{
				ASeqBind bind = (ASeqBind) node.getPatternBind().getBind();
				list.addAll(bind.getSeq().apply(rootVisitor, question));
				list.add(new SeqMembershipObligation(bind.getPattern(), bind.getSeq(), question, aF));
			}

			list.addAll(node.getWith().apply(rootVisitor, question));
			list.addAll(node.getBody().apply(rootVisitor, question));
			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseAWhileStm(AWhileStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();
			obligations.add(new WhileLoopObligation(node, question, aF));
			obligations.addAll(node.getExp().apply(rootVisitor, question));
			obligations.addAll(node.getStatement().apply(mainVisitor, question));

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseALetStm(ALetStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			for (PDefinition localDef : node.getLocalDefs())
			{
				// PDefinitionAssistantTC.get
				question.push(new PONameContext(aF.createPDefinitionAssistant().getVariableNames(localDef)));
				obligations.addAll(localDef.apply(rootVisitor, question));
				question.pop();
			}

			question.push(new POScopeContext());
			obligations.addAll(node.getStatement().apply(mainVisitor, question));
			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}
	
	public IProofObligationList defaultSSimpleBlockStm(SSimpleBlockStm node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			for (PStm stmt : node.getStatements())
			{
				obligations.addAll(stmt.apply(mainVisitor, question));
			}

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseABlockSimpleBlockStm(
			ABlockSimpleBlockStm node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = aF.createPDefinitionAssistant().getProofObligations(node.getAssignmentDefs(), rootVisitor, question);

			question.push(new POScopeContext());
			obligations.addAll(defaultSSimpleBlockStm(node, question));
			question.pop();

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList createNewReturnValue(INode node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList createNewReturnValue(Object node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}
}
