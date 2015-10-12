package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.util.NameGen;

public class JmlGenUtil
{
	private JmlGenerator jmlGen;

	public JmlGenUtil(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}

	public AIdentifierVarExpCG getInvParamVar(AMethodDeclCG invMethod)
	{
		AFormalParamLocalParamCG formalParam = getInvFormalParam(invMethod);

		if (formalParam == null)
		{
			return null;
		}

		String paramName = getName(formalParam.getPattern());
		
		if(paramName == null)
		{
			return null;
		}
		
		STypeCG paramType = formalParam.getType().clone();

		return jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(paramName, paramType);
	}

	public String getName(SPatternCG id)
	{
		if (!(id instanceof AIdentifierPatternCG))
		{
			Logger.getLog().printErrorln("Expected identifier pattern "
					+ "to be an identifier pattern at this point. Got: "
					+ id
					+ " in '" + this.getClass().getSimpleName() + "'");
			return null;
		}

		return ((AIdentifierPatternCG) id).getName();
	}
	
	public AFormalParamLocalParamCG getInvFormalParam(AMethodDeclCG invMethod)
	{
		if (invMethod.getFormalParams().size() == 1)
		{
			return invMethod.getFormalParams().get(0);
		} else
		{
			Logger.getLog().printErrorln("Expected only a single formal parameter "
					+ "for named invariant type method "
					+ invMethod.getName()
					+ " but got "
					+ invMethod.getFormalParams().size()
					+ " in '" + this.getClass().getSimpleName() + "'");

			if (!invMethod.getFormalParams().isEmpty())
			{
				return invMethod.getFormalParams().get(0);
			} else
			{
				return null;
			}
		}
	}

	public AMethodDeclCG getInvMethod(ATypeDeclCG typeDecl)
	{
		if (typeDecl.getInv() instanceof AMethodDeclCG)
		{
			return (AMethodDeclCG) typeDecl.getInv();
		} else if (typeDecl.getInv() != null)
		{
			Logger.getLog().printErrorln("Expected named type invariant function "
					+ "to be a method declaration at this point. Got: "
					+ typeDecl.getDecl()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
		}

		return null;
	}
	
	public List<AMethodDeclCG> getNamedTypeInvMethods(ADefaultClassDeclCG clazz)
	{
		List<AMethodDeclCG> invDecls = new LinkedList<AMethodDeclCG>();

		for (ATypeDeclCG typeDecl : clazz.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ANamedTypeDeclCG)
			{
				AMethodDeclCG m = getInvMethod(typeDecl);
				
				if(m != null)
				{
					invDecls.add(m);
				}
			}
		}

		return invDecls;
	}
	
	public List<String> getRecFieldNames(ARecordDeclCG r)
	{
		List<String> args = new LinkedList<String>();
		
		for(AFieldDeclCG f : r.getFields())
		{
			args.add(f.getName());
		}
		return args;
	}
	
	public List<ARecordDeclCG> getRecords(List<IRStatus<INode>> ast)
	{
		List<ARecordDeclCG> records = new LinkedList<ARecordDeclCG>();

		for (IRStatus<ADefaultClassDeclCG> classStatus : IRStatus.extract(ast, ADefaultClassDeclCG.class))
		{
			for (ATypeDeclCG typeDecl : classStatus.getIrNode().getTypeDecls())
			{
				if (typeDecl.getDecl() instanceof ARecordDeclCG)
				{
					records.add((ARecordDeclCG) typeDecl.getDecl());
				}
			}
		}

		return records;
	}
	
	public String getMethodCondArgName(SPatternCG pattern)
	{
		// By now all patterns should be identifier patterns
		if (pattern instanceof AIdentifierPatternCG)
		{
			String paramName = ((AIdentifierPatternCG) pattern).getName();

			if (jmlGen.getJavaGen().getInfo().getExpAssistant().isOld(paramName))
			{
				paramName = toJmlOldExp(paramName);
			} else if (jmlGen.getJavaGen().getInfo().getExpAssistant().isResult(paramName))
			{
				// The type checker prohibits use of 'RESULT' as name of a user specified identifier
				paramName = JmlGenerator.JML_RESULT;
			}

			return paramName;

		} else
		{
			Logger.getLog().printErrorln("Expected formal parameter pattern to be an indentifier pattern. Got: "
					+ pattern + " in '" + this.getClass().getSimpleName() + "'");

			return "UNKNOWN";
		}
	}
	
