package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;

/**
 * This class is responsible for annotating declarations that can point to null.
 * 
 * @author pvj
 *
 */
public class NullableAnnotator extends DepthFirstAnalysisAdaptor
{
	private JmlGenerator jmlGen;
	
	public NullableAnnotator(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}
	
	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		handleNullable(node, node.getType(), node.getInitial());
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		handleNullable(node, node.getType(), node.getExp());
	}

	public void handleNullable(SDeclCG decl, STypeCG type, SExpCG initExp)
	{
		// Annotate the construct as @nullable if one of the following conditions
		// are met:
		//
		// The first check checks if the type allows null, e.g. char | [nat]
		//
		// Following two checks:
		// Some expressions code generate to null so we need to take those into account
		if (jmlGen.getJavaGen().getInfo().getTypeAssistant().allowsNull(type)
				|| initExp instanceof ANullExpCG
				|| initExp instanceof AUndefinedExpCG)
		{
			jmlGen.getAnnotator().appendMetaData(decl, jmlGen.getAnnotator().consMetaData(JmlGenerator.JML_NULLABLE));
		}
	}
}
