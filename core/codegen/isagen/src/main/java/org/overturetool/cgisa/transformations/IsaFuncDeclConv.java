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
    
    
    
    /*
    UNDER CONSTRUCTION, first draft.
    TODO A lot, int invariant, clean up the substring search to find the invariants if possible, feels a bit hacky,
    also be able to handle more than one parameter, unfortunately this will have to be done recursively
    so that we can keep adding more \<and> clauses. Finally figure out a way to get this post condition into the AST,
    confused as to why --Got-- is empty after running this method even though postCond sysout shows everything with postCond is correct.*/
    //dig around the ast to generate post condition invariant checks
    private void transformPostConditions (AFuncDeclIR xo) {
    	AFuncDeclIR x = xo.clone();
    	
    	// Transform post conditions
    	if (x.getPostCond() != null) 
    	{
    		
    		AFuncDeclIR postCond = (AFuncDeclIR) x.getPostCond();
    		AMethodTypeIR methodType = new AMethodTypeIR(); //post condiiton method type
    		AFormalParamLocalParamIR afplp = new AFormalParamLocalParamIR(); //postCondition formal parameters
	        
    	        
    		AAndBoolBinaryExpIR mcp = new AAndBoolBinaryExpIR();
    		mcp.setLeft(( (AFuncDeclIR) (x.getPostCond()) ).getBody());//Provided post condition
    		
    		//set the body as an and expression of one being the provided post condition and one being the apply expression
    		AApplyExpIR aa = new AApplyExpIR();
    		
    		AIdentifierVarExpIR root = new AIdentifierVarExpIR();
			root.setName("isa_inv"+x.getFormalParams().get(0).getType().getNamedInvType()
					.getName().getName().substring(4));//TODO substring a bit hacky?
			
			//TODO for some reason int does not have a VDMInt invariant in the function mapping
			if(!x.getFormalParams().get(0).getType().getNamedInvType().getName().getName().contains("Int"))
				root.setType(isaFuncDeclIRMap.get("isa_inv"+x.getFormalParams().get(0).getType().getNamedInvType()
						.getName().getName().substring(4)).getMethodType());
			
			aa.setRoot(root);
			
			List<AIdentifierVarExpIR> args = new LinkedList<AIdentifierVarExpIR>();
			AIdentifierVarExpIR arg = new AIdentifierVarExpIR();
			arg.setName("RESULT");
			arg.setType(postCond.getMethodType());
			args.add(arg);
			aa.setArgs(args);
    			

    		
    		mcp.setRight(aa);//The invariants of param types
    		//set post condition method type
    		methodType.getParams().add(x.getFormalParams().get(0).getType());
    		methodType.getParams().add(x.getMethodType().getResult());
    		//methodType.getParams().add(x.getFormalParams().get(1).getType());
 	    	methodType.setResult(new ABoolBasicTypeIR());
    		postCond.setMethodType(methodType);
    		
    		
    		//set formal params
    		
        	AIdentifierPatternIR ip = new AIdentifierPatternIR();
        	
        	
        	//set param pattern
            x.getFormalParams().forEach
            ( 
            		(p) -> 
            		{
            			ip.setName(p.getPattern().toString()); 
	            		afplp.setPattern(ip);
	                    afplp.setType(p.getType());
	                    postCond.getFormalParams().add(afplp);
            		}
            );
            
            //set RESULT patterns
            ip.setName("RESULT");
        	afplp.setPattern(ip);
        	afplp.setType(x.getMethodType().getResult());
            postCond.getFormalParams().add(afplp);
            
            
            
            //Polish postCondition 
 	    	postCond.setBody(mcp);
    		postCond.setAccess(x.getAccess());
    		postCond.setName("post_"+x.getName());
    		formatIdentifierPatternVars(postCond);
    		x.setPostCond(postCond);
    		
    	//Broken? For some reason doing this results in a completely empty --Got-- output in junit
            AModuleDeclIR encModule = x.getAncestor(AModuleDeclIR.class);
            if(encModule != null)
            {
    			//encModule.getDecls().clear();//clearing first will show the post condition in the --Got-- output not sure why
                encModule.getDecls().add(postCond);
                
            }
          
            System.out.println("Post condition has been added");
         
    	}
    	
    	//add clause for if no post condition TODO
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
