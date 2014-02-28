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
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	private CompAssistantCG compAssistant;
	
	private LetBeStAssistantCG letBeStAssistant;
	
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
		
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(header.getIds(), header.getSet(), header.getSuchThat(), info);
		
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
		
		ABlockStmCG outerBlock = letBeStAssistant.consBlock(header.getIds(), header.getSet(), header.getSuchThat(), info);
		
		ALocalVarDeclCG resultDecl = letBeStAssistant.consDecl(node.getVar(), node.getValue());

		StmAssistantCG.injectDeclAsStm(outerBlock, resultDecl);
		
		transformationAssistant.replaceNodeWith(enclosingStm, outerBlock);
		
		outerBlock.getStatements().add(enclosingStm);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			//TODO: Pick up on this earlier (see above to do msg)
			throw new AnalysisException("Generation of a sequence comprehension is only supported within operations/functions");

		//Variable names 
		String setName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.SET_NAME_PREFIX);
		String iteratorName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.ITERATOR_NAME_PREFIX);
		String resSeqName = node.getVar();

		AForLoopStmCG forLoop = new AForLoopStmCG();
		forLoop.setInit(compAssistant.consIteratorDecl(iteratorName, setName));
		forLoop.setCond(compAssistant.consInstanceCall(compAssistant.consIteratorType(), iteratorName, compAssistant.getSeqTypeCloned(node).getSeqOf(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null));
		forLoop.setInc(null);
		forLoop.setBody(compAssistant.consForBody(node, iteratorName, resSeqName));

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
