package nl.marcelverhoef.treegen.codegenerator;

//project specific imports
import java.util.*;
import java.io.*;

import nl.marcelverhoef.treegen.typecheck.*;

public class CodeGenerator {

	// keep track of the error count during code generation
	public int errors = 0;
	
	// keep track of the top-level directory
	private String basedir;
	
	// place holder for the top-level implementation directory 
	private String impdir;
	
	// place holder for the top-level interface directory
	private String itfdir;
	
	// keep track of the package name
	private String packname;
	
	// keep track of the interface prefix
	private String iprefix;
	
	// keep track of the implementation prefix
	private String prefix;

	// keep track of the current class definition
	private ClassDefinition current = null;
	
	//
	// AUXILIARY FUNCTIONS
	//
	
	public boolean createDirectory(String dirname)
	{
		// create the file handler
		File dir = new File(dirname);
		
		// check for existence
		if (dir.exists()) {
			// it must be a directory
			return dir.isDirectory();
		} else {
			// we need to create a new directory
			return dir.mkdir();
		}
	}
	
	public void writeToFile (
			String fname,
			String text
	) {
		// create the file handle
		File fp = new File(fname);
		
		try {
			// create the file if it doesn't exit
			if (!fp.exists()) {
				if (!fp.createNewFile()) {
					// diagnostics
					System.out.println("File '" + fname + "' cannot be opened!");
					
					// flag error and return
					errors++;
					return;
				}
			}
		
			// create a file writer
			FileWriter fw = new FileWriter(fp);
			
			// write the text to the file
			fw.write(text);
			
			// flush the file writer
			fw.flush();
			
			// close the file
			fw.close();
		} catch (IOException e) {
			// diagnostics
			System.out.println("Error during '" + fname + "' file access!");
			
			// flag error
			errors++;
		}
	}
	
	public boolean isTrueTypeName(Type tp)
	{
		// check for type name
		if (tp.isTypeName()) {
			// retrieve the type name
			TypeName theTypeName = (TypeName) tp;
			
			// retrieve the type from the class definition look-up table
			Type theType = current.getTypeByName(theTypeName.type_name);
			
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theType != null) {
				return (!theType.isStringType());
			}
		}
	
