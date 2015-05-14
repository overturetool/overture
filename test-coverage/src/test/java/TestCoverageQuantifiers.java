package test.java;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.BaseTestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class TestCoverageQuantifiers extends BaseTestCase {
    @Override
    public void test() throws Exception {


    }

    //Test ForAll quantifier for a true result.
    public void test_foo1_a() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo1()", new File("src/test/resources/test_quantifiers_1.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("count(//for_all)","1");
        queries.put("count(//lesser/evaluation)","6");
        queries.put("//if_statement/evaluation","true");
        queries.put("//for_all/evaluation","true");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_1.vdmsl.xml",queries);
    }

    //Test ForAll quantifier for a false result.
    public void test_foo1_b() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo1()", new File("src/test/resources/test_quantifiers_2.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("count(//for_all)","1");
        queries.put("count(//lesser/evaluation)","4");
        queries.put("//if_statement/evaluation","false");
        queries.put("//for_all/evaluation","false");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_2.vdmsl.xml",queries);
    }

    //Test exists1 for false when there is more than one positive outcome.
    public void test_foo2_a() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo2()", new File("src/test/resources/test_quantifiers_3.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("//if_statement/evaluation","false");
        queries.put("count(//exists1)","1");
        queries.put("//exists1/evaluation","false");
        queries.put("count(//exists1/expression/equals/evaluation)", "4");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_3.vdmsl.xml",queries);
    }


    //Test exists1 for false when there is no positive outcome.
    public void test_foo2_b() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo2()", new File("src/test/resources/test_quantifiers_4.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("//if_statement/evaluation","false");
        queries.put("count(//exists1)","1");
        queries.put("//exists1/evaluation","false");
        queries.put("count(//exists1/expression/equals/evaluation)","6");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_4.vdmsl.xml",queries);
    }

    //Test exists1 for true when there is only one positive outcome.
    public void test_foo2_c() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo2()", new File("src/test/resources/test_quantifiers_5.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("//if_statement/evaluation","true");
        queries.put("count(//exists1)","1");
        queries.put("//exists1/evaluation","true");
        queries.put("count(//exists1/expression/equals/evaluation)","6");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_5.vdmsl.xml",queries);
    }


    //Test exists for false when there isn't any.
    public void test_foo3_a() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo3()", new File("src/test/resources/test_quantifiers_6.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("//if_statement/evaluation","false");
        queries.put("count(//exists)","1");
        queries.put("//exists/evaluation","false");
        queries.put("count(//exists/expression/equals/evaluation)","6");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_6.vdmsl.xml",queries);
    }


    //Test exists for true when there is one.
    public void test_foo3_b() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo3()", new File("src/test/resources/test_quantifiers_7.vdmsl".replace('/', File.separatorChar)), true);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        //assert result.
        HashMap<String, String> queries = new HashMap<String, String>();
        queries.put("count(//if_statement)","1");
        queries.put("//if_statement/evaluation","true");
        queries.put("count(//exists)","1");
        queries.put("//exists/evaluation","true");
        queries.put("count(//exists/expression/equals/evaluation)","4");
        assertQueries("test/target/vdmsl-coverage/quantifiers/test_quantifiers_7.vdmsl.xml",queries);
    }


    //Receives the generated XML path and pairs of queries/results expected from those queries.
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
        XpathEngine engine = XMLUnit.newXpathEngine();

        for(String query : queries.keySet()){
            try {
                assertEquals(engine.evaluate(query,doc),queries.get(query));
            } catch (XpathException e) {
                e.printStackTrace();
            }
        }
    }
}
