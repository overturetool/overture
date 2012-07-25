package org.overturetool.ct.utils;

import org.overture.interpreter.traces.Verdict;

public class TraceTestStatus
{
	private Integer number = -1;
	private Verdict status = null;

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(Integer number)
	{
		this.number = number;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber()
	{
		return number;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Verdict status)
	{
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public Verdict getStatus()
	{
		return status;
	}

}
