package gui;

import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    transient List<String> busAndrouteStms = new LinkedList<String>();
    
	private final static String dbConnStr = "jdbc:h2:bus"; //from application dir
	//private final static String dbConnStr = "jdbc:h2:~/bus"; //from user dir

	private Object lock;
    private int inflow;
	private int currentPlan;
	private int planCount;
    
    public Model() throws ClassNotFoundException, SQLException {
    	inflow = 0;
    	currentPlan = 1;
    	planCount = 0;
    	lock = new Object();
    	
    	Connection conn = null;
    	Statement plansStm = null;
    	
    	try
    	{
    		conn = DriverManager.getConnection(dbConnStr, "sa", "");
	        plansStm = conn.createStatement();
	        ResultSet plans = plansStm.executeQuery("SELECT COUNT(*) FROM Plan");
	        plans.next();
	        planCount = plans.getInt(1);
	        if(planCount < 1) 
	        	throw new SQLException("No busplans in database");
    	} finally {
    		if(plansStm != null)
    			plansStm.close();
    		if(conn != null)
    			conn.close();
        }
    	
    }

    public void nextPlan() throws SQLException
    {
    	if(currentPlan >= planCount)
    	{
    		currentPlan = 0;
    	}
    	currentPlan++;
    	loadPlan(currentPlan);
    }
    
    public void prevPlan() throws SQLException
    {
    	currentPlan--;
    	if(currentPlan <= 0)
    	{
    		currentPlan = planCount;
    	}
    	
    	loadPlan(currentPlan);
    }
    
    public void loadPlan() throws SQLException{
    	loadPlan(1);
    }
    
	private void loadPlan(int planid) throws SQLException {	
		
		roads.clear();
		waypoints.clear();
		busAndrouteStms.clear();
		
		Connection conn = null;
    	Statement plansStm = null;
    	try
    	{
    		//open db
	        //conn = DriverManager.getConnection("jdbc:h2:~/bus", "sa", ""); 
    		//conn = DriverManager.getConnection("jdbc:h2:bus", "sa", ""); 
    		conn = DriverManager.getConnection(dbConnStr, "sa", "");
    		
	        plansStm = conn.createStatement();
	        //get all waypoints
	        loadAllWps(conn, planid);
	        //get all roads in plan
	        ResultSet rds = plansStm.executeQuery("SELECT r.id, r.name, r.highspeed, r.wp1, wp.name AS wp1Name,  r.wp2, wp2.name AS wp2Name, " +
	        		"CASE WHEN rp.id IS NULL THEN false ELSE true END as roadparts " + 
	        		"FROM ROAD r " +
	        		"LEFT JOIN ROADPART rp on r.ID = rp.roadid " +
	        		"LEFT JOIN WAYPOINT wp on wp.ID =  r.wp1 " +
	        		"LEFT JOIN WAYPOINT wp2 on wp2.ID =  r.wp2  " +
	        		"WHERE r.PLANID  = " + planid);
        	Road tRoad;
        	//build roads
	        while (rds.next()) {
        		tRoad = new Road(rds.getString("name"), waypoints.get(rds.getString("wp1Name")), waypoints.get(rds.getString("wp2Name")));
        		tRoad.setHighspeed( rds.getBoolean("highspeed"));
	        	if(rds.getBoolean("roadparts")){
	        		loadRoadparts(conn, rds.getInt("id"), tRoad);
	        	}
	        	roads.put(tRoad.name(), tRoad);
	        }
        	plansStm.close();
        	
        	loadbus(conn, planid);
        	
        	setChanged();
            notifyObservers("planUpdated");
        	
    	} finally {
        	if(conn != null)
        	conn.close();
        }
	}
	
	private void loadbus(Connection conn, int planid) throws SQLException {
		
		Statement busStm = conn.createStatement();
		ResultSet buses = busStm.executeQuery("SELECT id, name FROM bus WHERE planid = " + planid);
		
		int busid = 0;
		while(buses.next()){
			busid = buses.getInt("id");
			String route = loadBusRoute(conn, busid, buses.getString("name"));
			busAndrouteStms.add(route);
			System.out.println(route);
		}
	}
	
	
	private String loadBusRoute(Connection conn, int busid, String name) throws SQLException {
		Statement routestm = conn.createStatement();
		ResultSet busRoute = routestm.executeQuery("SELECT r.name FROM busroute b, road r  WHERE b.road = r.id AND busid = " + busid );
		
		StringBuilder strBld = new StringBuilder();
		strBld.append(name);
		strBld.append(",[");
		while (busRoute.next()) {
			strBld.append("<");
			strBld.append(busRoute.getString("name"));
			strBld.append(">, ");	
		}
		strBld.replace(strBld.length()-2, strBld.length(), "]");
		return strBld.toString();
	}

	private void loadAllWps(Connection conn, int planid) throws SQLException{
		
		Statement wpsStm = null;
        wpsStm = conn.createStatement();
        ResultSet wps = wpsStm.executeQuery("SELECT name, x,y, stop FROM WAYPOINT WHERE planid = " + planid);
    	
    	Waypoint tWp;
        while (wps.next()) {            
            tWp = new Waypoint(wps.getString("name"), new Point(wps.getInt("x"), wps.getInt("y")), wps.getBoolean("stop"));
            waypoints.put(tWp.Name(), tWp);
        }
    	wpsStm.close();
	}
	
    private void loadRoadparts(Connection conn, int roadid, Road road) throws SQLException {

    	Statement wpsStm = null;
        wpsStm = conn.createStatement();
        ResultSet roadPrts = wpsStm.executeQuery("SELECT startx, starty, endX , endy  FROM ROADPART WHERE ROADID = " + roadid + " ORDER BY PRIORITY" );
        road.clearPreviousRoad();
        while (roadPrts.next()) {
        	road.addRoadPart(roadPrts.getInt("startx"), roadPrts.getInt("starty"), roadPrts.getInt("endx"), roadPrts.getInt("endy"));
        }
    	wpsStm.close();
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
	
	public Collection<String> getBusStms(){
		return busAndrouteStms;
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
}