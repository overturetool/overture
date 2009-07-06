package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class EclipseVDMJSL extends VDMJ implements EclipseVDMJ {
	public ModuleList modules = new ModuleList();
	private ArrayList<VDMError> parseErrors = new ArrayList<VDMError>();
	private ArrayList<VDMWarning> parseWarnings = new ArrayList<VDMWarning>();
	private TypeChecker typeChecker;
	
	public EclipseVDMJSL() {
		typeChecker = null;
		parseErrors = new ArrayList<VDMError>();
		parseWarnings = new ArrayList<VDMWarning>();
		Settings.dialect = Dialect.VDM_SL;
	}
	
	
	public List<VDMError> getParseErrors() {
		return parseErrors;
	}

	public List<VDMWarning> getParseWarnings() {
		return parseWarnings;
	}

	public List<VDMError> getTypeErrors() {
		return TypeChecker.getErrors();
	}

	public List<VDMWarning> getTypeWarnings() {
		return TypeChecker.getWarnings();
	}

	public ExitStatus parse(List<File> files) {
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
			catch (InternalException e)
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
    			for (VDMError error : reader.getErrors()) {
					parseErrors.add(error);
				}
//    			reader.printErrors(Console.out);
			}

			if (reader != null && reader.getWarningCount() > 0)
			{
				pwarn += reader.getWarningCount();
//    			reader.printWarnings(Console.out);
    			for (VDMWarning warning : reader.getWarnings()) {
					parseWarnings.add(warning);
				}
			}
   		}

//   		int n = modules.notLoaded();
//
//   		if (n > 0)
//   		{
//       		info("Parsed " + plural(n, "module", "s") + " in " +
//       			(double)(duration)/1000 + " secs. ");
//       		info(perrs == 0 ? "No syntax errors" :
//       			"Found " + plural(perrs, "syntax error", "s"));
//      		infoln(pwarn == 0 ? "" : " and " +
//      			(warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
//   		}

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	public ExitStatus parse(String content) {
		modules.clear();
		LexLocation.resetLocations();
   		int perrs = 0;
   		int pwarn = 0;
   		long duration = 0;

   			ModuleReader reader = null;
   			try
   			{
				long before = System.currentTimeMillis();
				LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
    			reader = new ModuleReader(ltr);
    			modules.addAll(reader.readModules());
    	   		long after = System.currentTimeMillis();
    	   		duration += (after - before);
    		}
			catch (InternalException e)
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

//   		int n = modules.notLoaded();
//
//   		if (n > 0)
//   		{
//       		info("Parsed " + plural(n, "module", "s") + " in " +
//       			(double)(duration)/1000 + " secs. ");
//       		info(perrs == 0 ? "No syntax errors" :
//       			"Found " + plural(perrs, "syntax error", "s"));
//      		infoln(pwarn == 0 ? "" : " and " +
//      			(warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
//   		}

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	public ExitStatus typeCheck() {
		int terrs = 0;
		long before = System.currentTimeMillis();

   		try
   		{
   			typeChecker = new ModuleTypeChecker(modules);
   			typeChecker.typeCheck();
   		}
		catch (InternalException e)
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

	@Override
	public Interpreter getInterpreter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ExitStatus interpret(List<File> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
