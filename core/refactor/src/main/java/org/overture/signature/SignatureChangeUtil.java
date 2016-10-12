package org.overture.signature;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;

public class SignatureChangeUtil {
	public static LexLocation calculateParamLocationFromOldLocation(ILexLocation oldLoc, String paramStr, boolean fromStartPos, int nrOfCharsToMove){
		if(fromStartPos){
			LexLocation loc = new LexLocation(
					oldLoc.getFile(),
					oldLoc.getModule(),
					oldLoc.getStartLine(),
					oldLoc.getStartPos()+nrOfCharsToMove,
					oldLoc.getEndLine(),
					oldLoc.getStartPos()+nrOfCharsToMove+String.valueOf(paramStr).length(),
					oldLoc.getStartOffset(), 
					oldLoc.getEndOffset());
			return loc;
		}else{
			LexLocation loc = new LexLocation(
					oldLoc.getFile(),
					oldLoc.getModule(),
					oldLoc.getStartLine(),
					oldLoc.getEndPos()+nrOfCharsToMove,
					oldLoc.getEndLine(),
					oldLoc.getEndPos()+nrOfCharsToMove+String.valueOf(paramStr).length(),
					oldLoc.getStartOffset(), 
					oldLoc.getEndOffset());
			return loc;
		}
	}
	
	public static LexLocation calculateNewParamLocationWhenNotEmptyList(ILexLocation oldLoc, String paramStr){
		return calculateParamLocationFromOldLocation(oldLoc,paramStr,false,2);
	}
	
	public static LexLocation calculateParamLocationWhenEmptyList(ILexLocation oldLoc, String paramStr){
		return calculateParamLocationFromOldLocation(oldLoc,paramStr,true,-3);
	}
	
	public static LexLocation calculateParamLocationInCallWhenEmptyList(ILexLocation oldLoc, String paramStr){
		return calculateParamLocationFromOldLocation(oldLoc,paramStr,true,2);
	}
}
