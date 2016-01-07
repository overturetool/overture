package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.traces.ICallStmToStringMethodBuilder;
import org.overture.codegen.traces.StoreAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class JavaCallStmToStringBuilder extends JavaClassCreatorBase implements ICallStmToStringMethodBuilder
{
	@Override
	public AMethodDeclCG consToString(IRInfo info, SStmCG callStm, Map<String, String> idConstNameMap, StoreAssistant storeAssistant, TransAssistantCG transAssistant)
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

			body.setExp(appendArgs(info, args, prefix, idConstNameMap, storeAssistant, transAssistant));

		}  else if (callStm instanceof ACallObjectExpStmCG)
		{
			ACallObjectExpStmCG callObj = (ACallObjectExpStmCG) callStm;

			SExpCG obj = callObj.getObj();
			String field = callObj.getFieldName();
			LinkedList<SExpCG> args = callObj.getArgs();

			String prefix = obj.toString();
			prefix += "." + field;

			body.setExp(appendArgs(info, args, prefix, idConstNameMap, storeAssistant, transAssistant));
		}
		// The CallObjectStmCG node has been transformed out of the tree
		else
		{
			Logger.getLog().printErrorln("Expected statement to be a call statement or call object statement. Got: "
					+ callStm);
			body.setExp(info.getExpAssistant().consStringLiteral("Unknown", false));
		}

		toStringMethod.setBody(body);

		return toStringMethod;
	}
	
	private SExpCG appendArgs(IRInfo info, List<SExpCG> args, String prefix, Map<String, String> idConstNameMap, StoreAssistant storeAssistant, TransAssistantCG transAssistant)
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
			
			if(arg instanceof AIdentifierVarExpCG && idConstNameMap.containsKey(((AIdentifierVarExpCG) arg).getName()))
			{
				AIdentifierVarExpCG idVarExp = ((AIdentifierVarExpCG) arg);
				utilsToStrCall.getArgs().add(storeAssistant.consStoreLookup(idVarExp, true));
			}
			else
			{
				utilsToStrCall.getArgs().add(arg.clone());
			}
			
			tmp.setLeft(utilsToStrCall);

			next.setRight(tmp);
			next = tmp;
		}

		next.setRight(info.getExpAssistant().consStringLiteral(")", false));
		
		return str;
	}

	@Override
	public AApplyExpCG toStringOf(SExpCG exp)
	{
		AApplyExpCG utilsToStrCall = consUtilsToStringCall();
		utilsToStrCall.getArgs().add(exp);
		
		return utilsToStrCall;
	}
}
