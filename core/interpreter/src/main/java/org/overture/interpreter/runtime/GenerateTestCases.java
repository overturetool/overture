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
import org.w3c.dom.NodeList;

//Analysis Adaptor
public class GenerateTestCases extends AnalysisAdaptor {
	private Document doc;
	private Element rootElement;
	private Element dec;
	private Element currentElement;
	public HashMap<ILexLocation, Element> xml_nodes;

	public GenerateTestCases() {
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
		this.xml_nodes = new HashMap<>();
	}

	public static void fill_source_file_location(Element and, ILexLocation local) {
		and.setAttribute("start_line", Integer.toString(local.getStartLine()));
		and.setAttribute("start_column", Integer.toString(local.getStartPos()));
		and.setAttribute("end_line", Integer.toString(local.getEndLine()));
		and.setAttribute("end_column", Integer.toString(local.getEndPos()));
	}
	
	public ILexLocation get_location(Element e) {
		int sl=Integer.valueOf(e.getAttribute("start_line"));
		int sc=Integer.valueOf(e.getAttribute("start_column"));
		int el=Integer.valueOf(e.getAttribute("end_line"));
		int ec=Integer.valueOf(e.getAttribute("end_column"));
		for (ILexLocation l : xml_nodes.keySet()){
			if (l.getEndLine()==el && l.getEndPos()==ec && l.getStartLine()==sl && l.getStartPos()==sc)
				return l;
		}
		return null;
	}
	
