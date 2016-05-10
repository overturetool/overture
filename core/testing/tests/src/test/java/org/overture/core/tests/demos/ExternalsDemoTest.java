package org.overture.core.tests.demos;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Settings;
import org.overture.core.tests.ParamExternalsTest;
import org.overture.core.tests.ParseTcFacade;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import com.google.gson.reflect.TypeToken;

/**
 * A simple test to demo the use of external test inputs. This class reuses
 * {@link IdTestResult} but rather than printing the entire test model, it just
 * prints a success/failure message regarding the type checking of the test
 * source. <br>
 * Also note that since this test works with external inputs, the data provider
 * is already set up in {@link ParamExternalsTest}. To launch these tests simply
 * use the property <code>-DexternalTestsPath=/path/to/files/</code>. This test
 * is intended to run on the tc external tests only.<br>
 * <br>
 * <b>Note:</b> Due to some quirks with Parameterized JUnit tests, if the
 * property is not set, the test will still launch, only with 0 cases. It's fine
 * in Maven but in Eclipse you will get a single test run that does nothing.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class ExternalsDemoTest extends ParamExternalsTest<IdTestResult> {

	// the update property for this test
	private static final String UPDATE_PROPERTY = "tests.update.example.ExternalsDemo";

	/**
	 * As usual in the new tests, the constructor only needs to pass the
	 * parameters up to super.
	 * 
	 * @param nameParameter
	 * @param testParameter
	 * @param resultParameter
	 */
	public ExternalsDemoTest(String nameParameter, String testParameter,
			String resultParameter) {
		super(nameParameter, testParameter, resultParameter);
	}

	/**
	 * Main comparison method. Simply call on {@link IdTestResult}.
	 */
	@Override
	public void compareResults(IdTestResult actual, IdTestResult expected) {
		IdTestResult.compare(actual, expected, testName);
	}

	/**
	 * Return the update property for this test. In general, it's good practice
	 * to do put it in a constant and return that.
	 */
	@Override
	protected String getUpdatePropertyString() {
		return UPDATE_PROPERTY;
	}

	/**
	 * Return the {@link Type} or reulst for this test. This is needed to help
	 * out with reflection in the deserialization of results.
	 */
	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<IdTestResult>() {
		}.getType();
		return resultType;
	}

	/**
	 * Process the VDM source. External inputs can be negative so we control the
	 * parsing and type checking ourselves. This makes the method much longer
	 * and more complex. If we were certain the sources were correct, we could
	 * just call a method from {@link ParseTcFacade}.<br>
	 * <br>
	 * Remember that you must always track the VDM dialect of the source and the
	 * only way to do this with external inputs is by placing them in dialect
	 * folders as we have done here. <br>
	 * <br>
	 * Because of this extra size and complexity it's a good idea to split up
	 * the method according to dialect and do some dispatching.
	 * 
	 * @return an {@link IdTestResult} with a list of error messages or an all
	 *         clear message
	 */
	@Override
	public IdTestResult processSource() {
		// Use file names only in error messages
		LexLocation.absoluteToStringLocation = false;

		IdTestResult r = new IdTestResult();
		if (modelPath.contains("sltest")) {
			return processSl();
		}
		if (modelPath.contains("rttest")) {
			try {
				return processRt();
			} catch (ParserException e) {
				r.add("Error performing RT parsing in " + testName);
			} catch (LexException e) {
				r.add("Error performing RT lexing in " + testName);
			}
		}
		if (modelPath.contains("pptest")) {
			return processPp();
		}

		r = new IdTestResult();
		r.add("Could not test " + testName
				+ " unable to locate model in path: " + modelPath);
		return r;
	}

	private IdTestResult processPp() {
		Settings.dialect = Dialect.VDM_PP;
		IdTestResult r = new IdTestResult();

		ParserResult<List<SClassDefinition>> pr = ParserUtil.parseOo(new File(
				modelPath));
		if (pr.errors.isEmpty()) {
			TypeCheckResult<List<SClassDefinition>> tr = TypeCheckerUtil
					.typeCheck(pr, pr.result, new ClassTypeChecker(pr.result));
			if (tr.errors.isEmpty()) {
				r.add(testName + " parses and type checks");
			} else {
				for (VDMError e : tr.errors) {
					r.add(makeErrorMsg(e));
				}
			}
		} else {
			for (VDMError e : pr.errors) {
				r.add(makeErrorMsg(e));
			}
		}
		return r;
	}

	private IdTestResult processRt() throws ParserException, LexException {
		Settings.dialect = Dialect.VDM_RT;
		IdTestResult r = new IdTestResult();

		ParserResult<List<SClassDefinition>> pr = ParserUtil.parseOo(new File(
				modelPath));
		if (pr.errors.isEmpty()) {
			ITypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();
			List<SClassDefinition> classes = new ArrayList<SClassDefinition>();
			classes.addAll(pr.result);
			classes.add(AstFactoryTC.newACpuClassDefinition(af));
			classes.add(AstFactoryTC.newABusClassDefinition(af));
			TypeCheckResult<List<SClassDefinition>> tr = TypeCheckerUtil
					.typeCheck(pr, pr.result, new ClassTypeChecker(pr.result));
			if (tr.errors.isEmpty()) {
				r.add(testName + "parses and type checks");
			} else {
				for (VDMError e : tr.errors) {
					r.add(makeErrorMsg(e));
				}
			}
		} else {
			for (VDMError e : pr.errors) {
				r.add(makeErrorMsg(e));
			}
		}
		return r;
	}

	private IdTestResult processSl() {
		Settings.dialect = Dialect.VDM_SL;
		IdTestResult r = new IdTestResult();

		File modelFile = new File(modelPath);

		ParserResult<List<AModuleModules>> pr = ParserUtil.parseSl(modelFile);
		if (pr.errors.isEmpty()) {
			TypeCheckResult<List<AModuleModules>> tr = TypeCheckerUtil
					.typeCheckSl(modelFile);
			if (tr.errors.isEmpty()) {
				r.add(testName + "parses and type checks");
			} else {
				for (VDMError e : tr.errors) {
					r.add(makeErrorMsg(e));
				}
			}
		} else {
			for (VDMError e : pr.errors) {
				r.add(makeErrorMsg(e));
			}
		}
		return r;
	}

	private String makeErrorMsg(VDMError e) {
		StringBuilder sb = new StringBuilder();

		sb.append("Error ");
		sb.append(e.number);
		sb.append(": ");
		sb.append(e.location.getFile().getName());
		sb.append(" at ");
		sb.append(e.location.getStartLine());
		sb.append(":");
		sb.append(e.location.getStartPos());
		sb.append(" ");
		sb.append(e.message.toString());

		return sb.toString();
	}
}
