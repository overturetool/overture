package org.overture.codegen.ir;

import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;


public class CodeGenBase
{
	protected IRGenerator generator;
	protected TransAssistantCG transAssistant;
	protected TempVarPrefixes varPrefixes;
	protected TraceNames tracePrefixes;
	
	protected CodeGenBase()
	{
		super();
		this.generator = new IRGenerator();
	}
	
	public void setIRGenerator(IRGenerator generator)
	{
		this.generator = generator;
	}

	public IRGenerator getIRGenerator()
	{
		return generator;
	}

	public void setSettings(IRSettings settings)
	{
		generator.getIRInfo().setSettings(settings);
	}
	
	public IRSettings getSettings()
	{
		return generator.getIRInfo().getSettings();
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
	
	public void setTransAssistant(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	public TransAssistantCG getTransAssistant()
	{
		return transAssistant;
	}
	
	public void setTempVarPrefixes(TempVarPrefixes varPrefixes)
	{
		this.varPrefixes = varPrefixes;
	}
	
	public TempVarPrefixes getTempVarPrefixes()
	{
		return varPrefixes;
	}

	public TraceNames getTracePrefixes()
	{
		return tracePrefixes;
	}

	public void setTracePrefixes(TraceNames tracePrefixes)
	{
		this.tracePrefixes = tracePrefixes;
	}
}