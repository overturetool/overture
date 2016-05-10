package org.overture.interpreter.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.commands.ClassCommandReader;
import org.overture.interpreter.commands.ModuleCommandReader;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.DebuggerException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ParserException;

public class DBGPExecProcesser
{
	private DBGPExecProcesser() {
	}

	public static class DBGPExecResult
	{
		public final String result;
		public final boolean quit;

		public DBGPExecResult(boolean quit, String result)
		{
			this.quit = quit;
			this.result = result;
		}
	}

	static boolean quitRequest = false;

	public static DBGPExecResult process(DBGPReader reader,
			Interpreter interpreter, String command)
	{

		Interpreter i = interpreter;
		final DBGPReader d = reader;
		List<File> fileList = new Vector<File>(i.getSourceFiles());
		final Writer result = new StringWriter();
		final Reader input = new StringReader(command);
		// System.out.println("Command session started in " + new File(".").getAbsolutePath());

		if (i instanceof ClassInterpreter)
		{
			new ClassCommandReader((ClassInterpreter) i, "", true)
			{
				private PrintWriter stdout = new PrintWriter(result, true);
				private BufferedReader stdin = new BufferedReader(input);

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

				protected boolean doEvaluate(String line)
				{
					line = line.substring(line.indexOf(' ') + 1);

					try
					{
						// long before = System.currentTimeMillis();
						println("= " + interpreter.execute(line, d));
						// long after = System.currentTimeMillis();
						// println("Executed in " + (double)(after-before)/1000 + " secs. ");

						if (RTLogger.getLogSize() > 0)
						{
							println("Dumped RT events");
							RTLogger.dump(false);
						}
					} catch (ParserException e)
					{
						println("Syntax: " + e.getMessage());
					} catch (DebuggerException e)
					{
						println("Debug: " + e.getMessage());
					} catch (RuntimeException e)
					{
						println("Runtime: " + e);
					} catch (VDMErrorsException e)
					{
						println(e.toString());
					} catch (Exception e)
					{
						println("Error: " + e.getMessage());
					}

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

				// @Override
				// protected boolean doThreads(String line)
				// {
				// return notAvailable(line);
				// }

				@Override
				public ExitStatus run(List<File> filenames)
				{
					Interpreter.setTraceOutput(stdout);
					setDebugReader(d);
					return super.run(filenames);
				}

				@Override
				protected boolean doQuit(String line)
				{
					boolean ok = super.doQuit(line);
					d.complete(ok ? DBGPReason.OK : DBGPReason.ERROR, null);
					quitRequest = true;
					return ok;
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
		} else
		{
			new ModuleCommandReader((ModuleInterpreter) i, "", true)
			{
				private PrintWriter stdout = new PrintWriter(result, true);
				private BufferedReader stdin = new BufferedReader(input);

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

				protected boolean doEvaluate(String line)
				{
					line = line.substring(line.indexOf(' ') + 1);

					try
					{
						// long before = System.currentTimeMillis();
						println("= " + interpreter.execute(line, d));
						// long after = System.currentTimeMillis();
						// println("Executed in " + (double)(after-before)/1000 + " secs. ");

						if (RTLogger.getLogSize() > 0)
						{
							println("Dumped RT events");
							RTLogger.dump(false);
						}
					} catch (ParserException e)
					{
						println("Syntax: " + e.getMessage());
					} catch (DebuggerException e)
					{
						println("Debug: " + e.getMessage());
					} catch (RuntimeException e)
					{
						println("Runtime: " + e);
					} catch (VDMErrorsException e)
					{
						println(e.toString());
					} catch (Exception e)
					{
						println("Error: " + e.getMessage());
					}

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
					setDebugReader(d);
					return super.run(filenames);
				}

				@Override
				protected boolean doQuit(String line)
				{
					boolean ok = super.doQuit(line);
					d.complete(ok ? DBGPReason.OK : DBGPReason.ERROR, null);
					quitRequest = true;
					return ok;
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
		try
		{
			result.flush();
			result.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new DBGPExecResult(quitRequest, result.toString());
	}
}
