package org.overture.ide.ui.outline;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.modules.Module;

public class DisplayNameCreator
{

	public static String getDisplayName(Object element)
	{
		StringBuilder sb = new StringBuilder();
		if(element instanceof ClassDefinition)
			return ((ClassDefinition)element).name.name;
		else if(element instanceof Module)
			return ((Module)element).name.name;
		else if(element instanceof Definition)
		{
			sb.append(((Definition)element).name.name);
			if(element instanceof ExplicitOperationDefinition)
			{
				ExplicitOperationDefinition def = (ExplicitOperationDefinition) element;
				sb.append("(");
				for (int i = 0; i < def.parameterPatterns.size(); i++)
				{
					if(def.paramDefinitions.get(i) instanceof LocalDefinition)
					sb.append(((LocalDefinition)def.paramDefinitions.get(i)).type+" "+ def.parameterPatterns.get(i));
					else
						sb.append(def.paramDefinitions.get(i).name.name+" "+ def.parameterPatterns.get(i));
					if(i+1< def.parameterPatterns.size())
						sb.append(", ");
				}
				sb.append(")");
			}
			
			
			
			
			
			
			

		}
		return sb.toString();
	}

}
