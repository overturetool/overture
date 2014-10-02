package org.overture.codegen.vdm2java;

import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.ir.IRInfo;

public class JavaClassCreator
{
	private IRInfo info;
	
	public JavaClassCreator(IRInfo info)
	{
		this.info = info;
	}
	
	public AMethodDeclCG generateToString(AClassDeclCG classDecl)
	{
		//Example: A{#32, x := 4, c = "STD"} (ID is omitted)
		
		AMethodDeclCG toStringMethod = new AMethodDeclCG();

		toStringMethod.setIsConstructor(false);
		toStringMethod.setAccess(JavaFormat.JAVA_PUBLIC);
		toStringMethod.setName("toString");

		AStringTypeCG returnType = new AStringTypeCG();

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);

		toStringMethod.setMethodType(methodType);
		
		LinkedList<AFieldDeclCG> fields = classDecl.getFields();

		AReturnStmCG body = new AReturnStmCG();
		
		if (fields.isEmpty())
		{
			body.setExp(info.getExpAssistant().consStringLiteral(classDecl.getName() + "{}", false));
			
		} else
		{
			ASeqConcatBinaryExpCG stringBuffer = new ASeqConcatBinaryExpCG();
			// "A{#"
			AStringLiteralExpCG strStart = info.getExpAssistant().consStringLiteral(classDecl.getName()
					+ "{", false);

			stringBuffer.setType(returnType.clone());
			stringBuffer.setLeft(strStart);

			ASeqConcatBinaryExpCG previous = stringBuffer;

			// Append instance variables and values
			for (int i = 0; i < fields.size(); i++)
			{
				AFieldDeclCG field = fields.get(i);
				ASeqConcatBinaryExpCG next = consNext(returnType, previous, field, i > 0);
				previous = next;
			}

			// "}"
			AStringLiteralExpCG strEnd = info.getExpAssistant().consStringLiteral("}", false);
			previous.setRight(strEnd);
			body.setExp(stringBuffer);
		}
		
		toStringMethod.setBody(body);
		
		return toStringMethod;
	}

	private ASeqConcatBinaryExpCG consNext(AStringTypeCG returnType,
			ASeqConcatBinaryExpCG previous, AFieldDeclCG field, boolean separate)
	{
		ASeqConcatBinaryExpCG next = new ASeqConcatBinaryExpCG();
		next.setType(returnType.clone());
		next.setLeft(consFieldStr(field, separate));
		
		previous.setRight(next);
		
		return next;
	}

	private SExpCG consFieldStr(AFieldDeclCG field, boolean separate)
	{
		String left = "";
		
		if(separate)
		{
			left += ", ";
		}
		
		left += field.getName();
		left += field.getFinal() != null && field.getFinal() ? " = " : " := ";
		
		AApplyExpCG toStringCall = JavaFormatAssistant.consUtilCall(new AStringTypeCG(), "toString");
		
		AIdentifierVarExpCG fieldVar = new AIdentifierVarExpCG();
		fieldVar.setType(field.getType().clone());
		fieldVar.setIsLambda(false);
		fieldVar.setOriginal(field.getName());
		
		toStringCall.getArgs().add(fieldVar);
		
		ASeqConcatBinaryExpCG fieldStr = new ASeqConcatBinaryExpCG();
		fieldStr.setType(new AStringTypeCG());
		fieldStr.setLeft(info.getExpAssistant().consStringLiteral(left, false));
		fieldStr.setRight(toStringCall);
		
		return fieldStr;
	}
}
