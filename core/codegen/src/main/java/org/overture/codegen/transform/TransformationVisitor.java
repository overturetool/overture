package org.overture.codegen.transform;

import java.util.LinkedList;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.ooast.OoAstInfo;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private OoAstInfo info;
	private CompAssistantCG compAssistant;
	
	public TransformationVisitor(OoAstInfo info)
	{
		this.info = info;
		this.compAssistant = new CompAssistantCG();
	}
	
	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		PStmCG enclosingStm = node.getAncestor(PStmCG.class);

		if (enclosingStm == null)
			throw new AnalysisException("Sequence comprehensions are currently only supported within methods");
		
		ACompSeqExpCG seqComp = (ACompSeqExpCG) node;

		//Variable names 
		String colName = info.getTempVarNameGen().nextVarName();
		String iteratorName = info.getTempVarNameGen().nextVarName();
		String resSeqName = seqComp.getVar();

		AWhileStmCG whileStm = new AWhileStmCG();
		whileStm.setExp(compAssistant.consInstanceCall(compAssistant.consIteratorType(), iteratorName, compAssistant.getSeqTypeCloned(seqComp).getSeqOf(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null));
		whileStm.setBody(compAssistant.consWhileBody(seqComp, iteratorName, resSeqName));

		//Construct and set up block containing sequence comprehension
		ABlockStmCG block = new ABlockStmCG();
		
		LinkedList<ALocalVarDeclCG> blockLocalDefs = block.getLocalDefs();
		blockLocalDefs.add(compAssistant.consSetBindDecl(colName, seqComp));
		blockLocalDefs.add(compAssistant.consIteratorDecl(iteratorName, colName));
		blockLocalDefs.add(compAssistant.consResultSeqDecl(resSeqName, seqComp));
		
		LinkedList<PStmCG> blockStms = block.getStatements();
		blockStms.add(whileStm);

		//Now replace the statement with the result of the sequence comprehension with the
		//block statement doing the sequence comprehension and add to this block statement
		//the statement with the result of the sequence comprehension
		INode stmParent = enclosingStm.parent();
		stmParent.replaceChild(enclosingStm, block);
		enclosingStm.parent(null);
		
		blockStms.add(enclosingStm);
	}
}
