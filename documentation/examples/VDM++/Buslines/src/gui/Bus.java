package gui;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Bus {

	int passCount;
	int id;
	Point currentPoint;
	List<Point> currentRoute;
	int currentRouteIndex;
	int currentRouteStepSize;
	
	Bus(int busId, Point initialPoint){
		id = busId;
		passCount = 0;
		currentRoute = new LinkedList<Point>();
		currentRouteIndex = 0;
		currentRouteStepSize = 0;
		currentPoint = initialPoint;
	}
	
	public void setPassengerCount(int passengerCount){
		passCount = passengerCount;
	}
	
	public int passengerCount() {
		return passCount;
	}
	
	public int lineNr(){
		return id;
	}
	
	public synchronized void move(){
		
		if(currentRouteIndex + currentRouteStepSize < currentRoute.size())
		{
			currentRouteIndex += currentRouteStepSize;
			currentPoint = currentRoute.get((currentRouteIndex));
		}
	}
	
	public synchronized void busArrived(){
		currentRouteIndex = currentRoute.size();
		currentPoint = currentRoute.get(currentRoute.size() -1);
	}
	
	public void setRoute(List<Point> route, int timeToMove)
	{
		currentRoute = route;
		currentRouteIndex = 0;

		currentRouteStepSize = 	(int) Math.floor(route.size() / timeToMove);
	}
	
	public Point getBusPosition(){
		
		return currentPoint;
	}
	
}
