/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core;

import org.overture.ide.debug.core.model.IVdmStackFrame;

public interface IDebugOptions
{

	abstract class Option
	{
		private final String name;

		public Option(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

	}

	public class BooleanOption extends Option
	{
		private final boolean defaultValue;

		public BooleanOption(String name, boolean defaultValue)
		{
			super(name);
			this.defaultValue = defaultValue;
		}

		public boolean getDefaultValue()
		{
			return defaultValue;
		}

	}

	public class IntegerOption extends Option
	{
		private final int defaultValue;

		public IntegerOption(String name, int defaultValue)
		{
			super(name);
			this.defaultValue = defaultValue;
		}

		public int getDefaultValue()
		{
			return defaultValue;
		}

	}

	public class StringOption extends Option
	{
		private final String defaultValue;

		public StringOption(String name, String defaultValue)
		{
			super(name);
			this.defaultValue = defaultValue;
		}

		public String getDefaultValue()
		{
			return defaultValue;
		}

	}

	boolean get(BooleanOption option);

	int get(IntegerOption option);

	String get(StringOption option);

	/**
	 * Filter the specified stack frames before they are returned to the client. Implementation should return copy of
	 * the array even if there are no modifications.
	 * 
	 * @param frames
	 * @return
	 */
	IVdmStackFrame[] filterStackLevels(IVdmStackFrame[] frames);

	/**
	 * @param stackFrames
	 * @return
	 */
	boolean isValidStack(IVdmStackFrame[] frames);

}
