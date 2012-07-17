import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.node.INode;


@SuppressWarnings("serial")
public class DBaseVisitor extends DepthFirstAnalysisAdaptor
{
	public final List<INode> visitedNodes = new Vector<INode>();
	
	@Override
	public void caseAE1Exp(AE1Exp node)
	{
		visitedNodes.add(node);
		super.caseAE1Exp(node);
	}
	
	@Override
	public void caseAE3Exp(AE3Exp node)
	{
		visitedNodes.add(node);
		super.caseAE3Exp(node);
	}
}
