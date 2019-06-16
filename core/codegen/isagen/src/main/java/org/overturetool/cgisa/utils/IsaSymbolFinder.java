package org.overturetool.cgisa.utils;

import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;

public class IsaSymbolFinder extends AnswerIsaAdaptor<String>
{
    private static String val;

	public static String findSymbol(INode node, String value) throws AnalysisException {
    	val =  value;
        IsaSymbolFinder finder = new IsaSymbolFinder();
        return node.apply(finder);
    }
 @Override
    public String caseASetSetTypeIR(ASetSetTypeIR node) throws AnalysisException {
        return "{" + val + "}";
    }
    
    @Override
    public String caseASeqSeqTypeIR(ASeqSeqTypeIR node) throws AnalysisException {
    	
    	return "[" + val + "]";
    }
   
    @Override
    public String caseACharBasicTypeIR(ACharBasicTypeIR node) throws AnalysisException {
    	return "\"" + val +  "\"";
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
