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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmjc.common.Utils;
import org.overturetool.vdmjc.dbgp.DBGPStatus;


public class CommandLine
{
	private Dialect dialect = Dialect.VDM_PP;
	private ProcessListener process;
	private long currentId = 0;
	private ConnectionThread currentThread = null;
	private String startLine = null;
	private List<File> loadedFiles = new Vector<File>();

	private static Queue<String> messages = new ConcurrentLinkedQueue<String>();

	public CommandLine(String[] args)
	{
		process = null;

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
					println("Usage: VDMJC [-vdmpp | -vdmsl | -vdmrt] [command]");
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

		println("Dialect is " + dialect.name());
	}

	public static void message(String msg)
	{
		messages.add(msg);
	}

	private void println(String line)
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

	private void print(String line)
	{
		System.out.print(line);
		System.out.flush();
	}

	private void setThread()
	{
    	if (process == null)
    	{
    		currentThread = null;
    		currentId = 0;
    	}
    	else if (process.hasEnded())
    	{
    		process = null;
    		currentThread = null;
    		currentId = 0;
    	}
    	else
    	{
    		currentThread = process.findConnection(currentId);
		}
	}

	private String getPrompt()
	{
		setThread();

    	if (currentThread == null)
    	{
    		return "> ";
    	}
     	else
    	{
			return "[" + currentThread + "]> ";
		}
	}

