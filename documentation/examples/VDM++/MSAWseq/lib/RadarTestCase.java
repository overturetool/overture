package dk.au.eng;

import org.junit.Assert;
import org.junit.Test;

public class RadarTestCase {

	@Test
	public void testCoordinatesToAngle90()
	{
		int angle = Radar.crdToAngle(0, 200);
		Assert.assertEquals(90,angle);
	}
	

	@Test
	public void testCoordinatesToAngle0()
	{
		int angle = Radar.crdToAngle(200, 0);
		Assert.assertEquals(0,angle);
	}

	@Test
	public void testCoordinatesToAngle180()
	{
		int angle = Radar.crdToAngle(-200, 0);
		Assert.assertEquals(180,angle);
	}

	@Test
	public void testCoordinatesToAngle270()
	{
		int angle = Radar.crdToAngle(0, -200);
		Assert.assertEquals(270,angle);
	}

	@Test
	public void testCoordinatesToAngle45()
	{
		int angle = Radar.crdToAngle(50, 50);
		Assert.assertEquals(45,angle);
	}
	
	@Test
	public void testCoordinatesToAngle135()
	{
		int angle = Radar.crdToAngle(-50, 50);
		Assert.assertEquals(135,angle);
	}

	
	@Test
	public void testCoordinatesToAngle225()
	{
		int angle = Radar.crdToAngle(-50, -50);
		Assert.assertEquals(225,angle);
	}
	
	@Test
	public void testCoordinatesToAngle315()
	{
		int angle = Radar.crdToAngle(50, -50);
		Assert.assertEquals(315,angle);
	}


}
