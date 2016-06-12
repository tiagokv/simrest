package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.distributions.Sim_negexp_obj;

public class Source extends Sim_entity {
    private Sim_port out;
    private Sim_negexp_obj delay;

    public Source(String name, double mean) {
      super(name);
      // Port for sending events to the processor
      out = new Sim_port("Out");
      add_port(out);
      delay = new Sim_negexp_obj("Delay", mean);
      add_generator(delay);
    }

    public void body() {
      for (int i=0; i < 10; i++) {
        // Send the processor a job
        sim_schedule(out, 0.0, 0);
        // Pause
        sim_pause(delay.sample());
      }
    }
  }
