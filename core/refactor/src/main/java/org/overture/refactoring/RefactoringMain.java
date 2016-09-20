package org.overture.refactoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class RefactoringMain {
	public static final String PRINT_ARG = "-print";
	public static final String OO_ARG = "-pp";
	public static final String RT_ARG = "-rt";
	public static final String SL_ARG = "-sl";
	public static final String RENAME_ARG = "-rename;";
	
	private static GeneratedData genData;
	
	public static void main(String[] args)
	{
		boolean printClasses = false;
		
		if (args == null || args.length <= 1)
		{
			usage("Too few arguments provided");
		}	
		
		genData = null;
		List<String> listArgs = Arrays.asList(args);
		List<File> files = new LinkedList<File>();
		RefactoringMode refacMode = null;
		String[] parameters = null;
		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();
			
			if (arg.equals(OO_ARG))
			{
				refacMode = RefactoringMode.OO_SPEC;
				Settings.dialect = Dialect.VDM_PP;
			} else if (arg.equals(RT_ARG))
			{
				refacMode = RefactoringMode.OO_SPEC;
				Settings.dialect = Dialect.VDM_RT;
			} else if (arg.equals(SL_ARG))
			{
				refacMode = RefactoringMode.SL_SPEC;
				Settings.dialect = Dialect.VDM_SL;
			} else if (arg.equals(PRINT_ARG))
			{
				printClasses = true;
			} else if (arg.contains(RENAME_ARG)){
				String parms = arg;
				parms = parms.replace(RENAME_ARG,"");
				parameters = parms.split(";");
				
			} else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (RefactoringUtils.isVdmSourceFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}
		
		//////////////////////////////////////////////////////////
		
		if (Settings.dialect == null)
		{
			usage("No VDM dialect specified");
		}
		
		MsgPrinter.getPrinter().println("\n************************");
		MsgPrinter.getPrinter().println("Starting refactoring...\n");

		if (files.isEmpty())
		{
			usage("Input files are missing");
		}
		
		if (refacMode == RefactoringMode.SL_SPEC)
		{
			handleSl(files, printClasses, parameters);
			
		} else if (refacMode == RefactoringMode.OO_SPEC)
		{
			handleOo(files, Settings.dialect, printClasses, parameters);
		} else
		{
			MsgPrinter.getPrinter().errorln("Unexpected dialect: "
					+ refacMode);
		}
		
	}
	
	public static void handleSl(List<File> files, boolean printCode, String[] parameters)
	{
		try
		{
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);

			if (GeneralCodeGenUtils.hasErrors(tcResult))
			{
				MsgPrinter.getPrinter().error("Found errors in VDM model:");
				MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}
			
			RefactoringBase refactoringBase = new RefactoringBase();
			
			if(parameters != null && parameters.length >= 3){
				genData = refactoringBase.generate(refactoringBase.getNodes(tcResult.result), parameters);
				//test(data);
			} else {
				MsgPrinter.getPrinter().println("No parameters");
			}

			//processData(printCode, outputDir, vdmCodGen, data, separateTestCode);

		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());
		}
	}
	
	public static void handleOo(List<File> files, Dialect dialect, boolean printCode, String[] parameters)
	{
		try
		{
			TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(files);
			
			if (GeneralCodeGenUtils.hasErrors(tcResult))
			{
				MsgPrinter.getPrinter().error("Found errors in VDM model:");
				MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}
			
			RefactoringBase refactoringBase = new RefactoringBase();
			
			if(parameters != null && parameters.length >= 3){
				genData = refactoringBase.generate(refactoringBase.getNodes(tcResult.result), parameters);
				//test(genData);
			} else {
				MsgPrinter.getPrinter().println("No parameters");
			}
//			processData(printCode, files.get(0), data);

		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());

		}
	}
	
	public static GeneratedData getGeneratedData(){
		return genData;
	}

	
	public static void usage(String msg)
	{
		MsgPrinter.getPrinter().errorln("VDM Refactoring Generator: " + msg
				+ "\n");
		
		MsgPrinter.getPrinter().errorln(PRINT_ARG
				+ ": print the refactored code to the console");

		System.exit(1);
	}
	
	public static void processData(boolean printCode, final File outputDir,
			GeneratedData data)
	{

		MsgPrinter.getPrinter().println("");

		if (outputDir != null)
		{

		}

		if (printCode)
		{
			MsgPrinter.getPrinter().println("**********");
			MsgPrinter.getPrinter().println("Content here:");
			MsgPrinter.getPrinter().println("\n");
		}

		List<Renaming> allRenamings = data.getAllRenamings();

		if (!allRenamings.isEmpty())
		{
			MsgPrinter.getPrinter().println("\nFollowing renamings of content have been made: ");

			MsgPrinter.getPrinter().println(GeneralCodeGenUtils.constructVarRenamingString(allRenamings));
		}

		if (data.getWarnings() != null && !data.getWarnings().isEmpty())
		{
			MsgPrinter.getPrinter().println("");
			for (String w : data.getWarnings())
			{
				MsgPrinter.getPrinter().println("[WARNING] " + w);
			}
		}
	}
	
	public static void test(GeneratedData data){
		BufferedReader br = null;
        int lineCount = 0;
        String line = null;
		List<Renaming> allRenamings = data.getAllRenamings();

		if (!allRenamings.isEmpty())
		{
			
			Collections.reverse(allRenamings); 
	        try {
				br = new BufferedReader(new FileReader("D:\\test.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	            try {
					while ((line = br.readLine()) != null)   
					{
	
					    StringBuilder buf = new StringBuilder(line);
					              // Print the content on the console
					    				   
					    	List<Renaming> lineRenamings = new ArrayList<Renaming>();
					    	
					    	for(Iterator<Renaming> i = allRenamings.iterator(); i.hasNext(); ) {
					    			Renaming item = i.next();
					    		  if (item.getLoc().getEndLine() == lineCount+1) {
					    			  lineRenamings.add(item);
					    		  }
				    		}
					    	Collections.reverse(lineRenamings); 
				    	  for(Iterator<Renaming> i = lineRenamings.iterator(); i.hasNext(); ) {
					            Renaming item = i.next();
					            String endPiece = buf.substring(item.getLoc().getEndPos()-1);
					            String startPiece = buf.substring(0,item.getLoc().getStartPos()-1);
					            line = startPiece + item.getNewName() + endPiece;
					            buf = new StringBuilder(line);
					        }

					    	System.out.println(line);
					    
					    lineCount++;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
}
