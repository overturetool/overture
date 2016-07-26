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

import org.apache.log4j.Logger;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SObjectDesignatorIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.AnswerAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.ASelfExpIR;
import org.overture.codegen.ir.statements.AApplyObjectDesignatorIR;
import org.overture.codegen.ir.statements.AFieldObjectDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierObjectDesignatorIR;
import org.overture.codegen.ir.statements.ANewObjectDesignatorIR;
import org.overture.codegen.ir.statements.ASelfObjectDesignatorIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.SMapTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;

public class ObjectDesignatorToExpIR extends AnswerAdaptor<SExpIR>
{
	private IRInfo info;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	public ObjectDesignatorToExpIR(IRInfo info)
	{
		this.info = info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SExpIR caseAApplyObjectDesignatorIR(AApplyObjectDesignatorIR node)
			throws AnalysisException
	{
		SObjectDesignatorIR object = node.getObject();
		SourceNode sourceNode = node.getSourceNode();
		LinkedList<SExpIR> args = node.getArgs();

		SExpIR root = object.apply(this);

		STypeIR rootType = root.getType();
		STypeIR applyType = null;

		if (rootType instanceof SSeqTypeIR)
		{
			applyType = ((SSeqTypeIR) rootType).getSeqOf();
		} else if (rootType instanceof SMapTypeIR)
		{
			applyType = ((SMapTypeIR) rootType).getTo();
		} else if (rootType instanceof AMethodTypeIR)
		{
			applyType = ((AMethodTypeIR) rootType).getResult();
		}

		applyType = applyType.clone();

		AApplyExpIR applyExp = new AApplyExpIR();
		applyExp.setArgs((List<? extends SExpIR>) args.clone());
		applyExp.setRoot(root);
		applyExp.setType(applyType);
		applyExp.setSourceNode(sourceNode);

		return applyExp;
	}

	@Override
	public SExpIR caseAFieldObjectDesignatorIR(AFieldObjectDesignatorIR node)
			throws AnalysisException
	{
		String fieldName = node.getFieldName();
		String fieldModule = node.getFieldModule();
		SObjectDesignatorIR obj = node.getObject();
		SourceNode sourceNode = node.getSourceNode();

		INode parent = node.parent();

		STypeIR fieldExpType = null;
		try
		{
			fieldExpType = info.getTypeAssistant().getFieldExpType(info, fieldName, fieldModule, obj, parent);
		} catch (org.overture.ast.analysis.AnalysisException e)
		{
			log.error("Could not find field expression type of " + node);
			fieldExpType = new AUnknownTypeIR();
		}

		SExpIR objExp = obj.apply(this);

		AFieldExpIR fieldExp = new AFieldExpIR();
		fieldExp.setMemberName(fieldName);
		fieldExp.setType(fieldExpType);
		fieldExp.setObject(objExp);
		fieldExp.setSourceNode(sourceNode);

		return fieldExp;
	}

	@Override
	public SExpIR caseAIdentifierObjectDesignatorIR(
			AIdentifierObjectDesignatorIR node) throws AnalysisException
	{
		return node.getExp().clone();
	}

	@Override
	public SExpIR caseANewObjectDesignatorIR(ANewObjectDesignatorIR node)
			throws AnalysisException
	{
		return node.getExp().clone();
	}

	@Override
	public SExpIR caseASelfObjectDesignatorIR(ASelfObjectDesignatorIR node)
			throws AnalysisException
	{
		ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);

		String className = enclosingClass.getName();
		SourceNode sourceNode = node.getSourceNode();

		AClassTypeIR classType = new AClassTypeIR();
		classType.setName(className);

		ASelfExpIR self = new ASelfExpIR();
		self.setType(classType);
		self.setSourceNode(sourceNode);

		return self;
	}

	@Override
	public SExpIR createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public SExpIR createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
