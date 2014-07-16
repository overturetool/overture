package org.overture.core.tests.examples;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.overture.ast.lex.Dialect;
import org.overture.core.tests.ParseTcFacade;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Utilities to help handle testing of Overture examples. The examples can either be provided as raw sources or as typed
 * ASTs.<br>
 * <br>
 * This class only has static methods.
 * 
 * @author ldc
 */
abstract public class ExamplesUtility
{

	private static final String SL_EXAMPLES_ROOT = "../../../documentation/examples/VDMSL";
	private static final String PP_EXAMPLES_ROOT = "../../../documentation/examples/VDM++";
	private static final String RT_EXAMPLES_ROOT = "../../../documentation/examples/VDMRT";
	private static final String LIBS_ROOT = "../../../documentation/examples/libs/";

	/**
	 * Get the ASTs for the Overture examples. This method only provides the trees for examples that are supposed to
	 * successfully parse and TC.
	 * 
	 * @return a collection of {@link ExampleAstData}, each representing one example.
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 */
	static public Collection<ExampleAstData> getExamplesAsts()
			throws ParserException, LexException, IOException
	{
		Collection<ExampleAstData> r = new Vector<ExampleAstData>();

		Collection<ExampleSourceData> examples = getExamplesSources();

		for (ExampleSourceData e : examples)
		{
			r.add(ParseTcFacade.parseTcExample(e));
		}

		return r;
	}

	/**
	 * Get raw sources for all the Overture public examples. Currently, only examples that are expected to parse and TC
	 * are returned. In other words, examples tagged as having intentional errors are not returned.
	 * 
	 * @return a list of {@link ExampleSourceData} containing the example sources
	 * @throws IOException
	 */
	public static Collection<ExampleSourceData> getExamplesSources()
			throws IOException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getSubSources(SL_EXAMPLES_ROOT, Dialect.VDM_SL));
		r.addAll(getSubSources(PP_EXAMPLES_ROOT, Dialect.VDM_PP));
		r.addAll(getSubSources(RT_EXAMPLES_ROOT, Dialect.VDM_RT));

		return r;
	}

	private static Collection<ExampleSourceData> getSubSources(
			String examplesRoot, Dialect dialect) throws IOException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		File dir = new File(examplesRoot);

		StringBuilder source = new StringBuilder();
		// grab examples groups
		for (File f : dir.listFiles())
		{
			// grab example projects
			if (f.isDirectory())
			{
				ExamplePacker p = new ExamplePacker(f, dialect);
				if (p.isCheckable())
				{
					for (File f2 : org.apache.commons.io.FileUtils.listFiles(dir, new RegexFileFilter(dialect.getFilter().toString()), DirectoryFileFilter.DIRECTORY))
					{
						source.append(FileUtils.readFileToString(f2));
						source.append("\n\n");
					}
					if (p.getLibs().size() > 0)
					{
						for (String lib : p.getLibs())
						{
							try
							{
								source.append(FileUtils.readFileToString(new File(LIBS_ROOT
										+ ExamplePacker.getName(dialect)
										+ "/"
										+ lib)));
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					r.add(new ExampleSourceData(p.getName(), dialect, p.getLanguageVersion(), source.toString()));
					source = new StringBuilder();
				}
			}
		}

		return r;

	}

}
