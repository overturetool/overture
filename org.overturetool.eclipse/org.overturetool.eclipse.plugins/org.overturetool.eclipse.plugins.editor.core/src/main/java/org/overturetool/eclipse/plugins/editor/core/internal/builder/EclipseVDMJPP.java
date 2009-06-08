package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class EclipseVDMJPP extends VDMJ {

	public ClassList classes = new ClassList();
	private TypeChecker typeChecker;
	private ArrayList<VDMError> parseErrors = new ArrayList<VDMError>();
	private ArrayList<VDMWarning> parseWarnings = new ArrayList<VDMWarning>();
	
	public EclipseVDMJPP() {
		Settings.dialect = Dialect.VDM_PP;
		typeChecker = null;
	}
	
	@Override
	protected ExitStatus interpret(List<File> filenames){
		ClassInterpreter interpreter = null;
		try
		{
   			long before = System.currentTimeMillis();
			interpreter = new ClassInterpreter(classes);
   			long after = System.currentTimeMillis();

   	   		infoln("Initialized " + plural(classes.size(), "class", "es") + " in " +(double)(after-before)/1000 + " secs. ");
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
//				interpreter.execute(script).
//				println(interpreter.execute(script).toString());
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
	
	@Override
	public ExitStatus parse(List<File> files) {
		parseErrors.clear();
		parseWarnings.clear();
		classes.clear();
		LexLocation.resetLocations();
   		int perrs = 0;

   		for (File file: files)
   		{
   			ClassReader reader = null;

   			try
   			{
				LexTokenReader ltr = new LexTokenReader(file, Settings.dialect, filecharset);
    			reader = new ClassReader(ltr);
    			classes.addAll(reader.readClasses());
    		}
			catch (MessageException e)
			{
   				println(e.toString());
			}
			catch (Throwable e)
			{
   				println(e.toString());
   				if (e instanceof ParserException)
   				{
   					ParserException parserException = (ParserException) e;
   					parseErrors.add(new VDMError(parserException));
   				}
   				// TODO Add to parseErrors
   				if (e instanceof StackOverflowError)
   				{
   					e.printStackTrace();
   				}
   				perrs++;
			}

			if (reader != null && reader.getErrorCount() > 0)
			{
    			perrs += reader.getErrorCount();
    			reader.printErrors(Console.out);
			}
			for (VDMError error : reader.getErrors()) {
				parseErrors.add(error);
			}
			for (VDMWarning warning : reader.getWarnings()) {
				parseWarnings.add(warning);
			}
			
   		}
   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}
	

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(java.util.List)
	 */
	public ExitStatus parse(String content)
	{
		parseErrors.clear();
		classes.clear();
		LexLocation.resetLocations();
   		int perrs = 0;
   		ClassReader reader = null;

		try
		{
			LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
			reader = new ClassReader(ltr);
			classes.addAll(reader.readClasses());
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

			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0)
		{
			perrs += reader.getErrorCount();
			reader.printErrors(Console.out);
		}

   		for (VDMError error : reader.getErrors()) {
			parseErrors.add(error);
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
	
	
	
	public List<VDMWarning> getTypeWarnings(){
		return TypeChecker.getWarnings();
	}
	
	public List<VDMError> getTypeErrors(){
		return TypeChecker.getErrors();
	}

	@Override
	public ExitStatus typeCheck() {
		int terrs = 0;
		long before = System.currentTimeMillis();

   		try
   		{
   			typeChecker = new ClassTypeChecker(classes);
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

	@Override
	public Interpreter getInterpreter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
