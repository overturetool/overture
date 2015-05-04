package test.java;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.BaseTestCase;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


public class TestCoverageQuantifiers extends BaseTestCase {
    @Override
    public void test() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_SL;
        Value test = InterpreterUtil.interpret(Dialect.VDM_SL, "foo1()", new File("src/test/resources/test_quantifiers.vdmsl".replace('/', File.separatorChar)), true);
        System.out.println("The interpreter executed test with the result: "
                + test);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmsl-coverage/quantifiers".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);

        File fXmlFile = new File("test/target/vdmpp-coverage/if-statement/test_if_elseif_statements.vdmpp.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //Query result file
        XpathEngine engine = XMLUnit.newXpathEngine();
        assertEquals(engine.evaluate("count(//if_statement)", doc), "1");
        assertEquals(engine.evaluate("count(//elseif)", doc),"1");
    }

}
