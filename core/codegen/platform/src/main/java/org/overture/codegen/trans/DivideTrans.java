package org.overture.codegen.trans;

import org.overture.ast.types.ARealNumericBasicType;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ARealLiteralExpIR;
import org.overture.codegen.ir.expressions.ATimesNumericBinaryExpIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;

public class DivideTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	public DivideTrans(IRInfo info)
	{
		this.info = info;
	}
	
	@Override
	public void caseADivideNumericBinaryExpIR(ADivideNumericBinaryExpIR node)
			throws AnalysisException
	{
		SExpIR leftExp = node.getLeft();
		leftExp.apply(this);
		
		SExpIR rightExp = node.getRight();
		rightExp.apply(this);

		if (info.getExpAssistant().isIntegerType(leftExp)
				&& info.getExpAssistant().isIntegerType(rightExp))
		{
			ARealLiteralExpIR one = new ARealLiteralExpIR();
			ARealNumericBasicTypeIR realTypeCg = new ARealNumericBasicTypeIR();
			realTypeCg.setSourceNode(new SourceNode(new ARealNumericBasicType()));
			one.setType(realTypeCg);
			one.setValue(1.0);

			ATimesNumericBinaryExpIR neutralMul = new ATimesNumericBinaryExpIR();
			neutralMul.setType(realTypeCg.clone());
			neutralMul.setLeft(one);
			neutralMul.setRight(leftExp);

			node.setLeft(info.getExpAssistant().isolateExpression(neutralMul));
		}
	}
}
