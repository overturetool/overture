package org.overture.interpreter.tests.coverage;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.test.framework.BaseTestCase;

public class TestCoverageGenerateTestCases extends BaseTestCase{

	@Override
	public void test() throws Exception {
		Settings.release = Release.VDM_10;
        Settings.dialect = Dialect.VDM_PP;
        InterpreterUtil.interpret(Dialect.VDM_PP, "new Test().Run(2,3,4)", new File("src/test/resources/coverage/test_operators2.vdmpp".replace('/', File.separatorChar)), true);
        
        Interpreter interpreter = Interpreter.getInstance();
        File coverageFolder = new File("src/test/target/vdmpp-coverage/operators".replace('/', File.separatorChar));
        coverageFolder.mkdirs();
        DBGPReaderV2.writeMCDCCoverage(interpreter, coverageFolder);
		
	}

}
