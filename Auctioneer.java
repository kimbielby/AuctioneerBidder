import java.net.*;
import java.io.*;
import java.util.*;

public class Auctioneer{
	// Instance Variables
    private Random rnd;
    private ServerSocket in_ss;
    private Socket in_soc;
    private Socket out_soc;
	FileWriter bidFile;
	PrintWriter printWriter;

	// Assigning Local Host address
    String	localhost = "127.0.0.1";

	// Integer Variables
    int in_port;
    int out_port;
	int the_bid;
	int current_bid;
	int roundTrips;
	int maxNumBids;

	/**
	 * Empty constructor
	 */
	public Auctioneer(){

	}

	/**
	 * Main constructor
	 */
	public Auctioneer (int inPor, int outPor, int maxT){
		rnd = new Random();
		this.in_port = inPor;
		this.out_port = outPor;
		this.maxNumBids=maxT;
		the_bid = 15;

		System.out.println("Auctioneer: " +in_port+ " of distributed auction is active ....");

		createBidFile();
		System.out.println("Auctioneer: " +in_port+ " -  STARTING AUCTION  with price = "+the_bid);
		while (roundTrips<maxNumBids){
			letsPause();
			createNodeSockOut();
			letsPause();
			closeNodeSockOut();
			createServerSock();
			acceptServerSock();
			letsPause();
			closeServerSock();
			roundTrips++;
		}
	 }

	/**
	 * Creates bid.txt file used to keep track of the bid
	 */
	private void createBidFile(){
		try {
			bidFile = new FileWriter("bid.txt", false);
			PrintWriter printWriter = new PrintWriter(bidFile, true);
			printWriter.print(the_bid);
			printWriter.close();
			bidFile.close();
		}
		catch (IOException e) {
			System.out.println("Cannot create the file "+e);
		}
	}

	/**
	 * Puts current thread to sleep for 1,000 milliseconds
	 */
	private void letsPause(){
		try{
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			System.out.println("Sleep interrupted: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect value entered: "+e);
		}
	}

	/**
	 * Creates a new out Socket with the localhost and the outgoing port number
	 */
	private void createNodeSockOut(){
		try {
			out_soc = new Socket(localhost, out_port);
		}
		catch (UnknownHostException e){
			System.out.println("Invalid IP address provided: "+e);
		}
		catch (IOException e){
			System.out.println("Socket creation failed: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect Port number: "+e);
		}
		sockConnectSuccess();
	}

	/**
	 * Checks that the out socket connected correctly
	 */
	private void sockConnectSuccess(){
		try {
			if (out_soc.isConnected()){
				System.out.println("Auctioneer: " +in_port+ " :: sent token to "+out_port);
			}
		}
		catch (Exception e){
			System.out.println("Could not connect to "+out_port+": Token not sent.");
		}
	}

	/**
	 * Closes the out socket, pauses then calls sockCloseSuccess method to
	 * 	check it closed correctly
	 */
	private void closeNodeSockOut(){
		try {
			out_soc.close();
		}
		catch (IOException e){
			System.out.println("Socket failed to close: "+e);
		}
		letsPause();
		sockCloseSuccess();
	}

	/**
	 * Checks the out socket has closed successfully
	 */
	private void sockCloseSuccess(){
		try {
			if (out_soc.isClosed()){
				System.out.println("Socket to bidder "+out_port+" is now closed.");
				System.out.println("Token has been passed successfully.");
			}
		}
		catch (Exception e){
			System.out.println("** Socket to first bidder "+out_port+" is still open **");
		}
	}

	/**
	 * Creates a new in Server Socket with the inwards port number
	 */
	private void createServerSock(){
		try {
			in_ss = new ServerSocket(in_port);
			System.out.println("Auctioneer's socket (Local Host: "+localhost+", Port number: "+in_port+") is listening....");
		}
		catch (IOException e){
			System.out.println("Socket could not be opened: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect port number: "+e);
		}
	}

	/**
	 * The in socket number accepts the Server Socket connection
	 */
	private void acceptServerSock(){
		try {
			in_soc = in_ss.accept();
			System.out.println("Auctioneer ("+in_port+ ") has received the token back");
		}
		catch (IOException e){
			System.out.println("Connection could not be made: "+e);
		}
	}

	/**
	 * Closes the Server Socket connection then pauses
	 */
	private void closeServerSock(){
		try {
			in_ss.close();
			System.out.println("Auctioneer: " +in_port+ " :: received token back");
		}
		catch (IOException e){
			System.out.println("Cannot close Server Socket: "+e);
		}
		letsPause();
	}

	/**
	 * Getter for the current bid amount
	 */
	public synchronized int getThe_bid(){
		try {
			BufferedReader br = new BufferedReader (new FileReader("bid.txt"));
			current_bid = Integer.parseInt(br.readLine());
		}
		catch (FileNotFoundException e){
			System.out.println("Could not access file bid.txt: "+e);
		}
		catch (IOException e){
			System.out.println("Could not read file bid.txt: "+e);
		}
		return current_bid;
	}

	 /**
	  * Setter for the new bid amount
	  */
	public synchronized void setThe_bid(int newBid){
		 try {
			 this.the_bid = newBid;
			 bidFile = new FileWriter("bid.txt", false);
			 printWriter = new PrintWriter(bidFile, true);
			 printWriter.println(the_bid);
			 printWriter.close();
			 bidFile.close();
			 try {
				 Thread.sleep(1000);
			 }
			 catch (InterruptedException e){
				 System.out.println("Sleep interrupted: "+e);
			 }
			 catch (IllegalArgumentException e){
				 System.out.println("Incorrect value entered: "+e);
			 }
		 }
		 catch (IOException e){
			System.out.println("Could not update the current bid value: "+e);
		 }
	}

	/**
	 * Main method
	 * Checks there are correct number of arguments provided
	 */
    public static void main (String[] args){
		if (args.length != 3) {
	    	System.out.print("Usage: Auctioneer [port number] [forward port number] [max bids allowed]");
	    	System.exit(1);
		}
    	Auctioneer a = new Auctioneer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}

