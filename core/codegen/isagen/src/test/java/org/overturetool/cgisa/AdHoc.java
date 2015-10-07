/*
 * #%~
 * VDM to Isabelle Translation
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
package org.overturetool.cgisa;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.core.tests.ParseTcFacade;

public class AdHoc
{
	@Test
	public void testQuick() throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException
	{
		
		File f = new File("src/test/resources/test.vdmsl");
		List<File> files = new LinkedList<>();
		files.add(f);
		
		List<INode> ast = ParseTcFacade.typedAstNoRetry(files, "Quick", Dialect.VDM_SL);
		
		IsaGen gen = new IsaGen();
		
		List<AModuleModules> classes = new LinkedList<>();
		
		for(INode n : ast)
		{
			classes.add((AModuleModules) n);
		}
		
		List<GeneratedModule> result = gen.generateIsabelleSyntax(classes);
		
		for (GeneratedModule generatedClass : result)
		{
			Logger.getLog().println("(**********)");

			if (generatedClass.hasMergeErrors())
			{
				Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

				GeneralCodeGenUtils.printMergeErrors(generatedClass.getMergeErrors());
			} else if (!generatedClass.canBeGenerated())
			{
				Logger.getLog().println("Could not generate class: "
						+ generatedClass.getName() + "\n");
				
				if(generatedClass.hasUnsupportedIrNodes())
				{
					Logger.getLog().println("Following VDM constructs are not supported by the IR:");
					GeneralCodeGenUtils.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
				}
				
				if(generatedClass.hasUnsupportedTargLangNodes())
				{
					Logger.getLog().println("Following IR constructs are not supported by the backend/target languages:");
					GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
				}
				
			} else
			{
				Logger.getLog().println(generatedClass.getContent());
				
				Set<IrNodeInfo> warnings = generatedClass.getTransformationWarnings();
				
				if(!warnings.isEmpty())
				{
					Logger.getLog().println("Following transformation warnings were found:");
					GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getTransformationWarnings());
				}
			}

			Logger.getLog().println("\n");
		}
		
	}
}
