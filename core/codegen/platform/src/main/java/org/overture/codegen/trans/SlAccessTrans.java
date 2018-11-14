package org.overture.codegen.trans;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.config.Settings;

public class SlAccessTrans extends DepthFirstAnalysisAdaptor {

    @Override
    public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException {

        if(Settings.dialect == Dialect.VDM_SL)
        {
            if(!node.getIsConstructor())
            {
                // Pre- and postconditions and invariants are private in the VDM AST -- even when they're being exported.
                // Therefore, we'll need to adjust their access modifiers. To keep things simple we'll just make any
                // method public.
                node.setAccess(IRConstants.PUBLIC);
            }
        }
    }
}
