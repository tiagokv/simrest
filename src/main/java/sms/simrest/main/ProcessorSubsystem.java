package sms.simrest.main;

import java.util.ArrayList;

import eduni.simjava.Sim_system;

import sms.simrest.entities.*;

public class ProcessorSubsystem {
    // The main method
    public static void main(String[] args) {
	    Sim_system.initialise();
	    Sim_system.set_trace_detail(false, true, false);
	    
	    // 1594963969σ2=Var(X)=1594963969 
	    
	    if( args.length % 2 == 1){
	    	System.err.println("Arguments are invalid or missing.");
	    }
	    
	    int qttMachines = 2;
	    for (int i = 0; i < args.length; i++) {
			if( args[i].equalsIgnoreCase("--machines") && args.length > i+1){
				qttMachines = Integer.parseInt( args[i+1] );
			}
		}
	    
	    Source source = new Source("Source", 86757.0, 0.19674);
	    Processor processor = new Processor("Processor", qttMachines, 90213.0, 0.18943);
	    
	    Sim_system.link_ports("Source", "Out", "Processor", "InCustomer");
	    // Buffet buffet = new Buffet ...
	    ArrayList<PaymentMachine> paymentMachines = new ArrayList<PaymentMachine>();
	    for (int i = 0; i < qttMachines; i++) {
	    	PaymentMachine paymMach = new PaymentMachine("PaymMachine" + i, 18212, 0.907);   	
	    	
	    	paymentMachines.add(paymMach);
	    	
			Sim_system.link_ports(processor.get_name(), processor.getPaymMachinePorts().get(i).out.get_pname(), 
								  paymMach.get_name(), paymMach.getInPort().get_pname() );
			
			Sim_system.link_ports(processor.get_name(), processor.getPaymMachinePorts().get(i).in.get_pname(), 
								  paymMach.get_name(), paymMach.getOutProcessorPort().get_pname() );
			
//			Sim_system.link_ports(buffet.get_name(), buffet.getPaymMachinePorts().get(i).in.get_pname(), 
//					  paymMach.get_name(), paymMach.getOutProcessorPort().get_pname() );
			
		}
	    
	    Sim_system.generate_graphs(true);
	    Sim_system.run();
    }
  }
