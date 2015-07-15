package org.overture.interpreter.tests.coverage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.test.framework.BaseTestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestCoverageOperators extends BaseTestCase{

	@Override
    public void test() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        InterpreterUtil.interpret(Dialect.VDM_SL, "operators(false,true,true)", new File("src/test/resources/coverage/test_operators.vdmsl".replace('/', File.separatorChar)), true);
        
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("src/test/target/vdmsl-coverage/operators".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeMCDCCoverage(interpreter, coverageFolder);
        
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        assertQueries("src/test/target/vdmsl-coverage/operators/test_operators.vdmsl.xml",queries);
    }
	
    public void test3() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        InterpreterUtil.interpret(Dialect.VDM_SL, "Run(true, false)", new File("src/test/resources/coverage/test_implies.vdmsl".replace('/', File.separatorChar)), true);
        
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("src/test/target/vdmsl-coverage/implies".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeMCDCCoverage(interpreter, coverageFolder);
    }
	
	public void test2() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        InterpreterUtil.interpret(Dialect.VDM_SL, "TestEqualsOP()", new File("src/test/resources/coverage/test_equalsoperator.vdmsl".replace('/', File.separatorChar)), true);
        
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("src/test/target/vdmsl-coverage/operators".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeMCDCCoverage(interpreter, coverageFolder);
        
        HashMap<String, String> queries = new HashMap<String, String>();
        //assertQueries("src/test/target/vdmsl-coverage/operators/test_equalsoperator.vdmsl.xml",queries);
    }
    
    public void assertQueries(String file_path, HashMap<String, String> queries){
        File fXmlFile = new File(file_path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Query result file
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath engine = xPathfactory.newXPath();

        for(String query : queries.keySet()){
                try {
					TestCase.assertEquals(engine.evaluate(query, doc), queries.get(query));
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }

}
