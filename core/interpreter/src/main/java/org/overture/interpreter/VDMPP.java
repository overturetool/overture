/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overture.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.messages.InternalException;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Settings;
import org.overture.interpreter.commands.ClassCommandReader;
import org.overture.interpreter.commands.CommandReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTTextLogger;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.TypeChecker;

/**
 * The main class of the VDM++ and VICE parser/checker/interpreter.
 */

public class VDMPP extends VDMJ
{
	protected ClassListInterpreter classes = new ClassListInterpreter();

	public VDMPP()
	{
		super();
		Settings.dialect = Dialect.VDM_PP;
	}

	/**
	 * @see VDMJ#parse(java.util.List)
	 */

	@Override
	public ExitStatus parse(List<File> files)
	{
		classes.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		int pwarn = 0;
		long duration = 0;

		for (File file : files)
		{
			ClassReader reader = null;

			try
			{
				if (file.getName().endsWith(".lib"))
				{
					FileInputStream fis = new FileInputStream(file);
					GZIPInputStream gis = new GZIPInputStream(fis);
					ObjectInputStream ois = new ObjectInputStream(gis);

					ClassListInterpreter loaded = null;
					long begin = System.currentTimeMillis();

					try
					{
						loaded = new ClassListInterpreter((ClassList) ois.readObject());
					} catch (Exception e)
					{
						println(file + " is not a valid VDM++ library");
						perrs++;
						continue;
					} finally
					{
						ois.close();
					}

					long end = System.currentTimeMillis();
					loaded.setLoaded();
					classes.addAll(loaded);
					classes.remap();

					infoln("Loaded " + plural(loaded.size(), "class", "es")
							+ " from " + file + " in " + (double) (end - begin)
							/ 1000 + " secs");
				} else
				{
					long before = System.currentTimeMillis();
					LexTokenReader ltr = new LexTokenReader(file, Settings.dialect, filecharset);
					reader = new ClassReader(ltr);
					classes.addAll(reader.readClasses());
					long after = System.currentTimeMillis();
					duration += after - before;
				}
			} catch (InternalException e)
			{
				println(e.toString());
				perrs++;
			} catch (Throwable e)
			{
				println(e.toString());
				perrs++;
			}

			if (reader != null && reader.getErrorCount() > 0)
			{
				perrs += reader.getErrorCount();
				reader.printErrors(Console.out);
			}

			if (reader != null && reader.getWarningCount() > 0)
			{
				pwarn += reader.getWarningCount();
				reader.printWarnings(Console.out);
			}
		}

		int n = classes.notLoaded();

		if (n > 0)
		{
			info("Parsed " + plural(n, "class", "es") + " in "
					+ (double) duration / 1000 + " secs. ");
			info(perrs == 0 ? "No syntax errors" : "Found "
					+ plural(perrs, "syntax error", "s"));
			infoln(pwarn == 0 ? "" : " and " + (warnings ? "" : "suppressed ")
					+ plural(pwarn, "warning", "s"));
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	/**
	 * @see VDMJ#typeCheck()
	 */

	@Override
	public ExitStatus typeCheck()
	{
		int terrs = 0;
		long before = System.currentTimeMillis();

		try
		{
			TypeChecker typeChecker = new ClassTypeChecker(classes, assistantFactory);
			typeChecker.typeCheck();
		} catch (InternalException e)
		{
			println(e.toString());
		} catch (Throwable e)
		{
			println(e.toString());

			if (e instanceof StackOverflowError)
			{
				e.printStackTrace();
			}

			terrs++;
		}

		long after = System.currentTimeMillis();
		terrs += TypeChecker.getErrorCount();

		if (terrs > 0)
		{
			TypeChecker.printErrors(Console.out);
		}

		int twarn = TypeChecker.getWarningCount();

		if (twarn > 0 && warnings)
		{
			TypeChecker.printWarnings(Console.out);
		}

		int n = classes.notLoaded();

		if (n > 0)
		{
			info("Type checked " + plural(n, "class", "es") + " in "
					+ (double) (after - before) / 1000 + " secs. ");
			info(terrs == 0 ? "No type errors" : "Found "
					+ plural(terrs, "type error", "s"));
			infoln(twarn == 0 ? "" : " and " + (warnings ? "" : "suppressed ")
					+ plural(twarn, "warning", "s"));
		}

		if (outfile != null && terrs == 0)
		{
			try
			{
				before = System.currentTimeMillis();
				FileOutputStream fos = new FileOutputStream(outfile);
				GZIPOutputStream gos = new GZIPOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(gos);

				oos.writeObject(classes);
				oos.close();
				after = System.currentTimeMillis();

				infoln("Saved " + plural(classes.size(), "class", "es")
						+ " to " + outfile + " in " + (double) (after - before)
						/ 1000 + " secs. ");
			} catch (IOException e)
			{
				infoln("Cannot write " + outfile + ": " + e.getMessage());
				terrs++;
			}
		}

		if (pog && terrs == 0)
		{
			ProofObligationList list;
			try
			{
				list = classes.getProofObligations(assistantFactory);

				if (list.isEmpty())
				{
					println("No proof obligations generated");
				} else
				{
					println("Generated "
							+ plural(list.size(), "proof obligation", "s")
							+ ":\n");
					print(list.toString());
				}
			} catch (AnalysisException e)
			{
				println(e.toString());
			}

		}

		return terrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	/**
	 * @see org.overture.interpreter.VDMJ#interpret(List, String)
	 */

	@Override
	protected ExitStatus interpret(List<File> filenames, String defaultName)
	{
		ClassInterpreter interpreter;

		if (logfile != null)
		{
			try
			{
				RTLogger.setLogfile(RTTextLogger.class, new File(logfile));
				RTLogger.setLogfile(NextGenRTLogger.class, new File(logfile));
				println("RT events now logged to " + logfile);
			} catch (FileNotFoundException e)
			{
				println("Cannot create RT event log: " + e.getMessage());
				return ExitStatus.EXIT_ERRORS;
			}
		}

		try
		{
			long before = System.currentTimeMillis();
			interpreter = getInterpreter();
			interpreter.init(null);

			if (defaultName != null)
			{
				interpreter.setDefaultName(defaultName);
			}

			long after = System.currentTimeMillis();

			infoln("Initialized " + plural(classes.size(), "class", "es")
					+ " in " + (double) (after - before) / 1000 + " secs. ");
		} catch (ContextException e)
		{
			println("Initialization: " + e);
			e.ctxt.printStackTrace(Console.out, true);
			dumpLogs();
			return ExitStatus.EXIT_ERRORS;
		} catch (Exception e)
		{
			println("Initialization: " + e.getMessage());
			dumpLogs();
			return ExitStatus.EXIT_ERRORS;
		}

		try
		{
			ExitStatus status;

			if (script != null)
			{
				println(interpreter.execute(script, null).toString());
				status = ExitStatus.EXIT_OK;
			} else
			{
				infoln("Interpreter started");
				CommandReader reader = new ClassCommandReader(interpreter, "> ");
				status = reader.run(filenames);
			}

			if (logfile != null)
			{
				RTLogger.dump(true);
				infoln("RT events dumped to " + logfile);
			}

			return status;
		} catch (ContextException e)
		{
			println("Execution: " + e);
			e.ctxt.printStackTrace(Console.out, true);
		} catch (Exception e)
		{
			println("Execution: " + e);
		}

		dumpLogs();

		return ExitStatus.EXIT_ERRORS;
	}

	@Override
	public ClassInterpreter getInterpreter() throws Exception
	{
		ClassInterpreter interpreter = new ClassInterpreter(classes);
		return interpreter;
	}
}
