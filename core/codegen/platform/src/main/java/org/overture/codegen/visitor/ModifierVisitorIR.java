package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SModifierCG;
import org.overture.codegen.ir.expressions.ARecordModifierCG;
import org.overture.codegen.ir.IRInfo;

public class ModifierVisitorCG  extends AbstractVisitorCG<IRInfo, SModifierCG>
{
	@Override
	public SModifierCG caseARecordModifier(ARecordModifier node, IRInfo question)
			throws AnalysisException
	{
		ILexIdentifierToken tag = node.getTag();
		PExp value = node.getValue();
		
		String name = tag.getName();
		SExpCG recCg = value.apply(question.getExpVisitor(), question);
		
		ARecordModifierCG recModifier = new ARecordModifierCG();
		recModifier.setName(name);
		recModifier.setValue(recCg);
		
		return recModifier;
	}
	
	
}
