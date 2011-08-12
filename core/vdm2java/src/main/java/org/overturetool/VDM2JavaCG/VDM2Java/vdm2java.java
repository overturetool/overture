/**
 * Overture VDM++ to Java Code Generator
 */
package org.overturetool.VDM2JavaCG.VDM2Java;

/**
 * @author mehmudjan (Maihemutijiang Maimaiti)
 *
 */

import jp.co.csk.vdm.toolbox.VDM.*;

import java.beans.Expression;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.overturetool.VDM2JavaCG.ast.java.imp.*;
import org.overturetool.VDM2JavaCG.ast.java.itf.*;
import org.overturetool.VDM2JavaCG.main.ClassExstractorFromTexFiles;
import org.overturetool.VDM2JavaCG.main.StatusLog;
import org.overturetool.VDM2JavaCG.main.ParseException;
import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.AssignmentDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.UntypedDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.AbsoluteExpression;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.expressions.BinaryExpression;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.CardinalityExpression;
import org.overturetool.vdmj.expressions.CompExpression;
import org.overturetool.vdmj.expressions.DistConcatExpression;
import org.overturetool.vdmj.expressions.DistIntersectExpression;
import org.overturetool.vdmj.expressions.DistMergeExpression;
import org.overturetool.vdmj.expressions.DistUnionExpression;
import org.overturetool.vdmj.expressions.DivExpression;
import org.overturetool.vdmj.expressions.DomainResByExpression;
import org.overturetool.vdmj.expressions.DomainResToExpression;
import org.overturetool.vdmj.expressions.ElementsExpression;
import org.overturetool.vdmj.expressions.EqualsExpression;
import org.overturetool.vdmj.expressions.EquivalentExpression;
import org.overturetool.vdmj.expressions.FloorExpression;
import org.overturetool.vdmj.expressions.GreaterExpression;
import org.overturetool.vdmj.expressions.HeadExpression;
import org.overturetool.vdmj.expressions.IfExpression;
import org.overturetool.vdmj.expressions.ImpliesExpression;
import org.overturetool.vdmj.expressions.InSetExpression;
import org.overturetool.vdmj.expressions.IndicesExpression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.IsExpression;
import org.overturetool.vdmj.expressions.IsOfClassExpression;
import org.overturetool.vdmj.expressions.LenExpression;
import org.overturetool.vdmj.expressions.MapCompExpression;
import org.overturetool.vdmj.expressions.MapDomainExpression;
import org.overturetool.vdmj.expressions.MapEnumExpression;
import org.overturetool.vdmj.expressions.MapExpression;
import org.overturetool.vdmj.expressions.MapInverseExpression;
import org.overturetool.vdmj.expressions.MapRangeExpression;
import org.overturetool.vdmj.expressions.MapUnionExpression;
import org.overturetool.vdmj.expressions.MapletExpression;
import org.overturetool.vdmj.expressions.MkBasicExpression;
import org.overturetool.vdmj.expressions.ModExpression;
import org.overturetool.vdmj.expressions.MuExpression;
import org.overturetool.vdmj.expressions.NewExpression;
import org.overturetool.vdmj.expressions.NilExpression;
import org.overturetool.vdmj.expressions.NotEqualExpression;
import org.overturetool.vdmj.expressions.NotExpression;
import org.overturetool.vdmj.expressions.NotInSetExpression;
import org.overturetool.vdmj.expressions.NotYetSpecifiedExpression;
import org.overturetool.vdmj.expressions.PlusPlusExpression;
import org.overturetool.vdmj.expressions.PowerSetExpression;
import org.overturetool.vdmj.expressions.ProperSubsetExpression;
import org.overturetool.vdmj.expressions.QuoteLiteralExpression;
import org.overturetool.vdmj.expressions.RangeResByExpression;
import org.overturetool.vdmj.expressions.RangeResToExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.expressions.RemExpression;
import org.overturetool.vdmj.expressions.SameClassExpression;
import org.overturetool.vdmj.expressions.SelfExpression;
import org.overturetool.vdmj.expressions.SeqCompExpression;
import org.overturetool.vdmj.expressions.SeqConcatExpression;
import org.overturetool.vdmj.expressions.SeqEnumExpression;
import org.overturetool.vdmj.expressions.SeqExpression;
import org.overturetool.vdmj.expressions.SetCompExpression;
import org.overturetool.vdmj.expressions.SetDifferenceExpression;
import org.overturetool.vdmj.expressions.SetEnumExpression;
import org.overturetool.vdmj.expressions.SetExpression;
import org.overturetool.vdmj.expressions.SetIntersectExpression;
import org.overturetool.vdmj.expressions.SetRangeExpression;
import org.overturetool.vdmj.expressions.SetUnionExpression;
import org.overturetool.vdmj.expressions.StarStarExpression;
import org.overturetool.vdmj.expressions.StringLiteralExpression;
import org.overturetool.vdmj.expressions.SubclassResponsibilityExpression;
import org.overturetool.vdmj.expressions.SubseqExpression;
import org.overturetool.vdmj.expressions.SubsetExpression;
import org.overturetool.vdmj.expressions.TailExpression;
import org.overturetool.vdmj.expressions.UnaryExpression;
import org.overturetool.vdmj.expressions.UnaryMinusExpression;
import org.overturetool.vdmj.expressions.UnaryPlusExpression;
import org.overturetool.vdmj.expressions.UndefinedExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.InternalException;
//import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.patterns.MultipleSetBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.SetPattern;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.statements.AssignmentStatement;
import org.overturetool.vdmj.statements.AtomicStatement;
import org.overturetool.vdmj.statements.BlockStatement;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.ErrorStatement;
import org.overturetool.vdmj.statements.FieldDesignator;
import org.overturetool.vdmj.statements.ForAllStatement;
import org.overturetool.vdmj.statements.ForIndexStatement;
import org.overturetool.vdmj.statements.ForPatternBindStatement;
import org.overturetool.vdmj.statements.IdentifierDesignator;
import org.overturetool.vdmj.statements.IfStatement;
import org.overturetool.vdmj.statements.MapSeqDesignator;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
import org.overturetool.vdmj.statements.ObjectApplyDesignator;
import org.overturetool.vdmj.statements.ObjectDesignator;
import org.overturetool.vdmj.statements.ObjectFieldDesignator;
import org.overturetool.vdmj.statements.ObjectIdentifierDesignator;
import org.overturetool.vdmj.statements.ObjectNewDesignator;
import org.overturetool.vdmj.statements.ObjectSelfDesignator;
import org.overturetool.vdmj.statements.ReturnStatement;
import org.overturetool.vdmj.statements.SkipStatement;
import org.overturetool.vdmj.statements.StateDesignator;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;
import org.overturetool.vdmj.statements.WhileStatement;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;


@SuppressWarnings({"all","unchecked","unused"})
public class vdm2java {

  private StatusLog log = null;
  private ClassDefinition cls;
  
  public static Vector<String> imports = new Vector<String>();
  //public static List<Vector<IJavaType>> union = new Vector<Vector<IJavaType>>();

  //----This list stors the list of Type definitions that are converted so that 
  //--- the same types will not be converted multiple t.
  private List<String> TypeDefinitions = new Vector<String>();

