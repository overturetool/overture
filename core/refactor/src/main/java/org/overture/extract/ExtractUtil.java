package org.overture.extract;

import java.util.LinkedList;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
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
	
	public static boolean isInRange(ILexLocation loc, int from, int to){	
		return loc.getStartLine() >= from && loc.getStartLine() <= to;
	}
	
	public static boolean addToOperationToFromOperation(PStm stm, ABlockSimpleBlockStm node, 
			LinkedList<PStm> statements, AExplicitOperationDefinition extractedOp, int stmIndex){
		
		if(!toOpAdded && stm instanceof ACallStm){
			ACallStm newStm = (ACallStm) stm.clone();
			newStm.setLocation(new LexLocation());
			newStm.setType(extractedOp.getType());
			newStm.setRootdef(extractedOp);
			newStm.setName(extractedOp.getName().clone());
			newStm.getArgs().clear();
			node.getStatements().set(stmIndex, newStm);
			toOpAdded = true;	
			return false;
		}
		return true;
	}		
}
