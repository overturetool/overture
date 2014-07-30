package ctruntime;

import java.util.List;
import java.util.Vector;

public class TraceResult
{
	public String traceName;
	public List<TraceTest> tests = new Vector<TraceTest>();
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Trace name: " + traceName).append("\n");
		
		for(TraceTest test : tests)
		{
			sb.append(test).append("\n");
		}
		
		return sb.toString();
	}
}