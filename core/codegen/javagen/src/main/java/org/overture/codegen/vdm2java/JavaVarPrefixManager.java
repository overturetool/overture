package org.overture.codegen.vdm2java;

import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.conc.ConcPrefixes;
import org.overture.codegen.trans.funcvalues.FuncValPrefixes;
import org.overture.codegen.trans.patterns.PatternVarPrefixes;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;

public class JavaVarPrefixManager
{
	protected IterationVarPrefixes iteVarPrefixes;
	protected TraceNames tracePrefixes;
	protected Exp2StmVarPrefixes exp2stmPrefixes;
	protected FuncValPrefixes funcValPrefixes;
	protected PatternVarPrefixes patternPrefixes;
	protected UnionTypeVarPrefixes unionTypePrefixes;
	protected ConcPrefixes concPrefixes;

	public JavaVarPrefixManager()
	{
		this.iteVarPrefixes = new IterationVarPrefixes();
		this.tracePrefixes = new TraceNames();
		this.exp2stmPrefixes = new Exp2StmVarPrefixes();
		this.funcValPrefixes = new FuncValPrefixes();
		this.patternPrefixes = new PatternVarPrefixes();
		this.unionTypePrefixes = new UnionTypeVarPrefixes();
		this.concPrefixes = new ConcPrefixes();
	}

	public void setTempVarPrefixes(IterationVarPrefixes iteVarPrefixes)
	{
		this.iteVarPrefixes = iteVarPrefixes;
	}

	public IterationVarPrefixes getIteVarPrefixes()
	{
		return iteVarPrefixes;
	}

	public TraceNames getTracePrefixes()
	{
		return tracePrefixes;
	}

	public void setTracePrefixes(TraceNames tracePrefixes)
	{
		this.tracePrefixes = tracePrefixes;
	}

	public Exp2StmVarPrefixes getExp2stmPrefixes()
	{
		return exp2stmPrefixes;
	}

	public void setExp2stmPrefixes(Exp2StmVarPrefixes exp2stmPrefixes)
	{
		this.exp2stmPrefixes = exp2stmPrefixes;
	}

	public FuncValPrefixes getFuncValPrefixes()
	{
		return funcValPrefixes;
	}

	public void setFuncValPrefixes(FuncValPrefixes funcValPrefixes)
	{
		this.funcValPrefixes = funcValPrefixes;
	}

	public PatternVarPrefixes getPatternPrefixes()
	{
		return patternPrefixes;
	}

	public void setPatternPrefixes(PatternVarPrefixes patternPrefixes)
	{
		this.patternPrefixes = patternPrefixes;
	}

	public UnionTypeVarPrefixes getUnionTypePrefixes()
	{
		return unionTypePrefixes;
	}

	public void setUnionTypePrefixes(UnionTypeVarPrefixes unionTypePrefixes)
	{
		this.unionTypePrefixes = unionTypePrefixes;
	}

	public ConcPrefixes getConcPrefixes()
	{
		return concPrefixes;
	}

	public void setConcPrefixes(ConcPrefixes concPrefixes)
	{
		this.concPrefixes = concPrefixes;
	}

	public String postCheckMethodName()
	{
		return "postCheck";
	}

	public String casesExp()
	{
		return "casesExp_";
	}

	public String whileCond()
	{
		return "whileCond_";
	}

	public String isExpSubject()
	{
		return "isExpSubject_";
	}

	public String funcRes()
	{
		return "funcResult_";
	}

	public String atomicTmpVar()
	{
		return "atomicTmp_";
	}
}
