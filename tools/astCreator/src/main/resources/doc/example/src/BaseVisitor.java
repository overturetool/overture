import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.expressions.AE1Exp;

@SuppressWarnings("serial")
public class BaseVisitor extends AnalysisAdaptor
{
	@Override
	public void caseAE1Exp(AE1Exp node)
	{
		System.out.println("Base-Hit AE1Exp"+ node);
	}
	
	
}
