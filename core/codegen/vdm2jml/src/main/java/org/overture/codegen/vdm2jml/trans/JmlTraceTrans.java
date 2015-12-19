package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.traces.ICallStmToStringMethodBuilder;
import org.overture.codegen.traces.StoreAssistant;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class JmlTraceTrans extends TracesTrans
{
	private List<TcExpInfo> tcExpInfo;

	public JmlTraceTrans(TransAssistantCG transAssistant, IterationVarPrefixes iteVarPrefixes, TraceNames tracePrefixes,
			ILanguageIterator langIterator, ICallStmToStringMethodBuilder toStringBuilder, List<INode> cloneFreeNodes)
	{
		super(transAssistant, iteVarPrefixes, tracePrefixes, langIterator, toStringBuilder, cloneFreeNodes);
		this.tcExpInfo = new LinkedList<>();
	}

	@Override
	public SExpCG consTypeCheckExp(SVarExpCG arg, STypeCG formalParamType, String traceEnclosingClass,
			StoreAssistant storeAssistant)
	{
		/**
		 * Just assume for now that 'exp' is 'false' and that 'arg' does not type check. Later, 'exp' will be replaced
		 * with a proper dynamic type check
		 */
		SExpCG exp = transAssistant.getInfo().getExpAssistant().consBoolLiteral(false);

		String storeId = storeAssistant.getIdConstNameMap().get(arg.getName());
		String name = storeId == null ? arg.getName() : consLookup(storeId);

		tcExpInfo.add(new TcExpInfo(name, formalParamType, exp, traceEnclosingClass));

		return exp;
	}

	private String consLookup(String storeId)
	{
		return tracePrefixes.storeVarName() + "." + tracePrefixes.storeGetValueMethodName() + "(" + storeId + ")";
	}

	public List<TcExpInfo> getTcExpInfo()
	{
		return tcExpInfo;
	}
}
