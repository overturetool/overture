package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	private CompAssistantCG compAssistant;
	
	//TODO: Rename type it is not a "stm" assistant anymore. It is more a header assistant
	private LetBeStStmAssistantCG letBeStAssistant;
	
	private TransformationAssistantCG transformationAssistant;
	
	public TransformationVisitor(OoAstInfo info)
	{
		this.info = info;
		this.compAssistant = new CompAssistantCG();
		this.letBeStAssistant = new LetBeStStmAssistantCG();
		this.transformationAssistant = new TransformationAssistantCG();
	}
	
	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(node.getHeader(), info);
		
		outerBlock.getStatements().add(node.getStatement());
		
		transformationAssistant.replaceNodeWith(node, outerBlock);
	}
	
	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			throw new AnalysisException("Generation of the let be st expressions is only supported within operations/functions");
		
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(node.getHeader(), info);
		
		ALocalVarDeclCG resultDecl = letBeStAssistant.consLetBeStExpResulDecl(node.getVar(), node.getValue());

		StmAssistantCG.injectDeclAsStm(outerBlock, resultDecl);
		
		transformationAssistant.replaceNodeWith(enclosingStm, outerBlock);
		
		outerBlock.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			throw new AnalysisException("Generation of a sequence comprehension is only supported within operations/functions");

		//Variable names 
		String setName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.SET_NAME_PREFIX);
		String iteratorName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.ITERATOR_NAME_PREFIX);
		String resSeqName = node.getVar();

		AForLoopStmCG forLoop = new AForLoopStmCG();
		forLoop.setInit(compAssistant.consIteratorDecl(iteratorName, setName));
		forLoop.setCond(compAssistant.consInstanceCall(compAssistant.consIteratorType(), iteratorName, compAssistant.getSeqTypeCloned(node).getSeqOf(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null));
		forLoop.setInc(null);
		forLoop.setBody(compAssistant.consWhileBody(node, iteratorName, resSeqName));

		//Construct and set up block containing sequence comprehension
		ABlockStmCG block = new ABlockStmCG();
		
		LinkedList<ALocalVarDeclCG> blockLocalDefs = block.getLocalDefs();
		blockLocalDefs.add(compAssistant.consSetBindDecl(setName, node));
		blockLocalDefs.add(compAssistant.consResultSeqDecl(resSeqName, node));
		
		LinkedList<PStmCG> blockStms = block.getStatements();
		blockStms.add(forLoop);

		//Now replace the statement with the result of the sequence comprehension with the
		//block statement doing the sequence comprehension and add to this block statement
		//the statement with the result of the sequence comprehension
		transformationAssistant.replaceNodeWith(enclosingStm, block);
		
		blockStms.add(enclosingStm);
	}
}
