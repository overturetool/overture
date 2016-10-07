package org.overture.signature;

import java.util.LinkedList;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;

public class SignatureChangeObject {

	public LinkedList<PExp> paramList;
	public ILexNameToken name;
	public String parentName;
	
	public SignatureChangeObject(ILexNameToken name, LinkedList<PExp> paramList, String parentName){
		this.paramList = paramList;
		this.name = name;
		this.parentName = parentName;	
	}
}
