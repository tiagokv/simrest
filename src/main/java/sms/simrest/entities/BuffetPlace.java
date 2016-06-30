package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_normal_obj;

public class BuffetPlace extends Sim_entity {

	private Sim_port in;
	private Sim_port out_buffet;
	private Sim_port out_table;
	private Sim_normal_obj duration;
	private Sim_stat stat;
	
	public BuffetPlace(String name, double mean, double variance) {
		super(name);
		
		in = new Sim_port("In_" + name);
		out_buffet = new Sim_port("Out_" + name + "_Buffet" );
		out_table = new Sim_port("Out_" + name + "_Table");
		
		add_port(in);
		add_port(out_table);
		add_port(out_buffet);
		
		duration = new Sim_normal_obj("buffetDuration", mean, variance);
	    add_generator(duration);
	    
		stat = new Sim_stat();
		stat.add_measure(Sim_stat.THROUGHPUT);
		stat.add_measure(Sim_stat.RESIDENCE_TIME);
		set_stat(stat);
	}
		
	public Sim_port getInPort() {
		return in;
	}

	public Sim_port getOutProcessorPort() {
		return out_buffet;
	}

	public Sim_port getOutTabletPort() {
		return out_table;
	}

	public void body(){
		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			
			sim_get_next(e);
			if(e.get_data() != null)
				sim_trace(1, "Buffet Place " + get_name() + " received Customer " + ((Customer)e.get_data()).id);
			
			double h = duration.sample();
			sim_process((h<0)?0:h); // draw from probability
			sim_completed(e);
			
			if(e.get_data() != null )
				sim_trace(1, "Buffet Place " + get_name() + " is done with Customer " + ((Customer)e.get_data()).id);
			
			sim_schedule(out_buffet, 0.0, 9);
			sim_schedule(out_table, 0, 0, e.get_data());
		}
	}

}
