package org.overture.codegen.nodes;

import org.overture.codegen.constants.ITextConstants;


public class IfStatementCG implements IStatementCG
{
	private String exp;
	private IfStatementCG elseStm;

	@Override
	public String generate()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("if (").append(exp).append(") ").append("then ").append(ITextConstants.NEW_LINE);
		sb.append("{").append(ITextConstants.NEW_LINE);

		sb.append(ITextConstants.INDENT);
		sb.append(elseStm.generate());

		sb.append("}").append(ITextConstants.NEW_LINE);

		return sb.toString();
	}

}
