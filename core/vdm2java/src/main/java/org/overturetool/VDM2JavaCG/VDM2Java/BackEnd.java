package org.overturetool.VDM2JavaCG.VDM2Java;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.VDM2JavaCG.ast.java.itf.IJavaClassDefinition;
import org.overturetool.VDM2JavaCG.ast.java.itf.IJavaDocument;
import org.overturetool.VDM2JavaCG.ast.java.itf.IJavaSpecification;
import org.overturetool.VDM2JavaCG.ast.java.imp.JavaClassDefinitions;
import org.overturetool.VDM2JavaCG.ast.java.imp.JavaDocument;
import org.overturetool.VDM2JavaCG.ast.java.imp.JavaSpecification;
import org.overturetool.VDM2JavaCG.main.StatusLog;
import org.overturetool.VDM2JavaCG.api.Util;

@SuppressWarnings({"all","unchecked","unused"})

public class BackEnd {

	public BackEnd() {
		// TODO Auto-generated constructor stub
	}

	 public void Save (final String fileName, final IJavaDocument doc, final StatusLog log) throws CGException {

		    Vector<IJavaClassDefinition> classes = null;
		    IJavaSpecification jspec = (IJavaSpecification) doc.getSpecification();
		    JavaClassDefinitions jclds = (JavaClassDefinitions) jspec;
		    classes = (Vector<IJavaClassDefinition>) jclds.getClassList();
		    {

		      HashSet hset1 = new HashSet();
		      Enumeration enm_16 = classes.elements();
		      while ( enm_16.hasMoreElements())
		        hset1.add(enm_16.nextElement());
		      IJavaClassDefinition cl = null;
		      for (Iterator enm = hset1.iterator(); enm.hasNext(); ) {

		        cl = (IJavaClassDefinition) enm.next();
		        {

		          JavaAstVisitor visitor = new JavaAstVisitor();
		          visitor.useNewLineSeparator(new Boolean(true));
		          try {
					visitor.visitClassDefinition((IJavaClassDefinition) cl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		          String tmpArg = null;
		          String str1 = null;
		          str1 = fileName.concat(Normalize(cl.getIdentifier().getName()));
		          tmpArg = str1.concat(new String(".java"));
		          Util.CreateFile(tmpArg);
		          String tmpstr = null;
		          tmpstr = visitor.result;
		          Util.WriteFile(tmpstr);
		          Util.CloseFile();
		          log.endClass(str1);
		          System.out.println("A Java source file '"+tmpArg+"' has been created.");
		        }
		      }
		    }
		  }
		
	 
	 
		  public String Normalize (final String fileName) throws CGException {
			  String name ="";
			  boolean first = true;
			  for(Character c : fileName.toCharArray())
				  if(c.isLetterOrDigit(c) && !first || first && c.isLetter(c))
					  name +=c;
			  if(name.length()==0)
				  return "tmp";
			  else
				 return name;
		  }


}