package org.overture.rename;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;

public class RenameUtil {
	
	public static boolean compareNodeLocation(ILexLocation newNode, String[] parameters){
		
		if(parameters.length >= 3){
			if(newNode.getStartLine() == Integer.parseInt(parameters[0]) &&
					(newNode.getStartPos() <= Integer.parseInt(parameters[1]) && 
					newNode.getEndPos() >= Integer.parseInt(parameters[1]))){
				return true;
			}
		}
		return false;
	}
	
	private static ApplyAndCallDetector applyAndCallDetector;
	
	public static ILexLocation GetApplyAndCallLocationParent(INode node,String[] parameters) throws AnalysisException{
		
		if(applyAndCallDetector == null){
			applyAndCallDetector = new ApplyAndCallDetector(parameters);
			node.parent().apply(applyAndCallDetector);
		}
		
		return applyAndCallDetector.getFoundParentLocation();
	}
	
	public static void init(){
		applyAndCallDetector = null;
	}
	
}
