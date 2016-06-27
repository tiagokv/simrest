package sms.simrest.entities;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.Sim_gamma_obj;
import eduni.simjava.distributions.Sim_negexp_obj;

public class PaymentMachine extends Sim_entity {

	private Sim_port in;
	private Sim_port out_processor;
	private Sim_port out_buffet;
	private Sim_gamma_obj delay;
	
	public PaymentMachine(String name, double scale, double shape) {
		super(name);
		
		in = new Sim_port("In_" + name);
		out_processor = new Sim_port("Out_" + name + "_Proc" );
		out_buffet = new Sim_port("Out_" + name + "_Buffet");
		
		add_port(in);
		add_port(out_buffet);
		add_port(out_processor);
		
		delay = new Sim_gamma_obj("NegExp_" + name, scale, shape);
		add_generator(delay);
	}
		
	public Sim_port getInPort() {
		return in;
	}

	public Sim_port getOutProcessorPort() {
		return out_processor;
	}

	public Sim_port getOutBuffetPort() {
		return out_buffet;
	}

	public void body(){
		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			
			sim_get_next(e);
			if(e.get_data() != null)
				sim_trace(1, "Payment Machine " + get_name() + " received Customer " + ((Customer)e.get_data()).id);
			sim_process(delay.sample()); // draw from probability
			sim_completed(e);
			
			if(e.get_data() != null )
				sim_trace(1, "Payment Machine " + get_name() + " is done with Customer " + ((Customer)e.get_data()).id);
			
			sim_schedule(out_processor, 0.0, 0);
//			sim_schedule(out_buffet, 0, 0, e.get_data());
		}
	}

}