	public String toJmlOldExp(String paramName)
	{
		// Convert old name to current name (e.g. _St to St)
		String currentArg = jmlGen.getJavaGen().getInfo().getExpAssistant().oldNameToCurrentName(paramName);

		// Note that invoking the copy method on the state should be okay
		// because the state should never be a null pointer
		currentArg += ".copy()";

		// Convert current name to JML old expression (e.g. \old(St)
		return String.format("%s(%s)", JmlGenerator.JML_OLD_PREFIX, currentArg);
	}
	
	/**
	 * This change to the IR is really only to circumvent a bug in OpenJML with loading of inner classes. The only thing
	 * that the Java code generator uses inner classes for is records. If this problem gets fixed the workaround
	 * introduced by this method should be removed.
	 * 
	 * @param ast
	 *            The IR passed by the Java code generator after the transformation process
	 * @param recInfo
	 *            Since the new record classes are deep copies we need to update the record info too
	 */
	public List<IRStatus<INode>> makeRecsOuterClasses(List<IRStatus<INode>> ast, RecClassInfo recInfo)
	{
		List<IRStatus<INode>> extraClasses = new LinkedList<IRStatus<INode>>();

		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(ast, ADefaultClassDeclCG.class))
		{
			ADefaultClassDeclCG clazz = status.getIrNode();

			List<ARecordDeclCG> recDecls = new LinkedList<ARecordDeclCG>();

			for (ATypeDeclCG d : clazz.getTypeDecls())
			{
				if (d.getDecl() instanceof ARecordDeclCG)
				{
					recDecls.add((ARecordDeclCG) d.getDecl());
				}
			}

			// Note that we do not remove the type declarations (or records) from the class.

			// For each of the records we will make a top-level class
			for (ARecordDeclCG recDecl : recDecls)
			{
				ADefaultClassDeclCG recClass = new ADefaultClassDeclCG();
				
				recInfo.registerRecClass(recClass);

				recClass.setMetaData(recDecl.getMetaData());
				recClass.setAbstract(false);
				recClass.setAccess(IRConstants.PUBLIC);
				recClass.setSourceNode(recDecl.getSourceNode());
				recClass.setStatic(false);
				recClass.setName(recDecl.getName());
				
				if(recDecl.getInvariant() != null)
				{
					recClass.setInvariant(recDecl.getInvariant().clone());
				}
				
				AInterfaceDeclCG recInterface = new AInterfaceDeclCG();
				recInterface.setPackage("org.overture.codegen.runtime");
				
				final String RECORD_NAME = "Record";
				recInterface.setName(RECORD_NAME);
				AExternalTypeCG recInterfaceType = new AExternalTypeCG();
				recInterfaceType.setName(RECORD_NAME);
				AMethodTypeCG copyMethodType = new AMethodTypeCG();
				copyMethodType.setResult(recInterfaceType);
				
				AMethodDeclCG copyMethod = jmlGen.getJavaGen().getJavaFormat().
						getRecCreator().consCopySignature(copyMethodType);
				copyMethod.setAbstract(true);
				
				recInterface.getMethodSignatures().add(copyMethod);
				
				recClass.getInterfaces().add(recInterface);

				// Copy the record methods to the class
				List<AMethodDeclCG> methods = new LinkedList<AMethodDeclCG>();
				for (AMethodDeclCG m : recDecl.getMethods())
				{
					AMethodDeclCG newMethod = m.clone(); 
					methods.add(newMethod);
					recInfo.updateAccessor(m, newMethod);
				}
				recClass.setMethods(methods);

				// Copy the record fields to the class
				List<AFieldDeclCG> fields = new LinkedList<AFieldDeclCG>();
				for (AFieldDeclCG f : recDecl.getFields())
				{
					AFieldDeclCG newField = f.clone();
					fields.add(newField);
					recInfo.register(newField);
				}
				recClass.setFields(fields);

				// Put the record classes of module M in package <userpackage>.<modulename>types
				// Examples: my.pack.Mtypes
				if (JavaCodeGenUtil.isValidJavaPackage(jmlGen.getJavaSettings().getJavaRootPackage()))
				{
					String recPackage = consRecPackage(clazz.getName(), jmlGen.getJavaSettings().getJavaRootPackage());
					recClass.setPackage(recPackage);
				} else
				{
					recClass.setPackage(clazz.getName() + JavaFormat.TYPE_DECL_PACKAGE_SUFFIX);
				}

				extraClasses.add(new IRStatus<INode>(recClass.getName(), recClass, new HashSet<VdmNodeInfo>()));
			}
		}

