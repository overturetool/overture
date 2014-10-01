/*
 * #%~
 * The VDM Pretty Printer
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
package org.overture.prettyprinter;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;

public class PrettyPrinterVisitor extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{

	private QuestionAnswerAdaptor<PrettyPrinterEnv, String> ppDefinition = new PrettyPrinterVisitorDefinitions();

	@Override
	public String defaultPDefinition(PDefinition node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.apply(ppDefinition, question);
	}

	@Override
	public String createNewReturnValue(INode arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
