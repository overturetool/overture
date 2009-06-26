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

package org.overturetool.vdmjc;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmjc.client.CommandLine;
import org.overturetool.vdmjc.client.Dialect;
import org.overturetool.vdmjc.config.Config;

public class VDMJC
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			Config.init();
		}
		catch (Exception e)
		{
			// Silently use default config values if no properties file.
		}

		try
		{
			Dialect dialect = Dialect.VDM_PP;
			String startLine = null;

			if (args.length > 0)
			{
				List<String> cmds = new Vector<String>();

				for (String arg: args)
				{
					if (arg.equals("-vdmpp"))
					{
						dialect = Dialect.VDM_PP;
					}
					else if (arg.equals("-vdmsl"))
					{
						dialect = Dialect.VDM_SL;
					}
					else if (arg.equals("-vdmrt"))
					{
						dialect = Dialect.VDM_RT;
					}
					else if (arg.startsWith("-"))
					{
						System.err.println("Usage: VDMJC [-vdmpp | -vdmsl | -vdmrt] [command]");
						System.exit(1);
					}
					else
					{
						cmds.add(arg);
					}
				}

				if (!cmds.isEmpty())
				{
					StringBuilder sb = new StringBuilder();

					for (String file: cmds)
					{
						sb.append(file);
						sb.append(" ");
					}

					startLine = sb.toString();
				}
			}

			System.out.println("Dialect is " + dialect.name());
			new CommandLine(dialect, startLine).run();
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
