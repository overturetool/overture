package org.overture.codegen.transform;

public interface ITransformationConfig
{

	public abstract String seqUtilFile();

	public abstract String setUtilFile();

	public abstract String mapUtilFile();

	public abstract String seqUtilEmptySeqCall();

	public abstract String setUtilEmptySetCall();

	public abstract String addElementToSeq();

	public abstract String mapUtilEmptyMapCall();

	public abstract String addElementToSet();

	public abstract String addElementToMap();

	public abstract String iteratorType();

	public abstract String hasNextElement();

	public abstract String nextElement();

	public abstract String runtimeExceptionTypeName();

	public abstract String iteratorMethod();

}