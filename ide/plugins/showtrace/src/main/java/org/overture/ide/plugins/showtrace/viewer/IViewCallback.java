package org.overture.ide.plugins.showtrace.viewer;

public interface IViewCallback {

	public abstract void panToTime(long time, long thrid);

	public abstract void updateOverviewPage();

	public abstract void showMessage(String message);
	
	 public abstract void addUpperError(Long x1, Long x2, String name);
	 
	 public abstract void addLowerError(Long x1, Long x2, String name);

}