package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IsaFuncDeclConv extends DepthFirstAnalysisIsaAdaptor {


    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private final TransAssistantIR t;
    private final AModuleDeclIR vdmToolkitModuleIR;
    private final IRInfo info;
    private final Map<String, AFuncDeclIR> isaFuncDeclIRMap;
    
    public IsaFuncDeclConv(IRInfo info, TransAssistantIR t, AModuleDeclIR vdmToolkitModuleIR) {
        this.t = t;
        this.info = info;
        this.vdmToolkitModuleIR = vdmToolkitModuleIR;

        this.isaTypeDeclIRMap = this.vdmToolkitModuleIR.getDecls()
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
    public void caseAFuncDeclIR(AFuncDeclIR x) {
//    	AIdentifierVarExpIR d = (AIdentifierVarExpIR) ((AApplyExpIR) (x.getBody())).getRoot();
//    	System.out.println(d.getType().getClass());	//root node type of aapplyexpir is a method type IR	
    	//root node type of aapplyexpir is a method type IR	as is args a list of them
    	
    	
//    	AIdentifierVarExpIR d = (AIdentifierVarExpIR) ((AApplyExpIR) (x.getBody())).getArgs().get(0);
//    	System.out.println(d.getType());	//root node type of aapplyexpir is a method type IR	
    		
    			
    	// If no parameter function set params to null to make this more concrete for velocity
    	if (x.getFormalParams().size() == 0) 
    	{
    		x.getMethodType().setParams(null);
    	}
    	
    	formatIdentifierPatternVars(x);
    	
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
    	
    	transformPostConditions(x);
    }
    /*
    UNDER CONSTRUCTION, first draft.
    TODO A lot, int invariant, clean up the substring search to find the invariants if possible, feels a bit hacky,
    also fix the foreach loop to be able to handle more than one parameter, unfortunately this will have to be done recursively
    so that we can keep adding more and clauses. Finally figure out a way to get this post condition into the AST,
    confused as to why --Got-- is empty after running this method even though postCond sysout shows everything with postCond is correct.*/
    //dig around the ast to generate post condition invariant checks
    private void transformPostConditions (AFuncDeclIR x) {
    	// Transform post conditions
    	if (x.getPostCond() != null) 
    	{
    		AFuncDeclIR postCond = (AFuncDeclIR) x.getPostCond();
    		formatIdentifierPatternVars(postCond);
    		
    		AAndBoolBinaryExpIR mcp = new AAndBoolBinaryExpIR();
    		mcp.setLeft(postCond.getBody());//Provided post condition
    		
    		//set the body as an and expression of one being the provided post condition and one being the apply expression
    		AApplyExpIR aa = new AApplyExpIR();
    		postCond.getFormalParams().forEach
    		
    		( p -> 
    		
    		{//TODO where is int invariant? sysout isaFuncDeclIRMap and see no isa_VDMInt
    			if (!p.getType().getNamedInvType().getName().getName().contains("Int")) 
    			{
    				//set the body as an and expression of one being the provided post condition and one being the apply expression
    	    		
    				
    				AIdentifierVarExpIR root = new AIdentifierVarExpIR();
    				root.setName("isa_inv"+p.getType().getNamedInvType()
	    					.getName().getName().substring(4));//TODO substring a bit hacky?
    				root.setType(isaFuncDeclIRMap.get("isa_inv"+p.getType().getNamedInvType()
	    					.getName().getName().substring(4)).getMethodType());
    				aa.setRoot(root);
    				
    				List<AIdentifierVarExpIR> args = new LinkedList<AIdentifierVarExpIR>();
    				AIdentifierVarExpIR arg = new AIdentifierVarExpIR();
    				arg.setName(p.getPattern().toString());
    				arg.setType(postCond.getMethodType());
    				args.add(arg);
    				aa.setArgs(args);
    			}
	    			
    		}
	    	);

			System.out.println(aa); //this works! Returns isa_invVDMNat(x )
    		mcp.setRight(aa);//The invariants of param types

    		System.out.println(mcp.getLeft());
    		System.out.println(mcp.getRight());
    		//As does this, returns:  true \<and> isa_invVDMNat(x ) - TODO why not being translated??
    		
    		postCond.setBody(mcp);
    		System.out.println(postCond);//This has been set up correctly by the method
    		x.setPostCond(postCond);
    		// Insert into AST 
    		System.out.println(x);
            AModuleDeclIR encModule = x.getAncestor(AModuleDeclIR.class);
            if(encModule != null)
            {
                encModule.getDecls().add(postCond);
            }

            System.out.println("Post condition has been added");
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
