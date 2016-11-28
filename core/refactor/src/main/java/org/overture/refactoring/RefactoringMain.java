package org.overture.refactoring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.config.Settings;
import org.overture.prettyprinter.RefactoringPrettyPrinter;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class RefactoringMain {
	public static final String PRINT_ARG = "-print";
	public static final String TEST_ARG = "-test";
	public static final String OO_ARG = "-pp";
	public static final String RT_ARG = "-rt";
	public static final String SL_ARG = "-sl";
	public static final String RENAME_ARG = "-rename;";
	public static final String EXTRACT_ARG = "-extract;";
	public static final String SIGNATURE_ARG = "-signature;";
	public static final String DEADMODELPARTREMOVER_ARG = "-DeadModelPartRemover";
	public static final String CONVERTFUNCTIONTOOPERATION = "-ConvertFunctionToOperation;";
	
	private static RefactoringBase refactoringBase = new RefactoringBase();
	
	private static boolean printAST = false;
	private static boolean test = false;
	private static boolean rename = false;
	private static boolean extract = false;
	private static boolean signature = false;
	private static boolean unreachableStmRemove = false;
	private static boolean convertFunctionToOperation = false;
	private static List<INode> generatedAST;
	private static GeneratedData generatedData;
	
	public static void main(String[] args)
	{	
		if (args == null || args.length <= 1)
		{
			usage("Too few arguments provided");
		}	
		printAST = false;
		test = false;
		rename = false;
		extract = false;
		signature = false;
		unreachableStmRemove = false;
		convertFunctionToOperation = false;
		generatedAST = null;
		generatedData = null;
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
				printAST = true;
			} else if (arg.equals(TEST_ARG))
			{
				test = true;
			} else if (arg.equals(DEADMODELPARTREMOVER_ARG))
			{
				unreachableStmRemove = true;
			} else if (arg.contains(CONVERTFUNCTIONTOOPERATION)){
				String parms = arg;
				parms = parms.replace(CONVERTFUNCTIONTOOPERATION,"");
				parameters = parms.split(";");
				convertFunctionToOperation = true;
			} else if (arg.contains(RENAME_ARG)){
				String parms = arg;
				parms = parms.replace(RENAME_ARG,"");
				parameters = parms.split(";");
				rename = true;
			}  else if (arg.contains(EXTRACT_ARG)){
				String parms = arg;
				parms = parms.replace(EXTRACT_ARG,"");
				parameters = parms.split(";");
				extract = true;
			} else if (arg.contains(SIGNATURE_ARG)){
				String parms = arg;
				parms = parms.replace(SIGNATURE_ARG,"");
				parameters = parms.split(";");
				signature = true;
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
		
		if (Settings.dialect == null)
		{
			usage("No VDM dialect specified");
		}
		if (files.isEmpty())
		{
			usage("Input files are missing");
		}
		
		if (refacMode == RefactoringMode.SL_SPEC)
		{
			handleSl(files, printAST, parameters);
			
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
			
			refactoringBase.init();
			if(rename){
				if(parameters != null && parameters.length >= 3){
					generatedAST = refactoringBase.generateRenaming(RefactoringBase.getNodes(tcResult.result), parameters);
				} else {
					MsgPrinter.getPrinter().println("No parameters");
				}
			}
			if(extract){
				if(parameters != null && parameters.length >= 3){
					generatedAST = refactoringBase.generateExtraction(RefactoringBase.getNodes(tcResult.result), parameters);
				} else {
					MsgPrinter.getPrinter().println("No parameters");
				}
			}
			if(signature){
				if(parameters != null && parameters.length >= 3){
					generatedAST = refactoringBase.generateSignatureChanges(RefactoringBase.getNodes(tcResult.result), parameters);
				} else {
					MsgPrinter.getPrinter().println("No parameters");
				}
			}
			if(convertFunctionToOperation){
				if(parameters != null && parameters.length >= 1){
					generatedAST = refactoringBase.convertFunctionToOperation(RefactoringBase.getNodes(tcResult.result), Integer.parseInt(parameters[0]));
				} else {
					MsgPrinter.getPrinter().println("No parameters");
				}
			}
			
			if(files.size() > 0){
				checkDefaultConfig(refactoringBase, tcResult, files.get(0));
			}
			
		} catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not code generate model: "
					+ e.getMessage());
		}
	}
	
	private static void checkDefaultConfig(RefactoringBase refactoringBase, TypeCheckResult<List<AModuleModules>> tcResult, File file){
		if(unreachableStmRemove){
			try {
				generatedAST = refactoringBase.removeUnreachableStm(RefactoringBase.getNodes(tcResult.result));
			} catch (AnalysisException e) {
				MsgPrinter.getPrinter().println(e.getMessage());
				e.printStackTrace();
			}
		}
		if(printAST){
			try {
				if(generatedAST == null){
					generatedAST = refactoringBase.extractUserModules(RefactoringBase.getNodes(tcResult.result));
				}
				PrintOutputAST(generatedAST);
			} catch (AnalysisException e) {
				MsgPrinter.getPrinter().println(e.getMessage());
				e.printStackTrace();
			}
		}
		if(test){
			if(generatedData == null){
				generatedData = refactoringBase.getGeneratedData();
			}
			if(generatedAST == null){
				generatedAST = refactoringBase.extractUserModules(RefactoringBase.getNodes(tcResult.result));
			}
			
			try {
				writeOutputASTToFile(generatedAST, file, true);
			} catch (AnalysisException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<INode> getGeneratedAST(){
		return generatedAST;
	}

	public static GeneratedData getGeneratedData(){
		return generatedData;
	}

	public static void usage(String msg)
	{
		MsgPrinter.getPrinter().errorln("VDM Refactoring Generator: " + msg
				+ "\n");
		System.exit(1);
	}
	
	public static void PrintOutputAST(List<INode> nodes)
			throws AnalysisException {
		System.out.println(RefactoringPrettyPrinter.prettyPrint(nodes));
	}
	
	public static void writeOutputASTToFile(List<INode> nodes, File file, boolean test)
			throws AnalysisException {
		String actual = RefactoringPrettyPrinter.prettyPrint(nodes);
		String filePath = file.getPath();
		if(test){
			filePath = filePath.replaceAll(".vdmsl", "Test.vdmsl");
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
		              new FileOutputStream(filePath), "utf-8"))) {
		   writer.write(actual);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
