package org.overturetool.VDM2JavaCG.VDM2Java;

import jp.co.csk.vdm.toolbox.VDM.*;

import org.overturetool.vdmj.types.*;
//import org.overturetool.VDM2JavaCG.api.Util;
import org.overturetool.VDM2JavaCG.ast.java.itf.*;
import org.overturetool.VDM2JavaCG.ast.java.imp.*;
import org.overturetool.VDM2JavaCG.VDM2Java.vdm2java;


public class Vdm2JavaType {

  static protected external_Vdm2JavaType child = new external_Vdm2JavaType();


  public Vdm2JavaType () throws CGException {}

  static public IJavaType GetQualifier (final Type t) throws CGException {

    IJavaType j_type = null;
    boolean flag = true;
    {

      flag = true;
      if (!(t instanceof InMapType)) 
        flag = false;
      if (flag) {

        InMapType t1 = (InMapType) t;
        Type t2 = null;
        t2 = (Type) t1;
        j_type = (IJavaType) ConvertType((Type) t2);
      }
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof MapType)) 
        flag = false;
      if (flag) {

        MapType t1 = (MapType) t;
        Type par_15 = null;
        par_15 = (Type) t1;
        j_type = (IJavaType) ConvertType((Type) par_15);
      }
    }
    if (!flag) 
      j_type = null;
    return (IJavaType) j_type;
  }


  static public IJavaType ConvertType (final Type t) throws CGException {

    Object obj = null;
    boolean flag = true;
    {

      flag = true;
      if (!(t instanceof BooleanType))
        flag = false;
      if (flag) 
        obj = new JavaBooleanType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof NaturalOneType))
        flag = false;
      if (flag) 
        obj = new JavaIntType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof NaturalType)) 
        flag = false;
      if (flag) 
        obj = new JavaIntType();
    }
    if (!flag) {

        flag = true;
        if (!(t instanceof RationalType)) 
          flag = false;
        if (flag) 
          obj = new JavaDoubleType();
      }
    if (!flag) {

      flag = true;
      if (!(t instanceof IntegerType))
        flag = false;
      if (flag) 
        obj = new JavaIntType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof RealType))
        flag = false;
      if (flag) 
        obj = new JavaDoubleType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof CharacterType)) 
        flag = false;
      if (flag) 
        obj = new JavaCharType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof TokenType)) 
        flag = false;
      if (flag) 
        obj = new JavaStringType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof SetType)) 
        flag = false;
      if (flag) {
      	SetType st = (SetType) t;
          obj = new JavaSetType((IJavaType) ConvertType(st.setof));
          if(!vdm2java.imports.contains(new String("import java.util.HashSet;"))) {
          vdm2java.imports.add("import java.util.HashSet;");
      	  }
      }
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof SeqType))
        flag = false;
      if (flag) {
    	SeqType st = (SeqType) t;
    	if (st.seqof instanceof CharacterType ) {
    		obj = new JavaStringType();
    	}
    	else 
    		obj = new JavaVectorType((IJavaType) ConvertType(st.seqof));
        if(!vdm2java.imports.contains(new String("import java.util.Vector;")))
        vdm2java.imports.add("import java.util.Vector;");
      }
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof Seq1Type))
        flag = false;
      if (flag) {
    	  SeqType st = (SeqType) t;
          obj = new JavaVectorType((IJavaType) ConvertType(st.seqof));
          if(!vdm2java.imports.contains(new String("import java.util.Vector;")))
              vdm2java.imports.add("import java.util.Vector;");
      }
    }
    
    
    
    if (!flag) {

      flag = true;
      if (!(t instanceof InMapType))
        flag = false;
      if (flag) {

        InMapType t1 = (InMapType) t;
        Type prm1 = (Type) t1.from;
        Type prm2 = (Type) t1.to;
        obj = new JavaBiMap(ConvertType((Type) prm1), ConvertType((Type) prm2));
        if(!vdm2java.imports.contains(new String("import com.google.common.collect.*;")))
            vdm2java.imports.add("import com.google.common.collect.*;");
      }
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof MapType))
        flag = false;
      if (flag) {

        MapType t1 = (MapType) t;
        Type prm1 = (Type) t1.from;
        Type prm2 = (Type) t1.to;
        obj = new JavaGenMap(ConvertType((Type) prm1), ConvertType((Type) prm2));
      }
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof VoidType))
        flag = false;
      if (flag) 
        obj = new JavaVoidType();
    }
    if (!flag) {

      flag = true;
      if (!(t instanceof OptionalType)) 
        flag = false;
      if (flag) {

       //OptionalType t1 = (OptionalType) t;
        //Type prm = null;
        //prm = (Type) t1.type;
        //obj = ConvertType((Type) prm);
    	  throw new InternalError("Sorry, optional Type is not yet Supported, '"+t.getLocation()+"'");
      }
    }
    
    if (!flag) {

        flag = true;
        if (!(t instanceof RecordType))
          flag = false;
        if (flag) 
          obj = new JavaClassType(((RecordType) t).name.name);
      }
    
    if (!flag) {

      flag = true;
      if (!(t instanceof NamedType))
        flag = false;
      if (flag) {
    	 NamedType a = (NamedType) t;
    	 if (a.type.isUnion()) {
    		 obj = new JavaClassType(a.typename.name);
    	 }
    	 else
    		 obj = ConvertType(a.type);
      }
    }
    
    if (!flag) {

        flag = true;
        if (!(t instanceof UnionType))
          flag = false;
        if (flag) {
        	throw new InternalError("Sorry, direct use of 'union type' is not yet supported, '"+t.getLocation()+"'"); 
        }
      }
    
    if (!flag) {
    	flag = true;
    	if (!(t instanceof ClassType)) 
    		flag = false;
    	if (flag) {
    		ClassType ct = (ClassType) t;
    		obj = new JavaClassType(ct.name.name);
    	}
    }
    
   /* if (!flag) {
    	flag = true;
        if (t instanceof BracketType) {
      	  BracketType u = (BracketType) t;
          obj = ConvertType(u.type); 
        }
      }*/
    
    if (!flag) {

        flag = true;
        if (!(t instanceof QuoteType))
          flag = false;
        if (flag) {
      	  QuoteType q = (QuoteType) t;
      	  
          obj = new JavaQuoteType(q.value);
        }
      }
    
    if (!flag) {

        flag = true;
        if (!(t instanceof TokenType))
          flag = false;
        if (flag) {  
          obj = new JavaTokenType();
        }
      }
   // if (u.type.isUnion()) {
  	//	throw new InternalError("Sorry, direct use of 'union type' is not supported.");
  	  //}
    
    if (!flag) 
    	throw new InternalError("Type '"+t+"' is not subtype of 'VDMPP Type' or not yet supported, '"+t.getLocation()+"'");
    return (IJavaType) obj;
  }

  static public IJavaType ConvertPropertyType (final Type t, final String owner) throws CGException {

    Object obj = null;
    {

      IJavaType ty = (IJavaType) (IJavaType) ConvertType((Type) t);
      if (new Boolean(UTIL.equals(ty, null)).booleanValue()) 
        obj = new JavaName(new JavaIdentifier(owner));
      else 
        obj = ty;
    }
    return (IJavaType) obj;
  }
}