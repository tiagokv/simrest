package sms.simrest.entities;

import java.util.ArrayList;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_predicate;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;

//The class for the processor
public class Table extends Sim_entity {
  
  private ArrayList<TablePlaceConnection> portTablePlaces;
  private ArrayList<Sim_port> inPorts;
  private Sim_stat stat;
  
  public final static String PREFIX_IN_TABLE = "In_TablePlace";
  public final static String PREFIX_OUT_TABLE = "Out_TablePlace";
  
  public class TablePlaceConnection{
	  public int id;
	  public Sim_port out;
	  public Sim_port in;
	  public Boolean isAvailable;
	  
	  public TablePlaceConnection(int id, Sim_port in, Sim_port out){
		  this.id = id;
		  this.out = out;
		  this.in  = in;
		  this.isAvailable = true;
	  }
  }
  
  public Table(String name, int qttBuffetPlaces, int qttTablePlaces) {
    super(name);
    
    inPorts = new ArrayList<Sim_port>();
    for (int i = 0; i < qttBuffetPlaces; i++) {
    	Sim_port in = new Sim_port("InCustomer" + i);
    	add_port(in);
    	inPorts.add(in);
	}  
    
    portTablePlaces = new ArrayList<TablePlaceConnection>();
    for (int i = 0; i < qttTablePlaces; i++) {
    	Sim_port port_in_place = new Sim_port(PREFIX_IN_TABLE + i); // Will tell that the machine is available
		Sim_port port_out_place = new Sim_port(PREFIX_OUT_TABLE + i); //Will pass the customer
		
		add_port(port_in_place);
		add_port(port_out_place);
		portTablePlaces.add( new TablePlaceConnection(i, port_in_place, port_out_place) );
	}
    
	stat = new Sim_stat();
	stat.add_measure(Sim_stat.QUEUE_LENGTH);
	stat.add_measure(Sim_stat.ARRIVAL_RATE);
	stat.measure_for(new int[] {0});
	set_stat(stat);
  }
  
  public ArrayList<Sim_port> getInPorts(){
	  return inPorts;
  }
  
  public ArrayList<TablePlaceConnection> getTablePlacePorts(){
	  return portTablePlaces;
  }

  private boolean doesEventComeFromInPorts(Sim_event event){
	  for (Sim_port sim_port : inPorts) {
		if( event.from_port(sim_port) ){
			return true;
		}
	  }
	  return false;
  }
  
  public void body() {
    while (Sim_system.running()) {
      Sim_event e = new Sim_event();

      if( areTablePlacesAvailable() ){
    	  sim_get_next(e);
      }else{
		  sim_get_next(new Sim_predicate() {
				@Override
				public boolean match(Sim_event arg0) {
					return !doesEventComeFromInPorts(arg0); //is not a customer
				}
		      }, e);
      }
      
      //Is a new customer coming?
      if( doesEventComeFromInPorts(e) && e.get_data() != null && e.get_data() instanceof Customer ){
    	  Customer cust = (Customer) e.get_data();
    	  
		  // Check table places
		  for (TablePlaceConnection tablePlaceConnection : portTablePlaces) {
			  if( tablePlaceConnection.isAvailable ){
				sim_trace(1,"Customer " + cust.id + " being sent to table place " + tablePlaceConnection.id);
				sim_schedule(tablePlaceConnection.out, 0.0, 0, cust);
				sim_completed(e);
				tablePlaceConnection.isAvailable = false;
				break;
			  }
		  }
    	  
      }else{ //then it´s a signal from the table places stating that they are available
    	  for (TablePlaceConnection tablePlaceConnection : portTablePlaces) {
			if(e.from_port(tablePlaceConnection.in)){ //If comes from this specific machine
				sim_trace(1, "Signal received from TablePlace " + tablePlaceConnection.id);
				sim_completed(e);
				tablePlaceConnection.isAvailable = true;
				break;
			}
    	  }
      }
      
    }
    
  }
  
  private boolean areTablePlacesAvailable(){
  	for (TablePlaceConnection tablePlaceConnection : portTablePlaces) {
			if( tablePlaceConnection.isAvailable ){
				return true;
			}
		}
  	return false;
  }
}