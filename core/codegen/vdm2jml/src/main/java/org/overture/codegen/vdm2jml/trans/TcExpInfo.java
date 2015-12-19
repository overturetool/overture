package org.overture.codegen.vdm2jml.trans;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;

public class TcExpInfo
{
	private STypeCG formalParamType;
	private SExpCG typeCheck;
	private String traceEnclosingClass;
	private String name;

	public TcExpInfo(String name, STypeCG formalParamType, SExpCG typeCheck, String traceEnclosingClass)
	{
		super();
		this.name = name;
		this.formalParamType = formalParamType;
		this.typeCheck = typeCheck;
		this.traceEnclosingClass = traceEnclosingClass;
	}

	public String getName()
	{
		return name;
	}
	
	public STypeCG getFormalParamType()
	{
		return formalParamType;
	}

	public SExpCG getTypeCheck()
	{
		return typeCheck;
	}

	public String getTraceEnclosingClass()
	{
		return traceEnclosingClass;
	}
}
