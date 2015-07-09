package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;

public class RecordMetodsTransformation extends DepthFirstAnalysisAdaptor
{
	private JavaRecordCreator recCreator;
	
	public RecordMetodsTransformation(JavaRecordCreator recCreator)
	{
		this.recCreator = recCreator;
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		for(ATypeDeclCG typeDecl : node.getTypeDecls())
		{
			if(typeDecl.getDecl() instanceof ARecordDeclCG)
			{
				ARecordDeclCG rec = (ARecordDeclCG) typeDecl.getDecl();
				
				rec.getMethods().add(recCreator.genRecConstructor(rec));
				rec.getMethods().add(recCreator.genEqualsMethod(rec));
				rec.getMethods().add(recCreator.genHashcodeMethod(rec));
				rec.getMethods().add(recCreator.genCopyMethod(rec));
				rec.getMethods().add(recCreator.genToStringMethod(rec));
			}
		}
	}
}
