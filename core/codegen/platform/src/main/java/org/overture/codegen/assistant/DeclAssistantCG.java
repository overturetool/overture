/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.assistant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AThreadDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANotImplementedExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.LexNameTokenWrapper;

public class DeclAssistantCG extends AssistantBase
{
	public DeclAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}
	
	public boolean isInnerClass(ADefaultClassDeclCG node)
	{
		return node.parent() != null && node.parent().getAncestor(ADefaultClassDeclCG.class) != null;
	}
	
	public <T extends SClassDeclCG> T buildClass(SClassDefinition node, IRInfo question, T classCg) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		boolean isAbstract = node.getIsAbstract();
		boolean isStatic = false;
		LinkedList<ILexNameToken> superNames = node.getSupernames();

		classCg.setPackage(null);
		classCg.setName(name);
		classCg.setAccess(access);
		classCg.setAbstract(isAbstract);
		classCg.setStatic(isStatic);
		classCg.setStatic(false);

		if (superNames.size() >= 1)
		{
			classCg.setSuperName(superNames.get(0).getName());
		}

		LinkedList<PDefinition> defs = node.getDefinitions();
		
		for (PDefinition def : defs)
		{
			SDeclCG decl = def.apply(question.getDeclVisitor(), question);

			if (decl == null)
			{
				continue;// Unspported stuff returns null by default
			}
			if (decl instanceof AFieldDeclCG)
			{
				classCg.getFields().add((AFieldDeclCG) decl);
			} else if (decl instanceof AMethodDeclCG)
			{

				AMethodDeclCG method = (AMethodDeclCG) decl;

				if (method.getIsConstructor())
				{
					String initName = question.getObjectInitializerCall((AExplicitOperationDefinition) def);

					AMethodDeclCG objInitializer = method.clone();
					objInitializer.setTag(new IRGeneratedTag(getClass().getName()));
					objInitializer.setName(initName);
					objInitializer.getMethodType().setResult(new AVoidTypeCG());
					objInitializer.setIsConstructor(false);
					objInitializer.setPreCond(null);
					objInitializer.setPostCond(null);
					
					classCg.getMethods().add(objInitializer);

					APlainCallStmCG initCall = new APlainCallStmCG();
					initCall.setType(objInitializer.getMethodType().getResult().clone());
					initCall.setClassType(null);
					initCall.setName(initName);

					for (AFormalParamLocalParamCG param : method.getFormalParams())
					{
						SPatternCG pattern = param.getPattern();

						if (pattern instanceof AIdentifierPatternCG)
						{
							AIdentifierPatternCG idPattern = (AIdentifierPatternCG) pattern;

							AIdentifierVarExpCG var = new AIdentifierVarExpCG();
							var.setIsLocal(true);
							var.setType(param.getType().clone());
							var.setName(idPattern.getName());
							var.setIsLambda(false);
							var.setSourceNode(pattern.getSourceNode());

							initCall.getArgs().add(var);
						}
					}

					method.setBody(initCall);
				}

				classCg.getMethods().add(method);
			} else if (decl instanceof ATypeDeclCG)
			{
				classCg.getTypeDecls().add((ATypeDeclCG) decl);
			} else if (decl instanceof AFuncDeclCG)
			{
				classCg.getFunctions().add((AFuncDeclCG) decl);
			} else if (decl instanceof AThreadDeclCG)
			{
				if (question.getSettings().generateConc())
				{
					classCg.setThread((AThreadDeclCG) decl);
				}
			}
			else if (decl instanceof APersyncDeclCG)
			{
				classCg.getPerSyncs().add((APersyncDeclCG) decl);
			}
			else if (decl instanceof AMutexSyncDeclCG)
			{
				classCg.getMutexSyncs().add((AMutexSyncDeclCG) decl);
			}
			else if(decl instanceof ANamedTraceDeclCG)
			{
				classCg.getTraces().add((ANamedTraceDeclCG) decl);
			}
			else
			{
				Logger.getLog().printErrorln("Unexpected definition in class: "
						+ name + ": " + def.getName().getName() + " at " + def.getLocation());
			}
		}
		
		if(node.getInvariant() != null && question.getSettings().generateInvariants())
		{
			SDeclCG invCg = node.getInvariant().apply(question.getDeclVisitor(), question);
			classCg.setInvariant(invCg);
		}

		boolean defaultConstructorExplicit = false;
		for (AMethodDeclCG method : classCg.getMethods())
		{
			if (method.getIsConstructor() && method.getFormalParams().isEmpty())
			{
				defaultConstructorExplicit = true;
				break;
			}
		}

		if (!defaultConstructorExplicit)
		{
			classCg.getMethods().add(question.getDeclAssistant().consDefaultContructor(name));
		}
		
		question.addClass(classCg);

		return classCg;
	}
	
	public AMethodDeclCG funcToMethod(AFuncDeclCG node)
	{
		SDeclCG preCond = node.getPreCond();
		SDeclCG postCond = node.getPostCond();
		String access = node.getAccess();
		Boolean isAbstract = node.getAbstract();
		LinkedList<ATemplateTypeCG> templateTypes = node.getTemplateTypes();
		AMethodTypeCG methodType = node.getMethodType();
		LinkedList<AFormalParamLocalParamCG> formalParams = node.getFormalParams();
		String name = node.getName();
		SExpCG body = node.getBody();
		SourceNode sourceNode = node.getSourceNode();

		AMethodDeclCG method = new AMethodDeclCG();
		method.setSourceNode(sourceNode);

		if (preCond != null) {
			method.setPreCond(preCond.clone());
		}
		if (postCond != null) {
			method.setPostCond(postCond.clone());
		}

		method.setAccess(access);
		method.setAbstract(isAbstract);
		method.setTemplateTypes(cloneNodes(templateTypes, ATemplateTypeCG.class));
		method.setMethodType(methodType.clone());
		method.setFormalParams(cloneNodes(formalParams, AFormalParamLocalParamCG.class));
		method.setName(name);
		method.setStatic(true);
		method.setIsConstructor(false);
		method.setImplicit(node.getImplicit());

		if (!(body instanceof ANotImplementedExpCG))
		{
			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(body.clone());
			method.setBody(returnStm);
		} else
		{
			method.setBody(new ANotImplementedStmCG());
		}
		return method;
	}
	
	public String getNodeName(INode node)
	{
		if(node instanceof SClassDefinition)
		{
			return ((SClassDefinition) node).getName().getName();
		}
		else if(node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getName().getName();
		}
		
		// Fall back
		return node.toString();
	}

	public boolean isLibrary(INode node) {
		if (node instanceof SClassDefinition) {
			return isLibraryName(((SClassDefinition) node).getName().getName());
		} else if (node instanceof AModuleModules) {
			return isLibraryName(((AModuleModules) node).getName().getName());
		}

		return false;
	}

	public boolean isLibraryName(String className)
	{
		for (int i = 0; i < IRConstants.CLASS_NAMES_USED_IN_VDM.length; i++)
		{
			if (IRConstants.CLASS_NAMES_USED_IN_VDM[i].equals(className))
			{
				return true;
			}
		}

		return false;
	}

	public <T extends SDeclCG> List<T> getAllDecls(SClassDeclCG classDecl,
			List<SClassDeclCG> classes, DeclStrategy<T> strategy)
	{
		List<T> allDecls = new LinkedList<T>();

		allDecls.addAll(strategy.getDecls(classDecl));

		String superName = classDecl.getSuperName();

		while (superName != null)
		{
			SClassDeclCG superClassDecl = findClass(classes, superName);
			
			if(superClassDecl == null)
			{
				//This would be the case if the super class
				// declaration is the VDMThread from the runtime
				break;
			}

			for (T superDecl : strategy.getDecls(superClassDecl))
			{
				if (isInherited(strategy.getAccess(superDecl)))
				{
					allDecls.add(superDecl);
				}
			}

			superName = superClassDecl.getSuperName();
		}

		return allDecls;
	}

	public List<AMethodDeclCG> getAllMethods(SClassDeclCG classDecl,
			List<SClassDeclCG> classes)
	{
		DeclStrategy<AMethodDeclCG> methodDeclStrategy = new DeclStrategy<AMethodDeclCG>()
		{
			@Override
			public String getAccess(AMethodDeclCG decl)
			{
				return decl.getAccess();
			}

			@Override
			public List<AMethodDeclCG> getDecls(SClassDeclCG classDecl)
			{
				return classDecl.getMethods();
			}
		};

		return getAllDecls(classDecl, classes, methodDeclStrategy);
	}

	public List<AFieldDeclCG> getAllFields(SClassDeclCG classDecl,
			List<SClassDeclCG> classes)
	{
		DeclStrategy<AFieldDeclCG> fieldDeclStrategy = new DeclStrategy<AFieldDeclCG>()
		{
			@Override
			public String getAccess(AFieldDeclCG decl)
			{
				return decl.getAccess();
			}

			@Override
			public List<AFieldDeclCG> getDecls(SClassDeclCG classDecl)
			{
				return classDecl.getFields();
			}
		};

		return getAllDecls(classDecl, classes, fieldDeclStrategy);
	}

	public boolean isInherited(String access)
	{
		return access.equals(IRConstants.PROTECTED)
				|| access.equals(IRConstants.PUBLIC);
	}

	public void setLocalDefs(List<PDefinition> localDefs,
			List<AVarDeclCG> localDecls, IRInfo question)
			throws AnalysisException
	{
		for (PDefinition def : localDefs)
		{
			if (def instanceof AValueDefinition)
			{
				localDecls.add(consLocalVarDecl((AValueDefinition) def, question));
			} else if (def instanceof AEqualsDefinition)
			{
				localDecls.add(consLocalVarDecl((AEqualsDefinition) def, question));
			}
		}
	}

	public SClassDeclCG findClass(List<SClassDeclCG> classes, String moduleName)
	{
		for (SClassDeclCG classDecl : classes)
		{
			if (classDecl.getName().equals(moduleName))
			{
				return classDecl;
			}
		}

		return null;
	}

	// This method assumes that the record is defined in definingClass and not a super class
	public ARecordDeclCG findRecord(SClassDeclCG definingClass,
			String recordName)
	{
		for (ATypeDeclCG typeDecl : definingClass.getTypeDecls())
		{
			SDeclCG decl = typeDecl.getDecl();
			
			if(!(decl instanceof ARecordDeclCG))
			{
				continue;
			}
			
			ARecordDeclCG recordDecl = (ARecordDeclCG) decl;
			
			if (recordDecl.getName().equals(recordName))
			{
				return recordDecl;
			}
		}

		return null;
	}
	
	// This method assumes that the record is defined in definingClass and not a super class
	public List<ARecordDeclCG> getRecords(SClassDeclCG definingClass)
	{
		List<ARecordDeclCG> records = new LinkedList<ARecordDeclCG>();
		
		for (ATypeDeclCG typeDecl : definingClass.getTypeDecls())
		{
			SDeclCG decl = typeDecl.getDecl();
			
			if(!(decl instanceof ARecordDeclCG))
			{
				continue;
			}
			
			ARecordDeclCG recordDecl = (ARecordDeclCG) decl;
			
			records.add(recordDecl);
		}
		
		return records;
	}

	public ARecordDeclCG findRecord(List<SClassDeclCG> classes,
			ARecordTypeCG recordType)
	{
		SClassDeclCG definingClass = findClass(classes, recordType.getName().getDefiningClass());
		ARecordDeclCG record = findRecord(definingClass, recordType.getName().getName());

		return record;
	}

	private AVarDeclCG consLocalVarDecl(AValueDefinition valueDef,
			IRInfo question) throws AnalysisException
	{
		STypeCG type = valueDef.getType().apply(question.getTypeVisitor(), question);
		SPatternCG pattern = valueDef.getPattern().apply(question.getPatternVisitor(), question);
		SExpCG exp = valueDef.getExpression().apply(question.getExpVisitor(), question);

		return consLocalVarDecl(valueDef, type, pattern, exp);

	}

	private AVarDeclCG consLocalVarDecl(AEqualsDefinition equalsDef,
			IRInfo question) throws AnalysisException
	{
		STypeCG type = equalsDef.getExpType().apply(question.getTypeVisitor(), question);
		SPatternCG pattern = equalsDef.getPattern().apply(question.getPatternVisitor(), question);
		SExpCG exp = equalsDef.getTest().apply(question.getExpVisitor(), question);

		return consLocalVarDecl(equalsDef, type, pattern, exp);

	}

	public AVarDeclCG consLocalVarDecl(STypeCG type,
			SPatternCG pattern, SExpCG exp)
	{
		return consLocalVarDecl(null, type, pattern, exp);
	}

	public AVarDeclCG consLocalVarDecl(INode node, STypeCG type,
			SPatternCG pattern, SExpCG exp)
	{
		AVarDeclCG localVarDecl = new AVarDeclCG();
		localVarDecl.setType(type);
		localVarDecl.setFinal(false);
		localVarDecl.setSourceNode(new SourceNode(node));
		localVarDecl.setPattern(pattern);
		localVarDecl.setExp(exp);
		
		return localVarDecl;
	}

	public AFieldDeclCG constructField(String access, String name,
			boolean isStatic, boolean isFinal, STypeCG type, SExpCG exp)
	{

		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(access);
		field.setName(name);
		field.setVolatile(false);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);

		return field;
	}

	public Set<ILexNameToken> getOverloadedMethodNames(
			AClassClassDefinition classDef)
	{
		List<LexNameTokenWrapper> methodNames = getMethodNames(classDef);
		Set<LexNameTokenWrapper> duplicates = findDuplicates(methodNames);

		Set<ILexNameToken> overloadedMethodNames = new HashSet<ILexNameToken>();

		for (LexNameTokenWrapper wrapper : methodNames)
		{
			if (duplicates.contains(wrapper))
			{
				overloadedMethodNames.add(wrapper.getName());
			}
		}

		return overloadedMethodNames;
	}

	private Set<LexNameTokenWrapper> findDuplicates(
			List<LexNameTokenWrapper> nameWrappers)
	{
		Set<LexNameTokenWrapper> duplicates = new HashSet<LexNameTokenWrapper>();
		Set<LexNameTokenWrapper> temp = new HashSet<LexNameTokenWrapper>();

		for (LexNameTokenWrapper wrapper : nameWrappers)
		{
			if (!temp.add(wrapper))
			{
				duplicates.add(wrapper);
			}
		}

		return duplicates;
	}

	private List<LexNameTokenWrapper> getMethodNames(
			AClassClassDefinition classDef)
	{
		List<LexNameTokenWrapper> methodNames = new LinkedList<LexNameTokenWrapper>();

		List<PDefinition> allDefs = new LinkedList<PDefinition>();

		LinkedList<PDefinition> defs = classDef.getDefinitions();
		LinkedList<PDefinition> inheritedDefs = classDef.getAllInheritedDefinitions();

		allDefs.addAll(defs);
		allDefs.addAll(inheritedDefs);

		for (PDefinition def : allDefs)
		{
			if (def instanceof SOperationDefinition
					|| def instanceof SFunctionDefinition)
			{
				methodNames.add(new LexNameTokenWrapper(def.getName()));
			}
		}

		return methodNames;
	}

	public void setDefaultValue(AVarDeclCG localDecl, STypeCG typeCg)
	{
		ExpAssistantCG expAssistant = assistantManager.getExpAssistant();

		if (typeCg instanceof AStringTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultStringlValue());
		} else if (typeCg instanceof ACharBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultCharlValue());
		} else if (typeCg instanceof AIntNumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultIntValue());
		} else if(typeCg instanceof ANat1NumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultNat1Value());
		} else if(typeCg instanceof ANatNumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultNatValue());
		}
		else if (typeCg instanceof ARealNumericBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultRealValue());
		} else if (typeCg instanceof ABoolBasicTypeCG)
		{
			localDecl.setExp(expAssistant.getDefaultBoolValue());
		} else
		{
			localDecl.setExp(assistantManager.getExpAssistant().consNullExp());
		}
	}

	public AFieldDeclCG getFieldDecl(List<SClassDeclCG> classes,
			ARecordTypeCG recordType, int number)
	{
		ARecordDeclCG record = findRecord(classes, recordType);

		return record.getFields().get(number);
	}
	
	public AFieldDeclCG getFieldDecl(SClassDeclCG clazz, String fieldName)
	{
		for(AFieldDeclCG field : clazz.getFields())
		{
			if(field.getName().equals(fieldName))
			{
				return field;
			}
		}
		
		return null;
	}

	public AFieldDeclCG getFieldDecl(List<SClassDeclCG> classes,
			ARecordTypeCG recordType, String memberName)
	{
		ATypeNameCG name = recordType.getName();

		if (name == null)
		{
			throw new IllegalArgumentException("Could not find type name for record type: "
					+ recordType);
		}

		String definingClassName = name.getDefiningClass();

		if (definingClassName == null)
		{
			throw new IllegalArgumentException("Could not find defining class for record type: "
					+ recordType);
		}

		String recName = name.getName();

		if (recName == null)
		{
			throw new IllegalArgumentException("Could not find record name for record type: "
					+ recordType);
		}

		SClassDeclCG definingClass = null;
		for (SClassDeclCG currentClass : classes)
		{
			if (currentClass.getName().equals(definingClassName))
			{
				definingClass = currentClass;
				break;
			}
		}

		if (definingClass == null)
		{
			throw new IllegalArgumentException("Could not find defining class with name: "
					+ definingClassName);
		}

		List<ARecordDeclCG> records = getRecords(definingClass);

		ARecordDeclCG recordDecl = null;
		for (ARecordDeclCG currentRec : records)
		{
			if (currentRec.getName().equals(recName))
			{
				recordDecl = currentRec;
				break;
			}
		}

		if (recordDecl == null)
		{
			throw new IllegalArgumentException("Could not find record with name '"
					+ recName + "' in class '" + definingClassName + "'");
		}

		List<AFieldDeclCG> fields = recordDecl.getFields();

		AFieldDeclCG field = null;
		for (AFieldDeclCG currentField : fields)
		{
			if (currentField.getName().equals(memberName))
			{
				field = currentField;
			}
		}
		
		return field;
	}
	
	public AMethodDeclCG initMethod(SOperationDefinition node, IRInfo question) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = question.getTcFactory().createPDefinitionAssistant().isStatic(node);
		boolean isAsync = question.getTcFactory().createPAccessSpecifierAssistant().isAsync(node.getAccess());
		String operationName = node.getName().getName();
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		if (!(type instanceof AMethodTypeCG))
		{
			return null;
		}

		AMethodTypeCG methodType = (AMethodTypeCG) type;

		SStmCG bodyCg = null; 
		if (node.getBody() != null)
		{
			bodyCg = node.getBody().apply(question.getStmVisitor(), question);
		}
		
		boolean isConstructor = node.getIsConstructor();
		boolean isAbstract = node.getBody() instanceof ASubclassResponsibilityStm;

		AMethodDeclCG method = new AMethodDeclCG();

		method.setImplicit(false);
		method.setAccess(access);
		method.setStatic(isStatic);
		method.setAsync(isAsync);
		method.setMethodType(methodType);
		method.setName(operationName);
		method.setBody(bodyCg);
		method.setIsConstructor(isConstructor);
		method.setAbstract(isAbstract);
		method.setImplicit(node instanceof AImplicitOperationDefinition);
		
		AExplicitFunctionDefinition preCond = node.getPredef();
		SDeclCG preCondCg = preCond != null ? preCond.apply(question.getDeclVisitor(), question) : null;
		method.setPreCond(preCondCg);
		
		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclCG postCondCg = postCond != null ? postCond.apply(question.getDeclVisitor(), question) : null;
		method.setPostCond(postCondCg);
		
		return method;
	}
	

	public List<AFormalParamLocalParamCG> consFormalParams(
			List<APatternListTypePair> params, IRInfo question)
			throws AnalysisException
	{
		List<AFormalParamLocalParamCG> paramsCg = new LinkedList<>();
		for(APatternListTypePair patternListPair : params)
		{
			STypeCG pairTypeCg = patternListPair.getType().apply(question.getTypeVisitor(), question);
			
			for(PPattern p : patternListPair.getPatterns())
			{
				SPatternCG patternCg = p.apply(question.getPatternVisitor(), question);
				
				AFormalParamLocalParamCG paramCg = new AFormalParamLocalParamCG();
				paramCg.setPattern(patternCg);
				paramCg.setType(pairTypeCg.clone());
				
				paramsCg.add(paramCg);
			}
		}
		return paramsCg;
	}
	
	public boolean isLocal(AIdentifierStateDesignator id, IRInfo info)
	{
		PDefinition idDef = info.getIdStateDesignatorDefs().get(id);
		
		if(idDef == null)
		{
			Logger.getLog().printErrorln("Could not find definition for identifier "
					+ "state designator " + id + " in '" + this.getClass().getSimpleName() + "'");
			return false;
		}
		else
		{
			return idDef instanceof AAssignmentDefinition;
		}
	}
	
	public boolean inFunc(INode node)
	{
		if(node.getAncestor(SFunctionDefinition.class) != null)
		{
			return true;
		}
	
		// If a node appears in a pre or post condition of an operation then the pre/post
		// expression might be directly linked to the operation. Therefore the above ancestor
		// check will not work.
		
		return appearsInPreOrPosOfEnclosingOp(node);
	}
	
	public boolean appearsInPreOrPosOfEnclosingFunc(INode node)
	{
		SFunctionDefinition encFunc = node.getAncestor(SFunctionDefinition.class);
		
		if(encFunc == null)
		{
			return false;
		}
		
		PExp preCond = encFunc.getPrecondition();
		PExp postCond = encFunc.getPostcondition();
		
		return appearsInPreOrPostExp(node, preCond, postCond);
	}

	public boolean appearsInPreOrPosOfEnclosingOp(INode node)
	{
		SOperationDefinition encOp = node.getAncestor(SOperationDefinition.class);
		
		if(encOp == null)
		{
			return false;
		}
		
		PExp preCond = encOp.getPrecondition();
		PExp postCond = encOp.getPostcondition();
		
		return appearsInPreOrPostExp(node, preCond, postCond);
	}

	private boolean appearsInPreOrPostExp(INode node, PExp preCond, PExp postCond)
	{
		INode next = node;
		Set<INode> visited = new HashSet<INode>();
		
		while (next.parent() != null
				&& !(next.parent() instanceof SOperationDefinition)
				&& !visited.contains(next))
		{
			visited.add(next);
			next = next.parent();
		}

		// If we are in a pre or post condition then 'next' should point to the
		// pre or post expression of the operation

		return preCond == next || postCond == next;
	}
	
	public AMethodDeclCG consDefaultContructor(String name)
	{
		AMethodDeclCG constructor = new AMethodDeclCG();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(name);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(classType);

		constructor.setMethodType(methodType);
		constructor.setAccess(IRConstants.PUBLIC);
		constructor.setAbstract(false);
		constructor.setIsConstructor(true);
		constructor.setName(name);
		constructor.setImplicit(false);
		constructor.setBody(new ABlockStmCG());
		
		return constructor;
	}
}
