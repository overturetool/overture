package org.overturetool.cgisa.transformations;

import org.overture.ast.expressions.AApplyExp;
import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.*;
import org.overturetool.cgisa.utils.IsaInvNameFinder;

import java.util.Map;

/*
Generates the expression for an invariant.
Example:
    VDM spec:   types
                test = nat
    Invariant expression: isa_inv_VDMNat i
    where i is a parameter to this visitor.

 */
public class IsaInvExpGen extends AnswerIsaAdaptor<SExpIR> {

    AIdentifierPatternIR ps;
    AMethodTypeIR methodType;
    private final Map<String, AFuncDeclIR> isaFuncDeclIRMap;

    public IsaInvExpGen(AIdentifierPatternIR ps, AMethodTypeIR methodType, Map<String, AFuncDeclIR> isaFuncDeclIRMap)
    {
        this.ps = ps;
        this.methodType = methodType;
        this.isaFuncDeclIRMap = isaFuncDeclIRMap;
    }

    public static SExpIR apply(SDeclIR decl, AIdentifierPatternIR afp, AMethodTypeIR methodType, Map<String, AFuncDeclIR> isaFuncDeclIRMap) throws AnalysisException {
        IsaInvExpGen finder = new IsaInvExpGen(afp, methodType, isaFuncDeclIRMap);
        return decl.apply(finder);
    }

    @Override
    public SExpIR caseANamedTypeDeclIR(ANamedTypeDeclIR node) throws AnalysisException {
        STypeIR type = node.getType();
        
        // Find invariant function
        AFuncDeclIR fInv = this.isaFuncDeclIRMap.get("isa_invTrue");
        // Create ref to function
        AIdentifierVarExpIR fInvIdentifier = new AIdentifierVarExpIR();
        fInvIdentifier.setName(fInv.getName());
        fInvIdentifier.setSourceNode(fInv.getSourceNode());
        fInvIdentifier.setType(fInv.getMethodType());

        // Crete apply expr
        AApplyExpIR exp = new AApplyExpIR();
        exp.setType(new ABoolBasicTypeIR());
        AIdentifierVarExpIR iVarExp = new AIdentifierVarExpIR();
        iVarExp.setName(this.ps.getName());
        iVarExp.setType(this.methodType);
        exp.getArgs().add(iVarExp);
        exp.setRoot(fInvIdentifier);

        return exp;
    }

    
    @Override
    public SExpIR caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        STypeIR type = node.getType();
        
        String typeName = IsaInvNameFinder.findName(node.getType().getNamedInvType()).substring(4);
        AFuncDeclIR fInv = this.isaFuncDeclIRMap.get("isa_inv"+typeName);
        
        // Create ref to function
        AIdentifierVarExpIR fInvIdentifier = new AIdentifierVarExpIR();
        fInvIdentifier.setName(fInv.getName());
        fInvIdentifier.setSourceNode(fInv.getSourceNode());
        fInvIdentifier.setType(fInv.getMethodType());
        

        // Crete apply expr
        AApplyExpIR exp = new AApplyExpIR();
        exp.setType(new ABoolBasicTypeIR());
        AIdentifierVarExpIR iVarExp = new AIdentifierVarExpIR();
        iVarExp.setName(this.ps.getName());
        iVarExp.setType(this.methodType);
        exp.getArgs().add(iVarExp);
        exp.setRoot(fInvIdentifier);
        
        return exp;
    }
    
    
    
    
    @Override
    public SExpIR caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException {
        throw new AnalysisException();
    }

    @Override
    public SExpIR createNewReturnValue(INode node) throws AnalysisException {
        return null;
    }

    @Override
    public SExpIR createNewReturnValue(Object node) throws AnalysisException {
            return null;
    }


    public SExpIR caseASeqSeqType(ASeqSeqTypeIR node)
            throws AnalysisException {
        if(node.getSeqOf().getTag()!= null)
        {
            Object t = node.getSeqOf().getTag();

            // We are referring to another type, and therefore we stop here. This is the instantiation of the polymorphic function.
            /*
            For VDM:


             */
            // Return expression corresponding to: isa_invSeqElemens[token](isa_true[token], p)
        }
        else {
            //We need to keep going
        }
        throw new AnalysisException();
    }

    public SExpIR caseATokenBasicTypeIR(ATokenBasicTypeIR n) throws AnalysisException
    {

        AApplyExp e = new AApplyExp();

        throw new AnalysisException();

    }


    public SExpIR caseASetSetTypeIR(ASetSetTypeIR node) throws AnalysisException {
        throw new AnalysisException();

    }


}
