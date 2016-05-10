package org.overture.isapog;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class IsaPogIntegrationTest extends ParamStandardTest<IsaPogResult> {

    private static final String UPDATE_PROPERTY = "tests.update.isapog";
    private static final String ISA_POG_ROOT = "src/test/resources/integration";
    ;

    public IsaPogIntegrationTest(String nameParameter, String inputParameter,
                                 String resultParameter) {
        super(nameParameter, inputParameter, resultParameter);
    }

    @Parameters(name = "{index} : {0}")
    public static Collection<Object[]> testData() {
        return PathsProvider.computePaths(ISA_POG_ROOT);
    }

    @Override
    public IsaPogResult processModel(List<INode> ast) {
        IsaPog ip;
        try {
            ip = new IsaPog(ast);
        } catch (org.overture.codegen.ir.analysis.AnalysisException | AnalysisException e) {
            fail(e.getMessage());
        }
        return new IsaPogResult(ip.getModelThyString(), ip.getPosThyString(), false);
    }

    @Override
    public Type getResultType() {
        Type resultType = new TypeToken<IsaPogResult>() {
        }.getType();
        return resultType;
    }

    @Override
    protected void checkAssumptions() {
        Assume.assumeTrue("Test skipped", false);
    }

    @Override
    protected String getUpdatePropertyString() {
        return UPDATE_PROPERTY;
    }

    @Override
    public void compareResults(IsaPogResult actual, IsaPogResult expected) {
        assertEquals("Negative test mismatch", expected.isNegative(), actual.isNegative());

        assertEquals("Model translation mismatch", expected.getModelthy(), actual.getModelthy());

        assertEquals("PO translation mismatch", expected.getPosthy(), actual.getPosthy());

    }

}