	private List<File> getFiles(String line) throws IOException
	{
		List<String> filenames = Arrays.asList(line.split("\\s+"));
		List<File> files = new Vector<File>();
		boolean OK = true;

		Iterator<String> it = filenames.iterator();
		it.next();	// to skip start "load" or "eval" etc.

		while (it.hasNext())
		{
			String name = it.next();
			File file = new File(name);

			if (file.exists())
			{
				files.add(file);
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

	private String readLine(String prompt, String reason) throws IOException
	{
		StringBuilder lineTyped = new StringBuilder();
		InputStreamReader console = new InputStreamReader(System.in);
		print(prompt);

		while (true)
		{
			int c = console.read();

			if (c == '\r')
			{
				continue;
			}
			else if (c == '\n' || c == -1)
			{
				String text = lineTyped.toString().trim();

				if (text.length() > 0)
				{
					return text;
				}

				println(reason);
				print(prompt);
			}
			else
			{
				lineTyped.append((char)c);
			}
		}
	}

	private String promptLine() throws IOException
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

		if (messages.isEmpty())
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

			Utils.milliPause(10);
		}
	}

	public void run()
	{
		Thread.currentThread().setName("CommandLine");
		boolean carryOn = true;
		long timestamp = System.currentTimeMillis();

		while (carryOn)
		{
			for (File file: loadedFiles)
			{
				if (file.lastModified() > timestamp)
				{
					println("File " + file + " has changed - try 'reload'");
				}
			}

			try
            {
            	setThread();

            	if (currentThread != null)
            	{
            		currentThread.status();		// So prompt is accurate
            	}

            	String line = promptLine();

	            if (line.equals(""))
	            {
	            	continue;
	            }
	            else if (line.equals("quit") || line.equals("q"))
				{
	            	println("Bye");
					carryOn = false;
				}
	            else if (line.startsWith("help"))
				{
					carryOn = processHelp(line);
				}
	            else if (line.equals("dbgp"))
				{
					println("DBGp trace is now " +
						(ConnectionThread.setTrace() ? "ON" : "OFF"));
				}
	            else if (line.startsWith("load"))
	            {
	            	carryOn = processLoad(line);
	            }
	            else if (line.startsWith("eval"))
	            {
	            	carryOn = processEval(line);
	            }
	            else if (process == null || process.hasEnded())
	            {
	            	println("VDMJ is not running - use 'load' or 'eval' command");
	            }
	            else if (line.equals("unload"))
	            {
	            	carryOn = processUnload();
	            }
	            else if (line.equals("reload"))
	            {
	            	timestamp = System.currentTimeMillis();
	            	carryOn = processReload();
	            }
	            else if (line.equals("init"))
	            {
	            	carryOn = processInit();
	            }
	            else if (line.startsWith("create"))
	            {
	            	carryOn = processCreate(line);
	            }
	            else if (line.startsWith("pog"))
	            {
	            	carryOn = processPOG(line);
	            }
	            else if (line.equals("files"))
	            {
	            	carryOn = processFiles();
	            }
	            else if (line.equals("modules"))
	            {
	            	carryOn = processModules();
	            }
	            else if (line.equals("classes"))
	            {
	            	carryOn = processClasses();
	            }
	            else if (line.equals("default"))
	            {
	            	carryOn = processDefault(line);
	            }
	            else if (line.equals("threads"))
	            {
	            	carryOn = processThreads();
	            }
	            else if (line.startsWith("thread"))
	            {
	            	carryOn = processThread(line);
	            }
	            else if (line.startsWith("coverage"))
	            {
	            	carryOn = processCoverage(line);
	            }
	            else if (line.equals("run") ||
	            		 line.equals("continue") ||
	            		 line.equals("c"))
	            {
	            	carryOn = processContinue();
	            }
	            else if (line.equals("step") || line.equals("s"))
	            {
	            	carryOn = processStep();
	            }
	            else if (line.equals("next") || line.equals("n"))
	            {
	            	carryOn = processNext();
	            }
	            else if (line.equals("out") || line.equals("o"))
	            {
	            	carryOn = processOut();
	            }
	            else if (line.startsWith("break"))
				{
	            	carryOn = processBreak(line);
				}
	            else if (line.startsWith("trace"))
				{
	            	carryOn = processTrace(line);
				}
	            else if (line.equals("list"))
				{
	            	carryOn = processList();
				}
	            else if (line.startsWith("remove"))
				{
	            	carryOn = processRemove(line);
				}
	            else if (line.startsWith("print") || line.startsWith("p "))
				{
	            	carryOn = processPrint(line);
				}
	            else if (line.equals("stack"))
				{
	            	carryOn = processStack();
				}
	            else if (line.equals("source"))
				{
	            	carryOn = processSource();
				}
	            else if (line.startsWith("get"))
				{
	            	carryOn = processGet(line);
				}
	            else if (line.equals("stop"))
				{
	            	carryOn = processStop();
				}
	            else
	            {
	            	println("Unknown command - try 'help'");
	            }
            }
            catch (IOException e)
            {
            	println("Failed: " + e.getMessage());
            	carryOn = false;
            }
		}

		if (currentThread != null)
		{
			try
			{
				currentThread.detach();
			}
			catch (IOException e)
			{
				// ?
			}
		}

		if (process != null)
		{
			process.die();
		}
	}

	private boolean processHelp(String line)
	{
		if (line.equals("help"))
		{
			println("Loading and starting:");
    		println("  load [<files>]");
    		println("  eval [<files>]");
    		println("  init");
    		println("  run");
    		println("  p[rint] <expression>");
    		println("  coverage [<files>]");
    		println("  pog [fn/op]");
    		println("  files");
    		println("  classes");
    		println("  modules");
    		println("  threads");
    		println("  thread <id>");
    		println("  unload");
    		println("  reload");
    		println("  q[uit]");
    		println("");
    		println("Debugging:");
    		println("  s[tep]");
    		println("  n[ext]");
    		println("  o[ut]");
    		println("  c[ontinue]");
    		println("  stack");
    		println("  source");
    		println("  stop");
    		println("  p[rint] <expression>");
    		println("  get <local|class|global> [frame]");
    		println("  break [<file>] <line> [<condition>]");
    		println("  trace [<file>] <line> [<display>]");
    		println("  list");
    		println("  remove <id>");
    		println("  dbgp");
    		println("");
    		println("Use 'help <command>' for more help");
		}
		else
		{
			println("No more help yet...");
		}

		return true;
	}

	private boolean processInit() throws IOException
	{
		currentThread.xcmd_overture_init();
		return true;
	}

	private boolean processCreate(String line) throws IOException
	{
		if (dialect == Dialect.VDM_SL)
		{
			println("Create is only available for VDM++");
		}
		else
		{
    		Pattern p = Pattern.compile("^create (\\w+)\\s*?:=\\s*(.+)$");
    		Matcher m = p.matcher(line);

    		if (m.matches())
    		{
    			String var = m.group(1);
    			String exp = m.group(2);

    			currentThread.xcmd_overture_create(var, exp);
    		}
    		else
    		{
    			println("Usage: create <id> := <value>");
    		}
		}

		return true;
	}

	private boolean processFiles() throws IOException
	{
		currentThread.xcmd_overture_files();
		return true;
	}

	private boolean processClasses() throws IOException
	{
		if (dialect == Dialect.VDM_SL)
		{
			println("Current dialect has no classes - try 'modules'");
		}
		else
		{
			currentThread.xcmd_overture_classes();
		}

		return true;
	}

	private boolean processModules() throws IOException
	{
		if (dialect != Dialect.VDM_SL)
		{
			println("Current dialect has no modules - try 'classes'");
		}
		else
		{
			currentThread.xcmd_overture_modules();
		}

		return true;
	}

	private boolean processDefault(String line) throws IOException
	{
		String parts[] = line.split("\\s+");

		if (parts.length != 2)
		{
			println("Usage: default <default class/module name>");
		}
		else
		{
			currentThread.xcmd_overture_default(parts[1]);
		}

		return true;
	}

	private boolean processPOG(String line) throws IOException
	{
		if (line.equals("pog"))
		{
			currentThread.xcmd_overture_pog("*");
		}
		else
		{
        	Pattern p1 = Pattern.compile("^pog (\\w+)$");
        	Matcher m = p1.matcher(line);

        	if (m.matches())
        	{
        		currentThread.xcmd_overture_pog(m.group(1));
         	}
        	else
        	{
        		println("Usage: pog [<fn/op name>]");
        	}
		}

		return true;
	}

	private boolean processThread(String line)
	{
		try
		{
			long newId = Integer.parseInt(line.substring(7));

			if (process.findConnection(newId) != null)
			{
				currentId = newId;
			}
			else
			{
				println("No such thread Id");
			}
		}
		catch (NumberFormatException e)
		{
			println("Usage: thread <#id>");
		}

		return true;
	}

	private boolean processThreads()
	{
		ConnectionThread[] all = process.getConnections();

		for (ConnectionThread ct: all)
		{
			println(ct.toString());
		}

		return true;
	}

	private boolean processCoverage(String line)
	{
		try
		{
			List<File> files = getFiles(line);

			if (files.isEmpty())
			{
				files = loadedFiles;
			}

			for (File file: files)
			{
				currentThread.xcmd_overture_coverage(file);
			}
		}
		catch (Exception e)
		{
			println("Problem locating files");
		}

		return true;
	}

	private boolean processLoad(String line)
	{
		if (process != null && !process.hasEnded())
		{
			println("VDMJ still running");
		}
		else
		{
			try
			{
				loadedFiles = getFiles(line);

				process = new ProcessListener(dialect, loadedFiles, "undefined");
				process.start();

				if (!process.waitStarted())
				{
					process = null;
				}
				else
				{
					while (messages.isEmpty())
					{
						Utils.milliPause(10);
					}
				}
			}
			catch (Exception e)
			{
				println("Problem loading files");
				loadedFiles.clear();
			}
		}

		return true;
	}

	private boolean processUnload() throws IOException
	{
		currentThread.detach();
		process.die();
		loadedFiles.clear();
		return true;
	}

	private boolean processReload() throws IOException
	{
		StringBuilder sb = new StringBuilder("load");

		for (File file: loadedFiles)
		{
			sb.append(" ");
			sb.append(file.getName());
		}

		processUnload();
		return processLoad(sb.toString());
	}

	private boolean processEval(String line)
	{
		if (process != null && !process.hasEnded())
		{
			println("VDMJ still running");
		}
		else
		{
			try
			{
				loadedFiles = getFiles(line);
				String expression = readLine("Evaluate: ", "Need an expression to evaluate");

				process = new ProcessListener(dialect, loadedFiles, expression);
				process.start();

				if (!process.waitStarted())
				{
					process = null;
				}
				else
				{
					while (messages.isEmpty())
					{
						Utils.milliPause(10);
					}
				}
			}
			catch (Exception e)
			{
				println("Problem loading files");
				loadedFiles.clear();
			}
		}

		return true;
	}

	private boolean processContinue() throws IOException
	{
 		currentThread.runme();
		return true;
	}

	private boolean processStep() throws IOException
	{
 		currentThread.step_into();
		return true;
	}

	private boolean processNext() throws IOException
	{
 		currentThread.step_over();
		return true;
	}

	private boolean processOut() throws IOException
	{
 		currentThread.step_out();
		return true;
	}

	private boolean processPrint(String line) throws IOException
	{
   		String expression = line.substring(line.indexOf(' ') + 1);

   		if (currentThread.getStatus() == DBGPStatus.BREAK)
   		{
   	   		currentThread.eval(expression);
   		}
   		else
   		{
   	   		currentThread.expr(expression);
   		}

		return true;
	}

	private boolean processStack() throws IOException
    {
		// currentThread.stack_get();
		currentThread.xcmd_overture_stack();
		return true;
    }

	private boolean processSource() throws IOException
	{
		currentThread.xcmd_overture_source();
		return true;
	}

	private boolean processGet(String line) throws IOException
	{
   		String[] parts = line.split("\\s+");

   		if (parts.length != 2 && parts.length != 3)
   		{
   			println("Usage: get <local|class|global> [frame]");
   		}
   		else
   		{
   			String type = parts[1];
   			String d = (parts.length == 3) ? parts[2] : "0";
   			int depth = Utils.parseInt(d);

       		if (type.equalsIgnoreCase("local"))
       		{
       			currentThread.context_get(0, depth);
       		}
       		else if (type.equalsIgnoreCase("class"))
       		{
       			currentThread.context_get(1, depth);
       		}
       		else if (type.equalsIgnoreCase("global"))
       		{
       			currentThread.context_get(2, depth);
       		}
       		else
       		{
       			println("Usage: get <local|class|global> [frame]");
       		}
   		}

   		return true;
	}

	private boolean processStop() throws IOException
	{
		currentThread.allstop();
		return true;
	}

	private boolean processBreak(String line)
	{
		try
		{
			Pattern p = Pattern.compile("^break ([\\w./\\\\]+) (\\d+) ?(.*)?$");
			Matcher m = p.matcher(line);

			if (m.matches())
			{
    			File file = new File(m.group(1));
    			int lnum = Integer.parseInt(m.group(2));
    			String condition = (m.groupCount() == 3) ? m.group(3) : null;
    			currentThread.breakpoint_set(file, lnum, condition);
    			return true;
			}
			else if (loadedFiles.size() == 1)
			{
    			p = Pattern.compile("^break (\\d+) ?(.*)?$");
    			m = p.matcher(line);

    			if (m.matches())
    			{
        			File file = loadedFiles.get(0);
        			int lnum = Integer.parseInt(m.group(1));
        			String condition = (m.groupCount() == 2) ? m.group(2) : null;
        			currentThread.breakpoint_set(file, lnum, condition);
        			return true;
    			}
			}

			println("Usage: break [<file>] <line#> [<condition>]");
		}
		catch (Exception e)
		{
			println("Usage: break [<file>] <line#> [<condition>]");
		}

		return true;
	}

	private boolean processTrace(String line)
	{
		try
		{
			Pattern p = Pattern.compile("^trace ([\\w./\\\\]+) (\\d+) ?(.*)?$");
			Matcher m = p.matcher(line);

			if (m.matches())
			{
    			File file = new File(m.group(1));
    			int lnum = Integer.parseInt(m.group(2));
    			String display = (m.groupCount() == 3) ? m.group(3) : null;
    			currentThread.xcmd_overture_trace(file, lnum, display);
    			return true;
			}
			else if (loadedFiles.size() == 1)
			{
    			p = Pattern.compile("^trace (\\d+) ?(.*)?$");
    			m = p.matcher(line);

    			if (m.matches())
    			{
        			File file = loadedFiles.get(0);
        			int lnum = Integer.parseInt(m.group(1));
        			String display = (m.groupCount() == 2) ? m.group(2) : null;
        			currentThread.xcmd_overture_trace(file, lnum, display);
        			return true;
    			}
			}

			println("Usage: trace [<file>] <line#> [<display>]");
		}
		catch (Exception e)
		{
			println("Usage: trace [<file>] <line#> [<display>]");
		}

		return true;
	}

	private boolean processList() throws IOException
	{
		// currentThread.breakpoint_list();
		currentThread.xcmd_overture_list();
		return true;
	}

	private boolean processRemove(String line) throws IOException
    {
		try
		{
			int n = Integer.parseInt(line.substring(7));
			currentThread.breakpoint_remove(n);
		}
		catch (NumberFormatException e)
		{
			println("Usage: remove <#id>");
		}
		return true;
    }
}
