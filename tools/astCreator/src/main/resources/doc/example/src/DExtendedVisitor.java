import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptorInterpreter;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.node.INode;


@SuppressWarnings("serial")
public class DExtendedVisitor extends DepthFirstAnalysisAdaptorInterpreter
{
	public final List<INode> visitedNodes = new Vector<INode>();
	
	@Override
	public void caseAE1Exp(AE1Exp node)
	{
		visitedNodes.add(node);
		super.caseAE1Exp(node);
	}
	
	@Override
	public void caseAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		visitedNodes.add(node);
		super.caseAE2ExpInterpreter(node);
	}
	
	@Override
	public void caseAE3Exp(AE3Exp node)
	{
		visitedNodes.add(node);
		super.caseAE3Exp(node);
	}
	
	@Override
	public void caseAE3ExpInterpreter(AE3ExpInterpreter node)
	{
		visitedNodes.add(node);
		super.caseAE3ExpInterpreter(node);
	}
}
