package org.overturetool.VDM2JavaCG.VDM2Java;

import jp.co.csk.vdm.toolbox.VDM.*;

import java.util.*;

import java.util.Iterator;
import java.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.overturetool.VDM2JavaCG.main.*;
import org.overturetool.VDM2JavaCG.Utilities.CGCollections;
import org.overturetool.VDM2JavaCG.Utilities.Token;
import org.overturetool.VDM2JavaCG.ast.java.imp.*;
import org.overturetool.VDM2JavaCG.ast.java.itf.*;
import org.overturetool.VDM2JavaCG.main.ClassExstractorFromTexFiles;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.statements.IdentifierDesignator;


@SuppressWarnings("all")
public class JavaAstVisitor extends JavaVisitor {



		  public String result = null;

		  private Long lvl = null;

		  private String nl = null;
		  
		  private String tab = "        ";
		  
		  private boolean import_flag = false;


		  private void init_Visitor () throws CGException {
		    try {

		      result = new String();
		      lvl = new Long(0);
		      nl = new String("");
		    }
		    catch (Exception e){

		      e.printStackTrace(System.out);
		      System.out.println(e.getMessage());
		    }
		  }

		  public JavaAstVisitor () throws CGException {
		    init_Visitor();
		  }
		  
		  public void useNewLineSeparator (final Boolean useNewLine) throws CGException {
		    if (useNewLine.booleanValue()) 
		      nl = "\n";
		    else 
		      nl = ("");
		  }


		  private void printNodeField (final IJavaNode pNode) throws Exception {
		    pNode.accept((IJavaVisitor) this);
		  }
		  
		  

		  private void printBooleanField (final Boolean pval) throws CGException {

		    String rhs_2 = null;
		    if (pval.booleanValue()) 
		      rhs_2 = new String("true");
		    else 
		      rhs_2 = new String("false");
		    result = UTIL.ConvertToString(UTIL.clone(rhs_2));
		  }


