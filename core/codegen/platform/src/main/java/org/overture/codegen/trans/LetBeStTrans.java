package org.overture.codegen.trans;

import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;

public class LetBeStTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private ILanguageIterator langIterator;
	private IterationVarPrefixes iteVarPrefixes;

	public LetBeStTrans(TransAssistantCG transAssistant,
			ILanguageIterator langIterator, IterationVarPrefixes iteVarPrefixes)
	{
		this.transAssistant = transAssistant;
		this.langIterator = langIterator;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		AHeaderLetBeStCG header = node.getHeader();

		if (!(header.getBinding() instanceof ASetMultipleBindCG))
		{
			transAssistant.getInfo().addTransformationWarning(node.getHeader().getBinding(), "This transformation only works for 'let be st' "
					+ "statements with with multiple set binds and not multiple type binds in '"
					+ this.getClass().getSimpleName() + "'");
			return;
		}

		SExpCG suchThat = header.getSuchThat();
		ASetMultipleBindCG binding = (ASetMultipleBindCG) node.getHeader().getBinding();

		SSetTypeCG setType = transAssistant.getSetTypeCloned(binding.getSet());
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();

		LetBeStStrategy strategy = new LetBeStStrategy(transAssistant, suchThat, setType, langIterator, tempVarNameGen, iteVarPrefixes);

		if (transAssistant.hasEmptySet(binding))
		{
			transAssistant.cleanUpBinding(binding);
			node.setStatement(new ABlockStmCG());
		}

		LinkedList<SPatternCG> patterns = binding.getPatterns();
		ABlockStmCG outerBlock = transAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy, iteVarPrefixes);

		// Only the statement of the let be st statement is added to the outer block statements.
		// We obtain the equivalent functionality of the remaining part of the let be st statement
		// from the transformation in the outer block
		outerBlock.getStatements().add(node.getStatement());

		// Replace the let be st statement with the transformation
		transAssistant.replaceNodeWithRecursively(node, outerBlock, this);

		outerBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(outerBlock));
	}
}
