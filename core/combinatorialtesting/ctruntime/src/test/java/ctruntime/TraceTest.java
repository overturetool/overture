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
}