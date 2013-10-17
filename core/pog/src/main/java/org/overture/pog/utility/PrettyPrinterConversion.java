package org.overture.pog.utility;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.pof.AVdmPoTree;
import org.overture.pof.PPoTree;
import org.overture.pof.analysis.AnswerPOFAdaptor;
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;

public class PrettyPrinterConversion extends AnswerPOFAdaptor<String>
{
	private PrettyPrinterVisitor overtureVisitor;
	
	

	public PrettyPrinterConversion()
	{
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String defaultPPoTree(PPoTree node) throws AnalysisException
	{
		throw new AnalysisException("Default POTree node reached.");
	}

	@Override
	public String caseAVdmPoTree(AVdmPoTree node)
			throws AnalysisException
	{
		PrettyPrinterEnv question = new PrettyPrinterEnv();
		overtureVisitor = new PrettyPrinterVisitor();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(node.getPredicate().apply(overtureVisitor,question));
		return sb.toString();
		
	}

	@Override
	public String createNewReturnValue(INode node)
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public String createNewReturnValue(Object node)
	{
		assert false : "Should not happen";
		return null;
	}
	
	
	

}
