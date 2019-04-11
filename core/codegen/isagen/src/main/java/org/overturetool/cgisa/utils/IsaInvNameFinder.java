package org.overturetool.cgisa.utils;

import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;

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
    public String caseASetSetTypeIR(ASetSetTypeIR node) throws AnalysisException {
        return "VDMSet";
    }
    
    @Override
    public String caseASeqSeqTypeIR(ASeqSeqTypeIR node) throws AnalysisException {
    	return "VDMSeq";
    }
    @Override
    public String caseANatNumericBasicTypeIR(ANatNumericBasicTypeIR node) throws AnalysisException {
    	return "VDMNat";
    }
    
    @Override
    public String caseAIntNumericBasicTypeIR(AIntNumericBasicTypeIR node) throws AnalysisException {
    	return "VDMInt";
    }
    
    @Override
    public String caseANat1NumericBasicTypeIR(ANat1NumericBasicTypeIR node) throws AnalysisException {
    	return "VDMNat1";
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
