package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.Map;
import java.util.stream.Collectors;

public class IsaBasicTypesConv extends DepthFirstAnalysisIsaAdaptor {

    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private final TransAssistantIR t;
    private final AModuleDeclIR vdmToolkitModuleIR;
    private final IRInfo info;
    private final static String isa_VDMInt = "isa_VDMInt";

    public IsaBasicTypesConv(IRInfo info, TransAssistantIR t, AModuleDeclIR vdmToolkitModuleIR) {
        this.t = t;
        this.info = info;
        this.vdmToolkitModuleIR = vdmToolkitModuleIR;

        this.isaTypeDeclIRMap = this.vdmToolkitModuleIR.getDecls().stream().filter(d -> {
            if (d instanceof ATypeDeclIR)
                return true;
            else
                return false;
        }).map(d -> (ATypeDeclIR) d).collect(Collectors.toMap(x -> ((ANamedTypeDeclIR) x.getDecl()).getName().getName(), x -> x));
    }

    public void caseAIntNumericBasicTypeIR(AIntNumericBasicTypeIR x){
        if(x.getNamedInvType() == null)
        {
            AIntNumericBasicTypeIR a = new AIntNumericBasicTypeIR();
            // Get isa_VDMInt
            ATypeDeclIR isa_td = isaTypeDeclIRMap.get(this.isa_VDMInt);
            x.setNamedInvType((ANamedTypeDeclIR)isa_td.getDecl().clone());
            //aIntNumericBasicTypeIR.setNamedInvType();
        }

    }
}
