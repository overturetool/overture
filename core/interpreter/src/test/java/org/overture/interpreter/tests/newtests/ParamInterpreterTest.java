package org.overture.interpreter.tests.newtests;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.overture.ast.node.INode;
import org.overture.config.Settings;
import org.overture.core.tests.ParamStandardTest;
import org.overture.interpreter.tests.utils.ExecutionToResultTranslator;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

			Result<String> auxResult = new Result<String>(val.toString(),
					new Vector<IMessage>(), new Vector<IMessage>());
			System.out.println(file.getName() + " -> " + val);
			return StringInterpreterResult.convert(auxResult);
		} catch (Exception e) {
			Result<String> auxResult = ExecutionToResultTranslator.wrap(e);
			return StringInterpreterResult.convert(auxResult);
		}
	}

	private List<String> getEntries() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				getEntryFile()));
		List<String> data = new Vector<String>();
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
	public StringInterpreterResult deSerializeResult(String resultPath)
			throws FileNotFoundException, IOException {
		// try to deserialize JSON file under new framework
		// try .result and .RESULT paths since the 2 frameworks use different
		// naming schemes
		File resultFile = new File(resultPath);
		StringInterpreterResult r = null;
		if (resultFile.exists()) {
			r = super.deSerializeResult(resultPath);
			if (r != null) {
				return r;
			}
		}

		resultFile = new File(modelPath + ".result");

		if (!resultFile.exists()) {
			throw new FileNotFoundException(resultFile.getPath());
		}

		// try XML deserialization inherited form the old one
		// File resultFile = new File(file.getAbsoluteFile()+ ".result");
		List<IMessage> warnings = new Vector<IMessage>();
		List<IMessage> errors = new Vector<IMessage>();
		String readResult = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();

			Document doc = db.parse(resultFile);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("message");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node node = nodeLst.item(i);
				String nodeType = node.getAttributes()
						.getNamedItem("messageType").getNodeValue();
				if (nodeType.equals("error")) {
					convertNodeToMessage(errors, node);
				} else if (nodeType.equals("warning")) {
					convertNodeToMessage(warnings, node);
				}
			}

			for (int i = 0; i < doc.getDocumentElement().getChildNodes()
					.getLength(); i++) {
				Node node = doc.getDocumentElement().getChildNodes().item(i);
				if (node.getNodeName() != null
						&& node.getNodeName().equals("result")) {
					node.normalize();

					readResult = StringInterpreterResult.decodeResult(node);
				}
				// System.out.println(node);
			}
			return StringInterpreterResult.convert(new Result<String>(
					readResult, warnings, errors));
		} catch (Exception e) {
			return null;
		}
	}

	private void convertNodeToMessage(List<IMessage> set, Node node) {

		NamedNodeMap nnm = node.getAttributes();
		String resource = nnm.getNamedItem("resource").getNodeValue();
		IMessage m = new Message(resource, Integer.parseInt(nnm.getNamedItem(
				"number").getNodeValue()), Integer.parseInt(nnm.getNamedItem(
				"line").getNodeValue()), Integer.parseInt(nnm.getNamedItem(
				"column").getNodeValue()), nnm.getNamedItem("message")
				.getNodeValue());
		set.add(m);
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
