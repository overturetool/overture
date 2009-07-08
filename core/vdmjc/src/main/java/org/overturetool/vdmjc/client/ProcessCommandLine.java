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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmjc.common.Utils;
import org.overturetool.vdmjc.dbgp.DBGPStatus;


public class ProcessCommandLine extends CommandLine
{
	private final List<File> loadedFiles;
	private final String expression;

	private ProcessListener process;
	private ConnectionThread currentThread = null;

	public ProcessCommandLine(
		Dialect dialect, List<File> loadedFiles, String expression)
	{
		super(dialect, null);
		this.loadedFiles = loadedFiles;
		this.expression = expression;
	}

	@Override
	public void run()
	{
		if (start())
		{
			process();
		}
	}

	private void checkThread()		// True if changed thread
	{
		if (currentThread == null || !currentThread.isAlive())
		{
			currentThread = process.getPrincipal();
			ConnectionThread.setFocus(currentThread);
		}
	}

	@Override
	protected String getPrompt() throws IOException
	{
		if (process.hasEnded())
		{
			int code = process.waitEnded();
			throw new RuntimeException(
				"VDMJ process died " + (code == 0 ? "(OK)" : "(errors)"));
		}

		checkThread();
		return "[" + currentThread + "]> ";
	}

	private boolean start()
	{
		process = new ProcessListener(dialect, loadedFiles, expression);
		process.start();

		if (!process.waitStarted())		// Didn't start
		{
			process = null;
			return false;
		}
		else
		{
			while (messages.isEmpty())
			{
				Utils.milliPause(10);
			}

			currentThread = null;

			while (!process.hasEnded() && currentThread == null)
			{
				Utils.milliPause(10);
				currentThread = process.getPrincipal();
			}

			ConnectionThread.setFocus(currentThread);
			return !process.hasEnded();
		}
	}

	private void process()
	{
		boolean carryOn = true;
		long timestamp = System.currentTimeMillis();

		while (carryOn)
		{
			try
            {
                if (process.hasEnded())
                {
                	carryOn = false;
                	break;
                }

            	checkThread();
           		currentThread.status();		// So prompt is accurate

                for (File file: loadedFiles)
    			{
    				if (file.lastModified() > timestamp)
    				{
    					println("File " + file + " has changed - try 'reload'");
    				}
    			}

            	String line = promptLine();

	            if (line.equals(""))
	            {
	            	continue;
	            }
	            else if (line.equals("quit") || line.equals("q"))
				{
	            	println("Terminating VDMJ process");
	            	process.die();
                	carryOn = false;
					break;
				}
	            else if (line.startsWith("help"))
				{
					carryOn = processHelp(line);
				}
	            else if (line.equals("dbgp"))
				{
	            	carryOn = processDBGP();
				}
	            else if (line.equals("quiet"))
				{
					carryOn = processQuiet();
				}
	            else if (line.startsWith("load"))
	            {
	            	println("Process still running - try 'unload'");
	            	continue;
	            }
	            else if (line.startsWith("eval"))
	            {
	            	println("Process still running - try 'unload'");
	            	continue;
	            }
	            else if (line.equals("unload"))
	            {
	            	println("Terminating VDMJ process");
	            	process.die();
					break;
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
	            else if (line.startsWith("default"))
	            {
	            	carryOn = processDefault(line);
	            }
	            else if (line.equals("threads") || line.equals("t"))
	            {
	            	carryOn = processThreads();
	            }
	            else if (line.startsWith("thread") || line.startsWith("t "))
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
            catch (Exception e)
            {
            	println("Failed: " + e.getMessage());
            	carryOn = false;
            }
		}
	}

	@Override
	protected boolean processHelp(String line)
	{
		if (line.equals("help"))
		{
			println("Running:");
    		println("  init");
    		println("  run");
    		println("  p[rint] <expression>");
    		println("  coverage [<files>]");
    		println("  pog [fn/op]");
    		println("  files");
    		println("  classes");
    		println("  modules");
    		println("  t[hreads]");
    		println("  t[hread] <id>");
    		println("  unload");
    		println("  reload");
    		println("  quiet");
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

	private boolean processReload()
	{
    	println("Terminating VDMJ process");
    	process.die();
    	return start();
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
			long newId = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
			ConnectionThread th = process.findConnection(newId);

			if (th != null)
			{
				currentThread = th;
				ConnectionThread.setFocus(currentThread);
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
   		String exp = line.substring(line.indexOf(' ') + 1);

   		if (currentThread.getStatus() == DBGPStatus.BREAK ||
   			currentThread.getStatus() == DBGPStatus.STOPPING)
   		{
   	   		currentThread.eval(exp);
   		}
   		else
   		{
   	   		currentThread.expr(exp);
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
