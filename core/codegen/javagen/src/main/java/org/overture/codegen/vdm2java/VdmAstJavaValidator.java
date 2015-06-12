package org.overture.codegen.vdm2java;

import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.VdmNodeInfo;

public class VdmAstJavaValidator extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	public VdmAstJavaValidator(IRInfo info)
	{
		this.info = info;
	}
	
	@Override
	public void caseAForAllExp(AForAllExp node) throws AnalysisException
	{
		validateQuantifiedExp(node, node.getBindList(), "forall expression");
	}
	
	@Override
	public void caseAExistsExp(AExistsExp node) throws AnalysisException
	{
		validateQuantifiedExp(node, node.getBindList(), "exists expression");
	}
	
	@Override
	public void caseAExists1Exp(AExists1Exp node) throws AnalysisException
	{
		if (info.getExpAssistant().outsideImperativeContext(node))
		{
			info.addUnsupportedNode(node, String.format("Generation of a %s is only supported within operations/functions", "exists1 expression"));
		}
		
		if (!(node.getBind() instanceof ASetBind))
		{
			info.addUnsupportedNode(node, String.format("Generation of a exist1 expression is only supported for set binds. Got: %s", node.getBind()));
		}
	}

	private void validateQuantifiedExp(PExp node, List<PMultipleBind> bindings, String nodeStr)
	{
		if (info.getExpAssistant().outsideImperativeContext(node))
		{
			info.addUnsupportedNode(node, String.format("Generation of a %s is only supported within operations/functions", nodeStr));
		}
		
		for(PMultipleBind multipleBind : bindings)
		{
			if (!(multipleBind instanceof ASetMultipleBind))
			{
				info.addUnsupportedNode(node, String.format("Generation of a %s is only supported for multiple set binds. Got: %s", nodeStr, multipleBind));
				return;
			}
		}
	}
	
	public boolean hasUnsupportedNodes()
	{
		return !info.getUnsupportedNodes().isEmpty();
	}
	
	public Set<VdmNodeInfo> getUnsupportedNodes()
	{
		return info.getUnsupportedNodes();
	}
}
