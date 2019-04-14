package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overturetool.cgisa.utils.IsaInvNameFinder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IsaFuncDeclConv extends DepthFirstAnalysisIsaAdaptor {


    private final AModuleDeclIR vdmToolkitModuleIR;
    private final Map<String, AFuncDeclIR> isaFuncDeclIRMap;
    
    public IsaFuncDeclConv(IRInfo info, TransAssistantIR t, AModuleDeclIR vdmToolkitModuleIR) {
        this.vdmToolkitModuleIR = vdmToolkitModuleIR;

        this.vdmToolkitModuleIR.getDecls()
                .stream()
                .filter(d -> {
                    if (d instanceof ATypeDeclIR)
                        return true;
                    else
                        return false;
                }).map(d -> (ATypeDeclIR) d)
                .collect(Collectors.toMap(x -> ((ANamedTypeDeclIR) x.getDecl()).getName().getName(), x -> x));
        
        this.isaFuncDeclIRMap = this.vdmToolkitModuleIR.getDecls().stream().filter(d ->
        {
            if (d instanceof AFuncDeclIR)
                return true;
            else
                return false;
        }).map(d -> (AFuncDeclIR) d).collect(Collectors.toMap(x -> x.getName(), x -> x));

        
    }
    
   
    // Transform AFuncDeclIR
    @Override
    public void caseAFuncDeclIR(AFuncDeclIR x) throws AnalysisException {
    	super.caseAFuncDeclIR(x);
    	
    	
    	transformPreConditions(x);
    	
    	transformPostConditions(x);
    			
    	// If no parameter function set params to null to make this more concrete for velocity
    	if (x.getFormalParams().size() == 0) 
    	{
    		x.getMethodType().setParams(null);
    	}
    	
    	formatIdentifierPatternVars(x);
    	
    }
    
    
    
    
    private void transformPostConditions (AFuncDeclIR node) throws AnalysisException {
    	AMethodTypeIR mt = node.getMethodType().clone();
    	// Post condition function
        AFuncDeclIR postCond = new AFuncDeclIR();
        // Set post_[function name] as post function name
        postCond.setName("post_" + node.getName()); 
        
    	// Set up method type for post condition
        AMethodTypeIR type = new AMethodTypeIR();
        type.setResult(new ABoolBasicTypeIR());
        List<STypeIR> params = mt.getParams();
        params.add(mt.getResult());
		type.setParams(params);
        postCond.setMethodType(type);
        
        
        // Provide post condition for functions without them
        AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
        identifierPattern.setName("");
        if (node.getFormalParams() != null && !node.getFormalParams().isEmpty())
        {
        	for (int i = 0; i < node.getFormalParams().size(); i++)
        	{
        		identifierPattern = new AIdentifierPatternIR();
    	        identifierPattern.setName(node.getFormalParams().get(i).getPattern().toString());
		        AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
		        afp.setPattern(identifierPattern);
		        afp.setType(node.getFormalParams().get(i).getType()); 
		        postCond.getFormalParams().add(afp);
        	}
        }
        
        // Add RESULT pattern
        if (mt.getResult() != null)
        {
	        identifierPattern = new AIdentifierPatternIR();
	        identifierPattern.setName("RESULT");
	        AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
	        afp.setPattern(identifierPattern);
	        afp.setType(mt.getResult()); 
	        postCond.getFormalParams().add(afp);
        }
		//an and expression of all of the parameter invariants
        SExpIR expr = IsaInvExpGen.apply(postCond.clone(), identifierPattern, postCond.getMethodType().clone(), isaFuncDeclIRMap);
        postCond.setBody(expr);
        
     	// Translation for already written post conditions
        if (node.getPostCond() != null)
        {
        	//No need to add formal params again they're all already put there above
        	AFuncDeclIR postCond_ = (AFuncDeclIR) node.getPostCond();
        	
        	AAndBoolBinaryExpIR andExisting = new AAndBoolBinaryExpIR();
        	andExisting.setLeft(expr);
        	andExisting.setRight(postCond_.getBody());
        	postCond.setBody(andExisting);
        } 
     	
        
        formatIdentifierPatternVars(postCond);
        // Insert into AST
        AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
        if(encModule != null)
        {
            encModule.getDecls().add(postCond);
        }

        System.out.println("Post condition has been added");

   
    }
    
  
    
    private void transformPreConditions(AFuncDeclIR x) {
    	// Transform pre conditions
    	if (x.getPreCond() != null) {
    		AFuncDeclIR preCond = (AFuncDeclIR) x.getPreCond();
    		formatIdentifierPatternVars(preCond);
    		// Insert into AST 
            AModuleDeclIR encModule = x.getAncestor(AModuleDeclIR.class);
            if(encModule != null)
            {
                encModule.getDecls().add(preCond);
            }

            System.out.println("Pre condition has been added");
    	}
    }
    
    private AFuncDeclIR formatIdentifierPatternVars (AFuncDeclIR node) {
    	/*	This puts a space between different parameters in the Isabelle function body
    	, xy is misinterpreted as one variable whereas x y is correctly interpreted as two
    	*/
    	node.getFormalParams().forEach
    	(		
    			p -> { 
    				
    				AIdentifierPatternIR ip = new AIdentifierPatternIR();
    				ip.setName(p.getPattern().toString() + " ");
    				p.setPattern(ip);
    				
    			}
    	);
    	
    	return node;
    }
    
    
    
    
    
}
