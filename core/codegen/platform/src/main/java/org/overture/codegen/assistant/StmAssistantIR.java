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
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AForIndexStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.ASuperCallStmCG;
import org.overture.codegen.ir.IRInfo;

public class StmAssistantCG extends AssistantBase
{
	public StmAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void injectDeclAsStm(ABlockStmCG block, AVarDeclCG decl)
	{
		ABlockStmCG wrappingBlock = new ABlockStmCG();

		wrappingBlock.getLocalDefs().add(decl);

		block.getStatements().add(wrappingBlock);
	}

	public void handleAlternativesCasesStm(IRInfo question, PExp exp,
			List<ACaseAlternativeStm> cases, List<ACaseAltStmStmCG> casesCg)
			throws AnalysisException
	{
		for (ACaseAlternativeStm alt : cases)
		{
			SStmCG altCg = alt.apply(question.getStmVisitor(), question);
			casesCg.add((ACaseAltStmStmCG) altCg);
		}

		PType expType = question.getTypeAssistant().resolve(exp.getType());
		
		if (expType instanceof AUnionType)
		{
			AUnionType unionType = ((AUnionType) expType).clone();
			question.getTcFactory().createAUnionTypeAssistant().expand(unionType);

			for (int i = 0; i < cases.size(); i++)
			{
				ACaseAlternativeStm vdmCase = cases.get(i);
				ACaseAltStmStmCG cgCase = casesCg.get(i);

				PType patternType = question.getAssistantManager().getTypeAssistant().getType(question, unionType, vdmCase.getPattern());
				STypeCG patternTypeCg = patternType.apply(question.getTypeVisitor(), question);
				cgCase.setPatternType(patternTypeCg);
			}
		} else
		{
			STypeCG expTypeCg = expType.apply(question.getTypeVisitor(), question);

			for (ACaseAltStmStmCG altCg : casesCg)
			{
				altCg.setPatternType(expTypeCg.clone());
			}
		}
	}
	
	public boolean inAtomic(SStmCG stm)
	{
		return stm.getAncestor(AAtomicStmCG.class) != null;
	}
	
	public String getSuperClassName(ASuperCallStmCG stm)
	{
		ADefaultClassDeclCG enclosingClass = stm.getAncestor(ADefaultClassDeclCG.class);
		
		return enclosingClass.getName();
	}
	
	public boolean isScoped(ABlockSimpleBlockStm block)
	{
		return appearsInRightContext(block);
	}
	
	public boolean isScoped(ALetStm let)
	{
		return appearsInRightContext(let);
	}

	private boolean appearsInRightContext(PStm block)
	{
		return !(block.parent() instanceof SOperationDefinition) && 
				!(block.parent() instanceof AElseIfStm) && 
				!(block.parent() instanceof AIfStm) &&
				!(block.parent() instanceof AForAllStm) &&
				!(block.parent() instanceof AForIndexStm);
	}
	
	public boolean isScoped(ABlockStmCG block)
	{
		return !(block.parent() instanceof AMethodDeclCG) &&
				!(block.parent() instanceof AElseIfStmCG) &&
				!(block.parent() instanceof AIfStmCG) &&
				!(block.parent() instanceof AForAllStmCG) &&
				!(block.parent() instanceof AForIndexStmCG) &&
				!(block.parent() instanceof AForLoopStmCG);
	}
	
	public boolean equal(AMetaStmCG left, AMetaStmCG right)
	{
		if(left.getMetaData().size() != right.getMetaData().size())
		{
			return false;
		}
		
		for(int i = 0; i < left.getMetaData().size(); i++)
		{
			String currentLeft = left.getMetaData().get(i).value;
			String currentRight = right.getMetaData().get(i).value;
			
			if(!currentLeft.equals(currentRight))
			{
				return false;
			}
		}
		
		return true;
	}
}