	public void saveCoverageXml(File coverage,String  filename) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(coverage.getPath() + File.separator + filename + "test_cases.xml"));
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
		ILexLocation local = node.getLocation();
		PExp exp = node.getIfExp();

		Element if_statement = doc.createElement("if_statement");
		fill_source_file_location(if_statement, local);
		Element expression = doc.createElement("expression");
		dec=if_statement;
		if_statement.appendChild(expression);
		currentElement = expression;
		Element condition = doc.createElement("condition");
		fill_source_file_location(condition, exp.getLocation());
		Element eval_true = doc.createElement("evaluation");
		eval_true.setAttribute("n", "1");
		eval_true.setTextContent("true");
		condition.appendChild(eval_true);
		
		Element eval_false = doc.createElement("evaluation");
		eval_false.setAttribute("n", "2");
		eval_false.setTextContent("false");
		condition.appendChild(eval_false);
		
		expression.appendChild(condition);
		rootElement.appendChild(if_statement);
		xml_nodes.put(local, if_statement);
		xml_nodes.put(node.getIfExp().getLocation(), condition);
		exp.apply(this);
	}

	

	@Override
	public void caseAExists1Exp(AExists1Exp node) throws AnalysisException {
		Element condition=(Element)xml_nodes.get(node.getLocation()).cloneNode(true);
		fill_source_file_location(condition, node.getPredicate().getLocation());
		currentElement.replaceChild(condition, xml_nodes.get(node.getLocation()));
		xml_nodes.remove(node.getLocation());
		xml_nodes.put(node.getLocation(), condition);
		node.getPredicate().apply(this);
	}

	@Override
	public void caseAExistsExp(AExistsExp node) throws AnalysisException {
		Element condition=(Element)xml_nodes.get(node.getLocation()).cloneNode(true);
		fill_source_file_location(condition, node.getPredicate().getLocation());
		currentElement.replaceChild(condition, xml_nodes.get(node.getLocation()));
		xml_nodes.remove(node.getLocation());
		xml_nodes.put(node.getLocation(), condition);
		node.getPredicate().apply(this);
	}

	@Override
	public void caseAElseIfStm(AElseIfStm node) throws AnalysisException {
		ILexLocation local = node.getLocation();
		PExp exp = node.getElseIf();
		
		Element elseif_statement = doc.createElement("elseif_statement");
		dec = elseif_statement;
		fill_source_file_location(elseif_statement, local);
		Element expression = doc.createElement("expression");
		elseif_statement.appendChild(expression);
		currentElement = expression;
		Element condition = doc.createElement("condition");
		fill_source_file_location(condition, exp.getLocation());
		Element eval_true = doc.createElement("evaluation");
		eval_true.setAttribute("n", "1");
		eval_true.setTextContent("true");
		condition.appendChild(eval_true);
		
		Element eval_false = doc.createElement("evaluation");
		eval_false.setAttribute("n", "2");
		eval_false.setTextContent("false");
		condition.appendChild(eval_false);
		
		expression.appendChild(condition);
		rootElement.appendChild(elseif_statement);
		xml_nodes.put(local, elseif_statement);
		xml_nodes.put(node.getElseIf().getLocation(), condition);
		exp.apply(this);
	}

	@Override
	public void caseAForAllExp(AForAllExp node) throws AnalysisException {
		Element condition=(Element)xml_nodes.get(node.getLocation()).cloneNode(true);
		fill_source_file_location(condition, node.getPredicate().getLocation());
		currentElement.replaceChild(condition, xml_nodes.get(node.getLocation()));
		xml_nodes.remove(node.getLocation());
		xml_nodes.put(node.getLocation(), condition);
		node.getPredicate().apply(this);
	}

	@Override
	public void caseAWhileStm(AWhileStm node) throws AnalysisException {
		ILexLocation local = node.getLocation();
		PExp exp = node.getExp();

		Element while_statement = doc.createElement("while_statement");
		dec=while_statement;
		fill_source_file_location(while_statement, local);
		Element expression = doc.createElement("expression");
		while_statement.appendChild(expression);
		currentElement = expression;
		Element condition = doc.createElement("condition");
		fill_source_file_location(condition, exp.getLocation());
		Element eval_true = doc.createElement("evaluation");
		eval_true.setAttribute("n", "1");
		eval_true.setTextContent("true");
		condition.appendChild(eval_true);
		
		Element eval_false = doc.createElement("evaluation");
		eval_false.setAttribute("n", "2");
		eval_false.setTextContent("false");
		condition.appendChild(eval_false);
		
		expression.appendChild(condition);
		rootElement.appendChild(while_statement);
		xml_nodes.put(local, while_statement);
		xml_nodes.put(node.getExp().getLocation(), condition);
		exp.apply(this);

	}

	@Override
	public void caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
			throws AnalysisException {
		
		Element and = doc.createElement("and");
		
		
		NodeList conditions=currentElement.getChildNodes();
		for (int i = 0; i < conditions.getLength(); i++) {
			NodeList evaluations = conditions.item(i).getChildNodes();
			Element condition=(Element) conditions.item(i).cloneNode(true);
			for (int j = 0; j < evaluations.getLength(); j++) {
				Element evaluation = (Element) evaluations.item(j);
				Element new_evaluation = (Element) evaluation.cloneNode(true);
				int n=Integer.valueOf(evaluation.getAttribute("n"));
				new_evaluation.setAttribute("n", String.valueOf(n+evaluations.getLength()));
				condition.appendChild(new_evaluation); 
				}
			currentElement.replaceChild(condition, conditions.item(i));
			ILexLocation local= get_location(condition);
			xml_nodes.remove(local);
			xml_nodes.put(local, condition);
			Element parent = xml_nodes.get(get_location(dec));
			parent.appendChild(condition);
			}
		
		
		ILexLocation left=node.getLeft().getLocation();
		ILexLocation right=node.getRight().getLocation();
		fill_source_file_location((Element) xml_nodes.get(node.getLocation()), left);
		Element condition_left =(Element) xml_nodes.get(node.getLocation()).cloneNode(true);
		
		Element condition_right=doc.createElement("condition");
		fill_source_file_location(condition_right, right);
		NodeList evaluations = xml_nodes.get(node.getLocation()).getChildNodes();
		
		for (int i = 0; i < evaluations.getLength(); i++) {
			Element eval=(Element) evaluations.item(i).cloneNode(true);
			if(i<evaluations.getLength()/2){
				eval.setTextContent("true");
			}else{
				eval.setTextContent("false");
			}
			eval.setAttribute("n", String.valueOf(i+1));
			condition_right.appendChild(eval);
		}
		xml_nodes.put(right, condition_right);
		currentElement.appendChild(condition_right);
		dec.appendChild(condition_right);
		xml_nodes.remove(left);
		xml_nodes.put(left, condition_left);
		//dec.appendChild(condition_left);
		
		node.getLeft().apply(this);
		node.getRight().apply(this);
		System.out.println("------->"+xml_nodes.toString());
		System.out.println("....>"+rootElement);
		
	}

	@Override
	public void caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node)
			throws AnalysisException {
		NodeList conditions=currentElement.getChildNodes();
		for (int i = 0; i < conditions.getLength(); i++) {
			NodeList evaluations = conditions.item(i).getChildNodes();
			Element condition=(Element) conditions.item(i).cloneNode(true);
			for (int j = 0; j < evaluations.getLength(); j++) {
				Element evaluation = (Element) evaluations.item(j);
				Element new_evaluation = (Element) evaluation.cloneNode(true);
				int n=Integer.valueOf(evaluation.getAttribute("n"));
				new_evaluation.setAttribute("n", String.valueOf(n+evaluations.getLength()));
				condition.appendChild(new_evaluation); 
				}
			currentElement.replaceChild(condition, conditions.item(i));
			ILexLocation local= get_location(condition);
			xml_nodes.remove(local);
			xml_nodes.put(local, condition);
			Element parent = xml_nodes.get(get_location(dec));
			parent.appendChild(condition);
			}
		
		
		ILexLocation left=node.getLeft().getLocation();
		ILexLocation right=node.getRight().getLocation();
		fill_source_file_location((Element) xml_nodes.get(node.getLocation()), left);
		Element condition_left =(Element) xml_nodes.get(node.getLocation()).cloneNode(true);
		
		Element condition_right=doc.createElement("condition");
		fill_source_file_location(condition_right, right);
		NodeList evaluations = xml_nodes.get(node.getLocation()).getChildNodes();
		
		for (int i = 0; i < evaluations.getLength(); i++) {
			Element eval=(Element) evaluations.item(i).cloneNode(true);
			if(i<evaluations.getLength()/2){
				eval.setTextContent("true");
			}else{
				eval.setTextContent("false");
			}
			eval.setAttribute("n", String.valueOf(i+1));
			condition_right.appendChild(eval);
		}
		xml_nodes.put(right, condition_right);
		currentElement.appendChild(condition_right);
		dec.appendChild(condition_right);
		xml_nodes.remove(left);
		xml_nodes.put(left, condition_left);
		//dec.appendChild(condition_left);
		node.getLeft().apply(this);
		node.getRight().apply(this);
	}

	@Override
	public void caseAIfExp(AIfExp node) throws AnalysisException {
		// TODO Auto-generated method stub
		ILexLocation local = node.getLocation();
		PExp exp = node.getTest();

		Element if_statement = doc.createElement("if_expression");
		fill_source_file_location(if_statement, local);
		Element expression = doc.createElement("expression");
		if_statement.appendChild(expression);
		currentElement = expression;
		Element condition = doc.createElement("condition");
		fill_source_file_location(condition, exp.getLocation());
		Element eval_true = doc.createElement("evaluation");
		eval_true.setAttribute("n", "1");
		eval_true.setTextContent("true");
		condition.appendChild(eval_true);
		
		Element eval_false = doc.createElement("evaluation");
		eval_false.setAttribute("n", "2");
		eval_false.setTextContent("false");
		condition.appendChild(eval_false);
		
		expression.appendChild(condition);
		rootElement.appendChild(if_statement);
		xml_nodes.put(local, if_statement);
		xml_nodes.put(node.getTest().getLocation(), condition);
		exp.apply(this);
	}
	
	
	
	
}
