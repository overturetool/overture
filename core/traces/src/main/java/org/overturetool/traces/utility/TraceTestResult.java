package org.overturetool.traces.utility;

import java.util.List;
import java.util.Vector;

public class TraceTestResult extends TraceTestStatus {
	private List<String> arguments = new Vector<String>();
	private List<String> results = new Vector<String>();
	/**
	 * @param results the results to set
	 */
public	void setResults(List<String> results) {
		this.results = results;
	}
	/**
	 * @return the results
	 */
public	List<String> getResults() {
		return results;
	}
	/**
	 * @param arguments the arguments to set
	 */
public	void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
	/**
	 * @return the arguments
	 */
public	List<String> getArguments() {
		return arguments;
	}
	
	public void addArgument(String argument)
	{
		arguments.add(argument);
	}
	public void addResult(String result)
	{
		results.add(result);
	}
	
	
	
}
