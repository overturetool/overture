/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class displays a detailed string representation of the Type.
 * 
 * @author nbattle
 */
public class DetailedTypeDisplayer extends AnswerAdaptor<String>
{
	protected ITypeCheckerAssistantFactory af;

	public DetailedTypeDisplayer(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public String caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{
		return "compose " + type.getName() + " of " + Utils.listToString(type.getFields()) + " end";
	}

	@Override
	public String defaultPType(PType type) throws AnalysisException
	{
		IAnswer<String> displayer = af.getTypeDisplayer();
		return type.apply(displayer);
	}

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		return null;
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		return null;
	}
}
