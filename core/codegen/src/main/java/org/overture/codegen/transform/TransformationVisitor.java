package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
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
	
	//TODO: Why have this when it is sub super class for the other two?
	private TransformationAssistantCG transformationAssistant;
	
	public TransformationVisitor(OoAstInfo info)
	{
		this.info = info;
		this.compAssistant = new CompAssistantCG();
		this.letBeStAssistant = new LetBeStAssistantCG();
		this.transformationAssistant = new TransformationAssistantCG();
	}
	
	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		AHeaderLetBeStCG header = node.getHeader();
		
		LetBeStStrategy strategy = new LetBeStStrategy(info.getTempVarNameGen(), letBeStAssistant, header.getSuchThat(), transformationAssistant.getSetTypeCloned(header.getSet()));
		
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(header.getIds(), header.getSet(), header.getSuchThat(), info.getTempVarNameGen(), strategy);
		
		outerBlock.getStatements().add(node.getStatement());
		
		transformationAssistant.replaceNodeWith(node, outerBlock);
	}
	
	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			//FIXME: pick up on this already during the construction of the OO AST and report the unsupported node.
			throw new AnalysisException("Generation of the let be st expressions is only supported within operations/functions");
		
		AHeaderLetBeStCG header = node.getHeader();
		
		LetBeStStrategy strategy = new LetBeStStrategy(info.getTempVarNameGen(), letBeStAssistant, header.getSuchThat(), transformationAssistant.getSetTypeCloned(header.getSet()));
		
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(header.getIds(), header.getSet(), header.getSuchThat(), info.getTempVarNameGen(), strategy);
		
		ALocalVarDeclCG resultDecl = letBeStAssistant.consDecl(node.getVar(), node.getValue());

		StmAssistantCG.injectDeclAsStm(outerBlock, resultDecl);
		
		transformationAssistant.replaceNodeWith(enclosingStm, outerBlock);
		
		outerBlock.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSetExpCG(ACompSetExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			//TODO: Pick up on this earlier (see above to do msg)
			throw new AnalysisException("Generation of a sequence comprehension is only supported within operations/functions");
		
		SetCompStrategy strategy = new SetCompStrategy(transformationAssistant, node.getFirst(), node.getPredicate(), node.getSet(), node.getVar());
		
		ABlockStmCG block = compAssistant.consBlock(node.getIds(), node.getSet(), node.getPredicate(), info.getTempVarNameGen(), strategy);
		
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		//TODO: Make it use the approach adopted by set comprehensions generation
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			//TODO: Pick up on this earlier (see above to do msg)
			throw new AnalysisException("Generation of a sequence comprehension is only supported within operations/functions");

		SeqCompStrategy strategy = new SeqCompStrategy(transformationAssistant, node.getFirst(), node.getPredicate(), node.getSet(), node.getVar(), transformationAssistant.getSeqTypeCloned(node));
		
		LinkedList<AIdentifierPatternCG> ids = new LinkedList<AIdentifierPatternCG>();
		ids.add(node.getId());
		
		ABlockStmCG block = compAssistant.consBlock(ids, node.getSet(), node.getPredicate(), info.getTempVarNameGen(), strategy);
		
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		
		block.getStatements().add(enclosingStm);
	}
}
