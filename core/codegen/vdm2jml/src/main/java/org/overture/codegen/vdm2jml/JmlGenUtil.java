package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;

public class JmlGenUtil
{
	private JavaCodeGen javaGen;

	public JmlGenUtil(JavaCodeGen javaGen)
	{
		this.javaGen = javaGen;
	}

	public AIdentifierVarExpCG getInvParamVar(AMethodDeclCG invMethod)
	{
		if (invMethod.getFormalParams().size() == 1)
		{
			AFormalParamLocalParamCG param = invMethod.getFormalParams().get(0);
			SPatternCG id = param.getPattern();

			if (!(id instanceof AIdentifierPatternCG))
			{
				Logger.getLog().printErrorln("Expected identifier pattern of formal parameter "
						+ "to be an identifier pattern at this point. Got: "
						+ id + " in '" + this.getClass().getSimpleName() + "'");
				return null;
			}

			String paramName = ((AIdentifierPatternCG) id).getName();
			STypeCG paramType = param.getType().clone();
			
			return javaGen.getTransformationAssistant().consIdentifierVar(paramName, paramType);
		} else
		{
			Logger.getLog().printErrorln("Expected only a single formal parameter "
					+ "for named invariant type method "
					+ invMethod.getName()
					+ " but got "
					+ invMethod.getFormalParams().size()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
			return null;
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
	
	public List<AMethodDeclCG> getNamedTypeInvMethods(AClassDeclCG clazz)
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

		for (IRStatus<AClassDeclCG> classStatus : IRStatus.extract(ast, AClassDeclCG.class))
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

			if (this.javaGen.getInfo().getExpAssistant().isOld(paramName))
			{
				paramName = toJmlOldExp(paramName);
			} else if (this.javaGen.getInfo().getExpAssistant().isResult(paramName))
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
		String currentArg = this.javaGen.getInfo().getExpAssistant().oldNameToCurrentName(paramName);

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
	 */
	public List<IRStatus<INode>> makeRecsOuterClasses(List<IRStatus<INode>> ast)
	{
		List<IRStatus<INode>> extraClasses = new LinkedList<IRStatus<INode>>();

		for (IRStatus<AClassDeclCG> status : IRStatus.extract(ast, AClassDeclCG.class))
		{
			AClassDeclCG clazz = status.getIrNode();

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
				AClassDeclCG recClass = new AClassDeclCG();

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
				
				AMethodDeclCG copyMethod = javaGen.getJavaFormat().
						getRecCreator().consCopySignature(copyMethodType);
				copyMethod.setAbstract(true);
				
				recInterface.getMethodSignatures().add(copyMethod);
				
				recClass.getInterfaces().add(recInterface);

				// Copy the record methods to the class
				List<AMethodDeclCG> methods = new LinkedList<AMethodDeclCG>();
				for (AMethodDeclCG m : recDecl.getMethods())
				{
					methods.add(m.clone());
				}
				recClass.setMethods(methods);

				// Copy the record fields to the class
				List<AFieldDeclCG> fields = new LinkedList<AFieldDeclCG>();
				for (AFieldDeclCG f : recDecl.getFields())
				{
					fields.add(f.clone());
				}
				recClass.setFields(fields);

				// Put the record classes of module M in package <userpackage>.<modulename>types
				// Examples: my.pack.Mtypes
				if (JavaCodeGenUtil.isValidJavaPackage(javaGen.getJavaSettings().getJavaRootPackage()))
				{
					String recPackage = javaGen.getJavaSettings().getJavaRootPackage()
							+ "." + clazz.getName()
							+ JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;
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
}
