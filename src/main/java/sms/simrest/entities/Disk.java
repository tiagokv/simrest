package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_normal_obj;

//The class for the two disks
public class Disk extends Sim_entity {
  private Sim_port in;
  private Sim_normal_obj delay;
  private Sim_stat stat;

  public Disk(String name, double mean, double var) {
    super(name);
    // Port for receiving events from the processor
    in = new Sim_port("In");
    add_port(in);
    
    delay = new Sim_normal_obj("Delay", mean, var);
    add_generator(delay);
    
    stat = new Sim_stat();
    stat.add_measure(Sim_stat.UTILISATION);
    stat.add_measure(Sim_stat.QUEUE_LENGTH);
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
    }
  }
}