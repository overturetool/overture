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
package org.overture.codegen.trans.letexps;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.ATernaryIfExpIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class IfExpTrans extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;

	public IfExpTrans(BaseTransformationAssistant baseAssistant)
	{
		this.baseAssistant = baseAssistant;
	}

	@Override
	public void inATernaryIfExpIR(ATernaryIfExpIR node) throws AnalysisException
	{
		INode parent = node.parent();

		if (parent instanceof AReturnStmIR)
		{
			AIfStmIR ifStm = new AIfStmIR();
			ifStm.setSourceNode(node.getSourceNode());

			ifStm.setIfExp(node.getCondition().clone());

			AReturnStmIR thenStm = new AReturnStmIR();
			thenStm.setExp(node.getTrueValue().clone());
			ifStm.setThenStm(thenStm);

			AReturnStmIR elseStm = new AReturnStmIR();
			elseStm.setExp(node.getFalseValue().clone());
			ifStm.setElseStm(elseStm);

			baseAssistant.replaceNodeWithRecursively(parent, ifStm, this);
		}
	}
}
