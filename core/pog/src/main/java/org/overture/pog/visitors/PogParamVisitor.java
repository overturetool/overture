package org.overture.pog.visitors;

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
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASeqMultipleBind;
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
import org.overture.pog.contexts.POCaseContext;
import org.overture.pog.contexts.PONameContext;
import org.overture.pog.contexts.PONotCaseContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SeqApplyObligation;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.POException;
import org.overture.pog.utility.PogAssistantFactory;

/**
 * This is the proof obligation visitor climbs through the AST and builds the list of proof obligations the given
 * program exhibits. References: [1] http://wiki.overturetool.org/images/9/95/VDM10_lang_man.pdf for BNF definitions.
 * This work is based on previous work by Nick Battle in the VDMJ package.
 * 
 * @author Overture team
 * @param <Q>
 * @param <A>
 * @since 1.0
 */
public class PogParamVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends QuestionAnswerAdaptor<IPOContextStack, IProofObligationList>
{

	private PogExpVisitor pogExpVisitor = new PogExpVisitor(this);
	private PogStmVisitor pogStmVisitor = new PogStmVisitor(this);
	private PogDefinitionVisitor pogDefinitionVisitor = new PogDefinitionVisitor(this);

	final private IPogAssistantFactory assistantFactory;

	public PogParamVisitor()
	{
		this.assistantFactory = new PogAssistantFactory();
	}

	/**
	 * <b>Warning!</b> This constructor is not for use with Overture extensions as it sets several customisable fields
	 * to Overture defaults. Use {@link #PogParamVisitor(IPogAssistantFactory)} instead
	 * 
	 * @param assistantFactory
	 */
	public PogParamVisitor(IPogAssistantFactory assistantFactory)
	{
		this.assistantFactory = assistantFactory;
	}

	@Override
	// See [1] pg. 167 for the definition
	public IProofObligationList caseAModuleModules(AModuleModules node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList ipol = new ProofObligationList();
		for (PDefinition p : node.getDefs())
		{
			question.push(new PONameContext(assistantFactory.createPDefinitionAssistant().getVariableNames(p)));
			ipol.addAll(p.apply(this, question));
			question.pop();
			question.clearStateContexts();
		}

		return ipol;

	}

	@Override
	public IProofObligationList defaultPExp(PExp node, IPOContextStack question)
			throws AnalysisException
	{

		return node.apply(pogExpVisitor, question);
	}

	@Override
	public IProofObligationList defaultPModifier(PModifier node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseACaseAlternative(ACaseAlternative node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			IProofObligationList obligations = new ProofObligationList();

			question.push(new POCaseContext(node.getPattern(), node.getType(), node.getCexp(), assistantFactory));
			obligations.addAll(node.getResult().apply(this.pogExpVisitor, question));
			question.pop();
			question.push(new PONotCaseContext(node.getPattern(), node.getType(), node.getCexp(), assistantFactory));

			return obligations;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultPType(PType node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPField(PField node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPAccessSpecifier(PAccessSpecifier node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPPattern(PPattern node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPPair(PPair node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPBind(PBind node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseASetBind(ASetBind node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			return node.getSet().apply(this.pogExpVisitor, question);
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseASeqBind(ASeqBind node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			return node.getSeq().apply(this.pogExpVisitor, question);
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseASetMultipleBind(ASetMultipleBind node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			return node.getSet().apply(this.pogExpVisitor, question);
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseASeqMultipleBind(ASeqMultipleBind node,
			IPOContextStack question) throws AnalysisException
	{
		try
		{
			return node.getSeq().apply(this.pogExpVisitor, question);
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList caseATypeMultipleBind(ATypeMultipleBind node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPPatternBind(PPatternBind node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPDefinition(PDefinition node,
			IPOContextStack question) throws AnalysisException
	{

		return node.apply(pogDefinitionVisitor, question);
	}

	@Override
	public IProofObligationList defaultPModules(PModules node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPImports(PImports node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPExports(PExports node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPExport(PExport node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPStm(PStm node, IPOContextStack question)
			throws AnalysisException
	{

		return node.apply(pogStmVisitor, question);
	}

	@Override
	public IProofObligationList defaultPStateDesignator(PStateDesignator node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			if (node.getSeqType() != null)
			{
				list.add(new SeqApplyObligation(node.getMapseq(), node.getExp(), question, assistantFactory));
			}

			// Maps are OK, as you can create new map domain entries

			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultPObjectDesignator(
			PObjectDesignator node, IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseATixeStmtAlternative(
			ATixeStmtAlternative node, IPOContextStack question)
			throws AnalysisException
	{
		try
		{
			IProofObligationList list = new ProofObligationList();

			if (node.getPatternBind().getPattern() != null)
			{
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ATypeBind)
			{
				// Nothing to do
			} else if (node.getPatternBind().getBind() instanceof ASetBind)
			{
				ASetBind bind = (ASetBind) node.getPatternBind().getBind();
				list.addAll(bind.getSet().apply(this.pogExpVisitor, question));
			}

			list.addAll(node.getStatement().apply(this.pogStmVisitor, question));
			return list;
		} catch (Exception e)
		{
			throw new POException(node, e.getMessage());
		}
	}

	@Override
	public IProofObligationList defaultPClause(PClause node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultPCase(PCase node,
			IPOContextStack question)
	{

		return new ProofObligationList();
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
