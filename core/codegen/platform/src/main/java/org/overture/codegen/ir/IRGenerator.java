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
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.trans.ITotalTransformation;

public class IRGenerator
{
	private static final String OBJ_INIT_CALL_NAME_PREFIX = "cg_init_";
	
	protected IRInfo codeGenInfo;

	public IRGenerator()
	{
		this(OBJ_INIT_CALL_NAME_PREFIX);
	}
	
	public IRGenerator(String objectInitCallPrefix)
	{
		this.codeGenInfo = new IRInfo(objectInitCallPrefix);
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

	public IRStatus<INode> generateFrom(org.overture.ast.node.INode node)
			throws AnalysisException
	{
		codeGenInfo.clearNodes();

		if(node instanceof SClassDefinition)
		{
			SClassDeclCG classCg = node.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
			Set<VdmNodeInfo> unsupportedNodes = new HashSet<VdmNodeInfo>(codeGenInfo.getUnsupportedNodes());
			String name = ((SClassDefinition) node).getName().getName();
			
			return new IRStatus<INode>(name, classCg, unsupportedNodes);
		}
		else if(node instanceof AModuleModules)
		{
			AModuleDeclCG module = node.apply(codeGenInfo.getModuleVisitor(), codeGenInfo);
			Set<VdmNodeInfo> unsupportedNodes = new HashSet<VdmNodeInfo>(codeGenInfo.getUnsupportedNodes());
			String name = ((AModuleModules) node).getName().getName();
			
			return new IRStatus<INode>(name, module, unsupportedNodes);
		}
		
		return null;
	}
	
	public void applyPartialTransformation(IRStatus<? extends INode> status,
			org.overture.codegen.cgast.analysis.intf.IAnalysis transformation)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		codeGenInfo.clearTransformationWarnings();

		status.getIrNode().apply(transformation);
		HashSet<IrNodeInfo> transformationWarnings = new HashSet<IrNodeInfo>(codeGenInfo.getTransformationWarnings());

		status.addTransformationWarnings(transformationWarnings);
	}

	public void applyTotalTransformation(IRStatus<INode> status,
			ITotalTransformation trans)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		codeGenInfo.clearTransformationWarnings();

		status.getIrNode().apply(trans);
		HashSet<IrNodeInfo> transformationWarnings = new HashSet<IrNodeInfo>(codeGenInfo.getTransformationWarnings());
		status.addTransformationWarnings(transformationWarnings);
		status.setIrNode(trans.getResult());
	}

	public IRStatus<SExpCG> generateFrom(PExp exp) throws AnalysisException
	{
		codeGenInfo.clearNodes();

		SExpCG expCg = exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
		Set<VdmNodeInfo> unsupportedNodes = new HashSet<VdmNodeInfo>(codeGenInfo.getUnsupportedNodes());

		return new IRStatus<SExpCG>("expression",expCg, unsupportedNodes);
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
