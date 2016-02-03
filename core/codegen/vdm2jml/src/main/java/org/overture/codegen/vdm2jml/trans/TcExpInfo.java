package org.overture.codegen.vdm2jml.trans;

import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.statements.AMetaStmCG;

public class TcExpInfo
{
	private STypeCG formalParamType;
	private AMetaStmCG typeCheck;
	private String traceEnclosingClass;
	private String name;

	public TcExpInfo(String expRef, STypeCG formalParamType, AMetaStmCG typeCheck, String traceEnclosingClass)
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
	
	public STypeCG getFormalParamType()
	{
		return formalParamType;
	}

	public AMetaStmCG getTypeCheck()
	{
		return typeCheck;
	}

	public String getTraceEnclosingClass()
	{
		return traceEnclosingClass;
	}
}
