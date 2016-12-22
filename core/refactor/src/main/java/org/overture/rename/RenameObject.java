package org.overture.rename;

import java.util.function.Consumer;
import org.overture.ast.intf.lex.ILexNameToken;
public class RenameObject{

	public Consumer<ILexNameToken> function;
	public String newName;
	public ILexNameToken name;
	
	public RenameObject(ILexNameToken name, String newName, Consumer<ILexNameToken> f){
		this.function = f;
		this.newName = newName;
		this.name = name;			
	}
}