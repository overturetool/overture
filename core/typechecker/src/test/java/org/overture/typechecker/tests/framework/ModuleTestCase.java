package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase.ParserType;
import org.overture.typechecker.tests.framework.TCStruct.Type;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;

public class ModuleTestCase extends TestCase {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings;
	private boolean generateResultOutput = true;
	private TCStructList tcHeaderList = null;
	private boolean isParseOk = true;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();
	
	
	public ModuleTestCase() {
		super("test");

	}

	public ModuleTestCase(File file) {
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
		this.tcHeaderList = new TCStructList();
	}

	@Override
	public String getName() {
		return this.content;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException {
		if (content != null) {
			moduleTc(content);
		}
	}

	private void moduleTc(String module) throws ParserException, LexException,
			IOException {
		System.out.flush();
		System.err.flush();

		parseFileHeader(file);
		List<AModuleModules> modules = null;
		try
		{
		modules = parse(file);
		}
		catch(ParserException e)
		{
			isParseOk = false;
		}
		catch(LexException e)
		{
			isParseOk = false;
		}
		
		if(isParseOk)
		{
			System.out.println("----------- Type checking starting for... " + file.getName() + " -----------");
			ModuleTypeChecker mtc = new ModuleTypeChecker(modules);
			mtc.typeCheck();
	
			String errorMessages = null;
			
			
			int tcV2found = tcHeaderList.size();
			int total = TypeChecker.getErrorCount() + TypeChecker.getWarningCount();
			String status = "Errors/Warnings: %s"   + 
					"\n by VDMJ: " + tcHeaderList.getErrorCount() + "/" + tcHeaderList.getWarningCount()
					 + "\n by TCv2: " + TypeChecker.getErrorCount() + "/" + TypeChecker.getWarningCount() ;
			
		
			if (mtc != null && TypeChecker.getErrorCount() > 0) {
	
				for (VDMError error : TypeChecker.getErrors()) {
					if(!tcHeaderList.markTCStruct(error))
					{
						errors.add(error);
					}
				}
			}
	
			if (mtc != null && TypeChecker.getWarningCount() > 0) {
				for (VDMWarning warning : TypeChecker.getWarnings()) {
					if(!tcHeaderList.markTCStruct(warning))
					{
						warnings.add(warning);
					}
				}				
			}
			
			
			if( !(tcHeaderList.size() == 0 && total == tcV2found)  )
			{
				System.out.println(status.format(status, "WRONG"));
				for (VDMError error : errors) {
					System.out.println(error.toString());
				}

				for (VDMWarning warning : warnings) {
					System.out.println(warning.toString());
				}
//				if (mtc != null && TypeChecker.getErrorCount() > 0) {
//					StringWriter s = new StringWriter();
//					TypeChecker.printErrors(new PrintWriter(s));// new
//																// PrintWriter(System.out));
//					errorMessages = "\n" + s.toString() + "\n";			
//					System.out.println(s.toString());
//				}
//		
//				if (mtc != null && TypeChecker.getWarningCount() > 0) {
//					StringWriter s = new StringWriter();
//					TypeChecker.printWarnings(new PrintWriter(s));// new
//					System.out.println(s.toString());
//				}
			
				System.out.println("Missing errors/warnings:");
				System.out.println(tcHeaderList.toString());
			}
			else{
				System.out.println(status.format(status, "OK"));	
			}
			
//			System.out.println("----------- Type checking ended for... " + file.getName() + " -----------");
			assertTrue("TEST FAILED: difference in errors: " +  Math.abs(total- tcV2found) , tcHeaderList.size() == 0 && total == tcV2found);
		}
	
	}

	

	private List<AModuleModules> parse(File file) throws ParserException,
			LexException {
		// if (file != null)
		// {
		return internal(new LexTokenReader(file, Settings.dialect));
		// } else if (content != null)
		// {
		// internal(new LexTokenReader(content, Settings.dialect));
		// }
	}

	protected List<AModuleModules> internal(LexTokenReader ltr)
			throws ParserException, LexException {
		ModuleReader reader = null;
		List<AModuleModules> result = null;
		String errorMessages = "";
		try {
			reader = getReader(ltr);
			result = read(reader);

			if (reader != null && reader.getErrorCount() > 0) {
				// perrs += reader.getErrorCount();
				StringWriter s = new StringWriter();
				reader.printErrors(new PrintWriter(s));// new
														// PrintWriter(System.out));
				errorMessages = "\n" + s.toString() + "\n";
				System.out.println(s.toString());
			}
			assertEquals(errorMessages, 0, reader.getErrorCount());

			if (reader != null && reader.getWarningCount() > 0) {
				// pwarn += reader.getWarningCount();
				// reader.printWarnings(new PrintWriter(System.out));
			}

			return result;
		} finally {
			// if (!hasRunBefore())
			// {
			// setHasRunBefore(true);
			// System.out.println("============================================================================================================");
			//
			// System.out.println("|");
			// System.out.println("|\t\t" + getReaderTypeName() + "s");
			// // System.out.println("|");
			// System.out.println("|___________________________________________________________________________________________________________");
			//
			// }
			// System.out.println(pad("Parsed " + getReaderTypeName(), 20) +
			// " - "
			// + pad(getReturnName(result), 35) + ": "
			// + pad(result + "", 35).replace('\n', ' ') + " from \""
			// + (content + "").replace('\n', ' ') + "\"");
			// System.out.flush();
		}
	}

	private List<AModuleModules> read(ModuleReader reader) {
		return reader.readModules();
	}

	private ModuleReader getReader(LexTokenReader ltr) {
		return new ModuleReader(ltr);
	}

	private void parseFileHeader(File file) throws IOException {

		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);

		String line = null;
		boolean more = true;

		while (more) {
			line = br.readLine();
			if (line.startsWith(tcHeader)) {
				line = line.substring(tcHeader.length()).trim();
				if(line.equals(""))
				{
					more = false;
					break;
				}
				String[] errors = line.split(" ");
				for (String error : errors) {
					String[] parsedError = error.split(":");
					String[] parsedLocation = parsedError[2].split(",");
					
					tcHeaderList.add(new TCStruct(
							Type.valueOf(parsedError[0]),
							Integer.parseInt(parsedError[1]), 
							Integer.parseInt(parsedLocation[0]), 
							Integer.parseInt(parsedLocation[1])));
				}
			} else {
				more = false;
			}
		}

	}
}
