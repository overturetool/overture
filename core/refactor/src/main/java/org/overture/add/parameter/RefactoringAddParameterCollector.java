package org.overture.add.parameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class RefactoringAddParameterCollector extends DepthFirstAnalysisAdaptor {

	private PDefinition enclosingDef;
	private Set<AddParameterRefactoring> addParameterRefactorings;
	private String[] parameters;
	private int startLine;
	private String paramType;
	private String paramName;
	private String paramPlaceholder;
	private boolean isParamListEmpty = true;
	
	public RefactoringAddParameterCollector(ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.enclosingDef = null;
		this.addParameterRefactorings = new HashSet<AddParameterRefactoring>();
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
			visitModuleDefs(node.getDefs(), node);
		}
	}

	@Override
	public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node) throws AnalysisException {

		if(compareNodeLocation(node.getLocation())){

			LexNameToken parName = new LexNameToken(node.getName().getModule(), paramName, null);
			//Add parameter to node patterns
			LinkedList<PPattern> patterns = node.getParameterPatterns();
			AIdentifierPattern idPattern = new AIdentifierPattern();
			LexLocation newLastLoc = new LexLocation();
			isParamListEmpty = patterns.isEmpty();
			PDefinition newParam;
			
			if(!isParamListEmpty){
				PPattern lastPattern = patterns.getLast();
				ILexLocation lastLoc = lastPattern.getLocation();
				newLastLoc = AddParameterUtil.calculateNewParamLocationWhenNotEmptyList(lastLoc, parName.toString());
			} else{
				newLastLoc = AddParameterUtil.calculateParamLocationWhenEmptyList(node.getType().getLocation(), parName.toString());
			}
			
			//Set patterns
			idPattern.setLocation(newLastLoc);
			idPattern.setName(parName);
			patterns.add(idPattern);
			
			//Set paramName
			newParam = new ALocalDefinition();
			newParam.setName(parName);
			
			//Get expression object
			AddParameterExpObject expObj = AddParameterUtil.getParamExpObj(paramType, paramPlaceholder, newLastLoc);
			newParam.setType(expObj.getType());
			
			//Add parameter to node definitions
			node.getParamDefinitions().add(newParam);
			
			//Add parameter to node operation type
			PType operationType = node.getType();
			if(operationType instanceof AOperationType){
				((AOperationType) operationType).getParameters().add(expObj.getType());
			}
			addParameterRefactorings.add(new AddParameterRefactoring(newLastLoc, parName.toString(), node.getName().getName(), newParam.getType().toString()));
			
			//Update calls
			signatureChangeCallOccurences(node.getLocation(), node.parent(), this::registerSignatureChange);
			//Update applications
			signatureChangeApplicationOccurences(node.getLocation(), node.parent(), this::registerSignatureChange);
		}		
	}
	
	private Set<AApplyExp> signatureChangeApplicationOccurences(ILexLocation defLoc, INode defScope, 
			Consumer<AddParameterObject> function) throws AnalysisException{
		ApplyOccurrenceSignatureChanger collector = new ApplyOccurrenceSignatureChanger(defLoc, function, paramName, paramPlaceholder, paramType, isParamListEmpty);
		defScope.apply(collector);
		return collector.getApplications();		
	}

	private Set<ACallStm> signatureChangeCallOccurences(ILexLocation defLoc, INode defScope,
			Consumer<AddParameterObject> function) throws AnalysisException{
		CallOccurrenceSignatureChanger collector = new CallOccurrenceSignatureChanger(defLoc, function, paramName, paramPlaceholder, paramType, isParamListEmpty);
		defScope.apply(collector);
		return collector.getCalls();
	}

	private void registerSignatureChange(AddParameterObject reObj){
		if (!contains(reObj.location))
		{
			addParameterRefactorings.add(new AddParameterRefactoring(reObj.location, reObj.newParamName.getName(), reObj.parentName, 
					reObj.paramType));	
			reObj.paramList.add(AddParameterUtil.getParamExpObj(paramType, paramPlaceholder, reObj.location).getExpression());
		}
	}
	
	private boolean contains(ILexLocation loc)
	{
		for (AddParameterRefactoring r : addParameterRefactorings)
		{
			if (r.getLoc().equals(loc))
			{
				return true;
			}
		}
		return false;
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

	public void init(boolean clearSignatureChanges)
	{
		this.enclosingDef = null;

		if (addParameterRefactorings != null && clearSignatureChanges)
		{
			addParameterRefactorings.clear();
		}
	}

	public Set<AddParameterRefactoring> getSignatureChanges()
	{
		return addParameterRefactorings;
	}
	
	private boolean compareNodeLocation(ILexLocation newNode){
		if(parameters.length >= 4 && newNode.getStartLine() == startLine){
			return true;
		}
		return false;
	}
	
	public void setRefactoringParameters(String[] parameters) {
		if(parameters.length >= 4){
			this.parameters = parameters;
			this.startLine = Integer.parseInt(parameters[0]);
			this.paramName = parameters[1];
			this.paramType = parameters[2];
			this.paramPlaceholder = parameters[3];
		}
	}
}
