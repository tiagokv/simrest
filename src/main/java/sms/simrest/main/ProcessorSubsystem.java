package sms.simrest.main;

import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_gamma_obj;
import eduni.simjava.distributions.Sim_logistic_obj;
import eduni.simjava.distributions.Sim_negexp_obj;
import eduni.simjava.distributions.Sim_normal_obj;
import eduni.simjava.distributions.Sim_random_obj;
import sms.simrest.entities.*;

public class ProcessorSubsystem {
    // The main method
    public static void main(String[] args) {
	    Sim_system.initialise();
	    Sim_system.set_trace_detail(false, true, false);
	    
	    
	    
	    //Sim_logistic_obj logistic = new Sim_logistic_obj("logistic", 107250.0, 22018); //buffet
	    //Sim_negexp_obj exponential = new Sim_negexp_obj("exponential", 0.5976); //payment
	    
	    
	    Sim_random_obj random = new Sim_random_obj("random"); 
	    Sim_normal_obj normal = new Sim_normal_obj("normal", 107500.0, 1594963969.0); //buffet
	    Sim_gamma_obj gamma = new Sim_gamma_obj("gamma", 86757.0, 0.19674); //arrival
	    Sim_gamma_obj gamma2 = new Sim_gamma_obj("gamma", 18623.0, 0.89854); //payment
	    Sim_gamma_obj gamma3 = new Sim_gamma_obj("gamma", 90213.0, 0.18943); //ticket payment
	    Sim_normal_obj normal2 = new Sim_normal_obj("normal", 1190500.0, 175460454400.0); //launch time
	    
	    for(int i = 0; i < 10000; i++) {
	    	System.out.println(gamma3.sample());
	    }
	    
	    if (false) {
	    
		    
		    
		    Source source = new Source("Source", 86757.0, 0.19674);
		    Processor processor = new Processor("Processor", 110.5, 90.5);
		    Disk disk1 = new Disk("Disk1", 130.0, 65.0);
		    Disk disk2 = new Disk("Disk2", 350.5, 200.5);
		    Sim_system.link_ports("Source", "Out", "Processor", "In");
		    Sim_system.link_ports("Processor", "Out1", "Disk1", "In");
		    Sim_system.link_ports("Processor", "Out2", "Disk2", "In");
		    Sim_system.run();
	    
	    }
    }
  }
