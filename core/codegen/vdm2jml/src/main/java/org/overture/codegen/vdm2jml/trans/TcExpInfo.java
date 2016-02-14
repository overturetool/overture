package org.overture.codegen.vdm2jml.trans;

import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.statements.AMetaStmIR;

public class TcExpInfo
{
	private STypeIR formalParamType;
	private AMetaStmIR typeCheck;
	private String traceEnclosingClass;
	private String name;

	public TcExpInfo(String expRef, STypeIR formalParamType, AMetaStmIR typeCheck, String traceEnclosingClass)
	{
		super();
		this.name = expRef;
		this.formalParamType = formalParamType;
		this.typeCheck = typeCheck;
		this.traceEnclosingClass = traceEnclosingClass;
	}

	public String getExpRef()
	{
		return name;
	}
	
	public STypeIR getFormalParamType()
	{
		return formalParamType;
	}

	public AMetaStmIR getTypeCheck()
	{
		return typeCheck;
	}

	public String getTraceEnclosingClass()
	{
		return traceEnclosingClass;
	}
}