		return extraClasses;
	}

	public static String consRecPackage(String defClass, String javaRootPackage)
	{
		String recPackage = "";

		if (JavaCodeGenUtil.isValidJavaPackage(javaRootPackage))
		{
			recPackage += javaRootPackage + ".";
		}

		recPackage += defClass + JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;

		return recPackage;
	}
	
	public AMethodDeclCG genInvMethod(ADefaultClassDeclCG clazz,
			ANamedTypeDeclCG namedTypeDecl)
	{
		AReturnStmCG body = new AReturnStmCG();
		body.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(true));
		
		STypeCG paramType = namedTypeDecl.getType();
		
		AMethodTypeCG invMethodType = new AMethodTypeCG();
		invMethodType.setResult(new ABoolBasicTypeCG());
		invMethodType.getParams().add(paramType.clone());
		
		String formalParamName = new NameGen(clazz).getName(JmlGenerator.GEN_INV_METHOD_PARAM_NAME);
		
		AFormalParamLocalParamCG formalParam = new AFormalParamLocalParamCG();
		formalParam.setType(paramType.clone());
		formalParam.setPattern(jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(formalParamName));
		
		AMethodDeclCG method = new AMethodDeclCG();
		method.setImplicit(false);
		method.setAbstract(false);
		method.setAccess(IRConstants.PUBLIC);
		method.setAsync(false);
		method.setBody(body);
		method.getFormalParams().add(formalParam);
		method.setIsConstructor(false);
		method.setMethodType(invMethodType);
		method.setName("inv_" + namedTypeDecl.getName());
		method.setStatic(true);
		
		return method;
	}
	
	public AIdentifierPatternCG consInvParamReplacementId(ADefaultClassDeclCG encClass, String originalParamName)
	{
		NameGen nameGen = new NameGen(encClass);
		nameGen.addName(originalParamName);
		
		String newParamName = nameGen.getName(JmlGenerator.INV_METHOD_REPLACEMENT_NAME_PREFIX
				+ originalParamName);
		
		return jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(newParamName);
	}
	
	public ADefaultClassDeclCG getEnclosingClass(INode node)
	{
		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

		if (enclosingClass != null)
		{
			return enclosingClass;
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing class of node "
					+ node + " in '" + this.getClass().getSimpleName() + "'");
			return null;
		}
	}
	
	public AMethodDeclCG getEnclosingMethod(INode node)
	{
		AMethodDeclCG enclosingMethod = node.getAncestor(AMethodDeclCG.class);

		if (enclosingMethod != null)
		{
			return enclosingMethod;
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing method of node "
					+ node + " in " + this.getClass().getSimpleName());

			return null;
		}
	}
	
	/**
	 * There are problems with OpenJML when you invoke named type invariant
	 * methods across classes. Until these bugs are fixed the workaround is simply
	 * to make sure that all generated classes have a local copy of a named invariant method.
	 * 
	 * TODO: Currently invariant method are named on the form <module>_<typename> although
	 * this does not truly garuantee uniqueness. For example if module A defines type
	 * B_C the invariant method name is A_B_C. However if module A_B defines type C
	 * then the invariant method will also be named A_B_C. So something needs to be
	 * done about this.
	 * 
	 * @param newAst
	 */
	public void distributeNamedTypeInvs(List<IRStatus<INode>> newAst)
	{
		// Collect all named type invariants
		List<ATypeDeclCG> allNamedTypeInvTypeDecls = new LinkedList<ATypeDeclCG>();
		for(IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(newAst, ADefaultClassDeclCG.class))
		{
			ADefaultClassDeclCG clazz = status.getIrNode();
		
			if(jmlGen.getJavaGen().getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}
			
			for(ATypeDeclCG typeDecl : clazz.getTypeDecls())
			{
				if(typeDecl.getDecl() instanceof ANamedTypeDeclCG)
				{
					allNamedTypeInvTypeDecls.add(typeDecl);
				}
			}
		}
		
		for(IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(newAst, ADefaultClassDeclCG.class))
		{
			ADefaultClassDeclCG clazz = status.getIrNode();
			
			if(jmlGen.getJavaGen().getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}
			
			List<ATypeDeclCG> classTypeDecls = new LinkedList<ATypeDeclCG>(clazz.getTypeDecls());
			
			for(ATypeDeclCG namedTypeInv : allNamedTypeInvTypeDecls)
			{
				if(!classTypeDecls.contains(namedTypeInv))
				{
					classTypeDecls.add(namedTypeInv.clone());
				}
			}
			
			clazz.setTypeDecls(classTypeDecls);
		}
	}
}
