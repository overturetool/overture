package org.overture.codegen.trans;

import org.overture.ast.types.ARealNumericBasicType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;

public class DivideTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	public DivideTransformation(IRInfo info)
	{
		this.info = info;
	}
	
	@Override
	public void caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node)
			throws AnalysisException
	{
		SExpCG leftExp = node.getLeft();
		leftExp.apply(this);
		
		SExpCG rightExp = node.getRight();
		rightExp.apply(this);

		if (info.getExpAssistant().isIntegerType(leftExp)
				&& info.getExpAssistant().isIntegerType(rightExp))
		{
			ARealLiteralExpCG one = new ARealLiteralExpCG();
			ARealNumericBasicTypeCG realTypeCg = new ARealNumericBasicTypeCG();
			realTypeCg.setSourceNode(new SourceNode(new ARealNumericBasicType()));
			one.setType(realTypeCg);
			one.setValue(1.0);

			ATimesNumericBinaryExpCG neutralMul = new ATimesNumericBinaryExpCG();
			neutralMul.setType(realTypeCg.clone());
			neutralMul.setLeft(one);
			neutralMul.setRight(leftExp);

			node.setLeft(info.getExpAssistant().isolateExpression(neutralMul));
		}
	}
}
