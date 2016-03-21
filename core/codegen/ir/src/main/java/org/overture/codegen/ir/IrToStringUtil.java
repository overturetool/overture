package org.overture.codegen.ir;

import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;

public class IrToStringUtil
{
	public static String getSimpleBlockString(ABlockStmIR node)
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (SStmIR s : node.getStatements())
		{
			sb.append(sep);
			sb.append(s.toString());
			sep = ";\n";
		}

		sb.append("\n");
		return sb.toString();
	}

	public static String getBlockSimpleBlockString(ABlockStmIR node)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		for (AVarDeclIR d : node.getLocalDefs())
		{
			sb.append(d);
			sb.append("\n");
		}

		sb.append("\n");
		sb.append(getSimpleBlockString(node));
		sb.append("}");
		return sb.toString();
	}
}
