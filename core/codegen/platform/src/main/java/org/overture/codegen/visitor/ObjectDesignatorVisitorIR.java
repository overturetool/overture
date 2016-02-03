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
package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SObjectDesignatorIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.AApplyObjectDesignatorIR;
import org.overture.codegen.ir.statements.AFieldObjectDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierObjectDesignatorIR;
import org.overture.codegen.ir.statements.ANewObjectDesignatorIR;
import org.overture.codegen.ir.statements.ASelfObjectDesignatorIR;
import org.overture.codegen.ir.IRInfo;

public class ObjectDesignatorVisitorIR extends
		AbstractVisitorIR<IRInfo, SObjectDesignatorIR>
{
	@Override
	public SObjectDesignatorIR caseAApplyObjectDesignator(
			AApplyObjectDesignator node, IRInfo question)
			throws AnalysisException
	{
		PObjectDesignator obj = node.getObject();
		SObjectDesignatorIR objCg = obj.apply(question.getObjectDesignatorVisitor(), question);

		AApplyObjectDesignatorIR applyObjDesignator = new AApplyObjectDesignatorIR();
		applyObjDesignator.setObject(objCg);

		LinkedList<SExpIR> newExpArgs = applyObjDesignator.getArgs();
		for (PExp arg : node.getArgs())
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);
			
			if (argCg != null)
			{
				newExpArgs.add(argCg);
			} else
			{
				return null;
			}
		}

		return applyObjDesignator;
	}

	@Override
	public SObjectDesignatorIR caseAFieldObjectDesignator(
			AFieldObjectDesignator node, IRInfo question)
			throws AnalysisException
	{
		PObjectDesignator obj = node.getObject();

		String fieldCg = null;
		String fieldModuleCg = null;

		if(node.getField() != null)
		{
			fieldCg = node.getField().getName();
			fieldModuleCg = node.getField().getModule();
		}
		else
		{
			fieldCg = node.getFieldName() != null ? node.getFieldName().getName() : null;
		}
		
		SObjectDesignatorIR objCg = obj.apply(question.getObjectDesignatorVisitor(), question);

		AFieldObjectDesignatorIR fieldObjDesignator = new AFieldObjectDesignatorIR();
		fieldObjDesignator.setFieldName(fieldCg);
		fieldObjDesignator.setFieldModule(fieldModuleCg);
		fieldObjDesignator.setObject(objCg);

		return fieldObjDesignator;
	}

	@Override
	public SObjectDesignatorIR caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, IRInfo question)
			throws AnalysisException
	{
		AVariableExp exp = node.getExpression();

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AIdentifierObjectDesignatorIR idObjDesignator = new AIdentifierObjectDesignatorIR();

		if (!(expCg instanceof SVarExpIR))
		{
			question.addUnsupportedNode(node, "Expected variable expression for identifier object designator. Got: "
					+ expCg);
			return null;
		}

		idObjDesignator.setExp((SVarExpIR) expCg);

		return idObjDesignator;
	}

	@Override
	public SObjectDesignatorIR caseANewObjectDesignator(
			ANewObjectDesignator node, IRInfo question)
			throws AnalysisException
	{
		ANewExp exp = node.getExpression();

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		ANewObjectDesignatorIR newObjDesignator = new ANewObjectDesignatorIR();

		if (!(expCg instanceof ANewExpIR))
		{
			question.addUnsupportedNode(node, "Expected expression of new object designator to be a 'new expression' but got: "
					+ expCg.getClass().getName());
			return null;
		}

		newObjDesignator.setExp((ANewExpIR) expCg);
		
		return newObjDesignator;
	}

	@Override
	public SObjectDesignatorIR caseASelfObjectDesignator(
			ASelfObjectDesignator node, IRInfo question)
			throws AnalysisException
	{
		return new ASelfObjectDesignatorIR();
	}

}
