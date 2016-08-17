package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;

public class RecMethodsTrans extends DepthFirstAnalysisAdaptor
{
	private JavaRecordCreator recCreator;

	public RecMethodsTrans(JavaRecordCreator recCreator)
	{
		this.recCreator = recCreator;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		for (ATypeDeclIR typeDecl : node.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ARecordDeclIR)
			{
				ARecordDeclIR rec = (ARecordDeclIR) typeDecl.getDecl();

				rec.getMethods().addFirst(recCreator.genToStringMethod(rec));
				rec.getMethods().addFirst(recCreator.genCopyMethod(rec));
				rec.getMethods().addFirst(recCreator.genHashcodeMethod(rec));
				rec.getMethods().addFirst(recCreator.genEqualsMethod(rec));
				rec.getMethods().addFirst(recCreator.genRecConstructor(rec));
			}
		}
	}
}
