package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.distributions.Sim_gamma_obj;
import eduni.simjava.distributions.Sim_negexp_obj;
import eduni.simjava.distributions.Sim_random_obj;

public class Source extends Sim_entity {
    private Sim_port out;
    private Sim_gamma_obj delay;
    private Sim_random_obj random;

    public Source(String name, double scale, double shape) {
      super(name);
      // Port for sending events to the processor
      out = new Sim_port("Out");
      add_port(out);
      delay = new Sim_gamma_obj("arrival", scale, shape);
      add_generator(delay);
      random = new Sim_random_obj("random");
    }

    public void body() {
      for (int i=0; i < 10; i++) {
    	  
    	Customer customer = new Customer();
    	
    	double sample = random.sample();
    	
    	if (sample <= 0.08333333333) {
    		customer.isTicket = true;
    	} else {
    		customer.isTicket = false;
    	}
    	
    	
        // Send the processor a job
        sim_schedule(out, 0.0, 0, customer);
        // Pause
        sim_pause(delay.sample());
      }
    }
  }
