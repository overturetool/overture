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
import org.overturetool.cgisa.IsaGen;
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
    public void caseAStateDeclIR(AStateDeclIR node) throws AnalysisException {
    	super.caseAStateDeclIR(node);
    	
    	SDeclIR decl = node.clone();
    	String typeName = IsaInvNameFinder.findName(node.clone());
    	SExpIR invExp = node.getInvExp().clone();
        // Invariant function
        AFuncDeclIR invFun_ = new AFuncDeclIR();
        invFun_.setName("inv_" + typeName); //inv_t

        AMethodTypeIR methodType = new AMethodTypeIR();
        
        STypeIR t = IsaDeclTypeGen.apply(decl.clone());
        methodType.getParams().add(t.clone());
        
	        
    	methodType.setResult(new ABoolBasicTypeIR());
        invFun_.setMethodType(methodType.clone());
	       
	        
	        
	
        // Translation for VDMToolkit and modeller written invariants
        if (invExp != null)
        {
        	AAndBoolBinaryExpIR multipleInvs = new AAndBoolBinaryExpIR();
        	//change (a_c a) to (c A) for Isabelle field access
            //if (decl instanceof ARecordDeclIR) formatExistingRecordInvExp(inv.getBody());
        	
            multipleInvs.setRight(invExp.clone());
        	
			AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
			identifierPattern.setName(typeName.substring(0, 1).toLowerCase());
			
			//set Inv pattern if one does not exist
			if (node.getInvPattern() != null) node.setInvPattern(identifierPattern.clone());
			
			SExpIR expr = IsaInvExpGen.apply(decl.clone(), 
					identifierPattern.clone() , 
					methodType.clone(), isaFuncDeclIRMap);
        	
			multipleInvs.setLeft(expr.clone());
        	
        	invFun_.setBody(multipleInvs.clone());
        	node.setInvExp(multipleInvs.clone());
        } 
        //translation for no inv types 
        else 
        {
        
        	SExpIR expr;
			AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
	        identifierPattern.setName(typeName.substring(0, 1).toLowerCase());
	        AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
	        afp.setPattern(identifierPattern.clone());
	        afp.setType(t.clone()); 
	        
	        node.setInvPattern(identifierPattern.clone());
	        
	        invFun_.getFormalParams().add(afp);
	        expr = IsaInvExpGen.apply(decl.clone(), identifierPattern, methodType.clone(), isaFuncDeclIRMap);
        	
        	
        	invFun_.setBody(expr.clone());
        	node.setInvExp(expr);
        }
        node.setInvDecl(invFun_.clone());
        
        IsaGen.funcGenHistoryMap.put(invFun_.getName(), invFun_.clone());
        System.out.println("Invariant function has been added");
        }
        
    
    
    
    
  
    @Override
    public void caseATypeDeclIR(ATypeDeclIR node) throws AnalysisException {
        super.caseATypeDeclIR(node);
        
        /*We do not want invariants built for each type declaration field
        instead we would like one invariant for the whole declaration type
        we skip subsequent record fields so that we do not get
        inv_field1 inv_field2 inv_record instead we get inv_record which accesses
        field1 and field2.*/
        
        String typeName = IsaInvNameFinder.findName(node.getDecl());
        SDeclIR decl = node.getDecl().clone();
         
        SDeclIR invFun;
        if (node.getDecl() instanceof ARecordDeclIR)
        	invFun = ( (ARecordDeclIR) decl).getInvariant();
        else
        	invFun = node.getInv();
        // Invariant function
        AFuncDeclIR invFun_ = new AFuncDeclIR();
        invFun_.setName("inv_" + typeName); //inv_t

        // Define the type signature
        //TODO: Type should be XTypeInt - correct?
        AMethodTypeIR methodType = new AMethodTypeIR();
        
        STypeIR t = IsaDeclTypeGen.apply(decl);
        methodType.getParams().add(t.clone());
        
	        
    	methodType.setResult(new ABoolBasicTypeIR());
        invFun_.setMethodType(methodType);
	       
	        
	        
	
        // Translation for VDMToolkit and modeller written invariants
        if (invFun != null)
        {
        	AFuncDeclIR inv = (AFuncDeclIR) invFun;//cast invariant function declaration to AFuncDeclIR
        	AAndBoolBinaryExpIR multipleInvs = new AAndBoolBinaryExpIR();
        	
        	for (int i = 0; i < inv.getMethodType().getParams().size(); i++)
        	{
	        	AFormalParamLocalParamIR afplp = new AFormalParamLocalParamIR();
	            afplp.setPattern(inv.getFormalParams().get(i).getPattern());
	            afplp.setType(inv.getMethodType().getParams().get(i).clone());
	            invFun_.getFormalParams().add(afplp);
        	}
        	
        	//change (a_c a) to (c A) for Isabelle field access
            //if (decl instanceof ARecordDeclIR) formatExistingRecordInvExp(inv.getBody());
        	
            multipleInvs.setRight(inv.getBody());
        	
			AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
			identifierPattern.setName(typeName.substring(0, 1).toLowerCase());
			SExpIR expr = IsaInvExpGen.apply(decl.clone(), 
					identifierPattern , 
					methodType.clone(), isaFuncDeclIRMap);
        	
			multipleInvs.setLeft(expr);
        	
        	invFun_.setBody(multipleInvs);
        } 
        //translation for no inv types 
        else 
        {
        	SExpIR expr;
			AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
	        identifierPattern.setName(typeName.substring(0, 1).toLowerCase());
	        AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
	        afp.setPattern(identifierPattern);
	        afp.setType(t.clone()); 
	        invFun_.getFormalParams().add(afp);
	        expr = IsaInvExpGen.apply(decl.clone(), identifierPattern, methodType.clone(), isaFuncDeclIRMap);
        	
        	
        	invFun_.setBody(expr);
        }
        

        // Insert into AST and get rid of existing invariant functions forEach field in record type
        AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
        if (decl instanceof ARecordDeclIR) encModule.getDecls().removeIf(
        		d -> d instanceof AFuncDeclIR && d.getChildren(true).get("_name").toString().contains("inv"));
        
        if(encModule != null)
        {
        	
            encModule.getDecls().add(invFun_);
        }

        IsaGen.funcGenHistoryMap.put(invFun_.getName(), invFun_.clone());
        
        System.out.println("Invariant function has been added");

        
    }
    
    @Override
    public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        super.caseAFieldDeclIR(node);
        if (node.parent() instanceof AStateDeclIR){
        	System.out.println("Redirecting State Invariants...");
        }
        else {
        STypeIR t = node.getType();// Invariant function
        AFuncDeclIR invFun_ = new AFuncDeclIR();
        invFun_.setName("inv_" + node.getName());
        
        AMethodTypeIR mt = new AMethodTypeIR();
        
    	mt.setResult(new ABoolBasicTypeIR()); //set return type to bool
        invFun_.setMethodType(mt.clone());
      
        

        AIdentifierPatternIR identifierPattern = new AIdentifierPatternIR();
        identifierPattern.setName("");//abbreviations have no params so do not use identifier pattern
        
        
        AFormalParamLocalParamIR afp = new AFormalParamLocalParamIR();
        afp.setPattern(identifierPattern);
        afp.setType(t.clone()); 
        invFun_.getFormalParams().add(afp);
    	
        
        SExpIR expr = IsaInvExpGen.apply(node, identifierPattern, mt.clone(), isaFuncDeclIRMap);
        
    	invFun_.setBody(expr);
    	IsaGen.funcGenHistoryMap.put(invFun_.getName(), invFun_);
        // Insert into AST
        AModuleDeclIR encModule = node.getAncestor(AModuleDeclIR.class);
        if(encModule != null)
        {
            encModule.getDecls().add(invFun_.clone());
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
