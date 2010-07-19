package nl.marcelverhoef.treegen;

// generic java imports
import java.util.*;
import java.io.File;

// project specific imports
import nl.marcelverhoef.treegen.ast.itf.*;
import nl.marcelverhoef.treegen.typecheck.*;
import nl.marcelverhoef.treegen.codegenerator.*;

public class TreeGen {
	
	// generic main access function
	public static void main(String[] args) {
		if (args.length == 0) {
			// abort if there are no files specified on the command-line
			System.out.println("treegen: no input files!");			
		} else {
			// place holders for the input parameters
			String packname = "";
			String basename = ".\\src";
			String toplevel = "";
			List<File> lof = new Vector<File>();
			
			// process the input parameters
			int idx = 0;
			while (idx < args.length) {
				if (args[idx].compareTo("-p") == 0) {
					if (idx+1 < args.length) {
						// copy the package name and update the index
						packname = args[idx+1];
						idx += 2;
						// diagnostics
						System.out.println("Using package name '"+packname+"'");
					} else {
						// just update the index
						idx += 1;
					}
				} else if (args[idx].compareTo("-d") == 0) {
					if (idx+1 < args.length) {
						// copy the base directory and update the index
						basename = args[idx+1];
						idx += 2;
						// diagnostics
						System.out.println("Using base directory '"+packname+"'");
					} else {
						// just update the index
						idx += 1;
					}
				} else if (args[idx].compareTo("-t") == 0) {
					if (idx+1 < args.length) {
						// copy the top-level AST entry and update the index
						toplevel = args[idx+1];
						idx += 2;
						// diagnostics
						System.out.println("Using top-level entry point '"+toplevel+"'");
					} else {
						// just update the index
						idx += 1;
					}
				} else {
					// assume it is a file name and update the index
					lof.add(new File(args[idx++]));
				}
			}
			
			// call the top-level function
			generate(packname, basename, toplevel, lof);
		}
	}
	
	public static void generate(String packname, String basename, String toplevel, List<File> lof)
	{
		// keep a local error count
		int errors = 0;
		
		// define the overall list of classes
		java.util.List<ITreeGenAstClassDefinition> defs = new java.util.Vector<ITreeGenAstClassDefinition>();
		
		// check each command-line argument
		for (File fp : lof) {
			// reconstruct the file name
			String arg = fp.getAbsolutePath(); // +File.separator+fp.getName();
			
			// check if the file exists
			if (fp.exists()) {
				// check if the file is readable
				if (fp.canRead()) {
					// create a parser instance
					TreeParser tp = new TreeParser(arg);
					// call the parser and add the result to the overall list
					defs.addAll(tp.parse());
					// update the error count
					errors += tp.errors;
				} else {
					// abort with warning
					System.out.println("treegen: file '"+arg+"' cannot be opened!");
					// increase the error count
					errors++;
				}
			} else {
				// abort with warning
				System.out.println("treegen: file '"+arg+"' does not exist!");
				// increase the error count
				errors++;
			}
		}
		
		// only start type check if there were no syntax errors
		if (errors == 0) {
			// create the tree checker object
			TreeChecker theChecker = new TreeChecker();
			
			// call the tree checker
			theChecker.performCheck(defs);

			// update the total error count
			errors += theChecker.errors;
			
			// only start the code generator if there were no semantic errors and input non-empty
			if  ((errors == 0) && (!theChecker.cls.isEmpty())) {
				// create the code generator object
				CodeGenerator theCodeGen = new CodeGenerator(packname, basename, toplevel);
				
				// call the code generator
				theCodeGen.generateCode(theChecker.cls);
				
				// update the total error count
				errors += theCodeGen.errors;
			}
			
		}
		
		// diagnostics
		System.out.println(errors+" errors found during processing");
	}
	
//	public static void main(String[] args) {
//		// check the argument list
//		if (args.length == 0) {
//			// abort if there are no files specified on the command-line
//			System.out.println("treegen: no input files!");
//		} else {
//			// keep a local error count
//			int errors = 0;
//			
//			// define the overall list of classes
//			java.util.List<ITreeGenAstClassDefinition> defs = new java.util.Vector<ITreeGenAstClassDefinition>();
//			
//			// check each command-line argument
//			for (String arg: args) {
//				// create a file handle
//				java.io.File fp = new java.io.File(arg);
//				// check if the file exists
//				if (fp.exists()) {
//					// check if the file is readable
//					if (fp.canRead()) {
//						// create a parser instance
//						TreeParser tp = new TreeParser(arg);
//						// call the parser and add the result to the overall list
//						defs.addAll(tp.parse());
//						// update the error count
//						errors += tp.errors;
//					} else {
//						// abort with warning
//						System.out.println("treegen: file '"+arg+"' cannot be opened!");
//						// increase the error count
//						errors++;
//					}
//				} else {
//					// abort with warning
//					System.out.println("treegen: file '"+arg+"' does not exist!");
//					// increase the error count
//					errors++;
//				}
//			}
//			
//			// only start type check if there were no syntax errors
//			if (errors == 0) {
//				// create the tree checker object
//				TreeChecker theChecker = new TreeChecker();
//				
//				// call the tree checker
//				theChecker.performCheck(defs);
//
//				// update the total error count
//				errors += theChecker.errors;
//				
//				// only start the code generator if there were no semantic errors and input non-empty
//				if  ((errors == 0) && (!theChecker.cls.isEmpty())) {
//					// create the code generator object
//					CodeGenerator theCodeGen = new CodeGenerator();
//					
//					// call the code generator
//					theCodeGen.generateCode(theChecker.cls);
//					
//					// update the total error count
//					errors += theCodeGen.errors;
//				}
//				
//			}
//			
//			// diagnostics
//			System.out.println(errors+" errors found during processing");
//		}
//	}
	
}
