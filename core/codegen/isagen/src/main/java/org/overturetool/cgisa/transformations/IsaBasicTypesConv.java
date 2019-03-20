package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.Map;
import java.util.stream.Collectors;

/***
 * Visitor to convert basic VDM types to VDMToolkit types
 */
public class IsaBasicTypesConv extends DepthFirstAnalysisIsaAdaptor {

    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private final TransAssistantIR t;
    private final AModuleDeclIR vdmToolkitModuleIR;
    private final IRInfo info;
    private final static String isa_VDMInt = "isa_VDMInt";
    private final static String isa_VDMNat1 = "isa_VDMNat1";

    public IsaBasicTypesConv(IRInfo info, TransAssistantIR t, AModuleDeclIR vdmToolkitModuleIR) {
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

    //Transform int to isa_VDMInt
    public void caseAIntNumericBasicTypeIR(AIntNumericBasicTypeIR x){
        if(x.getNamedInvType() == null)
        {
            AIntNumericBasicTypeIR a = new AIntNumericBasicTypeIR();
            // Retrieve isa_VDMInt from VDMToolkit
            ATypeDeclIR isa_td = isaTypeDeclIRMap.get(this.isa_VDMInt);

            x.setNamedInvType((ANamedTypeDeclIR)isa_td.getDecl().clone());
        }

    }
    //transform nat1 to isa_VDMNat1
    public void caseANat1NumericBasicTypeIR(ANat1NumericBasicTypeIR t){
        if(t.getNamedInvType() == null)
        {
            ANat1NumericBasicTypeIR a = new ANat1NumericBasicTypeIR();
            // Retrieve isa_VDMInt from VDMToolkit
            ATypeDeclIR isa_td = isaTypeDeclIRMap.get(this.isa_VDMNat1);

            t.setNamedInvType((ANamedTypeDeclIR)isa_td.getDecl().clone());
            System.out.println(t.getNamedInvType().toString());
        }

    }
    
    
}
