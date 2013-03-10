package org.overture.ct.utils;

public class TraceHelperNotInitializedException extends Exception
{

	public TraceHelperNotInitializedException(String projectName)
	{
		this.projectName = projectName;
	}

	public TraceHelperNotInitializedException(String message, String projectName)
	{
		super(message);
		this.projectName = projectName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String projectName;

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

}
