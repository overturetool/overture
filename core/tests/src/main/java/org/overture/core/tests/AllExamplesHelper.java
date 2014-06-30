package org.overture.core.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.tools.examplepackager.util.ExampleTestUtils;

/**
 * Special class to help handle testing of the Overture examples. This class
 * provides the examples but because these examples are Overture multi-file
 * projects, a special method to help parse and TC them is available.
 * 
 * @author ldc
 * 
 */
public class AllExamplesHelper {

	private static final String README_FILE = "/README.txt";
	private static final String NO_CHECK_REGEX = "\\#EXPECTED_RESULT\\=NO_CHECK";
	private static final String LANGUAGE_CLASSIC_REGEX = "\\#LANGUAGE_VERSION=classic";
	private static final String LANGUAGE_TEN_REGEX = "\\#LANGUAGE_VERSION=vdm10";

	public class ExampleAstData {

		String exampleName;
		List<INode> model;

		public ExampleAstData(String exampleName, List<INode> model) {
			this.exampleName = exampleName;
			this.model = model;
		}

		public String getExampleName() {
			return exampleName;
		}

		public List<INode> getModel() {
			return model;
		}

	}

	public Collection<ExampleAstData> getCorrectExampleAsts() {
		Collection<ExampleAstData> r = new LinkedList<AllExamplesHelper.ExampleAstData>();
		
		Collection<ExampleTestData> examples = ExampleTestUtils
				.getCorrectExamplesSources();

		for (ExampleTestData e : examples){
			r.add(parseExample(e));
		}
		
		return r;

	}

	private ExampleAstData parseExample(ExampleTestData e) throws ParserException, LexException {
		List<INode> ast = new LinkedList<INode>();
		switch (e.getDialect()) {
		case VDM_SL:
			ast = InputProcessor.typedAst(e.getSource(), Dialect.VDM_SL);
			break;

		default:
			break;
		}
		return new ExampleAstData(e.getName(), ast);

	}

}
