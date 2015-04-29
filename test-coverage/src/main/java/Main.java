package main.java;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;
import org.overture.test.framework.BaseTestCase;

import java.io.File;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_PP;
		Value test = InterpreterUtil.interpret(Dialect.VDM_PP, "new Test().Run(2,1,true,false)", new File("test-coverage/src/main/resources/test.vdmpp".replace('/', File.separatorChar)),true);
        System.out.println("The interpreter executed test with the result: "
                + test);
		Interpreter interpreter = Interpreter.getInstance();
		File coverageFolder = new File("test-coverage/test/target/vdm-coverage".replace('/', File.separatorChar));
		coverageFolder.mkdirs();

        if (interpreter instanceof ClassInterpreter)
        {
            ClassInterpreter ci = (ClassInterpreter) interpreter;

            for (SClassDefinition cdef : ci.getClasses())
            {
                cdef.apply(new DepthFirstAnalysisAdaptor()
                {
                    @Override
                    public void caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
                            throws AnalysisException
                    {
                        PrettyPrinterVisitor ppv = new PrettyPrinterVisitor();
                        node.apply(ppv,new PrettyPrinterEnv());
                    }


                });
            }
        }
		// write out current coverage data as produced in the Overture IDE
		DBGPReaderV2.writeCoverage(interpreter, coverageFolder);
        System.exit(0);
	}

}
