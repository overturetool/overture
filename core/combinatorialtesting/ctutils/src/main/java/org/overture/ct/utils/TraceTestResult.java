/*
 * #%~
 * Combinatorial Testing Utilities
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ct.utils;

import java.util.List;
import java.util.Vector;

public class TraceTestResult extends TraceTestStatus
{
	private List<String> arguments = new Vector<>();
	private List<String> results = new Vector<>();

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(List<String> results)
	{
		this.results = results;
	}

	/**
	 * @return the results
	 */
	public List<String> getResults()
	{
		return results;
	}

	/**
	 * @param arguments
	 *            the arguments to set
	 */
	public void setArguments(List<String> arguments)
	{
		this.arguments = arguments;
	}

	/**
	 * @return the arguments
	 */
	public List<String> getArguments()
	{
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
