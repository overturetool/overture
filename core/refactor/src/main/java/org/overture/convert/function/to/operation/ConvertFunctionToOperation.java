package org.overture.convert.function.to.operation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.CombinedDefaultModule;

public class ConvertFunctionToOperation  extends DepthFirstAnalysisAdaptor{
	
	private PDefinition enclosingDef;

	private AModuleModules currentModule;
	private boolean foundFunctionToConvert;
	private int line;
	
	public ConvertFunctionToOperation(int line)
	{
		this.enclosingDef = null;
		this.currentModule = null;
		this.foundFunctionToConvert = false;
		this.line = line;
	}
	
	@Override
	public void caseAModuleModules(AModuleModules node) throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}
		
		if(node instanceof CombinedDefaultModule)
		{
			for(AModuleModules m : ((CombinedDefaultModule) node).getModules())
			{
				m.apply(THIS);
			}
		}
		else 
		{
			currentModule = node;
			visitModuleDefs(node.getDefs(), node);
		}
	}
	
	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		handleExecutables(defs);
	}

	private void handleExecutables(List<PDefinition> defs)
			throws AnalysisException
	{
		for (PDefinition def : new LinkedList<>(defs))
		{
			if (def instanceof SOperationDefinition
					|| def instanceof SFunctionDefinition
					|| def instanceof ANamedTraceDefinition)
			{
				enclosingDef = def;
				def.apply(this);
			}
		}
	}
	
	@Override
	public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node) throws AnalysisException {
		
		if(isInRange(node.getLocation(), line) && !foundFunctionToConvert){
			foundFunctionToConvert = true;
			if(!checkIfUsedInFunction(node)){
			
				PStm convertedPStm = convertPExpToPStm(node.getBody());
				AExplicitFunctionDefinition nodeClone = node.clone();
				LexNameToken token = new LexNameToken(nodeClone.getName().getModule(), (nodeClone.getName().getName()), new LexLocation());
				
				List<PType> parameterTypes = new ArrayList<>();
				for(PDefinition item : nodeClone.getParamDefinitionList()){
					parameterTypes.add(item.getType());
				}
				
				AOperationType operationType = AstFactory.newAOperationType(new LexLocation(), parameterTypes, nodeClone.getExpectedResult());
				
				AExplicitOperationDefinition convertedOperation = AstFactory.newAExplicitOperationDefinition(token, operationType, nodeClone.getParamPatternList().getFirst(),nodeClone.getPrecondition(), nodeClone.getPostcondition(),convertedPStm);
		
				addToNodeCurrentModuleAndRemoveOld(convertedOperation, node);
				applyOccurrenceSwitcher(convertedOperation, node);
			}
		}
	}

	private boolean checkIfUsedInFunction(AExplicitFunctionDefinition node) throws AnalysisException {
		CheckIfFunctionUsedInFunction checker = new CheckIfFunctionUsedInFunction(node);
		currentModule.apply(checker);
		return checker.isFunctionUsedInFunction();
	}
	
	@Override
	public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node) throws AnalysisException {

		super.caseAExplicitOperationDefinition(node);
	}
	
	public PStm convertPExpToPStm(PExp pExp){
		
		if(pExp instanceof AIntLiteralExp){
			AIntLiteralExp aIntLiteralExp = (AIntLiteralExp) pExp;
			return AstFactory.newAReturnStm(pExp.getLocation(), aIntLiteralExp);
		}

		return null;
	}
	
	private void applyOccurrenceSwitcher(AExplicitOperationDefinition toOperation, AExplicitFunctionDefinition function) throws AnalysisException
	{
		FunctionOccurrenceSwitcher collector = new FunctionOccurrenceSwitcher(toOperation, function);
		currentModule.apply(collector);
	}
	
	public void addToNodeCurrentModuleAndRemoveOld(PDefinition newNode, PDefinition oldNode){
		System.out.println("Size1:" + currentModule.getDefs().size());
		currentModule.getDefs().remove(oldNode);
		System.out.println("Size2:" + currentModule.getDefs().size());
		currentModule.getDefs().add(newNode);
		System.out.println("Size3:" + currentModule.getDefs().size());
	}
	
	public static boolean isInRange(ILexLocation loc, int from){	
		return loc.getStartLine() == from;
	}
}
