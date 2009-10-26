package org.overture.ide.builders.vdmj;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMRT;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

/***
 * VDMJ interface used to build VDM-RT models
 * @author kela
 *
 */
public class EclipseVdmjRt extends VDMRT implements IEclipseVdmj {
	public ClassList modules;
	private ArrayList<VDMError> parseErrors = new ArrayList<VDMError>();
	private ArrayList<VDMWarning> parseWarnings = new ArrayList<VDMWarning>();
	private TypeChecker typeChecker;
	
	public EclipseVdmjRt(ClassList m) {
		modules=m;
		typeChecker = null;
		parseErrors = new ArrayList<VDMError>();
		parseWarnings = new ArrayList<VDMWarning>();
		Settings.dialect = Dialect.VDM_RT;
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



	@Override
	public ExitStatus typeCheck() {
		int terrs = 0;
		if(modules==null)
			return ExitStatus.EXIT_ERRORS; 
		
		long before = System.currentTimeMillis();

   		try
   		{
   			typeChecker = new ClassTypeChecker(modules);
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
			//TypeChecker.printErrors(Console.out);
		}

  		int twarn = TypeChecker.getWarningCount();

		if (twarn > 0 && warnings)
		{
//			TypeChecker.printWarnings(Console.out);
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





}
