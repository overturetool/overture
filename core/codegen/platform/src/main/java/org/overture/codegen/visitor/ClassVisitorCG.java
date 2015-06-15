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
package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.declarations.AThreadDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class ClassVisitorCG extends AbstractVisitorCG<IRInfo, AClassDeclCG>
{
	public ClassVisitorCG()
	{
	}

	@Override
	public AClassDeclCG caseAClassClassDefinition(AClassClassDefinition node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		boolean isAbstract = node.getIsAbstract();
		boolean isStatic = false;
		LinkedList<ILexNameToken> superNames = node.getSupernames();

		AClassDeclCG classCg = new AClassDeclCG();
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

			classCg.getMethods().add(constructor);
		}

		return classCg;
	}
}
