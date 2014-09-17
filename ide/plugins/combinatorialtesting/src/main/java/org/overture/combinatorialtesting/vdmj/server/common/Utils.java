/*
 * #%~
 * Combinatorial Testing
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
package org.overture.combinatorialtesting.vdmj.server.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Utils
{
	public static void pause(int secs)
	{
		milliPause(secs * 1000);
	}

	public static void milliPause(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
			// ?
		}
	}

	public static boolean isInt(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}
	}

	public static int parseInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		} catch (NumberFormatException e)
		{
			return 0;
		}
	}

	/*
	 * This method intends to provide some of the functionality provided by ProcessBuilder.inheritIO(), which is Java7
	 * specific. This method is inspired by the question at Stackoverflow:
	 * http://stackoverflow.com/questions/60302/starting-a-process-with-inherited-stdin-stdout-stderr-in-java-6
	 */
	public static void inheritOutput(Process process)
	{
		pipe(process.getErrorStream(), System.err);
		pipe(process.getInputStream(), System.out);
	}

	private static void pipe(final InputStream src, final PrintStream dest)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					byte[] buffer = new byte[1024];
					for (int n = 0; n != -1; n = src.read(buffer))
					{
						dest.write(buffer, 0, n);
					}
				} catch (IOException e)
				{ // just exit
				}
			}
		}).start();
	}

}
