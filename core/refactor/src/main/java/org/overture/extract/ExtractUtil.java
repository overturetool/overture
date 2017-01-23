package org.overture.extract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;

public class ExtractUtil {

	private static boolean toOpAdded = false;
	public static void init(){
		toOpAdded = false;
	}
	
	public static void removeFromStatements(PStm stm, LinkedList<PStm> statements){	
		for(int i = 0; i < statements.size(); i++){
			PStm item = statements.get(i);
			if(item.getLocation().getStartLine() == stm.getLocation().getStartLine()){		
				statements.remove(i);
				break;
			}
		}
	}
	
	public static void removeFromAssignmentDefs(AAssignmentDefinition assignment, LinkedList<AAssignmentDefinition> assignmentDefs) {
		for(int i = 0; i < assignmentDefs.size(); i++){
			AAssignmentDefinition item = assignmentDefs.get(i);
			if(item.getLocation().getStartLine() == assignment.getLocation().getStartLine()){		
				assignmentDefs.remove(i);
				break;
			}
		}
	}	
	
	public static boolean isInRange(ILexLocation loc, int from, int to){	
		return loc.getStartLine() >= from && loc.getStartLine() <= to;
	}
	
	public static void setParametersForOperation(AExplicitOperationDefinition node, List<PDefinition> pDefs){
		
		List<AIdentifierPattern> idPatterns = new ArrayList<AIdentifierPattern>();
		
		for(PDefinition item : pDefs){
			idPatterns.add(AstFactory.newAIdentifierPattern(item.getName().clone()));		
		}
		node.getParamDefinitions().clear();
		node.getParameterPatterns().clear();
		
		node.setParamDefinitions(pDefs);
		node.setParameterPatterns(idPatterns);
	}
	
	public static boolean addToOperationToFromOperation(PStm stm, ABlockSimpleBlockStm node, 
			LinkedList<PStm> statements, AExplicitOperationDefinition extractedOp, int stmIndex, List<PDefinition> pDefs){
		
		if(!toOpAdded){
			
			List<PExp> parameterExp = new ArrayList<PExp>();
			
			for(PDefinition item : pDefs){	
				parameterExp.add(AstFactory.newAVariableExp(item.getName()));
			}
			
			ACallStm newStm = AstFactory.newACallStm(extractedOp.getName().clone(), parameterExp);
			newStm.setLocation(new LexLocation());
			newStm.setType(extractedOp.getType());
			node.getStatements().set(stmIndex, newStm);
			toOpAdded = true;	
			return false;
		}
		return true;
	}

	
}
