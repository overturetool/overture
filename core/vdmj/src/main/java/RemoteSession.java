/*******************************************************************************
 *
 *	Copyright (c) 2011 Fujitsu Services Ltd.
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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.overturetool.vdmj.debug.RemoteControl;
import org.overturetool.vdmj.debug.RemoteInterpreter;

public class RemoteSession implements RemoteControl
{
	public void run(RemoteInterpreter interpreter)
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		boolean carryOn = true;

		while (carryOn)
		{
			try
			{
				System.out.print(">> ");
				String line = in.readLine();

				if (line.equals("quit"))
				{
					in.close();
					carryOn = false;
				}
				else if (line.equals("init"))
				{
					long before = System.currentTimeMillis();
					interpreter.init();
		   			long after = System.currentTimeMillis();
		   			System.out.println("Initialized in " + (double)(after-before)/1000 + " secs.");
				}
				else
				{
					long before = System.currentTimeMillis();
					String output = interpreter.execute(line);
		   			long after = System.currentTimeMillis();
					System.out.println(output);
		   			System.out.println("Executed in " + (double)(after-before)/1000 + " secs.");
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}
