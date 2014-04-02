package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ACompMapExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.AExists1QuantifierExpCG;
import org.overture.codegen.cgast.expressions.AExistsQuantifierExpCG;
import org.overture.codegen.cgast.expressions.AForAllQuantifierExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SQuantifierExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	
	private TransformationAssistantCG transformationAssistant;
	
	private ITransformationConfig config;
	
	public TransformationVisitor(OoAstInfo info, ITransformationConfig config, TempVarPrefixes varPrefixes)
	{
		this.info = info;
		this.config = config;
		this.transformationAssistant = new TransformationAssistantCG(info, varPrefixes);
	}
	
	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		AHeaderLetBeStCG header = node.getHeader();
		
		LetBeStStrategy strategy = new LetBeStStrategy(config, transformationAssistant, header.getSuchThat(), transformationAssistant.getSetTypeCloned(header.getBinding().getSet()));
		
		ASetMultipleBindCG binding = header.getBinding();
		
		if (transformationAssistant.hasEmptySet(binding))
		{
			transformationAssistant.cleanUpBinding(binding);
			node.setStatement(new ABlockStmCG());
		}
		
		ABlockStmCG outerBlock = transformationAssistant.consIterationBlock(binding.getPatterns(), header.getBinding().getSet(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile());
		
		outerBlock.getStatements().add(node.getStatement());
		
		transformationAssistant.replaceNodeWith(node, outerBlock);
		outerBlock.apply(this);
	}

	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "let be st expressions");

		AHeaderLetBeStCG header = node.getHeader();
		ASetMultipleBindCG binding = header.getBinding();
		LetBeStStrategy strategy = new LetBeStStrategy(config, transformationAssistant, header.getSuchThat(), transformationAssistant.getSetTypeCloned(binding.getSet()));

		ABlockStmCG outerBlock = new ABlockStmCG();

		if (transformationAssistant.hasEmptySet(binding))
		{
			transformationAssistant.cleanUpBinding(binding);
		}
		else
		{
			AVarLocalDeclCG resultDecl = transformationAssistant.consDecl(node.getVar(), node.getValue());
			info.getStmAssistant().injectDeclAsStm(outerBlock, resultDecl);
		}
		
		outerBlock.getStatements().addFirst(transformationAssistant.consIterationBlock(binding.getPatterns(), binding.getSet(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile()));

		transformationAssistant.replaceNodeWith(enclosingStm, outerBlock);
		outerBlock.getStatements().add(enclosingStm);
		outerBlock.apply(this);
	}

	@Override
	public void caseACompMapExpCG(ACompMapExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "map comprehension");
		
		ComplexCompStrategy strategy = new MapCompStrategy(config, transformationAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(node.getBindings(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile());
		
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
		block.apply(this);
	}
	
	@Override
	public void caseACompSetExpCG(ACompSetExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "set comprehension");
		
		ComplexCompStrategy strategy = new SetCompStrategy(config, transformationAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(node.getBindings(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile());
		
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
		block.apply(this);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "sequence comprehension");

		SeqCompStrategy strategy = new SeqCompStrategy(config, transformationAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());

		if (transformationAssistant.isEmptySet(node.getSet()))
		{
			node.setSet(null);
		}
		else
		{
			LinkedList<AIdentifierPatternCG> ids = new LinkedList<AIdentifierPatternCG>();
			ids.add(node.getId());

			ABlockStmCG block = transformationAssistant.consIterationBlock(ids, node.getSet(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile());

			transformationAssistant.replaceNodeWith(enclosingStm, block);

			block.getStatements().add(enclosingStm);
			block.apply(this);
		}
	}
	
	@Override
	public void caseAForAllQuantifierExpCG(AForAllQuantifierExpCG node) throws AnalysisException
	{
		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(config, transformationAssistant, node.getPredicate(), node.getVar(), OrdinaryQuantifier.FORALL);
		handleQuantifier(node, "forall expression", strategy);
	}
	
	@Override
	public void caseAExistsQuantifierExpCG(
			AExistsQuantifierExpCG node) throws AnalysisException
	{
		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(config, transformationAssistant, node.getPredicate(), node.getVar(), OrdinaryQuantifier.EXISTS);
		handleQuantifier(node, "exists expression", strategy);
	}
	
	@Override
	public void caseAExists1QuantifierExpCG(
			AExists1QuantifierExpCG node) throws AnalysisException
	{
		Exists1QuantifierStrategy strategy = new Exists1QuantifierStrategy(config, transformationAssistant, node.getPredicate(), node.getVar());
		handleQuantifier(node, "exists1 expression", strategy);
	}

	private void handleQuantifier(SQuantifierExpCG node, String nodeStr, QuantifierBaseStrategy strategy) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, nodeStr);
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(node.getBindList(), info.getTempVarNameGen(), strategy, config.iteratorType(), config.iteratorMethod(), config.setUtilFile());
		
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		block.getStatements().add(enclosingStm);
		block.apply(this);
	}
	
	private PStmCG getEnclosingStm(PExpCG node, String nodeStr) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		//This case should never occur as it must be checked for during the construction of the OO AST
		if (enclosingStm == null)
			throw new AnalysisException(String.format("Generation of a %s is only supported within operations/functions", node));
			
		return enclosingStm;
	}
}
