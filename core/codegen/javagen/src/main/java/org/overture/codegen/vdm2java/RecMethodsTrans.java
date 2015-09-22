package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;

public class RecMethodsTrans extends DepthFirstAnalysisAdaptor
{
	private JavaRecordCreator recCreator;
	
	public RecMethodsTrans(JavaRecordCreator recCreator)
	{
		this.recCreator = recCreator;
	}
	
	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		for(ATypeDeclCG typeDecl : node.getTypeDecls())
		{
			if(typeDecl.getDecl() instanceof ARecordDeclCG)
			{
				ARecordDeclCG rec = (ARecordDeclCG) typeDecl.getDecl();
				
				rec.getMethods().addFirst(recCreator.genToStringMethod(rec));
				rec.getMethods().addFirst(recCreator.genCopyMethod(rec));
				rec.getMethods().addFirst(recCreator.genHashcodeMethod(rec));
				rec.getMethods().addFirst(recCreator.genEqualsMethod(rec));
				rec.getMethods().addFirst(recCreator.genRecConstructor(rec));
			}
		}
	}
}
