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

package org.overturetool.vdmjc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CommandLine
{
	protected Dialect dialect;
	private String startLine = null;

	protected static Queue<String> messages = new ConcurrentLinkedQueue<String>();

	protected static Queue<String> scriptMessages = new ConcurrentLinkedQueue<String>();

	public CommandLine(Dialect dialect, String startLine)
	{
		this.dialect = dialect;
		this.startLine = startLine;
	}

	public static void message(String msg)
	{
		messages.add(msg);
	}

	protected void println(String line)
	{
		if (line.endsWith("\n"))
		{
			System.out.print(line);
		}
		else
		{
			System.out.println(line);
		}
	}

	protected void print(String line)
	{
		System.out.print(line);
		System.out.flush();
	}

	protected List<File> getFiles(String line) throws IOException
	{
		List<String> filenames = Arrays.asList(line.split("\\s+"));
		List<File> files = new Vector<File>();
		boolean OK = true;

		Iterator<String> it = filenames.iterator();
		it.next();	// to skip start "load" or "eval" etc.

		while (it.hasNext())
		{
			String name = it.next();
			File dir = new File(name);

			if (dir.exists())
			{
				if (dir.isDirectory())
				{
 					for (File file: dir.listFiles(dialect.getFilter()))
					{
						if (file.isFile())
						{
							files.add(file);
						}
					}
				}
    			else
    			{
    				files.add(dir);
    			}
			}
			else
			{
				println("Cannot find file " + name);
				OK = false;
			}
		}

		if (!OK)
		{
			throw new IOException("Missing files");
		}

		return files;
	}

	protected String getPrompt()
	{
   		return "> ";
	}

	protected String promptLine() throws IOException
	{
		if (startLine != null)
		{
			String line = startLine.trim();
			println("> " + startLine);
			startLine = null;
			return line;
		}

		StringBuilder lineTyped = new StringBuilder();
		InputStreamReader console = new InputStreamReader(System.in);
		boolean prompted = false;

		if (messages.isEmpty() && scriptMessages.isEmpty())
		{
			print(getPrompt());		// Otherwise nothing appears when quiet!
			prompted = true;
		}

		while (true)
		{
			if (!messages.isEmpty())
			{
				if (prompted)
				{
					print("\n");
				}

				while (!messages.isEmpty())
				{
					String msg = messages.poll();

					if (msg.length() > 0)
					{
						println(msg.toString());
					}
				}

				print(getPrompt());
				print(lineTyped.toString());
				prompted = true;
			}

			if (!scriptMessages.isEmpty() && messages.isEmpty() && prompted)
			{
				if (acceptScriptCommand(scriptMessages.peek()))
				{
					String line = scriptMessages.poll();
					println(line);
					return line;
				}
				else
				{
					return "";
				}
			}

			while (console.ready())
			{
				int c = console.read();

				if (c == '\r')
				{
					continue;
				}
				else if (c == '\n' || c == -1)
				{
					return lineTyped.toString().trim();
				}
				else
				{
					lineTyped.append((char)c);
				}
			}
		}
	}

	protected boolean acceptScriptCommand(@SuppressWarnings("unused") String peek)
	{
		return true;
	}

	public void run()
	{
		Thread.currentThread().setName("Command Line");
		boolean carryOn = true;

		while (carryOn)
		{
			try
            {
				String line = promptLine();

 	            if (line.equals(""))
	            {
	            	continue;
	            }
	            else if (line.equals("quit") || line.equals("q"))
				{
					carryOn = false;
				}
	            else if (line.startsWith("help"))
				{
					carryOn = processHelp(line);
				}
	            else if (line.equals("dbgp"))
				{
					carryOn = processDBGP();
				}
	            else if (line.startsWith("dialect"))
				{
					carryOn = processDialect(line);
				}
	            else if (line.equals("quiet"))
				{
					carryOn = processQuiet();
				}
	            else if (line.equals("ls") || line.equals("dir"))
				{
					carryOn = processLs();
				}
	            else if (line.startsWith("load"))
	            {
	            	carryOn = processLoad(line);
	            }
	            else if (line.startsWith("eval"))
	            {
	            	carryOn = processEval(line);
	            }
	            else if (line.startsWith("script"))
	            {
	            	carryOn = processScript(line);
	            }
	            else
	            {
	            	println("Unknown command - try 'help'");
	            }
            }
            catch (Exception e)
            {
            	println("Failed: " + e.getMessage());
            }
		}

    	println("Bye");
	}

	protected boolean processLs()
	{
		try
		{
			File dir = new File(".");
			println("Directory: " + dir.getCanonicalPath());

			for (File f: dir.listFiles())
			{
				println(f.getName());
			}
		}
		catch (IOException e)
		{
			println("IO error: " + e.getMessage());
		}

		return true;
	}

	protected boolean processHelp(String line)
	{
		if (line.equals("help"))
		{
			println("Loading and starting:");
    		println("  load [<files>]");
    		println("  eval [<files>]");
    		println("  script <vdmsl|vdmpp|vdmrt> <script file> <log file> <files>");
    		println("  dbgp");
    		println("  dialect <vdmsl|vdmpp|vdmrt>");
    		println("  quiet");
    		println("  help");
    		println("  ls | dir");
    		println("  q[uit]");
     		println("");
    		println("Use 'help <command>' for more help");
		}
		else if (line.startsWith("help script"))
		{
			println("Scripting:");
			println("script <vdmsl|vdmpp|vdmrt> <script file> <log file> <files>");
			println("");
			println("  <vdmsl|vdmpp|vdmrt>  Sets the dialect used in script.");
			println("  <script file>        Sets the path to the script file.");
			println("                       Script commands can be given in the form:");
			println("                         [repeat count] <command>");
			println("  <log file>           Sets the path to the RealTime log file.");
			println("                         Only allowed when dialect vdmrt is selected.");
			println("  <files>              Sets the path of all specification files or a directory.");
			println("                       If a directory is given the dialect filter is used to");
			println("                       recursive search for specification files.");
			println("");
			println("Usage:");
			println("  Run a script with dialect vdmrt, script file script.vdmjcs, log file log.logrt");
			println("  and with all specification files from the current directory filtered by *.vdmrt.");
			println("  Where script.vdmjcs contains script commands in the form:");
			println("    [repeat count] <command>");
			println("    break World.vdmrt 35");
			println("    p new World().Run()");
			println("    3 step");
			println("    run");
			println("");
			println("script vdmrt script.vdmjcs log.logrt .");
			println("");

		}
		else
		{
			println("No more help yet...");
		}

		return true;
	}

	protected boolean processDBGP()
	{
		println("DBGp trace is now " +
			(ConnectionThread.setTrace() ? "ON" : "OFF"));
		return true;
	}

	protected boolean processDialect(String line)
	{
		List<String> arguments = Arrays.asList(line.split("\\s+"));
		if (arguments.size() == 2)
		{
			dialect = Dialect.lookup("-" + arguments.get(1));
			if (dialect == null)
			{
				println("Problem decoding dialect in script.");
				dialect = Dialect.VDM_PP;
			}
		}
		else
		{
			println("Dialect malformed");
		}
		println("Dialect is "+dialect);
		return true;
	}

	protected boolean processQuiet()
	{
		println("Quiet setting is now " +
			(ConnectionThread.setQuiet() ? "ON" : "OFF"));
		return true;
	}

	protected boolean processLoad(String line)
	{
		try
		{
			new ProcessCommandLine(dialect, getFiles(line), "undefined").run();
		}
		catch (IOException e)
		{
			println("Problem loading files");
		}
		catch (Exception e)
		{
			println("Exception: " + e);
		}

		return true;
	}

	protected boolean processExecute(String line)
	{
		try
		{
			String files = line.substring(0,line.indexOf('"')).trim();
			String expression = line.substring(line.indexOf('"'));
			new ProcessCommandLine(dialect, getFiles(files), expression).run();
		}
		catch (IOException e)
		{
			println("Problem loading files");
		}
		catch (Exception e)
		{
			println("Exception: " + e);
		}

		return true;
	}

	protected boolean processEval(String line)
	{
		try
		{
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			print("Evaluate: ");
			String expression = stdin.readLine().trim();

			while (expression.length() == 0)
			{
				println("Need an expression to evaluate");
				print("Evaluate: ");
				expression = stdin.readLine().trim();
			}

			new ProcessCommandLine(dialect, getFiles(line), expression).run();
		}
		catch (IOException e)
		{
			println("Problem loading files");
		}
		catch (Exception e)
		{
			println("Exception: " + e);
		}

		return true;
	}

	protected boolean processScript(String line)
	{
		List<String> arguments = Arrays.asList(line.split("\\s+"));

		String dialectArg = arguments.get(1);

		dialect = Dialect.lookup("-"+dialectArg);
		if (dialect== null)
		{
			println("Problem decoding dialect in script.");
			return false;
		}
		println("Dialect is "+dialect);
		File script = new File(arguments.get(2));

		File logFile = new File(arguments.get(3));
		String files = "script ";//will be skipped by getFiles
		for (int i = 4; i < arguments.size(); i++)
		{
			files +=arguments.get(i)+" ";
		}
		files = files.trim();

		String expression = "undefined";

		try
		{
			if (script.exists() && script.isFile())
			{
				BufferedReader reader = null;
				try
				{
					reader = new BufferedReader(new FileReader(script));
					String text = null;

					while ((text = reader.readLine()) != null)
					{
						scriptMessages.addAll(decodeScriptLine(text));
					}
				} catch (IOException e)
				{
					println("Problem reading script file");
				} finally
				{
					try
					{
						if (reader != null)
						{
							reader.close();
						}
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				scriptMessages.add("quit");//Exit debug
				scriptMessages.add("quit");//Exit this

				new ProcessCommandLine(dialect, getFiles(files), expression,"vdm10",logFile).run();
			}
			else
			{
				println("Script is not found or is not a file");
				return false;
			}
		}
		catch (IOException e)
		{
			println("Problem loading files");
		}
		catch (Exception e)
		{
			println("Exception: " + e);
		}

		return true;
	}

	private List<String> decodeScriptLine(String text)
	{
		List<String> commands = new Vector<String>();

		if (text.startsWith("#")|| text.startsWith("--")|| text.startsWith("//"))
		{
			return commands;
		}

		if (text.contains(" "))
		{
			String repeatText = text.substring(0,text.indexOf(' '));

			try
			{
				Integer repeat = Integer.parseInt(repeatText);
				String command = text.substring(text.indexOf(' ') + 1);
				for (int i = 0; i < repeat; i++)
				{
					commands.add(command);
				}
			} catch (NumberFormatException e)
			{
				// no repeat
				commands.add(text);
			}

		}
		else
		{
			commands.add(text);
		}

		return commands;
	}
}
