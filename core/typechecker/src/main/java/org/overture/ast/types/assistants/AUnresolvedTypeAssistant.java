package org.overture.ast.types.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckException;
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

		return deref.clone();
	}

	private static PType dereference(AUnresolvedType type, Environment env, ATypeDefinition root)
	{
		PDefinition def = env.findType(type.getName(), type.getLocation().module);

		if (def == null)
		{
			throw new TypeCheckException(
				"Unable to resolve type name '" + type.getName() + "'", type.getLocation());
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
			TypeCheckerErrors.report(3434, "'" + type.getName() + "' is not the name of a type definition",type.getLocation(),type);
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
			TypeCheckerErrors.report(3296, "Cannot use '" + type.getName() + "' outside system class",type.getLocation(),type);
		}

		PType r = null;
		if(def instanceof ATypeDefinition)
		{
			r = ((ATypeDefinition)def).getInvType().clone();
		}
		else if(def instanceof AStateDefinition)
		{
			r = ((AStateDefinition)def).getRecordType().clone();
		} else
		{
			r = def.getType().clone();
		}
		
		List<PDefinition> tempDefs = new Vector<PDefinition>();
		tempDefs.add(def);
		r.setDefinitions(tempDefs);
		return r;
	}

	public static String toDisplay(AUnresolvedType exptype) {
		return "(unresolved " + exptype.getName() + ")";
		
	}

	public static PType isType(AUnresolvedType exptype, String typename) {
		return exptype.getName().getName().equals(typename) ? exptype : null;
	}

	public static boolean equals(AUnresolvedType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		if (other instanceof AUnresolvedType)
		{
			AUnresolvedType nother = (AUnresolvedType)other;
			return type.getName().equals(nother.getName());
		}

		if (other instanceof ANamedInvariantType)
		{
			ANamedInvariantType nother = (ANamedInvariantType)other;
			return type.getName().equals(nother.getName());
		}

		return false;
	}

	

}
