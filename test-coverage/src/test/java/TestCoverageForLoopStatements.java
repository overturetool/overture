package test.java;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.BaseTestCase;

import java.io.File;

public class TestCoverageForLoopStatements extends BaseTestCase {

    @Override
    public void test() throws Exception {
        Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_PP;
        Value test = InterpreterUtil.interpret(Dialect.VDM_PP, "new TestFor().Remove([1,2,3,4],4)", new File("src/test/resources/test_for_loop_statements.vdmpp".replace('/', File.separatorChar)), true);
        System.out.println("The interpreter executed test with the result: "
                + test);
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("test/target/vdmpp-coverage/if-statement".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeCoverage(interpreter, coverageFolder);
    }
}
