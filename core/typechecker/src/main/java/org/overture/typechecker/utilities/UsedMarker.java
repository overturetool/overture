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
package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to Mark used nodes from the AST.
 * 
 * @author kel
 */

public class UsedMarker extends AnalysisAdaptor
{

	protected ITypeCheckerAssistantFactory af;

	public UsedMarker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public void caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{

		node.setUsed(true);
		node.getState().apply(THIS);
	}

	@Override
	public void caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		node.setUsed(true);
		node.getDef().apply(THIS);
	}

	@Override
	public void caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		node.setUsed(true);
		node.getSuperdef().apply(THIS);
	}

	@Override
	public void caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{

		node.setUsed(true);
		node.getDef().apply(THIS);
		node.setUsed(true);
	}

	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		node.setUsed(true);
	}

}
