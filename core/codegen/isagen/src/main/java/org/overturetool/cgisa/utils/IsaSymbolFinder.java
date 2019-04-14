package org.overturetool.cgisa.utils;

import java.util.Arrays;

import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;

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
