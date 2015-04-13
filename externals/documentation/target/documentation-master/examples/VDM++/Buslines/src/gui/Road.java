package gui;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Road {

	String name;
	private boolean highspeed;
	private List<Line2D> roadParts;
	private List<Point> roadPoints;
	
	public Road(String roadname, int x1, int y1, int x2, int y2)
	{
		name =  roadname;
		roadPoints = new LinkedList<Point>();	
		roadParts = new LinkedList<Line2D>();
		
		Line2D.Double l = new Line2D.Double(x1, y1, x2, y2);
		roadParts.add(l);
		roadPoints.addAll(interpolateRoad(l));
	}
	
	public Road(String roadname, Waypoint waypoint1, Waypoint waypoint2)
	{
		name = roadname;
		roadPoints = new LinkedList<Point>();	
		roadParts = new LinkedList<Line2D>();
		
		Line2D.Double l = new Line2D.Double(waypoint1.point(), waypoint2.point());
		roadParts.add(l);
		roadPoints.addAll(interpolateRoad(l));
	}
	
	public void addRoadPart(int x1, int y1, int x2, int y2)
	{
		Line2D.Double l = new Line2D.Double(x1, y1, x2, y2);
		roadParts.add(l);
		
		List<Point> addPoints = interpolateRoad(l);
		
		Point lineStart = roadPoints.get(0);
		Point lineEnd = roadPoints.get(roadPoints.size() -1);
		
		 //start of existing road matches with start of new road, flip, and put new road in front
		if(lineStart.equals(addPoints.get(0)))
		{
			addPoints.remove(0);
			Collections.reverse(addPoints);
			roadPoints.addAll(0, addPoints);
		
		 //start of existing road matches with end of new road, put new road in front
		} else if(lineStart.equals(addPoints.get(addPoints.size()-1))){
			
			roadPoints.addAll(0, addPoints);
		
		//end of existing road matches with front of new road, join new road after existing
		} else if(lineEnd.equals(addPoints.get(0))){
			
			//clearout double defined point
			addPoints.remove(0);
			roadPoints.addAll(addPoints);
		
		//end of existing road matches with end of new road, flip and join new road after existing
		} else if(lineEnd.equals(addPoints.get(addPoints.size()-1))){
			Collections.reverse(addPoints);
			roadPoints.addAll(addPoints);
		} 
	}
	
	public List<Point> getCoordinatesGoingFrom(Waypoint wp){
		
		if(roadPoints.get(0).equals(wp.point())){
			Collections.reverse(roadPoints);
		}
		return new LinkedList<Point>(roadPoints);
	}
	
	private List<Point> interpolateRoad(Line2D.Double rp)
	{
		List<Point> points = new LinkedList<Point>();
		//calc all points on line between (x1,y1) and (x2,y2)
			
		//road are always straight and orthogonal on each other
		//so only check for direction NS or EW
		
		if(rp.getX1() == rp.getX2()) {  //NS
			
			//how many points
			int distance = (int) (rp.getY1() - rp.getY2());
			//starting from highest or lowest with respect to Y2
			int direction = (rp.getY1() < rp.getY2()) ? -1 : 1;
			
			for (int i = Math.abs(distance); i >= 0 ; i--) {
				points.add(new Point((int)rp.getX1(),(int)rp.getY2()+i * direction));				
			}
			
		} else {
			int distance = (int) (rp.getX1() - rp.getX2());
			int direction = (rp.getX1() < rp.getX2()) ? -1 : 1;
			
			for (int i = Math.abs(distance); i >= 0 ; i--) {
				points.add(new Point((int)rp.getX2()+i * direction, (int)rp.getY1()));				
			}
		}
		
		return points;
	}

	List<Line2D> getRoadParts() {
		return roadParts;
	}
	
	public String name(){
		return name;
	}

	void setHighspeed(boolean highspeed) {
		this.highspeed = highspeed;
	}

	boolean isHighspeed() {
		return highspeed;
	}
}
