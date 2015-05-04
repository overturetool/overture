package org.overture.interpreter.runtime;


import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;
import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AIfStm;
import org.overture.interpreter.eval.ExpressionEvaluator;
import org.overture.interpreter.eval.StatementEvaluator;
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

//Analysis Adaptor
public class CoverageToXML extends AnalysisAdaptor {
    private Document doc;
    private Element rootElement;
    private Element currentElement;
    private Context ctx;
    private int iteration;
    private HashMap<ILexLocation,Element> xml_nodes;

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
        this.xml_nodes=new HashMap<>();
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
        ILexLocation local=node.getLocation();
        this.iteration = (int) local.getHits();
        PExp exp=node.getIfExp();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n",Integer.toString(iteration));
        eval.setTextContent( String.valueOf(exp.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));

        if(!xml_nodes.containsKey(local)){
            Element if_statement =doc.createElement("if_statement");
            fill_source_file_location(if_statement, local);
            if_statement.appendChild(eval);
            Element expression = doc.createElement("expression");
            if_statement.appendChild(expression);
            currentElement = expression;
            exp.apply(this);
            rootElement.appendChild(if_statement);
            xml_nodes.put(local,if_statement);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
            for(int i =0;i<xml_nodes.get(local).getChildNodes().getLength();i++){
                if (xml_nodes.get(local).getChildNodes().item(i).getNodeName().equals("expression"))
                    currentElement= (Element) xml_nodes.get(local).getChildNodes().item(i);
            }
            exp.apply(this);
        }
    }


    @Override
    public void caseANotEqualBinaryExp(ANotEqualBinaryExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        PExp left = node.getLeft();
        PExp right = node.getRight();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", String.valueOf(iteration));
        eval.setTextContent(String.valueOf(node.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        if(!xml_nodes.containsKey(local)){
            Element op=doc.createElement("not_equal");
            fill_source_file_location(op, local);
            op.appendChild(eval);
            currentElement.appendChild(op);
            xml_nodes.put(local, op);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
        }

    }


    @Override
    public void caseAGreaterEqualNumericBinaryExp(AGreaterEqualNumericBinaryExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        PExp left = node.getLeft();
        PExp right = node.getRight();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", String.valueOf(iteration));
        eval.setTextContent(String.valueOf(node.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        if(!xml_nodes.containsKey(local)){
            Element op=doc.createElement("greater_or_equal");
            fill_source_file_location(op, local);
            op.appendChild(eval);
            currentElement.appendChild(op);
            xml_nodes.put(local, op);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
        }
    }


    @Override
    public void caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        PExp left = node.getLeft();
        PExp right = node.getRight();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", String.valueOf(iteration));
        eval.setTextContent(String.valueOf(node.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        if(!xml_nodes.containsKey(local)){
            Element op=doc.createElement("greater");
            fill_source_file_location(op, local);
            op.appendChild(eval);
            currentElement.appendChild(op);
            xml_nodes.put(local, op);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
        }
    }


    @Override
    public void caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        PExp left = node.getLeft();
        PExp right = node.getRight();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", String.valueOf(iteration));
        eval.setTextContent(String.valueOf(node.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        if(!xml_nodes.containsKey(local)){
            Element op=doc.createElement("lesser_or_equal");
            fill_source_file_location(op, local);
            op.appendChild(eval);
            currentElement.appendChild(op);
            xml_nodes.put(local, op);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
        }
    }


    @Override
    public void caseALessNumericBinaryExp(ALessNumericBinaryExp node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        PExp left = node.getLeft();
        PExp right = node.getRight();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", String.valueOf(iteration));
        eval.setTextContent(String.valueOf(node.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));
        if(!xml_nodes.containsKey(local)){
            Element op=doc.createElement("lesser");
            fill_source_file_location(op, local);
            op.appendChild(eval);
            currentElement.appendChild(op);
            xml_nodes.put(local, op);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
        }
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

    @Override
    public void caseAElseIfStm(AElseIfStm node) throws AnalysisException {
        ILexLocation local=node.getLocation();
        this.iteration = (int) local.getHits();
        PExp exp=node.getElseIf();
        Element eval = doc.createElement("evaluation");
        eval.setAttribute("n", Integer.toString(iteration));
        eval.setTextContent(String.valueOf(exp.apply(VdmRuntime.getStatementEvaluator(), ctx).boolValue(ctx)));

        if(!xml_nodes.containsKey(local)){
            Element if_statement =doc.createElement("elseif");
            fill_source_file_location(if_statement, local);
            if_statement.appendChild(eval);
            Element expression = doc.createElement("expression");
            if_statement.appendChild(expression);
            currentElement = expression;
            exp.apply(this);
            rootElement.appendChild(if_statement);
            xml_nodes.put(local,if_statement);
        }
        else {
            xml_nodes.get(local).appendChild(eval);
            for(int i =0;i<xml_nodes.get(local).getChildNodes().getLength();i++){
                if (xml_nodes.get(local).getChildNodes().item(i).getNodeName().equals("expression"))
                    currentElement= (Element) xml_nodes.get(local).getChildNodes().item(i);
            }
            exp.apply(this);
        }
    }


    @Override
    public void caseAForAllExp(AForAllExp node) throws AnalysisException {
        Element for_all=doc.createElement("for_all");
        fill_source_file_location(for_all, node.getLocation());
        Element evaluation = doc.createElement("evaluation");
        evaluation.setAttribute("n",String.valueOf(iteration));
        evaluation.setTextContent("true");
        for_all.appendChild(evaluation);
        Element expression = doc.createElement("expression");
        for_all.appendChild(expression);
        currentElement.appendChild(for_all);
        currentElement = expression;
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

                Context aux=ctx;
                ctx=evalContext;
                if (!node.getPredicate().apply(VdmRuntime.getExpressionEvaluator(), evalContext).boolValue(ctx)){
                    evaluation.setTextContent("false");
                    node.getPredicate().apply(this);
                    ctx=aux;
                    break;
                }
                node.getPredicate().apply(this);
                ctx=aux;

            }
        } catch (ValueException e)
        {
            System.out.println(VdmRuntimeError.abort(node.getLocation(), e));
        }
    }
}



