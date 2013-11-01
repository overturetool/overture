package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.visitor.CodeGenInfo;

public class TypeAssistantCG
{

	public static PTypeCG constructSeqType(SSeqTypeBase node, CodeGenInfo question)
			throws AnalysisException
	{
		PTypeCG seqOf = node.getSeqof().apply(question.getTypeVisitor(), question);

		// This is a special case since sequence of characters are strings
		if (seqOf instanceof ACharBasicTypeCG)
			return new AStringTypeCG();

		ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
		seqType.setSeqOf(seqOf);

		return seqType;
	}
	
}
