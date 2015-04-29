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


public class TestCoverageIfStatement extends BaseTestCase {
    @Override
    public void test() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_PP;
        Value test = InterpreterUtil.interpret(Dialect.VDM_PP, "new Test().Run(2,3,4)", new File("src/test/resources/test_if_elseif_statements.vdmpp".replace('/', File.separatorChar)), true);
        System.out.println("The interpreter executed test with the result: "
                + test);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmpp-coverage/if-statement".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);
        assertEquals(test.toString(),"4");

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
