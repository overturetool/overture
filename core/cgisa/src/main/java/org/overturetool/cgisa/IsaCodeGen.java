/*
 * #%~
 * VDM to Isabelle Code Generator
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
package org.overturetool.cgisa;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRClassDeclStatus;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

/**
 * Main facade class for VDM 2 Isabelle CG
 * 
 * @author ldc
 */
public class IsaCodeGen extends CodeGenBase
{
	public IsaCodeGen()
	{
		this(null);
	}
	
	public IsaCodeGen(ILogger log)
	{
		super(log);

		// TODO: Set up template engine (see JavaCodeGen)
		// TODO Auto-generated constructor stub
	}

	/**
	 * Main entry point into the Isabelle CG component. Takes an AST and returns corresponding Isabelle Syntax.
	 * 
	 * @param ast
	 *            of the complete VDM++ model
	 * @return Isabelly syntax encoded in a string
	 * @throws AnalysisException
	 * @throws org.overture.codegen.cgast.analysis.AnalysisException
	 */
	List<GeneratedModule> generateIsabelleSyntax(List<SClassDefinition> ast)
			throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{

		// <>
		// Transform AST into IR
		List<IRClassDeclStatus> statuses = new LinkedList<>();
		for (SClassDefinition clazz : ast)
		{
			IRClassDeclStatus result = this.generator.generateFrom(clazz);

			if (result.canBeGenerated())
			{
				statuses.add(result);
			}
		}

		// Apply transformations (none atm...)
		// Apply merge visitor to pretty print isabelle syntax

		// No utility methods (template callables) added for now

		TemplateStructure ts = new TemplateStructure("IsaTemplates");
		MergeVisitor pp = new MergeVisitor(ts, new TemplateCallable[] {});

		StringWriter sw = new StringWriter();

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();

		for (IRClassDeclStatus status : statuses)
		{
			AClassDeclCG irClass = status.getClassCg();

			irClass.apply(pp, sw);

			if (pp.hasMergeErrors())
			{
				generated.add(new GeneratedModule(irClass.getName(), irClass, pp.getMergeErrors()));
			} else if (pp.hasUnsupportedTargLangNodes())
			{
				generated.add(new GeneratedModule(irClass.getName(), new HashSet<VdmNodeInfo>(), pp.getUnsupportedInTargLang()));
			} else
			{
				// Code can be generated
				// Here should code be formatted
				GeneratedModule generatedModule = new GeneratedModule(irClass.getName(), irClass, sw.toString());
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
			}

		}

		// Return syntax
		return generated;

	}

}
