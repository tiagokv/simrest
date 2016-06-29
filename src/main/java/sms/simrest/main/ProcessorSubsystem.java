package sms.simrest.main;

import java.util.ArrayList;

import eduni.simjava.Sim_system;
import sms.simrest.entities.*;

public class ProcessorSubsystem {
    // The main method
    public static void main(String[] args) {
	    Sim_system.initialise();
	    Sim_system.set_trace_detail(false, true, false);
	    
	    
	    if( args.length % 2 == 1){
	    	System.err.println("Arguments are invalid or missing.");
	    }
	    
	    int qttMachines = 2;
	    int qttBuffetPlaces = 12;
	    int qttTablePlaces = 100;
	    for (int i = 0; i < args.length; i++) {
			if( args[i].equalsIgnoreCase("--machines") && args.length > i+1){
				qttMachines = Integer.parseInt( args[i+1] );
			}
			
			if( args[i].equalsIgnoreCase("--buffet") && args.length > i+1){
				qttBuffetPlaces = Integer.parseInt( args[i+1] );
			}
			
			if( args[i].equalsIgnoreCase("--places") && args.length > i+1){
				qttTablePlaces = Integer.parseInt( args[i+1] );
			}
		}
	    
	    Source source = new Source("Source", 86757.0, 0.19674);
	    Processor processor = new Processor("Processor", qttMachines, 90213.0, 0.18943);
	    
	    Sim_system.link_ports("Source", "Out", "Processor", "InCustomer");
	    
	    Buffet buffet = new Buffet("Buffet", qttMachines + 1, qttBuffetPlaces); //Plus one from ticket
	    
	    Sim_system.link_ports("Processor", "OutBuffet", buffet.get_name(), buffet.getInPorts().get(0).get_pname());
	    
	    Table table = new Table("Table", qttBuffetPlaces, qttTablePlaces);
	    
	    ArrayList<PaymentMachine> paymentMachines = new ArrayList<PaymentMachine>();
	    for (int i = 0; i < qttMachines; i++) {
	    	PaymentMachine paymMach = new PaymentMachine("PaymMachine" + i, 18212, 0.907);   	
	    	
	    	paymentMachines.add(paymMach);
	    	
			Sim_system.link_ports(processor.get_name(), processor.getPaymMachinePorts().get(i).out.get_pname(), 
								  paymMach.get_name(), paymMach.getInPort().get_pname() );
			
			Sim_system.link_ports(processor.get_name(), processor.getPaymMachinePorts().get(i).in.get_pname(), 
								  paymMach.get_name(), paymMach.getOutProcessorPort().get_pname() );
			
			Sim_system.link_ports(paymMach.get_name(), paymMach.getOutBuffetPort().get_pname(), 
								  buffet.get_name(), buffet.getInPorts().get(i+1).get_pname());
			
		}
	    
	    ArrayList<BuffetPlace> buffetPlaces = new ArrayList<BuffetPlace>();
	    for (int i = 0; i < qttBuffetPlaces; i++) {
	    	BuffetPlace buffetPlace = new BuffetPlace("BuffetPlace" + i, 107500.0, 1594963969.0);   	
	    	
	    	buffetPlaces.add(buffetPlace);
	    	
			Sim_system.link_ports(buffet.get_name(), buffet.getBuffetPlacePorts().get(i).out.get_pname(), 
					buffetPlace.get_name(), buffetPlace.getInPort().get_pname() );
			
			Sim_system.link_ports(buffet.get_name(), buffet.getBuffetPlacePorts().get(i).in.get_pname(), 
					buffetPlace.get_name(), buffetPlace.getOutProcessorPort().get_pname() );
			
			Sim_system.link_ports(buffetPlace.get_name(), buffetPlace.getOutTabletPort().get_pname(), 
								  table.get_name(), table.getInPorts().get(i).get_pname() );
		}
	    
	    ArrayList<TablePlace> tablePlaces = new ArrayList<TablePlace>();
	    for (int i = 0; i < qttTablePlaces; i++) {
	    	TablePlace tablePlace = new TablePlace("TablePlace" + i, 1190500.0, 175460454400.0);   	
	    	
	    	tablePlaces.add(tablePlace);
	    	
			Sim_system.link_ports(table.get_name(), table.getTablePlacePorts().get(i).out.get_pname(), 
					tablePlace.get_name(), tablePlace.getInPort().get_pname() );
			
			Sim_system.link_ports(table.get_name(), table.getTablePlacePorts().get(i).in.get_pname(), 
					tablePlace.get_name(), tablePlace.getOutTablePort().get_pname() );

		}
	    
	    
	    Sim_system.generate_graphs(true);
	    Sim_system.run();
    }
  }
