package org.overturetool.cgisa.transformations;

import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.cgisa.isair.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overturetool.cgisa.utils.IsaDeclTypeGen;
import org.overturetool.cgisa.utils.IsaInvExpGen;
import org.overturetool.cgisa.utils.IsaInvNameFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

            // Define the signature type
            AMethodTypeIR methodType = new AMethodTypeIR();
            STypeIR t = IsaDeclTypeGen.apply(node.getDecl());
            methodType.getParams().add(t.clone());
            methodType.setResult(new ABoolBasicTypeIR());


            AFuncDeclIR invFun_ = new AFuncDeclIR();
            invFun_.setMethodType(methodType);
            invFun_.setName("inv_" + typeName);

            // Generate the pattern
            AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
            AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
            identifierPattern.setName("x");
            afp.setPattern(identifierPattern);
            afp.setType(methodType.clone());
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
