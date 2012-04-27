package org.overturetool.util;

import java.util.Hashtable;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.interpreter.ast.definitions.AClassClassDefinitionInterpreter;
import org.overture.interpreter.ast.node.CopyAdaptorInterpreter;
import org.overture.interpreter.ast.node.NodeInterpreter;
import org.overturetool.vdmj.util.Delegate;

public class PostProcessingCopyAdaptor extends CopyAdaptorInterpreter
{
	@SuppressWarnings("rawtypes")
	public PostProcessingCopyAdaptor()
	{
		super(new CopyFactory(),new Hashtable());
		

	}
	
	@Override
	public NodeInterpreter caseAClassClassDefinition(AClassClassDefinition node)
	{
		AClassClassDefinitionInterpreter result = (AClassClassDefinitionInterpreter) super.caseAClassClassDefinition(node);

		result.setDelegate(new Delegate(result.getName().name, result.getDefinitions()));

		return result;
	}
}
