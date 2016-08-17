
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
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SBindIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.patterns.ASeqBindIR;
import org.overture.codegen.ir.patterns.ASetBindIR;
import org.overture.codegen.ir.patterns.ATypeBindIR;

public class BindVisitorIR extends AbstractVisitorIR<IRInfo, SBindIR>
{
	@Override
	public SBindIR caseASetBind(ASetBind node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);

		PExp set = node.getSet();
		SExpIR setCg = set.apply(question.getExpVisitor(), question);

		ASetBindIR setBind = new ASetBindIR();
		setBind.setPattern(patternCg);
		setBind.setSet(setCg);

		return setBind;
	}

	@Override
	public SBindIR caseATypeBind(ATypeBind node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);

		PType boundType = node.getType();
		STypeIR boundTypeCg = boundType.apply(question.getTypeVisitor(), question);

		ATypeBindIR setBind = new ATypeBindIR();
		setBind.setPattern(patternCg);
		setBind.setType(boundTypeCg);

		return setBind;
	}

	@Override
	public SBindIR caseASeqBind(ASeqBind node, IRInfo question)
			throws AnalysisException
	{

		PPattern pattern = node.getPattern();
		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);

		PExp seq = node.getSeq();
		SExpIR seqCg = seq.apply(question.getExpVisitor(), question);

		ASeqBindIR seqBind = new ASeqBindIR();
		seqBind.setPattern(patternCg);
		seqBind.setSeq(seqCg);

		return seqBind;
	}
}
