package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ACaseAltExpExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.ir.IRInfo;

public class StmAssistantCG extends AssistantBase
{
	public StmAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}
	
	public void injectDeclAsStm(ABlockStmCG block, AVarLocalDeclCG decl)
	{
		ABlockStmCG wrappingBlock = new ABlockStmCG();
		
		wrappingBlock.getLocalDefs().add(decl);
		
		block.getStatements().add(wrappingBlock);
	}
	
	public void handleAlternativesCasesStm(IRInfo question, PExp exp,
			List<ACaseAlternativeStm> cases, List<ACaseAltStmStmCG> casesCg)
			throws AnalysisException
	{
		for(ACaseAlternativeStm alt : cases)
		{
			SStmCG altCg = alt.apply(question.getStmVisitor(), question);
			casesCg.add((ACaseAltStmStmCG) altCg);
		}	
		
		if(exp.getType() instanceof AUnionType)
		{
			AUnionType unionType = ((AUnionType) exp.getType()).clone();
			question.getTcFactory().createAUnionTypeAssistant().expand(unionType);
			
			for(int i = 0; i < cases.size(); i++)
			{
				ACaseAlternativeStm vdmCase = cases.get(i);
				ACaseAltStmStmCG cgCase = casesCg.get(i);
				
				PType patternType = question.getAssistantManager().getTypeAssistant().getType(question, unionType, vdmCase.getPattern());
				STypeCG patternTypeCg = patternType.apply(question.getTypeVisitor(), question);
				cgCase.setPatternType(patternTypeCg);
			}
		}
		else
		{
			STypeCG expType = exp.getType().apply(question.getTypeVisitor(), question);
			
			for(ACaseAltStmStmCG altCg : casesCg)
			{
				altCg.setPatternType(expType.clone());
			}
		}
	}
}
