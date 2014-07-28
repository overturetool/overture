package org.overture.codegen.trans.letexps;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class DeflattenTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;
	
	public DeflattenTransformation(BaseTransformationAssistant baseAssistant)
	{
		super();
		this.baseAssistant = baseAssistant;
	}

	@Override
	public void caseALetDefStmCG(ALetDefStmCG node) throws AnalysisException
	{
		List<SStmCG> statements = new LinkedList<SStmCG>();
		statements.add(node.getStm());
		deFlatten(node, node.getLocalDefs(), statements);
	}
	
	@Override
	public void inABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		deFlatten(node, node.getLocalDefs(), node.getStatements());
	}

	private void deFlatten(INode node, List<? extends SLocalDeclCG> localDecls, List<SStmCG> statements) throws AnalysisException
	{
		int declCount = localDecls.size();
		int statementCount = statements.size();
		
		ABlockStmCG top = new ABlockStmCG();
		ABlockStmCG currentBlock = top;

		for (int i = 0; i < declCount; i++)
		{
			SLocalDeclCG current = localDecls.get(i).clone();
			currentBlock.getLocalDefs().add(current);

			ABlockStmCG nextBlock = new ABlockStmCG();
			currentBlock.getStatements().add(nextBlock);
			currentBlock = nextBlock;
		}

		ABlockStmCG topLevelStmBlock = currentBlock;
		
		for(int i = 0; i < statementCount; i++)
		{
			SStmCG current = statements.get(i).clone();
			currentBlock.getStatements().add(current);

			ABlockStmCG nextBlock = new ABlockStmCG();
			currentBlock.getStatements().add(nextBlock);
			currentBlock = nextBlock;
		}
		
		baseAssistant.replaceNodeWith(node, top);
	
		if(!topLevelStmBlock.getStatements().isEmpty())
		{
			topLevelStmBlock.getStatements().get(0).apply(this);
		}
	}
}
