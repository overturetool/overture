package org.overturetool.cgisa.utils;

import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.types.ARecordTypeIR;

public class IsaInvNameFinder extends AnswerIsaAdaptor<String>
{
    public static String findName(INode node) throws AnalysisException {
        IsaInvNameFinder finder = new IsaInvNameFinder();
        return node.apply(finder);
    }

    @Override
    public String caseANamedTypeDeclIR(ANamedTypeDeclIR node) throws AnalysisException {
        return node.getName().getName();
    }

    @Override
    public String caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException {
        return node.getName();
    }

    @Override
    public String createNewReturnValue(INode node) throws AnalysisException {
        return null;
    }

    @Override
    public String createNewReturnValue(Object node) throws AnalysisException {
        return null;
    }
}
