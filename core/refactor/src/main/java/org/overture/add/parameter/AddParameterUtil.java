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
	
	public static AddParameterExpObject getParamExpObj(String aParamType, String aParamPlaceholder, ILexLocation loc){
		AddParameterExpObject expObj = new AddParameterExpObject();
		
		switch(ParamType.valueOf(aParamType.toUpperCase())){
		case BOOL:
			ABooleanBasicType boolType = new ABooleanBasicType();
			expObj.setType(boolType);
			ABooleanConstExp boolExp = new ABooleanConstExp();
			LexBooleanToken boolToken = new LexBooleanToken(Boolean.parseBoolean(aParamPlaceholder), loc);
			boolExp.setType(boolType);
			boolExp.setValue(boolToken);
			boolExp.setLocation(loc);
			expObj.setExpression(boolExp);
			return expObj;
		case NAT:
			ANatNumericBasicType natType = new ANatNumericBasicType();
			expObj.setType(natType);
			LexIntegerToken natToken = new LexIntegerToken(Integer.parseInt(aParamPlaceholder), loc);
			AIntLiteralExp natExp = new AIntLiteralExp();
			natExp.setType(natType);
			natExp.setValue(natToken);
			natExp.setLocation(loc);
			expObj.setExpression(natExp);
			return expObj;
		case NAT1:
//			this.paramType = new ANatOneNumericBasicType();
//			this.paramPlaceholder = Integer.parseInt(aParamPlaceholder);
			return null;
		default:
			return null;
		}		
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
