package org.overturetool.mapper;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.overturetool.jml.ast.imp.*;
import org.overturetool.mapper.Vdm2Jml.Information;

public class BuildTypes {
	
	
	public HashMap<String,Information> map;
	public String path; 
	
	public BuildTypes(HashMap<String,Information> map, String path) {
		
		this.map = map;
		this.path = path;
	}
	
	
	public void buildClasses() throws Exception {
		
		Set s = this.map.keySet();
		Iterator it = s.iterator();
		
		while(it.hasNext()) {
			
			String classname = (String) it.next();
			System.out.println(classname);
			Information i = this.map.get(classname);
			
			String cl = buildClass(classname,i);
			
			File f = new File(this.path + "/" + classname + ".java");
			FileWriter fw = new FileWriter(f);
			fw.write(cl);
			fw.close();
			f.createNewFile();
		}
		
	}
	
	public String buildClass(String name, Information i) {
		
		Vector<JmlField> fl = new Vector<JmlField>();
		fl = i.field_ulist;
		JmlExpression expr = i.invariant;
		String inv = new String();
		JmlAccessDefinition access = i.access;
		String acc = access.toString() + " ";
		String ivars = new String();
		
		if(expr != null) {
			
			//remove prefix t. from elements (at specification level??)
			inv = "//@ invariant\n//@ " + expr.toString();
		}
		else
			inv = "";
		
		
		int j, size = fl.size();
		for(j = 0; j < size; j++) {
			
			JmlField field = fl.get(j);
			ivars += acc + field.toString() + ";\n";
		}
		
		
		String cl = new String();
		
		cl = acc + "class " + name + " {\n\n" + ivars + "\n" +  inv + "\n}";
		
		return cl;
	}

}
