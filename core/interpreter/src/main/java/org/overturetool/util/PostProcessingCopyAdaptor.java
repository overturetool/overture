package org.overturetool.util;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.interpreter.ast.definitions.AClassClassDefinitionInterpreter;
import org.overture.interpreter.ast.node.CopyAdaptorInterpreter;
import org.overture.interpreter.ast.node.NodeInterpreter;
import org.overturetool.vdmj.util.Delegate;

public class PostProcessingCopyAdaptor extends CopyAdaptorInterpreter
{
	@Override
	public NodeInterpreter caseAClassClassDefinition(AClassClassDefinition node)
	{
		AClassClassDefinitionInterpreter result = (AClassClassDefinitionInterpreter) super.caseAClassClassDefinition(node);

		result.setDelegate(new Delegate(result.getName().name, result.getDefinitions()));

		return result;
	}
}
