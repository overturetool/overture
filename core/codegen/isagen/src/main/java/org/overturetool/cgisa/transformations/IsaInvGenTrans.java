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
        // Invariant function
        AFuncDeclIR invFun_ = new AFuncDeclIR();
        invFun_.setName("inv_" + typeName); //inv_t

        // Define the type signature
        //TODO: Type should be XTypeInt - correct?
        AMethodTypeIR methodType = new AMethodTypeIR();
        STypeIR t = IsaDeclTypeGen.apply(node.getDecl());
        
        //TODO How on earth to get rid of NPE here??
        methodType.getParams().add(t.clone());
    	methodType.setResult(new ABoolBasicTypeIR());
        invFun_.setMethodType(methodType);
       
        
        

        // Translation for VDMToolkit and modeller written invariants
        if (invFun != null)
        {
        	AFuncDeclIR inv = (AFuncDeclIR) invFun;//cast invariant function declaration to AFuncDeclIR
        	AAndBoolBinaryExpIR multipleInvs = new AAndBoolBinaryExpIR();
        	
        	AFormalParamLocalParamIR afplp = new AFormalParamLocalParamIR();
            afplp.setPattern(setIdentifierPattern(inv, t));
            afplp.setType(t.clone());
            invFun_.getFormalParams().add(afplp);
            
        	multipleInvs.setRight(inv.getBody());
        	
        	SExpIR expr = IsaInvExpGen.apply(decl, setIdentifierPattern(inv, t), methodType.clone(), isaFuncDeclIRMap);
        	multipleInvs.setLeft(expr);
        	
        	invFun_.setBody(multipleInvs);
        } 
        //translation for no inv types TODO not sure if to translate across type invs like: isa_invVDMInt for VDMInt types
        else 
        {
        	// Generate the pattern
            //TODO: Pattern should have type XTypeInt - correct?
            AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
            identifierPattern.setName("x ");
            AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
            afp.setPattern(identifierPattern);
            afp.setType(t.clone()); // Wrong to set entire methodType?
            invFun_.getFormalParams().add(afp);
        	SExpIR expr = IsaInvExpGen.apply(decl, identifierPattern, methodType.clone(), isaFuncDeclIRMap);
        	invFun_.setBody(expr);
        }
        

        // Insert into AST
        AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
        if(encModule != null)
        {
            encModule.getDecls().add(invFun_);
        }

        System.out.println("Invariant function has been added");

   
    
    }
    
    
    @Override
    public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        super.caseAFieldDeclIR(node);
        
        
       // AFieldDeclIR node = n.clone();//cloning the node adds some things to the AST just not the inv we want!
        STypeIR t = node.getType();
        if (t.getNamedInvType() != null) {
        	
        	
            // Invariant function
            AFuncDeclIR invFun_ = new AFuncDeclIR();
            invFun_.setName("inv_" + node.getName());
            
            System.out.println(invFun_.getChildren(true));
            AMethodTypeIR mt = new AMethodTypeIR();
            
        	mt.setResult(new ABoolBasicTypeIR()); //set return type to bool
            invFun_.setMethodType(mt.clone());
          
            

            AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
            identifierPattern.setName(invFun_.getName());
            
            
            AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
            afp.setPattern(identifierPattern);
            afp.setType(t.clone()); 
            invFun_.getFormalParams().add(afp);
        	
            
            SExpIR expr = IsaInvExpGen.apply(node, identifierPattern, mt.clone(), isaFuncDeclIRMap);
            
        	invFun_.setBody(expr);
        	System.out.println(invFun_);
            // Insert into AST
            AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
            if(encModule != null)
            {
                encModule.getDecls().add(invFun_);
            }
            System.out.println("HERE"+encModule.getDecls());
            System.out.println("Invariant function has been added");

       
	
	   
        }
    }
    
    
    public AIdentifierPatternIR setIdentifierPattern(AFuncDeclIR inv, STypeIR t){
    	//set pattern to that used in invariant
    	AIdentifierPatternIR ip = new AIdentifierPatternIR();
    	
    	//if there are more than one inv parameters
        inv.getFormalParams().forEach
        ( 
        		(p) -> ip.setName(p.getPattern().toString()) 
        ); 
        
        return ip;
        
    }

    public String GenInvTypeDefinition(String arg){
        return "Definition\n" +
                "   inv_" + arg+ " :: \"" + arg + " \\<Rightarrow> \\<bool>\"\n" +
                "   where\n" +
                "";
    }

}
