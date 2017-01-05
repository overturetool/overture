package org.overture.add.parameter;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class AddParameterUtil {
	public static LexLocation calculateParamLocationFromOldLocation(ILexLocation oldLoc, String paramStr, boolean fromStartPos, int nrOfCharsToMove){
		LexLocation cloneOldLoc = oldLoc.clone();
		if(fromStartPos){
			LexLocation loc = new LexLocation(
					cloneOldLoc.getFile(),
					cloneOldLoc.getModule(),
					cloneOldLoc.getStartLine(),
					cloneOldLoc.getStartPos()+nrOfCharsToMove,
					cloneOldLoc.getEndLine(),
					cloneOldLoc.getStartPos()+nrOfCharsToMove+String.valueOf(paramStr).length(),
					cloneOldLoc.getStartOffset(), 
					cloneOldLoc.getEndOffset());
			return loc;
		}else{
			LexLocation loc = new LexLocation(
					cloneOldLoc.getFile(),
					cloneOldLoc.getModule(),
					cloneOldLoc.getStartLine(),
					cloneOldLoc.getEndPos()+nrOfCharsToMove,
					cloneOldLoc.getEndLine(),
					cloneOldLoc.getEndPos()+nrOfCharsToMove+String.valueOf(paramStr).length(),
					cloneOldLoc.getStartOffset(), 
					cloneOldLoc.getEndOffset());
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

	public static String createOperationModel(String paramType, String paramName, String paramPlaceholder, LexLocation newLastLoc) {
		StringBuilder sb = new StringBuilder();
		sb.append("operations\n\n");
		sb.append("op: " + paramType + " ==> ()\n");
		sb.append("op(" + paramName + ") == skip;\n\n");
		sb.append("op1: () ==> ()\n");
		sb.append("op1() == op(" + paramPlaceholder + ");");
		
		String fin = sb.toString();
		return fin;
	}

	public static AddParameterExpObject createParamObj(AModuleModules ast) {
		AddParameterExpObject expObj = new AddParameterExpObject();
		LinkedList<PDefinition> defs = ast.getDefs();
		
		//Get and set the type
		PDefinition firstOp = defs.get(0); 
		if(firstOp instanceof AExplicitOperationDefinition){
			expObj.setType(((AExplicitOperationDefinition) firstOp).getParamDefinitions().getFirst().getType());
		}
		
		//Get and set the expression
		PDefinition secondOp = defs.get(1);
		if(secondOp instanceof AExplicitOperationDefinition){
			ACallStm callStm = (ACallStm) ((AExplicitOperationDefinition) secondOp).getBody();
			expObj.setExpression(callStm.getArgs().getFirst());
		}
		
		return expObj;
	}
}
