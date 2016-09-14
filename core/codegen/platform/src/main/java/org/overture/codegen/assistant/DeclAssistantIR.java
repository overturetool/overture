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
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AMutexSyncDeclIR;
import org.overture.codegen.ir.declarations.ANamedTraceDeclIR;
import org.overture.codegen.ir.declarations.APersyncDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AThreadDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.ANotImplementedExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ANotImplementedStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.utils.LexNameTokenWrapper;

public class DeclAssistantIR extends AssistantBase
{
	public DeclAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void addDependencies(SClassDeclIR clazz,
			List<ClonableString> extraDeps, boolean prepend)
	{
		NodeAssistantIR nodeAssistant = assistantManager.getNodeAssistant();
		clazz.setDependencies(nodeAssistant.buildData(clazz.getDependencies(), extraDeps, prepend));
	}

	public boolean isInnerClass(SClassDeclIR node)
	{
		return node.parent() != null
				&& node.parent().getAncestor(SClassDeclIR.class) != null;
	}

	public boolean isTestCase(INode node)
	{
		if (!(node instanceof SClassDefinition))
		{
			return false;
		}

		SClassDefinition clazz = (SClassDefinition) node;

		for (SClassDefinition d : clazz.getSuperDefs())
		{
			if (d.getName().getName().equals(IRConstants.TEST_CASE))
			{
				return true;
			}

			if (isTestCase(d))
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean isTest(ADefaultClassDeclIR node)
	{
		return node != null && !node.getSuperNames().isEmpty()
				&& node.getSuperNames().get(0).getName().equals(IRConstants.TEST_CASE);
	}

	public <T extends SClassDeclIR> T buildClass(SClassDefinition node,
			IRInfo question, T classCg) throws AnalysisException
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

		for (ILexNameToken s : superNames)
		{
			ATokenNameIR superName = new ATokenNameIR();
			superName.setName(s.getName());

			classCg.getSuperNames().add(superName);
		}

		LinkedList<PDefinition> defs = node.getDefinitions();

		for (PDefinition def : defs)
		{
			SDeclIR decl = def.apply(question.getDeclVisitor(), question);

			if (decl == null)
			{
				continue;// Unspported stuff returns null by default
			}
			if (decl instanceof AFieldDeclIR)
			{
				classCg.getFields().add((AFieldDeclIR) decl);
			} else if (decl instanceof AMethodDeclIR)
			{
				classCg.getMethods().add((AMethodDeclIR) decl);
			} else if (decl instanceof ATypeDeclIR)
			{
				classCg.getTypeDecls().add((ATypeDeclIR) decl);
			} else if (decl instanceof AFuncDeclIR)
			{
				classCg.getFunctions().add((AFuncDeclIR) decl);
			} else if (decl instanceof AThreadDeclIR)
			{
				if (question.getSettings().generateConc())
				{
					classCg.setThread((AThreadDeclIR) decl);
				}
			} else if (decl instanceof APersyncDeclIR)
			{
				classCg.getPerSyncs().add((APersyncDeclIR) decl);
			} else if (decl instanceof AMutexSyncDeclIR)
			{
				classCg.getMutexSyncs().add((AMutexSyncDeclIR) decl);
			} else if (decl instanceof ANamedTraceDeclIR)
			{
				classCg.getTraces().add((ANamedTraceDeclIR) decl);
			} else
			{
				log.error("Unexpected definition in class: " + name + ": "
						+ def.getName().getName() + " at " + def.getLocation());
			}
		}

		if (node.getInvariant() != null
				&& question.getSettings().generateInvariants())
		{
			SDeclIR invCg = node.getInvariant().apply(question.getDeclVisitor(), question);
			classCg.setInvariant(invCg);
		}

		boolean defaultConstructorExplicit = false;
		for (AMethodDeclIR method : classCg.getMethods())
		{
			if (method.getIsConstructor() && method.getFormalParams().isEmpty())
			{
				defaultConstructorExplicit = true;
				break;
			}
		}

		if (!defaultConstructorExplicit)
		{
			AMethodDeclIR defaultContructor = question.getDeclAssistant().consDefaultContructor(name);
			defaultContructor.setSourceNode(new SourceNode(node));
			classCg.getMethods().add(defaultContructor);
		}

		question.addClass(classCg);

		return classCg;
	}

	public AMethodDeclIR funcToMethod(AFuncDeclIR node)
	{
		SDeclIR preCond = node.getPreCond();
		SDeclIR postCond = node.getPostCond();
		String access = node.getAccess();
		Boolean isAbstract = node.getAbstract();
		LinkedList<ATemplateTypeIR> templateTypes = node.getTemplateTypes();
		AMethodTypeIR methodType = node.getMethodType();
		LinkedList<AFormalParamLocalParamIR> formalParams = node.getFormalParams();
		String name = node.getName();
		SExpIR body = node.getBody();
		SourceNode sourceNode = node.getSourceNode();

		AMethodDeclIR method = new AMethodDeclIR();
		method.setSourceNode(sourceNode);

		if (preCond != null)
		{
			method.setPreCond(preCond.clone());
		}
		if (postCond != null)
		{
			method.setPostCond(postCond.clone());
		}

		method.setAccess(access);
		method.setAbstract(isAbstract);
		method.setTemplateTypes(cloneNodes(templateTypes, ATemplateTypeIR.class));
		method.setMethodType(methodType.clone());
		method.setFormalParams(cloneNodes(formalParams, AFormalParamLocalParamIR.class));
		method.setName(name);
		method.setStatic(true);
		method.setIsConstructor(false);
		method.setImplicit(node.getImplicit());

		if (!(body instanceof ANotImplementedExpIR))
		{
			AReturnStmIR returnStm = new AReturnStmIR();
			// Approximate return statement source node as the function body
			returnStm.setSourceNode(body.getSourceNode());
			returnStm.setExp(body.clone());
			method.setBody(returnStm);
		} else
		{
			method.setBody(new ANotImplementedStmIR());
		}
		return method;
	}

	public String getNodeName(INode node)
	{
		if (node instanceof SClassDefinition)
		{
			return ((SClassDefinition) node).getName().getName();
		} else if (node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getName().getName();
		}

		// Fall back
		return node.toString();
	}

	public boolean isLibrary(INode node)
	{
		if (node instanceof SClassDefinition)
		{
			return isLibraryName(((SClassDefinition) node).getName().getName());
		} else if (node instanceof AModuleModules)
		{
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

	public <T extends SDeclIR> List<T> getAllDecls(SClassDeclIR classDecl,
			List<SClassDeclIR> classes, DeclStrategy<T> strategy)
	{
		List<T> allDecls = new LinkedList<T>();

		allDecls.addAll(strategy.getDecls(classDecl));

		Set<String> allSuperNames = getSuperClasses(classDecl, classes);

		for (String s : allSuperNames)
		{
			SClassDeclIR superClassDecl = findClass(classes, s);

			if (superClassDecl != null)
			{
				for (T superDecl : strategy.getDecls(superClassDecl))
				{
					if (isInherited(strategy.getAccess(superDecl)))
					{
						allDecls.add(superDecl);
					}
				}
			}
		}

		return allDecls;
	}

	public Set<String> getSuperClasses(SClassDeclIR classDecl,
			List<SClassDeclIR> classes)
	{
		if (classDecl.getSuperNames().isEmpty())
		{
			return new HashSet<>();
		} else
		{
			Set<String> superClasses = new HashSet<>();

			for (ATokenNameIR s : classDecl.getSuperNames())
			{
				superClasses.add(s.getName());

				SClassDeclIR clazz = findClass(classes, s.getName());

				if (clazz != null)
				{
					superClasses.addAll(getSuperClasses(clazz, classes));
				}
			}

			return superClasses;
		}
	}

	public List<AMethodDeclIR> getAllMethods(SClassDeclIR classDecl,
			List<SClassDeclIR> classes)
	{
		DeclStrategy<AMethodDeclIR> methodDeclStrategy = new DeclStrategy<AMethodDeclIR>()
		{
			@Override
			public String getAccess(AMethodDeclIR decl)
			{
				return decl.getAccess();
			}

			@Override
			public List<AMethodDeclIR> getDecls(SClassDeclIR classDecl)
			{
				return classDecl.getMethods();
			}
		};

		return getAllDecls(classDecl, classes, methodDeclStrategy);
	}

	public List<AFieldDeclIR> getAllFields(SClassDeclIR classDecl,
			List<SClassDeclIR> classes)
	{
		DeclStrategy<AFieldDeclIR> fieldDeclStrategy = new DeclStrategy<AFieldDeclIR>()
		{
			@Override
			public String getAccess(AFieldDeclIR decl)
			{
				return decl.getAccess();
			}

			@Override
			public List<AFieldDeclIR> getDecls(SClassDeclIR classDecl)
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

	public void setFinalLocalDefs(List<PDefinition> localDefs,
			List<AVarDeclIR> localDecls, IRInfo question)
			throws AnalysisException
	{
		for (PDefinition def : localDefs)
		{
			AVarDeclIR varDecl = null;

			if (def instanceof AValueDefinition)
			{
				varDecl = consLocalVarDecl((AValueDefinition) def, question);
			} else if (def instanceof AEqualsDefinition)
			{
				varDecl = consLocalVarDecl((AEqualsDefinition) def, question);
			}

			if (varDecl != null)
			{
				varDecl.setFinal(true);
				localDecls.add(varDecl);
			} else
			{
				log.error("Problems encountered when trying to construct local variable");
			}
		}
	}

	public SClassDeclIR findClass(List<SClassDeclIR> classes, String moduleName)
	{
		for (SClassDeclIR classDecl : classes)
		{
			if (classDecl.getName().equals(moduleName))
			{
				return classDecl;
			}
		}

		return null;
	}

	// This method assumes that the record is defined in definingClass and not a super class
	public ARecordDeclIR findRecord(SClassDeclIR definingClass,
			String recordName)
	{
		for (ATypeDeclIR typeDecl : definingClass.getTypeDecls())
		{
			SDeclIR decl = typeDecl.getDecl();

			if (!(decl instanceof ARecordDeclIR))
			{
				continue;
			}

			ARecordDeclIR recordDecl = (ARecordDeclIR) decl;

			if (recordDecl.getName().equals(recordName))
			{
				return recordDecl;
			}
		}

		return null;
	}

	// This method assumes that the record is defined in definingClass and not a super class
	public List<ARecordDeclIR> getRecords(SClassDeclIR definingClass)
	{
		List<ARecordDeclIR> records = new LinkedList<ARecordDeclIR>();

		for (ATypeDeclIR typeDecl : definingClass.getTypeDecls())
		{
			SDeclIR decl = typeDecl.getDecl();

			if (!(decl instanceof ARecordDeclIR))
			{
				continue;
			}

			ARecordDeclIR recordDecl = (ARecordDeclIR) decl;

			records.add(recordDecl);
		}

		return records;
	}

	public ARecordDeclIR findRecord(List<SClassDeclIR> classes,
			ARecordTypeIR recordType)
	{
		SClassDeclIR definingClass = findClass(classes, recordType.getName().getDefiningClass());
		ARecordDeclIR record = findRecord(definingClass, recordType.getName().getName());

		return record;
	}

	private AVarDeclIR consLocalVarDecl(AValueDefinition valueDef,
			IRInfo question) throws AnalysisException
	{
		STypeIR type = valueDef.getType().apply(question.getTypeVisitor(), question);
		SPatternIR pattern = valueDef.getPattern().apply(question.getPatternVisitor(), question);
		SExpIR exp = valueDef.getExpression().apply(question.getExpVisitor(), question);

		return consLocalVarDecl(valueDef, type, pattern, exp);

	}

	private AVarDeclIR consLocalVarDecl(AEqualsDefinition equalsDef,
			IRInfo question) throws AnalysisException
	{
		STypeIR type = equalsDef.getExpType().apply(question.getTypeVisitor(), question);
		SPatternIR pattern = equalsDef.getPattern().apply(question.getPatternVisitor(), question);
		SExpIR exp = equalsDef.getTest().apply(question.getExpVisitor(), question);

		return consLocalVarDecl(equalsDef, type, pattern, exp);

	}

	public AVarDeclIR consLocalVarDecl(STypeIR type, SPatternIR pattern,
			SExpIR exp)
	{
		return consLocalVarDecl(exp.getSourceNode() != null
				? exp.getSourceNode().getVdmNode() : null, type, pattern, exp);
	}

	public AVarDeclIR consLocalVarDecl(INode node, STypeIR type,
			SPatternIR pattern, SExpIR exp)
	{
		AVarDeclIR localVarDecl = new AVarDeclIR();
		localVarDecl.setType(type);
		localVarDecl.setFinal(false);
		localVarDecl.setSourceNode(node != null ? new SourceNode(node) : null);
		localVarDecl.setPattern(pattern);
		localVarDecl.setExp(exp);

		return localVarDecl;
	}

	public AFieldDeclIR constructField(String access, String name,
			boolean isStatic, boolean isFinal, STypeIR type, SExpIR exp)
	{

		AFieldDeclIR field = new AFieldDeclIR();
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

	public void setDefaultValue(AVarDeclIR localDecl, STypeIR typeCg)
	{
		ExpAssistantIR expAssistant = assistantManager.getExpAssistant();

		if (typeCg instanceof AStringTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultStringlValue());
		} else if (typeCg instanceof ACharBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultCharlValue());
		} else if (typeCg instanceof AIntNumericBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultIntValue());
		} else if (typeCg instanceof ANat1NumericBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultNat1Value());
		} else if (typeCg instanceof ANatNumericBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultNatValue());
		} else if (typeCg instanceof ARealNumericBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultRealValue());
		} else if (typeCg instanceof ABoolBasicTypeIR)
		{
			localDecl.setExp(expAssistant.getDefaultBoolValue());
		} else
		{
			localDecl.setExp(assistantManager.getExpAssistant().consUndefinedExp());
		}
	}

