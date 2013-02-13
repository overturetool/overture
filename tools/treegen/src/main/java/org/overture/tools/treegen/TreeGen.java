package org.overture.tools.treegen;

// generic java imports
import java.util.*;
import java.io.File;

// project specific imports
import org.overture.tools.treegen.ast.imp.*;
import org.overture.tools.treegen.ast.itf.*;
import org.overture.tools.treegen.typecheck.*;
import org.overture.tools.treegen.codegenerator.*;

public class TreeGen {
	
	// generic main access function
	public static void main(String[] args) {
		if (args.length == 0) {
			// abort if there are no files specified on the command-line
			System.out.println("treegen: no input files!");			
		} else {
			// place holders for the input parameters
			String packname = null;
			String javadir = null;
			String vdmppdir = null;
			String toplevel = null;
			boolean split = false;
			
			// placeholder for the command-line options
			List<TreeGenOptions> tgopts = new Vector<TreeGenOptions>();
			
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
						// copy the base javadir and update the index
						javadir = args[idx+1];
						idx += 2;
						// diagnostics
						System.out.println("Using base javadir '"+javadir+"'");
					} else {
						// just update the index
						idx += 1;
					}
				} else if (args[idx].compareTo("-v") == 0) {
					if (idx+1 < args.length) {
						// copy the base vdmppdir and update the index
						vdmppdir = args[idx+1];
						idx += 2;
						// diagnostics
						System.out.println("Using base vdmppdir '"+vdmppdir+"'");
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
				} else if (args[idx].compareTo("-s") == 0) {
					// copy the value and update the index
					split = true;
					idx++;
					// diagnostics
					System.out.println("Do not use default VPP file collate option");
				} else {
					// assume it is a file name and update the index
					tgopts.add(new TreeGenOptions(args[idx++]));
				}
			}
			
			// iterate over all treegen options and set the other properties
			for (TreeGenOptions opt: tgopts) {
				// copy the javadir setting if it is defined
				if (javadir != null) opt.setJavaDirectory(javadir);
				// copy the javadir setting if it is defined
				if (vdmppdir != null) opt.setVppDirectory(vdmppdir);
				// copy the package name if it is defined
				if (packname != null) opt.setPackage(packname);
				// copy the top-level definition if it is defined
				if (toplevel != null) opt.setToplevel(toplevel);
				// set the option to split the VPP generated files
				opt.setSplitVpp(split);
			}
			
			// call the top-level function
			generate(tgopts);
		}
	}
	
	public static void generate(List<TreeGenOptions> optlst)
	{
		// keep a local error count
		int errors = 0;
		
		// define the overall list of classes
		java.util.List<ITreeGenAstClassDefinition> defs = new java.util.Vector<ITreeGenAstClassDefinition>();
		
		// parse all files
		for (TreeGenOptions opt : optlst) {
			// create the file pointer
			String arg = opt.getFilename();
			File fp = new File(arg);
			
			// check if the file exists
			if (fp.exists()) {
				// check if the file is readable
				if (fp.canRead()) {
					// create a parser instance
					TreeParser tp = new TreeParser(arg);
					// call the parser and retrieve the list of abstract syntax trees parsed
					java.util.List<ITreeGenAstClassDefinition> ldefs = tp.parse();
					// add the current command-line options to each of the parse trees
					for (ITreeGenAstClassDefinition def: ldefs) {
						// cast interface to instance
						TreeGenAstClassDefinition tgadef = (TreeGenAstClassDefinition) def;
						// set (remember) the command-line options used
						tgadef.setOpts(opt);
					}
					// add the parse trees to the list of all trees
					defs.addAll(ldefs);
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
				CodeGenerator theCodeGen = new CodeGenerator();
				
				// call the code generator
				theCodeGen.generateCode(theChecker.cls);
				
				// update the total error count
				errors += theCodeGen.errors;
			}
			
		}
		
		// diagnostics
		System.out.println(errors+" errors found during processing");
	}
		
}
