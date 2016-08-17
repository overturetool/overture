package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SModifierIR;
import org.overture.codegen.ir.expressions.ARecordModifierIR;

public class ModifierVisitorIR extends AbstractVisitorIR<IRInfo, SModifierIR>
{
	@Override
	public SModifierIR caseARecordModifier(ARecordModifier node,
			IRInfo question) throws AnalysisException
	{
		ILexIdentifierToken tag = node.getTag();
		PExp value = node.getValue();

		String name = tag.getName();
		SExpIR recCg = value.apply(question.getExpVisitor(), question);

		ARecordModifierIR recModifier = new ARecordModifierIR();
		recModifier.setName(name);
		recModifier.setValue(recCg);

		return recModifier;
	}

}