  public vdm2java () throws CGException {
	    init();
	  }
  
  
  private void init () throws CGException {
    try {
      log = (StatusLog) new StatusLog();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
  


  public StatusLog GetLog () throws CGException {
    return (StatusLog) log;
  }

  
  //------------------------- Build Java document------------------------------------------------
  public IJavaDocument Transform (final ClassList class_list) throws CGException {
	    JavaSpecification Jspecs = (JavaSpecification) (JavaSpecification) BuildJava((ClassList) class_list);
	    {
	     
	      JavaDocument Jdoc =  new JavaDocument();
	      Jdoc.setSpecification(Jspecs);
	      return (IJavaDocument) Jdoc;
	    }
	  }
	  


//-------------------- Building Java specification (Java Class/Interface List)---------------------------

  public JavaSpecification BuildJava (final ClassList class_list) throws CGException {

      List<IJavaClassDefinition> listofjclsdef = new Vector<IJavaClassDefinition>();
      int class_size = class_list.size();
      IJavaClassDefinition jclsdef = null;
          
      for(int i=0; i < class_size; i++ )
      {
    	  jclsdef = (IJavaClassDefinition) BuildClass(class_list.get(i));
    	  listofjclsdef.add(jclsdef);
      }
      JavaClassDefinitions jclsdefs = new JavaClassDefinitions(listofjclsdef);
    return (JavaSpecification) jclsdefs;
  }
  
  
// --------------------------- Building Java Class---------------------------------------------------------------------

  public IJavaClassDefinition BuildClass (final ClassDefinition c) throws CGException {

    cls = c;
	String vdmclsname = c.getName();
    log.addNewClassInfo(vdmclsname);
    {
      //-----------  get class name---------------------------------
    	String name = c.getName();
    	
      // -------------get modifiers -------------------------------
      IJavaModifier J_mdfr = null;
      J_mdfr = new JavaModifier(c.isAbstract, c.isUpdatable());      
    	
      // -------------get access definition --------------------------
      IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) c.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(c.isStatic(), J_scope);
 
      //  ------------get Definition List ------------------
      IJavaDefinitionList dList= null;
      List<IJavaDefinition> VJ_def = new Vector<IJavaDefinition>();
      { IJavaDefinition J_def = null;
        int max_dlist = c.definitions.size();
        for (int i=0; i < max_dlist; i++) {
        	J_def = (IJavaDefinition) buildJDef(c.definitions.get(i), name);
        	VJ_def.add(J_def);
        }  
         dList = new JavaDefinitionList(VJ_def);
      }
      
      //-------------- get inheritance clause-----------------------------
      List<IJavaIdentifier> supers = GetSuperClasses((LexNameList) c.supernames);
 
      return (IJavaClassDefinition) new JavaClassDefinition(false, J_adf, J_mdfr, new JavaIdentifier(name), new JavaInheritanceClause(new Vector<IJavaIdentifier>(), supers) , dList);
    
  }

 } 
  
  
  public Vector<IJavaIdentifier> GetSuperClasses (final LexNameList spns) throws CGException {
	  
    if (new Boolean(UTIL.equals(spns, null)).booleanValue()) 
      return new Vector<IJavaIdentifier>();
    else {

      Vector<IJavaIdentifier> list = new Vector() ;
      int max_lxn = spns.size();
      IJavaIdentifier jir = null;
      for (int i=0; i < max_lxn; i++) {
    	    jir = new JavaIdentifier(spns.get(i).getIdentifier().name); 
        	list.add(jir);
        }
      return (Vector<IJavaIdentifier>) list;
    }
  }

// ------------------------ building Definitions---------------------------------------
  
  private IJavaDefinition buildJDef (final Definition def, final String owner) throws CGException {

    boolean flag = true;
    {

      flag = true;
      if (!def.isInstanceVariable()) 
        flag = false;
      if (flag) {

        return (IJavaDefinition) BuildDefBlock((InstanceVariableDefinition) def, owner);
      }
      else {

        flag = true;
        if (!def.isValueDefinition()) 
          flag = false;
        if (flag) {
        	
          ValueDefinition tmp = (ValueDefinition) def;
          return (IJavaDefinition) BuildDefBlock((ValueDefinition) tmp, owner);
        }
        else {

          flag = true;
          if (!def.isTypeDefinition()) 
            flag = false;
          if (flag) {

            TypeDefinition tmp = (TypeDefinition) def;
            return (IJavaDefinition) BuildDefBlock((TypeDefinition) tmp, owner);
          }
          else {

            flag = true;
            if (!def.isFunctionOrOperation()) 
              flag = false;
            if (flag) {
            	if (def instanceof ExplicitOperationDefinition )
            		return (IJavaDefinition) BuildDefBlock((ExplicitOperationDefinition)def, owner);
            	if (def instanceof ImplicitOperationDefinition )
            		return (IJavaDefinition) BuildDefBlock((ImplicitOperationDefinition)def, owner);
            	if (def instanceof ExplicitFunctionDefinition )
            		return (IJavaDefinition) BuildDefBlock((ExplicitFunctionDefinition) def, owner);
            	if (def instanceof ImplicitFunctionDefinition )
            		return (IJavaDefinition) BuildDefBlock((ImplicitFunctionDefinition) def, owner);
            }
           }
          }
        }
          return null;
      }
    }


  public IJavaDefinition BuildDefBlock (final InstanceVariableDefinition v, final String owner) throws CGException {

    
    return (IJavaDefinition) BuildVariable(v, owner);
  }


//-------------- convert VDM instance variables to Java instance variables (currently empty)--------------  
  public IJavaInstanceVariableDefinition BuildVariable (final InstanceVariableDefinition ivd, final String owner) throws CGException {

      if (ivd.initialized)
    	  	return (IJavaInstanceVariableDefinition) new JavaInstanceVariableDefinition(new JavaAccessDefinition(ivd.isStatic(), ConvertScopeToJavaScope(ivd.accessSpecifier)), new JavaModifier(false, !ivd.isUpdatable()), Vdm2JavaType.ConvertType(ivd.type), new JavaIdentifier(ivd.getName()), BuildExpression(ivd.expression), true);
      else 
    	  return (IJavaInstanceVariableDefinition) new JavaInstanceVariableDefinition(new JavaAccessDefinition(ivd.isStatic(), ConvertScopeToJavaScope(ivd.accessSpecifier)), new JavaModifier(false, !ivd.isUpdatable()), Vdm2JavaType.ConvertType(ivd.type), new JavaIdentifier(ivd.getName()), new JavaEmptyExpression(), false);
  }

  
  public IJavaScope ConvertScopeToJavaScope (final AccessSpecifier sc) throws CGException {

    JavaScope js = null;
    {

      String val = null;
      val = sc.access.toString();
      boolean flag = true;
      {

        flag = true;
        if (!UTIL.equals(val, new String("public"))) 
          flag = false;
        if (flag) {
          js = new JavaScope();
          js.setEnum(JavaScopeEnum.EPUBLIC);}
      }
      if (!flag) {

        flag = true;
        if (!UTIL.equals(val, new String("private"))) 
          flag = false;
        if (flag) {
        	js = new JavaScope();
            js.setEnum(JavaScopeEnum.EPRIVATE);}
      }
      if (!flag) {

        flag = true;
        if (!UTIL.equals(val, new String("protected"))) 
          flag = false;
        if (flag) {
        	js = new JavaScope();
            js.setEnum(JavaScopeEnum.EPROTECTED);}
      }
      if (!flag) 
        System.out.println("Run-Time Error:Can not evaluate an undefined expression");
    }
    return (IJavaScope) js;
  }

  //public IJavaDefinition BuildDefBlock(final LocalDefinition ld, final String owner) {
	  
	//  return (IJavaInstanceVariableDefinition) new JavaInstanceVariableDefinition(new JavaAccessDefinition(ld.isStatic(), ConvertScopeToJavaScope(ld.accessSpecifier)), new JavaModifier(false, !ld.isUpdatable()), Vdm2JavaType.ConvertType(ld.type), new JavaIdentifier(ld.getName()), BuildExpression(ld.f), true);
  //}

  public IJavaDefinition BuildDefBlock(final ValueDefinition v, final String owner) throws CGException {
    return (IJavaDefinition) BuildValue(v, owner);
  }


  public IJavaDefinition BuildValue (final ValueDefinition vd, final String owner) throws CGException {

    return (IJavaInstanceVariableDefinition) new JavaInstanceVariableDefinition(new JavaAccessDefinition(vd.isStatic(), ConvertScopeToJavaScope(vd.accessSpecifier)), new JavaModifier(false, !vd.isUpdatable()), Vdm2JavaType.ConvertType(vd.getType()), new JavaIdentifier(vd.pattern.toString()), BuildExpression(vd.exp), true);
  }

//-------------- convert VDM type definitions to Java definitions (currently just the Union Type available)---------------- 
  public IJavaDefinition BuildDefBlock (final TypeDefinition td, final String owner) throws CGException {

      if (td.type instanceof NamedType) {
    	  if (td.type.isUnion()) {
    		  Boolean flag = false;
    		  for (Iterator<Type> i = td.type.getUnion().types.iterator();  i.hasNext();) {
    			  Type t = i.next();
    			  if ((t instanceof QuoteType) || (t instanceof RecordType))
    				  flag = true;
    		  }			
    		  if (flag)
    			  return (IJavaDefinition) ConvertUnionType(td);
    		  else 
    			  throw new InternalError("Sorry, currently only Quote and Record types are allowed to form a 'union type', '"+td.getLocation()+"'");
    	  }
    	  else if (((NamedType)td.type).type instanceof QuoteType) {
    		  return (IJavaDefinition) BuildClassFromQuoteType(td, new Vector<IJavaIdentifier>());
    	  }
    	  else  
    		  return (IJavaDefinition) new JavaEmptyDefinition();
      }
      
      if (td.type.isRecord()) {
    	  return (IJavaDefinition) BuildClassFromRecordType(td, new Vector<IJavaIdentifier>());
      }
      
      throw new InternalError("Type'"+td.kind()+"' is not subtype of 'VDMPP Type Definition' or not yet supported, '"+td.getLocation()+"'");
  }


 private IJavaDefinition BuildClassFromQuoteType(TypeDefinition td, List<IJavaIdentifier> union) throws CGException {
	 QuoteType qt = (QuoteType)((NamedType)td.type).type;
	 if (!TypeDefinitions.contains(qt.value)) {
		    IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) td.accessSpecifier);;
		    IJavaAccessDefinition J_adf = new JavaAccessDefinition(td.isStatic(), J_scope);
		    List<IJavaIdentifier> ji = new Vector<IJavaIdentifier>();
		    ji.add(new JavaIdentifier("Quote"));
		    IJavaIdentifier iji = new JavaIdentifier(qt.value);
		    List<IJavaDefinition> jd_list = new Vector<IJavaDefinition>();
		    JavaScope tjs = new JavaScope();
            tjs.setEnum(JavaScopeEnum.EPRIVATE);
		    jd_list.add(new JavaInstanceVariableDefinition(new JavaAccessDefinition(false, tjs), new JavaModifier(false, false), new JavaStringType(), new JavaIdentifier("Literal"), new JavaStringLiteralExpression(qt.value), true));
			JavaClassDefinition jd = new JavaClassDefinition(true, J_adf, new JavaModifier(false, td.isUpdatable()), iji , new JavaInheritanceClause(union, ji), new JavaDefinitionList(jd_list));
			TypeDefinitions.add(qt.value);
			return (IJavaDefinition) jd;
	 }	 
	// TODO Auto-generated method stub
	 else
		return (IJavaDefinition) new JavaEmptyDefinition();
}

 private IJavaDefinition BuildClassFromRecordType(TypeDefinition td, List<IJavaIdentifier> union) throws CGException {
	 RecordType qt = (RecordType)td.type.getRecord();
	 if (!TypeDefinitions.contains(qt.name.name)) {
		    IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) td.accessSpecifier);;
		    IJavaAccessDefinition J_adf = new JavaAccessDefinition(td.isStatic(), J_scope);
		    //List<IJavaIdentifier> ji = new Vector<IJavaIdentifier>();
		    //ji.add(new JavaIdentifier("Quote"));
		    IJavaIdentifier iji = new JavaIdentifier(qt.name.name);
		    List<IJavaDefinition> jd_list = new Vector<IJavaDefinition>();
		    JavaScope tjs = new JavaScope();
            tjs.setEnum(JavaScopeEnum.EPUBLIC);
            for (int i = 0; i < qt.fields.size(); i++) {
            	jd_list.add(new JavaInstanceVariableDefinition(new JavaAccessDefinition(false, tjs), new JavaModifier(false, false), Vdm2JavaType.ConvertType(qt.fields.get(i).type), new JavaIdentifier(qt.fields.get(i).tagname.name), new JavaEmptyExpression(), false));
            }
			JavaClassDefinition jd = new JavaClassDefinition(true, J_adf, new JavaModifier(false, td.isUpdatable()), iji , new JavaInheritanceClause(union, new Vector<IJavaIdentifier>()), new JavaDefinitionList(jd_list));
			TypeDefinitions.add(qt.name.name);
			return (IJavaDefinition) jd;
	 }	 
	// TODO Auto-generated method stub
	 else
		return (IJavaDefinition) new JavaEmptyDefinition();
}

