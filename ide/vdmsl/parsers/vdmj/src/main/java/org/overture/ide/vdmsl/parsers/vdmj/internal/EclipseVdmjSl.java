package org.overture.ide.vdmsl.parsers.vdmj.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.commands.ModuleCommandReader;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class EclipseVdmjSl extends VDMJ implements IEclipseVdmj
{

	public ModuleList modules = new ModuleList();
	private TypeChecker typeChecker;
	private ArrayList<VDMError> parseErrors = new ArrayList<VDMError>();
	private ArrayList<VDMWarning> parseWarnings = new ArrayList<VDMWarning>();

	public EclipseVdmjSl()
	{
		typeChecker = null;
		Settings.dialect = Dialect.VDM_SL;
	}

	@Override
	protected ExitStatus interpret(List<File> filenames)
	{

		ModuleInterpreter interpreter = null;
		try
		{
			long before = System.currentTimeMillis();
			interpreter = new ModuleInterpreter(modules);
			long after = System.currentTimeMillis();

			infoln("Initialized " + plural(modules.size(), "class", "es")
					+ " in " + (double) (after - before) / 1000 + " secs. ");
		} catch (ContextException e)
		{
			println("Initialization: " + e);
			// PrintWriter pw = new PrintWriter(System.out,true);
			// e.ctxt.printStackTrace(pw,true);
			return ExitStatus.EXIT_ERRORS;
		} catch (Exception e)
		{
			println("Initialization: " + e);
			return ExitStatus.EXIT_ERRORS;
		}

		try
		{
			if (script != null)
			{
				// interpreter.execute(script).
				// println(interpreter.execute(script).toString());
				return ExitStatus.EXIT_OK;
			} else
			{
				infoln("Interpreter started");
				CommandReader reader = new ModuleCommandReader(interpreter,
						"> ");
				return reader.run(filenames);
			}
		} catch (ContextException e)
		{
			println("Execution: " + e);
			// PrintWriter pw = new PrintWriter(System.out,true);
			// e.ctxt.printStackTrace(pw,true);
		} catch (Exception e)
		{
			println("Execution: " + e);
		}

		return ExitStatus.EXIT_ERRORS;
	}

	@Override
	public ExitStatus parse(List<File> files)
	{
		parseErrors.clear();
		parseWarnings.clear();

		modules.clear();
		LexLocation.resetLocations();
		int perrs = 0;
//		int pwarn = 0;
//		// long duration = 0;
//
//		for (File file : files)
//		{
//
//		}

		// int n = classes.notLoaded();
		//
		// if (n > 0)
		// {
		// info("Parsed " + plural(n, "class", "es") + " in " +
		// (double)(duration)/1000 + " secs. ");
		// info(perrs == 0 ? "No syntax errors" :
		// "Found " + plural(perrs, "syntax error", "s"));
		// infoln(pwarn == 0 ? "" : " and " +
		// (warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
		// }

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;

	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */
	public ExitStatus parse(String content,File file)
	{
		parseErrors.clear();
		parseWarnings.clear();
		modules.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		ModuleReader reader = null;

		try
		{
			LexTokenReader ltr = new LexTokenReader(content, Settings.dialect,file);
			reader = new ModuleReader(ltr);
			modules.addAll(reader.readModules());
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

			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0)
		{
			perrs += reader.getErrorCount();
			// reader.printErrors(Console.out);
			for (VDMError error : reader.getErrors())
			{
				parseErrors.add(error);
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			// reader.printWarnings(Console.out);
			for (VDMWarning warning : reader.getWarnings())
			{
				parseWarnings.add(warning);
			}
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	public List<VDMWarning> getParseWarnings()
	{
		return parseWarnings;
	}

	public List<VDMError> getParseErrors()
	{
		return parseErrors;
	}

	public List<VDMWarning> getTypeWarnings()
	{
		return TypeChecker.getWarnings();
	}

	public List<VDMError> getTypeErrors()
	{
		return TypeChecker.getErrors();
	}

	@Override
	public ExitStatus typeCheck()
	{
		int terrs = 0;

		try
		{
			typeChecker = new ModuleTypeChecker(modules);
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
		terrs += TypeChecker.getErrorCount();

		if (terrs > 0)
		{
			// TypeChecker.printErrors(Console.out);
		}

		// if (pog && terrs == 0)
		// {
		// ProofObligationList list = classes.getProofObligations();
		//
		// }

		return terrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	@Override
	public Interpreter getInterpreter() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ModuleList getModules()
	{
		return modules;
	}

	public ExitStatus parse(File file)
	{
		parseErrors.clear();
		parseWarnings.clear();

		modules.clear();
		LexLocation.resetLocations();
		
		int perrs = 0;
		ModuleReader reader = null;

		try
		{
			if (file.getName().endsWith(".lib"))
			{
				FileInputStream fis = new FileInputStream(file);
				GZIPInputStream gis = new GZIPInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(gis);

				ModuleList loaded = null;
				// long begin = System.currentTimeMillis();

				try
				{
					loaded = (ModuleList) ois.readObject();
				} catch (Exception e)
				{
					// println(file + " is not a valid VDM++ library");
					perrs++;
					
				} finally
				{
					ois.close();
				}

				// long end = System.currentTimeMillis();
				loaded.setLoaded();
				modules.addAll(loaded);
				// classes.remap();

				// infoln("Loaded " + plural(loaded.size(), "class", "es") +
				// " from " + file + " in " + (double)(end-begin)/1000 +
				// " secs");
			} else
			{
				LexTokenReader ltr = new LexTokenReader(file, Settings.dialect,
						filecharset);
				reader = new ModuleReader(ltr);
				modules.addAll(reader.readModules());
			}
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

			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0)
		{
			perrs += reader.getErrorCount();
			// reader.printErrors(Console.out);
			for (VDMError error : reader.getErrors())
			{
				parseErrors.add(error);
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			// reader.printWarnings(Console.out);
			for (VDMWarning warning : reader.getWarnings())
			{
				parseWarnings.add(warning);
			}
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

}
