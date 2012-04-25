package gui;

import java.awt.Point;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

class Model extends Observable implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
    transient Map<Integer, Passenger> passengers = new HashMap<Integer, Passenger>();
    transient Vector<Integer> passengersOnBus = new Vector<Integer>();
    
    transient Map<String, Road> roads = new HashMap<String, Road>();
    transient Map<Integer, Bus> buses = new HashMap<Integer, Bus>();
    transient Map<String, Waypoint> waypoints = new HashMap<String, Waypoint>();
    
	Object lock;
    int inflow;
    
    public Model() {
    	inflow = 0;
    	lock = new Object();
    	
        buildWaypoints();
        buildRoads();
    }
    
    public synchronized void move(){
    	
    	for (Bus b : buses.values()) {
			b.move();
		}
    	
    	setChanged();
        notifyObservers("move");
    }
    
    public void busInRouteTo(int busid, String roadid, String waypoint, int time){
    	
       	Waypoint wp = waypoints.get(waypoint);
    	Road r = roads.get(roadid);
    	List<Point> coords = r.getCoordinatesGoingFrom(wp);
    	Bus b = buses.get(busid);
    	b.setRoute(coords, time);
    }
    
	public synchronized void passengerAtCentral(int id, String goal) {
		
		passengers.put(id, new Passenger(id, goal));
		
		setChanged();
        notifyObservers("passengerAdded");
	}
	
	public synchronized void passengerAnnoyed(int id) {
		
		passengers.get(id).setAnnoyed(true);
		
		setChanged();
        notifyObservers("passengerAnnoyed");
	}
	
	public synchronized void passengerGotOnBus(int id) {
		
		passengers.get(id).gotOnBus();
		
		synchronized (lock) {
			passengersOnBus.add(id);
		}

		Thread runner = new Thread(new Runnable(){
			 public void run(){
				 
				 try{
						Thread.sleep(750);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
					synchronized (lock) {
				    	//remove passenger that got on a bus
						if(!passengersOnBus.isEmpty()) {
							
							for (int i : passengersOnBus) {
									passengers.remove(i);
							}
							
							passengersOnBus.clear();
						}
					}
			 }

		});
		runner.start();
		
		setChanged();
        notifyObservers("passengerGotOnBus");
	}
	
	public synchronized void inflowChanged(int flow) {
		
		inflow = flow; 
		
		setChanged();
        notifyObservers("inflowChanged");
	}
	
	public void busArrived(int busline) {
		
		Bus b = buses.get(busline);
		b.busArrived();
		
		setChanged();
        notifyObservers("busStopping");
	}
	
	public void busAdded(int busline) {
		
        Bus bs;
        
        bs = new Bus(busline, waypoints.get("Central").point());
        buses.put(bs.lineNr(), bs);
		
		setChanged();
        notifyObservers("busAdded");
	}
	
	public void busPassengerCountChanged(int busline, int count) {
		
		Bus b = buses.get(busline);
		b.setPassengerCount(count);
		
		setChanged();
        notifyObservers("busPassengerCount");
	}
	
	
	public synchronized Collection<Passenger> getPassengers(){
		return new LinkedList<Passenger>(passengers.values());
	}
	
	public synchronized int getInflow()
	{
		return inflow;
	}
	

	public void setLimits(int xLimit, int yLimit) {

        // Notify observers
       setChanged();
       notifyObservers("limits");
   }

	public synchronized Collection<Bus> getBuses() {
		
		
		
		return buses.values();
	}
	
	public Collection<Road> getRoads() {
		return roads.values();
	}
	
	public Collection<Waypoint> getWaypoints() {
		return waypoints.values();
	}
	
	private void buildRoads() {
		Road tRoad;
        //R1
        tRoad = new Road("R1", waypoints.get("A"), waypoints.get("B"));
        roads.put(tRoad.name(), tRoad);
        
        //R2
        tRoad = new Road("R2", waypoints.get("B"),waypoints.get("WP1"));
        roads.put(tRoad.name(), tRoad);
        
        //R3
        tRoad = new Road("R3", waypoints.get("B"),waypoints.get("WP2"));
        roads.put(tRoad.name(), tRoad);
        
        //R4
        tRoad = new Road("R4", 0, 0, 0, 5);
        tRoad.addRoadPart(0, 0, 4, 0);
        //tRoad.addWaypoint(waypoints.get("A"), waypoints.get("WP2"));
        roads.put(tRoad.name(), tRoad);
        
        //R5
        tRoad = new Road("R5", waypoints.get("WP2"),waypoints.get("C"));
        roads.put(tRoad.name(), tRoad);
        
        //R6
        tRoad = new Road("R6", 10, 0, 10, 2);
        tRoad.addRoadPart(10, 2, 12, 2);
        //tRoad.addWaypoint(waypoints.get("C"), waypoints.get("D"));
        roads.put(tRoad.name(), tRoad);
        
        //R7
        tRoad = new Road("R7", waypoints.get("C"),waypoints.get("F"));
        roads.put(tRoad.name(), tRoad);
        
        //R8
        tRoad = new Road("R8", 16, 0, 21, 0);
        tRoad.addRoadPart(21, 0, 21, 5);
        //tRoad.addWaypoint(waypoints.get("Central"), waypoints.get("F"));
        roads.put(tRoad.name(), tRoad);
        
        //R9
        tRoad = new Road("R9", waypoints.get("Central"),waypoints.get("WP3"));
        roads.put(tRoad.name(), tRoad);
        
        //R10
        tRoad = new Road("R10", waypoints.get("WP3"),waypoints.get("WP4"));
        roads.put(tRoad.name(), tRoad);
        
        //R11
        tRoad = new Road("R11", waypoints.get("WP4"),waypoints.get("D"));
        roads.put(tRoad.name(), tRoad);
        
        //R12
        tRoad = new Road("R12", 16, 5, 16, 7);
        tRoad.addRoadPart(16, 7, 14, 7);
        //tRoad.addWaypoint(waypoints.get("E"), waypoints.get("WP3"));
        roads.put(tRoad.name(), tRoad);
        
        //R13
        tRoad = new Road("R13", 14, 7, 12, 7);
        tRoad.addRoadPart(12, 7, 12, 5);
        //tRoad.addWaypoint(waypoints.get("E"), waypoints.get("WP1"));
        roads.put(tRoad.name(), tRoad);
        	
        //R14
        tRoad = new Road("R14", 12, 5, 12, 2);
        roads.put(tRoad.name(), tRoad);
        
        //R15
        tRoad = new Road("R15", waypoints.get("WP4"),  waypoints.get("F"));
        roads.put(tRoad.name(), tRoad);
        
        //R16
        tRoad = new Road("R16", 12, 5, 16, 5);
        roads.put(tRoad.name(), tRoad);
        
        //HW1
        tRoad = new Road("HW1", 0, 5,  0, 10);
        tRoad.addRoadPart(0, 10, 21, 10);
        tRoad.addRoadPart(21, 10, 21, 5);
        tRoad.setHighspeed(true);
        //tRoad.addWaypoint(waypoints.get("Central"), waypoints.get("A"));
        roads.put(tRoad.name(), tRoad);
	}
	
    
    private void buildWaypoints() {
		Waypoint tWp;
        
        tWp = new Waypoint("A", new Point(0, 5), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("B", new Point(4, 5), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("C", new Point(10, 0), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("D", new Point(12, 2), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("E", new Point(14, 7), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("F", new Point(16, 0), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("Central", new Point(21, 5), true);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("WP1", new Point(12, 5), false);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("WP2", new Point(4, 0), false);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("WP3", new Point(16, 5), false);
        waypoints.put(tWp.Name(), tWp);
        
        tWp = new Waypoint("WP4", new Point(16, 2), false);
        waypoints.put(tWp.Name(), tWp);
	}
}
