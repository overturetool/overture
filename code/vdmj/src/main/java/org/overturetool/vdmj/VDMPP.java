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

import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;


/**
 * The main class of the VDM++ and VICE parser/checker/interpreter.
 */

public class VDMPP extends VDMJ
{
	protected ClassList classes = new ClassList();

	public VDMPP()
	{
		Settings.dialect = Dialect.VDM_PP;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */

	@Override
	protected ExitStatus parse(List<String> files)
	{
		classes.clear();
		LexLocation.resetLocations();
		long before = System.currentTimeMillis();
   		int perrs = 0;

   		for (String file: files)
   		{
   			ClassReader reader = null;

   			try
   			{
   				if (file.endsWith(".obj"))
   				{
   					FileInputStream fis = new FileInputStream(file);
   	    	        GZIPInputStream gis = new GZIPInputStream(fis);
   	    	        ObjectInputStream ois = new ObjectInputStream(gis);
   	    	        ClassList loaded = null;

   	    	        try
   	    	        {
   	    	        	loaded = (ClassList)ois.readObject();
   	    	        }
       	 			catch (Exception e)
       				{
       	   				println("Object file is not compatible?");
       	   				perrs++;
       	   				continue;
       				}
       	 			finally
       	 			{
       	 				ois.close();
       	 			}

       	 			loaded.remap();
   	    	        loaded.setLoaded();
   	    	        classes.addAll(loaded);

   	    	   		infoln("Loaded " + plural(loaded.size(), "class", "es") + " from " + file);
   				}
   				else
   				{
    				LexTokenReader ltr = new LexTokenReader(
    					new File(file), Settings.dialect, filecharset);
        			reader = new ClassReader(ltr);
        			classes.addAll(reader.readClasses());
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
   		}

   		long after = System.currentTimeMillis();

   		info("Parsed " + plural(classes.size(), "class", "es") + " in " +
   			(double)(after-before)/1000 + " secs. ");
   		infoln(perrs == 0 ? "No syntax errors" :
   			"Found " + plural(perrs, "syntax error", "s"));

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#typeCheck()
	 */

	@Override
	protected ExitStatus typeCheck()
	{
		int terrs = 0;
		long before = System.currentTimeMillis();

   		try
   		{
   			TypeChecker typeChecker = new ClassTypeChecker(classes);
   			typeChecker.typeCheck();
   		}
		catch (MessageException e)
		{
			println(e.toString());
		}
		catch (Throwable e)
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

		info("Type checked " + plural(classes.size(), "class", "es") + " in " +
			(double)(after-before)/1000 + " secs. ");
  		info(terrs == 0 ? "No type errors" :
  			"Found " + plural(terrs, "type error", "s"));
		infoln(twarn == 0 ? "" : " and " +
			(warnings ? "" : "suppressed ") + plural(twarn, "warning", "s"));

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

    	   		infoln("Saved " + plural(classes.size(), "class", "es") +
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
			ProofObligationList list = classes.getProofObligations();

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
	protected ExitStatus interpret(List<String> filenames)
	{
		ClassInterpreter interpreter = null;

		try
		{
   			long before = System.currentTimeMillis();
			interpreter = new ClassInterpreter(classes);
			interpreter.init(null);
   			long after = System.currentTimeMillis();

   	   		infoln("Initialized " + plural(classes.size(), "class", "es") + " in " +
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
				CommandReader reader = new ClassCommandReader(interpreter, "> ");
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
}
