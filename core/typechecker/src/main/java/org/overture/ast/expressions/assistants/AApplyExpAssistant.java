package org.overture.ast.expressions.assistants;

import java.util.List;

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;


public class AApplyExpAssistant {

	public static PType functionApply(AApplyExp node, boolean isSimple, AFunctionType ft) {
		List<PType> ptypes = ft.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{ 
			TypeCheckerErrors.concern(isSimple, 3059, "Too many arguments",node.getLocation(),node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		}
		else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3060, "Too few arguments",node.getLocation(),node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		}

		int i=0;

		for (PType at: node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				TypeCheckerErrors.concern(isSimple, 3061, "Inappropriate type for argument " + i,node.getLocation(),node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ft.getResult();
	}

	public static PType operationApply(AApplyExp node, boolean isSimple,
			AOperationType ot) {
		List<PType> ptypes = ot.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3062, "Too many arguments",node.getLocation(),node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		}
		else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3063, "Too few arguments",node.getLocation(),node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		}

		int i=0;

		for (PType at: node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				TypeCheckerErrors.concern(isSimple, 3064, "Inappropriate type for argument " + i,node.getLocation(),node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ot.getResult();
	}

	public static PType sequenceApply(AApplyExp node, boolean isSimple,
			SSeqType seq) {
		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3055, "Sequence selector must have one argument",node.getLocation(),node);
		}
		else if (!PTypeAssistant.isNumeric(node.getArgtypes().get(0)))
		{
			TypeCheckerErrors.concern(isSimple, 3056, "Sequence application argument must be numeric",node.getLocation(),node);
		}
		else if (seq.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3268, "Empty sequence cannot be applied",node.getLocation(),node);
		}

		return seq.getSeqof();
	}

	public static PType mapApply(AApplyExp node, boolean isSimple, SMapType map) {
		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3057, "Map application must have one argument",node.getLocation(),node);
		}
		else if (map.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3267, "Empty map cannot be applied",node.getLocation(),node);
		}

		PType argtype = node.getArgtypes().get(0);

		if (!TypeComparator.compatible(map.getFrom(), argtype))
		{
			TypeCheckerErrors.concern(isSimple, 3058, "Map application argument is incompatible type",node.getLocation(),node);
			TypeCheckerErrors.detail2(isSimple, "Map domain", map.getFrom(), "Argument", argtype);
		}

		return map.getTo();
	}
}
