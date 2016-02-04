package org.overture.codegen.vdm2java;

public interface IJavaQouteEventCoordinator
{
	public void registerJavaQuoteObs(IJavaQuoteEventObserver obs);
	public void unregisterJavaQuoteObs(IJavaQuoteEventObserver obs);
}
