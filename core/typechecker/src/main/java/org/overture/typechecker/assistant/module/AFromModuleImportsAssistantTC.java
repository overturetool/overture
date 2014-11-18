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
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.visitor.TypeCheckVisitor;

//FIXME only used in 1 class. move it
public class AFromModuleImportsAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AFromModuleImportsAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PDefinition> getDefinitions(AFromModuleImports ifm,
			AModuleModules from)
	{

		List<PDefinition> defs = new Vector<PDefinition>();

		for (List<PImport> ofType : ifm.getSignatures())
		{
			for (PImport imp : ofType)
			{
				defs.addAll(af.createPImportAssistant().getDefinitions(imp, from));
			}
		}

		return defs;
	}

	public void typeCheck(AFromModuleImports ifm, ModuleEnvironment env)
			throws AnalysisException
	{
		TypeCheckVisitor tc = new TypeCheckVisitor();
		TypeCheckInfo question = new TypeCheckInfo(af, env, null, null);

		for (List<PImport> ofType : ifm.getSignatures())
		{
			for (PImport imp : ofType)
			{
				imp.apply(tc, question);
			}
		}

	}

}
