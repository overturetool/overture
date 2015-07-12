package org.overture.interpreter.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CoverageToXML extends QuestionAdaptor<Context> {
	private Document doc;
	private Element rootElement;
	private Element currentElement;
	public int iteration;
	private HashMap<ILexLocation, Element> xml_nodes;

	public CoverageToXML() {
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.doc = db.newDocument();
		this.rootElement = doc.createElement("file");
		this.currentElement = rootElement;
		this.doc.appendChild(rootElement);
		this.iteration = 0;
		this.xml_nodes = new HashMap<>();
	}

	public ILexLocation get_location(Element e) {
		int sl = Integer.valueOf(e.getAttribute("start_line"));
		int sc = Integer.valueOf(e.getAttribute("start_column"));
		int el = Integer.valueOf(e.getAttribute("end_line"));
		int ec = Integer.valueOf(e.getAttribute("end_column"));
		for (ILexLocation l : xml_nodes.keySet()) {
			if (l.getEndLine() == el && l.getEndPos() == ec
					&& l.getStartLine() == sl && l.getStartPos() == sc)
				return l;
		}
		return null;
	}

	public static void fill_source_file_location(Element and, ILexLocation local) {
		and.setAttribute("start_line", Integer.toString(local.getStartLine()));
		and.setAttribute("start_column", Integer.toString(local.getStartPos()));
		and.setAttribute("end_line", Integer.toString(local.getEndLine()));
		and.setAttribute("end_column", Integer.toString(local.getEndPos()));
	}

	public void saveCoverageXml(File coverage, String filename) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(coverage.getPath()
				+ File.separator + filename + ".xml"));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void caseAVariableExp(AVariableExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setTextContent(ctx.lookup(node.getName()).toString());
		eval.setAttribute("n", Integer.toString(this.iteration));

		if (!xml_nodes.containsKey(local)) {
			Element boolean_variable = doc.createElement("boolean_variable");
			fill_source_file_location(boolean_variable, local);
			boolean_variable.appendChild(eval);
			currentElement.appendChild(boolean_variable);
			xml_nodes.put(local, boolean_variable);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseANotUnaryExp(ANotUnaryExp node, Context ctx)
			throws AnalysisException {

		ILexLocation local = node.getLocation();
		if (!xml_nodes.containsKey(local)) {
			Element not = doc.createElement("not");
			fill_source_file_location(not, local);
			currentElement.appendChild(not);
			currentElement = not;
			xml_nodes.put(local, not);
		} else {
			currentElement = xml_nodes.get(local);
		}
		PExp expression = node.getExp();
		expression.apply(this, ctx);
	}

	@Override
	public void defaultSBooleanBinaryExp(SBooleanBinaryExp node, Context ctx)
			throws AnalysisException {

		ILexLocation local = node.getLocation();
		PExp left = node.getLeft();
		PExp right = node.getRight();

		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement(node.getOp().toString());
			fill_source_file_location(op, local);
			currentElement.appendChild(op);
			currentElement = op;
			xml_nodes.put(local, op);
			left.apply(this, ctx);
			currentElement = op;
			right.apply(this, ctx);
		} else {
			currentElement = xml_nodes.get(local);
			left.apply(this, ctx);
			currentElement = xml_nodes.get(local);
			right.apply(this, ctx);
		}

	}

	@Override
	public void caseAIfStm(AIfStm node, Context ctx) throws AnalysisException {
		ILexLocation local = node.getLocation();
		this.iteration = (int) local.getHits();
		PExp exp = node.getIfExp();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", Integer.toString(iteration));
		eval.setTextContent(String.valueOf(exp.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {
			Element if_statement = doc.createElement("if_statement");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(eval);
			Element expression = doc.createElement("expression");
			if_statement.appendChild(expression);
			currentElement = expression;
			rootElement.appendChild(if_statement);
			xml_nodes.put(local, if_statement);
			exp.apply(this, ctx);
		} else {
			xml_nodes.get(local).appendChild(eval);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
			exp.apply(this, ctx);
		}
	}

	@Override
	public void caseANotEqualBinaryExp(ANotEqualBinaryExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("not_equal");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}

	}

	@Override
	public void caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("greater_or_equal");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			Context ctx) throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("greater");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node,
			Context ctx) throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("lesser_or_equal");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			Context ctx) throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("lesser");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseAEqualsBinaryExp(AEqualsBinaryExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", String.valueOf(iteration));
		eval.setTextContent(String.valueOf(node.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
		if (!xml_nodes.containsKey(local)) {
			Element op = doc.createElement("equals");
			fill_source_file_location(op, local);
			op.appendChild(eval);
			currentElement.appendChild(op);
			xml_nodes.put(local, op);
		} else {
			xml_nodes.get(local).appendChild(eval);
		}
	}

	@Override
	public void caseAExists1Exp(AExists1Exp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element evaluation = doc.createElement("evaluation");
		evaluation.setAttribute("n", String.valueOf(iteration));
		evaluation.setTextContent(null);

		if (!xml_nodes.containsKey(local)) {
			Element exists = doc.createElement("exists1");
			fill_source_file_location(exists, node.getLocation());
			Element expression = doc.createElement("expression");
			exists.appendChild(evaluation);
			exists.appendChild(expression);
			xml_nodes.put(local, exists);
			currentElement.appendChild(exists);
			currentElement = expression;
		} else {
			xml_nodes.get(local).appendChild(evaluation);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
		}

		ValueList allValues = null;
		boolean alreadyFound = false;

		try {
			allValues = ctx.assistantFactory.createPBindAssistant()
					.getBindValues(node.getBind(), ctx);
		} catch (ValueException e) {
			VdmRuntimeError.abort(node.getLocation(), e);
		}

		for (Value val : allValues) {
			try {
				Context evalContext = new Context(ctx.assistantFactory,
						node.getLocation(), "exists1", ctx);
				evalContext.putList(ctx.assistantFactory
						.createPPatternAssistant().getNamedValues(
								node.getBind().getPattern(), val, ctx));
				Context aux = ctx;
				ctx = evalContext;
				node.getPredicate().apply(this, ctx);
				ctx = aux;
				if (node.getPredicate()
						.apply(VdmRuntime.getExpressionEvaluator(), evalContext)
						.boolValue(ctx)) {
					if (alreadyFound) {
						evaluation.setTextContent("false");
						break;
					}

					alreadyFound = true;
				}
			} catch (ValueException e) {
				VdmRuntimeError.abort(node.getLocation(), e);
			} catch (PatternMatchException e) {
				// Ignore pattern mismatches
			}
		}
		if (evaluation.getTextContent().isEmpty())
			evaluation.setTextContent(String.valueOf(alreadyFound));
	}

	@Override
	public void caseAExistsExp(AExistsExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element evaluation = doc.createElement("evaluation");
		evaluation.setAttribute("n", String.valueOf(iteration));
		evaluation.setTextContent(null);

		if (!xml_nodes.containsKey(local)) {
			Element exists = doc.createElement("exists");
			fill_source_file_location(exists, node.getLocation());
			exists.appendChild(evaluation);
			Element expression = doc.createElement("expression");
			exists.appendChild(expression);
			currentElement.appendChild(exists);
			currentElement = expression;
		} else {
			xml_nodes.get(local).appendChild(evaluation);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
		}
		try {
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb : node.getBindList()) {
				ValueList bvals = ctx.assistantFactory
						.createPMultipleBindAssistant().getBindValues(mb, ctx);

				for (PPattern p : mb.getPlist()) {
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init(ctx, false);

			while (quantifiers.hasNext()) {
				Context evalContext = new Context(ctx.assistantFactory,
						node.getLocation(), "exists", ctx);
				NameValuePairList nvpl = quantifiers.next();
				for (NameValuePair nvp : nvpl) {
					Value v = evalContext.get(nvp.name);

					if (v == null) {
						evalContext.put(nvp.name, nvp.value);
					} else {
						if (!v.equals(nvp.value)) {
							break; // This quantifier set does not match
						}
					}
				}

				Context aux = ctx;
				ctx = evalContext;
				if (node.getPredicate()
						.apply(VdmRuntime.getExpressionEvaluator(), evalContext)
						.boolValue(ctx)) {
					evaluation.setTextContent("true");
					node.getPredicate().apply(this, ctx);
					ctx = aux;
					break;
				}
				node.getPredicate().apply(this, ctx);
				ctx = aux;

			}
		} catch (ValueException e) {
			System.out.println(VdmRuntimeError.abort(node.getLocation(), e));
		}
		if (evaluation.getTextContent().isEmpty())
			evaluation.setTextContent("false");
	}

	
	
	
	@Override
	public void caseAElseIfStm(AElseIfStm node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		this.iteration = (int) local.getHits();
		PExp exp = node.getElseIf();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", Integer.toString(iteration));
		eval.setTextContent(String.valueOf(exp.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {

			Element if_statement = doc.createElement("elseif");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(eval);
			Element expression = doc.createElement("expression");
			if_statement.appendChild(expression);
			currentElement = expression;
			exp.apply(this, ctx);
			rootElement.appendChild(if_statement);
			xml_nodes.put(local, if_statement);
		} else {
			xml_nodes.get(local).appendChild(eval);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
			exp.apply(this, ctx);
		}
	}

	@Override
	public void caseAElseIfExp(AElseIfExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		this.iteration = (int) local.getHits();
		PExp exp = node.getElseIf();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", Integer.toString(iteration));
		eval.setTextContent(String.valueOf(exp.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {

			Element if_statement = doc.createElement("elseifexpression");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(eval);
			Element expression = doc.createElement("expression");
			if_statement.appendChild(expression);
			currentElement = expression;
			exp.apply(this, ctx);
			rootElement.appendChild(if_statement);
			xml_nodes.put(local, if_statement);
		} else {
			xml_nodes.get(local).appendChild(eval);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
			exp.apply(this, ctx);
		}
	}

	@Override
	public void caseAForAllStm(AForAllStm node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		if (!xml_nodes.containsKey(local)) {
			this.iteration = (int) local.getHits();

			Element forall_statement = doc.createElement("forall_statement");
			fill_source_file_location(forall_statement, local);
			Element expression = doc.createElement("expression");
			forall_statement.appendChild(expression);
			currentElement = expression;
			rootElement.appendChild(forall_statement);
			xml_nodes.put(local, forall_statement);
		} else {
			Element evaluation = doc.createElement("evaluation");
			evaluation.setAttribute(
					"n",
					Integer.toString((int) node.getStatement().getLocation()
							.getHits()));

			evaluation.setTextContent(String.valueOf(node.getSet()
					.apply(VdmRuntime.getExpressionEvaluator(), ctx)
					.boolValue(ctx)));

			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
				xml_nodes.get(local).appendChild(evaluation);
			}
		}
	}

	@Override
	public void caseAForAllExp(AForAllExp node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();
		Element evaluation = doc.createElement("evaluation");
		evaluation.setAttribute("n", String.valueOf(iteration));
		evaluation.setTextContent(null);

		if (!xml_nodes.containsKey(local)) {

			Element exists = doc.createElement("for_all_expression");
			fill_source_file_location(exists, node.getLocation());
			Element expression = doc.createElement("expression");
			exists.appendChild(evaluation);
			exists.appendChild(expression);
			xml_nodes.put(local, exists);
			currentElement.appendChild(exists);
			currentElement = expression;
		} else {
			xml_nodes.get(local).appendChild(evaluation);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
		}

		boolean value = true;
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctx);

		try {
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb : node.getBindList()) {
				ValueList bvals = ctx.assistantFactory
						.createPMultipleBindAssistant().getBindValues(mb, ctx);

				for (PPattern p : mb.getPlist()) {
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init(ctx, false);

			while (quantifiers.hasNext()) {
				Context evalContext = new Context(ctx.assistantFactory,
						node.getLocation(), "forall", ctx);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp : nvpl) {

					Value v = evalContext.get(nvp.name);

					if (v == null) {
						evalContext.put(nvp.name, nvp.value);
					} else {
						if (!v.equals(nvp.value)) {
							matches = false;
							break; // This quantifier set does not match
						}
					}
				}
				node.getPredicate().apply(this, evalContext);
				if (matches
						&& !node.getPredicate()
								.apply(VdmRuntime.getExpressionEvaluator(),
										evalContext).boolValue(ctx)) {
					evaluation.setTextContent("false");
					value = false;
					break;
				}
			}
		} catch (ValueException e) {
		}
		if (value)
			evaluation.setTextContent("true");
	}

	@Override
	public void caseAWhileStm(AWhileStm node, Context ctx)
			throws AnalysisException {
		ILexLocation local = node.getLocation();

		Element evaluation = doc.createElement("evaluation");
		evaluation.setAttribute("n", Integer.toString(iteration));
		evaluation
				.setTextContent(String.valueOf(node.getExp()
						.apply(VdmRuntime.getExpressionEvaluator(), ctx)
						.boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {
			this.iteration = (int) local.getHits();

			Element while_statement = doc.createElement("while_statement");
			while_statement.appendChild(evaluation);
			fill_source_file_location(while_statement, local);
			Element expression = doc.createElement("expression");
			while_statement.appendChild(expression);
			currentElement = expression;
			rootElement.appendChild(while_statement);
			xml_nodes.put(local, while_statement);
		} else {
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
				xml_nodes.get(local).appendChild(evaluation);
			}
		}

	}

	@Override
	public void caseAIfExp(AIfExp node, Context ctx) throws AnalysisException {
		ILexLocation local = node.getLocation();
		this.iteration = (int) local.getHits();
		PExp exp = node.getTest();
		Element eval = doc.createElement("evaluation");
		eval.setAttribute("n", Integer.toString(iteration));
		eval.setTextContent(String.valueOf(exp.apply(
				VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));

		if (!xml_nodes.containsKey(local)) {

			Element if_statement = doc.createElement("if_expression");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(eval);
			Element expression = doc.createElement("expression");
			if_statement.appendChild(expression);
			currentElement = expression;
			exp.apply(this, ctx);
			rootElement.appendChild(if_statement);
			xml_nodes.put(local, if_statement);
		} else {
			xml_nodes.get(local).appendChild(eval);
			for (int i = 0; i < xml_nodes.get(local).getChildNodes()
					.getLength(); i++) {
				if (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
						.equals("expression"))
					currentElement = (Element) xml_nodes.get(local)
							.getChildNodes().item(i);
			}
			exp.apply(this, ctx);
		}
	}

	public void mark_tested(GenerateTestCases gtc) {
		NodeList decisions = rootElement.getChildNodes();
		for (int i = 0; i < decisions.getLength(); i++) {
			Element decision = (Element) decisions.item(i);
			Element generated = gtc.xml_nodes.get(get_location(decision));
			NodeList expressions = decision.getElementsByTagName("expression");
			if (expressions != null) {
				NodeList conditions = generated.getElementsByTagName("condition");
				HashMap<Integer, ArrayList<Integer>> test_numbers = new HashMap<Integer, ArrayList<Integer>>();
				if (conditions != null) {
					for(int j=0; j<conditions.getLength();j++){
						Element condition = (Element) conditions.item(j);
						ILexLocation local = get_location(condition);
						Element tested= xml_nodes.get(local);
						NodeList evaluations = condition.getElementsByTagName("evaluation");
						
						for (int k=0; k<evaluations.getLength();k++){
							Element evaluation = (Element) evaluations.item(k);
							int test_number = Integer.valueOf(evaluation.getAttribute("n"));
							if(!test_numbers.containsKey(test_number))test_numbers.put(test_number, new ArrayList<Integer>());
							NodeList tested_evaluation = tested.getChildNodes();
							for (int n=0;n<tested_evaluation.getLength();n++){
								Element eval2= (Element) tested_evaluation.item(n);
								int test_number2= Integer.valueOf(eval2.getAttribute("n"));
								if(evaluation.getTextContent().equals("?")){
									if(!test_numbers.get(test_number).contains(test_number2) && j==0){
										test_numbers.get(test_number).add(test_number2);
									}
								}
								else if(eval2.getTextContent().equals(evaluation.getTextContent())){
									if(!test_numbers.get(test_number).contains(test_number2) && j==0){
										test_numbers.get(test_number).add(test_number2);
									}
								}else if(!eval2.getTextContent().equals(evaluation.getTextContent())){
									if(test_numbers.get(test_number).contains(test_number2)){
										test_numbers.get(test_number).remove(test_numbers.get(test_number).indexOf(test_number2));
									}
								}
							}
							
						}
					}
					for(int tn: test_numbers.keySet()){
						if (!test_numbers.get(tn).isEmpty()){
							setTested(generated, String.valueOf(tn));
						}
					}

				}

			}
			
		}

	}

	public void setTested(Element decision, String test_number) {
		NodeList evaluations = decision.getElementsByTagName("evaluation");
		for (int i = 0; i < evaluations.getLength(); i++) {
			Element eval = (Element) evaluations.item(i);
			if (eval.getAttribute("n").equals(test_number))
				eval.setAttribute("tested", "true");
		}
	}

	public boolean evaluationEquals(Element a, Element b) throws Exception {
		String content_a = a.getTextContent();
		String content_b = b.getTextContent();

		if (content_a == null || content_b == null)
			throw new Exception("No text content on node " + a.toString());

		if (!content_a.equals("true") || !content_a.equals("false")
				|| !content_a.equals("?"))
			throw new Exception("Node " + a.toString()
					+ " has invalid content!");

		if (!content_b.equals("true") || !content_b.equals("false")
				|| !content_b.equals("?"))
			throw new Exception("Node " + b.toString()
					+ " has invalid content!");

		if (a.getTextContent().equals("?") || b.getTextContent().equals("?"))
			return true;
		else
			return (a.getTextContent().equals(b.getTextContent()));
	}

	public String getEvalNumber(Element a) throws Exception {
		if (a.hasAttribute("n"))
			return a.getAttribute("n");
		else {
			throw new Exception("Evaluation has no number!");
		}
	}

}
