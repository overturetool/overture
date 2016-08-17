package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ASeqConcatBinaryExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.traces.ICallStmToStringMethodBuilder;
import org.overture.codegen.traces.StoreAssistant;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.config.Settings;

public class JavaCallStmToStringBuilder extends JavaClassCreatorBase
		implements ICallStmToStringMethodBuilder
{
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public AMethodDeclIR consToString(IRInfo info, SStmIR callStm,
			Map<String, String> idConstNameMap, StoreAssistant storeAssistant,
			TransAssistantIR transAssistant)
	{
		AMethodDeclIR toStringMethod = consToStringSignature();

		AReturnStmIR body = new AReturnStmIR();

		if (callStm instanceof APlainCallStmIR)
		{
			APlainCallStmIR plainCall = (APlainCallStmIR) callStm;

			STypeIR type = plainCall.getClassType();
			String name = plainCall.getName();
			LinkedList<SExpIR> args = plainCall.getArgs();

			String prefix = "";

			if (type instanceof AClassTypeIR)
			{
				prefix = ((AClassTypeIR) type).getName() + "`";
			}

			prefix += name;

			body.setExp(appendArgs(info, args, prefix, idConstNameMap, storeAssistant, transAssistant));

		} else if (callStm instanceof ACallObjectExpStmIR)
		{
			ACallObjectExpStmIR callObj = (ACallObjectExpStmIR) callStm;

			SExpIR obj = callObj.getObj();
			String field = callObj.getFieldName();
			LinkedList<SExpIR> args = callObj.getArgs();

			String prefix = obj.toString();
			prefix += "." + field;

			body.setExp(appendArgs(info, args, prefix, idConstNameMap, storeAssistant, transAssistant));
		}
		// The CallObjectStmIR node has been transformed out of the tree
		else
		{
			log.error("Expected statement to be a call statement or call object statement. Got: "
					+ callStm);
			body.setExp(info.getExpAssistant().consStringLiteral("Unknown", false));
		}

		toStringMethod.setBody(body);

		return toStringMethod;
	}

	private SExpIR appendArgs(IRInfo info, List<SExpIR> args, String prefix,
			Map<String, String> idConstNameMap, StoreAssistant storeAssistant,
			TransAssistantIR transAssistant)
	{
		if (args == null || args.isEmpty())
		{
			return info.getExpAssistant().consStringLiteral(prefix
					+ "()", false);
		}

		ASeqConcatBinaryExpIR str = new ASeqConcatBinaryExpIR();
		str.setType(new AStringTypeIR());
		str.setLeft(info.getExpAssistant().consStringLiteral(prefix
				+ "(", false));

		ASeqConcatBinaryExpIR next = str;

		for (SExpIR arg : args)
		{
			ASeqConcatBinaryExpIR tmp = new ASeqConcatBinaryExpIR();
			tmp.setType(new AStringTypeIR());

			AApplyExpIR utilsToStrCall = consUtilsToStringCall();

			if (arg instanceof AIdentifierVarExpIR
					&& idConstNameMap.containsKey(((AIdentifierVarExpIR) arg).getName()))
			{
				AIdentifierVarExpIR idVarExp = (AIdentifierVarExpIR) arg;
				if (Settings.dialect != Dialect.VDM_SL)
				{
					utilsToStrCall.getArgs().add(storeAssistant.consStoreLookup(idVarExp, true));
				} else
				{
					utilsToStrCall.getArgs().add(idVarExp);
				}
			} else
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
	public AApplyExpIR toStringOf(SExpIR exp)
	{
		AApplyExpIR utilsToStrCall = consUtilsToStringCall();
		utilsToStrCall.getArgs().add(exp);

		return utilsToStrCall;
	}
}
