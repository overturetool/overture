package org.overture.codegen.vdm2jml.trans;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;

public class TcExpInfo
{
	private SVarExpCG arg;
	private STypeCG formalParamType;
	private SExpCG typeCheck;
	private String traceEnclosingClass;

	public TcExpInfo(SVarExpCG arg, STypeCG formalParamType, SExpCG typeCheck, String traceEnclosingClass)
	{
		super();
		this.arg = arg;
		this.formalParamType = formalParamType;
		this.typeCheck = typeCheck;
		this.traceEnclosingClass = traceEnclosingClass;
	}

	public SVarExpCG getArg()
	{
		return arg;
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
