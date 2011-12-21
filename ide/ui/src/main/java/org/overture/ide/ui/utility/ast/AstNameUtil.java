package org.overture.ide.ui.utility.ast;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PType;

public class AstNameUtil
{
	public static String getName(INode node)
	{
		if(node instanceof PDefinition)
		{
			return ((PDefinition) node).getName().name;
		}else if(node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getName()==null?null:((AModuleModules) node).getName().name;
		}else if(node instanceof PStm)
		{
			return ((PStm) node).getLocation().module;
		}else if(node instanceof PExp)
		{
			return ((PExp) node).getLocation().module;
		}else if(node instanceof PType)
		{
			return ((PType) node).getLocation().module;
		}else if(node instanceof AFieldField)
		{
			return ((AFieldField) node).getTagname().name;
		}

		return "Unresolved Name";
	}
}
