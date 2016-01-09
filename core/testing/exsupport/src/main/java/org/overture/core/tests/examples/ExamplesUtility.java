/*
 * #%~
 * Overture Testing Framework
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.core.tests.examples;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.ParseTcFacade;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Utilities to help handle testing of Overture examples. The examples can
 * either be provided as raw sources or as typed ASTs.<br>
 * <br>
 * This class only has static methods.
 * 
 * @author ldc
 */
abstract public class ExamplesUtility {

	private static final String SL_EXAMPLES_ROOT = "VDMSL";
	private static final String PP_EXAMPLES_ROOT = "VDM++";
	private static final String RT_EXAMPLES_ROOT = "VDMRT";
	private static final String LIBS_ROOT = "libs/";

	private static final String SL_LIBS_ROOT = "SL/";
	private static final String PP_LIBS_ROOT = "PP/";
	private static final String RT_LIBS_ROOT = "RT/";

	/**
	 * Get the ASTs for the Overture examples. This method only provides the
	 * trees for examples that are supposed to successfully parse and TC.
	 * 
	 * @param examplesRoot
	 *            the path to the root folder of the examples
	 * 
	 * @return a collection of {@link ExampleAstData}, each representing one
	 *         example.
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	static public Collection<ExampleSourceData> getExamplesAsts(
			String examplesRoot) throws ParserException, LexException,
			IOException, URISyntaxException {

		Collection<ExampleSourceData> examples = getExamplesSources(examplesRoot);

		return examples;
	}

	/**
	 * Parse and type check an Overture example.
	 * 
	 * @param e
	 *            The {@link ExampleSourceData} of the example to process
	 * @return the {@link ExampleAstData} of the submitted example
	 * @throws ParserException
	 * @throws LexException
	 */
	public static ExampleAstData parseTcExample(ExampleSourceData e)
			throws ParserException, LexException {
		List<INode> ast = new LinkedList<INode>();
		Settings.release = e.getRelease();

		ast = ParseTcFacade.typedAstNoRetry(e.getSource(), e.getName(),
				e.getDialect());

		return new ExampleAstData(e.getName(), ast);
	}

	/**
	 * Get raw sources for all the Overture public examples. Currently, only
	 * examples that are expected to parse and TC are returned. In other words,
	 * examples tagged as having intentional errors are not returned.
	 * 
	 * @param examplesRoot
	 *            the path to the root folder of the examples
	 * 
	 * @return a list of {@link ExampleSourceData} containing the example
	 *         sources
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Collection<ExampleSourceData> getExamplesSources(
			String examplesRoot) throws IOException, URISyntaxException {
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getExamples_(examplesRoot, SL_EXAMPLES_ROOT, Dialect.VDM_SL));
		r.addAll(getExamples_(examplesRoot, PP_EXAMPLES_ROOT, Dialect.VDM_PP));
		r.addAll(getExamples_(examplesRoot, RT_EXAMPLES_ROOT, Dialect.VDM_RT));

		return r;
	}

	/**
	 * Get raw sources for the Overture VDM libraries.
	 * 
	 * @param libsRoot
	 *            the path to the root folder of the examples
	 * 
	 * @return a list of {@link ExampleSourceData} containing the libss sources
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Collection<ExampleSourceData> getLibSources(String libsRoot)
			throws IOException, URISyntaxException {
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		r.addAll(getLibs_(libsRoot + SL_LIBS_ROOT, Dialect.VDM_SL));
		r.addAll(getLibs_(libsRoot + PP_LIBS_ROOT, Dialect.VDM_PP));
		r.addAll(getLibs_(libsRoot + RT_LIBS_ROOT, Dialect.VDM_RT));

		return r;
	}

	private static Collection<ExampleSourceData> getLibs_(String libsroot,
			Dialect dialect) throws IOException, URISyntaxException {

		File dir = new File(libsroot);
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		List<File> libs = new LinkedList<File>();

		for (File f : dir.listFiles()) {
			libs.add(f);
		}
		r.add(new ExampleSourceData(dialect.toString(), dialect,
				Release.DEFAULT, libs));
		return r;
	}

	private static Collection<ExampleSourceData> getExamples_(
			String externalsRoot, String examplesRoot, Dialect dialect)
			throws IOException, URISyntaxException {
		List<ExampleSourceData> r = new LinkedList<ExampleSourceData>();

		File dir = new File(externalsRoot + examplesRoot);
		dir.mkdirs();

		List<File> source = new LinkedList<File>();
		// grab examples groups
		for (File f : dir.listFiles()) {
			// grab example projects
			if (f.isDirectory()) {
				ExamplePacker p = new ExamplePacker(f, dialect);
				if (p.isCheckable()) {
					Collection<File> files = FileUtils.listFiles(f,
							new RegexFileFilter(getFilterString(dialect)),
							DirectoryFileFilter.DIRECTORY);
					source.addAll(files);

					if (p.getLibs().size() > 0) {
						for (String lib : p.getLibs()) {
							source.add(new File(externalsRoot + LIBS_ROOT
									+ ExamplePacker.getName(dialect) + "/"
									+ lib));

						}
					}
					r.add(new ExampleSourceData(p.getName(), dialect, p
							.getLanguageVersion(), source));
					source = new LinkedList<File>();
				}
			}
		}

		return r;
	}

	private static String getFilterString(Dialect dialect) throws IOException {
		switch (dialect) {
		case VDM_SL:
			return ".*\\.vdm(sl)?";
		case VDM_PP:
			return ".*\\.v(dm)?pp";
		case VDM_RT:
			return ".*\\.vdmrt";
		default:
			throw new IOException("Unrecognized dialect: " + dialect.toString());
		}
	}
}
