package org.overture.codegen.runtime;

public class StaticOperationsCounters
{
	public static int methodnumber = 50;
	public volatile static long[] act = new long[methodnumber]; // holds the #act history counter.
	public volatile static long[] fin = new long[methodnumber]; // holds the #fin history counter.
	public volatile static long[] req = new long[methodnumber]; // holds the #req history counter.
	public volatile static long[] active = new long[methodnumber]; // holds the #active history counter.
	public volatile static long[] waiting = new long[methodnumber]; // holds the #waiting history counter.

	// public StaticOperationsCounters()
	// {
	//
	// act = new long[nrf];
	// fin = new long[(int)nrf];
	// req = new long[(int)nrf];
	// active = new long[(int)nrf];
	// waiting = new long[(int)nrf];
	// }
}
