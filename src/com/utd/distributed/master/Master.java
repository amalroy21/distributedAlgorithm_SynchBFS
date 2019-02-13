package com.utd.distributed.master;

import java.util.HashMap;
import com.utd.distributed.process.Process;

public class Master implements Runnable{

   private int processCount;
   private Process[] p;
   private volatile HashMap<Integer,Boolean> roundDetails = new HashMap<>();
   private boolean masterDone = false;
   private boolean treeDone = false;
   
   public Master(int processCount){
		this.processCount = processCount;
		for(int i = 0; i < processCount; i++){
			roundDetails.put(i, false);
		}
	}
   
   // Passing the Reference of the Process
   public void setProcesses(Process[] p){
		this.p = p;
		for(int i = 0; i < processCount; i++){
			//p[i].setProcessNeighbors(p);
		}
   }
   
   @Override
   public void run() {
		// TODO Auto-generated method stub
		System.out.println("Master started");
		
		while (!roundDone()) {
			//System.out.println("round done1");
			// Waiting till all the Processes have started
		}
		resetRoundDetails();
		initiateProcesses();
		while (!roundDone()) {
			//System.out.println("round done 2");
			//Initiating the messages from all the Processes
		}
		resetRoundDetails();
		startRound();
		while(!masterDone){
			while (!roundDone()) {
				//System.out.println("round done 3");
				// Waiting till all the Processes complete one round
			}
			while(!treeDone){
				resetRoundDetails();
				startRound();
				while(!roundDone()){
					// Waiting till all the Processes complete one round
				}
			}
			resetRoundDetails();
			getParents();
			while(!roundDone()){
				// Waiting for all Processes to send parents
			}
			printTree();
			stopMasterProcess();
			masterDone = true;
		}
	}
   
   
	
	// Sending Terminate message to all the Processes
	public void stopMasterProcess(){
		for(int i = 0; i < processCount; i++){
			p[i].setMessageFromMaster("MasterDone");
		}
	}
	
	// Collecting round completion information from each Process
	public synchronized void roundCompletionForProcess(int id){
		roundDetails.put(id, true);
	}
	
	// Constructing and printing the Shortest Paths Tree
	public void printTree(){
		System.out.println("Asynch BFS Algorithm Executed !!");
		int result[][] = new int[processCount][processCount];
		for(int i = 0; i < result.length; i++){
			for(int j = 0; j < result[0].length;j++){
				result[i][j] = -1;
			}
		}
		/*for(Integer id : parents.keySet()){
			if(id == parents.get(id).parent){
				result[id][parents.get(id).parent] = -1;
				result[parents.get(id).parent][id] = -1;
			}else{
			result[id][parents.get(id).parent] = parents.get(id).weight;
			result[parents.get(id).parent][id] = parents.get(id).weight;
			}
		}*/
		
		System.out.println("Following is the resultant BFS Tree as a Adjacency List: ");
		System.out.println("Process"+"\t"+"Neighbours");
		for (int i = 0; i < processCount; i++) {
			System.out.print(i+"\t");
			for (int j = 0; j < processCount; j++) {
				if(result[i][j] != - 1){
				System.out.print(j+"\t");//+" : "+result[i][j] + "\t");
				}
			}
			System.out.println();
		}
		
	}
	
	
	// Request for parent Process for each Process for building the Shortest
			// path tree
	private void getParents() {
		for (int i = 0; i < processCount; i++) {
			p[i].setMessageFromMaster("SendParent");
		}
	}
	
	// To start next round
	private void startRound() {
		for (int i = 0; i < processCount; i++) {
			p[i].setMessageFromMaster("StartRound");
		}
	}
	
	// To start next round
	private void initiateProcesses() {
		for (int i = 0; i < processCount; i++) {
			p[i].setMessageFromMaster("Initiate");
		}
	}
	
	// Reset the Round confirmation after each round
	private void resetRoundDetails(){
		for(int i = 0; i < processCount; i++){
			roundDetails.put(i, false);
		}
	}
	
	// To check the completion of the Round
	private boolean roundDone(){
		//System.out.println("round done entered");
		for(boolean b : roundDetails.values()){
			if(!b){
				return false;
			}
		}
		
		return true;
	}
}