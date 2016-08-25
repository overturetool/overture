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
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.statements.AAtomicStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACaseAltStmStmIR;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AForIndexStmIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.statements.ASuperCallStmIR;

public class StmAssistantIR extends AssistantBase
{
	public StmAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void injectDeclAsStm(ABlockStmIR block, AVarDeclIR decl)
	{
		ABlockStmIR wrappingBlock = new ABlockStmIR();

		wrappingBlock.getLocalDefs().add(decl);

		block.getStatements().add(wrappingBlock);
	}

	public void handleAlternativesCasesStm(IRInfo question, PExp exp,
			List<ACaseAlternativeStm> cases, List<ACaseAltStmStmIR> casesCg)
			throws AnalysisException
	{
		for (ACaseAlternativeStm alt : cases)
		{
			SStmIR altCg = alt.apply(question.getStmVisitor(), question);
			casesCg.add((ACaseAltStmStmIR) altCg);
		}

		PType expType = question.getTypeAssistant().resolve(exp.getType());

		if (expType instanceof AUnionType)
		{
			AUnionType unionType = ((AUnionType) expType).clone();
			question.getTcFactory().createAUnionTypeAssistant().expand(unionType);

			for (int i = 0; i < cases.size(); i++)
			{
				ACaseAlternativeStm vdmCase = cases.get(i);
				ACaseAltStmStmIR cgCase = casesCg.get(i);

				PType patternType = question.getAssistantManager().getTypeAssistant().getType(question, unionType, vdmCase.getPattern());
				STypeIR patternTypeCg = patternType.apply(question.getTypeVisitor(), question);
				cgCase.setPatternType(patternTypeCg);
			}
		} else
		{
			STypeIR expTypeCg = expType.apply(question.getTypeVisitor(), question);

			for (ACaseAltStmStmIR altCg : casesCg)
			{
				altCg.setPatternType(expTypeCg.clone());
			}
		}
	}

	public boolean inAtomic(SStmIR stm)
	{
		return stm.getAncestor(AAtomicStmIR.class) != null;
	}

	public String getSuperClassName(ASuperCallStmIR stm)
	{
		ADefaultClassDeclIR enclosingClass = stm.getAncestor(ADefaultClassDeclIR.class);

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
		return !(block.parent() instanceof SOperationDefinition)
				&& !(block.parent() instanceof AElseIfStm)
				&& !(block.parent() instanceof AIfStm)
				&& !(block.parent() instanceof AForAllStm)
				&& !(block.parent() instanceof AForIndexStm);
	}

	public boolean isScoped(ABlockStmIR block)
	{
		return !(block.parent() instanceof AMethodDeclIR)
				&& !(block.parent() instanceof AElseIfStmIR)
				&& !(block.parent() instanceof AIfStmIR)
				&& !(block.parent() instanceof AForAllStmIR)
				&& !(block.parent() instanceof AForIndexStmIR)
				&& !(block.parent() instanceof AForLoopStmIR);
	}

	public boolean equal(AMetaStmIR left, AMetaStmIR right)
	{
		if (left.getMetaData().size() != right.getMetaData().size())
		{
			return false;
		}

		for (int i = 0; i < left.getMetaData().size(); i++)
		{
			String currentLeft = left.getMetaData().get(i).value;
			String currentRight = right.getMetaData().get(i).value;

			if (!currentLeft.equals(currentRight))
			{
				return false;
			}
		}

		return true;
	}
}
