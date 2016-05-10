/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.messages.rtlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

/**
 * Real-time logger. This logger delegates the actual logging to its internal loggers that each must have an output
 * location specified through {@link RTLogger#setLogfile(Class, File)}
 * 
 * @author kel
 */
public class RTLogger
{
	static int eventCount = 0;
	static boolean enabled = false;

	private static List<IRTLogger> loggers = new Vector<>();

	static
	{
		loggers.add(new RTTextLogger());
		loggers.add(new NextGenRTLogger());
	}

	/**
	 * Turns the logging on or off
	 * 
	 * @param on
	 */
	public static synchronized void enable(boolean on)
	{
		for (IRTLogger logger : loggers)
		{
			logger.enable(on);
		}

		enabled = on;
	}

	/**
	 * Log a message
	 * 
	 * @param message
	 */
	public static synchronized void log(RTMessage message)
	{
		if (enabled)
		{
			eventCount++;

			for (IRTLogger logger : loggers)
			{
				logger.log(message);
			}
		}
	}

	/**
	 * Configure the logger output types
	 * 
	 * @param loggerClass
	 *            the logger class
	 * @param outputFile
	 *            the output file
	 * @throws FileNotFoundException
	 */
	public static void setLogfile(Class<? extends IRTLogger> loggerClass,
			File outputFile) throws FileNotFoundException
	{
		for (IRTLogger logger : loggers)
		{
			if (logger.getClass() == loggerClass)
			{
				logger.setLogfile(outputFile);
				logger.dump(true); // Write out and close previous
			}
		}

		enable(true);
	}

	/**
	 * Gets an estimate of events that may not already have been persisted
	 * 
	 * @return
	 */
	public static int getLogSize()
	{
		return eventCount;
	}

	/**
	 * Force a dump of cached log messages if any
	 * 
	 * @param close
	 */
	public static synchronized void dump(boolean close)
	{
		for (IRTLogger logger : loggers)
		{
			logger.dump(close);
		}

		eventCount = 0;
	}
}
