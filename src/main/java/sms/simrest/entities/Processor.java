package sms.simrest.entities;

import java.util.ArrayList;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_predicate;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_gamma_obj;
import eduni.simjava.distributions.Sim_normal_obj;
import eduni.simjava.distributions.Sim_random_obj;

//The class for the processor
public class Processor extends Sim_entity {
  private Sim_port in;
  private Sim_gamma_obj delay;

  private ArrayList<PaymMachineConnection> portMachines;
  
  public final static String PREFIX_IN_MACHINE = "In_PaymMachine";
  public final static String PREFIX_OUT_MACHINE = "Out_PaymMachine";
  
  public class PaymMachineConnection{
	  public int id;
	  public Sim_port out;
	  public Sim_port in;
	  public Boolean isAvailable;
	  
	  public PaymMachineConnection(int id, Sim_port in, Sim_port out){
		  this.id = id;
		  this.out = out;
		  this.in  = in;
		  this.isAvailable = true;
	  }
  }
  
  public Processor(String name, int qttMachines, double scale, double shape) {
    super(name);
    // Receive Customers from Source
    in = new Sim_port("InCustomer");
    add_port(in);
    
    portMachines = new ArrayList<PaymMachineConnection>();
    for (int i = 0; i < qttMachines; i++) {
    	Sim_port port_in_machine = new Sim_port(PREFIX_IN_MACHINE + i); // Will tell that the machine is available
		Sim_port port_out_machine = new Sim_port(PREFIX_OUT_MACHINE + i); //Will pass the customer
		
		add_port(port_in_machine);
		add_port(port_out_machine);
		portMachines.add( new PaymMachineConnection(i, port_in_machine, port_out_machine) );
	}

    delay = new Sim_gamma_obj("Gamma", scale, shape);
    add_generator(delay);

  }
  
  public ArrayList<PaymMachineConnection> getPaymMachinePorts(){
	  return portMachines;
  }

  public void body() {
    while (Sim_system.running()) {
      Sim_event e = new Sim_event();

      //First gather all payment machine responses
      sim_select( new Sim_predicate() {
		@Override
		public boolean match(Sim_event arg0) {
			return !arg0.from_port(in); //is not a customer
		}
      }, e);
      
      //No machine event was found
      if( e.get_tag() == -1 ){
          //Simulate priority queue, first Tickets then machine payments
          sim_select( new Sim_predicate() {
    		@Override
    		public boolean match(Sim_event arg0) {
    			if( arg0.get_data() instanceof Customer ){
    				return ((Customer)arg0.get_data()).isTicket;
    			}
    			
    			return false;
    		}
          }, e);
          
          if( e.get_tag() == -1 && arePaymMachinesAvailable() ){ //There was no ticket in queue
        	  sim_get_next(e);
          }
      }
      
      //Is a new customer coming?
      if( e.from_port(in) && e.get_data() != null && e.get_data() instanceof Customer ){
    	  Customer cust = (Customer) e.get_data();
    	  sim_trace(1, "Customer " + cust.id + " uses Ticket? " + cust.isTicket);
    	  
    	  if( cust.isTicket ){
    		  sim_process(delay.sample());
    		  sim_completed(e);
    	  }else{
    		  // Check paym. machines
    		  for (PaymMachineConnection paymMachineConnection : portMachines) {
    			  if( paymMachineConnection.isAvailable ){
    				sim_trace(1,"Customer " + cust.id + " being sent to machine " + paymMachineConnection.id);
  					sim_schedule(paymMachineConnection.out, 0.0, 0, cust);
  					paymMachineConnection.isAvailable = false;
  					break;
    			  }
    		  }
    	  }
      }else{ //then it´s a signal from the paym. machines stating that they are available
    	  for (PaymMachineConnection paymMachineConnection : portMachines) {
			if(e.from_port(paymMachineConnection.in)){ //If comes from this specific machine
				sim_trace(1, "Signal received from Machine " + paymMachineConnection.id);
				paymMachineConnection.isAvailable = true;
				break;
			}
    	  }
      }
      
    }
    
  }
  
  private boolean arePaymMachinesAvailable(){
  	for (PaymMachineConnection paymMachineConnection : portMachines) {
			if( paymMachineConnection.isAvailable ){
				return true;
			}
		}
  	return false;
  }
}