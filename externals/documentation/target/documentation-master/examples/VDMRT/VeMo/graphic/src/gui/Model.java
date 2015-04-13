package gui;

import gui.Vehicle.CarColor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;

class Model extends Observable implements Serializable {
 
	private static final long serialVersionUID = 1L;
	public static final int GRID_SIZE  = 50;
    public static final int VEHICLE_RANGE  = 10;
    
    private int colorNo = 0;
    CarColor[] colors;
    
    private Map<Long, Vehicle> vecCol =new HashMap<Long, Vehicle>();
    private HashMap<Long, HashSet<Long>> connections =new HashMap<Long, HashSet<Long>>();
    
    public Model() {
    	colors = Vehicle.CarColor.values();
    }

    public void addVehicle(Long vecID){
    	colorNo = colorNo % (colors.length);
        this.addVehicle(new Vehicle(vecID, colors[colorNo]));
    	colorNo++;
    }
    
    public void addVehicle(Vehicle vehicle){
        if(!vecCol.containsKey(vehicle.getVechicleID()))
        {
            vecCol.put(vehicle.getVechicleID(),vehicle);
            connections.put(vehicle.getVechicleID(), new HashSet<Long>());
            
            // Notify observers
            setChanged();
            notifyObservers("newVehicle");
        }
    }

    public void connectVehicles(long vec, long vec2){
        if(connections.containsKey(vec)){

            connections.get(vec).add(vec2);
        }
    }

    public void disconnectVehicles(long vec, long vec2){
        if(connections.containsKey(vec)){

            connections.get(vec).remove(vec2);
        }
    }
    
    public void updatePosition(long vecId, int x, int y){
    	if(vecCol.containsKey(vecId)){
    		vecCol.get(vecId).SetPosition(x, y);
    	}
    }
    
    public void updateDirection(long vecId, int dir){
    	if(vecCol.containsKey(vecId)){
    		vecCol.get(vecId).SetDirection(dir);
    	}
    }

    public HashSet<Long> getConnectedVehicles(long vecId){
        if(connections.containsKey(vecId)){
            return connections.get(vecId);
        }
        
        return null;
    }

    public Collection<Vehicle> getVehicles(){
        return vecCol.values();
    }

    public void setLimits(int xLimit, int yLimit) {

         // Notify observers
        setChanged();
        notifyObservers("limits");
    }
    
    public void refresh() {
        // Notify observers
        setChanged();
        notifyObservers("refresh");
    }

    public void toggleRange(){
        setChanged();
        notifyObservers("range"); 
    }

    public void toggleConnections(){
        setChanged();
        notifyObservers("connections");
   	}
    
    public void receivedMessage(long vecId){
    	
    	if(vecCol.containsKey(vecId)){
    		vecCol.get(vecId).SetReceivedData();
    	}
    }
}
