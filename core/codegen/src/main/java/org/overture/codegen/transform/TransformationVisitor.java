package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompMapExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	
	private CompAssistantCG compAssistant;	
	private LetBeStAssistantCG letBeStAssistant;
	
	public TransformationVisitor(OoAstInfo info)
	{
		this.info = info;
		this.compAssistant = new CompAssistantCG();
		this.letBeStAssistant = new LetBeStAssistantCG();
	}
	
	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		AHeaderLetBeStCG header = node.getHeader();
		
		LetBeStStrategy strategy = new LetBeStStrategy(info.getTempVarNameGen(), letBeStAssistant, header.getSuchThat(), letBeStAssistant.getSetTypeCloned(header.getBinding().getSet()));
		
		ABlockStmCG outerBlock = letBeStAssistant.consIterationBlock(header.getBinding().getPatterns(), header.getBinding().getSet(), header.getSuchThat(), info.getTempVarNameGen(), strategy);
		
		outerBlock.getStatements().add(node.getStatement());
		
		letBeStAssistant.replaceNodeWith(node, outerBlock);
	}
	
	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "let be st expressions");
		
		AHeaderLetBeStCG header = node.getHeader();
		
		LetBeStStrategy strategy = new LetBeStStrategy(info.getTempVarNameGen(), letBeStAssistant, header.getSuchThat(), letBeStAssistant.getSetTypeCloned(header.getBinding().getSet()));
		
		ABlockStmCG outerBlock = letBeStAssistant.consIterationBlock(header.getBinding().getPatterns(), header.getBinding().getSet(), header.getSuchThat(), info.getTempVarNameGen(), strategy);
		
		ALocalVarDeclCG resultDecl = letBeStAssistant.consDecl(node.getVar(), node.getValue());

		StmAssistantCG.injectDeclAsStm(outerBlock, resultDecl);
		
		letBeStAssistant.replaceNodeWith(enclosingStm, outerBlock);
		
		outerBlock.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompMapExpCG(ACompMapExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "map comprehension");

		ComplexCompStrategy strategy = new MapCompStrategy(compAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());
		
		ABlockStmCG block = compAssistant.consSetCompIterationBlock(node.getBindings(), node.getPredicate(), info.getTempVarNameGen(), strategy);
		
		compAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSetExpCG(ACompSetExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "set comprehension");
		
		ComplexCompStrategy strategy = new SetCompStrategy(compAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());
		
		ABlockStmCG block = compAssistant.consSetCompIterationBlock(node.getBindings(), node.getPredicate(), info.getTempVarNameGen(), strategy);
		
		compAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = getEnclosingStm(node, "sequence comprehension");

		SeqCompStrategy strategy = new SeqCompStrategy(compAssistant, node.getFirst(), node.getPredicate(), node.getVar(), node.getType());
		
		LinkedList<AIdentifierPatternCG> ids = new LinkedList<AIdentifierPatternCG>();
		ids.add(node.getId());
		
		ABlockStmCG block = compAssistant.consIterationBlock(ids, node.getSet(), node.getPredicate(), info.getTempVarNameGen(), strategy);
		
		compAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
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
