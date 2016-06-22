package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ALetBeStStmIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;

public class LetBeStTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;
	private ILanguageIterator langIterator;
	private IterationVarPrefixes iteVarPrefixes;

	public LetBeStTrans(TransAssistantIR transAssistant,
			ILanguageIterator langIterator, IterationVarPrefixes iteVarPrefixes)
	{
		this.transAssistant = transAssistant;
		this.langIterator = langIterator;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	public void caseALetBeStStmIR(ALetBeStStmIR node) throws AnalysisException
	{
		AHeaderLetBeStIR header = node.getHeader();

		if (!(header.getBinding() instanceof ASetMultipleBindIR))
		{
			transAssistant.getInfo().addTransformationWarning(node.getHeader().getBinding(), "This transformation only works for 'let be st' "
					+ "statements with with multiple set binds and not multiple type binds in '"
					+ this.getClass().getSimpleName() + "'");
			return;
		}

		SExpIR suchThat = header.getSuchThat();
		ASetMultipleBindIR binding = (ASetMultipleBindIR) node.getHeader().getBinding();

		SSetTypeIR setType = transAssistant.getSetTypeCloned(binding.getSet());
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();

		LetBeStStrategy strategy = new LetBeStStrategy(transAssistant, suchThat, setType, langIterator, tempVarNameGen, iteVarPrefixes);

		if (transAssistant.hasEmptySet(binding))
		{
			transAssistant.cleanUpBinding(binding);
			node.setStatement(new ABlockStmIR());
		}

		List<SPatternIR> patterns = binding.getPatterns();
		ABlockStmIR outerBlock = transAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy, iteVarPrefixes);

		// Only the statement of the let be st statement is added to the outer block statements.
		// We obtain the equivalent functionality of the remaining part of the let be st statement
		// from the transformation in the outer block
		outerBlock.getStatements().add(node.getStatement());

		// Replace the let be st statement with the transformation
		transAssistant.replaceNodeWithRecursively(node, outerBlock, this);

		outerBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(outerBlock));
	}
}
