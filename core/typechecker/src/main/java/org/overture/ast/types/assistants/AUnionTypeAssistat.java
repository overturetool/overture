package org.overture.ast.types.assistants;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;


public class AUnionTypeAssistat {

	public static PType typeResolve(AUnionType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved())
		{
			return type;
		}
		else
		{
			type.setResolved(true);
			type.setInfinite(true);
		}

		try
		{
			Set<PType> fixed = new HashSet<PType>();

			for (PType t: type.getTypes())
			{
				if (root != null)
					root.setInfinite(false);

				fixed.add(PTypeAssistant.typeResolve(t, root, rootVisitor, question));

				if (root != null)
					type.setInfinite(type.getInfinite() && root.getInfinite());
			}

			type.setTypes(new Vector<PType>(fixed));
			if (root != null) root.setInfinite(type.getInfinite());

			// Resolved types may be unions, so force a re-expand
			type.setExpanded(false);
			expand(type);

			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static void unResolve(AUnionType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType t: type.getTypes())
		{
			PTypeAssistant.unResolve(t);
		}
		
	}

	private static void expand(AUnionType type) {
		
		if (type.getExpanded()) return;
		Set<PType> exptypes = new HashSet<PType>();

		for (PType t: type.getTypes())
		{
    		if (t instanceof AUnionType)
    		{
    			AUnionType ut = (AUnionType)t;
  				expand(ut);
   				exptypes.addAll(ut.getTypes());
    		}
    		else
    		{
    			exptypes.add(t);
    		}
		}

		type.setTypes(new Vector<PType>(exptypes));
		type.setExpanded(true);
		NodeList<PDefinition> definitions = type.getDefinitions();

		for (PType t: type.getTypes())
		{
			if (t.getDefinitions() != null)
			{
				definitions.addAll(t.getDefinitions());
			}
		}
		
	}
	

}
