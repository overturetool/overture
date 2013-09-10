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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTThreadSwapMessage.SwapType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;


public class RTLogger
{
	private static boolean enabled = false;
	private static List<RTMessage> events = new LinkedList<RTMessage>();
	private static PrintWriter logfile = null;
	private static RTMessage cached = null;
	private static NextGenRTLogger nextGenLog = NextGenRTLogger.getInstance();
	
	public static synchronized void enable(boolean on)
	{
		if (!on)
		{
			dump(true);
			cached = null;
		}

		enabled = on;
	}

	
	public static synchronized void log(RTMessage message)
	{
		oldLog(message);
		nextGenLog.log(message);
	}
	
	public static synchronized void oldLog(RTMessage message)
	{
		if (!enabled)
		{
			return;
		}
		// generate any static deploys required for this message
		message.generateStaticDeploys();
		
		doLog(message);

	}

	
	private static synchronized void doLog(RTMessage message)
	{
		RTMessage event = message;

		if (event instanceof RTThreadSwapMessage && (((RTThreadSwapMessage)event).getType()==SwapType.In 
				|| ((RTThreadSwapMessage)event).getType()==SwapType.DelayedIn))
		{
			if (cached != null)
			{
				doInternalLog(cached);
			}

			cached = event;
			return;
		}

		if (cached != null)
		{
			if (event instanceof RTThreadSwapMessage && ((RTThreadSwapMessage)event).getType()==SwapType.Out)
			{
				RTThreadMessage eventThreadMessage = (RTThreadMessage) event;
				if(cached instanceof RTThreadSwapMessage)
				{
					RTThreadSwapMessage cachedThreadSwap = (RTThreadSwapMessage) cached;
					
					if((cachedThreadSwap.getType() == SwapType.DelayedIn || cachedThreadSwap.getType() == SwapType.In)
							&& cachedThreadSwap.equals(eventThreadMessage.getThread())
							&& cachedThreadSwap.getLogTime().equals(eventThreadMessage.getLogTime()))
					{
						cached = null;
						return;
					}
						
				}
			}

			doInternalLog(cached);
			cached = null;
		}

		doInternalLog(event);
	}

	
	private static void doInternalLog(RTMessage event)
	{
		if (logfile == null)
		{
			Console.out.println(event);
		} else
		{
			events.add(event);

			if (events.size() > 1000)
			{
				dump(false);
			}
		}
	}

	public static void setLogfile(PrintWriter out)
	{
		enabled = true;
		dump(true); // Write out and close previous
		logfile = out;
		cached = null;
	}

	public static int getLogSize()
	{
		return events.size();
	}

	public static synchronized void dump(boolean close)
	{
		if (logfile != null)
		{
			for (RTMessage event : events)
			{
				logfile.println(event.getMessage());
			}

			logfile.flush();
			events.clear();

			if (close)
			{
				logfile.close();
			}
			
			try
			{
				nextGenLog.persistToFile();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
