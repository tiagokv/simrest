package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_normal_obj;
import eduni.simjava.distributions.Sim_random_obj;

//The class for the processor
public class Processor extends Sim_entity {
  private Sim_port in, out1, out2;
  private Sim_normal_obj delay;
  private Sim_random_obj prob;
  private Sim_stat stat;

  public Processor(String name, double mean, double var) {
    super(name);
    // Port for receiving events from the source
    in = new Sim_port("In");
    // Port for sending events to disk 1
    out1 = new Sim_port("Out1");
    // Port for sending events to disk 2
    out2 = new Sim_port("Out2");
    add_port(in);
    add_port(out1);
    add_port(out2);
    
    delay = new Sim_normal_obj("Delay", mean, var);
    prob = new Sim_random_obj("Probability");
    add_generator(delay);
    add_generator(prob);
    
    stat = new Sim_stat();
    stat.add_measure(Sim_stat.THROUGHPUT);
    stat.add_measure(Sim_stat.RESIDENCE_TIME);
    set_stat(stat);
    
  }

  public void body() {
    while (Sim_system.running()) {
      Sim_event e = new Sim_event();
      // Get the next event
      sim_get_next(e);
      // Process the event
      sim_process(delay.sample());
      // The event has completed service
      sim_completed(e);
      
      double p = prob.sample();
      if (p < 0.60) {
    	sim_trace(1, "Disk1 selected for I/O work.");
        // Even I/O jobs go to disk 1
        sim_schedule(out1, 0.0, 1);
      } else {
    	sim_trace(1, "Disk2 selected for I/O work.");
        // Odd I/O jobs go to disk 2
        sim_schedule(out2, 0.0, 1);
      }
    }
  }
}