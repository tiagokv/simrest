# simrest
Simulation of Restaurant using SimJava

This is a code that simulates a restaurant using the library SimJava.
The classes are disposed in the following structure:
- Source: Sends events containing Customers to the class Processor
- Processor: Represents a Cashier.
  - If the customer uses a pre-paid ticket to pay the lunch, it goes directly to buffet
  - If one uses a debit card, then it is forwarded to the Payment Machine
- PaymentMachine: Holds the customer for a time according to a distribution and then sends the customer to the buffet
- Buffet and BuffetPlaces: Utilized as a Master-Slave architecture, and Buffet class controls which places are available
- Table and TablePlaces: Same logic as above
