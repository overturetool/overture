package org.overture.interpreter.runtime;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.values.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.util.HashMap;


public class CoverageToXML extends QuestionAdaptor<Context> {
	private Document doc;
	private Element rootElement;
	private Element currentElement;
	private int iteration;
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			boolean_variable.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			not.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.getIfExp().toString());
			Element if_statement = doc.createElement("if_statement");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("not_equal");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("greater_or_equal");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("greater");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("lesser_or_equal");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("lesser");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element op = doc.createElement("equals");
			fill_source_file_location(op, local);
			op.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element exists = doc.createElement("exists1");
			exists.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element exists = doc.createElement("exists");
			exists.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.getElseIf().toString());
			Element if_statement = doc.createElement("elseif");
			if_statement.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.getStatement().toString());
			Element forall_statement = doc.createElement("forall_statement");
			forall_statement.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.toString());
			Element exists = doc.createElement("for_all_expression");
			exists.appendChild(source_code);
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
		
		boolean value=true;
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctx);

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb : node.getBindList())
			{
				ValueList bvals = ctx.assistantFactory.createPMultipleBindAssistant().getBindValues(mb, ctx);

				for (PPattern p : mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init(ctx, false);

			while (quantifiers.hasNext())
			{
				Context evalContext = new Context(ctx.assistantFactory, node.getLocation(), "forall", ctx);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp : nvpl)
				{	
					
					Value v = evalContext.get(nvp.name);
			        
					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					} else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break; // This quantifier set does not match
						}
					}
				}
				node.getPredicate().apply(this, evalContext);
				if (matches
						&& !node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(), evalContext).boolValue(ctx))
				{
					evaluation.setTextContent("false");
					value = false;
					break;
				}
			}
		} catch (ValueException e)
		{
		}
		if(value)evaluation.setTextContent("true");
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.getExp().toString());
			Element while_statement = doc.createElement("while_statement");
			while_statement.appendChild(source_code);
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
			Element source_code = doc.createElement("source_code");
			source_code.setTextContent(node.getTest().toString());
			Element if_statement = doc.createElement("if_expression");
			fill_source_file_location(if_statement, local);
			if_statement.appendChild(source_code);
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

	/*
	 * @Override public void caseAPostOpExp(APostOpExp node) throws
	 * AnalysisException { // TODO Auto-generated method stub ILexLocation local
	 * = node.getLocation(); this.iteration = (int) local.getHits(); PExp exp =
	 * node.getPostexpression(); Element eval = doc.createElement("evaluation");
	 * eval.setAttribute("n", Integer.toString(iteration));
	 * eval.setTextContent(String.valueOf(exp.apply(
	 * VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
	 * 
	 * if (!xml_nodes.containsKey(local)) { Element source_code =
	 * doc.createElement("source_code");
	 * source_code.setTextContent(node.getPostexpression().toString()); Element
	 * if_statement = doc.createElement("post");
	 * if_statement.appendChild(source_code);
	 * fill_source_file_location(if_statement, local);
	 * if_statement.appendChild(eval); Element expression =
	 * doc.createElement("expression"); if_statement.appendChild(expression);
	 * currentElement = expression; exp.apply(this);
	 * rootElement.appendChild(if_statement); xml_nodes.put(local,
	 * if_statement); } else { xml_nodes.get(local).appendChild(eval); for (int
	 * i = 0; i < xml_nodes.get(local).getChildNodes() .getLength(); i++) { if
	 * (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
	 * .equals("expression")) currentElement = (Element) xml_nodes.get(local)
	 * .getChildNodes().item(i); } exp.apply(this); } }
	 * 
	 * @Override public void caseAPreOpExp(APreOpExp node) throws
	 * AnalysisException { ILexLocation local = node.getLocation();
	 * this.iteration = (int) local.getHits(); PExp exp = node.getExpression();
	 * Element eval = doc.createElement("evaluation"); eval.setAttribute("n",
	 * Integer.toString(iteration));
	 * eval.setTextContent(String.valueOf(exp.apply(
	 * VdmRuntime.getExpressionEvaluator(), ctx).boolValue(ctx)));
	 * 
	 * if (!xml_nodes.containsKey(local)) { Element source_code =
	 * doc.createElement("source_code");
	 * source_code.setTextContent(node.getExpression().toString()); Element
	 * if_statement = doc.createElement("pre");
	 * if_statement.appendChild(source_code);
	 * fill_source_file_location(if_statement, local);
	 * if_statement.appendChild(eval); Element expression =
	 * doc.createElement("expression"); if_statement.appendChild(expression);
	 * currentElement = expression; exp.apply(this);
	 * rootElement.appendChild(if_statement); xml_nodes.put(local,
	 * if_statement); } else { xml_nodes.get(local).appendChild(eval); for (int
	 * i = 0; i < xml_nodes.get(local).getChildNodes() .getLength(); i++) { if
	 * (xml_nodes.get(local).getChildNodes().item(i).getNodeName()
	 * .equals("expression")) currentElement = (Element) xml_nodes.get(local)
	 * .getChildNodes().item(i); } exp.apply(this); } }
	 */

}
