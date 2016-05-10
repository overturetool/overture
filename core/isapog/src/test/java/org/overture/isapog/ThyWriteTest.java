package org.overture.isapog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParseTcFacade;

public class ThyWriteTest {

    private static final String MODEL_PATH = "src/test/resources/thywrite/model.vdmsl";
    private static final String THYS_PATH = "src/test/resources/thywrite/";

    private static final String MODEL_THY = THYS_PATH + "DEFAULT.thy";
    private static final String POS_THY = THYS_PATH + "DEFAULT_POs.thy";

    @Test
    public void fileWriteTest() throws IOException, AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        List<INode> ast = ParseTcFacade.typedAst(MODEL_PATH, "ThyWrite");

        IsaPog isapo = new IsaPog(ast);
        isapo.writeThyFiles(THYS_PATH);

        File modelFile = new File(MODEL_THY);
        File posFile = new File(POS_THY);

        assertNotNull(modelFile);
        assertNotNull(posFile);
        assertTrue(modelFile.exists());
        assertTrue(posFile.exists());
        modelFile.deleteOnExit();
        posFile.deleteOnExit();
    }


}
