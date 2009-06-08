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

package org.overturetool.vdmj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.commands.ModuleCommandReader;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;


/**
 * The main class of the VDM-SL parser/checker/interpreter.
 */

public class VDMSL extends VDMJ
{
	private ModuleList modules = new ModuleList();

	public VDMSL()
	{
		Settings.dialect = Dialect.VDM_SL;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */

	@Override
	public ExitStatus parse(List<File> files)
	{
		modules.clear();
		LexLocation.resetLocations();
   		int perrs = 0;
   		int pwarn = 0;
   		long duration = 0;

   		for (File file: files)
   		{
   			ModuleReader reader = null;

   			try
   			{
   				if (file.getName().endsWith(".lib"))
   				{
   					FileInputStream fis = new FileInputStream(file);
   	    	        GZIPInputStream gis = new GZIPInputStream(fis);
   	    	        ObjectInputStream ois = new ObjectInputStream(gis);

   	    	        ModuleList loaded = null;
   	    	        long begin = System.currentTimeMillis();

   	    	        try
   	    	        {
   	    	        	loaded = (ModuleList)ois.readObject();
   	    	        }
       	 			catch (Exception e)
       				{
       	   				println(file + " is not a valid VDM-SL library");
       	   				perrs++;
       	   				continue;
       				}
       	 			finally
       	 			{
       	 				ois.close();
       	 			}

   	    	        long end = System.currentTimeMillis();
   	    	        loaded.setLoaded();
   	    	        modules.addAll(loaded);

   	    	   		infoln("Loaded " + plural(loaded.size(), "module", "s") +
   	    	   			" from " + file + " in " + (double)(end-begin)/1000 + " secs");
   				}
   				else
   				{
   					long before = System.currentTimeMillis();
    				LexTokenReader ltr =
    					new LexTokenReader(file, Settings.dialect, filecharset);
        			reader = new ModuleReader(ltr);
        			modules.addAll(reader.readModules());
        	   		long after = System.currentTimeMillis();
        	   		duration += (after - before);
   				}
    		}
			catch (MessageException e)
			{
   				println(e.toString());
			}
			catch (Throwable e)
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

   		int n = modules.notLoaded();

   		if (n > 0)
   		{
       		info("Parsed " + plural(n, "module", "s") + " in " +
       			(double)(duration)/1000 + " secs. ");
       		info(perrs == 0 ? "No syntax errors" :
       			"Found " + plural(perrs, "syntax error", "s"));
      		infoln(pwarn == 0 ? "" : " and " +
      			(warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
   		}

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#typeCheck()
	 */

	@Override
	public ExitStatus typeCheck()
	{
		int terrs = 0;
		long before = System.currentTimeMillis();

   		try
   		{
   			TypeChecker typeChecker = new ModuleTypeChecker(modules);
   			typeChecker.typeCheck();
   		}
		catch (MessageException e)
		{
			println(e.toString());
		}
		catch (Throwable e)
		{
			println(e.toString());
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

   		int n = modules.notLoaded();

   		if (n > 0)
   		{
    		info("Type checked " + plural(n, "module", "s") +
    			" in " + (double)(after-before)/1000 + " secs. ");
      		info(terrs == 0 ? "No type errors" :
      			"Found " + plural(terrs, "type error", "s"));
      		infoln(twarn == 0 ? "" : " and " +
      			(warnings ? "" : "suppressed ") + plural(twarn, "warning", "s"));
   		}

		if (outfile != null && terrs == 0)
		{
			try
			{
				before = System.currentTimeMillis();
    	        FileOutputStream fos = new FileOutputStream(outfile);
    	        GZIPOutputStream gos = new GZIPOutputStream(fos);
    	        ObjectOutputStream oos = new ObjectOutputStream(gos);

    	        oos.writeObject(modules);
    	        oos.close();
    	   		after = System.currentTimeMillis();

    	   		infoln("Saved " + plural(modules.size(), "module", "s") +
    	   			" to " + outfile + " in " +
    	   			(double)(after-before)/1000 + " secs. ");
			}
			catch (IOException e)
			{
				infoln("Cannot write " + outfile + ": " + e.getMessage());
				terrs++;
			}
		}

		if (pog && terrs == 0)
		{
			ProofObligationList list = modules.getProofObligations();

			if (list.isEmpty())
			{
				println("No proof obligations generated");
			}
			else
			{
    			println("Generated " +
    				plural(list.size(), "proof obligation", "s") + ":\n");
    			print(list.toString());
			}
		}

   		return terrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#interpret(List)
	 */

	@Override
	protected ExitStatus interpret(List<File> filenames)
	{
		ModuleInterpreter interpreter = null;

		try
		{
   			long before = System.currentTimeMillis();
   			interpreter = getInterpreter();
   			long after = System.currentTimeMillis();

   	   		infoln("Initialized " + plural(modules.size(), "module", "s") + " in " +
   	   			(double)(after-before)/1000 + " secs. ");
		}
		catch (ContextException e)
		{
			println("Initialization: " + e);
			e.ctxt.printStackTrace(true);
			return ExitStatus.EXIT_ERRORS;
		}
		catch (Exception e)
		{
			println("Initialization: " + e);
			return ExitStatus.EXIT_ERRORS;
		}

		try
		{
			if (script != null)
			{
				println(interpreter.execute(script, null).toString());
				return ExitStatus.EXIT_OK;
			}
			else
			{
				infoln("Interpreter started");
				CommandReader reader = new ModuleCommandReader(interpreter, "> ");
				return reader.run(filenames);
			}
		}
		catch (ContextException e)
		{
			println("Execution: " + e);
			e.ctxt.printStackTrace(true);
		}
		catch (Exception e)
		{
			println("Execution: " + e);
		}

		return ExitStatus.EXIT_ERRORS;
	}

	@Override
	public ModuleInterpreter getInterpreter() throws Exception
	{
		ModuleInterpreter interpreter = new ModuleInterpreter(modules);
		interpreter.init(null);
		return interpreter;
	}
}
