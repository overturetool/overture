package org.overture.codegen.vdm2java;

import java.util.LinkedList;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ASeqConcatBinaryExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AStringTypeIR;

public class JavaClassCreator extends JavaClassCreatorBase
{
	private IRInfo info;

	public JavaClassCreator(IRInfo info)
	{
		this.info = info;
	}

	public AMethodDeclIR generateToString(ADefaultClassDeclIR classDecl)
	{
		// Example: A{#32, x := 4, c = "STD"} (ID is omitted)

		AMethodDeclIR toStringMethod = consToStringSignature();

		LinkedList<AFieldDeclIR> fields = classDecl.getFields();

		AReturnStmIR body = new AReturnStmIR();

		AStringTypeIR returnType = new AStringTypeIR();

		if (fields.isEmpty())
		{
			body.setExp(info.getExpAssistant().consStringLiteral(classDecl.getName()
					+ "{}", false));

		} else
		{
			ASeqConcatBinaryExpIR stringBuffer = new ASeqConcatBinaryExpIR();
			// "A{#"
			AStringLiteralExpIR strStart = info.getExpAssistant().consStringLiteral(classDecl.getName()
					+ "{", false);

			stringBuffer.setType(returnType.clone());
			stringBuffer.setLeft(strStart);

			ASeqConcatBinaryExpIR previous = stringBuffer;

			// Append instance variables and values
			for (int i = 0; i < fields.size(); i++)
			{
				AFieldDeclIR field = fields.get(i);
				ASeqConcatBinaryExpIR next = consNext(returnType, previous, field, i > 0);
				previous = next;
			}

			// "}"
			AStringLiteralExpIR strEnd = info.getExpAssistant().consStringLiteral("}", false);
			previous.setRight(strEnd);
			body.setExp(stringBuffer);
		}

		toStringMethod.setBody(body);

		return toStringMethod;
	}

	private ASeqConcatBinaryExpIR consNext(AStringTypeIR returnType,
			ASeqConcatBinaryExpIR previous, AFieldDeclIR field,
			boolean separate)
	{
		ASeqConcatBinaryExpIR next = new ASeqConcatBinaryExpIR();
		next.setType(returnType.clone());
		next.setLeft(consFieldStr(field, separate));

		previous.setRight(next);

		return next;
	}

	private SExpIR consFieldStr(AFieldDeclIR field, boolean separate)
	{
		String left = "";

		if (separate)
		{
			left += ", ";
		}

		left += field.getName();
		left += field.getFinal() != null && field.getFinal() ? " = " : " := ";

		AApplyExpIR toStringCall = consUtilsToStringCall();

		AIdentifierVarExpIR fieldVar = new AIdentifierVarExpIR();
		fieldVar.setType(field.getType().clone());
		fieldVar.setIsLambda(false);
		fieldVar.setIsLocal(false);
		fieldVar.setName(field.getName());

		toStringCall.getArgs().add(fieldVar);

		ASeqConcatBinaryExpIR fieldStr = new ASeqConcatBinaryExpIR();
		fieldStr.setType(new AStringTypeIR());
		fieldStr.setLeft(info.getExpAssistant().consStringLiteral(left, false));
		fieldStr.setRight(toStringCall);

		return fieldStr;
	}
}
