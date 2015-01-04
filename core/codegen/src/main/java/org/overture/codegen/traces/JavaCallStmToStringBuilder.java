package org.overture.codegen.traces;

import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaClassCreatorBase;

public class JavaCallStmToStringBuilder extends JavaClassCreatorBase implements ICallStmToStringMethodBuilder
{
	@Override
	public AMethodDeclCG consToString(IRInfo info, SStmCG callStm)
	{
		AMethodDeclCG toStringMethod = consToStringSignature();

		AReturnStmCG body = new AReturnStmCG();

		if (callStm instanceof APlainCallStmCG)
		{
			APlainCallStmCG plainCall = (APlainCallStmCG) callStm;

			STypeCG type = plainCall.getClassType();
			String name = plainCall.getName();
			LinkedList<SExpCG> args = plainCall.getArgs();

			String prefix = "";

			if (type instanceof AClassTypeCG)
			{
				prefix = ((AClassTypeCG) type).getName() + "`";
			}

			prefix += name;

			body.setExp(appendArgs(info, args, prefix));

		}  else if (callStm instanceof ACallObjectStmCG)
		{
			ACallObjectStmCG callObj = (ACallObjectStmCG) callStm;

			SObjectDesignatorCG obj = callObj.getDesignator();
			String field = callObj.getFieldName();
			LinkedList<SExpCG> args = callObj.getArgs();

			String prefix = obj.toString();
			prefix += "." + field;

			body.setExp(appendArgs(info, args, prefix));
		} else
		{
			Logger.getLog().printErrorln("Expected statement to be a call statement or call object statement. Got: "
					+ callStm);
			body.setExp(info.getExpAssistant().consStringLiteral("Unknown", false));
		}

		toStringMethod.setBody(body);

		return toStringMethod;
	}
	
	private SExpCG appendArgs(IRInfo info, LinkedList<SExpCG> args, String prefix)
	{
		if (args == null || args.isEmpty())
		{
			return info.getExpAssistant().consStringLiteral(prefix + "()", false);
		}

		ASeqConcatBinaryExpCG str = new ASeqConcatBinaryExpCG();
		str.setType(new AStringTypeCG());
		str.setLeft(info.getExpAssistant().consStringLiteral(prefix + "(", false));

		ASeqConcatBinaryExpCG next = str;

		for (SExpCG arg : args)
		{
			ASeqConcatBinaryExpCG tmp = new ASeqConcatBinaryExpCG();
			tmp.setType(new AStringTypeCG());

			AApplyExpCG utilsToStrCall = consUtilsToStringCall();
			utilsToStrCall.getArgs().add(arg.clone());
			tmp.setLeft(utilsToStrCall);

			next.setRight(tmp);
			next = tmp;
		}

		next.setRight(info.getExpAssistant().consStringLiteral(")", false));
		
		return str;
	}
}
