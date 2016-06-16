package sms.simrest.main;

import eduni.simjava.Sim_system;

import sms.simrest.entities.*;

public class ProcessorSubsystem {
    // The main method
    public static void main(String[] args) {
	    Sim_system.initialise();
	    Sim_system.set_trace_detail(false, true, false);
	    
	    Source source = new Source("Source", 0.19674, 86757.0); //não sei se ta certo
	    Processor processor = new Processor("Processor", 110.5, 90.5);
	    Disk disk1 = new Disk("Disk1", 130.0, 65.0);
	    Disk disk2 = new Disk("Disk2", 350.5, 200.5);
	    Sim_system.link_ports("Source", "Out", "Processor", "In");
	    Sim_system.link_ports("Processor", "Out1", "Disk1", "In");
	    Sim_system.link_ports("Processor", "Out2", "Disk2", "In");
	    Sim_system.run();
    }
  }
