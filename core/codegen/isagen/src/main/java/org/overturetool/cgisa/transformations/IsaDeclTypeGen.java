package org.overturetool.cgisa.transformations;

import java.util.LinkedList;
import java.util.List;

import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;

public class IsaDeclTypeGen extends AnswerIsaAdaptor<STypeIR> {

    public static STypeIR apply(INode node) throws AnalysisException {
        IsaDeclTypeGen finder = new IsaDeclTypeGen();
        return node.apply(finder);
    }

    public STypeIR caseANamedTypeDeclIR(ANamedTypeDeclIR n)
    {
        AIntNumericBasicTypeIR a = new AIntNumericBasicTypeIR();
        a.setNamedInvType(n.clone());
        return a;
    }

    public STypeIR caseAStateDeclIR(AStateDeclIR n)
    {
    	ARecordTypeIR a = new ARecordTypeIR();
    	ATypeNameIR o = new ATypeNameIR();
    	o.setName(n.getName());
    	a.setName(o);
        return a;
    	
    }
    
    public STypeIR caseARecordDeclIR(ARecordDeclIR n)
    {
    	ARecordTypeIR a = new ARecordTypeIR();
    	ATypeNameIR o = new ATypeNameIR();
    	o.setName(n.getName());
    	a.setName(o);
        return a;
    	
    }

    @Override
    public STypeIR createNewReturnValue(INode node) throws AnalysisException {
        return null;
    }

    @Override
    public STypeIR createNewReturnValue(Object node) throws AnalysisException {
        return null;
    }
}
