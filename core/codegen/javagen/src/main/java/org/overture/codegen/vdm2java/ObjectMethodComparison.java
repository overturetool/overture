package org.overture.codegen.vdm2java;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.analysis.violations.NamingComparison;
import org.overture.codegen.ir.IRInfo;

public class ObjectMethodComparison extends NamingComparison
{
	public ObjectMethodComparison(String[] names, IRInfo irInfo, String correctionPrefix)
	{
		super(names, irInfo, correctionPrefix);
	}

	@Override
	public boolean mustHandleNameToken(ILexNameToken nameToken)
	{
		if(nameToken.parent() instanceof SOperationDefinition)
		{
			// Rename operation definition
			return names.contains(nameToken.getName());
		}
		else if(nameToken.parent() instanceof SFunctionDefinition)
		{
			// Rename function definition
			return names.contains(nameToken.getName());
		}
		else if(nameToken.parent() instanceof ACallStm)
		{
			//Rename call statement, e.g. wait()
			return names.contains(nameToken.getName());
		}
		else if(nameToken.parent() instanceof ACallObjectStm){
			
			//Rename call object statement, e.g. e.wait()
			return names.contains(nameToken.getName());
		}
		else if(nameToken.parent() instanceof AFieldExp)
		{
			//Rename field expression, e.notify()
			return names.contains(nameToken.getName());
		}
		else if (nameToken.parent() instanceof AVariableExp)
		{
			AVariableExp var = (AVariableExp) nameToken.parent();

			PDefinition unfolded = var.getVardef();

			while (unfolded instanceof AInheritedDefinition)
			{
				unfolded = ((AInheritedDefinition) unfolded).getSuperdef();
			}
			
			if(unfolded instanceof SFunctionDefinition || unfolded instanceof SOperationDefinition)
			{
				if(names.contains(nameToken.getName()))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean mustHandleLexIdentifierToken(LexIdentifierToken lexId)
	{
		// Then 'lexId' must be the field 
		if(lexId.parent() instanceof ACallObjectStm)
		{
			return names.contains(lexId.getName());  
		}
		else if(lexId.parent() instanceof AFieldExp)
		{
			return names.contains(lexId.getName());
		}
	
		return false;
	}
}
