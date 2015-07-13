/*
 * #%~
 * The Java Code Generator Runtime
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
package org.overture.codegen.runtime.traces;

import java.io.Serializable;

public class TraceTest implements Serializable
{
	private static final long serialVersionUID = 1089982782591444372L;

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
		if (obj == null)
		{
			return false;
		}

		if (getClass() != obj.getClass())
		{
			return false;
		}

		final TraceTest other = (TraceTest) obj;

		if (this.no == null ? other.no != null : !this.no.equals(other.no))
		{
			return false;
		}

		if (this.test == null ? other.test != null
				: !this.test.equals(other.test))
		{
			return false;
		}

		if (this.result == null ? other.result != null
				: !this.result.equals(other.result))
		{
			return false;
		}

		if (this.verdict == null ? other.verdict != null
				: !this.verdict.equals(other.verdict))
		{
			return false;
		}

		return true;
	}
}
