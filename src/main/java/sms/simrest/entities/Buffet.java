package sms.simrest.entities;

import java.util.ArrayList;
import java.util.Iterator;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_predicate;
import eduni.simjava.Sim_system;

//The class for the processor
public class Buffet extends Sim_entity {

  private ArrayList<BuffetPlaceConnection> portPlaces;
  private ArrayList<Sim_port> inPorts;
  
  public final static String PREFIX_IN_PLACE = "In_BuffetPlace";
  public final static String PREFIX_OUT_PLACE = "Out_BuffetPlace";
  
  public class BuffetPlaceConnection{
	  public int id;
	  public Sim_port out;
	  public Sim_port in;
	  public Boolean isAvailable;
	  
	  public BuffetPlaceConnection(int id, Sim_port in, Sim_port out){
		  this.id = id;
		  this.out = out;
		  this.in  = in;
		  this.isAvailable = true;
	  }
  }
  
  public Buffet(String name, int qttEntries, int qttPlaces) {
    super(name);

    inPorts = new ArrayList<Sim_port>();
    // Receive Customers from Source
    for (int i = 0; i < qttEntries; i++) {
    	Sim_port in = new Sim_port(PREFIX_IN_PLACE + "InCustomer" + i);
    	add_port(in);
    	inPorts.add(in);
	}

    portPlaces = new ArrayList<BuffetPlaceConnection>();
    for (int i = 0; i < qttPlaces; i++) {
    	Sim_port port_in_place = new Sim_port(PREFIX_IN_PLACE + i); // Will tell that the machine is available
		Sim_port port_out_place = new Sim_port(PREFIX_OUT_PLACE + i); //Will pass the customer
		
		add_port(port_in_place);
		add_port(port_out_place);
		portPlaces.add( new BuffetPlaceConnection(i, port_in_place, port_out_place) );
	}
    
  }
  
  public ArrayList<Sim_port> getInPorts(){
	  return inPorts;
  }
  
  public ArrayList<BuffetPlaceConnection> getBuffetPlacePorts(){
	  return portPlaces;
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
      
      if( areBuffetPlacesAvailable() ){
    	  sim_get_next(e);
      }else{
          //First gather all buffet places responses
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
    	  sim_trace(1, "Customer " + cust.id + " is serving food");
    	  
		  // Check buffet places
		  for (BuffetPlaceConnection buffetPlaceConnection : portPlaces) {
			  if( buffetPlaceConnection.isAvailable ){
				sim_trace(1,"Customer " + cust.id + " being sent to place " + buffetPlaceConnection.id);
				sim_completed(e);
				sim_schedule(buffetPlaceConnection.out, 0.0, 0, cust);
				buffetPlaceConnection.isAvailable = false;
				break;
			  }
		  }
    	  
      }else{ //then it´s a signal from the buffet places stating that they are available
    	  for (BuffetPlaceConnection buffetPlaceConnection : portPlaces) {
			if(e.from_port(buffetPlaceConnection.in)){ //If comes from this specific machine
				sim_trace(1, "Signal received from Place " + buffetPlaceConnection.id);
				sim_completed(e);
				buffetPlaceConnection.isAvailable = true;
				break;
			}
    	  }
      }
      
    }
    
  }
  
  private boolean areBuffetPlacesAvailable(){
  	for (BuffetPlaceConnection buffetPlaceConnection : portPlaces) {
			if( buffetPlaceConnection.isAvailable ){
				return true;
			}
		}
  	return false;
  }
}