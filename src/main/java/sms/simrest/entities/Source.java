package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.distributions.Sim_gamma_obj;
import eduni.simjava.distributions.Sim_negexp_obj;
import eduni.simjava.distributions.Sim_random_obj;

public class Source extends Sim_entity {
    private Sim_port out;
    private Sim_gamma_obj delay;
    private Sim_random_obj random;
    private Sim_stat stat;

    public Source(String name, double scale, double shape) {
      super(name);
      // Port for sending events to the processor
      out = new Sim_port("Out");
      add_port(out);
      
      delay = new Sim_gamma_obj("arrival", scale, shape);
      random = new Sim_random_obj("random");
      
      add_generator(delay);
      add_generator(random);
      
      stat = new Sim_stat();
      stat.add_measure(Sim_stat.THROUGHPUT);
      set_stat(stat);
    }

    public void body() {
      for (int i=0; i < 100; i++) {
    	  
    	Customer customer = new Customer(i);
    	
    	double sample = random.sample();
    	
    	if (sample <= 0.08333333333) {
    		customer.isTicket = true;
    	} else {
    		customer.isTicket = false;
    	}
    	sim_trace(1, "Customer " + customer.id + " will pay with " + (customer.isTicket? "Ticket": "Machine" ));
        // Send the processor a job
        sim_schedule(out, 0.0, 0, customer);
        // Pause
        sim_pause(delay.sample());
      }
    }
  }
