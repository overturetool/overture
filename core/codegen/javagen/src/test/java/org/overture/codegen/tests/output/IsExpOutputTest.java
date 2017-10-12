package org.overture.codegen.tests.output;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.codegen.tests.output.base.JavaOutputTestBase;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.core.testing.PathsProvider;

import java.io.File;
import java.util.Collection;

@RunWith(Parameterized.class)
public class IsExpOutputTest extends JavaOutputTestBase {

    public static final String ROOT = "src" + File.separatorChar + "test"
            + File.separatorChar + "resources" + File.separatorChar
            + "is_expressions";

    public IsExpOutputTest(String nameParameter, String inputParameter,
                                String resultParameter)
    {
        super(nameParameter, inputParameter, resultParameter);
    }

    @Parameterized.Parameters(name = "{index} : {0}")
    public static Collection<Object[]> testData()
    {
        return PathsProvider.computePaths(ROOT);
    }

    @Override
    protected String getUpdatePropertyString()
    {
        return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "isexp";
    }
}
