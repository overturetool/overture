/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer.data;

public class Conjecture
{
	public enum ConjectureType {SOURCE, DESTINATION}
	
	private ConjectureType type;
	private Long time;
    private Long threadID;
    private String name;

    public Conjecture(Long time, Long threadID, String name, ConjectureType type)
    {
    	this.type = type;
    	this.time = time;
    	this.threadID = threadID;
    	this.name = name;
    }

	public Long getTime()
	{
		return time;
	}

	public Long getThreadID()
	{
		return threadID;
	}

	public String getName()
	{
		return name;
	}
	
    public ConjectureType getType()
    {
		return type;
	}
}
