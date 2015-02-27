package org.overturetool.cgisa;

import java.util.HashSet;

import org.overture.codegen.ir.IRClassDeclStatus;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.ILogger;
import org.overturetool.cgisa.ir.ExtIrClassDeclStatus;

public class ExtIrGenerator extends IRGenerator
{

	public ExtIrGenerator(ILogger log, String objectInitCallPrefix)
	{
		super(log, objectInitCallPrefix);
	}

	@Override
	public void applyTransformation(IRClassDeclStatus status,
			org.overture.codegen.cgast.analysis.intf.IAnalysis transformation)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		codeGenInfo.clearTransformationWarnings();

		if (status instanceof ExtIrClassDeclStatus)
		{
			((ExtIrClassDeclStatus) status).getEClassCg().apply(transformation);
		} else
		{
			status.getClassCg().apply(transformation);
		}
		HashSet<IrNodeInfo> transformationWarnings = new HashSet<IrNodeInfo>(codeGenInfo.getTransformationWarnings());

		status.addTransformationWarnings(transformationWarnings);
	}
}
