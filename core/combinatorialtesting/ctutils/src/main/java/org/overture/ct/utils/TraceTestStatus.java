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
