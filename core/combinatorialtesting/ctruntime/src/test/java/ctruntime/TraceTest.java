package ctruntime;

import org.overture.interpreter.traces.Verdict;

public class TraceTest
{
	private Integer no;
	private String test;
	private String result;
	private Verdict verdict;
	
	public TraceTest(Integer no, String test, String result, Verdict verdict)
	{
		super();
		this.no = no;
		this.test = test;
		this.result = result;
		this.verdict = verdict;
	}
	
	public Integer getNo()
	{
		return no;
	}
	
	public String getTest()
	{
		return test;
	}
	
	public String getResult()
	{
		return result;
	}
	
	public Verdict getVerdict()
	{
		return verdict;
	}
	
	@Override
	public String toString()
	{
		return String.format("TraceTest: Number: %s Test: %s Result: %s Verdict: %s", no, test, result, verdict);
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = 0;
		
		hashCode += no != null ? no.hashCode() : 0;
		hashCode += test != null ? test.hashCode() : 0;
		hashCode += result != null ? result.hashCode() : 0;
		hashCode += verdict != null ? verdict.hashCode() : 0;
		
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(getClass() != obj.getClass())
		{
			return false;
		}
		
		final TraceTest other = (TraceTest) obj;
		
		if(this.no == null ? other.no != null : !this.no.equals(other.no))
		{
			return false;
		}
		
		if(this.test == null ? other.test != null : !this.test.equals(other.test))
		{
			return false;
		}
		
		if(this.result == null ? other.result != null : !this.result.equals(other.result))
		{
			return false;
		}
		
		if(this.verdict == null ? other.verdict != null : !this.verdict.equals(other.verdict))
		{
			return false;
		}

		return true;
	}
}