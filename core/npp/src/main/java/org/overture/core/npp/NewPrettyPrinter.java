package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;

/**
 * The Class PrettyPrinter is the main visitor for the VDM pretty printer package. It handles dispatching of nodes and
 * can be safely applied to any node in the AST. <br>
 * <br>
 * The general operating principle is that when applied to any node, the visitor will return the corresponding string of
 * the syntax block of that node. Indentation is controlled internally and always starts at 0 levels (the discipline is
 * caller increments/decrements). <br>
 * However, due to extensibility concerns, direct application of the class requires users to feed it ausers are advised
 * to instead use the public utility method available at: .
 */
public class NewPrettyPrinter extends
		QuestionAnswerAdaptor<IndentTracker, String> implements IPrettyPrinter
{

	private static final String NODE_NOT_FOUND_ERROR = "ERROR: Node Not Found.";

	/**
	 * The attribute table for handling non abstract syntax such as separators.
	 */
	InsTable mytable;
	ExpressionNpp expPrinter;

	// PatternNpp
	// BindNpp...

	/**
	 * Creates a VDM-syntax pretty printer. <br>
	 * <b>Warning:</b> this method pre-loads {@link VdmNsTable} attributes. Extensions should use
	 * {@link NewPrettyPrinter#PrettyPrinter(InsTable)} and configure it instead.
	 * 
	 * @return a new instance of {@link NewPrettyPrinter}
	 */
	public static NewPrettyPrinter newInstance()
	{
		return new NewPrettyPrinter(VdmNsTable.getInstance());
	}

	public static String prettyPrint(INode node) throws AnalysisException
	{
		String s = node.apply(newInstance(), new IndentTracker());
		return s.replace("\t", "  ");
	}

	/**
	 * Instantiates a new pretty printer for base ASTs.
	 * 
	 * @param nsTable
	 *            the attributes table for the printer
	 */
	public NewPrettyPrinter(InsTable nsTable)
	{
		mytable = nsTable;
		expPrinter = new ExpressionNpp(this, nsTable);

	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.core.npp.IPrettyPrinter#setInsTable(org.overture.core.npp .InsTable)
	 */
	@Override
	public void setInsTable(InsTable it)
	{
		mytable = it;
	}


	@Override
	public String defaultPExp(PExp node, IndentTracker question) throws AnalysisException
	{
		return node.apply(expPrinter, question);
	}

	

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return NODE_NOT_FOUND_ERROR;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return NODE_NOT_FOUND_ERROR;
	}

}
