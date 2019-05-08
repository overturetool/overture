package org.overturetool.cgisa.transformations;
import org.overture.ast.expressions.AApplyExp;
import org.overture.cgisa.isair.analysis.AnswerIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.*;
import org.overturetool.cgisa.IsaGen;
import org.overturetool.cgisa.utils.IsaInvNameFinder;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.crypto.NodeSetData;

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
	private AIdentifierVarExpIR targetIP;
	private final LinkedList<ANamedTypeDeclIR> invArr = new LinkedList<ANamedTypeDeclIR>();


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
        node.getType();
        //TODO make for different types invariants
        // Find invariant function
        AFuncDeclIR fInv = this.isaFuncDeclIRMap.get("isa_invTrue");
        // Create ref to function
        AIdentifierVarExpIR fInvIdentifier = new AIdentifierVarExpIR();
        fInvIdentifier.setName(fInv.getName());
        fInvIdentifier.setSourceNode(fInv.getSourceNode());
        fInvIdentifier.setType(fInv.getMethodType().clone());

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
    public SExpIR caseAStateDeclIR(AStateDeclIR node) throws AnalysisException {
    	//TODO e.g. where "inv_recType r \<equiv> isa_invVDMSeq isa_invVDMNat1 (x r) etc. 
    	LinkedList<AFieldDeclIR> fields = new LinkedList<AFieldDeclIR>();
		node.getFields().forEach(f -> fields.add(f.clone()));
        AApplyExpIR completeExp = new AApplyExpIR();
        LinkedList<AApplyExpIR> fieldInvariants = new LinkedList<AApplyExpIR>();
        
        for (int i = 0; i < fields.size(); i++) 
	        {
        		STypeIR type = fields.get(i).getType();
		    	AIdentifierVarExpIR invExp = new AIdentifierVarExpIR();
	            invExp.setName("("+node.getName().substring(0,1).toLowerCase()+
	            		node.getName().toString().substring(1, node.getName().toString().length())+"_"+
	            		fields.get(i).getName()+" "+this.ps.toString()+")");
	            invExp.setType(this.methodType.clone());
	            this.targetIP = invExp;
				
	            completeExp.setType(new ABoolBasicTypeIR());
	            //Recursively build curried inv function e.g.  (inv_VDMSet (inv_VDMSet inv_Nat1)) inv_x
	           
	            try {
					fieldInvariants.add(buildInvForType(type.clone()));
				} catch (AnalysisException e) {
					e.printStackTrace();
				}
		    	
	        }
    	
     // Link numerous apply expressions together in an and expression
        if (fieldInvariants.size() >= 2)
        	return genAnd(fieldInvariants).clone();
        else
        // Just one field return it as an apply expression
        	return fieldInvariants.get(0).clone();
    }
    
    
    
    
    
    
    @Override
    public SExpIR caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException {
    	//TODO e.g. where "inv_recType r \<equiv> isa_invVDMSeq isa_invVDMNat1 (x r) etc. 
    	LinkedList<AFieldDeclIR> fields = new LinkedList<AFieldDeclIR>();
		node.getFields().forEach(f -> fields.add(f.clone()));
        AApplyExpIR completeExp = new AApplyExpIR();
        LinkedList<AApplyExpIR> fieldInvariants = new LinkedList<AApplyExpIR>();
        
        for (int i = 0; i < fields.size(); i++) 
	        {
        		STypeIR type = fields.get(i).getType();
		    	AIdentifierVarExpIR invExp = new AIdentifierVarExpIR();
	            invExp.setName("("+node.getName().substring(0,1).toLowerCase()+
	            		node.getName().toString().substring(1, node.getName().toString().length())+"_"+
	            		fields.get(i).getName()+" "+this.ps.toString()+")");
	            invExp.setType(this.methodType.clone());
	            this.targetIP = invExp;
				
	            completeExp.setType(new ABoolBasicTypeIR());
	            //Recursively build curried inv function e.g.  (inv_VDMSet (inv_VDMSet inv_Nat1)) inv_x
	           
	            try {
					fieldInvariants.add(buildInvForType(type.clone()));
				} catch (AnalysisException e) {
					e.printStackTrace();
				}
		    	
	        }
    	
     // Link numerous apply expressions together in an and expression
        if (fieldInvariants.size() >= 2)
        	return genAnd(fieldInvariants);
        else
        // Just one field return it as an apply expression
        	return fieldInvariants.get(0);
    }
    
    
    
    @Override
    public SExpIR caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        STypeIR t = node.getType().clone();
        AApplyExpIR completeExp = new AApplyExpIR();
        // Crete apply to the inv_ expr e.g inv_x inv_y
        AIdentifierVarExpIR invExp = new AIdentifierVarExpIR();
        invExp.setName(node.getName());
        invExp.setType(this.methodType.clone());
        this.targetIP = invExp;
		
        completeExp.setType(new ABoolBasicTypeIR());
        //Recursively build curried inv function e.g.  (inv_VDMSet (inv_VDMSet inv_Nat1)) inv_x
       
		completeExp = buildInvForType(t);
			
    	
    	
		return completeExp;
    }
    
    
    @Override
    public SExpIR caseAFuncDeclIR(AFuncDeclIR node) throws AnalysisException {
        LinkedList<AFormalParamLocalParamIR> t = node.getFormalParams();
        node.setMethodType(this.methodType);
        LinkedList<AApplyExpIR> paramInvariants = new LinkedList<AApplyExpIR>();
        
        for (int i = 0; i < node.getFormalParams().size(); i++) {
        	STypeIR type = t.get(i).getType();      
        	AApplyExpIR completeExp = new AApplyExpIR();
        	
        	// Create apply to the inv_ expr e.g inv_x inv_y
            AIdentifierVarExpIR invExp = new AIdentifierVarExpIR();
            invExp.setName(node.getFormalParams().get(i).getPattern().toString());
            invExp.setType(this.methodType.clone());
            this.targetIP = invExp;
			
            completeExp.setType(new ABoolBasicTypeIR());
            //Recursively build curried inv function e.g.  (inv_VDMSet (inv_VDMSet inv_Nat1)) inv_x
           
			try {
				completeExp = buildInvForType(type);
			} catch (AnalysisException e) {
				e.printStackTrace();
			}
    	
			paramInvariants.add(completeExp);
			        
        		
        }
        
        
        // Link numerous apply expressions together in an and expression
        if (paramInvariants.size() >= 2)
        	return genAnd(paramInvariants);
        else
        // Just one parameter return it as an apply expression
        	return paramInvariants.get(0);
        
        
    	
    }
     
    
    private SExpIR genAnd(LinkedList<AApplyExpIR> paramInvariants) {
    	
    	AAndBoolBinaryExpIR and = new AAndBoolBinaryExpIR();
    	
    	//base case
		if (paramInvariants.size() == 2)
	    	{
				and.setLeft(paramInvariants.get(0));
				and.setRight(paramInvariants.get(1));
	    	}
		else
			{
				and.setLeft(paramInvariants.get(0));
				paramInvariants.remove(0);
				and.setRight( genAnd(paramInvariants) );
			}
		return and;
		
	}

	//build curried invariant
    public AApplyExpIR buildInvForType(STypeIR seqtNode) throws AnalysisException {
    	
    	String typeName = IsaInvNameFinder.findName(seqtNode);
    	
    	AFuncDeclIR fInv;
    	if (this.isaFuncDeclIRMap.get("isa_inv"+typeName) != null)
    	{
    		fInv = this.isaFuncDeclIRMap.get("isa_inv"+typeName).clone();
    	}
    	else
    	{
    		fInv = IsaGen.funcGenHistoryMap.get("inv_"+typeName).clone();
    		
    	}
    	if (fInv.getMethodType().clone() == null)
    	{
    		AMethodTypeIR mt = new AMethodTypeIR();
    		mt.setResult(new ABoolBasicTypeIR());
    		mt.getParams().add(seqtNode.clone());
    		fInv.setMethodType(mt.clone());
    	}
    	
         // Create ref to function
        AIdentifierVarExpIR curriedInv = new AIdentifierVarExpIR();
        curriedInv.setName(fInv.getName());
        curriedInv.setSourceNode(fInv.getSourceNode());
        curriedInv.setType(fInv.getMethodType().clone());//Must always clone
    	AApplyExpIR accum = new AApplyExpIR();
    	accum.setRoot(curriedInv);
    	
    	
    	//if this type is not the last in the nested types, then keep rescursing until we get to the final nested type
    	if ( seqtNode instanceof ASetSetTypeIR && ((ASetSetTypeIR) seqtNode).getSetOf() != null )
    	{
    		accum.getArgs().add(buildInvForType(((ASetSetTypeIR) seqtNode).getSetOf().clone()));
    	}
    	else if (seqtNode instanceof ASeqSeqTypeIR && ((ASeqSeqTypeIR) seqtNode).getSeqOf() != null)
    	{
    		
    		accum.getArgs().add(buildInvForType(((ASeqSeqTypeIR) seqtNode).getSeqOf().clone()));
    	}
    	else
    	{
    		accum.getArgs().add(targetIP);
    	}
    	return accum;
        
	}


    @Override
    public SExpIR createNewReturnValue(INode node) throws AnalysisException {
        return null;
    }

    @Override
    public SExpIR createNewReturnValue(Object node) throws AnalysisException {
            return null;
    }

    public SExpIR caseATokenBasicTypeIR(ATokenBasicTypeIR n) throws AnalysisException
    {

        new AApplyExp();

        throw new AnalysisException();

    }


    public SExpIR caseASetSetTypeIR(ASetSetTypeIR node) throws AnalysisException {
        throw new AnalysisException();

    }


}
