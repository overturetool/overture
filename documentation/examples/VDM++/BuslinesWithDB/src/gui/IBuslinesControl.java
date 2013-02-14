package gui;

public interface IBuslinesControl {
	
	abstract void IncreaseInflow();
	abstract void DecreaseInflow();
	abstract void AddWaypoint(String wp);
	abstract void AddBusstop(String wp);
	void AddRoad(String wp1, String wp2, String road, int length, boolean highspeed);
	void AddBus(String stm);
	void GfxDone();
}
