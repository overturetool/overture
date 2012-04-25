package org.overturetool.VDM2JavaCG.VDM2Java;

import java.io.File;
import java.nio.charset.Charset;
//import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
import java.util.Vector;

//import org.overture.ide.core.VdmCore;
import org.overturetool.vdmj.VDMPP;
//import org.overturetool.vdmj.ast.IAstNode;
//import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
//import org.overturetool.vdmj.lex.Dialect;
//import org.overturetool.vdmj.lex.LexLocation;
//import org.overturetool.vdmj.lex.LexTokenReader;
//import org.overturetool.vdmj.messages.InternalException;
//import org.overturetool.vdmj.messages.VDMError;
//import org.overturetool.vdmj.messages.VDMWarning;
//import org.overturetool.vdmj.syntax.ClassReader;
//import org.overturetool.vdmj.typechecker.*;

public class Parser extends VDMPP{
	
	public static String filecharset = Charset.defaultCharset().name();
	public static ClassList Classes = new ClassList();
	public Parser() {super();}

	public ClassList startParseFile(File file)
	{
		List<File> fls = new Vector<File>();
		fls.add(file);
		super.parse(fls);
		super.typeCheck();
		return super.classes;
		
	}	
}
