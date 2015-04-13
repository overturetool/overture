package main.java;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

import java.io.File;
import java.util.HashMap;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_PP;
		Value test = InterpreterUtil.interpret(Dialect.VDM_PP, "new Test().Run(1,2,true,false)", new File("teste/src/main/resources/test.vdmpp".replace('/', File.separatorChar)));
        System.out.println("The interpreter executed test1 with the result: "
                + test);
		Interpreter interpreter = Interpreter.getInstance();
		File coverageFolder = new File("teste/test/target/vdm-coverage".replace('/', File.separatorChar));
		coverageFolder.mkdirs();
		
		// write out current coverage data as produced in the Overture IDE
		DBGPReaderV2.writeCoverage(interpreter, coverageFolder);
        System.exit(0);
	}

}
