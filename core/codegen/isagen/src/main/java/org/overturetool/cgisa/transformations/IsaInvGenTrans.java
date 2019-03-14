package org.overturetool.cgisa.transformations;

import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overturetool.cgisa.utils.IsaInvNameFinder;

import java.util.Map;
import java.util.stream.Collectors;


public class IsaInvGenTrans extends DepthFirstAnalysisIsaAdaptor {

    private final AModuleDeclIR vdmToolkitModule;
    private final Map<String, ATypeDeclIR> isaTypeDeclIRMap;
    private IRInfo info;
    private final Map<String, AFuncDeclIR> isaFuncDeclIRMap;

    public IsaInvGenTrans(IRInfo info, AModuleDeclIR vdmToolkitModuleIR) {
        this.info = info;
        this.vdmToolkitModule = vdmToolkitModuleIR;

        this.isaFuncDeclIRMap = this.vdmToolkitModule.getDecls().stream().filter(d ->
        {
            if (d instanceof AFuncDeclIR)
                return true;
            else
                return false;
        }).map(d -> (AFuncDeclIR) d).collect(Collectors.toMap(x -> x.getName(), x -> x));

        this.isaTypeDeclIRMap = this.vdmToolkitModule.getDecls().stream().filter(d -> {
            if (d instanceof ATypeDeclIR)
                return true;
            else
                return false;
        }).map(d -> (ATypeDeclIR) d).collect(Collectors.toMap(x -> ((ANamedTypeDeclIR) x.getDecl()).getName().getName(), x -> x));


    }

    @Override
    public void caseATypeDeclIR(ATypeDeclIR node) throws AnalysisException {
        super.caseATypeDeclIR(node);

        String typeName = IsaInvNameFinder.findName(node.getDecl());
        SDeclIR decl = node.getDecl();
        SDeclIR invFun = node.getInv();

        if(invFun == null)
        {
            // Invariant function
            AFuncDeclIR invFun_ = new AFuncDeclIR();
            invFun_.setName("inv_" + typeName);

            // Define the type signature
            //TODO: Type should be XTypeInt - correct?
            AMethodTypeIR methodType = new AMethodTypeIR();
            STypeIR t = IsaDeclTypeGen.apply(node.getDecl());
            methodType.getParams().add(t.clone());
            methodType.setResult(new ABoolBasicTypeIR());
            invFun_.setMethodType(methodType);

            // Generate the pattern
            //TODO: Pattern should have type XTypeInt - correct?
            AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
            identifierPattern.setName("x");
            AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
            afp.setPattern(identifierPattern);
            afp.setType(t.clone()); // Wrong to set entire methodType?
            invFun_.getFormalParams().add(afp);

            // Generate the expression
            SExpIR expr = IsaInvExpGen.apply(decl, identifierPattern, methodType.clone(), isaFuncDeclIRMap);
            invFun_.setBody(expr);

            // Insert into AST
            AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
            if(encModule != null)
            {
                encModule.getDecls().add(invFun_);
            }

            System.out.println("Invariant function has been added");

        }
    }

    public String GenInvTypeDefinition(String arg){
        return "Definition\n" +
                "   inv_" + arg+ " :: \"" + arg + " \\<Rightarrow> \\<bool>\"\n" +
                "   where\n" +
                "";
    }

}
