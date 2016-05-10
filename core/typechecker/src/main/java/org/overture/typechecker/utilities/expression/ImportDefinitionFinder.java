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
package org.overture.typechecker.utilities.expression;

import java.util.List;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the definitions of an imported object from a module.
 * 
 * @author kel
 */
public class ImportDefinitionFinder extends
		QuestionAnswerAdaptor<AModuleModules, List<PDefinition>>
{
	protected ITypeCheckerAssistantFactory af;

	public ImportDefinitionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<PDefinition> caseAAllImport(AAllImport imp,
			AModuleModules module) throws AnalysisException
	{
		// return AAllImportAssistantTC.getDefinitions(imp,from);
		imp.setFrom(module);

		if (imp.getFrom().getExportdefs().isEmpty())
		{
			TypeCheckerErrors.report(3190, "Import all from module with no exports?", imp.getLocation(), imp);
		}

		List<PDefinition> imported = new ArrayList<PDefinition>();

		for (PDefinition d : imp.getFrom().getExportdefs())
		{
			PDefinition id = AstFactory.newAImportedDefinition(imp.getLocation(), d);
			af.createPDefinitionAssistant().markUsed(id); // So imports all is quiet
			imported.add(id);

		}

		return imported; // The lot!
	}

	@Override
	public List<PDefinition> caseATypeImport(ATypeImport imp,
			AModuleModules module) throws AnalysisException
	{
		List<PDefinition> list = new ArrayList<PDefinition>();
		imp.setFrom(module);

		PDefinition expdef = af.createPDefinitionListAssistant().findType(imp.getFrom().getExportdefs(), imp.getName(), null);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3191, "No export declared for import of type "
					+ imp.getName() + " from " + imp.getFrom().getName(), imp.getLocation(), imp);
		} else
		{
			if (imp.getRenamed() != null)
			{
				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(), expdef);
			} else
			{
				expdef = AstFactory.newAImportedDefinition(imp.getName().getLocation(), expdef);
			}

			list.add(expdef);
		}

		return list;
	}

	@Override
	public List<PDefinition> defaultSValueImport(SValueImport imp,
			AModuleModules module) throws AnalysisException
	{
		List<PDefinition> list = new ArrayList<PDefinition>();
		imp.setFrom(module);
		ILexNameToken name = imp.getName();

		PDefinition expdef = af.createPDefinitionListAssistant().findName(module.getExportdefs(), name, NameScope.NAMES);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3193, "No export declared for import of value "
					+ name + " from " + module.getName(), imp.getLocation(), imp);
		} else
		{
			if (imp.getRenamed() != null)
			{
				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(), expdef);
			} else
			{
				expdef = AstFactory.newAImportedDefinition(imp.getLocation(), expdef);
			}

			list.add(expdef);
		}

		return list;
	}

	@Override
	public List<PDefinition> createNewReturnValue(INode node,
			AModuleModules question) throws AnalysisException
	{
		assert false : "PImport.getDefinitions should never hit this case";
		return null;
	}

	@Override
	public List<PDefinition> createNewReturnValue(Object node,
			AModuleModules question) throws AnalysisException
	{
		assert false : "PImport.getDefinitions should never hit this case";
		return null;
	}

}
