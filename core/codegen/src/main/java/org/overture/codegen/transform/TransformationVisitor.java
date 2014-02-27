package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	private CompAssistantCG compAssistant;
	private LetBeStStmAssistantCG letBeStStmAssistant;
	
	public TransformationVisitor(OoAstInfo info)
	{
		this.info = info;
		this.compAssistant = new CompAssistantCG();
		this.letBeStStmAssistant = new LetBeStStmAssistantCG();
	}
	
	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		INode parent = node.parent();
		
		PTypeCG setType = node.getSet().getType();
		
		//Variable names
		String setName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.SET_NAME_PREFIX);
		String successVarName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.SUCCESS_VAR_NAME_PREFIX);

		ABlockStmCG outerBlock = new ABlockStmCG();
		LinkedList<ALocalVarDeclCG> outerBlockDecls = outerBlock.getLocalDefs();

		outerBlockDecls.add(letBeStStmAssistant.consSetBindDecl(setName, node));
		outerBlockDecls.add(letBeStStmAssistant.consSuccessVarDecl(successVarName));
		
		ABlockStmCG nextBlock = outerBlock;
		
		LinkedList<AIdentifierPatternCG> ids = node.getIds();
		int numberOfIds = ids.size();
		
		for (int i = 0;; i++)
		{
			AIdentifierPatternCG id = ids.get(i);

			//Add next id to outer block
			outerBlockDecls.add(letBeStStmAssistant.consChosenElemenDecl(setType, id.getName()));

			//Construct next for loop
			String iteratorName = info.getTempVarNameGen().nextVarName(JavaTempVarPrefixes.ITERATOR_NAME_PREFIX);
			
			AForLoopStmCG forLoop = new AForLoopStmCG();
			forLoop.setInit(letBeStStmAssistant.consIteratorDecl(iteratorName, setName));
			forLoop.setCond(letBeStStmAssistant.consWhileCondition(node, iteratorName, successVarName));
			forLoop.setInc(null);
			
			ABlockStmCG forBody = letBeStStmAssistant.consForBody(setType, node.getSuchThat(), id.getName(), iteratorName, successVarName);
			forLoop.setBody(forBody);

			nextBlock.getStatements().add(forLoop);
			
			if (i < numberOfIds) 
			{
				nextBlock = forBody;
			}
			else
			{
				forBody.getStatements().add(letBeStStmAssistant.consSuccessAssignment(node.getSuchThat(), successVarName));
				break;
			}
		}

		LinkedList<PStmCG> outerBlockStms = outerBlock.getStatements();
		
		outerBlockStms.add(letBeStStmAssistant.consIfCheck(successVarName));
		outerBlockStms.add(node.getStatement());
		
		parent.replaceChild(node, outerBlock);
		node.parent(null);
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			throw new AnalysisException("Sequence comprehensions are currently only supported within methods");

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
		INode stmParent = enclosingStm.parent();
		stmParent.replaceChild(enclosingStm, block);
		enclosingStm.parent(null);
		
		blockStms.add(enclosingStm);
	}
}