private List<IJavaDefinition> BuildClassFromSimpleType (final TypeDefinition td) throws CGException {

    if (!td.getType().getUnion().types.isEmpty()) {
    	List<IJavaDefinition> Dlist = new Vector<IJavaDefinition>();
    	List<IJavaIdentifier> lji = new Vector<IJavaIdentifier>();
		lji.add(new JavaIdentifier(td.name.name));
    	for (int i = 0; i < td.getType().getUnion().definitions.size(); i++) {
    		if (td.getType().getUnion().definitions.get(i).getType().isUnion()) {
    			// needs to be changed, has to check if the enclosed union is already converted or has to be
    			// converted first.
    			//jd = ConvertUnionType((TypeDefinition)tnm.definitions.get(0));
    			//Dlist.add(jd);
    			throw new InternalError("Sorry, currently only Quote and Record types are allowed to form a 'union type', '"+td.getLocation()+"'");
    		}
    		if (td.getType().getUnion().definitions.get(i).getType() instanceof RecordType) {
    			Dlist.add(BuildClassFromRecordType((TypeDefinition) td.getType().getUnion().definitions.get(i), lji));
    		}
    		else
    			Dlist.add(BuildClassFromQuoteType((TypeDefinition) td.getType().getUnion().definitions.get(i), lji));;
    	}
    	for (Iterator<Type> i = td.getType().getUnion().types.iterator(); i.hasNext();) {
    		if (i.next() instanceof QuoteType)
    			Dlist.add(BuildClassFromQuoteType(td.getType().getUnion().types.iterator().next(), td)); }
    	return Dlist;
    }
	return new Vector<IJavaDefinition>();
  }

private IJavaDefinition BuildClassFromQuoteType(Type t, TypeDefinition td) throws CGException { 
	QuoteType qt;
	 if (t instanceof NamedType)
		 qt = (QuoteType)((NamedType)t).type;
	 else 
		 qt = (QuoteType)t;
	 List<IJavaIdentifier> union = new Vector<IJavaIdentifier>();
		union.add(new JavaIdentifier(td.name.name));
	 if (!TypeDefinitions.contains(qt.value)) {
		    IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) td.accessSpecifier);;
		    IJavaAccessDefinition J_adf = new JavaAccessDefinition(td.isStatic(), J_scope);
		    List<IJavaIdentifier> ji = new Vector<IJavaIdentifier>();
		    ji.add(new JavaIdentifier("Quote"));
		    IJavaIdentifier iji = new JavaIdentifier(qt.value);
		    List<IJavaDefinition> jd_list = new Vector<IJavaDefinition>();
		    JavaScope tjs = new JavaScope();
           tjs.setEnum(JavaScopeEnum.EPRIVATE);
		    jd_list.add(new JavaInstanceVariableDefinition(new JavaAccessDefinition(false, tjs), new JavaModifier(false, false), new JavaStringType(), new JavaIdentifier("Literal"), new JavaStringLiteralExpression(qt.value), true));
			JavaClassDefinition jd = new JavaClassDefinition(true, J_adf, new JavaModifier(false, td.isUpdatable()), iji , new JavaInheritanceClause(union, ji), new JavaDefinitionList(jd_list));
			TypeDefinitions.add(qt.value);
			return (IJavaDefinition) jd;
	 }
	 else
			return (IJavaDefinition) new JavaEmptyDefinition();		 
}

  private IJavaInterfaceDefinition ConvertUnionType (TypeDefinition td) throws CGException {
	  
	  IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) td.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(td.isStatic(), J_scope);
      IJavaDefinitionList jdl = new JavaDefinitionList(BuildClassFromSimpleType(td));
    return (IJavaInterfaceDefinition) new JavaInterfaceDefinition(J_adf, new JavaModifier(false,false), new JavaIdentifier(td.name.getIdentifier().name), new JavaInheritanceClause(new Vector<IJavaIdentifier>(), new Vector<IJavaIdentifier>()), jdl);
  }

//-------------- converting VDM explicit operation to Java method (currently empty)-------------- 
  public IJavaMethodDefinition BuildDefBlock (final ExplicitOperationDefinition xo, final String owner) throws CGException {

	// --------------- getting access definition-----------------
	  IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) xo.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(xo.isStatic(), J_scope);
      
      
   // -------------get modifiers (explicit functions are not abstract)-------------------------------
      IJavaModifier J_mdfr = null;
      J_mdfr = new JavaModifier(xo.body instanceof SubclassResponsibilityStatement, xo.isUpdatable());  
      
   // -------------get Identifier--------------------
      String name = xo.name.getIdentifier().name;
      IJavaIdentifier method_name = new JavaIdentifier(name);
      
// --------------- getting return type ------------------------------
	        IJavaType type = null;
	        type = (IJavaType) Vdm2JavaType.ConvertType(xo.getType().getOperation().result);
	        
//------------------get parameter list-------------------------------------------------------	        
	       List<IJavaBind> pars = null; 
	       pars = (Vector<IJavaBind>) buildParameters(xo.getParamPatternList(), xo.getType().getOperation().parameters);
//----------------------- get method body----------------------------------------	     
	        IJavaMethodBody body = null;
	        List<IJavaStatement> statement_list = new Vector<IJavaStatement>();
	        IJavaStatement statement = null; 
	       statement = (IJavaStatement) BuildStatement(xo.body);
	       statement_list.add(statement);
	     //-------------getting preconditions----------
	       IJavaExpression Pre = new JavaEmptyExpression();
	       if (!(xo.precondition == null))
	    	   Pre = (IJavaExpression) BuildExpression(xo.precondition);
	       //---------converting postcondition-----------
	       String post = "";
	       if (!(xo.postcondition == null))
	    	   post = xo.postcondition.toString();
	    	 
	       body = new JavaMethodBody(Pre, statement_list, (xo.body instanceof SubclassResponsibilityStatement), false, post);
	        return (IJavaMethodDefinition) new JavaMethodDefinition(xo.isConstructor, J_adf, J_mdfr, method_name, type, pars, body);
  }

