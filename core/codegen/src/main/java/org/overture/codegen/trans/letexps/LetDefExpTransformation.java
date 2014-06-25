package org.overture.codegen.trans.letexps;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class LetDefExpTransformation extends DepthFirstAnalysisAdaptor
{
	protected BaseTransformationAssistant baseAssistant;
	
	public LetDefExpTransformation(BaseTransformationAssistant baseAssistant)
	{
		this.baseAssistant = baseAssistant;
	}

	public void inALetDefExpCG(ALetDefExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = getEnclosingStm(node, "let def expression");
		
		baseAssistant.replaceNodeWith(node, node.getExp());
		
		ABlockStmCG blockStm = new ABlockStmCG();
		
		for(AVarLocalDeclCG local : node.getLocalDefs())
		{
			blockStm.getLocalDefs().add(local.clone());
		}
		
		baseAssistant.replaceNodeWith(enclosingStm, blockStm);
		
		blockStm.getStatements().add(enclosingStm);
		
		blockStm.apply(this);
	}

	protected SStmCG getEnclosingStm(SExpCG node, String nodeStr)
			throws AnalysisException
	{
		SStmCG enclosingStm = node.getAncestor(SStmCG.class);
	
		//This case should never occur since all functions should be transformed into operations at this point
		if (enclosingStm == null)
		{
			throw new AnalysisException(String.format("Could not find enclosing statement for: %s", nodeStr));
		}
		
		return enclosingStm;
	}
}
