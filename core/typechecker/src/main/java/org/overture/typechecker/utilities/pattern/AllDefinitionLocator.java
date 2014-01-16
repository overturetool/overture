package org.overture.typechecker.utilities.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Get a complete list of all definitions, including duplicates. This method should only be used only by PP
 * 
 * @author gkanos
 */

public class AllDefinitionLocator
		extends
		QuestionAnswerAdaptor<AllDefinitionLocator.NewQuestion, List<PDefinition>>
{

	public static class NewQuestion
	{
		PType ptype;
		NameScope scope;

		public NewQuestion(PType ptype, NameScope scope)
		{
			this.ptype = ptype;
			this.scope = scope;
		}
	}

	protected ITypeCheckerAssistantFactory af;

	public AllDefinitionLocator(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<PDefinition> caseAIdentifierPattern(AIdentifierPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new ArrayList<PDefinition>();
		defs.add(AstFactory.newALocalDefinition(pattern.getLocation(), pattern.getName().clone(), question.scope, question.ptype));
		return defs;
	}

	@Override
	public List<PDefinition> caseABooleanPattern(ABooleanPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseACharacterPattern(ACharacterPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAExpressionPattern(AExpressionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAIgnorePattern(AIgnorePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAIntegerPattern(AIntegerPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseANilPattern(ANilPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAQuotePattern(AQuotePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseARealPattern(ARealPattern node,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAStringPattern(AStringPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAConcatenationPattern(
			AConcatenationPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		List<PDefinition> list = af.createPPatternAssistant().getDefinitions(pattern.getLeft(), question.ptype, question.scope);
		list.addAll(af.createPPatternAssistant().getDefinitions(pattern.getRight(), question.ptype, question.scope));
		return list;
	}

	@Override
	public List<PDefinition> caseARecordPattern(ARecordPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		PType type = pattern.getType();

<<<<<<< HEAD
		if (!af.createPTypeAssistant().isRecord(type))
=======
		if (!PTypeAssistantTC.isTag(type))
>>>>>>> origin/pvj/main
		{
			TypeCheckerErrors.report(3200, "Mk_ expression is not a record type", pattern.getLocation(), pattern);
			TypeCheckerErrors.detail("Type", type);
			return defs;
		}

		ARecordInvariantType pattype = af.createPTypeAssistant().getRecord(type);
		PType using = af.createPTypeAssistant().isType(question.ptype, pattype.getName().getFullName());

		if (using == null || !(using instanceof ARecordInvariantType))
		{
			TypeCheckerErrors.report(3201, "Matching expression is not a compatible record type", pattern.getLocation(), pattern);
			TypeCheckerErrors.detail2("Pattern type", type, "Expression type", question.ptype);
			return defs;
		}

		// RecordType usingrec = (RecordType)using;

		if (pattype.getFields().size() != pattern.getPlist().size())
		{
			TypeCheckerErrors.report(3202, "Record pattern argument/field count mismatch", pattern.getLocation(), pattern);
		} else
		{
			Iterator<AFieldField> patfi = pattype.getFields().iterator();

			for (PPattern p : pattern.getPlist())
			{
				AFieldField pf = patfi.next();
				// defs.addAll(p.getDefinitions(usingrec.findField(pf.tag).type, scope));
				defs.addAll(af.createPPatternAssistant().getDefinitions(p, pf.getType(), question.scope));
			}
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseASeqPattern(ASeqPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isSeq(question.ptype))
		{
			TypeCheckerErrors.report(3203, "Sequence pattern is matched against "
					+ question.ptype, pattern.getLocation(), pattern);
		} else
		{
			PType elem = af.createPTypeAssistant().getSeq(question.ptype).getSeqof();

			for (PPattern p : pattern.getPlist())
			{
				defs.addAll(af.createPPatternAssistant().getDefinitions(p, elem, question.scope));
			}
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseASetPattern(ASetPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		//return ASetPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isSet(question.ptype))
		{
			TypeCheckerErrors.report(3204, "Set pattern is not matched against set type", pattern.getLocation(), pattern);
			TypeCheckerErrors.detail("Actual type", question.ptype);
		} else
		{
			ASetType set = af.createPTypeAssistant().getSet(question.ptype);

			if (!set.getEmpty())
			{
				for (PPattern p : pattern.getPlist())
				{
					defs.addAll(af.createPPatternAssistant().getDefinitions(p, set.getSetof(), question.scope));
				}
			}
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseATuplePattern(ATuplePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isProduct(question.ptype, pattern.getPlist().size()))
		{
			TypeCheckerErrors.report(3205, "Matching expression is not a product of cardinality "
					+ pattern.getPlist().size(), pattern.getLocation(), pattern);
			TypeCheckerErrors.detail("Actual", question.ptype);
			return defs;
		}

		AProductType product = af.createPTypeAssistant().getProduct(question.ptype, pattern.getPlist().size());
		Iterator<PType> ti = product.getTypes().iterator();

		for (PPattern p : pattern.getPlist())
		{
			defs.addAll(af.createPPatternAssistant().getDefinitions(p, ti.next(), question.scope));
		}

		return defs;
	}

	@Override
	public List<PDefinition> caseAUnionPattern(AUnionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isSet(question.ptype))
		{
			TypeCheckerErrors.report(3206, "Matching expression is not a set type", pattern.getLocation(), pattern);
		}

		defs.addAll(af.createPPatternAssistant().getDefinitions(pattern.getLeft(), question.ptype, question.scope));
		defs.addAll(af.createPPatternAssistant().getDefinitions(pattern.getRight(), question.ptype, question.scope));

		return defs;
	}

	@Override
	public List<PDefinition> caseAMapUnionPattern(AMapUnionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isMap(question.ptype))
		{
			TypeCheckerErrors.report(3315, "Matching expression is not a map type", pattern.getLocation(), pattern);
		}

		defs.addAll(af.createPPatternAssistant().getDefinitions(pattern.getLeft(), question.ptype, question.scope));
		defs.addAll(af.createPPatternAssistant().getDefinitions(pattern.getRight(), question.ptype, question.scope));

		return defs;
	}

	@Override
	public List<PDefinition> caseAMapPattern(AMapPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!af.createPTypeAssistant().isMap(question.ptype))
		{
			TypeCheckerErrors.report(3314, "Map pattern is not matched against map type", pattern.getLocation(), pattern);
			TypeCheckerErrors.detail("Actual type", question.ptype);
		} else
		{
			SMapType map = af.createPTypeAssistant().getMap(question.ptype);

			if (!map.getEmpty())
			{
				for (AMapletPatternMaplet p : pattern.getMaplets())
				{
					defs.addAll(af.createAMapletPatternMapletAssistant().getDefinitions(p, map, question.scope));
				}
			}
		}

		return defs;
	}

	@Override
	public List<PDefinition> createNewReturnValue(INode node,
			NewQuestion question) throws AnalysisException
	{
		assert false : "PPatternAssistant.getDefinitions - should not hit this case";
		return null;
	}

	@Override
	public List<PDefinition> createNewReturnValue(Object node,
			NewQuestion question) throws AnalysisException
	{
		assert false : "PPatternAssistant.getDefinitions - should not hit this case";
		return null;
	}
}
