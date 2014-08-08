package org.overture.core.tests.examples;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
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

	private static final int EXAMPLE_DEPTH = 3;

	private static final String README_FILE_NAME = "README";
	private static final String IO_LIB_NAME = "IO";
	private static final String VDMUNIT_LIB_NAME = "VDMUnit";
	private static final String CSV_LIB_NAME = "CSV";

	private static final String SL_LIBS_INDEX = "/examples/vdm-libs-sl.index";
	private static final String PP_LIBS_INDEX = "/examples/vdm-libs-pp.index";
	private static final String RT_LIBS_INDEX = "/examples/vdm-libs-rt.index";

	private static final String SL_EXAMPLES_INDEX = "/examples/vdm-examples-sl.index";
	private static final String PP_EXAMPLES_INDEX = "/examples/vdm-examples-pp.index";
	private static final String RT_EXAMPLES_INDEX = "/examples/vdm-examples-rt.index";

	/**
	 * Get the ASTs for the Overture examples. This method only provides the trees for examples that are supposed to
	 * successfully parse and TC.
	 * 
	 * @return a collection of {@link ExampleAstData}, each representing one example.
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	static public Collection<ExampleAstData> getExamplesAsts()
			throws ParserException, LexException, IOException,
			URISyntaxException
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
	 * @throws URISyntaxException
	 */
	public static Collection<ExampleSourceData> getExamplesSources()
			throws IOException, URISyntaxException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getExamples_(SL_EXAMPLES_INDEX, Dialect.VDM_SL));
		r.addAll(getExamples_(PP_EXAMPLES_INDEX, Dialect.VDM_PP));
		r.addAll(getExamples_(RT_EXAMPLES_INDEX, Dialect.VDM_RT));

		return r;
	}

	/**
	 * Get raw sources for the Overture VDM libraries.
	 * 
	 * @return a list of {@link ExampleSourceData} containing the libss sources
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Collection<ExampleSourceData> getLibSources()
			throws IOException, URISyntaxException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getLibs_(SL_LIBS_INDEX, Dialect.VDM_SL));
		r.addAll(getLibs_(PP_LIBS_INDEX, Dialect.VDM_PP));
		r.addAll(getLibs_(RT_LIBS_INDEX, Dialect.VDM_RT));

		return r;
	}

	private static Collection<ExampleSourceData> getLibs_(String index,
			Dialect dialect) throws IOException, URISyntaxException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();
		List<String> lines = parseIndex(index);

		List<File> lf;

		for (String line : lines)
		{
			File lib = getFileViaResource(line);

			lf = new Vector<File>();
			if (lib.getName().contains(CSV_LIB_NAME)
					|| lib.getName().contains(VDMUNIT_LIB_NAME))
			{
				lf.add(getLib(IO_LIB_NAME, dialect)); // csv and vdmunit need IO to TC

			}
			lf.add(lib);
			r.add(new ExampleSourceData(lib.getName(), dialect, Release.DEFAULT, lf));
		}

		return r;
	}

	private static Collection<ExampleSourceData> getExamples_(String index,
			Dialect dialect) throws IOException, URISyntaxException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();
		List<String> lines = parseIndex(index);
		List<File> sources = new Vector<File>();
		Collection<ExamplePacker> packedExamples = collectExamplePacks(lines, dialect);

		for (ExamplePacker p : packedExamples)
		{

			if (p.isCheckable())
			{
				{
					for (File f : p.getSpecFiles())
					{
						sources.add(f);
					}
					if (p.getLibs().size() > 0)
					{
						for (String lib : p.getLibs())
						{
							sources.add(getLib(lib, dialect));
						}
					}
					r.add(new ExampleSourceData(p.getName(), dialect, p.getLanguageVersion(), sources));
					sources = new Vector<File>();
				}
			}
		}
		return r;
	}

	private static Collection<ExamplePacker> collectExamplePacks(
			List<String> indices, Dialect dialect) throws URISyntaxException
	{
		List<ExamplePacker> packedExamples = new LinkedList<ExamplePacker>();

		ListIterator<String> it = indices.listIterator();
		String lastName = indices.get(0).split(File.separator)[EXAMPLE_DEPTH];
		List<File> sources = new Vector<File>();
		File readme = null;
		while (it.hasNext())
		{
			String line = it.next();
			String name = line.split(File.separator)[EXAMPLE_DEPTH];
			if (!name.equals(lastName))
			{
				assertNotNull("Could not find README file corresponding to example for "
						+ line, readme);
				packedExamples.add(new ExamplePacker(name, dialect, readme, sources));
				lastName = name;
				sources = new Vector<File>();
				readme = null;
			}

			if (line.contains(README_FILE_NAME))
			{
				readme = getFileViaResource(line);
			} else
			{
				sources.add(getFileViaResource(line));
			}

		}

		return packedExamples;
	}

	private static File getLib(String lib, Dialect dialect) throws IOException,
			URISyntaxException
	{
		String index = "";

		switch (dialect)
		{
			case VDM_SL:
				index = SL_LIBS_INDEX;
				break;
			case VDM_PP:
				index = PP_LIBS_INDEX;
				break;
			case VDM_RT:
				index = RT_LIBS_INDEX;
				break;
			default:
				fail("Unknown dialect " + dialect);
				break;
		}

		List<String> lines = parseIndex(index);
		ListIterator<String> it = lines.listIterator();

		while (it.hasNext())
		{
			String s = it.next();
			if (s.contains(lib))
			{
				return getFileViaResource(s);
			}
		}

		fail("Could not find lib " + lib);

		return null;
	}

	private static File getFileViaResource(String path)
	{
		URL url = ExamplesUtility.class.getResource(path);
		String fullPath = url.getFile();
		fullPath = fullPath.replace("%20", " "); // we can have spaces in file names so hack around it...
		File f = new File(fullPath);
		return f;
	}

	private static List<String> parseIndex(String path) throws IOException,
			URISyntaxException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getFileViaResource(path)));
		List<String> r = new LinkedList<String>();
		String line;
		while ((line = reader.readLine()) != null)
		{
			r.add(line);
		}
		reader.close();
		return r;
	}
}
