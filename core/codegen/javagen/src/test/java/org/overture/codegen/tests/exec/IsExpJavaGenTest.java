package org.overture.codegen.tests.exec;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.base.JavaGenTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.ExecutableSpecTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.tests.output.IsExpOutputTest;
import org.overture.config.Release;

import java.io.File;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class IsExpJavaGenTest extends JavaGenTestBase {

    public IsExpJavaGenTest(String name, File vdmSpec,
                                 TestHandler testHandler)
    {
        super(vdmSpec, testHandler);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> getData()
    {
        return collectTests(new File(IsExpOutputTest.ROOT), new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP));
    }

    @Override
    protected String getPropertyId()
    {
        return "isexp";
    }
}
