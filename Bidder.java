import java.net.*;
import java.io.*;
import java.util.*;

public class Bidder{
	// Instance Variables
    private Random rnd;
    private ServerSocket in_ss;
    private Socket in_soc;
    private Socket out_soc;
	Auctioneer auctioneer;

	// Assigning Local Host address
	String localhost = "127.0.0.1";

	// Integer Variables
    int in_port;
    int out_port;
	int the_bid;

	/**
	 * Constructor
	 * Calls to create Server Socket first then enters the while loop
	 */
    public Bidder (int inPor, int outPor){
		rnd = new Random();
		in_port = inPor;
		out_port = outPor;

		auctioneer = new Auctioneer();
		createServerSock();

		while (true){
			acceptServerSock();
			letsPause();
			bidding();
			letsPause();
			closeServerSock();
			createNodeSockOut();
			letsPause();
			closeNodeSockOut();
		}
    }

	/**
	 * Creates an inwards Server Socket using the inwards port number
	 */
	private void createServerSock(){
		try {
			in_ss = new ServerSocket(in_port);
			System.out.println("Bidder: " +in_port+ " of distributed auction is active ....");
		}
		catch (IOException e){
			System.out.println("Could not create Server Socket for Bidder "+in_port+": "+e);
		}
	}

	/**
	 * Inwards socket number accepts the connection with the Server Socket
	 */
	private void acceptServerSock(){
		try {
			in_soc = in_ss.accept();
			System.out.println("Bidder: " + in_port + " has received the token.");
		}
		catch (IOException e){
			System.out.println("Connection could not be made: "+e);
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
	 * Calls to get the current bid amount
	 */
	private void bidding(){
		try {
			the_bid = auctioneer.getThe_bid();
			System.out.println("Bidder: "+in_port+" is reading the bid file");
		}
		catch (Exception e){
			System.out.println("Could not read file bid.txt: "+e);
		}

		// To bid or not to bid...
		try {
			// Generate a random integer of either 0 or 1
			int randNum = rnd.nextInt(2);

			if (randNum == 1){
				the_bid += 10;
				auctioneer.setThe_bid(the_bid);
				System.out.println("Bidder: "+in_port+" -  my bid is "+the_bid);
			}
			else {
				System.out.println("Bidder "+in_port+": no bid");
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Could not generate random number: "+e);
		}
	}

	/**
	 * Closes the inwards Server Socket connection
	 */
	private void closeServerSock(){
		try {
			in_soc.close();
			System.out.println("Socket is now closed");
		}
		catch (IOException e){
			System.out.println("Cannot close Server Socket: "+e);
		}
		letsPause();
	}

	/**
	 * Creates an out Socket with the local host address and the outwards port number
	 * Then checks if connection was successful
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
	 * Checks if out socket is connected
	 */
	private void sockConnectSuccess(){
		try {
			if (out_soc.isConnected()){
				System.out.println("Socket to Node "+out_port+" connected okay ");
			}
		}
		catch (Exception e){
			System.out.println("Could not connect to "+out_port+": Token not sent.");
		}
	}

	/**
	 * Closes the out socket connection, pauses then checks it was closed successfully
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
	 * Checks out socket connection was closed successfully
	 */
	private void sockCloseSuccess(){
		try {
			if (out_soc.isClosed()){
				System.out.println("Socket to Node "+out_port+" is now closed.");
				System.out.println("Token has been passed successfully.");
			}
		}
		catch (Exception e){
			System.out.println("** Socket to first bidder "+out_port+" is still open **");
		}
	}

	/**
	 * Main method
	 * Checks there are correct number of arguments provided
	 */
    public static void main (String[] args){
	if (args.length != 2) {
	    System.out.print("Usage: Bidder [port number] [forward port number]");
	    System.exit(1);
	}
    	Bidder b = new Bidder(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}

