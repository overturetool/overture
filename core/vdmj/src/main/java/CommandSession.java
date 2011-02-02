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
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.ModuleCommandReader;
import org.overturetool.vdmj.debug.RemoteControl;
import org.overturetool.vdmj.debug.RemoteInterpreter;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;

public class CommandSession implements RemoteControl
{
	public void run(RemoteInterpreter interpreter)
	{
		Interpreter i = interpreter.getInterpreter();
		List<File> fileList = new Vector<File>(i.getSourceFiles());
		System.out.println("Command session started in " + new File(".").getAbsolutePath());

		if (i instanceof ClassInterpreter)
		{
			new ClassCommandReader((ClassInterpreter)i, "> ")
			{
				private PrintWriter stdout = new PrintWriter(System.out, true);
				private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

				@Override
				protected PrintWriter getStdout()
				{
					return stdout;
				}

				@Override
				protected BufferedReader getStdin()
				{
					return stdin;
				}

				@Override
				protected boolean notAvailable(String line)
				{
					println("Command not available from here");
					return true;
				}

				@Override
				protected boolean doRuntrace(String line, boolean debug)
				{
					if (!debug)
					{
						return super.doRuntrace(line, debug);
					}

					return notAvailable(line);
				}

				@Override
				protected boolean doReLoad(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doLoad(String line, List<File> filenames)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doRemove(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doList(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doSource(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doBreak(String line) throws Exception
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doTrace(String line) throws Exception
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doStep(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doNext(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doOut(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doStack(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doUp(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doDown(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doContinue(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doThreads(String line)
				{
					return notAvailable(line);
				}

				@Override
				public ExitStatus run(List<File> filenames)
				{
					Interpreter.setTraceOutput(stdout);
					return super.run(filenames);
				}

				@Override
				protected void doHelp(String line)
				{
					println("classes - list the loaded class names");
					println("default <class> - set the default class name");
					println("create <id> := <exp> - create a named variable");
					println("print <expression> - evaluate expression");
					println("runtrace <name> [test number] - run CT trace(s)");
					println("filter %age | <reduction type> - reduce CT trace(s)");
					println("assert <file> - run assertions from a file");
					println("init - re-initialize the global environment");
					println("env - list the global symbols in the default environment");
					println("pog [<function/operation>] - generate proof obligations");
					println("coverage clear|write <dir>|merge <dir>|<filenames> - handle line coverage");
					println("latex|latexdoc [<files>] - generate LaTeX line coverage files");
					println("word [<files>] - generate Word HTML line coverage files");
					println("files - list files in the current specification");
					println("set [<pre|post|inv|dtc|measures> <on|off>] - set runtime checks");
					println("quit - leave the interpreter");
				}

			}.run(fileList);
		}
		else
		{
			new ModuleCommandReader((ModuleInterpreter)i, "> ")
			{
				private PrintWriter stdout = new PrintWriter(System.out, true);
				private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

				@Override
				protected PrintWriter getStdout()
				{
					return stdout;
				}

				@Override
				protected BufferedReader getStdin()
				{
					return stdin;
				}

				@Override
				protected boolean notAvailable(String line)
				{
					println("Command not available from here");
					return true;
				}

				@Override
				protected boolean doRuntrace(String line, boolean debug)
				{
					if (!debug)
					{
						return super.doRuntrace(line, debug);
					}

					return notAvailable(line);
				}

				@Override
				protected boolean doReLoad(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doLoad(String line, List<File> filenames)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doRemove(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doList(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doSource(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doBreak(String line) throws Exception
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doTrace(String line) throws Exception
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doStep(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doNext(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doOut(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doStack(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doUp(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doDown(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doContinue(String line)
				{
					return notAvailable(line);
				}

				@Override
				protected boolean doThreads(String line)
				{
					return notAvailable(line);
				}

				@Override
				public ExitStatus run(List<File> filenames)
				{
					Interpreter.setTraceOutput(stdout);
					return super.run(filenames);
				}

				@Override
				protected void doHelp(String line)
				{
					println("modules - list the loaded module names");
					println("default <module> - set the default module name");
					println("state - show the default module state");
					println("print <expression> - evaluate expression");
					println("runtrace <name> [test number] - run CT trace(s)");
					println("filter %age | <reduction type> - reduce CT trace(s)");
					println("assert <file> - run assertions from a file");
					println("init - re-initialize the global environment");
					println("env - list the global symbols in the default environment");
					println("pog [<function/operation>] - generate proof obligations");
					println("coverage clear|write <dir>|merge <dir>|<filenames> - handle line coverage");
					println("latex|latexdoc [<files>] - generate LaTeX line coverage files");
					println("word [<files>] - generate Word HTML line coverage files");
					println("files - list files in the current specification");
					println("set [<pre|post|inv|dtc|measures> <on|off>] - set runtime checks");
					println("quit - leave the interpreter");
				}

			}.run(fileList);
		}
	}
}
