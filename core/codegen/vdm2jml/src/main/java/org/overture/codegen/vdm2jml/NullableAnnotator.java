package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;

public class NullableAnnotator extends DepthFirstAnalysisAdaptor
{
	private static final String JML_NULLABLE = "//@ nullable;";

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		handleNullable(node, node.getInitial());
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		handleNullable(node, node.getExp());
	}

	public void handleNullable(SDeclCG decl, SExpCG initExp)
	{
		if (initExp instanceof ANullExpCG || initExp instanceof AUndefinedExpCG)
		{
			JmlGenerator.appendMetaData(decl, JmlGenerator.consMetaData(JML_NULLABLE));
		}
	}
}
