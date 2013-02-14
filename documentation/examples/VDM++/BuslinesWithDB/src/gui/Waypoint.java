package gui;

import java.awt.Point;

public class Waypoint {

	boolean stop;
	String name;
	Point point;
	
	public Waypoint(String wpName, Point location) {
		this(wpName, location, false);
	}
	
	public Waypoint(String wpName, Point location, boolean isStop) {
		
		name = wpName;
		stop = isStop;
		point = location;
	}
	
	public int X(){
		return (int) point.getX();
	}
	
	public int Y(){
		return (int) point.getY();
	}
	
	public Point point(){
		return  point;
	}
	
	public String Name(){
		return name;
	}
	
	public boolean IsStop()
	{
		return stop;
	}
	
	@Override
	public String toString() {
		return "<" + name + ">";
	}
}
