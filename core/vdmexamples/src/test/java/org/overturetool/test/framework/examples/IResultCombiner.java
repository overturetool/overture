package org.overturetool.test.framework.examples;

public interface IResultCombiner<R>
{
	R combine(R a, R b);
}
