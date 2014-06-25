package org.overture.core.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Main parameterized test class for the new tests. Runs tests on VDM source
 * models and compares them with stored result files. This class is meant to be
 * subclassed as a way to quickly create your own tests suites.<br>
 * <br>
 * A comparison method for results must be provided. Serialization of results is
 * as automated as possible but can be overridden if your results classes are to
 * complex for auto. <br>
 * <br>
 * These tests are meant to be used as parameterized JUnit test and so any
 * subclass of it must be annotated with
 * <code>@RunWith(Parameterized.class)</code>. <br>
 * <br>
 * Many methods in this class are parameterized on a type <code>R</code> that
 * represents the output of of the analysis of the functionality under test.
 * These types must implement {@link IResult} and you will most likely need to
 * write some kind of translation between your outputs and <code>R</code>.
 * 
 * @author ldc
 */
public abstract class ParamTestAbstract {

	private String modelPath;
	private String resultPath;

	/**
	 * Constructor for the test. Should be initialized with parameters from
	 * {@link #testData()}.
	 * 
	 * @param testParameter
	 *            filename for the VDM source to test
	 * @param resultParameter
	 *            test result file
	 */
	public ParamTestAbstract(String _, String testParameter,
			String resultParameter) {
		this.modelPath = testParameter;
		this.resultPath = resultParameter;
	}

	/**
	 * Generate the test data. <b>Warning:</b>This method <b>must</b> be
	 * overridden as it returns <code>null</code>. Subclasses must override it
	 * to provide data specific to their tests. You should use
	 * {@link PathsProvider} for this. <br>
	 * <br>
	 * In order to use JUnit parameterized tests, you must annotate the method
	 * with <code>@Parameters(name = "{index} : {0}")</code>. Supporting
	 * parameterized tests is also the reason this method is static rather than
	 * abstract.
	 * 
	 * @return the test data as a collection of file path arrays.
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {
		return null;
	}

	/**
	 * The main test executor. Constructs ASTs and processes them via
	 * {@link #processModel(List)} and Results via
	 * {@link #deSerializeResult(String)}. It then compares the two according to
	 * {@link #testCompare(Object, IResult)}.
	 * 
	 * @param <R>
	 *            a result type produced by the analysed plug-in. You may need
	 *            to create one.
	 * 
	 * @throws IOException
	 * @throws LexException
	 * @throws ParserException
	 */
	@Test
	public <R extends IResult> void testCase() throws ParserException,
			LexException, IOException {
		List<INode> ast = InputProcessor.typedAst(modelPath);
		R actual = processModel(ast);
		R expected = deSerializeResult(resultPath);
		this.testCompare(actual, expected);
	}

	/**
	 * Compares output of the processed model with a previous. This method must
	 * be overridden to implement result comparison behavior. Don't forget to
	 * assert something.
	 * 
	 * @param actual
	 *            the processed model
	 * @param expected
	 *            the stored result
	 */
	public abstract <R extends IResult> void testCompare(R actual, R expected);

	/**
	 * Analyses a model (represented by its AST). This method must
	 * be overridden to perform whatever analysis the functionality under
	 * test performs.<br>
	 * <br>
	 * The output of this method must be of type <code>R</code> that implements
	 * {@link IResult}.
	 * @param ast representing the model to process
	 * @return the output of the analysis
	 */
	public abstract <R extends IResult> R processModel(List<INode> ast);

	/**
	 * This method tries its best to deserialize any results file. If your
	 * results are too complex for it to handle, you should override this method
	 * to deal with them.
	 * 
	 * @param resultPath
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public <R extends IResult> R deSerializeResult(String resultPath)
			throws FileNotFoundException, IOException {
		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(resultPath));
		Type resultType = new TypeToken<R>() {
		}.getType();
		R results = gson.fromJson(json, resultType);
		return results;
	}

}