	public AFieldDeclIR getFieldDecl(List<SClassDeclIR> classes,
			ARecordTypeIR recordType, int number)
	{
		ARecordDeclIR record = findRecord(classes, recordType);

		return record.getFields().get(number);
	}

	public AFieldDeclIR getFieldDecl(SClassDeclIR clazz, String fieldName)
	{
		for (AFieldDeclIR field : clazz.getFields())
		{
			if (field.getName().equals(fieldName))
			{
				return field;
			}
		}

		return null;
	}

	public AFieldDeclIR getFieldDecl(List<SClassDeclIR> classes,
			ARecordTypeIR recordType, String memberName)
	{
		ATypeNameIR name = recordType.getName();

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

		SClassDeclIR definingClass = null;
		for (SClassDeclIR currentClass : classes)
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

		List<ARecordDeclIR> records = getRecords(definingClass);

		ARecordDeclIR recordDecl = null;
		for (ARecordDeclIR currentRec : records)
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

		List<AFieldDeclIR> fields = recordDecl.getFields();

		AFieldDeclIR field = null;
		for (AFieldDeclIR currentField : fields)
		{
			if (currentField.getName().equals(memberName))
			{
				field = currentField;
			}
		}

		return field;
	}

	public AMethodDeclIR initMethod(SOperationDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = question.getTcFactory().createPDefinitionAssistant().isStatic(node);
		boolean isAsync = question.getTcFactory().createPAccessSpecifierAssistant().isAsync(node.getAccess());
		String operationName = node.getName().getName();
		STypeIR type = node.getType().apply(question.getTypeVisitor(), question);

		if (!(type instanceof AMethodTypeIR))
		{
			return null;
		}

		AMethodTypeIR methodType = (AMethodTypeIR) type;

		SStmIR bodyCg = null;
		if (node.getBody() != null)
		{
			bodyCg = node.getBody().apply(question.getStmVisitor(), question);
		}

		boolean isConstructor = node.getIsConstructor();
		boolean isAbstract = node.getBody() instanceof ASubclassResponsibilityStm;

		AMethodDeclIR method = new AMethodDeclIR();

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
		SDeclIR preCondCg = preCond != null
				? preCond.apply(question.getDeclVisitor(), question) : null;
		method.setPreCond(preCondCg);

		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclIR postCondCg = postCond != null
				? postCond.apply(question.getDeclVisitor(), question) : null;
		method.setPostCond(postCondCg);

		return method;
	}

