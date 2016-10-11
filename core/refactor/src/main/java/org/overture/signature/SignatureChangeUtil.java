package org.overture.signature;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;

public class SignatureChangeUtil {
	
	public static LexLocation CalculateNewLastParamLocation(ILexLocation oldLoc, String paramStr){
		LexLocation loc = new LexLocation(
				oldLoc.getFile(),
				oldLoc.getModule(),
				oldLoc.getStartLine(),
				oldLoc.getEndPos()+2,oldLoc.getEndLine(),
				oldLoc.getEndPos()+2+String.valueOf(paramStr).length(),
				oldLoc.getStartOffset(), 
				oldLoc.getEndOffset());
		return loc;		
	}
}
