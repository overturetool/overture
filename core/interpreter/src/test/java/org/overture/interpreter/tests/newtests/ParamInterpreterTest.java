package org.overture.interpreter.tests.newtests;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.junit.Assert;
import org.overture.ast.node.INode;
import org.overture.config.Settings;
import org.overture.core.tests.ParamStandardTest;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ICollectedRuntimeExceptions;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.parser.messages.LocatedException;

import com.google.gson.reflect.TypeToken;

public abstract class ParamInterpreterTest extends
		ParamStandardTest<StringInterpreterResult> {

	File file;

	private static final String TEST_UPDATE_PROPERTY = "tests.update.interpreter.base";

	public ParamInterpreterTest(String nameParameter, String inputParameter,
			String resultParameter) {
		super(nameParameter, inputParameter, resultParameter);
		file = new File(modelPath);
	}

	@Override
	public StringInterpreterResult processModel(List<INode> ast) {
		String entry = "1+1";
		if (getEntryFile() == null || !getEntryFile().exists()) {
			entry = createEntryFile();
			if (entry == null) {
				if (getEntryFile() == null || !getEntryFile().exists()) {
					Assert.fail("No entry for model (" + getEntryFile() + ")");
				}
			}
		} else {
			try {
				entry = getEntries().get(0);
			} catch (IOException e) {
				fail("Could not process entry file for " + testName);
			}
		}
		try {
			Value val = InterpreterUtil.interpret(ast, entry, Settings.dialect);

			StringInterpreterResult result = new StringInterpreterResult(
					val.toString(), new Vector<Message>(),
					new Vector<Message>());

			return result;
		} catch (Exception e) {
			return wrap(e);
		}
	}

	private StringInterpreterResult wrap(Exception e) {

		Vector<Message> errors = new Vector<Message>();
		String message = e.getMessage();
		if (e instanceof ICollectedRuntimeExceptions) {
			List<String> messages = new ArrayList<String>();
			List<Exception> collectedExceptions = new ArrayList<Exception>(
					((ICollectedRuntimeExceptions) e).getCollectedExceptions());
			for (Exception err : collectedExceptions) {
				if (err instanceof ContextException) {
					ContextException ce = (ContextException) err;
					errors.add(new Message(ce.location.getFile().getName(),
							ce.number, ce.location.getStartLine(), ce.location
									.getStartPos(), ce.getMessage()));
				} else if (err instanceof ValueException) {
					ValueException ve = (ValueException) err;
					errors.add(new Message("?", ve.number, 0, 0, ve
							.getMessage()));
				} else if (err instanceof LocatedException) {
					LocatedException le = (LocatedException) err;
					errors.add(new Message(le.location.getFile().getName(),
							le.number, le.location.getStartLine(), le.location
									.getStartPos(), le.getMessage()));
				} else {
					messages.add(err.getMessage());
					err.printStackTrace();
				}
			}
			Collections.sort(messages);
			message = messages.toString();
		}
		return new StringInterpreterResult(message, new Vector<Message>(),
				errors);
	}

	private List<String> getEntries() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				getEntryFile()));
		List<String> data = new ArrayList<String>();
		String text = null;
		while ((text = reader.readLine()) != null) {
			data.add(text.trim());
		}
		reader.close();

		return data;
	}

	protected String createEntryFile() {
		try {
			String tmp = search(file.getParentFile(), file.getName());

			if (tmp != null && !tmp.isEmpty()) {
				file.getParentFile().mkdirs();
				FileWriter fstream = new FileWriter(getEntryFile());
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(tmp);
				out.close();
				return tmp;
			}
		} catch (IOException e) {
		}
		return null;

	}

	protected String search(File file, String name) throws IOException {
		File readme = new File(new File(file, name.substring(0,
				name.length() - 2)), "README.txt");
		if (readme.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(readme));

				String text = null;
				while ((text = reader.readLine()) != null) {
					text = text.trim();
					if (text.startsWith("#ENTRY_POINT")) {
						return text.substring(text.indexOf('=') + 1).trim();
					}
				}
			} finally {
				reader.close();
			}
		}
		return null;
	}

	protected File getEntryFile() {
		return new File(modelPath + ".entry");
	}

	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<StringInterpreterResult>() {
		}.getType();
		return resultType;

	}

	@Override
	protected String getUpdatePropertyString() {
		return TEST_UPDATE_PROPERTY;
	}

	@Override
	public void compareResults(StringInterpreterResult actual,
			StringInterpreterResult expected) {
		expected.assertEqualResults(actual.getResult());
	}

}
