package org.overture.tools.treegen.codegenerator;

//project specific imports
import java.util.*;
import java.io.*;

import org.overture.tools.treegen.typecheck.*;

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
	
	// place holder for the visitors
	private String ibvitf;
	private String ibvimp;
	
	//
	// CONSTRUCTOR
	//
	
	public CodeGenerator (
			String ppnm,
			String pbnm,
			String ptlvl
	) {
		// set the global variables
		this.packname = ppnm;
		this.basedir = pbnm;
	}
	
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
			return "Vector<" + etp + ">";
		}
		
		// deal with sets
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSetType.set_type);
			
			// return the result string
			return "HashSet<" + etp + ">";
		}
		
		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getJavaType(pfx, theMapType.domain);
			String ertp = getJavaType(pfx, theMapType.range);
			
			// return the result string
			return "HashMap<"+edtp+","+ertp+">";
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
			return "I" + pfx + theTypeName.type_name;
		}
		
		// default (implies an error!)
		return "undefined";
	}
	
	public String getAbstractJavaType(String pfx, Type tp)
	{
		// check for abstract data types (sequences)
		if (tp.isSeqType()) {
			// overrule seq of char
			if (tp.isStringType()) return "String";
			
			// convert to proper type
			SeqType theSeqType = (SeqType) tp;
			
			// retrieve the embedded type string
			String etp = getAbstractJavaType(pfx, theSeqType.seq_type);
			
			// return the result string
			return "List<" + etp + ">";
		}
		
		// check for abstract data types (sets)
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getAbstractJavaType(pfx, theSetType.set_type);
			
			// return the result string
			return "Set<" + etp + ">";
		}
		
		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getAbstractJavaType(pfx, theMapType.domain);
			String ertp = getAbstractJavaType(pfx, theMapType.range);
			
			// return the result string
			return "Map<"+edtp+","+ertp+">";
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
			return pfx + theTypeName.type_name;
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
			return "new Vector<" + etp + ">()";
		}
		
		// deal with sets
		if (tp.isSetType()) {
			// convert to proper type
			SetType theSetType = (SetType) tp;
			
			// retrieve the embedded type string
			String etp = getJavaType(pfx, theSetType.set_type);
			
			// return the result string
			return "new HashSet<" + etp + ">()";
		}

		// deal with maps
		if (tp.isMapType()) {
			// convert to proper type
			MapType theMapType = (MapType) tp;
			
			// retrieve the embedded type strings
			String edtp = getJavaType(pfx, theMapType.domain);
			String ertp = getJavaType(pfx, theMapType.range);
			
			// return the result string
			return "new HashMap<"+edtp+","+ertp+">()";
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
	
	public String getDeepInstanceCopy(Type tp, String scp)
	{
		// create a copy of the Java basic types
		if (tp.isBooleanType()) return "new Boolean("+scp+")"; 
		if (tp.isNatType()) return "new Integer("+scp+")";
		if (tp.isRealType()) return "new Double("+scp+")";
		if (tp.isCharType()) return "new Character("+scp+")";
		if (tp.isStringType()) return "new String("+scp+")";

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
	
	public boolean isUserDefined(Type tp)
	{
		// check sequence type
		if (tp.isSeqType()) {
			// obtain and check the embedded type
			SeqType theSeqType = (SeqType) tp;
			return isUserDefined(theSeqType.seq_type);
		}
		
		// check set type
		if (tp.isSetType()) {
			// obtain and check the embedded type
			SetType theSetType = (SetType) tp;
			return isUserDefined(theSetType.set_type);
		}
		
		// check map type
		if (tp.isMapType()) {
			// obtain and check the embedded type
			MapType theMapType = (MapType) tp;
			return (isUserDefined(theMapType.domain) || isUserDefined(theMapType.range));
		}
		
		if (tp.isTypeName()) {
			// obtain and check the embedded type
			TypeName theTypeName = (TypeName) tp;
			
			// retrieve the type from the class definition look-up table
			Type theEmbeddedType = current.getTypeByName(theTypeName.type_name);
			
			// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
			if (theEmbeddedType != null) {
				return  (!theEmbeddedType.isStringType());
			} else {
				return true;
			}
		}
		
		// default return (all other cases)
		return false;
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
		
		// generate the top-level entry classes
		if (errors == 0) {
			// generate [I]Document classes if top-level types where defined
			if (!cd.getToplevel().isEmpty()) generateToplevel(clnm, cd);
		}
		
		// next create the visitors
		if (errors == 0) generateCodeVisitorsPre(clnm, cd);
		
		// then create classes and interfaces for each defined type
		if (errors == 0) generateCodeTypes(clnm, cd);
		
		// next create the visitors
		if (errors == 0) generateCodeVisitorsPost(clnm, cd);
		
		// finally generate the deep copy constructors
		if (errors == 0) generateConvertors(clnm, cd);
}
	
	public void generateCodeValues(String clnm, ClassDefinition cd)
	{
		// retrieve the top-level directory (and replace all double backslashes by a single backslash)
		basedir = cd.getDirectory().replaceAll("\\\\\\\\", "\\\\");
		
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
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+"\n{\n";
		
		// IMPLEMENTATION: add the pointer to the parent
		cls += "\t// link each node to a possible parent node\n";
		cls += "\tprivate "+inm+" parent = null;\n\n";
		
		// IMPLEMENTATION: add an operation to retrieve the parent
		cls += "\t// retrieve the parent node\n";
		cls += "\tpublic "+inm+" getParent() { return parent; }\n\n";
		
		// IMPLEMENTATION: add an operation to set the parent
		cls += "\t// set the parent node\n";
		cls += "\tpublic void setParent("+inm+" pNode)\n";
		cls += "\t{\n\t\tassert(pNode != null);\n\t\tparent = pNode;\n\t}\n\n";
		
		// INTERFACE: add the operation to set and get the parent
		itf += "\t// get the parent of this node\n";
		itf += "\tpublic abstract "+inm+" getParent();\n\n";
		itf += "\t// set the parent of this node\n";
		itf += "\tpublic abstract void setParent("+inm+" pNode);\n\n";
		
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
				
		// add the visitor
		itf += "\t// visitor support\n";
		itf += "\tpublic abstract void accept("+iprefix+"Visitor pVisitor);\n\n";
		cls += "\t// visitor support\n";
		cls += "\tpublic void accept("+iprefix+"Visitor pVisitor) { pVisitor.visitNode(this); }\n\n";
		
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
	
	public void generateToplevel (String clnm, ClassDefinition cd)
	{
		// local variables to store the class and interface names
		String cnm = prefix + "Document";
		String inm = iprefix + "Document";
		
		// the class and interface code
		String cls = new String();
		String itf = new String();
		String vis = new String();
		
		// compose the class header
		cls += "// this file is automatically generated by treegen. do not modify!\n\n";
		cls += "package "+packname+".imp;\n\n";
		cls += "// import the abstract tree interfaces\n";
		cls += "import "+packname+".itf.*;\n";
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// compose the class body
		cls += "public class "+cnm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+"\n{\n";
		
		// IMPLEMENTATION: add private member variable to hold the top-level tree instance
		cls += "\t// private member variable to store the top-level tree instance\n";
		cls += "\tprivate "+iprefix+"Node m_node;\n\n";
		
		// IMPLEMENTATION: add public default constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+prefix+"Document() { m_node = null; }\n\n";
		
		// iterate over the list of top-level entries (if available)
		for (String tnm : cd.getToplevel()) {
			// INTERFACE: add the is-operator
			itf += "\tpublic abstract boolean is"+tnm+"();\n";
			
			// IMPLEMENTATION: add the is-operator interface
			cls += "\t// public is-operator to test the type of node\n";
			cls += "\tpublic boolean is"+tnm+"() { return (m_node instanceof "+iprefix+tnm+"); }\n\n";
			
			// IMPLEMENTATION: add the set operation
			cls += "\t// public set operation\n";
			cls += "\tpublic void set"+tnm+"("+iprefix+tnm+" p_node)\n";
			cls += "\t{\n";
			cls += "\t\t// consistency check and assign value\n";
			cls += "\t\tassert(p_node != null);\n";
			cls += "\t\tm_node = p_node;\n";
			cls += "\t}\n\n";
			
			// INTERFACE: add the get operation interface
			itf += "\tpublic abstract "+iprefix+tnm+" get"+tnm+"();\n";
			
			// IMPLEMENTATION: add the get operation
			cls += "\t// public get operation\n";
			cls += "\tpublic "+iprefix+tnm+" get"+tnm+"()\n";
			cls += "\t{\n";
			cls += "\t\t// consistency check and return value\n";
			cls += "\t\tassert(is"+tnm+"());\n";
			cls += "\t\treturn ("+iprefix+tnm+") m_node;\n";
			cls += "\t}\n\n";
			
			// IMPLEMENTATION: visitor dispatcher
			vis += "\t\tif(is"+tnm+"()) pVisitor.visit"+tnm+"(("+iprefix+tnm+") m_node);\n";
		}
		
		// add the visitor
		itf += "\tpublic abstract void accept("+iprefix+"Visitor pVisitor);\n\n";
		cls += "\t// visitor support\n";
		cls += "\tpublic void accept("+iprefix+"Visitor pVisitor)\n";
		cls += "\t{\n"+ vis + "\t}\n\n";
		
		// compose the class footer
		cls += "}\n";
		
		// compose the interface footer
		itf += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm+".java", cls);
		writeToFile(itfdir+inm+".java", itf);
	}
	
	public void generateCodeVisitorsPre (String clnm, ClassDefinition cd)
	{
		// initialize the visitor code blocks
		ibvimp = new String();
		ibvitf = new String();
		
		// compose the visitor implementation header
		ibvimp += "// this file is automatically generated by treegen. do not modify!\n\n";
		ibvimp += "package "+packname+".imp;\n\n";
		ibvimp += "// import the abstract tree interfaces\n";
		ibvimp += "import "+packname+".itf.*;\n\n";
		ibvimp += "public class "+clnm+"Visitor implements I"+clnm+"Visitor {\n\n";
		
		// add default (and empty) INode visitor
		ibvimp += "\t// empty visitor for type I"+cd.class_name+"Node\n";
		ibvimp += "\tpublic void visitNode(I"+cd.class_name+"Node pNode) {};\n\n";
		
		// compose the visitor interface header
		ibvitf += "// this file is automatically generated by treegen. do not modify!\n\n";
		ibvitf += "package "+packname+".itf;\n\n";
		ibvitf += "public abstract interface I"+clnm+"Visitor {\n";
		ibvitf += "\t// visitor operation signatures for all "+ cd.class_name+" types\n";
		ibvitf += "\tpublic abstract void visitNode(I"+cd.class_name+"Node pNode);\n";
	}
	
	public void generateCodeVisitorsPost (String clnm, ClassDefinition cd)
	{
		// finish the visitor footers
		ibvimp += "}\n";
		ibvitf += "}\n";
		
		// finally save the visitors to file
		writeToFile(impdir+prefix+"Visitor.java", ibvimp);
		writeToFile(itfdir+iprefix+"Visitor.java", ibvitf);
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
		
		// fix the class header layout
		cls += "\n";
		
		// generate the visitor operation interface
		ibvitf += "\tpublic abstract void visit"+tnm+"(I"+cnm+" pNode);\n";
		
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
		
		// compose the visitor accept operation
		cls += "\t// visitor support\n";
		cls += "\tpublic void accept("+iprefix+"Visitor pVisitor) { pVisitor.visit"+tnm+"(this); }\n\n";
		
		// compose the visitor operation
		generateTypeUnionVisitor(tnm, inm, ut);
		
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
	
	public void generateTypeUnionVisitor(String tnm, String inm, UnionType ut)
	{
		// compose the visitor visit operation
		ibvimp += "\t// visitor operation for type "+inm+" (cannot be overridden)\n";
		ibvimp += "\tpublic void visit"+tnm+"("+inm+" pNode)\n\t{\n";
		
		// check for possible subtypes
		if (!ut.getTypeNames().isEmpty()) ibvimp += "\t\t// dispatch to handler of correct sub-type\n";
		
		// create the dispatcher for the visitor
		for (String sclsnm: ut.getTypeNames()) {
			// obtain the interface type name
			String itnm = iprefix + sclsnm;
			
			// compose the dispatch routine
			ibvimp += "\t\tif (pNode instanceof "+itnm+")\n";
			ibvimp += "\t\t\t{ visit"+sclsnm+"(("+itnm+") pNode); return; }\n";
		}
		
		// create the error handler for the dispatcher
		ibvimp += "\t\t// default error handler (should never be called)\n";
		ibvimp += "\t\tthrow new InternalError(\"Type '\"+pNode.identify()+\"' is not subtype of '"+inm+"'\");\n";
		
		// complete the visitor
		ibvimp += "\t};\n\n";
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
			cls += "\tpublic static final "+cnm+" E"+qv+" = new "+cnm+"(\""+qv+"\");\n\n";
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
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// generate the visitor operations
		ibvitf += "\tpublic abstract void visit"+tnm+"(I"+cnm+" pNode);\n";
		
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
			cls += "\tpublic boolean is"+qv+"() { return (m_enum == "+cnm+"Enum.E"+qv+"); }\n\n";
			
			// INTERFACE: declare the "is" operators
			itf += "\tpublic abstract boolean is"+qv+"();\n";
		}
		
		// compose the class constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n\t{\n";
		cls += "\t\tsuper();\n";
		cls += "\t\tm_enum = null;\n";
		cls += "\t}\n\n";
		
		// compose the visitor accept operation
		cls += "\t// visitor support\n";
		cls += "\tpublic void accept("+iprefix+"Visitor pVisitor) { pVisitor.visit"+tnm+"(this); }\n\n";
		
		// compose the visitor visit operation
		ibvimp += "\t// empty visitor operation for type "+inm+"\n";
		ibvimp += "\tpublic void visit"+tnm+"("+inm+" pNode) {};\n\n";
		
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
		
		// fix the class header layout
		cls += "\n";
		
		// compose the interface header
		itf += "// this file is automatically generated by treegen. do not modify!\n\n";
		itf += "package "+packname+".itf;\n\n";
		
		// check for use of collection types
		if (cd.hasCollection(tnm)) {
			// IMPLEMENTATION
			cls += "// import java collection types\n";
			cls += "import java.util.*;\n\n";
			// INTERFACE
			itf += "// import java collection types\n";
			itf += "import java.util.*;\n\n";
		}
		
		// generate the visitor operations
		ibvitf += "\tpublic abstract void visit"+tnm+"(I"+cnm+" pNode);\n";
		
		// compose the class body
		cls += "public class "+cnm+" extends "+basenm+" implements "+inm+"\n{\n";
		
		// compose the interface body
		itf += "public abstract interface "+inm+" extends "+baseinm+"\n{\n";
		
		// iterate over the list of fields
		for (Field field: rt.getAllFields()) {
			// retrieve the beautified name of the field name
			String fstr = beautify(field.field_name);
			
			// retrieve the abstract Java type of the field
			String fatpstr = getAbstractJavaType(iprefix, field.field_type);
			
			// retrieve the Java type initializer of the field
			String ftpistr = getJavaTypeInitializer(prefix, field.field_type);
			
			// IMPLEMENTATION: member variable initialization block for default constructor
			cst += "\t\tm_"+field.field_name+" = null;\n";
			
			// IMPLEMENTATION: create the private member variable
			cls += "\t// private member variable ("+field.field_name+")\n";
			cls += "\tprivate " + fatpstr + " m_"+field.field_name+" = " + ftpistr + ";\n\n";
			
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
			cls += "\tpublic void set"+fstr+"("+fatpstr+" p_"+field.field_name+")\n\t{\n";
			if (!field.field_type.isOptionalType()) {
				cls += "\t\t// consistency check (field must be non null!)\n";
				cls += "\t\tassert(p_"+field.field_name+" != null);\n\n";
			}
			cls += "\t\t// instantiate the member variable\n";
			cls += "\t\tm_"+field.field_name+" = p_"+field.field_name+";\n";
			cls += generateSetParent(field);
			cls += "\t}\n\n";
		}
		
		// compose the default constructor
		cls += "\t// default constructor\n";
		cls += "\tpublic "+cnm+"()\n";
		cls += "\t{\n\t\tsuper();\n" + cst + "\t}\n\n";
		
		// compose the visitor accept operation
		cls += "\t// visitor support\n";
		cls += "\tpublic void accept("+iprefix+"Visitor pVisitor) { pVisitor.visit"+tnm+"(this); }\n\n";
		
		// compose the visitor visit operation
		if (cd.getRootTypeByName(tnm).isUnionType()) {
			// retrieve the type name union
			UnionType ut = (UnionType) cd.getRootTypeByName(tnm);
			generateTypeUnionVisitor(tnm, inm, ut);
		} else {
			ibvimp += "\t// empty visitor operation for type "+inm+"\n";
			ibvimp += "\tpublic void visit"+tnm+"("+inm+" pNode) {};\n\n";
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

	public String generateSetParent(Field field)
	{
		// check type names
		if (field.field_type.isTypeName() && isUserDefined(field.field_type)) {
			String retval = "\n\t\t// set the parent of the parameter passed\n";
			retval += "\t\tp_"+field.field_name+".setParent(this);\n";
			return retval;
		}
		
		// check the sequences
		if (field.field_type.isSeqType() && isUserDefined(field.field_type)) {
			String retval = "\n\t\t// set the parent of each element in the sequence parameter passed\n";
			retval += "\t\tfor ("+iprefix+"Node lnode: p_"+field.field_name+") lnode.setParent(this);\n";
			return retval;
		}
		
		// check the sets
		if (field.field_type.isSetType() && isUserDefined(field.field_type)) {
			String retval = "\n\t\t// set the parent of each element in the set parameter passed\n";
			retval += "\t\tfor ("+iprefix+"Node lnode: p_"+field.field_name+") lnode.setParent(this);\n";
			return retval;
		}
		
		// check the maps
		if (field.field_type.isMapType()) {
			// cast to a map type
			MapType theMapType = (MapType) field.field_type;
			
			// compose the result string
			String retval = new String();
			
			// first check the domain
			if (isUserDefined(theMapType.domain)) {
				retval += "\n\t\t// set the parent of each domain element in the map parameter passed\n";
				retval += "\t\tfor ("+iprefix+"Node lnode: p_"+field.field_name+".keySet()) lnode.setParent(this);\n";
			}
			
			// then check the range
			if (isUserDefined(theMapType.range)) {
				retval += "\n\t\t// set the parent of each range element in the map parameter passed\n";
				retval += "\t\tfor ("+iprefix+"Node lnode: p_"+field.field_name+".values()) lnode.setParent(this);\n";
			}
			
			// return the composed string
			return retval;
		}
		
		// default return
		return "";
	}
	
	public void generateConvertors(String clnm, ClassDefinition cd)
	{
		// first check if there are defined subtypes
		if (cd.super_class != null) {
			// generate the converter wrapper class
			generateConvertClass(clnm, cd, cd.super_class.class_name, cd.super_class);
			
			// generate the converter visitor class
			generateConvertVisitor(clnm, cd, cd.super_class.class_name, cd.super_class);
		}
	}
	
	public void generateConvertClass (
			String cnm1,
			ClassDefinition cd1,
			String cnm2,
			ClassDefinition cd2
	) {
		// place holders for the convert text
		String cvt = new String();
		
		// compose the class header
		cvt += "// this file is automatically generated by treegen. do not modify!\n\n";
		cvt += "package "+cd1.getPackage()+".imp;\n\n";
		cvt += "// import the abstract tree interfaces\n";
		cvt += "import "+cd1.getPackage()+".itf.*;\n";
		cvt += "import "+cd2.getPackage()+".itf.*;\n";
		
		// fix the class header layout
		cvt += "\n";
		
		// compose the class body
		cvt += "public class "+cnm2+"Convert\n{\n";

		// generate the top-level convert routine
		cvt += "\t// top-level convert operation\n";
		cvt += "\tpublic I"+cnm1+"Document convert(I"+cnm2+"Document pDocument)\n";
		cvt += "\t{\n";
		
		// create the result instance
		cvt += "\t\t// create the result instance\n";
		cvt += "\t\t"+cnm1+"Document nDocument = new "+cnm1+"Document();\n\n";
		
		// create the convert visitor
		cvt += "\t\t// create the converter visitor\n";
		cvt += "\t\t"+cnm2+"ConvertVisitor theConverter = new "+cnm2+"ConvertVisitor();\n\n";
		
		// execute the converter
		cvt += "\t\t// execute the converter\n";
		cvt += "\t\tpDocument.accept(theConverter);\n\n";
		
		// check the result type
		cvt += "\t\t// store the result based on the appropriate top-level type\n";
		for (String tnm: cd2.getToplevel()) {
			cvt += "\t\tif(pDocument.is"+tnm+"()) nDocument.set"+tnm;
			cvt += "((I"+cnm1+tnm+") theConverter.m_result);\n";
		}
		cvt += "\n";
		
		// return the result
		cvt += "\t\t// return the result\n";
		cvt += "\t\treturn nDocument;\n";
		cvt += "\t}\n\n";
		
		// compose the class footer
		cvt += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm2+"Convert.java", cvt);
	}

	public void generateConvertVisitor (
			String cnm1,
			ClassDefinition cd1,
			String cnm2,
			ClassDefinition cd2
	) {
		// place holders for the convert text
		String cvv = new String();
		
		// compose the class header
		cvv += "// this file is automatically generated by treegen. do not modify!\n\n";
		cvv += "package "+cd1.getPackage()+".imp;\n\n";
		cvv += "// import the java abstract data types\n";
		cvv += "import java.util.*;\n\n";
		cvv += "// import the abstract tree interfaces\n";
		cvv += "import "+cd2.getPackage()+".itf.*;\n";
		cvv += "import "+cd1.getPackage()+".itf.*;\n\n";
		
		// compose the class body
		cvv += "public class "+cnm2+"ConvertVisitor implements I"+cnm2+"Visitor\n{\n";
		
		// create the public member variable to store intermediate results
		cvv += "\t// member variable to store intermediate results\n";
		cvv += "\tpublic "+cnm1+"Node m_result;\n\n";
		
		// create the constructor of the visitor
		cvv += "\t// default constructor\n";
		cvv += "\tpublic "+cnm2+"ConvertVisitor() { m_result = null; }\n\n";
		
		// generate the node converter
		cvv += generateConvertNodeVisitor(cnm1, cd1, cnm2, cd2);
		
		// iterate over the list of all defined types in the class definition
		for (String tnm: cd2.getAllTypes()) {
			// retrieve the defined type
			Type theType = cd2.getTypeByName(tnm);
			
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
					cvv += generateConvertRecordVisitor(cnm1, cd1, cnm2, cd2, tnm, (RecordType) theType);
				} else if (theType.isUnionType()) {
					// handle the union type
					cvv += generateConvertTypeUnionVisitor(cnm1, cd1, cnm2, cd2, tnm, (UnionType) theType);
				}
				// other types are DELIBERATELY skipped!
			}
		}
		
		// compose the class footer
		cvv += "}\n";
		
		// write the class code to a file
		writeToFile(impdir+cnm2+"ConvertVisitor.java", cvv);
	}
	
	public String generateConvertNodeVisitor(
		String cnm1,
		ClassDefinition cd1,
		String cnm2,
		ClassDefinition cd2
	) {
		// place holders for the convert text
		String cvvn = new String();

		// process the node class instance variables
		HashMap<String,MemberVariable> vars = cd2.getAllVariables();
		
		// generate the operation header
		cvvn += "\t// deep copy constructor for the node\n";
		cvvn += "\tpublic void visitNode(I"+cnm2+"Node p_node)\n";
		cvvn += "\t{\n";
		
		// add remark conditionally
		if (!vars.keySet().isEmpty()) {
			cvvn += "\t\t// consistency check\n";
			cvvn += "\t\tassert(m_result != null);\n";
			cvvn += "\t\tassert(p_node != null);\n";
		}
		
		// iterate over the list of member variables
		for (String vnm : vars.keySet()) {
			// generate comment
			cvvn += "\n\t\t// perform deep copy on member variable '"+vnm+"'\n";
			
			// retrieve the member variable
			MemberVariable mv = vars.get(vnm);
			
			// create the copy and convert operation for the node member variable
			cvvn += "\t\t"+getJavaType(cnm2, mv.type)+" m_"+vnm+" = p_node.get";
			cvvn += beautify(vnm)+"();\n";
			cvvn += "\t\tif (m_"+vnm+" != null) m_result.set"+beautify(vnm);
			cvvn += "("+getDeepInstanceCopy(mv.type, "m_"+vnm)+");\n";
		}
		
		// the operation trailer
		cvvn += "\t}\n\n";
		
		// return the result
		return cvvn;
	}
	
	public String generateConvertRecordVisitor(
		String cnm1,
		ClassDefinition cd1,
		String cnm2,
		ClassDefinition cd2,
		String tnm,
		RecordType rtp
	) {
		// place holders for the convert text
		String cvvn = new String();
		
		// generate the operation header
		cvvn += "\t// deep copy constructor for the '"+tnm+"' type\n";
		cvvn += "\tpublic void visit"+tnm+"(I"+cnm2+tnm+" p_node)\n";
		cvvn += "\t{\n";
		
		// consistency check
		cvvn += "\t\t// consistency check\n";
		cvvn += "\t\tassert(p_node != null);\n";
		
		// place holder to check for type name union
		boolean istnu = false;
		
		// check for type name union as root type
		Type theRootType = cd2.getRootTypeByName(tnm);
		if (theRootType != null) {
			if (theRootType.isUnionType()) {
				UnionType theRootUnionType = (UnionType) theRootType;
				if (theRootUnionType.isTypeNameUnion()) {
					// iterate over the type name union
					cvvn += "\n\t\t// first dispatch to appropriate sub-class visitor\n";
					for (String qnm: theRootUnionType.getTypeNames()) {
						cvvn += "\t\tif (p_node instanceof I"+cnm2+qnm+")\n";
						cvvn += "\t\t\tvisit"+qnm+"((I"+cnm2+qnm+") p_node);\n";
					}
					
					// set the type name union indicator
					istnu = true;
				}
			}
		}
		
		// generate the local result type
		cvvn += "\n\t\t// declare the local result type\n";
		if (istnu) {
			cvvn += "\t\t"+cnm1+tnm+" l_result = ("+cnm1+tnm+") m_result;\n\n";		
		} else {
			cvvn += "\t\t"+cnm1+tnm+" l_result = new "+cnm1+tnm+"();\n\n";
		}
			
		// iterate over the list of fields
		for (Field field: rtp.getAllFields()) {
			// retrieve the beautified name of the field name
			String fstr = beautify(field.field_name);
			
			// create the field copy action
			String fca = "t_"+field.field_name;
			
			// obtain the java type of the field
			String ftp = getJavaType(cnm1,field.field_type);
			
			// add comment
			cvvn += "\t\t// copy and convert member field '"+field.field_name+"'\n";
			
			// retrieve the field
			cvvn += "\t\t"+getAbstractJavaType("I"+cnm2, field.field_type)+" "+fca+" = p_node.get"+fstr+"();\n";
			
			// process the field
			cvvn += generateConvertFieldVisitor(fca,fstr,ftp,"I"+cnm2,field.field_type);
		}
		
		// check whether the super type is also a record
		if (istnu) {
			cvvn += "\t\t// result and node member variables are already copied\n";
		} else {
			// copy the result
			cvvn += "\t\t// copy the result\n";
			cvvn += "\t\tm_result = l_result;\n";
			
			// call the convert operation on the node
			cvvn += "\n\t\t// convert the node member variables\n";
			cvvn += "\t\tvisitNode(p_node);\n";							
		}
		
		// the operation trailer
		cvvn += "\t}\n\n";
				
		// return the result
		return cvvn;
	}

	public String generateConvertFieldVisitor(
			String fnm,
			String bnm,
			String tnm,
			String pfx,
			Type ftp
	) {
		// place holder for the converted string
		String cfld = new String();
		
		// check for collection type
		if (ftp.isCollection()) {
			// handle sequence types
			if (ftp.isSeqType()) {
				// cast to sequence type
				SeqType theSeqType = (SeqType) ftp;
				
				// retrieve the target sequence type
				String tstp = getAbstractJavaType(iprefix, theSeqType);
				String istp = getJavaTypeInitializer(prefix, theSeqType);
				String etp = getAbstractJavaType(pfx, theSeqType.seq_type);
				
				// create the target sequence type (pre)
				cfld += "\t\t"+tstp+" r_"+fnm+" = "+istp+";\n";
				cfld += "\t\tfor ("+etp+" t_elem: "+fnm+") {\n";
				
				// deal with type names
				if (theSeqType.seq_type.isTypeName()) {
					// convert to proper type
					TypeName theTypeName = (TypeName) theSeqType.seq_type;
				
					// retrieve the type from the class definition look-up table
					Type theType = current.getTypeByName(theTypeName.type_name);
								
					// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
					if (theType != null) {
						if (theType.isStringType()) {
							// copy the string field
							cfld += "\t\t\tr_"+fnm+".add(new String(t_elem));\n";
						} else {
							// recursively call the visitor and store the result
							cfld += "\t\t\t// recursively call the visitor and store the result\n";
							cfld += "\t\t\tvisit"+theTypeName.type_name+"(t_elem);\n";
							cfld += "\t\t\tr_"+fnm+".add(("+iprefix+theTypeName.type_name+") m_result);\n";
						}
					}
				} else {
					// it is a basic type
					cfld += "\t\t\tr_"+fnm+".add("+getDeepInstanceCopy(theSeqType.seq_type, "t_elem")+");\n";
				}

				// target sequence (post)
				cfld += "\t\t}\n";
				cfld += "\t\tl_result.set"+bnm+"(r_"+fnm+");\n\n";
			}
			
			// handle set types
			if (ftp.isSetType()) {
				// cast to set type
				SetType theSetType = (SetType) ftp;
				
				// retrieve the target sequence type
				String tstp = getAbstractJavaType(iprefix, theSetType);
				String istp = getJavaTypeInitializer(prefix, theSetType);
				String etp = getAbstractJavaType(pfx, theSetType.set_type);
				
				// create the target sequence type (pre)
				cfld += "\t\t"+tstp+" r_"+fnm+" = "+istp+";\n";
				cfld += "\t\tfor ("+etp+" t_elem: "+fnm+") {\n";
				
				// deal with type names
				if (theSetType.set_type.isTypeName()) {
					// convert to proper type
					TypeName theTypeName = (TypeName) theSetType.set_type;
				
					// retrieve the type from the class definition look-up table
					Type theType = current.getTypeByName(theTypeName.type_name);
								
					// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
					if (theType != null) {
						if (theType.isStringType()) {
							// copy the string field
							cfld += "\t\t\tr_"+fnm+".add(new String(t_elem));\n";
						} else {
							// recursively call the visitor and store the result
							cfld += "\t\t\t// recursively call the visitor and store the result\n";
							cfld += "\t\t\tvisit"+theTypeName.type_name+"(t_elem);\n";
							cfld += "\t\t\tr_"+fnm+".add(("+iprefix+theTypeName.type_name+") m_result);\n";
						}
					}
				} else {
					// it is a basic type
					cfld += "\t\t\tr_"+fnm+".add("+getDeepInstanceCopy(theSetType.set_type, "t_elem")+");\n";
				}

				// target set (post)
				cfld += "\t\t}\n";
				cfld += "\t\tl_result.set"+bnm+"(r_"+fnm+");\n\n";
			}
			
			// handle map types
			if (ftp.isMapType()) {
				// cast to map type
				MapType theMapType = (MapType) ftp;
				
				// retrieve the target sequence type
				String tstp = getAbstractJavaType(iprefix, theMapType);
				String istp = getJavaTypeInitializer(prefix, theMapType);
				String edtp = getAbstractJavaType(pfx, theMapType.domain);
				String ertp = getAbstractJavaType(pfx, theMapType.range);
				
				// create the target sequence type (pre)
				cfld += "\t\t"+tstp+" r_"+fnm+" = "+istp+";\n";
				cfld += "\t\tfor ("+edtp+" t_delem: "+fnm+".keySet()) {\n";
				cfld += "\t\t\t// retrieve the associated range element\n";
				cfld += "\t\t\t"+ertp+" t_relem = "+fnm+".get(t_delem);\n";
				
				// add remark
				cfld += "\t\t\t// convert the domain element\n";
				
				// convert the domain element
				if (theMapType.domain.isTypeName()) {
					// convert to proper type
					TypeName theTypeName = (TypeName) theMapType.domain;
					
					// retrieve the type from the class definition look-up table
					Type theType = current.getTypeByName(theTypeName.type_name);
								
					// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
					if (theType != null) {
						if (theType.isStringType()) {
							// copy the string field
							cfld += "\t\t\tString r_t_delem = new String(t_delem);\n";
						} else {
							// compose the result type
							String restp = iprefix+theTypeName.type_name;
							// recursively call the visitor and store the result
							cfld += "\t\t\tvisit"+theTypeName.type_name+"(t_delem);\n";
							cfld += "\t\t\t"+restp+" r_t_delem = ("+restp+") m_result;\n";
						}
					}
				} else {
					// compose the result type
					String restp = this.getJavaType(prefix, theMapType.domain);
					// it is a basic type
					cfld += "\t\t\t"+restp+" r_t_delem = "+getDeepInstanceCopy(theMapType.domain, "t_delem")+";\n";
				}
				
				// add remark
				cfld += "\t\t\t// convert the range element\n";
				
				// convert the range element
				if (theMapType.range.isTypeName()) {
					// convert to proper type
					TypeName theTypeName = (TypeName) theMapType.range;
					
					// retrieve the type from the class definition look-up table
					Type theType = current.getTypeByName(theTypeName.type_name);
								
					// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
					if (theType != null) {
						if (theType.isStringType()) {
							// copy the string field
							cfld += "\t\t\tString r_t_relem = new String(t_relem);\n";
						} else {
							// compose the result type
							String restp = iprefix+theTypeName.type_name;
							// recursively call the visitor and store the result
							cfld += "\t\t\tvisit"+theTypeName.type_name+"(t_relem);\n";
							cfld += "\t\t\t"+restp+" r_t_relem = ("+restp+") m_result;\n";
						}
					}
				} else {
					// compose the result type
					String restp = this.getJavaType(prefix, theMapType.range);
					// it is a basic type
					cfld += "\t\t\t"+restp+" r_t_relem = "+getDeepInstanceCopy(theMapType.range, "t_relem")+";\n";
				}
				
				// insert the converted elements in the map
				cfld += "\t\t\t// add the converted elements to the map\n";
				cfld += "\t\t\tr_"+fnm+".put(r_t_delem, r_t_relem);\n";
				
				// target map (post)
				cfld += "\t\t}\n";
				cfld += "\t\tl_result.set"+bnm+"(r_"+fnm+");\n\n";
			}
		} else {
			// normal field type
			cfld += "\t\tif ("+fnm+" != null) {\n";
			
			// deal with type names
			if (ftp.isTypeName()) {
				// convert to proper type
				TypeName theTypeName = (TypeName) ftp;
			
				// retrieve the type from the class definition look-up table
				Type theType = current.getTypeByName(theTypeName.type_name);
							
				// check for string type (a shorthand was used to denote 'seq of char' as the embedded type
				if (theType != null) {
					if (theType.isStringType()) {
						// copy the string field
						cfld += "\t\t\tl_result.set"+bnm+"(new String("+fnm+"));\n";
					} else {
						// recursively call the visitor and store the result
						cfld += "\t\t\t// recursively call the visitor and store the result\n";
						cfld += "\t\t\tvisit"+theTypeName.type_name+"("+fnm+");\n";
						cfld += "\t\t\tl_result.set"+bnm+"(("+iprefix+theTypeName.type_name+") m_result);\n";
					}
				}
			} else {
				// it is a basic type
				cfld += "\t\t\tl_result.set"+bnm+"("+getDeepInstanceCopy(ftp, fnm)+");\n";
			}

			// fix the lay-out
			cfld += "\t\t}\n\n";
		}
		
		// return the result
		return cfld;
	}
	
	public String generateConvertTypeUnionVisitor(
		String cnm1,
		ClassDefinition cd1,
		String cnm2,
		ClassDefinition cd2,
		String tnm,
		UnionType rtp
	) {
		// place holders for the convert text
		String cvvn = new String();
		
		// generate the operation header
		cvvn += "\t// deep copy constructor for the '"+tnm+"' type\n";
		cvvn += "\tpublic void visit"+tnm+"(I"+cnm2+tnm+" p_node)\n";
		cvvn += "\t{\n";
		
		// consistency check
		cvvn += "\t\t// consistency check\n";
		cvvn += "\t\tassert(p_node != null);\n\n";
		
		// process quoted type unions
		if (rtp.isQuotedTypeUnion()) {
			// create the result type
			cvvn += "\t\t// create the local result\n";
			cvvn += "\t\t"+cnm1+tnm+" l_result = new "+cnm1+tnm+"();\n\n";
			
			// iterate over the quoted type union
			cvvn += "\t\t// copy the enumeration value\n";
			for (String qnm: rtp.getQuotedTypes()) {
				cvvn += "\t\tif (p_node.is"+qnm+"()) l_result.setEnum("+cnm1+tnm+"Enum.E"+qnm+");\n";
			}
			
			// return the result
			cvvn += "\n\t\t// return the result\n";
			cvvn += "\t\tm_result = l_result;\n\n";
			
			// call the convert operation on the node
			cvvn += "\t\t// convert the node member variables\n";
			cvvn += "\t\tvisitNode(p_node);\n";							
		}
		
		// process type name unions
		if (rtp.isTypeNameUnion()) {
			// iterate over the type name union
			cvvn += "\t\t// dispatch to appropriate sub-class visitor\n";
			for (String qnm: rtp.getTypeNames()) {
				cvvn += "\t\tif (p_node instanceof I"+cnm2+qnm+")\n";
				cvvn += "\t\t\tvisit"+qnm+"((I"+cnm2+qnm+") p_node);\n";
			}
		}
		// the operation trailer
		cvvn += "\t}\n\n";
				
		// return the result
		return cvvn;
	}

}
