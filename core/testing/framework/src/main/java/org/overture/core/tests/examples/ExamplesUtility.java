package org.overture.core.tests.examples;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
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

	private static final String SL_EXAMPLES_ROOT = "/examples/VDMSL";
	private static final String PP_EXAMPLES_ROOT = "/examples/VDM++";
	private static final String RT_EXAMPLES_ROOT = "/examples/VDMRT";
	private static final String LIBS_ROOT = "/examples/libs/";

	private static final String SL_LIBS_ROOT = "/examples/libs/SL/";
	private static final String PP_LIBS_ROOT = "/examples/libs/PP/";
	private static final String RT_LIBS_ROOT = "/examples/libs/RT/";

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

		r.addAll(getSubSources(getPath(SL_EXAMPLES_ROOT), Dialect.VDM_SL));
		r.addAll(getSubSources(getPath(PP_EXAMPLES_ROOT), Dialect.VDM_PP));
		r.addAll(getSubSources(getPath(RT_EXAMPLES_ROOT), Dialect.VDM_RT));

		return r;
	}
	
	public static String getDocumentationPath()
	{
		return "../../../documentation/";
	}
	
	public static String getPath(String relativeExamplePath)
	{
		return (getDocumentationPath()+relativeExamplePath).replace('/', File.separatorChar);
	}

	/**
	 * Get raw sources for the Overture VDM libraries.
	 * 
	 * @return a list of {@link ExampleSourceData} containing the libss sources
	 * @throws IOException
	 */
	public static Collection<ExampleSourceData> getLibSources()
			throws IOException
	{
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getSubLibs(getPath(SL_LIBS_ROOT), Dialect.VDM_SL));
		r.addAll(getSubLibs(getPath(PP_LIBS_ROOT), Dialect.VDM_PP));
		r.addAll(getSubLibs(getPath(RT_LIBS_ROOT), Dialect.VDM_RT));

		return r;
	}

	private static Collection<ExampleSourceData> getSubLibs(String libsroot,
			Dialect dialect) throws IOException
	{
		URL url = ExamplesUtility.class.getResource(libsroot);
		File dir = new File(url.getPath());
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		List<File> sb = new Vector<File>();
		String csvName = "";

		for (File f : dir.listFiles())
		{
			if (f.getName().contains("CSV")) // csv needs IO to TC
			{
//				sb.append(FileUtils.readFileToString(f));
//				sb.append("\n");
//				csvName = f.getName();
				sb.add(f);
			} else
			{
				
				r.add(new ExampleSourceData(f.getName(), dialect, Release.DEFAULT, f));

				if (f.getName().contains("IO"))
				{
//					sb.append(FileUtils.readFileToString(f));
//					sb.append("\n");
					sb.add(f);
				}
			}
		}

		r.add(new ExampleSourceData(csvName, dialect, Release.DEFAULT, sb));
		return r;
	}

	private static Collection<ExampleSourceData> getSubSources(
			String examplesRoot,final  Dialect dialect) throws IOException
	{

		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		URL url = ExamplesUtility.class.getResource(examplesRoot);
		File dir = new File(examplesRoot.replace('/', File.separatorChar));

		List<File> sources =null;
		// grab examples groups
		for (File f : dir.listFiles())
		{
			sources= new Vector<File>();
			// grab example projects
			if (f.isDirectory())
			{
				ExamplePacker p = new ExamplePacker(f, dialect);
				if (p.isCheckable())
				{
					sources.addAll(p.getSpecFiles());
					
					if (p.getLibs().size() > 0)
					{
						for (String lib : p.getLibs())
						{
							final File file = new File(getPath(LIBS_ROOT+"/"+ ExamplePacker.getName(dialect)+ "/"+ lib));
//								source.append(FileUtils.readFileToString(file));
							sources.add(file);
						}
					}
					r.add(new ExampleSourceData(p.getName(), dialect, p.getLanguageVersion(), sources));
//					source = new StringBuilder();
				}
			}
		}

		return r;

	}

}
