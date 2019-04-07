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
        STypeIR t = node.getType();
        
        AApplyExpIR completeExp = new AApplyExpIR();
        AApplyExpIR typeExp = new AApplyExpIR();
    	if (t instanceof ASeqSeqTypeIR) 
    	{
    		//do stuff...
    	}
    	else if (t instanceof ASetSetTypeIR) 
    	{
    		ASetSetTypeIR set = (ASetSetTypeIR) t.clone();
    		
    		AIdentifierVarExpIR setOfInv = buildInvForType(set.getSetOf().getNamedInvType().clone());
    		AIdentifierVarExpIR setInv = buildInvForType(set.getNamedInvType().clone());
    		// Create type types invariant apply to expression e.g isa_VDMSet isa_VDMSeq isaVDMNat1
            typeExp = new AApplyExpIR();
            typeExp.setType(new ABoolBasicTypeIR());
            typeExp.getArgs().add(setOfInv);
            typeExp.setRoot(setInv);
            
            // Crete apply to the inv_ expr e.g inv_x inv_y
            AIdentifierVarExpIR invExp = new AIdentifierVarExpIR();
            invExp.setName("inv_"+node.getName());
            invExp.setType(this.methodType);
            
            //string together in one bug apply exp
            completeExp = new AApplyExpIR();
            completeExp.setType(new ABoolBasicTypeIR());
            completeExp.getArgs().add(invExp);
            completeExp.setRoot(typeExp);
    	}
       
        
        
        return completeExp;
    }
    
     
    
    
    private AIdentifierVarExpIR buildInvForType(ANamedTypeDeclIR invariantNode) throws AnalysisException {
    	String typeName = IsaInvNameFinder.findName(invariantNode).substring(4);
    	AFuncDeclIR fInv = this.isaFuncDeclIRMap.get("isa_inv"+typeName);
         // Create ref to function
        AIdentifierVarExpIR fInvIdentifier = new AIdentifierVarExpIR();
        fInvIdentifier.setName(fInv.getName());
        fInvIdentifier.setSourceNode(fInv.getSourceNode());
        fInvIdentifier.setType(fInv.getMethodType().clone());//Must always clone
        
		return fInvIdentifier;
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
