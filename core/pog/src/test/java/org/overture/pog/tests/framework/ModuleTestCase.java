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

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typecheck.ClassTypeChecker;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.overturetool.vdmj.util.Base64;

public class ModuleTestCase extends TestCase
{

	private static boolean isPermutationOf(String org, String perm)
	{
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
	ProofObligationList proofObligations = new ProofObligationList();

	public ModuleTestCase()
	{
		super("test");

	}

	private static String makePoString(ProofObligation po)
	{
		LexLocation loc = po.location;
		String poString = "|" + loc.startLine + ":" + po.location.startPos
				+ " " + po.name + "," + po.value + "," + po.kind + ","
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
		proofObligations.clear();
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
		if (file.getName().endsWith("vpp"))
			Settings.dialect = Dialect.VDM_PP;
		System.out.flush();
		System.err.flush();

		List<String> expectedProofObligations;
		expectedProofObligations = getExpectedProofObligations();

		TypeChecker tc = null;
		Runnable doPoG = null;
		switch (Settings.dialect)
		{
			case VDM_SL:
			{
				final List<AModuleModules> modules;
				try
				{
					modules = parse(file);
				} catch (Exception e)
				{
					isParseOk = false;
					break;
				}
				tc = new ModuleTypeChecker(modules);
				doPoG = new Runnable()
				{
					public void run()
					{
						for (AModuleModules aModule : modules)
						{
							proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
						}
					}
				};
			}
				break;
			case VDM_PP:
			{
				final List<SClassDefinition> classes;
				final POContextStack question = new POContextStack();
				try
				{
					classes = parse(file);
				} catch (Exception e)
				{
					isParseOk = false;
					break;
				}
				tc = new ClassTypeChecker(classes);
				doPoG = new Runnable()
				{

					public void run()
					{
						for (SClassDefinition cd : classes)
							proofObligations.addAll(cd.apply(new PogVisitor(), question));
					}
				};

			}
				break;
			default:
				throw new RuntimeException("Unknown dialect.");
		}

		if (isParseOk)
		{
			tc.typeCheck();
			boolean typeCheckOk = TypeChecker.getErrorCount() == 0;

			if (typeCheckOk)
			{
				doPoG.run();
			} else if (expectedProofObligations.size() > 0)
				fail(file.getName() + " failed because of the type checker.");

		}

		// read out the expected proof obligations from the test case header
		// (the file)

		int expPoSize = expectedProofObligations.size();
		int actPoSize = proofObligations.size();

		final class Pair<V, K, Z>
		{
			public V first;
			public K middle;
			public Z last;

			Pair(V v, K k, Z z)
			{
				this.first = v;
				this.middle = k;
				this.last = z;
			}
		}

		List<String> actualPos = new LinkedList<String>();
		for (ProofObligation po : proofObligations)
			actualPos.add(makePoString(po));

		List<Pair<String, String, Integer>> ratedStuff = new LinkedList<Pair<String, String, Integer>>();

		String more = "";
		List<String> notMatchedExpectedProofObligations = new LinkedList<String>();
		// find all the exact matches
		for (String poExp : expectedProofObligations)
		{
			String okayPo = null;

			for (String poAct : actualPos)
			{

				if (isPermutationOf(poExp, poAct))
				{
					okayPo = poAct;
					break;
				}
			}

			if (okayPo != null)
			{
				actualPos.remove(okayPo);
			} else
			{
				notMatchedExpectedProofObligations.add(poExp);
			}
		}

		// find the best match for all the expected pogs that wasn't matched exactly
		int count = 0;
		for (String poAct : actualPos)
		{
			int min = Integer.MAX_VALUE;

			String minExpPo = null;

			if (notMatchedExpectedProofObligations.isEmpty())
				break;

			for (String poExp : notMatchedExpectedProofObligations)
			{
				int rate = editDistance(poAct, poExp);
				if (rate < min)
				{
					minExpPo = poExp;
					min = rate;
				}

			}
			ratedStuff.add(new Pair<String, String, Integer>(poAct, minExpPo, min));
			notMatchedExpectedProofObligations.remove(minExpPo);

			if (++count > 9)
			{

				more = " And there are more...";
				break;
			}
		}

		actualPos = actualPos.subList(count, actualPos.size());

		System.out.println("Proof obligations expected: " + expPoSize
				+ " actual: " + actPoSize
				+ " of these actual proof obligations " + ratedStuff.size()
				+ " mismatched. " + more + "\n\n");

		// Report all the matched proof obligations
		if (ratedStuff.size() > 0)
		{
			System.out.println("Mismatched po's with best match: ");
			for (Pair<String, String, Integer> p : ratedStuff)
			{
				System.out.println("Expected proof obligation:");
				System.out.println("--------------------------");
				System.out.println(p.middle);
				System.out.println("Matched actual proof obligation (" + p.last
						+ "):");
				System.out.println("--------------------------");
				System.out.println(p.first);
				System.out.println();
			}
		}

		// Report all not matched proof obligations
		if (!notMatchedExpectedProofObligations.isEmpty())
		{
			System.out.println("These proof obligations were not matched at all: ");
			System.out.println("------------------------------------------------ ");
			int i = 0;
			for (String p : notMatchedExpectedProofObligations)
			{
				System.out.println("\n" + p + "\n");
				if (i++ > 10)
				{
					System.out.println("... And "
							+ (notMatchedExpectedProofObligations.size() - 10)
							+ " more...");
					break;
				}

			}
		}

		// Report all not matched proof obligations
		if (expPoSize < actPoSize)
		{
			System.out.println("These actual proof obligations were not expected: ");
			System.out.println("------------------------------------------------- ");
			int i = 0;
			for (String p : actualPos)
			{
				System.out.println("\n" + p + "\n");
				if (i++ > 10)
				{
					System.out.println("... And " + (actualPos.size() - 10)
							+ " more...");
					break;
				}

			}
		}

		if (ratedStuff.size() > 0 || expPoSize != actPoSize)
			throw new RuntimeException("Proof obligation mismatch - Expected: "
					+ expPoSize + " Actual: " + actPoSize + " Mismatching: "
					+ ratedStuff.size());

	}

	private <T> List<T> parse(File file) throws ParserException, LexException
	{
		// if (file != null)
		// {
		return internal(new LexTokenReader(file, Settings.dialect));
		// } else if (content != null)
		// {
		// internal(new LexTokenReader(content, Settings.dialect));
		// }
	}

	protected <T> List<T> internal(LexTokenReader ltr) throws ParserException,
			LexException
	{
		SyntaxReader reader = null;
		List<T> result = null;
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

	@SuppressWarnings("unchecked")
	private static <T> List<T> read(SyntaxReader reader)
	{
		if (reader instanceof ModuleReader)
			return (List<T>) ModuleReader.class.cast(reader).readModules();

		if (reader instanceof ClassReader)
			return (List<T>) ClassReader.class.cast(reader).readClasses();

		return null;
	}

	private static SyntaxReader getReader(LexTokenReader ltr)
	{
		if (ltr.dialect == Dialect.VDM_SL)
			return new ModuleReader(ltr);
		if (ltr.dialect == Dialect.VDM_PP)
			return new ClassReader(ltr);
		return null;
	}

	private static int editDistance(String n, String m)
	{
		int nl = n.length();
		int ml = m.length();
		char na[] = n.toCharArray();
		char ma[] = m.toCharArray();

		if (nl == 0)
			return ml;
		if (ml == 0)
			return nl;

		int[][] dp = new int[nl + 1][ml + 1];
		for (int i = 0; i < nl + 1; i++)
			dp[i][0] = i;

		for (int i = 0; i < ml + 1; i++)
			dp[0][i] = i;

		// favor matching the initial characters extremely as the line number and function really should be the same
		dp[0][0] = -200;

		for (int ni = 1; ni < nl + 1; ni++)
		{
			for (int mi = 1; mi < ml + 1; mi++)
			{
				// favor matching to replacing
				int cost = na[ni - 1] == ma[mi - 1] ? -4 : 2;
				int leftOf = dp[ni - 1][mi] + 2;
				int topOf = (dp[ni][mi - 1]) + 2;
				int diagOf = dp[ni - 1][mi - 1] + cost;
				dp[ni][mi] = Math.min(leftOf, Math.min(topOf, diagOf));
			}
		}

		return dp[nl][ml];
	}

}
