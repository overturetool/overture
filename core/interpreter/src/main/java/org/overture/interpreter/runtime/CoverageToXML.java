package org.overture.interpreter.runtime;


import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AIfStm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
//Analysis Adaptor
public class CoverageToXML extends AnalysisAdaptor {
    private Document doc;
    private Element rootElement;
    private Element currentElement;
    private Context ctx;
    private int iteration;

    public CoverageToXML(){
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
        this.ctx = null;
        this.iteration = 0;
    }

    public void setContext(Context context){
        this.ctx=context;
    }

    public static void fill_source_file_location(Element and, ILexLocation local) {
        and.setAttribute("start_line", Integer.toString(local.getStartLine()));
        and.setAttribute("start_column", Integer.toString(local.getStartPos()));
        and.setAttribute("end_line", Integer.toString(local.getEndLine()));
        and.setAttribute("end_column", Integer.toString(local.getEndPos()));
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
        StreamResult result = new StreamResult(new File(coverage.getPath() + File.separator + filename + ".xml"));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void caseAVariableExp(AVariableExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        Element eval=doc.createElement("evaluation");
        fill_source_file_location(eval, local);
        eval.setTextContent(ctx.lookup(node.getName()).toString());
        eval.setAttribute("n", Integer.toString(this.iteration));
        currentElement.appendChild(eval);
    }

    @Override
    public void caseANotUnaryExp(ANotUnaryExp node) throws AnalysisException {
        Element not=doc.createElement("not");
        ILexLocation local=node.getLocation();
        fill_source_file_location(not, local);
        PExp expression = node.getExp();
        currentElement.appendChild(not);
        currentElement = not;
        expression.apply(this);
    }

    @Override
    public void defaultSBooleanBinaryExp(SBooleanBinaryExp node) throws AnalysisException {
        Element op=doc.createElement(node.getOp().toString());
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }

    @Override
    public void caseAIfStm(AIfStm node) throws AnalysisException {
        this.iteration = (int) node.getLocation().getHits();
        ILexLocation local=node.getLocation();
        Element if_statement =doc.createElement("if_statement");
        Element eval = doc.createElement("evaluation");
        if_statement.appendChild(eval);
        eval.setAttribute("n",Integer.toString(iteration));
        rootElement.appendChild(if_statement);
        fill_source_file_location(if_statement, local);
        PExp exp=node.getIfExp();
        eval.setTextContent( String.valueOf(exp.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        Element expression=doc.createElement("expression");
        if_statement.appendChild(expression);
        currentElement = expression;
        exp.apply(this);
    }


    @Override
    public void caseANotEqualBinaryExp(ANotEqualBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("not_equal");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }


    @Override
    public void caseAGreaterEqualNumericBinaryExp(AGreaterEqualNumericBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("greater_or_equal");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }


    @Override
    public void caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("greater");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }


    @Override
    public void caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("lesser_or_equal");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }


    @Override
    public void caseALessNumericBinaryExp(ALessNumericBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("lesser");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);
    }

    @Override
    public void defaultSBinaryExp(SBinaryExp node) throws AnalysisException {
        Element op=doc.createElement("numeric_expression");
        ILexLocation local=node.getLocation();
        fill_source_file_location(op, local);
        PExp left = node.getLeft();
        PExp right = node.getRight();
        currentElement.appendChild(op);
        currentElement = op;
        left.apply(this);
        currentElement = op;
        right.apply(this);

    }
}