//-------------- converting VDM implicit operation to Java method (currently empty)----------------   
  public IJavaMethodDefinition BuildDefBlock (final ImplicitOperationDefinition IopDef, final String owner) throws CGException {
	  
	// --------------- getting access definition-----------------
	  IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) IopDef.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(IopDef.isStatic(), J_scope);
      
      
   // -------------get modifiers (explicit functions are not abstract)-------------------------------
      IJavaModifier J_mdfr = null;
      J_mdfr = new JavaModifier(IopDef.body instanceof SubclassResponsibilityStatement, IopDef.isUpdatable());  
      
   // -------------get Identifier--------------------
      String name = IopDef.name.getIdentifier().name;
      IJavaIdentifier method_name = new JavaIdentifier(name);
      
// --------------- getting return type ------------------------------
	        IJavaType type = null;
	        type = (IJavaType) Vdm2JavaType.ConvertType(IopDef.getType().getOperation().result);
	        
//------------------get parameter list-------------------------------------------------------	        
	       List<IJavaBind> pars = null; 
	       pars = (Vector<IJavaBind>) buildParameters(IopDef.getListParamPatternList(), IopDef.getType().getOperation().parameters);
//----------------------- get method body----------------------------------------	     
	        IJavaMethodBody body = null;
	        List<IJavaStatement> statement_list = new Vector<IJavaStatement>();
	        IJavaStatement statement = null; 
	       statement = (IJavaStatement) BuildStatement(IopDef.body);
	       statement_list.add(statement);
	     //-------------getting preconditions----------
	       IJavaExpression Pre = new JavaEmptyExpression();
	       if (!(IopDef.precondition == null))
	    	   Pre = (IJavaExpression) BuildExpression(IopDef.precondition);
	       //---------converting postcondition-----------
	       String post = "";
	       if (!(IopDef.postcondition == null))
	    	   post = IopDef.postcondition.toString();
	    	 
	       body = new JavaMethodBody(Pre, statement_list, (IopDef.body instanceof SubclassResponsibilityStatement), true, post);
	        return (IJavaMethodDefinition) new JavaMethodDefinition(IopDef.isConstructor, J_adf, J_mdfr, method_name, type, pars, body);
  }

//-------------- converting VDM explicit function to Java method----------------     
  public IJavaMethodDefinition BuildDefBlock (final ExplicitFunctionDefinition exFunc, final String owner) throws CGException {

	  // --------------- getting access definition-----------------
	  IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) exFunc.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(exFunc.isStatic(), J_scope);
      
      
   // -------------get modifiers (explicit functions are not abstract)-------------------------------
      IJavaModifier J_mdfr = null;
      J_mdfr = new JavaModifier(exFunc.body instanceof SubclassResponsibilityExpression, exFunc.isUpdatable());  
      
   // -------------get Identifier--------------------
      String name = exFunc.name.getIdentifier().name;
      IJavaIdentifier method_name = new JavaIdentifier(name);
      
// --------------- getting return type ------------------------------
	        IJavaType type = null;
	        type = (IJavaType) Vdm2JavaType.ConvertType(exFunc.getType().getFunction().result);
	        
//------------------get parameter list-------------------------------------------------------	        
	       List<IJavaBind> pars = null; 
	       pars = (Vector<IJavaBind>) buildParameters(exFunc.paramPatternList, exFunc.getType().getFunction().parameters);
//----------------------- get method body----------------------------------------	     
	        IJavaMethodBody body = null;
	        List<IJavaStatement> statement_list = new Vector<IJavaStatement>();
	        IJavaStatement statement = null; 
	       statement = (IJavaStatement) BuildStatement(exFunc.body);
	       statement_list.add(statement);
	     //-------------getting preconditions----------
	       IJavaExpression Pre = new JavaEmptyExpression();
	       if (!(exFunc.precondition == null))
	    	   Pre = (IJavaExpression) BuildExpression(exFunc.precondition);
	       //---------converting postcondition-----------
	       String post = "";
	       if (!(exFunc.postcondition == null))
	    	   post = exFunc.postcondition.toString();
	       
	       body = new JavaMethodBody(Pre, statement_list, (exFunc.body instanceof SubclassResponsibilityExpression), false, post);
	       return (IJavaMethodDefinition) new JavaMethodDefinition(false, J_adf, J_mdfr, method_name, type, pars, body);
	  }
  
 //-------------- Build statements from VDM++ expressions--------------------------
  public IJavaStatement BuildStatement(org.overturetool.vdmj.expressions.Expression exp) {
	// TODO Auto-generated method stub
	if(exp instanceof IfExpression) {
		
		IJavaIfStatement jIF;
		IfExpression IF = (IfExpression) exp;
		List<IJavaElseIfStatement> lst_jes = new Vector<IJavaElseIfStatement>();
		if (!IF.elseList.isEmpty()) {
			int size = IF.elseList.size();
			for (int i = 0; i < size; i++) {
				lst_jes.add(new JavaElseIfStatement(BuildStatement(IF.elseList.get(i).thenExp), BuildExpression(IF.elseList.get(i).elseIfExp)));
			}
		}
		if (!(IF.elseExp == null)) {
			jIF = new JavaIfStatement(BuildExpression(IF.ifExp), BuildStatement(IF.thenExp),lst_jes, BuildStatement(IF.elseExp));
		}
		else 
			jIF = new JavaIfStatement(BuildExpression(IF.ifExp), BuildStatement(IF.thenExp),lst_jes, new JavaEmptyStatement());
	return jIF;
	}
	
	else if (exp instanceof NotYetSpecifiedExpression) {
		return (IJavaStatement) new JavaNotYetSpecifiedStatement();
	}
	else
     	return (IJavaStatement) new JavaReturnStatement(BuildExpression(exp));
}

 //-------------- Build statements from VDM++ statements---------------------------
  public IJavaStatement BuildStatement(org.overturetool.vdmj.statements.Statement stmt) throws CGException {
		// TODO Auto-generated method stub
		if(stmt instanceof IfStatement) {
			
			IJavaIfStatement JIF;
			IfStatement IF = (IfStatement) stmt;
			List<IJavaElseIfStatement> lst_jes = new Vector<IJavaElseIfStatement>();
			if (!IF.elseList.isEmpty()) {
				int size = IF.elseList.size();
				for (int i = 0; i < size; i++) {
					lst_jes.add(new JavaElseIfStatement(BuildStatement(IF.elseList.get(i).thenStmt), BuildExpression(IF.elseList.get(i).elseIfExp)));
				}
			}
			if (!(IF.elseStmt == null)) {
				JIF = new JavaIfStatement(BuildExpression(IF.ifExp), BuildStatement(IF.thenStmt),lst_jes, BuildStatement(IF.elseStmt));
			}
			else 
				JIF = new JavaIfStatement(BuildExpression(IF.ifExp), BuildStatement(IF.thenStmt),lst_jes, new JavaEmptyStatement());
		return JIF;			
		}	
		if (stmt instanceof ReturnStatement)
	     	return (IJavaStatement) new JavaReturnStatement(BuildExpression(((ReturnStatement)stmt).expression));
		if (stmt instanceof NotYetSpecifiedStatement)
			return (IJavaStatement) new JavaNotYetSpecifiedStatement();
		if (stmt instanceof SubclassResponsibilityStatement)
			return (IJavaStatement) new JavaIsSubClassResponsibilityStatement();
		if (stmt instanceof AssignmentStatement) {
			AssignmentStatement st = (AssignmentStatement) stmt;
				return (IJavaStatement) new JavaAssignStatement(BuildDesignator(st.target), BuildExpression(st.exp));
		}
		if (stmt instanceof BlockStatement) {
			BlockStatement bs = (BlockStatement) stmt;
			List<IJavaAssignmentDefinition> jd = new Vector<IJavaAssignmentDefinition>();
			for (int i = 0; i < bs.assignmentDefs.size(); i++) {
				AssignmentDefinition ad = (AssignmentDefinition) bs.assignmentDefs.get(i);
				jd.add(new JavaAssignmentDefinition(new JavaIdentifier(ad.name.name), Vdm2JavaType.ConvertType(ad.type), BuildExpression(ad.expression)));
			}
		}
		if (stmt instanceof AtomicStatement) {
			AtomicStatement as = (AtomicStatement) stmt;
			List<IJavaAssignStatement> ljs = new Vector<IJavaAssignStatement>();
			for (int i = 0; i < as.assignments.size(); i++) {
				ljs.add((IJavaAssignStatement) BuildStatement(as.assignments.get(i)));
			}
			return (IJavaStatement) new JavaAtomicStatement(ljs);
		}
		if (stmt instanceof ErrorStatement) {
			return (IJavaStatement) new JavaErrorStatement();
		}
		if (stmt instanceof SkipStatement) {
			return (IJavaStatement) new JavaContinueStatement();
		}
		if (stmt instanceof WhileStatement) {
			return (IJavaStatement) new JavaWhileLoop(BuildExpression(((WhileStatement) stmt).exp), BuildStatement(((WhileStatement) stmt).statement));
		}
		if (stmt instanceof ForAllStatement) {
			return (IJavaStatement) new JavaSetForLoop(((ForAllStatement) stmt).pattern.toString(), BuildExpression(((ForAllStatement) stmt).set), BuildStatement(((ForAllStatement) stmt).statement));
		}
		if (stmt instanceof ForPatternBindStatement) {
			return (IJavaStatement) new JavaSeqForLoop(((ForPatternBindStatement) stmt).reverse, ((ForPatternBindStatement) stmt).patternBind.pattern.toString(), BuildExpression(((ForPatternBindStatement) stmt).exp), BuildStatement(((ForPatternBindStatement) stmt).statement));
		}
		if (stmt instanceof ForIndexStatement) {
			return (IJavaStatement) new JavaIndexForLoop(((ForIndexStatement) stmt).var.name, BuildExpression(((ForIndexStatement) stmt).from), BuildExpression(((ForIndexStatement) stmt).to), BuildExpression(((ForIndexStatement) stmt).by), BuildStatement(((ForIndexStatement) stmt).statement));
		}
		if (stmt instanceof CallStatement) {
			List<IJavaExpression> lje = new Vector<IJavaExpression>();
			CallStatement oad = (CallStatement) stmt;
			for (int i = 0; i < oad.args.size(); i++) {
				lje.add(BuildExpression(oad.args.get(i)));
			}
			return (IJavaStatement) new JavaCallStatement(new JavaIdentifier(oad.name.name), lje);
		}
		if (stmt instanceof CallObjectStatement) {
			List<IJavaExpression> lje = new Vector<IJavaExpression>();
			CallObjectStatement oad = (CallObjectStatement) stmt;
			for (int i = 0; i < oad.args.size(); i++) {
				lje.add(BuildExpression(oad.args.get(i)));
			}
			return (IJavaStatement) new JavaCallObjectStatement(BuildObjectDesignator(oad.designator), new JavaName(new JavaIdentifier(oad.fieldname.name)), lje);
		}
		
		throw new InternalError("Type '"+stmt.kind()== null ? stmt.toString():stmt.kind()+"' is not subtype of 'VDMPP Statement' or not yet supported, '"+stmt.getLocation()+"'");
	}
  
  public IJavaObjectDesignator BuildObjectDesignator(final ObjectDesignator sd) {
		if (sd instanceof ObjectIdentifierDesignator)
			return (IJavaObjectDesignator) new JavaNameDesignator(new JavaIdentifier(((ObjectIdentifierDesignator)sd).name.name));
		if (sd instanceof ObjectFieldDesignator)
			return (IJavaObjectDesignator) new JavaObjectFieldReference(BuildObjectDesignator(((ObjectFieldDesignator)sd).object), new JavaName(new JavaIdentifier(((ObjectFieldDesignator)sd).fieldname.name)));
		if (sd instanceof ObjectApplyDesignator) {
			List<IJavaExpression> lje = new Vector<IJavaExpression>();
			ObjectApplyDesignator oad = (ObjectApplyDesignator) sd;
			for (int i = 0; i < oad.args.size(); i++) {
				lje.add(BuildExpression(oad.args.get(i)));
			}
			return (IJavaObjectDesignator) new JavaObjectApply(BuildObjectDesignator(((ObjectApplyDesignator)sd).object), lje);
		}
		if (sd instanceof ObjectNewDesignator) {
			return (IJavaObjectDesignator) new JavaNewDesignator((IJavaNewExpression) BuildExpression(((ObjectNewDesignator) sd).expression));
		}	
		if (sd instanceof ObjectSelfDesignator)
			return (IJavaObjectDesignator) new JavaThisDesignator(new JavaThisExpression());
		else
			throw new InternalError("Type '"+sd.toString()+"' is not subtype of 'VDMPP ObjectDesignator' or not yet supported, '"+sd.location+"'");
	}

  	public IJavaStateDesignator BuildDesignator(final StateDesignator sd) {
  		if (sd instanceof IdentifierDesignator)
  			return (IJavaStateDesignator) new JavaStateDesignatorName(new JavaName(new JavaIdentifier(((IdentifierDesignator)sd).name.name)));
  		if (sd instanceof FieldDesignator)
  			return (IJavaStateDesignator) new JavaFieldReference(BuildDesignator(((FieldDesignator)sd).object), new JavaIdentifier(((FieldDesignator)sd).field.name));
  		if (sd instanceof MapSeqDesignator)
  			return (IJavaStateDesignator) new JavaMapOrSequenceReference(BuildDesignator(((MapSeqDesignator)sd).mapseq), BuildExpression(((MapSeqDesignator)sd).exp));
  		else
  			throw new InternalError("Type '"+sd.toString()+"' is not subtype of 'VDMPP StateDesignator' or not yet supported, '"+sd.getLocation()+"'");
  	}
  