		// default
		return false;
	}
	
	public String getJavaType(String pfx, Type tp)
	{
		// convert basis type representations to its Java equivalent
		if (tp.isBooleanType()) return "Boolean"; 
		if (tp.isNatType()) return "Integer";
		if (tp.isRealType()) return "Double";
		if (tp.isCharType()) return "Character";
		if (tp.isStringType()) return "String";
		if (tp.isJavaType()) {
			JavaType theJavaType = (JavaType) tp;
			return theJavaType.java_type;
		}
		
		// deal with sequences
		if (tp.isSeqType()) {
			// convert to proper type
			SeqType theSeqType = (SeqType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSeqType.seq_type);
	
			// return the result string
			if (isTrueTypeName(theSeqType.seq_type)) {
				return "java.util.Vector<? extends " + etp + ">";
			} else {				
				return "java.util.Vector<" + etp + ">";
			}			
		}
		
		// deal with sets
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSetType.set_type);
			
			// return the result string
			if (isTrueTypeName(theSetType.set_type)) {
				return "java.util.HashSet<? extends " + etp + ">";
			} else {
				return "java.util.HashSet<" + etp + ">";
			}
		}
		
		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getJavaType(pfx, theMapType.domain);
			String ertp = getJavaType(pfx, theMapType.range);
			
			if (isTrueTypeName(theMapType.domain)) edtp = "? extends " + edtp;
			if (isTrueTypeName(theMapType.range)) ertp = "? extends " + ertp;
			
			// return the result string
			return "java.util.HashMap<"+edtp+","+ertp+">";
		}
		
		// deal with type names
		if (tp.isTypeName()) {
			// convert to proper type
			TypeName theTypeName = (TypeName) tp;
		
			// retrieve the type from the class definition look-up table
			Type theType = current.getTypeByName(theTypeName.type_name);
						
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theType != null) {
				if (theType.isStringType()) return "String";
			}
			
			// default return the prefix and class name as the Java type
			return pfx + theTypeName.type_name;
		}
		
		// default (implies an error!)
		return "undefined";
	}
	
	public String getAbstractJavaType(Type tp)
	{
		// check for abstract data types (sequences)
		if (tp.isSeqType()) {
			// overrule seq of char
			if (tp.isStringType()) return "String";
			
			// convert to proper type
			SeqType theSeqType = (SeqType) tp;
			
			// retrieve the embedded type string
			String etp = getAbstractJavaType(theSeqType.seq_type);
			
			// return the result string
			if (isTrueTypeName(theSeqType.seq_type)) {
				return "java.util.List<? extends " + etp + ">";
			} else {				
				return "java.util.List<" + etp + ">";
			}			
		}
		
		// check for abstract data types (sets)
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getAbstractJavaType(theSetType.set_type);
			
			// return the result string
			if (isTrueTypeName(theSetType.set_type)) {
				return "java.util.Set<? extends " + etp + ">";
			} else {
				return "java.util.Set<" + etp + ">";
			}
		}
		
		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getAbstractJavaType(theMapType.domain);
			String ertp = getAbstractJavaType(theMapType.range);
			
			if (isTrueTypeName(theMapType.domain)) edtp = "? extends " + edtp;
			if (isTrueTypeName(theMapType.range)) ertp = "? extends " + ertp;
			
			// return the result string
			return "java.util.Map<"+edtp+","+ertp+">";
		}
		
		// deal with type names
		if (tp.isTypeName()) {
			// convert to proper type
			TypeName theTypeName = (TypeName) tp;
		
			// retrieve the type from the class definition look-up table
			Type theType = current.getTypeByName(theTypeName.type_name);
			
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theType != null) {
				if (theType.isStringType()) return "String";
			}
			
			// default return the interface prefix and class name as the Java type
			return iprefix + theTypeName.type_name;
		}

		// default response is to return the concrete Java type
		return getJavaType(prefix, tp);
	}
	
	public String getJavaTypeInitializer(String pfx, Type tp)
	{
		// first check for the optional type
		if (tp.isOptionalType()) return "null";
		
		// convert basic type representation to its proper Java initializer
		if (tp.isBooleanType()) return "new Boolean(false)"; 
		if (tp.isNatType()) return "new Integer(0)";
		if (tp.isRealType()) return "new Double(0.0)";
		if (tp.isCharType()) return "new Character(\'\\0\')";
		if (tp.isStringType()) return "new String()";

		// deal with sequences
		if (tp.isSeqType()) {
			// convert to proper type
			SeqType theSeqType = (SeqType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSeqType.seq_type);
			
			// return the result string
			return "new java.util.Vector<" + etp + ">()";
		}
		
		// deal with sets
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSetType.set_type);
			
			// return the result string
			return "new java.util.HashSet<" + etp + ">()";
		}

		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getJavaType(pfx, theMapType.domain);
			String ertp = getJavaType(pfx, theMapType.range);
			
			// return the result string
			return "new java.util.HashMap<"+edtp+","+ertp+">()";
		}
		
		// convert the type name
		if (tp.isTypeName()) {
			// convert to proper type
			TypeName theTypeName = (TypeName) tp;
		
			// retrieve the type from the class definition look-up table
			Type theType = current.getTypeByName(theTypeName.type_name);
			
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theType != null) {
				if (theType.isStringType()) return "new String()";
			}
			
			// default return null (type must be instantiated by user)
			return "null";
		}
		
		// default (implies an error!)
		return "undefined";
	}
	
	public String getDeepInstanceCopy(Type tp, String bcls, String scp)
	{
		// create a copy of the Java basic types
		if (tp.isBooleanType()) return "new Boolean("+scp+")"; 
		if (tp.isNatType()) return "new Integer("+scp+")";
		if (tp.isRealType()) return "new Double("+scp+")";
		if (tp.isCharType()) return "new Character("+scp+")";
		if (tp.isStringType()) return "new String("+scp+")";

		// convert the sequence
		if (tp.isSeqType()) {
			// convert to proper type
			SeqType theSeqType = (SeqType) tp;
			
			// return convert operator
			return getDeepInstanceCopy(theSeqType.seq_type, bcls, scp);
		}
		
		// convert the set
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// return convert operator
			return getDeepInstanceCopy(theSetType.set_type, bcls, scp);
		}
		
		// convert the type name
		if (tp.isTypeName()) {
			// convert to proper type
			TypeName theTypeName = (TypeName) tp;
		
			// retrieve the type from the class definition look-up table
			Type theType = current.getTypeByName(theTypeName.type_name);
			
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theType != null) {
				if (theType.isStringType()) return "new String("+scp+")";
			}
			
			// return convert operator
			return "("+bcls+theTypeName.type_name+") "+scp+".convert"+bcls+"()";
		}
		
		// convert the java type (assumes the copy constructor is available)
		if (tp.isJavaType()) {
			// convert to proper type
			JavaType theJavaType = (JavaType) tp;
			
			// default return the copy constructor
			return "new "+theJavaType.java_type+"("+scp+")";
		}
		
		// default (implies an error!)
		return "undefined";
	}
	
	public String beautify (String str) {
		// place holder for the result
		String res = new String();
		
		// keep track of upper case state (always start with upper case character
		boolean toupper = true;

		// ignore null string objects
		if (str == null) return res;
		
		for (int idx=0; idx<str.length(); idx++) {
			// retrieve a single character from the string
			String lstr = str.substring(idx,idx+1);
			
			// check for underscore character
			if (lstr.compareTo("_") == 0) {
				// set the toupper marker (and skip the underscore)
				toupper = true;
			} else {
				// copy the character
				if (toupper) {
					res += lstr.toUpperCase();
				} else {
					res += lstr;
				}
				// reset the toupper marker
				toupper = false;
			}
		}
		
		// return the result
		return res;
	}
	
	//
	// TOP-LEVEL ENTRY FUNCTION FOR THE CODE GENERATOR
	//
	
	public void generateCode(HashMap<String,ClassDefinition> cds)
	{
		// diagnostics
		System.out.println("Starting code generation phase");
		
		// iterate over the class definition mapping
		for (String clnm: cds.keySet()) {
			// generate code for each class individually
			generateCodeClass(clnm, cds.get(clnm));
		}
		
		// diagnostics
		System.out.println("Finished code generation");
	}
	
	public void generateCodeClass(String clnm, ClassDefinition cd)
	{
		// diagnostics
		System.out.println("Code generation for class "+clnm);
		
		// initialise the prefixes
		iprefix = "I" + clnm;
		prefix = clnm;
		
		// set the current class definition indicator
		current = cd;
		
		// first create the directory structure based on the values passed
		generateCodeValues(clnm, cd);
		
		// next create the top-level node class and interface
		if (errors == 0) generateCodeVariables(clnm, cd);
		
		// then create classes and interfaces for each defined type
		if (errors == 0) generateCodeTypes(clnm, cd);
	}
	
	public void generateCodeValues(String clnm, ClassDefinition cd)
	{
		// retrieve the top-level directory
		basedir = cd.getDirectory();
		
		// retrieve the package definition
		packname = cd.getPackage();
		
		// test for proper existence of top-level directory
		File tldir = new File(basedir);
		
		// check for existence
		if (!tldir.exists()) {
			// diagnostics
			System.out.println("root directory '"+basedir+"' does not exist!");
			
			// flag error and exit
			errors++;
			return;
		}
		
		// check whether or not it is a directory
		if (!tldir.isDirectory()) {
			// diagnostics
			System.out.println("'"+basedir+"' is not a directory!");
			
			// flag error and exit
			errors++;
			return;
		}
		
		// diagnostics
		System.out.println("Generating code in directory '"+basedir+"' using package name '"+packname+"'");

		// construct the source directory
		basedir += File.separator + "src";
	
		// create the directory if it does not exist yet
		if (!createDirectory(basedir)) {
			// diagnostics
			System.out.println("'"+basedir+"' cannot be created");
			
			// flag error and exit
			errors++;
			return;
		}
		
		// split the package string into parts
		String pstr[] = packname.split("[\\.]");
		
		// iterate over the package string
		for (String pname: pstr) {
			// construct the directory name
			basedir += File.separator + pname;
			
			if (!createDirectory(basedir)) {
				// diagnostics
				System.out.println("'"+basedir+"' cannot be created");
				
				// flag error and exit
				errors++;
				return;
			}
		}
		
		// keep track of top-level source directories
		impdir = basedir + File.separator + "imp";
		itfdir = basedir + File.separator + "itf";
		
		// create the interface directory
		if (!createDirectory(itfdir)) {
			// diagnostics
			System.out.println("'"+basedir+"' cannot be created");
			
			// flag error and exit
			errors++;
			return;
		} else {
			impdir += File.separator;
		}
		
		// create the implementation directory
		if (!createDirectory(impdir)) {
			// diagnostics
			System.out.println("'"+basedir+"' cannot be created");
			
			// flag error and exit
			errors++;
			return;
		} else {
			itfdir += File.separator;
		}

	}
	
	public void generateCodeVariables (String clnm, ClassDefinition cd)
	{
		// local variables to store the class and interface names
		String cnm = prefix + "Node";
		String inm = iprefix + "Node";
		
		// the class and interface code
		String cls = new String();
		String itf = new String();
		String cst = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		cls += "// import the abstract tree interfaces\n";
		cls += "import "+packname+".itf.*;\n";
		
		// include all node definitions for all subclasses
		for (ClassDefinition scls : cd.sub_classes) {
			cls += "import "+scls.getPackage()+".imp."+scls.class_name+"Node;\n";
		}
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+"\n{\n";
		
		// process the node class instance variables
		HashMap<String,MemberVariable> vars = cd.getAllVariables();
		
		// add comment to interface code if there are node member variables
		if (!vars.keySet().isEmpty()) itf += "\t// member variable read operations\n";
		
		// iterate over the mapping
		for (String vnm : vars.keySet()) {
			// retrieve the member variable
			MemberVariable mv = vars.get(vnm);
			
			// IMPLEMENTATION: member variable initialization block for default constructor
			cst += "\t\tm_"+vnm+" = null;\n";
			
			// retrieve the java type of the member variable
			String jtp = getJavaType(prefix, mv.type);

			// IMPLEMENTATION: declare the member variable
			cls += "\t// private member variable ("+vnm+")\n";
			cls += "\tprivate "+jtp+" m_"+vnm+";\n\n";
			
			// obtain the beautified name for the getter/setter operations
			String fnm = beautify(vnm);
			
			// IMPLEMENTATION: the setter operation
			cls += "\t// public set operation for private member variable ("+vnm+")\n";
			cls += "\tpublic void set"+fnm+"("+jtp+" piv)\n";
			cls += "\t{\n\t\tm_"+vnm+" = piv;\n\t}\n\n";
			
			// IMPLEMENTATION: the getter operation
			cls += "\t// public get operation for private member variable ("+vnm+")\n";
			cls += "\tpublic "+jtp+" get"+fnm+"()\n";
			cls += "\t{\n\t\treturn m_"+vnm+";\n\t}\n\n";
			
			// INTERFACE: the getter operation
			itf += "\tpublic abstract "+jtp+" get"+fnm+"();\n";
		}
		
		// correct the layout of the generated code
		if (!vars.keySet().isEmpty()) {
			itf += "\n";
		}
		
		// create the default constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n";
		cls += "\t{\n"+ cst + "\t}\n\n";
		
		// compose the deep copy operators for each subclass
		for (ClassDefinition scls : cd.sub_classes) {
			// copy operator for the base class (header)
			cls += "\t// copy and convert operator for the node\n";
			cls += "\tpublic final void copy"+scls.class_name+"("+scls.class_name+"Node pnode)\n";
			cls += "\t{\n";
			
			// add a comment if there are node fields to copy
			if (!vars.keySet().isEmpty()) cls += "\t\t// copy all node member variables\n";
			
			// iterate over the set of variables
			for (String vnm : vars.keySet()) {
				// retrieve the member variable
				MemberVariable mv = vars.get(vnm);
				
				// create the copy and convert operation for the node member variable
				cls += "\t\tif (m_"+vnm+" != null) pnode.set"+beautify(vnm);
				cls += "("+getDeepInstanceCopy(mv.type, scls.class_name, "m_"+vnm)+");\n";
			}
			// copy operator for the base class (trailer)
			cls += "\t}\n\n";
			
			// semi-abstract deep copy-and-convert operator
			cls += "\t// deep copy and convert operator for subclass "+scls.class_name+"\n";
			cls += "\tpublic "+scls.class_name+"Node convert"+scls.class_name+"()\n";
			cls += "\t{\n";
			cls += "\t\tthrow new InternalError(\""+cnm+".convert"+scls.class_name+"() illegally called\");\n";
			cls += "\t}\n\n";
		}
		
		// compose the class footer
		cls += "\t// the identity function\n";
		cls += "\tpublic String identify() { return \""+cnm+"\"; }\n";
		cls += "}\n";
		
		// compose the interface footer
		itf += "\t// the identity function\n";
		itf += "\tpublic abstract String identify();\n";
		itf += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
		writeToFile(itfdir+inm+".java", itf);
	}
	
	public void generateCodeTypes(String clnm, ClassDefinition cd)
	{
		// iterate over the list of all defined types in the class definition
		for (String tnm: cd.getAllTypes()) {
			// retrieve the defined type
			Type theType = cd.getTypeByName(tnm);
			
			// consistency check
			if (theType == null) {
				// diagnostics
				System.out.println("Cannot find type '"+tnm+"' in any definition");
				
				// flag error
				errors++;
			} else {
				// dispatch to the appropriate handler (only deal with record and union types)
				if (theType.isRecordType()) {
					// handle the record type
					generateCodeRecordType(clnm, cd, tnm, (RecordType) theType);
				} else if (theType.isUnionType()) {
					// handle the union type
					generateCodeUnionTypes(clnm, cd, tnm, (UnionType) theType);
				}
				// other types are DELIBERATELY skipped!
			}
		}
	}

	public void generateCodeUnionTypes(String clnm, ClassDefinition cd, String tnm, UnionType ut)
	{
		// dispatch to the expected union sub-types
		if (ut.isQuotedTypeUnion() || ut.isTypeNameUnion()) {
			if (ut.isQuotedTypeUnion()) {
				// generate the wrapper class
				generateCodeQuotedTypeUnion(clnm, cd, tnm, ut);
				// generate the enumerator
				generateCodeQuotedTypeUnionEnum(clnm, cd, tnm, ut);
			}
			if (ut.isTypeNameUnion()) generateCodeTypeNameUnion(clnm, cd, tnm, ut);
		} else {
			// diagnostics
			System.out.println("Type union '"+tnm+"' is not supported in class '"+clnm+"'");
			
			// flag error
			errors++;
		}
	}

	public void generateCodeTypeNameUnion(String clnm, ClassDefinition cd, String tnm, UnionType ut)
	{
		// place holder for the class and interface name
		String cnm = prefix + tnm;
		String basenm = prefix + "Node";
		String inm = iprefix + tnm;
		String baseinm = iprefix + "Node";
		
		// the class and interface code
		String cls = new String();
		String itf = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		cls += "// import the abstract tree interfaces\n";
		cls += "import "+packname+".itf.*;\n";
		
		// include all node definitions for all subclasses
		for (ClassDefinition scls : cd.sub_classes) {
			cls += "import "+scls.getPackage()+".imp."+scls.class_name+"Node;\n";
		}
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" extends "+basenm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+" extends "+baseinm+"\n{\n";
		
		// compose the default constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n";
		cls += "\t{\n\t\tsuper();\n\t}\n\n";
		
		// compose the deep copy operators for each subclass
		for (ClassDefinition scls : cd.sub_classes) {
			cls += "\t// deep copy and convert operator for subclass "+scls.class_name+"\n";
			cls += "\tpublic "+scls.class_name+"Node convert"+scls.class_name+"()\n";
			cls += "\t{\n";
			cls += "\t\tthrow new InternalError(\""+cnm+".convert"+scls.class_name+"() illegally called\");\n";
			cls += "\t}\n\n";
		}
		
		// compose the class footer
		cls += "\t// the identity function\n";
		cls += "\tpublic String identify() { return \""+cnm+"\"; }\n";
		cls += "}\n";
		
		// compose the interface footer
		itf += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
		writeToFile(itfdir+inm+".java", itf);		
	}
	
	public void generateCodeQuotedTypeUnionEnum(String clnm, ClassDefinition cd, String tnm, UnionType ut)
	{
		// place holder for the class and interface name
		String cnm = prefix + tnm + "Enum";
	
		// the class code
		String cls = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+"\n{\n";
		
		// declare the private member variable to store the enumerated value
		cls += "\t// private member variable to store enumerated value\n";
		cls += "\tprivate String m_enum;\n\n";
		
		// declare the private constructor
		cls += "\t// private constructor\n";
		cls += "\tprivate "+cnm+"(String pstr) { m_enum = pstr; }\n\n";
		
		// retrieve the set of quoted values
		for (String qv: ut.getQuotedTypes()) {
			// IMPLEMENTATION: create the quoted value interface
			cls += "\t// public enumerated value to describe quoted value <"+qv+">\n";
			cls += "\tpublic static final "+cnm+" I"+qv+" = new "+cnm+"(\""+qv+"\");\n\n";
		}
		
		// compose the class footer
		cls += "\t// the identity function\n";
		cls += "\tpublic String identify() { return m_enum; }\n";
		cls += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
	}

	public void generateCodeQuotedTypeUnion(String clnm, ClassDefinition cd, String tnm, UnionType ut)
	{
		// place holder for the class and interface name
		String cnm = prefix + tnm;
		String basenm = prefix + "Node";
		String inm = iprefix + tnm;
		String baseinm = iprefix + "Node";
		
		// the class and interface code
		String cls = new String();
		String itf = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		cls += "// import the abstract tree interfaces\n";
		cls += "import "+packname+".itf.*;\n";
		
		// include all node definitions for all subclasses
		for (ClassDefinition scls : cd.sub_classes) {
			cls += "import "+scls.getPackage()+".imp.*;\n";
		}
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" extends "+basenm+" implements "+inm+"\n{\n";
		
		// declare the private member variable to store the enumerated value
		cls += "\t// private member variable to store enumerated value\n";
		cls += "\tprivate "+cnm+"Enum m_enum;\n\n";
		
		// declare the setEnum operation
		cls += "\t// public operation to set the enumeration value\n";
		cls += "\tpublic void setEnum("+cnm+"Enum p_enum) { m_enum = p_enum; }\n\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+" extends "+baseinm+"\n{\n";
		
		// retrieve the set of quoted values
		for (String qv: ut.getQuotedTypes()) {
			// IMPLEMENTATION: create the "is" operators
			cls += "\t// public member operation to query the quoted value\n";
			cls += "\tpublic boolean is"+qv+"() { return (m_enum == "+cnm+"Enum.I"+qv+"); }\n\n";
			
			// INTERFACE: declare the "is" operators
			itf += "\tpublic abstract boolean is"+qv+"();\n";
		}
		
		// compose the class constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n\t{\n";
		cls += "\t\tsuper();\n";
		cls += "\t\tm_enum = null;\n";
		cls += "\t}\n\n";
		
		// compose the deep copy operators for each subclass
		for (ClassDefinition scls : cd.sub_classes) {
			// create the operation header
			cls += "\t// deep copy and convert operator for subclass "+scls.class_name+"\n";
			cls += "\tpublic "+scls.class_name+"Node convert"+scls.class_name+"()\n";
			cls += "\t{\n";
			cls += "\t\t// create the subclassed instance\n";
			cls += "\t\t"+scls.class_name+tnm+" res = new "+scls.class_name+tnm+"();\n\n";
					
			// copy the node member variables
			cls += "\t\t// copy the node member variables\n";
			cls += "\t\tcopy"+scls.class_name+"(res);\n\n";
			
			// copy the enumerated value (header)
			cls += "\t\t// copy the enumerated (quoted) value\n";
			cls += "\t\tif (m_enum != null) {\n";
			
			// iterate over the enumeration
			for (String qv: ut.getQuotedTypes()) {
				cls += "\t\t\tif (is"+qv+"()) res.setEnum("+scls.class_name+tnm+"Enum.I"+qv+");\n";
			}
			
			// copy the enumerated value (trailer)
			cls += "\t\t}\n\n";
			
			// create the operation trailer
			cls += "\t\t// return the result\n";
			cls += "\t\treturn res;\n";
			cls += "\t}\n\n";
		}
		
		// compose the class footer
		cls += "\t// the identity function\n";
		cls += "\tpublic String identify() { return m_enum.identify(); }\n";
		cls += "}\n";
		
		// compose the interface footer
		itf += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
		writeToFile(itfdir+inm+".java", itf);		
	}

	public void generateCodeRecordType(String clnm, ClassDefinition cd, String tnm, RecordType rt)
	{
		// place holder for the class and interface name
		String cnm = prefix + tnm;
		String basenm = prefix + cd.getSubtype(tnm);
		String inm = iprefix + tnm;
		String baseinm = iprefix + cd.getSubtype(tnm);
		
		// the class and interface code
		String cls = new String();
		String itf = new String();
		String cst = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		cls += "// import the abstract tree interfaces\n";
		cls += "import "+packname+".itf.*;\n";
		
		// include all node definitions for all subclasses
		for (ClassDefinition scls : cd.sub_classes) {
			cls += "import "+scls.getPackage()+".imp.*;\n";
		}
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" extends "+basenm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+" extends "+baseinm+"\n{\n";
		
		// iterate over the list of fields
		for (Field field: rt.getAllFields()) {
			// retrieve the beautified name of the field name
			String fstr = beautify(field.field_name);
			
			// retrieve the Java type of the field
			String ftpstr = getJavaType(prefix, field.field_type);
			
			// retrieve the abstract Java type of the field
			String fatpstr = getAbstractJavaType(field.field_type);
			
			// retrieve the Java type initializer of the field
			String ftpistr = getJavaTypeInitializer(prefix, field.field_type);
			
			// IMPLEMENTATION: member variable initialization block for default constructor
			cst += "\t\tm_"+field.field_name+" = null;\n";
			
			// IMPLEMENTATION: create the private member variable
			cls += "\t// private member variable ("+field.field_name+")\n";
			cls += "\tprivate " + ftpstr + " m_"+field.field_name+" = " + ftpistr + ";\n\n";
			
			// check for optional type
			if (field.field_type.isOptionalType()) {
				// INTERFACE: create the 'has' operation interface
				itf += "\tpublic abstract boolean has" + fstr + "();\n";
				
				// IMPLEMENTATION: create the 'has' operation
				cls += "\t// public operation to check optional type status\n";
				cls += "\tpublic boolean has" + fstr + "() { return (m_"+field.field_name+" != null); };\n\n";
			}
			
			// INTERFACE: create the 'get' operation interface
			itf += "\tpublic abstract "+fatpstr+" get"+fstr+"();\n";
			
			// IMPLEMENTATION: create the 'get' operation
			cls += "\t// public operation to retrieve the embedded private field value\n";
			cls += "\tpublic "+fatpstr+" get"+fstr+"()\n";
			cls += "\t{\n\t\treturn m_"+field.field_name+";\n\t}\n\n";
			
			// IMPLEMENTATION: create the 'set' operation
			cls += "\t// public operation to set the embedded private field value\n";
			cls += "\tpublic void set"+fstr+"("+ftpstr+" p_"+field.field_name+")\n\t{\n";
			if (!field.field_type.isOptionalType()) {
				cls += "\t\t// consistency check (field must be non null!)\n";
				cls += "\t\tassert(p_"+field.field_name+" != null);\n\n";
			}
			cls += "\t\t// instantiate the member variable\n";
			cls += "\t\tm_"+field.field_name+" = p_"+field.field_name+";\n\t}\n\n";
		}
		
		// compose the default constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n";
		cls += "\t{\n\t\tsuper();\n" + cst + "\t}\n\n";
		
		// compose the deep copy operators for each subclass
		for (ClassDefinition scls : cd.sub_classes) {
			// create the operation header
			cls += "\t// deep copy and convert operator for subclass "+scls.class_name+"\n";
			cls += "\tpublic "+scls.class_name+"Node convert"+scls.class_name+"()\n";
			cls += "\t{\n";
			cls += "\t\t// create the subclassed instance\n";
			cls += "\t\t"+scls.class_name+tnm+" res = new "+scls.class_name+tnm+"();\n\n";
						
			// copy the node member variables
			cls += "\t\t// copy the node member variables\n";
			cls += "\t\tcopy"+scls.class_name+"(res);\n\n";
			
			// add comment if there are fields to copy 
			if (!rt.getAllFields().isEmpty()) cls += "\t\t// copy and convert each member field\n";
			
			// iterate over the list of fields
			for (Field field: rt.getAllFields()) {
				// retrieve the beautified name of the field name
				String fstr = beautify(field.field_name);
				
				// create the field copy action
				String fca = "m_"+field.field_name;
				
				// create copy and convert operators for each field
				if (field.field_type.isCollection()) {
					// collections: sequences set map
					cls += "\t\tif ("+fca+" != null) {\n";
					cls += "\t\t\t// create the new collection\n";

					// sequences
					if (field.field_type.isSeqType()) {
						// retrieve the sequence type
						SeqType theSeqType = (SeqType) field.field_type;
						
						// strings to store Java types
						String itp = getJavaType(prefix, theSeqType.seq_type);
						String rtp = getJavaType(scls.class_name, theSeqType.seq_type);
						String jtp = "java.util.Vector<"+rtp+">";
						
						// create the sequence
						cls += "\t\t\t"+jtp+" c_"+field.field_name+" = new "+jtp+"();\n\n";
						
						// perform deep copy on the sequence
						cls += "\t\t\t// perform deep copy on the collection\n";
						cls += "\t\t\tfor ("+itp+" val: m_"+field.field_name+") {\n";
						cls += "\t\t\t\tc_"+field.field_name+".add(";
						cls += getDeepInstanceCopy(field.field_type,scls.class_name,"val");
						cls += ");\n\t\t\t}\n\n";
					}
					
					// sets
					if (field.field_type.isSetType()) {
						// retrieve the sequence type
						SetType theSetType = (SetType) field.field_type;
						
						// strings to store Java types
						String itp = getJavaType(prefix, theSetType.set_type);
						String rtp = getJavaType(scls.class_name, theSetType.set_type);
						String jtp = "java.util.HashSet<"+rtp+">";
						
						// create the set
						cls += "\t\t\t"+jtp+" c_"+field.field_name+" = new "+jtp+"();\n\n";
						
						// perform deep copy on the set
						cls += "\t\t\t// perform deep copy on the collection\n";
						cls += "\t\t\tfor ("+itp+" val: m_"+field.field_name+") {\n";
						cls += "\t\t\t\tc_"+field.field_name+".add(";
						cls += getDeepInstanceCopy(field.field_type,scls.class_name,"val");
						cls += ");\n\t\t\t}\n\n";
					}
					
					// maps
					if (field.field_type.isMapType()) {	
						// retrieve the sequence type
						MapType theMapType = (MapType) field.field_type;
						
						// strings to store Java types
						String idtp = getJavaType(prefix, theMapType.domain);
						String irtp = getJavaType(prefix, theMapType.range);
						String rdtp = getJavaType(scls.class_name, theMapType.domain);
						String rrtp = getJavaType(scls.class_name, theMapType.range);
						String jtp = "java.util.HashMap<"+rdtp+","+rrtp+">";
						
						// create the map
						cls += "\t\t\t"+jtp+" c_"+field.field_name+" = new "+jtp+"();\n\n";
						
						// perform deep copy on the map
						cls += "\t\t\t// perform deep copy on the collection\n";
						cls += "\t\t\tfor ("+idtp+" dom: m_"+field.field_name+".keySet()) {\n";
						cls += "\t\t\t\t"+irtp+ " rng = m_"+field.field_name+".get(dom);\n";
						cls += "\t\t\t\t"+rdtp+ " key = "+getDeepInstanceCopy(theMapType.domain, scls.class_name, "dom")+";\n";
						cls += "\t\t\t\t"+rrtp+ " val = "+getDeepInstanceCopy(theMapType.range, scls.class_name, "rng")+";\n";
						cls += "\t\t\t\tc_"+field.field_name+".put(key,val);\n";
						cls += "\t\t\t}\n\n";
					}
					
					// store the created collection
					cls += "\t\t\t// store the new collection\n";
					cls += "\t\t\tres.set"+fstr+"(c_"+field.field_name+");\n";
					cls += "\t\t}\n\n";
				} else {
					// normal field type
					cls += "\t\tif ("+fca+" != null) res.set"+fstr;
					cls += "("+getDeepInstanceCopy(field.field_type, scls.class_name, fca)+");\n";
				}
			}
			
			// fix layout if there where fields
			if (!rt.getAllFields().isEmpty()) cls += "\n";
			
			// create the operation trailer
			cls += "\t\t// return the result\n";
			cls += "\t\treturn res;\n";
			cls += "\t}\n\n";
		}
		
		// compose the class footer
		cls += "\t// the identity function\n";
		cls += "\tpublic String identify() { return \""+cnm+"\"; }\n";
		cls += "}\n";
		
		// compose the interface footer
		itf += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
		writeToFile(itfdir+inm+".java", itf);		
	}
}
