package org.overturetool.traces.utility;

import org.overturetool.traces.utility.ITracesHelper.TestResultType;

public class TraceTestStatus {
	private Integer number = -1;
	private TestResultType status = TestResultType.Unknown;
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param status the status to set
	 */
public	void setStatus(TestResultType status) {
		this.status = status;
	}
	/**
	 * @return the status
	 */
public	TestResultType getStatus() {
		return status;
	}
	
	

}
