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
package org.overture.codegen.trans.conv;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AFieldObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ANewObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ASelfObjectDesignatorCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;

public class ObjectDesignatorToExpCG extends AnswerAdaptor<SExpCG>
{
	private IRInfo info;

	public ObjectDesignatorToExpCG(IRInfo info)
	{
		this.info = info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SExpCG caseAApplyObjectDesignatorCG(AApplyObjectDesignatorCG node)
			throws AnalysisException
	{
		SObjectDesignatorCG object = node.getObject();
		SourceNode sourceNode = node.getSourceNode();
		LinkedList<SExpCG> args = node.getArgs();

		SExpCG root = object.apply(this);

		STypeCG rootType = root.getType();
		STypeCG applyType = null;

		if (rootType instanceof SSeqTypeCG)
		{
			applyType = ((SSeqTypeCG) rootType).getSeqOf();
		} else if (rootType instanceof SMapTypeCG)
		{
			applyType = ((SMapTypeCG) rootType).getTo();
		} else if (rootType instanceof AMethodTypeCG)
		{
			applyType = ((AMethodTypeCG) rootType).getResult();
		}

		applyType = applyType.clone();

		AApplyExpCG applyExp = new AApplyExpCG();
		applyExp.setArgs((List<? extends SExpCG>) args.clone());
		applyExp.setRoot(root);
		applyExp.setType(applyType);
		applyExp.setSourceNode(sourceNode);

		return applyExp;
	}

	@Override
	public SExpCG caseAFieldObjectDesignatorCG(AFieldObjectDesignatorCG node)
			throws AnalysisException
	{
		String fieldName = node.getFieldName();
		String fieldModule = node.getFieldModule();
		SObjectDesignatorCG obj = node.getObject();
		SourceNode sourceNode = node.getSourceNode();

		INode parent = node.parent();

		STypeCG fieldExpType = null;
		try
		{
			fieldExpType = info.getTypeAssistant().getFieldExpType(info, fieldName, fieldModule, obj, parent);
		} catch (org.overture.ast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Could not find field expression type of " + node + " in 'ObjectDesignatorToExpCG'");
			fieldExpType = new AUnknownTypeCG();
		}

		SExpCG objExp = obj.apply(this);

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(fieldName);
		fieldExp.setType(fieldExpType);
		fieldExp.setObject(objExp);
		fieldExp.setSourceNode(sourceNode);

		return fieldExp;
	}

	@Override
	public SExpCG caseAIdentifierObjectDesignatorCG(
			AIdentifierObjectDesignatorCG node) throws AnalysisException
	{
		return node.getExp().clone();
	}

	@Override
	public SExpCG caseANewObjectDesignatorCG(ANewObjectDesignatorCG node)
			throws AnalysisException
	{
		return node.getExp().clone();
	}

	@Override
	public SExpCG caseASelfObjectDesignatorCG(ASelfObjectDesignatorCG node)
			throws AnalysisException
	{
		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

		String className = enclosingClass.getName();
		SourceNode sourceNode = node.getSourceNode();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(className);

		ASelfExpCG self = new ASelfExpCG();
		self.setType(classType);
		self.setSourceNode(sourceNode);

		return self;
	}

	@Override
	public SExpCG createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public SExpCG createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
