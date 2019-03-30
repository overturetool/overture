package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import java.util.Map;
import java.util.stream.Collectors;

public class IsaFuncDeclConv extends DepthFirstAnalysisIsaAdaptor {


    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private final TransAssistantIR t;
    private final AModuleDeclIR vdmToolkitModuleIR;
    private final IRInfo info;

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
    }
    
   // Transform AFuncDeclIR
    public void caseAFuncDeclIR(AFuncDeclIR x) {
    	// If no parameter function set params to null to make this more concrete for velocity
    	if (x.getFormalParams().size() == 0) 
    	{
    		x.getMethodType().setParams(null);
    	}
    	
       /*	This puts a space between different parameters in the Isabelle function body
    	, xy is misinterpreted as one variable whereas x y is correctly interpreted as two
    	*/
    	x.getFormalParams().forEach
    	(		
    			p -> { 
    				
    				AIdentifierPatternIR ip = new AIdentifierPatternIR();
    				ip.setName(p.getPattern().toString() + " ");
    				p.setPattern(ip);
    				
    			}
    	);
    }
    
    
    
}
