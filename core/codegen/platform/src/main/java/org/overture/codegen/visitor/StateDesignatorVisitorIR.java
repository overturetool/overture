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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStateDesignatorIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.statements.AFieldStateDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.statements.AMapSeqStateDesignatorIR;
import org.overture.codegen.ir.IRInfo;

public class StateDesignatorVisitorIR extends
		AbstractVisitorIR<IRInfo, SStateDesignatorIR>
{
	@Override
	public SStateDesignatorIR caseAFieldStateDesignator(
			AFieldStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator stateDesignator = node.getObject();
		String fieldName = node.getField().getName();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SStateDesignatorIR stateDesignatorCg = stateDesignator.apply(question.getStateDesignatorVisitor(), question);

		AFieldStateDesignatorIR field = new AFieldStateDesignatorIR();
		field.setType(typeCg);
		field.setObject(stateDesignatorCg);
		field.setField(fieldName);

		return field;
	}

	@Override
	public SStateDesignatorIR caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		String name = node.getName().getName();
		String className = node.getName().getModule();
		boolean explicit = node.getName().getExplicit();
		boolean isLocal = question.getDeclAssistant().isLocal(node, question);
		
		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		AIdentifierStateDesignatorIR idStateDesignatorCg = new AIdentifierStateDesignatorIR();
		idStateDesignatorCg.setType(typeCg);
		idStateDesignatorCg.setName(name);
		idStateDesignatorCg.setClassName(className);
		idStateDesignatorCg.setExplicit(explicit);
		idStateDesignatorCg.setIsLocal(isLocal);

		return idStateDesignatorCg;
	}

	@Override
	public SStateDesignatorIR caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator mapSeq = node.getMapseq();
		PExp exp = node.getExp();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SStateDesignatorIR mapSeqCg = mapSeq.apply(question.getStateDesignatorVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AMapSeqStateDesignatorIR mapSeqStateDesignator = new AMapSeqStateDesignatorIR();
		mapSeqStateDesignator.setType(typeCg);
		mapSeqStateDesignator.setMapseq(mapSeqCg);
		mapSeqStateDesignator.setExp(expCg);

		return mapSeqStateDesignator;
	}
}
