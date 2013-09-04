package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;

public class TypeUtils
{
	public static class TypeUnwrapper<A> extends AnswerAdaptor<A>
	{
		/**
		 * Generated serial version
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public A caseABracketType(ABracketType node) throws AnalysisException
		{
			return node.getType().apply(this);
		}

		@Override
		public A caseAOptionalType(AOptionalType node) throws AnalysisException
		{
			return node.getType().apply(this);
		}
	}

	public static class MapBasisChecker extends TypeUnwrapper<Boolean>
	{
		/**
		 * Generated serial version
		 */
		private static final long serialVersionUID = 1L;

		protected ITypeCheckerAssistantFactory af;

		public MapBasisChecker(ITypeCheckerAssistantFactory af)
		{
			this.af = af;
		}

		@Override
		public Boolean defaultSMapType(SMapType type) throws AnalysisException
		{
			return SMapTypeAssistantTC.isMap((SMapType) type);
		}

		@Override
		public Boolean caseANamedInvariantType(ANamedInvariantType type)
				throws AnalysisException
		{
			return ANamedInvariantTypeAssistantTC.isMap((ANamedInvariantType) type);
		}

		@Override
		public Boolean caseAUnionType(AUnionType type) throws AnalysisException
		{
			return AUnionTypeAssistantTC.isMap((AUnionType) type);
		}

		@Override
		public Boolean caseAUnknownType(AUnknownType type)
				throws AnalysisException
		{
			return AUnknownTypeAssistantTC.isMap((AUnknownType) type);
		}

		@Override
		public Boolean defaultPType(PType node) throws AnalysisException
		{
			return false;
		}
	}
}
