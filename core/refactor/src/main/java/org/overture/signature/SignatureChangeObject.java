package org.overture.signature;

import java.util.function.Consumer;
import org.overture.ast.intf.lex.ILexNameToken;

public class SignatureChangeObject {

	public Consumer<ILexNameToken> function;
	public ILexNameToken name;
	
	public SignatureChangeObject(ILexNameToken name, Consumer<ILexNameToken> f){
		this.function = f;
		this.name = name;	
	}
}
