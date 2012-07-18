import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.statements.AS1StmInterpreter;

@SuppressWarnings("serial")
public class ExtendedVisitor extends BaseVisitor implements IAnalysisInterpreter
{
	
	@Override
	public void caseAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		System.out.println("Base-Hit AE1Exp" + node);
	}

	@Override
	public void caseTIntInterpreter(TIntInterpreter node)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseAE3ExpInterpreter(AE3ExpInterpreter node)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseAS1StmInterpreter(AS1StmInterpreter node)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseAE4ExpInterpreter(AE4ExpInterpreter node)
	{
		// TODO Auto-generated method stub
		
	}
}
