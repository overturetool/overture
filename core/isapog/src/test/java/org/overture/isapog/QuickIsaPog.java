package org.overture.isapog;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.testing.ParseTcFacade;

public class QuickIsaPog {
    String modelPath = "src/test/resources/adhoc/isapog.vdmsl";
    String thysPath = "src/test/resources/adhoc/";

    @Test
    public void fileWriteTest() throws IOException, AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        List<INode> ast = ParseTcFacade.typedAst(modelPath, "AdHoc");
        IsaPog isapo = new IsaPog(ast);

        if (isapo.hasErrors()) {
            fail(isapo.getErrorMessage());
        }

        isapo.writeThyFiles(thysPath);
    }

    @Test
    public void printTest() throws IOException, AnalysisException,
            org.overture.codegen.ir.analysis.AnalysisException {
        List<INode> ast = ParseTcFacade.typedAst(modelPath, "AdHoc");

        IsaPog isapo = new IsaPog(ast);

        if (isapo.hasErrors()) {
            fail(isapo.getErrorMessage());
        }

        System.out.println("(** Model THY **)");
        System.out.println(isapo.getModelThyString());
        System.out.println();
        System.out.println("(** POs THY **)");
        System.out.println(isapo.getPosThyString());

    }

}