//-------------- converting VDM implicit function to Java method--------------   
  public IJavaMethodDefinition BuildDefBlock (final ImplicitFunctionDefinition ImpFunc, final String owner) throws CGException {

	// --------------- getting access definition-----------------
	  IJavaScope J_scope = (IJavaScope) ConvertScopeToJavaScope((AccessSpecifier) ImpFunc.accessSpecifier);
      IJavaAccessDefinition J_adf = new JavaAccessDefinition(ImpFunc.isStatic(), J_scope);
      
      
   // -------------get modifiers (explicit functions are not abstract)-------------------------------
      IJavaModifier J_mdfr = null;
      J_mdfr = new JavaModifier(ImpFunc.body instanceof SubclassResponsibilityExpression, ImpFunc.isUpdatable());  
      
   // -------------get Identifier--------------------
      String name = ImpFunc.name.getIdentifier().name;
      IJavaIdentifier method_name = new JavaIdentifier(name);
      
// --------------- getting return type ------------------------------
	        IJavaType type = null;
	        type = (IJavaType) Vdm2JavaType.ConvertType(ImpFunc.getType().getFunction().result);
	        
//------------------get parameter list-------------------------------------------------------	        
	       List<IJavaBind> pars = null; 
	       pars = (Vector<IJavaBind>) buildParameters(ImpFunc.getParamPatternList(), ImpFunc.getType().getFunction().parameters);
//----------------------- get method body----------------------------------------	     
	        IJavaMethodBody body = null;
	        List<IJavaStatement> statement_list = new Vector<IJavaStatement>();
	        IJavaStatement statement = new JavaEmptyStatement(); 
	       //statement = (IJavaStatement) BuildStatement(ImpFunc.body);
	       statement_list.add(statement);
	     //-------------getting preconditions----------
	       IJavaExpression Pre = new JavaEmptyExpression();
	       if (!(ImpFunc.precondition == null))
	    	   Pre = (IJavaExpression) BuildExpression(ImpFunc.precondition);
	       //---------converting postcondition-----------
	       String post = "";
	       if (!(ImpFunc.postcondition == null))
	    	   post = ImpFunc.postcondition.toString();
	       body = new JavaMethodBody(Pre, statement_list, (ImpFunc.body instanceof SubclassResponsibilityExpression), true, post);
	        return (IJavaMethodDefinition) new JavaMethodDefinition(false, J_adf, J_mdfr, method_name, type, pars, body);
	  }
  
  //------------- Building parameters from VDM++--------------------------------------
  private Vector buildParameters (final List<PatternList> lstPL, TypeList tl) throws CGException {

    Vector<JavaTypeBind> v_jtb = new Vector<JavaTypeBind>();
    List<IJavaPattern> pattern_list = new Vector<IJavaPattern>();
    int listsize = lstPL.size();
    for (int i = 0; i < listsize; i++) {
    	int PLsize = lstPL.get(i).size();
    	for (int j = 0; j < PLsize; j++){
    		if (lstPL.get(i).get(j) instanceof SetPattern){
    			SetPattern sp = (SetPattern) lstPL.get(i).get(j);
    			List<IJavaPattern> lstJP = new Vector<IJavaPattern>(); 
    			int max_var = sp.getVariableNames().size();
    			for (int z = 0; z < max_var; z++)
    				lstJP.add(new JavaPatternIdentifier(new JavaIdentifier(sp.getVariableNames().get(z).getIdentifier().name)));
    			pattern_list.add(new JavaSetPattern(lstJP));
    		}
    	pattern_list.add(new JavaPatternIdentifier(new JavaIdentifier(lstPL.get(i).get(j).getVariableNames().get(0).getIdentifier().name)));
    }
   }
    List<IJavaType> typeList = new Vector<IJavaType>();
    int max_tl = tl.size();
    for(int i= 0; i < max_tl; i++) {
    	typeList.add((IJavaType) Vdm2JavaType.ConvertType(tl.get(i)));
    }
  
    v_jtb.add(new JavaTypeBind(pattern_list, typeList));
    return (Vector) v_jtb; 
  }

  //--------------Building Expressions from VDM++ expressions----------------------
  public IJavaExpression BuildExpression(org.overturetool.vdmj.expressions.Expression fx) {
	  Double db = null;
	  if (fx instanceof IntegerLiteralExpression) {
		  IntegerLiteralExpression ie = (IntegerLiteralExpression) fx;
		  Long i = ie.value.value;
		  IJavaSymbolicLiteralExpression JSLE = new JavaSymbolicLiteralExpression(new JavaNumericLiteral(i));
		  return (IJavaExpression) JSLE;
	  }
	  
	  if (fx instanceof BooleanLiteralExpression) {
		  BooleanLiteralExpression ie = (BooleanLiteralExpression) fx;
		  boolean i = ie.value.value;
		  IJavaSymbolicLiteralExpression JSLE = new JavaSymbolicLiteralExpression(new JavaBooleanLiteral(i));
		  return (IJavaExpression) JSLE;
	  }
	  
	  if (fx instanceof RealLiteralExpression)
		  {RealLiteralExpression rle = (RealLiteralExpression) fx;
		  db = rle.value.value;
	  IJavaSymbolicLiteralExpression JSLE = new JavaSymbolicLiteralExpression(new JavaRealLiteral(db));
	  return (IJavaExpression) JSLE;}
	  
	  if (fx instanceof StringLiteralExpression) {
		  StringLiteralExpression sl = (StringLiteralExpression) fx;
		  return (IJavaExpression) new JavaStringLiteralExpression(sl.value.value);
	  }
	  
	  if (fx instanceof NewExpression) {
		  NewExpression ne = (NewExpression) fx;
		  List<IJavaExpression> je = new Vector<IJavaExpression>();
		  if (!ne.args.isEmpty()) {
			  for (int i = 0; i < ne.args.size(); i++) {
				  je.add(BuildExpression(ne.args.get(i)));
			  }
		  }
		  return (IJavaExpression) new JavaNewExpression(new JavaName(new JavaIdentifier(ne.classname.name)), je);
	  }
	  
	  if (fx instanceof SelfExpression) {
		  return (IJavaExpression) new JavaThisExpression();
	  }
	  
	  if (fx instanceof IsExpression) {
		  IsExpression is = (IsExpression) fx;
		  if (is.basictype != null) {
			  try {
				return (IJavaExpression) new JavaIsBasicTypeExpression(Vdm2JavaType.ConvertType(is.basictype), BuildExpression(is.test));
			} catch (CGException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  else 
			  return (IJavaExpression) new JavaIsExpression(new JavaName(new JavaIdentifier(is.typename.name)), BuildExpression(is.test));
	  }
	  
	  if (fx instanceof SubseqExpression) {
		  return (IJavaExpression) new JavaSubVectorExpression(BuildExpression(((SubseqExpression) fx).seq), BuildExpression(((SubseqExpression) fx).from), BuildExpression(((SubseqExpression) fx).to));
	  }
	  
	  if (fx instanceof SeqExpression) {
		  if (fx instanceof SeqEnumExpression) {
			  if (!((SeqEnumExpression)fx).members.isEmpty()) {
				  int esize = ((SeqEnumExpression)fx).members.size();
				  List<IJavaExpression> lstje = new Vector<IJavaExpression>();
				  for (int i = 0; i < esize; i++) {
					  lstje.add(BuildExpression(((SeqEnumExpression)fx).members.get(i)));
				  }
				  return (IJavaExpression) new JavaVectorEnumExpression(lstje);
			  }
			  else {
				  return (IJavaExpression) new JavaVectorEnumExpression();
			  }
		  }
		  
		  if (fx instanceof SeqCompExpression) {
			  SeqCompExpression sce = (SeqCompExpression) fx;
			  List<IJavaBind> jb = new Vector<IJavaBind>();
				  jb.add(new JavaSetBind(BuildExpression(sce.setbind.set)));
			  return (IJavaExpression) new JavaSetCompExpression(jb, BuildExpression(sce.predicate));
		  }
		  throw new InternalError("Sequence Expression '"+fx.kind()+"' is not subtype of 'VDMPP Expression' or not yet supported, '"+fx.getLocation()+"'");
	  }
	  
	  if (fx instanceof BinaryExpression)
	  {
		  BinaryExpression be = (BinaryExpression) fx;
		  if (fx instanceof EqualsExpression) {
			  EqualsExpression ee = (EqualsExpression) fx;
			  IJavaExpression jee;
			  if (ee.left instanceof RealLiteralExpression || ee.left instanceof IntegerLiteralExpression || ee.left instanceof BooleanLiteralExpression || ee.left instanceof BinaryExpression || ee.left instanceof UnaryExpression) {
				  IJavaBinaryOperator jbo = (IJavaBinaryOperator) BuildOperator(be.op);
				  jee = new JavaBinaryExpression(BuildExpression(be.left), jbo, BuildExpression(be.right));
			  }
			  else {
				  IJavaBinaryObjectOperator jboo = (IJavaBinaryObjectOperator) BuildObjectOperator(be.op);
				  jee = new JavaEqualsExpression(BuildExpression(be.left), jboo , BuildExpression(be.right));
			  }			  
			  return (IJavaExpression) jee;
		  }
		  
		  if (fx instanceof ImpliesExpression) {
			  return (IJavaExpression) new JavaImplicationExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof EquivalentExpression) {
			  return (IJavaExpression) new JavaBiimplicationExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof DivExpression) {
			  return (IJavaExpression) new JavaIntDivExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof RemExpression) {
			  return (IJavaExpression) new JavaRemainderExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof ModExpression) {
			  return (IJavaExpression) new JavaModulusExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof StarStarExpression) {
			  return (IJavaExpression) new JavaStarStarExpression(BuildExpression(be.left), BuildExpression(be.right));
		  }
		  
		  if (fx instanceof NotEqualExpression) {
			  NotEqualExpression ee = (NotEqualExpression) fx;
			  IJavaExpression jee;
			  if (ee.left instanceof RealLiteralExpression || ee.left instanceof IntegerLiteralExpression || ee.left instanceof BooleanLiteralExpression) {
				  IJavaBinaryOperator jbo = (IJavaBinaryOperator) BuildOperator(be.op);
				  jee = new JavaBinaryExpression(BuildExpression(be.left), jbo, BuildExpression(be.right));
			  }
			  else {
				  JavaBinaryObjectOperator jboo = new JavaBinaryObjectOperator();
				  jboo.setEnum(JavaBinaryObjectOperatorEnum.EEQUALS);
				  JavaUnaryOperator juo = new JavaUnaryOperator();
				  juo.setEnum(JavaUnaryOperatorEnum.ENOT);
				  jee = new JavaUnaryExpression(juo, (new JavaEqualsExpression(BuildExpression(be.left), jboo , BuildExpression(be.right))));
			  }			  
			  return (IJavaExpression) jee;
		  }
		  
		  if(fx instanceof SeqConcatExpression) {
		  	  SeqConcatExpression sc = (SeqConcatExpression) fx;
		  	  return (IJavaExpression) new JavaVectorConcatExpression(BuildExpression(sc.left), BuildExpression(sc.right));
		  }
		  
		  if(fx instanceof PlusPlusExpression) {
		  	  return (IJavaExpression) new JavaPlusPlusExpression(BuildExpression(((PlusPlusExpression) fx).left), BuildExpression(((PlusPlusExpression) fx).right));
		  }
		  
		  if (fx instanceof SetUnionExpression) {
			  return (IJavaExpression) new JavaSetUnionExpression(BuildExpression(((SetUnionExpression) fx).left), BuildExpression(((SetUnionExpression) fx).right));
		  }
		  if (fx instanceof SetIntersectExpression) {
			  return (IJavaExpression) new JavaSetIntersectExpression(BuildExpression(((SetIntersectExpression) fx).left), BuildExpression(((SetIntersectExpression) fx).right));
		  }
		  if (fx instanceof InSetExpression) {
			  return (IJavaExpression) new JavaInSetExpression(BuildExpression(((InSetExpression) fx).right), BuildExpression(((InSetExpression) fx).left));
		  }
		  if (fx instanceof NotInSetExpression) {
			  return (IJavaExpression) new JavaNotInSetExpression(BuildExpression(((InSetExpression) fx).right), BuildExpression(((InSetExpression) fx).left));
		  }
		  if (fx instanceof SetDifferenceExpression) {
			  return (IJavaExpression) new JavaSetDifferenceExpression(BuildExpression(((SetDifferenceExpression) fx).left), BuildExpression(((SetDifferenceExpression) fx).right));
		  }
		  if (fx instanceof SubsetExpression) {
			  return (IJavaExpression) new JavaSubSetExpression(BuildExpression(((SubsetExpression) fx).right), BuildExpression(((SubsetExpression) fx).left));
		  }
		  if (fx instanceof ProperSubsetExpression) {
			  return (IJavaExpression) new JavaProperSubsetExpression(BuildExpression(((ProperSubsetExpression) fx).left), BuildExpression(((ProperSubsetExpression) fx).right));
		  }
		  if (fx instanceof MapUnionExpression) {
			  return (IJavaExpression) new JavaMapUnionExpression(BuildExpression(((MapUnionExpression) fx).left), BuildExpression(((MapUnionExpression) fx).right));
		  }
		  if (fx instanceof CompExpression) {
			  return (IJavaExpression) new JavaCompositionExpression(BuildExpression(((CompExpression) fx).left), BuildExpression(((CompExpression) fx).right));
		  }
		  if (fx instanceof DomainResByExpression) {
			  return (IJavaExpression) new JavaDomainResByExpression(BuildExpression(((DomainResByExpression) fx).left), BuildExpression(((DomainResByExpression) fx).right));
		  }
		  if (fx instanceof DomainResToExpression) {
			  return (IJavaExpression) new JavaDomainResToExpression(BuildExpression(((DomainResToExpression) fx).left), BuildExpression(((DomainResToExpression) fx).right));
		  }
		  if (fx instanceof RangeResByExpression) {
			  return (IJavaExpression) new JavaRangeResByExpression(BuildExpression(((RangeResByExpression) fx).left), BuildExpression(((RangeResByExpression) fx).right));
		  }
		  if (fx instanceof RangeResToExpression) {
			  return (IJavaExpression) new JavaRangeResToExpression(BuildExpression(((RangeResToExpression) fx).left), BuildExpression(((RangeResToExpression) fx).right));
		  }
		  
		  IJavaBinaryOperator jbo = (IJavaBinaryOperator) BuildOperator(be.op);
		  IJavaBinaryExpression jbe = new JavaBinaryExpression(BuildExpression(be.left), jbo, BuildExpression(be.right));
		  return (IJavaExpression) jbe;
	  }
	  
	  if(fx instanceof VariableExpression)
	  {
		  VariableExpression ve = (VariableExpression) fx;
		  IJavaName jn = new JavaName(new JavaIdentifier(ve.name.getIdentifier().toString()));
		  return (IJavaExpression) jn;
	  }
	  
	  if (fx instanceof UnaryExpression) {
		  UnaryExpression ue = (UnaryExpression) fx;
	      if (ue instanceof FloorExpression) {
	    	  JavaUnaryOperator juo = new JavaUnaryOperator();
	    	  juo.setEnum(JavaUnaryOperatorEnum.EFLOOR);
	    	  AddImports("import java.lang.Math;");
		  return (IJavaExpression) new JavaUnaryExpression(juo, BuildExpression(ue.exp));
	      }
	      if (ue instanceof UnaryMinusExpression) {
	    	  JavaUnaryOperator juo = new JavaUnaryOperator();
	    	  juo.setEnum(JavaUnaryOperatorEnum.EMINUS);
		  return (IJavaExpression) new JavaUnaryExpression(juo, BuildExpression(ue.exp));
	      }
	      if (ue instanceof UnaryPlusExpression) {
	    	  JavaUnaryOperator juo = new JavaUnaryOperator();
	    	  juo.setEnum(JavaUnaryOperatorEnum.EPLUS);
		  return (IJavaExpression) new JavaUnaryExpression(juo, BuildExpression(ue.exp));
	      }
	      if (ue instanceof NotExpression) {
	    	  JavaUnaryOperator juo = new JavaUnaryOperator();
	    	  juo.setEnum(JavaUnaryOperatorEnum.ENOT);
		  return (IJavaExpression) new JavaUnaryExpression(juo, BuildExpression(ue.exp));
	      }
	      if (ue instanceof AbsoluteExpression) {
	    	  JavaUnaryOperator juo = new JavaUnaryOperator();
	    	  juo.setEnum(JavaUnaryOperatorEnum.EABSOLUTE);
	    	  AddImports("import java.lang.Math;");
		  return (IJavaExpression) new JavaUnaryExpression(juo, BuildExpression(ue.exp));
	      }
	      if (fx instanceof HeadExpression) {
			  HeadExpression hd = (HeadExpression) fx;
			  return (IJavaExpression) new JavaHeadExpression(BuildExpression(hd.exp));
		  }
		  
		  if (fx instanceof TailExpression) {
			  TailExpression tl = (TailExpression) fx;
			  return (IJavaExpression) new JavaTailExpression(BuildExpression(tl.exp));
		  }
		  
		  if (fx instanceof DistUnionExpression) {
			  return (IJavaExpression) new JavaDistUnionExpression(BuildExpression(((DistUnionExpression) fx).exp));
		  }
		  if (fx instanceof DistIntersectExpression) {
			  return (IJavaExpression) new JavaDistIntersectionExpression(BuildExpression(((DistIntersectExpression) fx).exp));
		  }
		  if (fx instanceof PowerSetExpression) {
			  return (IJavaExpression) new JavaPowerSetExpression(BuildExpression(((PowerSetExpression) fx).exp));
		  }
		  if (fx instanceof CardinalityExpression) {
			  return (IJavaExpression) new JavaCardinalityExpression(BuildExpression(((CardinalityExpression) fx).exp));
		  }
		  
		  if (fx instanceof LenExpression) {
			  return (IJavaExpression) new JavaLengthExpression(BuildExpression(((LenExpression) fx).exp));
		  }
		  if (fx instanceof ElementsExpression) {
			  return (IJavaExpression) new JavaElementsExpression(BuildExpression(((ElementsExpression) fx).exp));
		  }
		  if (fx instanceof IndicesExpression) {
			  return (IJavaExpression) new JavaIndexesExpression(BuildExpression(((IndicesExpression) fx).exp));
		  }
		  if (fx instanceof DistConcatExpression) {
			  return (IJavaExpression) new JavaDistConcat(BuildExpression(((DistConcatExpression) fx).exp));
		  }
		  if (fx instanceof MapDomainExpression) {
			  return (IJavaExpression) new JavaMapDomainExpression(BuildExpression(((MapDomainExpression) fx).exp));
		  }
		  if (fx instanceof MapRangeExpression) {
			  return (IJavaExpression) new JavaMapRangeExpression(BuildExpression(((MapRangeExpression) fx).exp));
		  }
		  if (fx instanceof DistMergeExpression) {
			  return (IJavaExpression) new JavaDistMergeExpression(BuildExpression(((DistMergeExpression) fx).exp));
		  }
		  if (fx instanceof MapInverseExpression) {
			  return (IJavaExpression) new JavaMapInverseExpression(BuildExpression(((MapInverseExpression) fx).exp));
		  }
	  }
	  
	  if (fx instanceof ApplyExpression) {
		  ApplyExpression ae = (ApplyExpression) fx;
		  if (ae.root instanceof VariableExpression) {
			  VariableExpression vn = (VariableExpression) ae.root;
			  if(cls.findName(vn.name, cls.nameScope) instanceof InstanceVariableDefinition) {
				  InstanceVariableDefinition iv = (InstanceVariableDefinition) cls.findName(vn.name, cls.nameScope);
				  if (iv.type.isSeq()) {
					  return (IJavaExpression) new JavaVectorApplication(BuildExpression(ae.root), BuildExpression(ae.args.firstElement()));
				  }
				  else 
					  return (IJavaExpression) new JavaMapApplication(BuildExpression(ae.root), BuildExpression(ae.args.firstElement()));
			  }
			  else {
				  List<IJavaExpression> listJE = new Vector<IJavaExpression>();
				  int list_size = ae.args.size();
				  for (int i = 0; i < list_size; i++) {
					  listJE.add(BuildExpression(ae.args.get(i)));
				  }
				  return (IJavaExpression) new JavaApplyExpression(BuildExpression(ae.root), listJE);
			  }				  
		  }
		  
		  else if (ae.root instanceof SeqExpression) {
			  return (IJavaExpression) new JavaVectorApplication(BuildExpression(ae.root), BuildExpression(ae.args.firstElement()));
		  }
		  
		  else if (ae.root instanceof MapExpression)
			  return (IJavaExpression) new JavaMapApplication(BuildExpression(ae.root), BuildExpression(ae.args.firstElement()));
		  else
			  throw new InternalError("Expression '"+fx.kind()+"' is not subtype of 'VDMPP ApplyExpression' or not yet supported, '"+fx.getLocation()+"'");
	  }
	  
	  if (fx instanceof SetExpression) {
		  if (fx instanceof SetEnumExpression) {
			  SetEnumExpression see = (SetEnumExpression) fx;
			  List<IJavaExpression> je = new Vector<IJavaExpression>();
			  for (int i=0; i < see.members.size(); i++) {
				  je.add(BuildExpression(see.members.get(i)));
			  }
			  return (IJavaExpression) new JavaSetEnumExpression(je);
		  }
		  
		  if (fx instanceof SetRangeExpression) {
			  SetRangeExpression sre = (SetRangeExpression) fx;
			  return (IJavaExpression) new JavaSetRangeExpression(BuildExpression(sre.first), BuildExpression(sre.last));
		  }
		  if (fx instanceof SetCompExpression) {
			  SetCompExpression sce = (SetCompExpression) fx;
			  List<IJavaBind> jb = new Vector<IJavaBind>();
			  for (int i = 0; i < sce.bindings.size(); i++) {
				  MultipleSetBind msb = (MultipleSetBind) sce.bindings.get(0);
				  jb.add(new JavaSetBind(BuildExpression(msb.set)));
			  }
			  return (IJavaExpression) new JavaSetCompExpression(jb, BuildExpression(sce.predicate));			  
		  }
	  }
	  
	  if (fx instanceof MapExpression) {
		  if (fx instanceof MapEnumExpression) {
			  MapEnumExpression mee = (MapEnumExpression) fx;
			  List<IJavaMapletExpression> je = new Vector<IJavaMapletExpression>();
			  for (int i=0; i < mee.members.size(); i++) {
				  je.add((IJavaMapletExpression)BuildMapletExpression(mee.members.get(i)));
			  }
			  return (IJavaExpression) new JavaMapEnumExpression(je);
		  }
	  }
	  
	  if (fx instanceof NotYetSpecifiedExpression) {
		  
		  return (IJavaExpression) new JavaNotYetSpecifiedExpression();
	  }
	  
	  if (fx instanceof SubclassResponsibilityExpression) {
		  return (IJavaExpression) new JavaIsSubClassResponsibilityExpression();
	  }
	  
	  if(fx instanceof IfExpression) {
			
			IJavaIfExpression jIF;
			IfExpression IF = (IfExpression) fx;
			List<IJavaElseIfExpression> lst_jes = new Vector<IJavaElseIfExpression>();
			if (!IF.elseList.isEmpty()) {
				int size = IF.elseList.size();
				for (int i = 0; i < size; i++) {
					lst_jes.add(new JavaElseIfExpression(BuildExpression(IF.elseList.get(i).thenExp), BuildExpression(IF.elseList.get(i).elseIfExp)));
				}
			}
			if (!(IF.elseExp == null)) {
				jIF = new JavaIfExpression(BuildExpression(IF.ifExp), BuildExpression(IF.thenExp),lst_jes, BuildExpression(IF.elseExp));
			}
			else 
				jIF = new JavaIfExpression(BuildExpression(IF.ifExp), BuildExpression(IF.thenExp),lst_jes, new JavaEmptyExpression());
		return jIF;
		}
	  if (fx instanceof NilExpression) {
		  return (IJavaExpression) new JavaSymbolicLiteralExpression(new JavaNilLiteral());
	  }
	  
	  if (fx instanceof UndefinedExpression) {
		  return (IJavaExpression) new JavaSymbolicLiteralExpression(new JavaNilLiteral());
	  }
	  
	  if (fx instanceof IsOfClassExpression) {
		  return (IJavaExpression) new JavaIsExpression(new JavaName(new JavaIdentifier(((IsOfClassExpression) fx).classname.name)), BuildExpression(((IsOfClassExpression) fx).exp));
	  }
	  
	  if (fx instanceof SameClassExpression) {
		  return (IJavaExpression) new JavaSameClassMembership(BuildExpression(((SameClassExpression) fx).left), BuildExpression(((SameClassExpression) fx).right));
	  }
	  if (fx instanceof QuoteLiteralExpression) {
		  return (IJavaExpression) new JavaQuoteLiteralExpression(((QuoteLiteralExpression) fx).type.value);
	  }
	  if (fx instanceof MkBasicExpression) {
		  return (IJavaExpression) new JavaTokenExpression(BuildExpression(((MkBasicExpression) fx).arg));
	  }
	  if (fx instanceof MuExpression) {
		  MuExpression mu = (MuExpression) fx;
		  throw new InternalError("MuExpression is currently not supported, still working on it, '"+fx.getLocation()+"'");
	  }
	  
	  throw new InternalError("Type '"+fx.kind()+"' is not subtype of 'VDMPP Expression' or not yet supported, '"+fx.getLocation()+"'");
	    
  }

  private IJavaMapletExpression BuildMapletExpression (MapletExpression mapletExpression) {
		  return (IJavaMapletExpression) new JavaMapletExpression(BuildExpression(((MapletExpression) mapletExpression).left), BuildExpression(((MapletExpression) mapletExpression).right)); 
}


//-------------Converting Opertars---------------------------
  private IJavaBinaryOperator BuildOperator(LexToken op) {
	// TODO Auto-generated method stub
	  JavaBinaryOperator jbo = new JavaBinaryOperator();
	if (op.is(Token.AND))
		jbo.setEnum(JavaBinaryOperatorEnum.EAND);
	if (op.is(Token.DIV))
		jbo.setEnum(JavaBinaryOperatorEnum.EDIV);
	if (op.is(Token.DIVIDE))
		jbo.setEnum(JavaBinaryOperatorEnum.EDIVIDE);
	if (op.is(Token.GT))
		jbo.setEnum(JavaBinaryOperatorEnum.EGT);
	if (op.is(Token.GE))
		jbo.setEnum(JavaBinaryOperatorEnum.EGE);
	if (op.is(Token.LT))
		jbo.setEnum(JavaBinaryOperatorEnum.ELT);
	if (op.is(Token.LE))
		jbo.setEnum(JavaBinaryOperatorEnum.ELE);
	if (op.is(Token.OR))
		jbo.setEnum(JavaBinaryOperatorEnum.EOR);
	if (op.is(Token.REM))
		jbo.setEnum(JavaBinaryOperatorEnum.EREM);
	if (op.is(Token.MOD))
		jbo.setEnum(JavaBinaryOperatorEnum.EMOD);
	if (op.is(Token.NE))
		jbo.setEnum(JavaBinaryOperatorEnum.ENE);
	if (op.is(Token.MINUS))
		jbo.setEnum(JavaBinaryOperatorEnum.EMINUS);
	if (op.is(Token.PLUS))
		jbo.setEnum(JavaBinaryOperatorEnum.EPLUS);
	if (op.is(Token.TIMES))
		jbo.setEnum(JavaBinaryOperatorEnum.EMULTIPLY);
	if (op.is(Token.EQUALSEQUALS))
		jbo.setEnum(JavaBinaryOperatorEnum.EEQ);
	if (op.is(Token.EQUALS))
		jbo.setEnum(JavaBinaryOperatorEnum.EEQEQ); 
	return (IJavaBinaryOperator) jbo;
}

//---------------Building non-primitive operators-------------
  private IJavaBinaryObjectOperator BuildObjectOperator(LexToken op) {
		// TODO Auto-generated method stub
		  JavaBinaryObjectOperator jboo = new JavaBinaryObjectOperator();
		if (op.is(Token.EQUALS))
			jboo.setEnum(JavaBinaryObjectOperatorEnum.EEQUALS); 
		return (IJavaBinaryObjectOperator) jboo;
  }
//---------------Getting simple (primitive type) name ---------------  
private String GetSimpleTypeName (final IJavaType t) throws CGException {

    String varRes_2 = null;
    boolean succ_3 = true;
    {

      succ_3 = true;
      if (!UTIL.equals(new Boolean(true), new Boolean(t instanceof IJavaBooleanType))) 
        succ_3 = false;
      if (succ_3) 
        varRes_2 = new String("boolean");
    }
    if (!succ_3) {

      succ_3 = true;
      if (!UTIL.equals(new Boolean(true), new Boolean(t instanceof IJavaIntType))) 
        succ_3 = false;
      if (succ_3) 
        varRes_2 = new String("int");
    }
    if (!succ_3) {

      succ_3 = true;
      if (!UTIL.equals(new Boolean(true), new Boolean(t instanceof IJavaCharType))) 
        succ_3 = false;
      if (succ_3) 
        varRes_2 = new String("char");
    }
    if (!succ_3) 
      varRes_2 = new String("String");
    return varRes_2;
  }

	public static void AddImports(String Import) {
		if (!imports.contains(new String(Import))) {
			imports.add(Import);
		}
	}
}