		  private void printIntField (final Long pval) throws CGException {

		    
		    result = pval.toString();
		  }
		
		  
		  private void printDoubleField (final Double pval) throws CGException {

		   result = pval.toString();
		  }
		
		  
		  private void printCharField (final Character pval) throws CGException {

		    String rhs_2 = null;
		    rhs_2 = new String();
		    rhs_2 = rhs_2 + pval;
		    result = UTIL.ConvertToString(UTIL.clone(rhs_2));
		  }
		
		  
		  private void printField (final Object fld) throws Exception {
		    if (((fld instanceof Boolean))) 
		      printBooleanField((Boolean) fld);
		    else 
		      if (((fld instanceof Character))) 
		        printCharField((Character) fld);
		      else {
		        if ((UTIL.IsInteger(fld) && ((Number) fld).intValue() >= 0)) 
		          printIntField(UTIL.NumberToLong(fld));
		        else {
		          if ((UTIL.IsReal(fld))) 
		            printDoubleField(UTIL.NumberToReal(fld));
		          else {

		            Boolean cond_6 = null;
		            cond_6 = new Boolean(fld instanceof IJavaNode);
		            if (cond_6.booleanValue()) 
		              printNodeField((IJavaNode) fld);
		            else {
		              printStringField(UTIL.ConvertToString(fld));
		            }
		          }
		        }
		      }
		  }
		
		  
		  private void printStringField (final String str) throws CGException {

		    String rhs_2 = null;
		    String var1_3 = null;
		    var1_3 = new String("\"").concat(str);
		    rhs_2 = var1_3.concat(new String("\""));
		    result = UTIL.ConvertToString(UTIL.clone(rhs_2));
		  }
		
		  
		  private void printSeqofField (final Vector pval) throws Exception {

		    String str = new String("");
		    Long cnt = new Long(pval.size());
		    while ( ((cnt.intValue()) > (new Long(0).intValue()))){

		      Object tmpArg_v_7 = null;
		      if ((1 <= new Long(new Long(new Long(pval.size()).intValue() - cnt.intValue()).intValue() + new Long(1).intValue()).intValue()) && (new Long(new Long(new Long(pval.size()).intValue() - cnt.intValue()).intValue() + new Long(1).intValue()).intValue() <= pval.size())) 
		        tmpArg_v_7 = pval.get(new Long(new Long(new Long(pval.size()).intValue() - cnt.intValue()).intValue() + new Long(1).intValue()).intValue() - 1);
		      else 
		        UTIL.RunTime("Run-Time Error:Illegal index");
		      printField(tmpArg_v_7);
		      String rhs_15 = null;
		      rhs_15 = str.concat(result);
		      str = UTIL.ConvertToString(UTIL.clone(rhs_15));
		      if (((cnt.intValue()) > (new Long(1).intValue()))) {

		        String rhs_21 = null;
		        rhs_21 = str.concat(new String(", "));
		        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
		      }
		      cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue() - new Long(1).intValue())));
		    }
		    result = UTIL.ConvertToString(UTIL.clone(str));
		  }
		
		  
		  public void visitNode (final IJavaNode pNode) {
		    pNode.accept((IJavaVisitor) this);
		  }
		
		  
		/*  public void visitDocument (final IJavaDocument pcmp) throws Exception {

		    String str = null;
		    String var1_2 = null;
		    String var2_4 = null;
		    JavaClassDefinitions jcds = (JavaClassDefinitions) pcmp.getSpecification();
		    var2_4 = jcds.getClassList().get(0).getIdentifier().getName();
		    var1_2 = new String("--BEGIN FileName: ").concat(var2_4);
		    str = var1_2.concat(nl);
		    Boolean cond_6 = null;
		    cond_6 = pcmp.isSpecification();
		    if (cond_6.booleanValue())
				try {
					{

					  IJavaSpecification tmpArg_v_8 = null;
					  tmpArg_v_8 = (IJavaSpecification) pcmp.getSpecification();
					  visitSpecification((IJavaSpecification) tmpArg_v_8);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    String rhs_9 = null;
		    String var1_10 = null;
		    String var1_11 = null;
		    var1_11 = str.concat(result);
		    var1_10 = var1_11.concat(new String("--END FileName: "));
		    String var2_15 = null;
		    var2_15 = jcds.getClassList().get(0).getIdentifier().getName();
		    rhs_9 = var1_10.concat(var2_15);
		    result = UTIL.ConvertToString(UTIL.clone(rhs_9));
		  }*/
		
		  
		/*  public void visitSpecification (final IJavaSpecification jspec) {

		    String str = nl;
		    JavaClassDefinitions jcds = (JavaClassDefinitions) jspec;
		    {

		      List<IJavaClassDefinition> sq_2 = null;
		      sq_2 = jcds.getClassList();
		      IJavaClassDefinition node = null;
		      for (Iterator enm_16 = sq_2.iterator(); enm_16.hasNext(); ) {

		        IJavaClassDefinition elem_3 = (IJavaClassDefinition) enm_16.next();
		        node = (IJavaClassDefinition) elem_3;
		        {

		          try {
					printNodeField((IJavaNode) node);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		          String rhs_8 = null;
		          String var1_9 = null;
		          String var1_10 = null;
		          var1_10 = str.concat(nl);
		          var1_9 = var1_10.concat(result);
		          rhs_8 = var1_9.concat(nl);
		          str = UTIL.ConvertToString(UTIL.clone(rhs_8));
		        }
		      }
		    }
		    result = UTIL.ConvertToString(UTIL.clone(str));
		  }*/
		
		  
		  public void visitClassDefinition (final IJavaClassDefinition pcmp) {

		    String str = "";
		    if (!pcmp.getNested()) {
		    	str = "import org.overturetool.VDM2JavaCG.Utilities.*;".concat(nl).concat("import java.util.*;").concat(nl);
		    }
		    //---------printing the imports------------
		    if(!import_flag) {
		    	if(!vdm2java.imports.isEmpty()){
		    		int size = vdm2java.imports.size();
		    		for(int i = 0; i <size; i++) {
		    			str = str.concat(vdm2java.imports.get(i)).concat(nl);
		    		}
		    		str = str.concat(nl).concat(nl);
		    	}
		    	import_flag = true;
		    }
		    //---------printing access information-------------
		    IJavaAccessDefinition jAD = (IJavaAccessDefinition) pcmp.getAccessdefinition();
		    try {
				printNodeField((IJavaNode) jAD);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    str = str.concat(result);
		    
		    //-------------printing the modifiers------------------
		    IJavaModifier jm = (IJavaModifier) pcmp.getModifiers();
		    try {
				printNodeField((IJavaNode) jm);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    str = str.concat(result);
		    
		    str = str.concat("class ").concat(pcmp.getIdentifier().getName());
		    Boolean cond_4 = null;
		    cond_4 = !(pcmp.getInheritanceClause().getImplementList().isEmpty()) || !(pcmp.getInheritanceClause().getExtends().isEmpty());
		    if (cond_4.booleanValue())
				try {
					{
					  IJavaInheritanceClause inh = null;
					  inh = (IJavaInheritanceClause) pcmp.getInheritanceClause();
					  printNodeField((IJavaNode) inh);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else 
		      result = "";
	
		    str = str.concat(result).concat(" {").concat(nl);
		    
		      IJavaDefinitionList jdl = pcmp.getBody();
		      IJavaDefinition db = null;
		      for (Iterator enm = jdl.getDefinitionList().iterator(); enm.hasNext(); )
				try {
					{

						db = (IJavaDefinition) enm.next();
					    {

					      printNodeField((IJavaNode) db);
					      String tmp_str = null;
					      tmp_str = str.concat(nl).concat(result);
					      str = tmp_str;
					      
					    }
					  }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!pcmp.getNested()) {
					str = str.concat("public boolean equals (Object obj) {").concat(nl).concat(tab).concat("if (!(obj instanceof ").concat(pcmp.getIdentifier().getName()).concat( "))");
					str = str.concat(nl).concat(tab).concat("  ").concat("return false;").concat(nl).concat(tab);
					str = str.concat("else").concat(nl).concat(tab).concat("  ").concat("return true; }").concat(nl);
				}
		    str = str.concat("}");
		    result = str;
		  }
		
		  public void visitInstanceVariableDefinition(IJavaInstanceVariableDefinition jivd) {
			  String str = "";
		//----------printing access modifiers---------------------------
			  IJavaAccessDefinition jad = (IJavaAccessDefinition) jivd.getAccess();
			  try {
				printNodeField((IJavaNode) jad);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  str = str.concat(result).concat(" ");
			  
			  IJavaModifier jm = (IJavaModifier) jivd.getModifiers();
			  try {
				printNodeField((IJavaNode) jm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			
			//-------------------printing variable type and name ---------------------------
			IJavaType vt = (IJavaType) jivd.getType();

		    try {
				printNodeField((IJavaNode) vt);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			str = str.concat(result).concat(" ").concat(jivd.getName().getName());
			//----------------------printing initialization if any------------------
			if (jivd.getInitialized()) {
				try {
					printNodeField((IJavaNode) jivd.getExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(" ").concat("=").concat(" ").concat(result);
			}
			result = str.concat(";");			
		  }
		  
		  public void visitInterfaceDefinition(IJavaInterfaceDefinition pNode) {
			  String str = "";
			    
			//---------printing access information-------------
			    IJavaAccessDefinition jAD = (IJavaAccessDefinition) pNode.getAccessdefinition();
			    try {
					printNodeField((IJavaNode) jAD);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    str = str.concat(result);
			    
			    //-------------printing the modifiers------------------
			    IJavaModifier jm = (IJavaModifier) pNode.getModifiers();
			    try {
					printNodeField((IJavaNode) jm);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    str = str.concat(result);
			    	
			    str = str.concat("interface ").concat(pNode.getIdentifier().getName());
			    Boolean cond_4 = null;
			    cond_4 = !pNode.getInheritanceClause().getExtends().isEmpty();
			    if (cond_4.booleanValue())
					try {
						{
						  IJavaInheritanceClause tmpArg_v_7 = null;
						  tmpArg_v_7 = (IJavaInheritanceClause) pNode.getInheritanceClause();
						  printNodeField((IJavaNode) tmpArg_v_7);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else 
			      result = UTIL.ConvertToString(UTIL.clone(new String("")));
		
			    str = str.concat(result).concat(" {");
			    String str1 = "";
			    if (!pNode.getBody().getDefinitionList().isEmpty()) {
			      IJavaDefinitionList jdl = pNode.getBody();
			      IJavaDefinition db = null;
			      for (Iterator enm = jdl.getDefinitionList().iterator(); enm.hasNext(); )
					try {
						{

							db = (IJavaDefinition) enm.next();
						    {

						      printNodeField((IJavaNode) db);
						      String rhs_19 = null;
						      String var1_20 = null;
						      var1_20 = str1.concat(nl);
						      rhs_19 = var1_20.concat(result);
						      str1 = UTIL.ConvertToString(UTIL.clone(rhs_19));
						      
						    }
						  }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    str = str.concat("}").concat(nl).concat(str1);
			    result = str;
		  }
		  
		  public void visitEmptyDefinition (final IJavaEmptyDefinition je) {
			  result = "";
		  }
		  
		  public void visitInheritanceClause (final IJavaInheritanceClause pcmp) {

		    String str = new String("");
		    List<IJavaIdentifier> impl_list  = pcmp.getImplementList();
		    List<IJavaIdentifier> extend = pcmp.getExtends();
		    Long length = new Long(impl_list.size());
		    Long i = new Long(1);
		    if (!impl_list.isEmpty())
		    	str = str.concat(" implements ");
		    while (((i.intValue()) <= (length.intValue()))){

		      String rhs_6 = null;
		      String var2_8 = null;
		      if ((1 <= i.intValue()) && (i.intValue() <= impl_list.size())) 
		        var2_8 = impl_list.get(i.intValue() - 1).getName();
			else
				try {
					UTIL.RunTime("Run-Time Error:Illegal index");
				} catch (VDMRunTimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      rhs_6 = str.concat(var2_8);
		      str = UTIL.ConvertToString(UTIL.clone(rhs_6));
		      try {
				i = UTIL.NumberToLong(UTIL.clone(new Long(i.intValue() + new Long(1).intValue())));
			} catch (VDMRunTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      if (((i.intValue()) <= (length.intValue()))) {

		        String rhs_17 = null;
		        rhs_17 = str.concat(new String(" ,"));
		        str = UTIL.ConvertToString(UTIL.clone(rhs_17));
		      }
		    }
		    if (!extend.isEmpty()) {
		    	str = str.concat(" extends ").concat(extend.get(0).getName());
		    }
		    result = str;
		  }
		
		  public void visitAccessDefinition (final IJavaAccessDefinition pcmp) {
			  //System.out.println("what?!");
		    String str = new String("");
		    Boolean cond_2 = null;
		    IJavaScope js = null;
		    js = (IJavaScope) pcmp.getScope();
				try {
					printNodeField((IJavaNode) js);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		    str = str.concat(result);
		    
		    cond_2 = pcmp.getStaticAccsess();
		    if (cond_2.booleanValue()) 
		      str = str.concat(" static");
		    result = str.concat(" ");
		  }
		  
		
		  public void visitIdentifier (final IJavaIdentifier pcmp) {

			    String str = new String("");
			    String list = new String(pcmp.getName());
			    list = str.concat(list);
			    result = UTIL.ConvertToString(UTIL.clone(list));
			  
			  }	  
		  
		  public void visitScope (final IJavaScope pNode) {
	   
		    boolean succ_2 = true;
		 
		      if (!pNode.isPUBLIC()) 
		        succ_2 = false;
		      if (succ_2) 
		        result = "public";
		      else {

		        succ_2 = true;
		        if (!pNode.isPRIVATE()) 
		          succ_2 = false;
		        if (succ_2) 
		          result = "private";
		        else {

		          succ_2 = true;
		          if (!pNode.isPROTECTED()) 
		            succ_2 = false;
		          if (succ_2) 
		            result = "protected";
		          
		          else {

		            try {
						UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
					} catch (VDMRunTimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		          }
		        }
		      }
		  }
		
		  public void visitPattern (final IJavaPattern pNode) {
		    pNode.accept((IJavaVisitor) this);
		  }
		
		  public void visitBinaryExpression(IJavaBinaryExpression pNode) {
			     String str = "";
			     try {
					printNodeField((IJavaNode) pNode.getLhsExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				
				try {
					printNodeField((IJavaNode) pNode.getOperator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(" ").concat(result);

				try {
					printNodeField((IJavaNode) pNode.getRhsExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				result = str;
		  }
		  
		  public void visitImplicationExpression(IJavaImplicationExpression pNode) {
			  String str = "Utils.implication(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		
		  public void visitBiimplicationExpression(IJavaBiimplicationExpression pNode) {
			  String str = "Utils.biimplication(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitIntDivExpression(IJavaIntDivExpression pNode) {
			  String str = "Utils.div(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitRemainderExpression(IJavaRemainderExpression pNode) {
			  String str = "Utils.rem(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitModulusExpression(IJavaModulusExpression pNode) {
			  String str = "Utils.mod(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitStarStarExpression(IJavaStarStarExpression pNode) {
			  String str = "Utils.StarStar(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitCompositionExpression(IJavaCompositionExpression pNode) {
			  String str = "CGCollections.comp(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitMapUnionExpression(IJavaMapUnionExpression pNode) {
			  String str = "CGCollections.munion(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitDomainResByExpression(IJavaDomainResByExpression pNode) {
			  String str = "CGCollections.DomBy(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitRangeResByExpression(IJavaRangeResByExpression pNode) {
			  String str = "CGCollections.RangeBy(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitDomainResToExpression(IJavaDomainResToExpression pNode) {
			  String str = "CGCollections.DomTo(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitRangeResToExpression(IJavaRangeResToExpression pNode) {
			  String str = "CGCollections.RangeTo(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitLengthExpression(IJavaLengthExpression pNode) {
			  String str = "";
			     try {
					printNodeField((IJavaNode) pNode.getSeqName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(".size()");
				result = str;
		  }
		  
		  public void visitElementsExpression(IJavaElementsExpression pNode) {
			  String str = "CGCollections.elems(";
			     try {
					printNodeField((IJavaNode) pNode.getSeqname());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitIndexesExpression(IJavaIndexesExpression pNode) {
			  String str = "CGCollections.indexes(";
			     try {
					printNodeField((IJavaNode) pNode.getSeqName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitDistConcat(IJavaDistConcat pNode) {
			  String str = "CGCollections.conc(";
			     try {
					printNodeField((IJavaNode) pNode.getSeqName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitSeqCompExpression(final IJavaSeqCompExpression jsc) {
			  String str = "CGCollections.SeqComprehension(";
				try {
					printNodeField((IJavaNode) ((JavaInSetExpression)jsc.getPredicate()).getOwner());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(",");
				try {
					printNodeField((IJavaNode) ((JavaSetBind)jsc.getBindlist().get(0)).getSetName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				result = str.concat(")");
		  }
		  
		  public void visitSubVectorExpression(final IJavaSubVectorExpression jsc) {
			  String str = "CGCollections.SubSequence(";
			  try {
					printNodeField((IJavaNode) jsc.getVecName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");
				try {
					printNodeField((IJavaNode) jsc.getFrom());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat("-1, ");
				try {
					printNodeField((IJavaNode) jsc.getTo());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				result = str.concat("-1)");
		  }
		  
		  public void visitVectorApplication(IJavaVectorApplication pNode) {
			  String str = "";
			     try {
					printNodeField((IJavaNode) pNode.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(".elementAt(");

				try {
					printNodeField((IJavaNode) pNode.getAt());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat("-1)");
				result = str;
		  }
		  
		  public void visitMapApplication(IJavaMapApplication pNode) {
			  String str = "";
			     try {
					printNodeField((IJavaNode) pNode.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(".get(");

				try {
					printNodeField((IJavaNode) pNode.getKey());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitPlusPlusExpression(IJavaPlusPlusExpression pNode) {
			  String str = "Utils.PlusPlus(";
			     try {
					printNodeField((IJavaNode) pNode.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(", ");

				try {
					printNodeField((IJavaNode) pNode.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitVectorConcatExpression(IJavaVectorConcatExpression pNode) {
			  String str = "CGCollections.concatenation(";
			  try {
				printNodeField((IJavaNode) pNode.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(", ");
			try {
				printNodeField((IJavaNode) pNode.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")");
			result = str;			
		  }
		  
		  public void visitUnaryExpression(IJavaUnaryExpression pNode) {
			  String str = "";
				try {
					printNodeField((IJavaNode) pNode.getOperator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat("(");

				try {
					printNodeField((IJavaNode) pNode.getExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitEqualsExpression(IJavaEqualsExpression pNode) {
			  String str = "(";
			     try {
					printNodeField((IJavaNode) pNode.getLhsExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				
				try {
					printNodeField((IJavaNode) pNode.getOperator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat("(");

				try {
					printNodeField((IJavaNode) pNode.getRhsExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(")");
				result = str;
		  }
		  
		  public void visitLiteral (final IJavaLiteral pNode) {
		    pNode.accept((IJavaVisitor) this);
		  }
	  
		  public void visitType (final IJavaType pNode) {
		    pNode.accept((IJavaVisitor) this);
		  }
		
		  public void visitPatternIdentifier (final IJavaPatternIdentifier pcmp) {

		    String str = null;
		    String var1_2 = null;
		    var1_2 = pcmp.getIdentifier().getName();
		    str = var1_2.concat(new String(""));
		    result = UTIL.ConvertToString(UTIL.clone(str));
		  }

		  public void visitApplyExpression(IJavaApplyExpression pNode) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) pNode.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat("(");
			int size = pNode.getExpressionList().size();
			for (int i = 0; i < size; i++) {
				try {
					printNodeField((IJavaNode) pNode.getExpressionList().get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				if ((i+1) < size)
					str=str.concat(", ");
			}
			str = str.concat(")");
			result = str;
		  }
		  
		  public void visitSymbolicLiteralExpression (final IJavaSymbolicLiteralExpression pcmp) {

		    String str = new String("");
		    IJavaLiteral tmpArg_v_3 = null;
		    tmpArg_v_3 = (IJavaLiteral) pcmp.getLiteral();
		    try {
				printNodeField((IJavaNode) tmpArg_v_3);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		
		  public void visitVectorEnumExpression(IJavaVectorEnumExpression pNode) {
			  if (pNode.getElements().isEmpty()) {
				  result = "new Vector()";
			  }
			  else {
				  String str = "CGCollections.SeqEnumeration(";
				  int size = pNode.getElements().size();
				  for (int i = 0; i < size; i++) {
					  try {
						printNodeField((IJavaNode) pNode.getElements().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result);
					if (i < size-1) {
						str = str.concat(", ");
					}
				  }
				  result = str.concat(")");
			  }
		  }
		  
		  public void visitStringLiteralExpression(IJavaStringLiteralExpression jsl) {
			  String str = "";
			  result = str.concat("\"").concat(jsl.getString()).concat("\"");
		  }
		  
		  public void visitTextLiteral (final IJavaTextLiteral pcmp) {

		    String str = null;
		    str = pcmp.getVal();
		    String rhs_2 = null;
		    String var1_3 = null;
		    var1_3 = new String("\"").concat(str);
		    rhs_2 = var1_3.concat(new String("\""));
		    result = UTIL.ConvertToString(UTIL.clone(rhs_2));
		  }
		
		  public void visitQuoteLiteralExpression(IJavaQuoteLiteralExpression jsl) {
			  String str = "(new Quote(".concat(jsl.getLiteral()).concat("))");
			  result = str;
		  }
		  
		  public void visitTokenExpression(IJavaTokenExpression jsl) {
			  String str = "(new Token(";
			  try {
				printNodeField((IJavaNode) jsl.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat("))");
			  result = str;
		  }
		  
		  public void visitCharacterLiteral (final IJavaCharacterLiteral pcmp) {

		    String str = new String("\'").concat(pcmp.getVal().toString()).concat(new String("\'"));
		    result = str;
		  }


		  public void visitCharType (final IJavaCharType var_1_1) {

		    result = new String("Character");
		  }
		  
		  public void visitQuoteType (final IJavaQuoteType q) {

			    result = q.getLiterral();
			  }
		  public void visitTokenType (final IJavaTokenType t) {

			    result = "Token";
			  }
		  
		  public void visitStringType(IJavaStringType pNode) {
			  result = "String";
		  }
		  
		  public void visitName (final IJavaName pcmp) {

		    String str = new String("");
		    Boolean flag = !pcmp.getIdentifier().getName().isEmpty();
		    if (flag.booleanValue()) {

		      String name = null;
		      name = pcmp.getIdentifier().getName();
		      str = str.concat(name);
		    }
		    result = str;
		    
		  }


		  public void visitIntType (final IJavaIntType ji) {
			  
		    result = "Integer";
		  }
		  
		  public void visitClassType (final IJavaClassType jst) {

			    result = jst.getName();
			  }
		  
		  public void visitBooleanType (final IJavaBooleanType var_1_1) {

		    result = "Boolean";
		  }
		  
		  public void visitGenMap(IJavaGenMap mt) {
			  String str = "HashMap<";
			  try {
				printNodeField((IJavaNode) mt.getKey());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) mt.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(">");
	        result = str;
		  }
		  
		  public void visitBiMap(IJavaBiMap mt) {
			  String str = "BiMap<";
			  try {
				printNodeField((IJavaNode) mt.getKey());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) mt.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(">");
	        result = str;
		  }
	
		  public void visitBinaryOperator(final IJavaBinaryOperator pNode) {
			  String str = null;
			  if (pNode.isGT()) 
				  str = ">";
			  if (pNode.isLT()) 
				  str = "<";
			  if (pNode.isEQEQ())
				  str = "==";
			  if (pNode.isEQ())
				  str = "=";
			  if (pNode.isAND())
				  str = "&&";
			  if (pNode.isDIV())
				  str = "/";
			  if (pNode.isDIVIDE())
				  str = "/";
			  if (pNode.isGE())
				  str = ">=";
			  if (pNode.isLE())
				  str = "<=";
			  if (pNode.isMINUS())
				  str = "-";
			  if (pNode.isMOD())
				  str = "%";
			  if (pNode.isMULTIPLY())
				  str = "*";
			  if (pNode.isNE())
				  str = "!=";
			  if (pNode.isOR())
				  str = "||";
			  if (pNode.isPLUS())
				  str = "+";
			  if (pNode.isREM())
				  str = "%";
			  result = str.concat(" ");
		  }

		  public void visitUnaryOperator(final IJavaUnaryOperator pNode) {
			  String str = null;
			  if (pNode.isMINUS()) 
				  str = "-";
			  if (pNode.isPLUS()) 
				  str = "+";
			  if (pNode.isNOT())
				  str = "!";
			  if (pNode.isFLOOR())
				  str = "(Integer) Math.floor";
			  if (pNode.isABSOLUTE())
				  str = "Math.abs";
			  result = str;
		  }
		  
		  public void visitHeadExpression(final IJavaHeadExpression pNode) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) pNode.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = result.concat(".firstElement()");
			  result = str;
		  }

		  public void visitTailExpression(final IJavaTailExpression pNode) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) pNode.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat("new Vector(").concat(result).concat(".subList(1, ").concat(result).concat(".size()))");
			  result = str;
		  }
		  
		  public void visitCardinalityExpression (final IJavaCardinalityExpression jc) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) jc.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".size()");
			result = str;
		  }
		  
		  public void visitBinaryObjectOperator(final IJavaBinaryObjectOperator pNode) {
			  String str = null;
			  if (pNode.isEQUALS()) 
				  str = ".equals";
			  result = str; 
		  }
		  
		  public void visitDoubleType (final IJavaDoubleType var_1_1) {

		    String str = new String("Double");
		    result = str;
		  }
	
		  public void visitUnresolvedType(final IJavaUnresolvedType jut) {
			  result = jut.getTypename().getName();
		  }
		  
		  public void visitVectorType(IJavaVectorType pNode) {
			  String str = "Vector<";
			  try {
				printNodeField((IJavaNode) pNode.getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*if (result.equals(new String("int"))) {
				result = "Integer";
			}
			if (result.equals(new String("boolean"))) {
				result = "Boolean";
			}
			if (result.equals(new String("double"))) {
				result = "Double";
			}
			if (result.equals(new String("char"))) {
				result = "Character";
			}
			if (result.equals(new String("float"))) {
				result = "Float";
			}
			if (result.equals(new String("short"))) {
				result = "Short";
			}
			if (result.equals(new String("byte"))) {
				result = "Byte";
			}
			if (result.equals(new String("long"))) {
				result = "Long";
			}*/
			str = str.concat(result).concat(">");
			result = str;
		  }
		  
		  public void visitSetType(IJavaSetType st) {
			  String str = "HashSet<";
			  try {
				printNodeField((IJavaNode) st.getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(">");
	        result = str;
		  }
		  
		  public void visitSetCompExpression(final IJavaSetCompExpression jsc) {
			  String str = "CGCollections.SetComprehension(";
				try {
					printNodeField((IJavaNode) ((JavaInSetExpression)jsc.getPredicate()).getOwner());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(",");
				try {
					printNodeField((IJavaNode) ((JavaSetBind)jsc.getBindlist().get(0)).getSetName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				result = str.concat(")");
		  }
		  
		  public void visitSetEnumExpression(final IJavaSetEnumExpression jse) {
			  String str = "CGCollections.SetEnumeration(";
			  for(int i = 0; i < jse.getArgs().size(); i++) {
				  try {
					printNodeField((IJavaNode) jse.getArgs().get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!(i == (jse.getArgs().size() - 1)))
				str = str.concat(result).concat(",");
				else 
					str = str.concat(result);
			  }
			  result = str.concat(")");
		  }
		  
		  public void visitSetDifferenceExpression(final IJavaSetDifferenceExpression jsd) {
			  String str = "CGCollections.difference(";
			  try {
				printNodeField((IJavaNode) jsd.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) jsd.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitSetIntersectExpression(final IJavaSetIntersectExpression jsi) {
			  String str = "CGCollections.inter(";
			  try {
				printNodeField((IJavaNode) jsi.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) jsi.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitSetRangeExpression(final IJavaSetRangeExpression jsr) {
			  String str = "CGCollections.SetRange(";
			  try {
				printNodeField((IJavaNode) jsr.getHead());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) jsr.getTail());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitSetUnionExpression(final IJavaSetUnionExpression jsu) {
			  String str = "CGCollections.union(";
			  try {
				printNodeField((IJavaNode) jsu.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) jsu.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitInSetExpression(final IJavaInSetExpression jis) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) jis.getOwner());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".contains(");
			try {
				printNodeField((IJavaNode) jis.getElm());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")");
			result = str;
		  }
		  
		  public void visitNotInSetExpression(final IJavaNotInSetExpression jnis) {
			  String str = "!";
			  try {
				printNodeField((IJavaNode) jnis.getOwner());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".contains(");
			try {
				printNodeField((IJavaNode) jnis.getElm());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")");
			result = str;
		  }
		  
		  public void visitPowerSetExpression(final IJavaPowerSetExpression jps) {
			  String str = "CGCollections.power(";
			try {
				printNodeField((IJavaNode) jps.getSetname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")");
			result = str;
		  }
		  
		  public void visitSubSetExpression(IJavaSubSetExpression jss) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) jss.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".containsAll(");
			try {
				printNodeField((IJavaNode) jss.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitProperSubsetExpression(IJavaProperSubsetExpression jpss) {
			  String str = "CGCollections.psubset(";
			  try {
				printNodeField((IJavaNode) jpss.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(",");
			try {
				printNodeField((IJavaNode) jpss.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitDistUnionExpression(IJavaDistUnionExpression jdu) {
			  String str = "CGCollections.dunion(";
			try {
				printNodeField((IJavaNode) jdu.getSetname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitDistIntersectionExpression(IJavaDistIntersectionExpression jdi) {
			  String str = "CGCollections.dinter(";
				try {
					printNodeField((IJavaNode) jdi.getSetname());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result);
				result = str.concat(")");
		  }
		  
		  //---------Map Expressions--------------------------------
		  
		  public void visitDistMergeExpression(IJavaDistMergeExpression jdu) {
			  String str = "CGCollections.merge(";
			try {
				printNodeField((IJavaNode) jdu.getMapname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitMapRangeExpression(IJavaMapRangeExpression jdu) {
			  String str = "CGCollections.range(";
			try {
				printNodeField((IJavaNode) jdu.getMapname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitMapDomainExpression(IJavaMapDomainExpression jdu) {
			  String str = "";
			try {
				printNodeField((IJavaNode) jdu.getMapname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".keySet()");
			result = str;
		  }
		  
		  public void visitMapInverseExpression(IJavaMapInverseExpression jdu) {
			  String str = "";
			try {
				printNodeField((IJavaNode) jdu.getMapname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(".inverse()");
			result = str;
		  }
		  
		  public void visitMapletExpression (IJavaMapletExpression jme) {
			  String str = "CGCollections.MapLet(";
			  try {
				printNodeField((IJavaNode) jme.getLeft());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(", ");
			try {
				printNodeField((IJavaNode) jme.getRight());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result);
			result = str.concat(")");
		  }
		  
		  public void visitMapEnumExpression (IJavaMapEnumExpression jmee) {
			  String str = "CGCollections.MapEnumeration(";
			  for(int i = 0; i < jmee.getArgs().size(); i++) {
				  try {
					printNodeField((IJavaNode) jmee.getArgs().get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!(i == (jmee.getArgs().size() - 1)))
				str = str.concat(result).concat(",");
				else 
					str = str.concat(result);
			  }
			  result = str.concat(")");
		  }
		  
		  public void visitNewExpression(IJavaNewExpression jne) {
			String str = "new ";
			try {
				printNodeField((IJavaNode) jne.getClassName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat("(");
			if (!jne.getExpressions().isEmpty()) {
				for (int i = 0; i < jne.getExpressions().size(); i++) {
					try {
						printNodeField((IJavaNode) jne.getExpressions().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result);
					if (i < (jne.getExpressions().size()-1))
						str = str.concat(", ");
				}
			}
			result = str.concat(")");
		  }
		  
		  public void visitThisExpression(IJavaThisExpression jne) {
				
				result = "this";
			  }
		  
		  public void visitIsExpression (final IJavaIsExpression pcmp) {

			    String str = new String("");
			    try {
					printNodeField((IJavaNode) pcmp.getInstanceName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    str = str.concat(result).concat(" instanceof ");
			    
			    try {
					printNodeField((IJavaNode) pcmp.getClassName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    result = str.concat(result);
			  }
		  
		  public void visitSameClassMembership (final IJavaSameClassMembership pcmp) {

			    String str = new String("");
			    try {
					printNodeField((IJavaNode) pcmp.getLeft());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    str = str.concat(result).concat(".getClass()").concat(".equals(");
			    
			    try {
					printNodeField((IJavaNode) pcmp.getRight());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    result = str.concat(result).concat(".getClass())");
			  }
		  
		  public void visitIsBasicTypeExpression (final IJavaIsBasicTypeExpression pcmp) {

			    String str = new String("Utils.Is(");
			    try {
					printNodeField((IJavaNode) pcmp.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    str = str.concat(result).concat(", ");
			    
			    try {
					printNodeField((IJavaNode) pcmp.getType());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    result = str.concat("\"").concat(result).concat("\")");
			  }
		  
		  public void visitNumericLiteral (final IJavaNumericLiteral pcmp) {

		    String str = new String("");
		    Long tmpArg_v_3 = null;
		    tmpArg_v_3 = pcmp.getVal();
		    try {
				printIntField(tmpArg_v_3);
			} catch (CGException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    str = str.concat(result);
		    result = str;
		  }
	
		  public void visitRealLiteral (final IJavaRealLiteral pcmp) {

		    String str = new String("");
		    Double tmpArg_v_3 = null;
		    tmpArg_v_3 = pcmp.getVal();
		    try {
				printDoubleField(tmpArg_v_3);
			} catch (CGException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    str = UTIL.ConvertToString(UTIL.clone(result));
		    result = UTIL.ConvertToString(UTIL.clone(str));
		  }
	
		  public void visitQuoteLiteral (final IJavaQuoteLiteral pcmp) {

		    String str = new String("<");
		    String tmpArg_v_3 = null;
		    tmpArg_v_3 = pcmp.getVal();
		    try {
				printStringField(tmpArg_v_3);
			} catch (CGException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    String rhs_4 = null;
		    String var1_5 = null;
		    String var2_7 = null;
		    int from_11 = (int) Math.max(new Long(2).doubleValue() - 1, 0);
		    int to_12 = (int) Math.min(new Long(new Long(result.length()).intValue() - new Long(1).intValue()).doubleValue(), result.length());
		    if (from_11 > to_12) 
		      var2_7 = new String();
		    else 
		      var2_7 = new String(result.substring(from_11, to_12));
		    var1_5 = str.concat(var2_7);
		    rhs_4 = var1_5.concat(new String(">"));
		    str = UTIL.ConvertToString(UTIL.clone(rhs_4));
		    result = UTIL.ConvertToString(UTIL.clone(str));
		  }
		
		  public void visitBooleanLiteral (final IJavaBooleanLiteral pcmp) {
			String str = "";
		    try {
				printBooleanField(pcmp.getVal());
			} catch (CGException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    str = str.concat(result);
		    result = str;
		  }

		  public void visitNilLiteral (final IJavaNilLiteral var_1_1) {
		    result = new String(" null ");
		  }
		  
		  
		  public void visitReturnStatement(final IJavaReturnStatement jrs) {
			  String str = "";
			  if (jrs.hasExpression()) {
				  IJavaExpression je = jrs.getExpression();
				  try {
					printNodeField((IJavaNode) je);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
					str = str.concat("return ").concat(result).concat(";");
					result = str;      
			  }
		  }
		  
		  public void visitAssignStatement(final IJavaAssignStatement jas) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jas.getStateDesignator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  str = str.concat(result).concat(" = ");
			  try {
				printNodeField((IJavaNode) jas.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  str = str.concat(result);
			  result = str.concat(";");      
		  }
		  
		  public void visitStateDesignatorName(final IJavaStateDesignatorName jsdn) {
			  result = jsdn.getName().getIdentifier().getName();      
		  }
		  
		  public void visitFieldReference(final IJavaFieldReference jas) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jas.getStateDesignator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  str = str.concat(result).concat(".").concat(jas.getIdentifier().getName());  
			  result = str;
		  }
		  
		  public void visitMapOrSequenceReference(final IJavaMapOrSequenceReference jas) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jas.getStateDesignator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  str = str.concat(result).concat("(");
			  try {
					printNodeField((IJavaNode) jas.getExpression());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//------needed to be improved to be able to determine the Sequence reference and subtract one(because in Java Vector starts from 0)---
				str = str.concat(result).concat(")");   
				result = str;
		  }
		  
		  public void visitEmptyStatement(IJavaEmptyStatement pNode) {
			  result = "";
		  }
		  
		  public void visitNotYetSpecifiedStatement(IJavaNotYetSpecifiedStatement pNode) {
			  result = "throw new InternalError(\"This method is not yet specified!\");";
		  }
		  
		  public void visitBlockStatement(final IJavaBlockStatement jbs) {
			  String str = "{";
				  for (int i = 0; i < jbs.getStatements().size(); i++) {
					  try {
						printNodeField((IJavaNode) jbs.getStatements().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result).concat(nl);
				  }
				  result = str.concat("}").concat(nl);
		  }
		  
		  public void visitAtomicStatement(final IJavaAtomicStatement jbs) {
			  String str = "{";
				  for (int i = 0; i < jbs.getStatements().size(); i++) {
					  try {
						printNodeField((IJavaNode) jbs.getStatements().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result).concat(nl);
				  }
				  result = str.concat("}").concat(nl);
		  }
		  
		  public void visitErrorStatement (final IJavaErrorStatement pcmp) {
			  result = "throw new InternalError (\"The result of the statement is undefined!\");";
		  }
		  public void visitContinueStatement (final IJavaContinueStatement pcmp) {
			  result = "continue;";
		  }
		  
		  public void visitCallStatement(final IJavaCallStatement jbs) {
			  String str = jbs.getName().getName();
			  str = str.concat("(");
				  for (int i = 0; i < jbs.getArgs().size(); i++) {
					  try {
						printNodeField((IJavaNode) jbs.getArgs().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result);
					if (i < jbs.getArgs().size()-1)
						str = str.concat(",");
				  }
				  result = str.concat(");");
		  }
		  
		  public void visitCallObjectStatement(final IJavaCallObjectStatement jbs) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jbs.getObjectDesignator());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  str = str.concat(result).concat(".").concat(jbs.getName().getIdentifier().getName()).concat("(");
				  for (int i = 0; i < jbs.getExpressionList().size(); i++) {
					  try {
						printNodeField((IJavaNode) jbs.getExpressionList().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result);
					if (i < jbs.getExpressionList().size()-1)
						str = str.concat(",");
				  }
				  result = str.concat(");");
		  }
		  
		  public void visitNameDesignator(final IJavaNameDesignator jsdn) {
			  result = jsdn.getName().getName();      
		  }
		  
		  public void visitThisDesignator(final IJavaThisDesignator jsdn) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) jsdn.getThis());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  str = result;
			  result = str;
		  }
		  
		  public void visitNewDesignator(final IJavaNewDesignator jsdn) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) jsdn.getNew());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  str = result;
			  result = str;
		  }
		  
		  public void visitObjectFieldReference(final IJavaObjectFieldReference jas) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jas.getObjectDesignator());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(result).concat(".").concat(jas.getName().getIdentifier().getName());
				
			  result = str ;     
		  }
		  
		  public void visitObjectApply(final IJavaObjectApply jbs) {
			  String str = "";
				  try {
					printNodeField((IJavaNode) jbs.getObjectDesignator());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  str = str.concat(result).concat("(");
				  for (int i = 0; i < jbs.getExpressionList().size(); i++) {
					  try {
						printNodeField((IJavaNode) jbs.getExpressionList().get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result);
					if (i < jbs.getExpressionList().size()-1)
						str = str.concat(",");
				  }
				  result = str.concat(")");
		  }
		  
		  public void visitWhileLoop(final IJavaWhileLoop wl) {
			  String str = "while(";
				  try {
					printNodeField((IJavaNode) wl.getCondition());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  str = str.concat(result).concat(") {").concat(nl).concat(tab);
					  try {
						printNodeField((IJavaNode) wl.getStatement());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(); }
					str = str.concat(result).concat("}");
				
				  result = str.concat(nl);
		  }
		  
		  public void visitSetForLoop(final IJavaSetForLoop wl) {
			  String str = "for(Iterator<Integer> i = ";
				  try {
					printNodeField((IJavaNode) wl.getSet());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				str = str.concat(result).concat(".iterator(); i.hasNext();)").concat(" {").concat(nl).concat(tab);
				str = str.concat(wl.getVarName()).concat(" = i.next();").concat(nl).concat(tab);
					  try {
						printNodeField((IJavaNode) wl.getStatement());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(); }
					str = str.concat(result).concat("}");
				  result = str.concat(nl);
		  }
		  
		  public void visitSeqForLoop(final IJavaSeqForLoop wl) {
			  String str = "";
			  if (!wl.getReverse()) {
				   str = "for(int ".concat(wl.getVarName()).concat(" = 0; ").concat(wl.getVarName()).concat(" < ");
				  	try {
				  		printNodeField((IJavaNode) wl.getSeq());
				  	} catch (Exception e1) {
				  		// TODO Auto-generated catch block
				  		e1.printStackTrace();
				  	}
				  	str = str.concat(result).concat(".size(); ").concat(wl.getVarName()).concat("++)");
			  }
			  else {
				   str = "for(int ".concat(wl.getVarName()).concat(" = ");
				  	try {
				  		printNodeField((IJavaNode) wl.getSeq());
				  	} catch (Exception e1) {
				  		// TODO Auto-generated catch block
				  		e1.printStackTrace();
				  	}
				  	str = str.concat(result).concat(".size(); ").concat(wl.getVarName()).concat(" > 0; ").concat(wl.getVarName()).concat("--)");
			  }
			  
			  str = str.concat(" {").concat(nl).concat(tab);
				  		try {
						printNodeField((IJavaNode) wl.getStatement());
				  		} catch (Exception e) {
				  			// TODO Auto-generated catch block
				  			e.printStackTrace(); }
					str = str.concat(result).concat("}");
				  result = str.concat(nl);
		  }
		  
		  public void visitIndexForLoop(final IJavaIndexForLoop wl) {
			  String str = "";
				   str = "for(int ".concat(wl.getVarName()).concat(" = ");
				  	try {
				  		printNodeField((IJavaNode) wl.getFrom());
				  	} catch (Exception e1) {
				  		// TODO Auto-generated catch block
				  		e1.printStackTrace();
				  	}
				  	str = str.concat(result).concat(";").concat(wl.getVarName()).concat(" <= ");
				  	try {
						printNodeField((IJavaNode) wl.getTo());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				  	
				  	str = str.concat(result).concat("; ").concat(wl.getVarName()).concat(" + ");
				  	try {
						printNodeField((IJavaNode) wl.getStep());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					str = str.concat(result).concat(")").concat(" {").concat(nl).concat(tab);
			
				  		try {
						printNodeField((IJavaNode) wl.getStatement());
				  		} catch (Exception e) {
				  			// TODO Auto-generated catch block
				  			e.printStackTrace(); }
					str = str.concat(result).concat("}");
				  result = str.concat(nl);
		  }
		  
		  public void visitAssignmentDefinition (final IJavaAssignmentDefinition pcmp) {
			  String str = "";
			  try {
				printNodeField((IJavaNode) pcmp.getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(" ");
			str = str.concat(pcmp.getIdentifier().getName()).concat(" = ");
			try {
				printNodeField((IJavaNode) pcmp.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(";");
			result = str;
		  }
		  
		  public void visitMethodBody (final IJavaMethodBody pcmp) {

		    String str = new String("{").concat(nl).concat(tab);
		    //-------printing Precondition------------
		    if (!(pcmp.getPrecondition() instanceof IJavaEmptyExpression)) {
		    	try {
					printNodeField((IJavaNode) pcmp.getPrecondition());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat("if (").concat(result).concat(")").concat(nl).concat(tab).concat(tab).concat("throw new InternalError(\"Precondition is not satisfied!\");");
		    }
		    //--------printing the statement----------
		      Boolean flag = null;
		      flag = !(pcmp.getStatement().isEmpty());
		      if (flag.booleanValue()) {

		       int max_st = pcmp.getStatement().size();
		       for(int i = 0; i < max_st; i++) {
		    	   
		       
		    	 IJavaStatement js = (IJavaStatement) pcmp.getStatement().get(i);
		        try {
					printNodeField((IJavaNode) js);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
		       }

		       str = str.concat(result).concat(nl).concat(tab);

		      }
		      else {
		        result = "";
		        str = str.concat(result).concat(nl).concat(tab);}
		      if (pcmp.getImplicit()) {
			    	str = str.concat("throw new InternalError(\"This method is only defined implicitly!\");").concat(nl);
			      }
		      //-----------Printing postcondition----------------------------------------------
		      if (!(pcmp.getPostcondition() == "")) {
		    	  str = str.concat("//-------postcondition '").concat(pcmp.getPostcondition()).concat("' is ignored-------------------------------").concat(nl);
		    	  try {
					StatusLog.warn("Postcondition is ignored in method '"+((JavaMethodDefinition)pcmp.getParent()).getIdentifier().getName()+"'");
				} catch (CGException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		    str = str.concat(tab).concat(new String("}"));
		    result = str;
		  }
		  
		  
		  public void visitMethodDefinition (final IJavaMethodDefinition pcmp) {

			    String str = new String("");
			    //String str_static = new String("");
			    //String str_init = new String("");
			    //-------printing static and non-static initializers---------
			   /* if (!init_flag) {
			    	if (!Initializer_list.isEmpty()) {
			    		for(int i = 0; i <Initializer_list.size(); i++) {
			    			if (Initializer_list.get(i) instanceof IJavaStatic_Init) {
			    				IJavaExpression JE = ((JavaStatic_Init) Initializer_list.get(i)).getBody();
			    				try {
									printNodeField((IJavaNode) JE);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								str_static = str_static.concat(result);
			    			}
			    		}
			    		if (!(str_static == "")) {
			    			str = "static {".concat(str_static).concat(" }").concat(nl);
			    		}
			    		for (int i = 0; i <Initializer_list.size(); i++) {
			    			if (Initializer_list.get(i) instanceof IJavaInit) {
			    				IJavaExpression je = ((JavaInit) Initializer_list.get(i)).getBody();
			    				try {
									printNodeField((IJavaNode)je);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								str_init = str_init.concat(result);
			    			}
			    		}
			    		if (!(str_init == "")) {
			    			str = ("private void Initialize() {").concat(nl).concat("tab").concat(str_init).concat(" }").concat(nl);
			    		}
			    		init_flag = true;
			    	}
			    }*/
			    IJavaAccessDefinition jad = null;
			    
			    //-----------print access field--------------------
			    jad = pcmp.getAccess();
		        try {
					printNodeField((IJavaNode) jad);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    str = str.concat(result);
			    
			    //------------print modifiers-------------------------    
			    IJavaModifier jm = pcmp.getModifiers();
			    try {
					printNodeField((IJavaNode) jm);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    str = str.concat(result);
			    
			    //----------print return-type field-------------------
			    
			    if (!pcmp.getIsConstructor()) {
			    	String rt1 = "";
				    IJavaType rt = pcmp.getReturntype();
				    try {
						printNodeField((IJavaNode) rt);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (rt instanceof JavaVoidType)
						rt1 = str.concat("void").concat(" ");
					else
						rt1 = str.concat(result).concat(" ");
					str = rt1;
			    }
				
				//******print print method identifier--------------------
				IJavaIdentifier ji = pcmp.getIdentifier();
				try {
					printNodeField((IJavaNode) ji);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String ji1 = str.concat(result);
				str = ji1;
				
				//*********Print ParameterList---------------
				List<IJavaBind> jb = pcmp.getParameterList();
				for(int i=0; i<jb.size(); i++) {
					try {
						printNodeField((IJavaNode) jb.get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				String jb1 = str.concat(result).concat(" ");
				str = jb1;
				
				//*******print Method Body-----------------
			    IJavaMethodBody jmb = null;
			    jmb = pcmp.getBody();
			    if (!jmb.getSubclassResponsibility()) {
			    	try {
			    		printNodeField((IJavaNode) jmb);
			    	} catch (Exception e) {
			    		// TODO Auto-generated catch block
			    		e.printStackTrace();
			    	}
			    }
			    else 
			    	result = ";";
			    result = str.concat(result).concat("\n");
			  }

		  public void visitModifier(IJavaModifier pNode) {
			  String str = "";
			  if(pNode.getAbstract())
				  str = str.concat("abstract").concat(" ");
			  if(pNode.getFinal())
				  str = str.concat("final").concat(" ");
			  result = str;
		  }
		  
		  public void visitIfExpression(IJavaIfExpression pNode) {
			  String str = "((";
			  try {
				printNodeField((IJavaNode) pNode.getIfExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(") ? ");
			
			IJavaExpression ijs = pNode.getThenExpression();
			try {
				printNodeField((IJavaNode) ijs);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			str = str.concat(result).concat(" : ");
			
			if (!pNode.getElseifExpressionList().isEmpty()) {
				int size = pNode.getElseifExpressionList().size();
				IJavaElseIfExpression jeis;
				for (int i = 0; i < size; i++) {
					jeis = pNode.getElseifExpressionList().get(i);
					try {
						printNodeField((IJavaNode) jeis);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result).concat(" : ");
				}
			}
			
			if(!(pNode.getElseExpression() instanceof IJavaEmptyExpression))  
				{ IJavaExpression js = pNode.getElseExpression();
					try {
					printNodeField((IJavaNode) js);
					} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
					str = str.concat(result).concat(")");
					for (int i = 0; i < pNode.getElseifExpressionList().size(); i++)
						str = str.concat(")");
				}
			result = str;  
		  }
		  
		  public void visitElseIfExpression(IJavaElseIfExpression jeis) {
			  String str = "((";
			  try {
				printNodeField((IJavaNode) jeis.getElseifExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")").concat(" ? ");
			try {
				printNodeField((IJavaNode) jeis.getThenExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(" : ");
			result = str;
		  }
		 		 
		  public void visitIfStatement(IJavaIfStatement pNode) {
			  String str = "if".concat("(");
			  try {
				printNodeField((IJavaNode) pNode.getExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")").concat(nl).concat(tab).concat("  ").concat("{ ");
			
			IJavaStatement ijs = pNode.getThenStatement();
			try {
				printNodeField((IJavaNode) ijs);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			str = str.concat(result).concat(" }").concat(nl);
			
			if (!pNode.getElselist().isEmpty()) {
				int size = pNode.getElselist().size();
				IJavaElseIfStatement jeis;
				for (int i = 0; i < size; i++) {
					jeis = pNode.getElselist().get(i);
					try {
						printNodeField((IJavaNode) jeis);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = str.concat(result).concat(nl);
				}
			}
			
			if(!(pNode.getElseStatement() instanceof IJavaEmptyStatement))  
				{ IJavaStatement js = pNode.getElseStatement();
				try {
					printNodeField((IJavaNode) js);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				str = str.concat(tab).concat("else").concat(nl).concat(tab).concat("  ").concat("{ ").concat(result).concat(" }");
				}
			result = str;  
		  }
		  
		  public void visitElseIfStatement(IJavaElseIfStatement jeis) {
			  String str = tab.concat("else if(");
			  try {
				printNodeField((IJavaNode) jeis.getElseifExpression());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(")").concat(nl).concat(tab).concat("  { ");
			try {
				printNodeField((IJavaNode) jeis.getThenStatement());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str = str.concat(result).concat(" }");
			result = str;
		  }
		  
		  public void visitTypeBind (final IJavaTypeBind jtb) {
			  String str = new String("(");
			  
			  //*******print parameter type
			  IJavaType jt = null;
			  if (!jtb.getTypeList().isEmpty() && !jtb.getPatternPattern().isEmpty()) {
				  int list_size = jtb.getTypeList().size();
				  for (int i = 0; i < list_size; i++) {
					  jt = jtb.getTypeList().get(i);
					  try {
						  printNodeField((IJavaNode) jt);
					  } catch (Exception e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
					  }
					  str = str.concat(result).concat(" ");
					  result = str;
					  
					  //----------------print parameter pattern--------------
					  
					  List<IJavaPattern> jp = null;
					  jp = jtb.getPatternPattern();
						  try {
							  printNodeField((IJavaNode) jp.get(i));
						  } catch (Exception e) {
							  // TODO Auto-generated catch block
							  e.printStackTrace();
						  }
						  str = str.concat(result);
						  if((i+1) < list_size) {
							  str = str.concat(", ");
						  }
				  }
			  }
			  str = str.concat(")");
			  result = UTIL.ConvertToString(UTIL.clone(str));
			  
			  
		    //result = UTIL.ConvertToString(UTIL.clone(new String("NOT YET SUPPORTED")));
		  }
}