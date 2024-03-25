Peer To Peer Chat System: 

By: Loay Ahmed Salem	222929 


A QUICK FEATURE THAT ISN'T IMMEDIATELY OBVIOUS WHEN RUNNING THE PROGRAM, IS THAT IF YOU WRITE "exit" without quotes it will unregister the peer

 

Topology used:	Ring-Star Hybrid topology 

 

All Peers' messages are sent through the ring (a peer sends a message to the peer in front of it, the peer in front of it sends it to the peer in front of it and so on, creating a ring).
The coordinator’s only job is to assign new peers their spot in the ring, remove existing peers from the ring and manage the ring making sure everything is working smoothly.
The coordinator as a centralized piece instead of part of the ring makes it easier for it to do its job, it makes it easier for it to figure out which part of the ring is faulty (if there is a fault),
in case of failure in the ring it makes it easier for peers to reach out for the coordinator for recalibration. 

 

2 entities: Peers And Coordinator 

Peers functionalities: 

Send Private messages to other peers: Peer Ecrypts the message to encryption that only one other peer can decrypt and sends it to the peer in front, the peer infront tries to decrypt fails so it sends it to the peer infront of it and so on till one specific peer receives it tries to decrypt and succeeds. 

Send Public messages to other peers: Peer sends an unencrypted message to the peer infront of it, the peer infront sends the message to the peer infront of it and so on till the message goes a full loop back to its original sender. 

A peer can ask the coordinator to Leave: the peer connects to the coordinator and sends him a message asking to leave. 

Coordinator functionalities:  

Unregister Peers: The coordinator removes the peer from the ring by disconnecting the connection it has with the peer in front of it and removing the connection that the peer behind it has with it and reconnecting the peer behind it to the peer infront of it. 

Register Peers: it creates a peer and if the ring is empty it leaves it without a connection if not, it connects the peer with the last peer in registered with the new peer and connects the new peer with the first peer registered. 

Advantages and disadvantages: 

Advantages: 

Fault tolerant: in case of failure in the ring it makes it easier for peers to reach out for the coordinator for recalibration.  

Easy to implement 

Reduced Network Congestion 

Efficient data transfer (eliminates chances of data collision) 

Reduces the reliability of the centralized coordinator in case of a star topology (Coordinator fails everything is dead) 

Reduces the pressure on the centralized coordinator in case of a star topology 

Disadvantages: 

Limited Scalability: the more devices there is the slower the messages are sent 

Single Point of failure (Mitigated greatly by having a centralized Coordinator that can easily fix things up) 
