package org.overture.interpreter.runtime;

import java.io.File;
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

import org.omg.CORBA.FREE_MEM;
import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.AWhileStm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//Analysis Adaptor
public class EliminateMaskedTests extends AnalysisAdaptor {
	private HashMap<ILexLocation, Element> xml_nodes;

	public EliminateMaskedTests(HashMap<ILexLocation, Element> xml_nodes) {
		this.xml_nodes = xml_nodes;
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

	public void saveCoverageXml(File coverage, String filename) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = db.newDocument();
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
				+ File.separator + filename + "test_cases.xml"));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void caseANotUnaryExp(ANotUnaryExp node) throws AnalysisException {
		PExp expression = node.getExp();
		expression.apply(this);
	}

	@Override
	public void caseAIfStm(AIfStm node) throws AnalysisException {
		PExp expression = node.getIfExp();
		expression.apply(this);
	}

	@Override
	public void caseAExists1Exp(AExists1Exp node) throws AnalysisException {
		PExp expression = node.getPredicate();
		expression.apply(this);
	}

	@Override
	public void caseAExistsExp(AExistsExp node) throws AnalysisException {
		PExp expression = node.getPredicate();
		expression.apply(this);
	}

	@Override
	public void caseAElseIfStm(AElseIfStm node) throws AnalysisException {
		PExp expression = node.getElseIf();
		expression.apply(this);
	}

	@Override
	public void caseAForAllExp(AForAllExp node) throws AnalysisException {
		PExp expression = node.getPredicate();
		expression.apply(this);
	}

	@Override
	public void caseAWhileStm(AWhileStm node) throws AnalysisException {
		PExp expression = node.getExp();
		expression.apply(this);
	}

	@Override
	public void caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
			throws AnalysisException {
		PExp left = node.getLeft();
		PExp right = node.getRight();

		if (xml_nodes.containsKey(left.getLocation())) {
			Element condition = xml_nodes.get(left.getLocation());
			NodeList evaluations = condition.getChildNodes();
			boolean found_true = false;

			for (int i = 0; i < evaluations.getLength(); i++) {
				Element evaluation = (Element) evaluations.item(i);
				if (evaluation.getTextContent().equals("false")) {
					if (found_true) {
						String eval_number = evaluation.getAttribute("n");
						remove_evaluation(condition.getParentNode(),
								eval_number);
					} else {
						found_true = true;
					}
				}
			}
		}
		if (xml_nodes.containsKey(right.getLocation())) {
			Element condition = xml_nodes.get(right.getLocation());
			NodeList evaluations = condition.getChildNodes();
			boolean found_true = false;
			for (int i = 0; i < evaluations.getLength(); i++) {
				Element evaluation = (Element) evaluations.item(i);
				if (evaluation.getTextContent().equals("false")) {
					if (found_true) {
						String eval_number = evaluation.getAttribute("n");
						Element parent = (Element) condition.getParentNode();
						remove_evaluation(parent, eval_number);
					} else {
						found_true = true;
					}
				}
			}
		}
		left.apply(this);
		right.apply(this);
	}

	private void remove_evaluation(Node parentNode, String eval_number) {

		if (parentNode!=null) {
			NodeList conditions = parentNode.getChildNodes();
			for (int i = 0; i < conditions.getLength(); i++) {
				NodeList evaluations = conditions.item(i).getChildNodes();
				for (int j = 0; j < evaluations.getLength(); j++) {
					Element evaluation = (Element) evaluations.item(j);
					if (evaluation.getAttribute("n").equals(eval_number)) {
						conditions.item(i).removeChild(evaluation);
					}
				}
			}
		}
	}

	@Override
	public void caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node)
			throws AnalysisException {
		PExp left = node.getLeft();
		PExp right = node.getRight();

		if (xml_nodes.containsKey(left.getLocation())) {
			Element condition = xml_nodes.get(left.getLocation());
			NodeList evaluations = condition.getChildNodes();
			boolean found_true = false;
			for (int i = 0; i < evaluations.getLength(); i++) {
				Element evaluation = (Element) evaluations.item(i);
				if (evaluation.getTextContent().equals("true")) {
					if (found_true) {
						String eval_number = evaluation.getAttribute("n");
						remove_evaluation(condition.getParentNode(),
								eval_number);
					} else {
						found_true = true;
					}
				}
			}
		}
		if (xml_nodes.containsKey(right.getLocation())) {
			Element condition = xml_nodes.get(right.getLocation());
			NodeList evaluations = condition.getChildNodes();
			boolean found_true = false;
			for (int i = 0; i < evaluations.getLength(); i++) {
				Element evaluation = (Element) evaluations.item(i);
				if (evaluation.getTextContent().equals("true")) {
					if (found_true) {
						String eval_number = evaluation.getAttribute("n");
						remove_evaluation(condition.getParentNode(),
								eval_number);
					} else {
						found_true = true;
					}
				}
			}
		}
		left.apply(this);
		right.apply(this);
	}

	@Override
	public void caseAIfExp(AIfExp node) throws AnalysisException {
		PExp expression = node.getTest();
		expression.apply(this);
	}

}
