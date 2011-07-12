package org.overture.ast.types.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;


public class AUnresolvedTypeAssistant {

	public static PType typeResolve(AUnresolvedType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		PType deref = dereference(type,question.env, root);

		if (!(deref instanceof AClassType))
		{
			deref = PTypeAssistant.typeResolve(deref, root, rootVisitor, question);
		}

		return deref;
	}

	private static PType dereference(AUnresolvedType type, Environment env, ATypeDefinition root)
	{
		PDefinition def = env.findType(type.getTypename(), type.getLocation().module);

		if (def == null)
		{
			throw new TypeCheckException(
				"Unable to resolve type name '" + type.getTypename() + "'", type.getLocation());
		}

		if (def instanceof AImportedDefinition)
		{
			AImportedDefinition idef = (AImportedDefinition)def;
			def = idef.getDef();
		}

		if (def instanceof ARenamedDefinition)
		{
			ARenamedDefinition rdef = (ARenamedDefinition)def;
			def = rdef.getDef();
		}

		if (!(def instanceof ATypeDefinition) &&
			!(def instanceof AStateDefinition) &&
			!(def instanceof SClassDefinition) &&
			!(def instanceof AInheritedDefinition))
		{
			TypeCheckerErrors.report(3434, "'" + type.getTypename() + "' is not the name of a type definition",type.getLocation(),type);
		}

		if (def instanceof ATypeDefinition)
		{
			if (def == root)
			{
				root.setInfinite(true);
			}
		}

		if ((def instanceof ACpuClassDefinition ||
			 def instanceof ABusClassDefinition) && !env.isSystem())
		{
			TypeCheckerErrors.report(3296, "Cannot use '" + type.getTypename() + "' outside system class",type.getLocation(),type);
		}

		PType r = def.getType();
		List<PDefinition> tempDefs = new Vector<PDefinition>();
		tempDefs.add(def);
		r.setDefinitions(tempDefs);
		return r;
	}

	

}
