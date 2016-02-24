#Heart Beat Protocol
This is an implementation of Perfect Failure detection and heartbeat protocol usin UDP sockets
##Introduction
This was part of distributed system research on distributed algorithms. The main aim in this sample is to demonstrate on failure detection using UDP sockets in a local machine. The two most important algorithms implemented in this project are the Perfect Failure Detection algorithm and Leader Election algorithm.
##Running the program

1. Create a new eclipse project named HeartBeatProtocol
2. Copy the content of SRC to an eclipse project (SRC directory).
3. Click on run to lauch ServerMain and provide a name for your server. You can lauch more than one server by click on run each time providing a different name.

##Algorithms

##Perfect Failure Detector
This algorithm outputs a subset of processes in the system that have clashes. When a process q is output at some time t at a process p, the q is said to be detected (of having crashed) by p. The perfect failure detector guarantees the following properties:
1. Every process that crashed is eventually permanently detected
2. No correct process is ever detected
A variation of this is the eventually strong failure detector which has a disadvantage in that is unreliable. This is because it keeps adjusting the time to detect failure which results in poor performance of the algorithm in detecting failure.

## Monarchical Eventual Leader Election (Detection)
This algorithm is used with the perfect failure detector algorithm. The failure of one not does not signify the end of execution i.e. “The death in royal family does not mean final” [CITATION Chr11 \p 57 \l 1033 ]. The algorithm maintain the set of processes that are suspected by the Perfect Failure Detector and declared the non-suspected process with the highest rank as the leader. This is due to the fact that, provided one process is correct, it will be trusted by all the other processes running in a distributed environment. 

  >Upon event (Ω, suspected)
  
    >Get set of suspected processes
    
    >Set leader to null
    
  >Upon event ( <> P, suspect| p) do
  
    >Suspected=suspected U {p}
    
  >Upon event (<>P, restore |p) do
  
    >Suspected=suspected-{p}
    
  >Upon leader not equal maxrank(π-{suspected})
  
    >Trigger (Ω, Trust | leader)
