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
package org.overture.codegen.trans.assistants;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.AAnonymousClassExpCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.AElseIfStmCG;
import org.overture.codegen.ir.statements.AIfStmCG;

public class BaseTransformationAssistant
{
	public void replaceNodeWith(INode original, INode replacement)
	{
		if (original != replacement)
		{
			replace(original, replacement);
		}
	}

	public void replaceNodeWithRecursively(INode original, INode replacement,
			DepthFirstAnalysisAdaptor analysis) throws AnalysisException
	{
		if (original != replacement)
		{
			replaceNodeWith(original, replacement);
			replacement.apply(analysis);
		}
	}

	private void replace(INode original, INode replacement)
	{
		INode parent = original.parent();

		if (parent != null)
		{
			parent.replaceChild(original, replacement);
		}

		original.parent(null);
	}

	public SStmCG findEnclosingStm(INode node)
			throws AnalysisException

	{
		if (node.getAncestor(AAnonymousClassExpCG.class) == null)
		{
			AVarDeclCG localDecl = node.getAncestor(AVarDeclCG.class);
			
			if (localDecl != null && localDecl.parent() instanceof ABlockStmCG)
			{
				ABlockStmCG block = (ABlockStmCG) localDecl.parent();

				if (block.getLocalDefs().size() <= 1)
				{
					return block;
				}

				List<AVarDeclCG> defsToLift = new LinkedList<AVarDeclCG>();

				int i = 0;
				for (; i < block.getLocalDefs().size(); i++)
				{
					AVarDeclCG currentDef = block.getLocalDefs().get(i);

					if (currentDef == localDecl)
					{
						defsToLift.add(currentDef);
						i++;
						for (; i < block.getLocalDefs().size(); i++)
						{
							currentDef = block.getLocalDefs().get(i);
							defsToLift.add(currentDef);
						}
					}
				}

				block.getLocalDefs().removeAll(defsToLift);
				LinkedList<SStmCG> statementsToLift = block.getStatements();

				ABlockStmCG liftedBlock = new ABlockStmCG();
				liftedBlock.setLocalDefs(defsToLift);
				liftedBlock.setStatements(statementsToLift);

				block.getStatements().clear();
				block.getStatements().add(liftedBlock);

				return liftedBlock;
			}
		}
		
		SStmCG enclosingStm = node.getAncestor(SStmCG.class);

		if (enclosingStm == null)
		{
			return null;
		}

		if (enclosingStm instanceof AElseIfStmCG)
		{
			AElseIfStmCG elseIf = (AElseIfStmCG) enclosingStm;
			AIfStmCG enclosingIf = elseIf.getAncestor(AIfStmCG.class);

			LinkedList<AElseIfStmCG> elseIfList = new LinkedList<AElseIfStmCG>(enclosingIf.getElseIf());
			for (int i = 0; i < elseIfList.size(); i++)
			{
				AElseIfStmCG currentElseIf = elseIfList.get(i);
				if (elseIf == currentElseIf)
				{
					enclosingIf.getElseIf().remove(currentElseIf);
					AIfStmCG elsePart = new AIfStmCG();
					elsePart.setIfExp(currentElseIf.getElseIf());
					elsePart.setThenStm(currentElseIf.getThenStm());

					for (int j = i + 1; j < elseIfList.size(); j++)
					{
						enclosingIf.getElseIf().remove(elseIfList.get(j));
						elsePart.getElseIf().add(elseIfList.get(j));
					}

					ABlockStmCG block = new ABlockStmCG();
					block.getStatements().add(elsePart);

					elsePart.setElseStm(enclosingIf.getElseStm());
					enclosingIf.setElseStm(block);

					return elsePart;
				}
			}
		}

		return enclosingStm;
	}
	
	public SStmCG getEnclosingStm(INode node, String nodeStr)
			throws AnalysisException
	{
		SStmCG enclosingStm = findEnclosingStm(node);

		if (enclosingStm == null)
		{
			new AnalysisException(String.format("Could not find enclosing statement for %s", node));
		}
		
		return enclosingStm;
	}
}
