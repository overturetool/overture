package org.overture.interpreter.runtime;


import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.PStm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class CoverageToXML {
    private Document doc;
    private Element rootElement;

    public CoverageToXML(){
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        doc = db.newDocument();
        rootElement = doc.createElement("file");
        doc.appendChild(rootElement);
    }

    public static void fill_source_file_location(Element and, ILexLocation local) {
        and.setAttribute("start_line", Integer.toString(local.getStartLine()));
        and.setAttribute("start_column", Integer.toString(local.getStartPos()));
        and.setAttribute("end_line", Integer.toString(local.getEndLine()));
        and.setAttribute("end_column", Integer.toString(local.getEndPos()));
    }

    public void evaluations(Element root, AVariableExp exp){
        ILexLocation local=exp.getLocation();
        for(int i=0;i<local.getHits();i++){
            Element eval=doc.createElement("evaluation");
            eval.setAttribute("n",Integer.toString(i));
            root.appendChild(eval);
        }
    }

    public void and_operator(Element root, AAndBooleanBinaryExp exp){
        Element and=doc.createElement("and");
        ILexLocation local=exp.getLocation();
        CoverageToXML.fill_source_file_location(and, local);
        PExp left=exp.getLeft();
        PExp right=exp.getRight();
        if((left instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) left);
            and.appendChild(condition);
        }
        else decision(left, and);
        if((right instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) right);
            and.appendChild(condition);
        }
        else decision(right, and);
        root.appendChild(and);
    }

    public void greater_operator(Element root, AGreaterNumericBinaryExp exp){
        Element greater=doc.createElement("greater");
        ILexLocation local=exp.getLocation();
        fill_source_file_location(greater, local);
        PExp left = exp.getLeft();
        PExp right = exp.getRight();

        if((left instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) left);
            greater.appendChild(condition);
        }
        else decision(left, greater);
        if((right instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) right);
            greater.appendChild(condition);
        }
        else decision(right, greater);
        root.appendChild(greater);
    }

    public void or_operator(Element root, AOrBooleanBinaryExp exp){
        Element or=doc.createElement("or");
        ILexLocation local=exp.getLocation();
        fill_source_file_location(or, local);
        PExp left = exp.getLeft();
        PExp right = exp.getRight();
        if((left instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) left);
            or.appendChild(condition);
        }
        else decision(left, or);
        if((right instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) right);
            or.appendChild(condition);
        }
        else decision(right, or);
        root.appendChild(or);
    }

    public void not_operator(Element root, ANotUnaryExp exp){
        Element not=doc.createElement("not");
        ILexLocation local=exp.getLocation();
        fill_source_file_location(not, local);
        PExp expression = exp.getExp();
        if((expression instanceof AVariableExp)){
            Element condition=doc.createElement("condition");
            evaluations(condition, (AVariableExp) expression);
            not.appendChild(condition);
        }
        else decision(expression, not);
        root.appendChild(not);
    }

    public void decision(PExp exp,Element root){
        if(exp instanceof AAndBooleanBinaryExp)and_operator(root,(AAndBooleanBinaryExp)exp);
        else if(exp instanceof AGreaterNumericBinaryExp)greater_operator(root,(AGreaterNumericBinaryExp)exp);
        else if(exp instanceof AOrBooleanBinaryExp)or_operator(root,(AOrBooleanBinaryExp) exp);
        else if(exp instanceof ANotUnaryExp)not_operator(root, (ANotUnaryExp) exp);
    }

    public void if_statement(AIfStm node){
        ILexLocation local=node.getLocation();

        Element if_statement =doc.createElement("if_statement");
        rootElement.appendChild(if_statement);
        fill_source_file_location(if_statement, local);

        PExp exp=node.getIfExp();
        Element expression=doc.createElement("expression");

        PStm elsestm=node.getElseStm();
        PStm thenstm=node.getThenStm();
        ILexLocation local_else=elsestm.getLocation();
        ILexLocation local_then=thenstm.getLocation();

        for(int i= (int) local_else.getHits();i>0;i--){
            Element eval=doc.createElement("evaluation");
            eval.setAttribute("n",Integer.toString(i));
            eval.setTextContent("false");
            if_statement.appendChild(eval);
        }

        for(int i= (int) local_then.getHits();i>0;i--){
            Element eval=doc.createElement("evaluation");
            eval.setAttribute("n",Integer.toString(i));
            eval.setTextContent("true");
            if_statement.appendChild(eval);
        }

        decision(exp, expression);
        if_statement.appendChild(expression);
    }

    public void saveCoverageXml(File coverage,File f) {
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
        StreamResult result = new StreamResult(new File(coverage.getPath() + File.separator + f.getName() + ".covtbl.xml"));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
