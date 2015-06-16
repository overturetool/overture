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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.utils.GeneratedModule;
import org.overturetool.cgisa.transformations.GroupMutRecs;
import org.overturetool.cgisa.transformations.SortDependencies;

/**
 * Main facade class for VDM 2 Isabelle CG
 * 
 * @author ldc
 */
public class IsaGen extends CodeGenBase
{

	public static GeneratedModule vdmModule2IsaTheory(AModuleModules module)
			throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		IsaGen ig = new IsaGen();
		List<AModuleModules> ast = new LinkedList<AModuleModules>();
		ast.add(module);
		List<GeneratedModule> r = ig.generateIsabelleSyntax(ast);

		return r.get(0);
	}

	public static String vdmExp2IsaString(PExp exp) throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		IsaGen ig = new IsaGen();
		GeneratedModule r = ig.generateIsabelleSyntax(exp);
		if (r.hasMergeErrors())
		{
			throw new org.overture.codegen.cgast.analysis.AnalysisException(exp.toString()
					+ " cannot be generated. Merge errors:"
					+ r.getMergeErrors().toString());
		}
		if (r.hasUnsupportedIrNodes())
		{
			throw new org.overture.codegen.cgast.analysis.AnalysisException(exp.toString()
					+ " cannot be generated. Unsupported in IR:"
					+ r.getUnsupportedInIr().toString());
		}
		if (r.hasUnsupportedTargLangNodes())
		{
			throw new org.overture.codegen.cgast.analysis.AnalysisException(exp.toString()
					+ " cannot be generated. Unsupported in TargLang:"
					+ r.getUnsupportedInTargLang().toString());
		}

		return r.getContent();
	}

	public IsaGen()
	{
		initVelocity();
	}

	private void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}

	public GeneratedModule generateIsabelleSyntax(PExp exp)
			throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		IRStatus<SExpCG> status = this.generator.generateFrom(exp);

		if (status.canBeGenerated())
		{
			return prettyPrint(status);
		}

		throw new org.overture.codegen.cgast.analysis.AnalysisException(exp.toString()
				+ " cannot be code-generated");
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
	public List<GeneratedModule> generateIsabelleSyntax(List<AModuleModules> ast)
			throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		// Transform AST into IR
		List<IRStatus<INode>> statuses = new LinkedList<>();
		for (AModuleModules sclass : ast)
		{
			IRStatus<INode> result = this.generator.generateFrom(sclass);

			if (result.canBeGenerated())
			{
				statuses.add(result);
			} else
			{
				Vector<GeneratedModule> r = new Vector<GeneratedModule>();
				r.add(new GeneratedModule("ERROR", result.getUnsupportedInIr(), new HashSet<IrNodeInfo>()));
				return r;
			}
		}

		// Apply transformations
		for (IRStatus<INode> status : statuses)
		{
			// first, transform away any recursion cycles
			GroupMutRecs groupMR = new GroupMutRecs();
			generator.applyTotalTransformation(status, groupMR);

			if (status.getIrNode() instanceof AModuleDeclCG)
			{
				AModuleDeclCG cClass = (AModuleDeclCG) status.getIrNode();
				// then sort remaining dependencies
				SortDependencies sortTrans = new SortDependencies(cClass.getDecls());
				generator.applyPartialTransformation(status, sortTrans);
			}
		}

		return prettyPrint(statuses);

	}

	private List<GeneratedModule> prettyPrint(List<IRStatus<INode>> statuses)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		// Apply merge visitor to pretty print Isabelle syntax
		TemplateStructure ts = new TemplateStructure("IsaTemplates");
		IsaTranslations isa = new IsaTranslations(ts);
		MergeVisitor pp = isa.getMergeVisitor();

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();

		for (IRStatus<INode> status : statuses)
		{
			generated.add(prettyPrintNode(pp, status));

		}

		// Return syntax
		return generated;
	}

	private GeneratedModule prettyPrint(IRStatus<? extends INode> status)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		// Apply merge visitor to pretty print Isabelle syntax
		TemplateStructure ts = new TemplateStructure("IsaTemplates");
		IsaTranslations isa = new IsaTranslations(ts);
		MergeVisitor pp = isa.getMergeVisitor();
		return prettyPrintNode(pp, status);
	}

	private GeneratedModule prettyPrintNode(MergeVisitor pp,
			IRStatus<? extends INode> status)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		INode irClass = status.getIrNode();

		StringWriter sw = new StringWriter();

		irClass.apply(pp, sw);

		if (pp.hasMergeErrors())
		{
			return new GeneratedModule(status.getIrNodeName(), irClass, pp.getMergeErrors());
		} else if (pp.hasUnsupportedTargLangNodes())
		{
			return new GeneratedModule(status.getIrNodeName(), new HashSet<VdmNodeInfo>(), pp.getUnsupportedInTargLang());
		} else
		{
			// Code can be generated. Ideally, should format it
			GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), irClass, sw.toString());
			generatedModule.setTransformationWarnings(status.getTransformationWarnings());
			return generatedModule;
		}
	}
}
