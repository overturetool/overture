package org.overture.pog.tests.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.overture.ast.modules.AModuleModules;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.util.Base64;

public class ModuleTestCase extends TestCase
{

	private static boolean isPermutationOf(String org, String perm)
	{
		boolean result = true;
		if (org.length() != perm.length())
			return false;
		for (char c : org.toCharArray())
			if (perm.indexOf(c) == -1)
				return false;
		return true;
	}

	public enum ParserType
	{
		Expression, Expressions, Module, Class, Pattern, Type, Statement, Bind
	}

	public static final String tcHeader = "-- TCErrors:";
	public static final Boolean printOks = false;

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings;
	private boolean generateResultOutput = true;
	private boolean isParseOk = true;
	List<VDMError> errors = new Vector<VDMError>();
	List<VDMWarning> warnings = new Vector<VDMWarning>();
	ProofObligationList proofObligation = new ProofObligationList();

	public ModuleTestCase()
	{
		super("test");

	}

	private static String makePoString(ProofObligation po)
	{
		String poString = "|" + po.name + "," + po.value + "," + po.kind + ","
				+ po.proof + "," + po.status + "|";
		return poString;
	}

	private String base64Decode(String s)
	{
		try
		{
			return new String(Base64.decode(s));
		} catch (Exception e)
		{
			// in our case it is a runtime exception if the encoding fails we
			// expect it to be correct at all times as it is auto-generated.
			throw new RuntimeException(e);
		}
	}

	private List<String> getExpectedProofObligations() throws IOException
	{
		List<String> result = new LinkedList<String>();
		String line = null;
		String header = null;
		String proofObligationSection = "PROOFOBLIGATION:";
		// read the header line
		try
		{
			FileReader f = new FileReader(file);
			BufferedReader input = new BufferedReader(f);
			while ((line = input.readLine()) != null)
				if (line.startsWith(tcHeader))
				{
					header = line;
					break;
				}
			f.close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		// collect expected proof obligations for the header line
		if (header != null)
		{
			String[] sections = header.split(" ");

			// for each space separated part of the header line
			for (int j = 0; j < sections.length; j++)
			{
				String s = sections[j];

				// if it start with PROOFOBLIGATION: then the following section
				// will be the base64 encoded proof obligation string
				// representation
				// as created by makePoString.
				if (s.trim().startsWith(proofObligationSection))
				{
					if (sections.length > j + 1)
					{
						// add the decoded string
						result.add(base64Decode(sections[j + 1]));
					} else
						throw new RuntimeException("Found "
								+ proofObligationSection
								+ " followed by nothing. That is wrong.");
				}
			}
		}
		return result;
	}

	public ModuleTestCase(File file)
	{
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
		proofObligation.clear();
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content != null)
		{
			moduleTc(content);
		}
	}

	private void moduleTc(String module) throws ParserException, LexException,
			IOException
	{
		System.out.flush();
		System.err.flush();

		List<AModuleModules> modules = null;
		try
		{
			modules = parse(file);
		} catch (ParserException e)
		{
			isParseOk = false;
		} catch (LexException e)
		{
			isParseOk = false;
		}

		if (isParseOk)
		{

			ModuleTypeChecker mtc = new ModuleTypeChecker(modules);
			mtc.typeCheck();

			if (TypeChecker.getErrorCount() == 0)
			{
				for (AModuleModules aModule : modules)
				{
					proofObligation.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
				}
			}

		}

		// read out the expected proof obligations from the test case header
		// (the file)
		List<String> expectedProofObligations;
		expectedProofObligations = getExpectedProofObligations();

		// fail if expected and actual number of po's are different
		if (expectedProofObligations.size() != proofObligation.size())
		{
			System.out.println("Different number of ProofObligations: (Expected: "
					+ expectedProofObligations.size()
					+ ", Actual: "
					+ proofObligation.size() + ")\n---- Expected ----\n");
			for (String s : expectedProofObligations)
				System.out.println(s + "\n");
			System.out.println("\n------ Actual -----\n");
			for (ProofObligation po : proofObligation)
				System.out.println(makePoString(po) + "\n");

			throw new RuntimeException("The number of generated proof obligations are different from the actually encountered proof obligations. Expected: "
					+ expectedProofObligations.size()
					+ ", Actual: "
					+ proofObligation.size());
		}
		// for each po found by our PoGVisitor check that it is among the
		// expected po's
		for (ProofObligation po : proofObligation)
		{

			String poString = makePoString(po).trim();
			boolean poFound = false;
			for (String s : expectedProofObligations)
			{
				if (isPermutationOf(s, poString))
				{
					poFound = true;
					break;
				}
			}

			if (!poFound)
			{
				System.out.println(po.toString());
				throw new RuntimeException("Proof obligation from AST_v2: \n"
						+ poString
						+ "\nis not in the expected list VDMJ produces:\n"
						+ expectedProofObligations);
			}
		}

	}

	private List<AModuleModules> parse(File file) throws ParserException,
			LexException
	{
		// if (file != null)
		// {
		return internal(new LexTokenReader(file, Settings.dialect));
		// } else if (content != null)
		// {
		// internal(new LexTokenReader(content, Settings.dialect));
		// }
	}

	protected List<AModuleModules> internal(LexTokenReader ltr)
			throws ParserException, LexException
	{
		ModuleReader reader = null;
		List<AModuleModules> result = null;
		String errorMessages = "";
		try
		{
			reader = getReader(ltr);
			result = read(reader);

			if (reader != null && reader.getErrorCount() > 0)
			{
				// perrs += reader.getErrorCount();
				StringWriter s = new StringWriter();
				reader.printErrors(new PrintWriter(s));// new
														// PrintWriter(System.out));
				errorMessages = "\n" + s.toString() + "\n";
				System.out.println(s.toString());
			}
			assertEquals(errorMessages, 0, reader.getErrorCount());

			if (reader != null && reader.getWarningCount() > 0)
			{
				// pwarn += reader.getWarningCount();
				// reader.printWarnings(new PrintWriter(System.out));
			}

			return result;
		} finally
		{
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

	private List<AModuleModules> read(ModuleReader reader)
	{
		return reader.readModules();
	}

	private ModuleReader getReader(LexTokenReader ltr)
	{
		return new ModuleReader(ltr);
	}

}
