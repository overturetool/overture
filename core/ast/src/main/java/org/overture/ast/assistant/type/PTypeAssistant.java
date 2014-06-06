package org.overture.ast.assistant.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;

public class PTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public boolean isNumeric(PType type)
	{
		try
		{
			return type.apply(af.getNumericFinder());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static SNumericBasicType getNumeric(PType type)
	{
		try
		{
			return type.apply(af.getNumericBasisChecker());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public int hashCode(PType type)
	{
		return internalHashCode(type);
	}

	protected int internalHashCode(PType type)
	{
		try
		{
			return type.apply(af.getHashChecker());
		} catch (AnalysisException e)
		{
			return type.getClass().hashCode();
		}
	}

	public static int hashCode(List<PType> list)
	{
		int hashCode = 1;
		for (PType e : list)
		{
			hashCode = 31 * hashCode + (e == null ? 0 : af.createPTypeAssistant().internalHashCode(e));
		}
		return hashCode;
	}

	public String getName(PType type)
	{
		return type.getLocation().getModule();
	}
}
