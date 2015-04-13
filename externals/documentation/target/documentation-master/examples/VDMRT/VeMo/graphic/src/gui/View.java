package gui;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

class View extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

	static final int CAR_ICON_OFFSET = 40;

    Model model;
    ConcurrentHashMap<Long, Vehicle> vecCol =new ConcurrentHashMap<Long, Vehicle>();

    boolean drawRange = true;
    private boolean drawConnections = true;

    transient Image bg;
    
    View(Model model) throws IOException {
        this.model = model;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
    }

    public void addVehicles(Collection<Vehicle> vehicles){

        for(Vehicle vec: vehicles){
            addVehicle(vec);
        }
    }

    public void addVehicle(Vehicle vehicle){

        if(!vecCol.containsKey(vehicle.getVechicleID())){
            Dimension size = vehicle.getPreferredSize();
            vehicle.setBounds(1, 1, size.width, size.height);
            add(vehicle);
            vecCol.put(vehicle.getVechicleID(),vehicle);
        	vehicle.GridSizeChanged();
        }
    }


    transient Graphics2D g2;
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent( g );

        g2 = (Graphics2D) g;
        
        if(drawRange){

            g2.setStroke(new BasicStroke(2));
            for (Iterator<Vehicle> it = vecCol.values().iterator(); it.hasNext();) {
                Vehicle vec = it.next();
                int circleSize = vec.GetRange();
                g.setColor(vec.getCarColor());
                 //draw range
                g2.drawOval(vec.GetPosition().x - ((circleSize/2) - CAR_ICON_OFFSET), vec.GetPosition().y - (circleSize/2) + CAR_ICON_OFFSET, circleSize, circleSize);
            }
        }

        if(drawConnections){
            g2.setStroke(new BasicStroke(4));
           
            HashSet<Long> connected;
            Point connVec;
            Vehicle vec;

            //for all vehicles
            for (Iterator<Vehicle> it = vecCol.values().iterator(); it.hasNext();) {
                vec = it.next();

                //get connections to other vehicles
                connected = model.getConnectedVehicles(vec.getVechicleID());

                for(Long connVecId : connected){

                    connVec = vecCol.get(connVecId).GetPosition();
                    //draw connections
                    g2.setColor(Color.ORANGE);
                    g2.drawLine(connVec.x + CAR_ICON_OFFSET, connVec.y + CAR_ICON_OFFSET, vec.GetPosition().x + CAR_ICON_OFFSET, vec.GetPosition().y + CAR_ICON_OFFSET);
                }
            }
        }
    }
    

    public void update(Observable obs, Object arg) {
    
        //window resize
        if(arg != null && arg == "limits"){
            for (Iterator<Vehicle> it = vecCol.values().iterator(); it.hasNext();) {
                 Vehicle vec = it.next();
                 vec.GridSizeChanged();
            }
        }

        if(arg != null && arg == "range"){
             drawRange = !drawRange;

        }

        if(arg != null && arg == "connections"){
             drawConnections = !drawConnections;
        }

        if(arg != null && arg == "newVehicle"){
            addVehicles(model.getVehicles());
        }
  
        repaint();
    }
}

    