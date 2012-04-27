package org.overturetool.parser.imp;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;
import org.overturetool.visitor.*;
import org.overturetool.ast.itf.*;

public class TestParser {

	public static void printHelp()
{

System.out.println("java -jar parserJar.jar <options> <files>");
System.out.println("where options can be:");

System.out.println("-d to run the parser in debug mode, showing the complete derivation history performed by the parser");

System.out.println("-e to specify the encoding used in the UTF-8 input file. E.g. specify -eSJIS when parsing Japanese VDM++ specifications");
System.out.println("-sl to produce an abstract syntax tree as one VDM-SL value to standard output");
System.out.println("-pp to produce an abstract syntax tree as one VDM++ value to standard output");
System.out.println("-O (used in combination with -sl) generates an .asl file containing the abstract syntax tree");
System.out.println("-O (used in combination with -pp) generates an .app file containing the abstract syntax tree");
System.out.println("a list of filenames. If no filename is specified, the tool will read from standard input.");
System.out.println("");

}

	public static void main(String[] args) {

if(args==null || args.length==0)
printHelp();
		
		// output a welcome message
		System.out.println("Start test parser");

		// declare local variables
		
		// pdebug used to flag parser debug output to stdout
		boolean pdebug = false;
		
		// sl_output indicates printing the ast as a VDM-SL value
		boolean sl_output = false;
		
		// pp_output indicates printing the ast as a VDM++ value
		boolean pp_output = false;
		
		// foutput indicates printing the ast to a file (stdout otherwise)
		boolean foutput = false;
		
		// keep track of the encoding of the file
		String encoding = null;
		
		// maintain a list of file names
		Vector<String> fnames = new Vector<String>();
		
		// handle the command-line arguments
		int arg = 0;
		while (arg < args.length) {
			if (args[arg].compareTo("-d") == 0) {
				// -d is used to signal debug output
				pdebug = true;
			} else if (args[arg].compareTo("-sl") == 0) {
				// -sl will cause the AST to be pretty printed to stdout
				sl_output = true;
			} else if (args[arg].compareTo("-pp") == 0) {
				// -pp will cause the AST to be pretty printed to stdout as an VDM++ value
				pp_output = true;
			} else if (args[arg].compareTo("-O") == 0) {
				// -O will cause the AST to be pretty printed to a file
				foutput = true;
			} else if (args[arg].startsWith("-e")) {
				// -e is used to pass the encoding, e.g. -eSJIS sets the encoding to Shift-JIS
				encoding = args[arg].substring(2);
			} else {
				// all other arguments are considered as file names
				fnames.add(new String(args[arg]));
			}
			arg++;
		}
		
		// if there are no file names supplied, read from standard input
		if (fnames.size() == 0) {
			System.out.println("Reading from standard input");
			OvertureParser theParser = new OvertureParser(System.in);
			theParser.yydebug = pdebug;
			try {
				theParser.parseDocument();
				System.out.println(theParser.astDocument.getLexems().size() + " lexems scanned");
			}
			catch (CGException cge) {
				cge.printStackTrace();
			}
			System.out.println(theParser.errors + " error(s) found");
		} else {
			// there are files to parse, iterate over the list
			Iterator<String> iter = fnames.iterator();
			while (iter.hasNext()) {
				try {
					String fname = iter.next();
					System.out.println("Reading file \"" + fname + "\"");
					// create the input stream
					java.io.FileInputStream theFile = new java.io.FileInputStream(fname);
					java.io.InputStreamReader theStream;
					if (encoding != null) {
						theStream = new java.io.InputStreamReader(theFile,encoding);
					} else {
						theStream = new java.io.InputStreamReader(theFile);
					}
					// indicate the encoding scheme used on this platform
					System.out.println("Encoding = " + theStream.getEncoding());
					// create the parser instance and parse the file
					OvertureParser theParser = new OvertureParser(theStream);
					theParser.yydebug = pdebug;
					try {
						theParser.parseDocument();
						theParser.astDocument.setFilename(fname);
						if (pdebug == true) {
							System.out.println(theParser.astDocument.getLexems().size() + " lexems scanned");
							Iterator objs = theParser.astDocument.getLexems().iterator();
							int cnt = 1;
							while (objs.hasNext()) {
								IOmlLexem olex = (IOmlLexem) objs.next();
								System.out.print(cnt + " = OmlLexem(");
								System.out.print(olex.getLine().toString()+",");
								System.out.print(olex.getColumn().toString()+",");
								System.out.print(olex.getLexval().toString()+",\"");
								System.out.print(olex.getText()+"\",");
								switch (olex.getType().intValue()) {
								case 0:
									System.out.println("ILEXEMUNKNOWN)");
									break;
								case 1:
									System.out.println("ILEXEMKEYWORD)");
									break;
								case 2:
									System.out.println("ILEXEMIDENTIFIER)");
									break;
								case 3:
									System.out.println("ILEXEMLINECOMMENT)");
									break;
								case 4:
									System.out.println("ILEXEMBLOCKCOMMENT)");
									break;
								};
								cnt++;
							}
						}
					}
					catch (CGException cge) {
						cge.printStackTrace();
					}
					System.out.println(theParser.errors + " error(s) found");
					
					// post-process the file if required using the VdmSlVisitor
					if (theParser.errors == 0) {
						// the string to hold the pretty printed ast
						String res = null;

						// output the vdm-sl value
						if (sl_output) {
							try {
								VdmSlVisitor theVisitor = new VdmSlVisitor();
								theVisitor.visitDocument(theParser.astDocument);
								res = theVisitor.result;
							}
							catch (CGException cge) {
								cge.printStackTrace();
							}							
						}
						
						// output the VDM++ value
						if (pp_output) {
							try {
								VdmPpVisitor theVisitor = new VdmPpVisitor();
								theVisitor.visitDocument(theParser.astDocument);
								res = theVisitor.result;
							}
							catch (CGException cge) {
								cge.printStackTrace();
							}														
						}
						
						// should we print the abstract syntax?
						if (res != null) {
							if (foutput) {
								try {
									String ofname = fname;
									if (sl_output) {
										ofname += ".asl";
									} else {
										ofname += ".app";
									}
									java.io.FileOutputStream theOutputFile = new java.io.FileOutputStream(ofname);
									java.io.OutputStreamWriter theOutputStream;
									if (encoding != null) {
										theOutputStream = new java.io.OutputStreamWriter(theOutputFile,encoding);
									} else {
										theOutputStream = new java.io.OutputStreamWriter(theOutputFile);									
									}								
									theOutputStream.write(res + "\n");
									theOutputStream.close();
								}
								catch (java.io.IOException ioe) {
									ioe.printStackTrace();
								}
							} else {
								System.out.println(res);
							}
						}
					}
				}
				catch (java.io.UnsupportedEncodingException uee) {
					System.out.println(uee.getMessage());
				}
				catch (java.io.FileNotFoundException fnfe) {
					System.out.println(fnfe.getMessage());
				}
			}
		}
		System.out.println("End test parser");
	}

}
