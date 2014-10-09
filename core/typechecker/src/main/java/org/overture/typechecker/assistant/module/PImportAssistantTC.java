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
package org.overture.typechecker.assistant.module;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PImportAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public PImportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PDefinition> getDefinitions(PImport imp, AModuleModules from)
	{
		try
		{
			return imp.apply(af.getImportDefinitionFinder(), from);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}
}
