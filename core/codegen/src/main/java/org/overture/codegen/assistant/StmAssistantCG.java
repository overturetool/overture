/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.assistant;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
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
