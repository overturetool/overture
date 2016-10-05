package org.overture.extract;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;

public class BodyOccurrenceCollector  extends DepthFirstAnalysisAdaptor{
	
	private AExplicitOperationDefinition fromOperation;
	private AExplicitOperationDefinition toOperation;
	private AModuleModules currentModule;
	private int from;
	private int to;
	private boolean toOpAdded = false;
	private String extractedOperationName;
	
	public BodyOccurrenceCollector(AExplicitOperationDefinition fromOp, AModuleModules currentModule, int from, int to, String extractedOperationName)
	{
		this.fromOperation = fromOp;
		this.toOperation = null;
		this.currentModule = currentModule;
		this.from = from;
		this.to = to;
		this.toOpAdded = false;
		this.extractedOperationName = extractedOperationName;
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException {
		
		if(!isInRange(node.getLocation())){
			fromOperation.getBody();
		}
		super.caseACallStm(node);
	}
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node) throws AnalysisException {
		
		LinkedList<PStm> fromStatements = new LinkedList<PStm>(node.getStatements());
		ABlockSimpleBlockStm toNodeOperation = null;
		for(PStm stm : fromStatements){
			
			if(isInRange(stm.getLocation())){
				
				if(toOperation == null){
					addToNodeCurrentModule(fromOperation);
				}
				
				if(addToOperationToFromOperation( stm, node, fromStatements)){
					removeFromStatements(stm, node.getStatements());
				}
				
			} else if(!isInRange(stm.getLocation())){
				
				if(toOperation == null){
					addToNodeCurrentModule(fromOperation);
				}
				addToOperationToFromOperation( stm, node, fromStatements);
				
				if(toNodeOperation == null){
					if(toOperation.getBody() instanceof ABlockSimpleBlockStm){
						toNodeOperation = (ABlockSimpleBlockStm) toOperation.getBody(); 
					}
				}
				if(toNodeOperation != null){
					removeFromStatements(stm, toNodeOperation.getStatements());					
				}
			}
		}
	}
	
	private void removeFromStatements(PStm stm, LinkedList<PStm> statements){	
		for(int i = 0; i < statements.size(); i++){
			PStm item = statements.get(i);
			if(item.getLocation().getStartLine() == stm.getLocation().getStartLine()){		
				statements.remove(i);
				break;
			}
		}
	}
	
	private boolean isInRange(ILexLocation loc){	
		return loc.getStartLine() >= from && loc.getStartLine() <= to;
	}
	
	public boolean addToOperationToFromOperation(PStm stm, ABlockSimpleBlockStm node, LinkedList<PStm> statements){
		
		if(!toOpAdded && stm instanceof ACallStm){
			ACallStm newStm = (ACallStm) stm.clone();
			newStm.setLocation(toOperation.getLocation());
			newStm.setType(toOperation.getType());
			newStm.setRootdef(toOperation);
			newStm.setName(toOperation.getName());
			node.getStatements().set(statements.indexOf(stm), newStm);
			toOpAdded = true;	
			return false;
		}
		return true;
	}
	
	public void addToNodeCurrentModule(AExplicitOperationDefinition node){
		System.out.println("Sized:" + currentModule.getDefs().size());
		
		toOperation = node.clone();
		LexNameToken token = new LexNameToken(toOperation.getName().getModule(), extractedOperationName, toOperation.getName().getLocation());
		toOperation.setName(token);
		toOperation.setLocation(new LexLocation());
		currentModule.getDefs().add(toOperation);
		
		System.out.println("Sizea:" + currentModule.getDefs().size());
	}
	
}
