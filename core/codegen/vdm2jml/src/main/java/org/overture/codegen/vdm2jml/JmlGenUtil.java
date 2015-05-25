package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGen;

public class JmlGenUtil
{
	private JavaCodeGen javaGen;

	public JmlGenUtil(JavaCodeGen javaGen)
	{
		this.javaGen = javaGen;
	}

	public AIdentifierVarExpCG getNamedTypeInvParamVar(AMethodDeclCG invMethod)
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

	public AMethodDeclCG getMethod(ATypeDeclCG typeDecl)
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
}
