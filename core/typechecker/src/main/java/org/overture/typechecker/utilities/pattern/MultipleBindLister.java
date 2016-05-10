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
package org.overture.typechecker.utilities.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class MultipleBindLister extends AnswerAdaptor<List<PMultipleBind>>
{
	protected ITypeCheckerAssistantFactory af;

	public MultipleBindLister(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<PMultipleBind> caseASetBind(ASetBind bind)
			throws AnalysisException
	{
		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern());
		List<PMultipleBind> mblist = new ArrayList<PMultipleBind>();
		mblist.add(AstFactory.newASetMultipleBind(plist, bind.getSet()));
		return mblist;
	}

	@Override
	public List<PMultipleBind> caseATypeBind(ATypeBind bind)
			throws AnalysisException
	{
		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern().clone());
		List<PMultipleBind> mblist = new ArrayList<PMultipleBind>();
		mblist.add(AstFactory.newATypeMultipleBind(plist, bind.getType().clone()));
		return mblist;
	}

	@Override
	public List<PMultipleBind> defaultPBind(PBind bind)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public List<PMultipleBind> createNewReturnValue(INode node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PMultipleBind> createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
