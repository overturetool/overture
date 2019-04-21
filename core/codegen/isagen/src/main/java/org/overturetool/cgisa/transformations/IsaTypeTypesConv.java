package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overturetool.cgisa.IsaGen;

import java.util.Map;
import java.util.stream.Collectors;

/***
 * Visitor to convert sequence or set VDM types to VDMToolkit types
 */
public class IsaTypeTypesConv extends DepthFirstAnalysisIsaAdaptor {

    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private final TransAssistantIR t;
    private final AModuleDeclIR vdmToolkitModuleIR;
    private final IRInfo info;

    private final static String isa_VDMSet = "isa_VDMSet";

    private final static String isa_VDMSeq = "isa_VDMSeq";

    public IsaTypeTypesConv(IRInfo info, TransAssistantIR t, AModuleDeclIR vdmToolkitModuleIR) {
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
    
   //transform seq into VDMSeq
    public void caseASeqSeqTypeIR(ASeqSeqTypeIR x) {
    	if(x.getNamedInvType() == null)
        {
            
            // Retrieve isa_VDMSeq from VDMToolkit
            ATypeDeclIR isa_td = isaTypeDeclIRMap.get(IsaTypeTypesConv.isa_VDMSeq);

            x.setNamedInvType((ANamedTypeDeclIR)isa_td.getDecl().clone());
            
        }
    }
  //transform set into VDMSet
    public void caseASetSetTypeIR(ASetSetTypeIR x) {
    	if(x.getNamedInvType() == null)
        {
            
            // Retrieve isa_VDMSet from VDMToolkit
            ATypeDeclIR isa_td = isaTypeDeclIRMap.get(IsaTypeTypesConv.isa_VDMSet);

            x.setNamedInvType((ANamedTypeDeclIR)isa_td.getDecl().clone());
        }
    }
    
    
    
    
}
