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
package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SourceNode;

public class IRVisitor<A extends PIR> extends QuestionAnswerAdaptor<IRInfo, A>
{
	private AbstractVisitorIR<IRInfo, A> irBuilder;

	public IRVisitor(AbstractVisitorIR<IRInfo, A> visitor)
	{
		this.irBuilder = visitor;
	}

	@Override
	public A defaultINode(org.overture.ast.node.INode node, IRInfo question)
			throws AnalysisException
	{
		A irNode = node.apply(irBuilder, question);

		if (irNode != null)
		{
			irNode.setSourceNode(new SourceNode(node));
		}

		return irNode;
	}

	@Override
	public A createNewReturnValue(org.overture.ast.node.INode node,
			IRInfo question) throws AnalysisException
	{
		return null;
	}

	@Override
	public A createNewReturnValue(Object node, IRInfo question)
			throws AnalysisException
	{
		return null;
	}
}
