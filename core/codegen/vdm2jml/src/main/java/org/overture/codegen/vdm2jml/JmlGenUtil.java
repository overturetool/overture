package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGen;

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
}
