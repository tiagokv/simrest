package sms.simrest.entities;

import java.util.ArrayList;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_predicate;
import eduni.simjava.Sim_system;

//The class for the processor
public class Table extends Sim_entity {
  private Sim_port in;

  private ArrayList<TablePlaceConnection> portTablePlaces;
  
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
  
  public Table(String name, int qttTablePlaces) {
    super(name);
    // Receive Customers from Source
    in = new Sim_port("InCustomer");
    add_port(in);
    
    portTablePlaces = new ArrayList<TablePlaceConnection>();
    for (int i = 0; i < qttTablePlaces; i++) {
    	Sim_port port_in_place = new Sim_port(PREFIX_IN_TABLE + i); // Will tell that the machine is available
		Sim_port port_out_place = new Sim_port(PREFIX_OUT_TABLE + i); //Will pass the customer
		
		add_port(port_in_place);
		add_port(port_out_place);
		portTablePlaces.add( new TablePlaceConnection(i, port_in_place, port_out_place) );
	}
    
    
  }
  
  public ArrayList<TablePlaceConnection> getTablePlacePorts(){
	  return portTablePlaces;
  }

  public void body() {
    while (Sim_system.running()) {
      Sim_event e = new Sim_event();

      //First gather all table places responses
      sim_select( new Sim_predicate() {
		@Override
		public boolean match(Sim_event arg0) {
			return !arg0.from_port(in); //is not a customer
		}
      }, e);
      
      if( e.get_tag() == -1 && areTablePlacesAvailable() ){
    	  sim_get_next(e);
      }
      
      //Is a new customer coming?
      if( e.from_port(in) && e.get_data() != null && e.get_data() instanceof Customer ){
    	  Customer cust = (Customer) e.get_data();
    	  
		  // Check table places
		  for (TablePlaceConnection tablePlaceConnection : portTablePlaces) {
			  if( tablePlaceConnection.isAvailable ){
				sim_trace(1,"Customer " + cust.id + " being sent to place " + tablePlaceConnection.id);
				sim_schedule(tablePlaceConnection.out, 0.0, 0, cust);
				tablePlaceConnection.isAvailable = false;
				break;
			  }
		  }
    	  
      }else{ //then it´s a signal from the table places stating that they are available
    	  for (TablePlaceConnection tablePlaceConnection : portTablePlaces) {
			if(e.from_port(tablePlaceConnection.in)){ //If comes from this specific machine
				sim_trace(1, "Signal received from TablePlace " + tablePlaceConnection.id);
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