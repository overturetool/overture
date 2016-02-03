package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.INode;
import org.overture.codegen.traces.ICallStmToStringMethodBuilder;
import org.overture.codegen.traces.StoreAssistant;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TraceStmBuilder;
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
	public TraceStmBuilder consStmBuilder(StoreAssistant storeAssist, String traceEnclosingClass)
	{
		return new JmlTraceStmBuilder(this, traceEnclosingClass, storeAssist, tcExpInfo);
	}

	public List<TcExpInfo> getTcExpInfo()
	{
		return tcExpInfo;
	}
}
