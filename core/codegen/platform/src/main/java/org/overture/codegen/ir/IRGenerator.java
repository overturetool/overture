/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.ir;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.analysis.vdm.IdStateDesignatorDefCollector;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.trans.ITotalTransformation;

public class IRGenerator
{
	protected IRInfo codeGenInfo;

	public IRGenerator()
	{
		this.codeGenInfo = new IRInfo();
	}
	
	public void computeDefTable(List<? extends org.overture.ast.node.INode> mergedParseLists)
			throws AnalysisException
	{
		List<org.overture.ast.node.INode> classesToConsider = new LinkedList<>();

		for (org.overture.ast.node.INode node : mergedParseLists)
		{
			if (!codeGenInfo.getDeclAssistant().isLibrary(node))
			{
				classesToConsider.add(node);
			}
		}
		
		Map<AIdentifierStateDesignator, PDefinition> idDefs = IdStateDesignatorDefCollector.getIdDefs(classesToConsider, codeGenInfo.getTcFactory());
		codeGenInfo.setIdStateDesignatorDefs(idDefs);
	}

	public void clear()
	{
		codeGenInfo.clear();
	}

	public IRStatus<PIR> generateFrom(org.overture.ast.node.INode node)
			throws AnalysisException
	{
		codeGenInfo.clearNodes();

		if(node instanceof SClassDefinition)
		{
			SClassDeclIR classCg = node.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
			Set<VdmNodeInfo> unsupportedNodes = new HashSet<>(codeGenInfo.getUnsupportedNodes());
			String name = ((SClassDefinition) node).getName().getName();
			
			return new IRStatus<>(node, name, classCg, unsupportedNodes);
		}
		else if(node instanceof AModuleModules)
		{
			AModuleDeclIR module = node.apply(codeGenInfo.getModuleVisitor(), codeGenInfo);
			Set<VdmNodeInfo> unsupportedNodes = new HashSet<>(codeGenInfo.getUnsupportedNodes());
			String name = ((AModuleModules) node).getName().getName();
			
			return new IRStatus<>(node, name, module, unsupportedNodes);
		}
		
		return null;
	}
	
	public void applyPartialTransformation(IRStatus<? extends INode> status,
			org.overture.codegen.ir.analysis.intf.IAnalysis transformation)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		if(!status.canBeGenerated())
		{
			throw new org.overture.codegen.ir.analysis.AnalysisException("Cannot apply partial transformation to a status that cannot be generated!");
		}
		
		codeGenInfo.clearTransformationWarnings();

		status.getIrNode().apply(transformation);
		HashSet<IrNodeInfo> transformationWarnings = new HashSet<>(codeGenInfo.getTransformationWarnings());

		status.addTransformationWarnings(transformationWarnings);
	}

	public void applyTotalTransformation(IRStatus<PIR> status,
			ITotalTransformation trans)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		if(!status.canBeGenerated())
		{
			throw new org.overture.codegen.ir.analysis.AnalysisException("Cannot apply total transformation to a status that cannot be generated!");
		}
		
		codeGenInfo.clearTransformationWarnings();

		status.getIrNode().apply(trans);
		HashSet<IrNodeInfo> transformationWarnings = new HashSet<>(codeGenInfo.getTransformationWarnings());
		status.addTransformationWarnings(transformationWarnings);
		status.setIrNode(trans.getResult());
	}

	public IRStatus<SExpIR> generateFrom(PExp exp) throws AnalysisException
	{
		codeGenInfo.clearNodes();

		SExpIR expCg = exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
		Set<VdmNodeInfo> unsupportedNodes = new HashSet<>(codeGenInfo.getUnsupportedNodes());

		return new IRStatus<SExpIR>(exp, "expression",expCg, unsupportedNodes);
	}

	public List<String> getQuoteValues()
	{
		return codeGenInfo.getQuoteValues();
	}

	public IRInfo getIRInfo()
	{
		return codeGenInfo;
	}
}
