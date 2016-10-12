package org.overture.signature;

import java.util.LinkedList;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;

public class SignatureChangeObject {

	public LinkedList<PExp> paramList;
	public ILexLocation location;
	public ILexNameToken newParamName;
	public String parentName;
	public String paramType;
	
	public SignatureChangeObject(ILexLocation loc, ILexNameToken newParamName, LinkedList<PExp> paramList, 
			String parentName, String paramType){
		this.location = loc;
		this.paramList = paramList;
		this.newParamName = newParamName;
		this.parentName = parentName;
		this.paramType = paramType;	
	}
}