	public List<AFormalParamLocalParamIR> consFormalParams(
			List<APatternListTypePair> params, IRInfo question)
			throws AnalysisException
	{
		List<AFormalParamLocalParamIR> paramsCg = new LinkedList<>();
		for (APatternListTypePair patternListPair : params)
		{
			STypeIR pairTypeCg = patternListPair.getType().apply(question.getTypeVisitor(), question);

			for (PPattern p : patternListPair.getPatterns())
			{
				SPatternIR patternCg = p.apply(question.getPatternVisitor(), question);

				AFormalParamLocalParamIR paramCg = new AFormalParamLocalParamIR();
				paramCg.setPattern(patternCg);
				paramCg.setType(pairTypeCg.clone());

				paramsCg.add(paramCg);
			}
		}
		return paramsCg;
	}

	/**
	 * Based on the definition table computed by {@link IRGenerator#computeDefTable(List)} this method determines
	 * whether a identifier state designator is local or not.
	 * 
	 * @param id
	 *            The identifier state designator
	 * @param info
	 *            The IR info
	 * @return True if <code>id</code> is local - false otherwise
	 */
	public boolean isLocal(AIdentifierStateDesignator id, IRInfo info)
	{
		PDefinition idDef = info.getIdStateDesignatorDefs().get(id);

		if (idDef == null)
		{
			log.error("Could not find definition for identifier state designator ");
			return false;
		} else
		{
			return idDef instanceof AAssignmentDefinition;
		}
	}

	public boolean inFunc(INode node)
	{
		if (node.getAncestor(SFunctionDefinition.class) != null)
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

		if (encFunc == null)
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

		if (encOp == null)
		{
			return false;
		}

		PExp preCond = encOp.getPrecondition();
		PExp postCond = encOp.getPostcondition();

		return appearsInPreOrPostExp(node, preCond, postCond);
	}

	private boolean appearsInPreOrPostExp(INode node, PExp preCond,
			PExp postCond)
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

	public AMethodDeclIR consDefaultContructor(String name)
	{
		AMethodDeclIR constructor = new AMethodDeclIR();

		AClassTypeIR classType = new AClassTypeIR();
		classType.setName(name);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(classType);

		constructor.setMethodType(methodType);
		constructor.setAccess(IRConstants.PUBLIC);
		constructor.setAbstract(false);
		constructor.setIsConstructor(true);
		constructor.setName(name);
		constructor.setImplicit(false);
		constructor.setBody(new ABlockStmIR());

		return constructor;
	}
}
