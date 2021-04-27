package CapstoneProject;

import java.util.*;

/**
 * @author Josh Janoe
 * 
 * This file is modified from the warm-up assignment to
 * search for overlap between second degree connections,
 * find degree of separation between any two nodes/users,
 * and find the connections to a given user at n degrees
 * of separation.
 * 
 *  In addition it may be interesting (or more interesting)
 *  to look at the rate of decline in mutual friends with
 *  increasing degrees of separation
 *
 */
public class CapstoneDoSGraph implements Graph {
	
	public final HashMap<Integer,HashSet<Integer>> graphMap;
	
	/*
	 * Simple constructors
	 * first basic constructor creates empty graph
	 * second constructor uses existing info to create graph with data
	 */
	public CapstoneDoSGraph() {
		graphMap = new HashMap<Integer, HashSet<Integer>>();
	}
	
	public CapstoneDoSGraph(HashMap<Integer, HashSet<Integer>> gMap) {
		graphMap = gMap;
	}
	
	/**
	 *	Method adds a new vertex/node to the graph
	 *	with empty set of connections
	 *
	 * @param num	the num/ID of the new node
	 */
	public void addVertex(int num) {
		graphMap.put(num, new HashSet<Integer>());
	}

	
	/**
	 * Method iterates over the nodes connected to a given node's designated connection
	 * and compares common connections
	 *
	 * @param user		integer representing the user ID used as the starting point
	 * @param firstDegreeConnection		integer representing the secondary connection
	 *                                  whose connections we want to compare
	 * @return 			a HashMap of all the connections of the firstDegreeConnection
	 * 					and their mutual friends with the user
	 */
	public HashMap<Integer,HashSet<Integer>> getSecondDegreeMutualFriends (	Integer user, 
																			Integer firstDegreeConnection) {
		HashMap<Integer, HashSet<Integer>> secondaryMutualFriends = new HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> sharedConnections = new HashSet<>();
		//obtain user friend list from graph
		if (graphMap.containsKey(user)) {
			HashSet<Integer> userFriends = getConnections(user);
			//obtain specified 1st degree connection friend list
			if (graphMap.containsKey(firstDegreeConnection)) {
				HashSet<Integer> secondDegreeFriends = getConnections(firstDegreeConnection);
				//for every second degree connection
				for (Integer secondDegreeFriend : secondDegreeFriends ) {
				//get list of friends
					sharedConnections = getMutualFriends(user, secondDegreeFriend);
					secondaryMutualFriends.put(secondDegreeFriend, sharedConnections);
				}													
			}
		}		
		return secondaryMutualFriends;	
	}	
	
	/**
	 * Helper method to find the degree of separation between
	 * any 2 given users
	 *
	 * @param user1		an integer representing one person's ID
	 * @param user2		an integer representing a second person's ID
	 * @return 			the degree of separtion between user1 and user2
	 */
	public int getDegreeOfSeparation (Integer user1, Integer user2){
		if (!validUserCheck(user1,user2)){
			return 0;
		}
		List<Integer> path = getPath(user1, user2);
		return path.size();
	}

	private boolean validUserCheck(Integer user1, Integer user2){
		if(!graphMap.containsKey(user1)){
			System.out.println("User 1 does not exist in network!");
			return false;
		}
		if(!graphMap.containsKey(user2)){
			System.out.println("User 2 does not exist in network!");
			return false;
		}
		return true;
	}
	
	/**
	 * Helper method to find all the connections of a
	 * given user to nth degree of separation
	 * 
	 * @param user		an integer representing one person's ID
	 * @param degreeOfSeparation
	 * 					the number of connections between the user
	 * 					and the connections at this int
	 * @return 			a list (or HashSet) of the users with
	 * 					n degrees of separation from the user
	 */	
	protected HashSet<Integer> getNDegreeConnections(Integer user, int degreeOfSeparation){
		//create list to return with all connection and n degrees of separation
		HashSet<Integer> connections = new HashSet<Integer>();
		HashSet<Integer> currConnections = getConnections(user);
		//HashSet<Integer> visited = new HashSet<Integer>();
		Queue<Integer> toVisit = new LinkedList<Integer>();
		int currDegree = 0;
		int remainingInCurrDegree = 1;

		toVisit.add(user);
		//Breadth First Search
		while (!toVisit.isEmpty() && degreeOfSeparation > currDegree){
			Integer currConnection = toVisit.remove();
			currConnections = getConnections(currConnection);
			for (Integer connection : currConnections) {
				if(!toVisit.contains(connection) && connection != user){
					toVisit.add(connection);
				}
			}
			//counter that makes new counter equal to size of toVisit list whenever the current counter = 0
			remainingInCurrDegree--;
			if (remainingInCurrDegree==0){
				remainingInCurrDegree = toVisit.size();
				currDegree++;
			}
		}
		//connections = (HashSet<Integer>) toVisit;
		for ( int i : toVisit){
			connections.add(i);
		}
		return connections;
	}

	protected List<Integer> getPath(Integer user1, Integer user2){
		//System.out.println("Finding Path...");

		if (!validUserCheck(user1,user2)){
			return null;
		}

		List<Integer> path = new ArrayList<Integer>();
		HashMap<Integer,Integer> parentMap = new HashMap<Integer,Integer>();//child,parent
		Queue<Integer> toVisit = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();

		toVisit.add(user1);
		parentMap.put(user1,null);

		while (!toVisit.isEmpty()){
			Integer currUser = toVisit.remove();
			//System.out.print(currUser+": { ");
			HashSet<Integer> currFriends = graphMap.get(currUser);
			if(currFriends.contains(user2)){
				parentMap.put(user2,currUser);
				//System.out.println(user2+" }");
				break;
			}
			for (Integer friend : currFriends){
				if(!toVisit.contains(friend)){
					toVisit.add(friend);
				}
				if (!parentMap.containsKey(friend)) {
					parentMap.put(friend, currUser);
				}
				//System.out.print(friend + ", ");
			}
			//System.out.println(" }");
			visited.add(currUser);
			if (currUser == user2){
				break;
			}
		}
		path = reconstructPath(user1, user2, parentMap);
		return path;
	}

	private List<Integer> reconstructPath(Integer start, Integer end, HashMap<Integer, Integer> parentMap) {
		//System.out.println("Reconstructing Path...");
		List<Integer> path = new ArrayList<Integer>();
		Integer currUser = end;
		while (true){
			//System.out.print(currUser+" <-- ");
			path.add(currUser);
			currUser = parentMap.get(currUser);
			if (currUser == start){
				//System.out.println(currUser);
				break;
			}
		}
		int pathSize = path.size();
		//System.out.println("Degress of Separation: "+pathSize);
		return path;
	}

	/**
	 * Helper method to pull connections/friends of
	 * the given user from graphMap
	 *
	 * @param user	an integer representing one person's ID
	 * @return 		all the direct connections for user
	 */
	protected HashSet<Integer> getConnections (Integer user) {
		return graphMap.get(user);
	}
	
	/**
	 * Helper method to find the mutual/shared connections
	 * between any two individuals in the graph
	 * 
	 * @param user1	an integer representing one person's ID
	 * @param user2	an integer representing a second person's ID
	 * @return 			the connections shared by both people
	 */	
	protected HashSet<Integer> getMutualFriends (Integer user1, Integer user2){
		//create empty list to return with shared friends/connections
		HashSet<Integer> mutualFriends = new HashSet<Integer>();
		//verify both are people exist in the graph
		if(graphMap.containsKey(user1) && graphMap.containsKey(user2)) {
			//obtain list of friends for both people
			HashSet<Integer> person1Friends = getConnections(user1);
			HashSet<Integer> person2Friends = getConnections(user2);
			//iterate over 1 list and compare with contents of other
			for (Integer friend : person1Friends) {
				//if a person is in both lists add to mutual friends list
				if (person2Friends.contains(friend)) {
					mutualFriends.add(friend);
				}
			}
		}
		return mutualFriends;
	}

	/**
	 * Creates an edge/connection between 2 users/vertices
	 *
	 * @param from	an integer representing one person's ID
	 *              and the first vertex of the edge
	 * @param to	an integer representing a second person's ID
	 *              and the second vertex of the edge
	 */
	public void addEdge(int from, int to) {
		//if "from" is not a current vertex, add it
		if (!graphMap.containsKey(from)) this.addVertex(from);
		//if "to" is not a current vertex, add it
		if (!graphMap.containsKey(to)) this.addVertex(to);
		//get current list of edges for "from"
		HashSet<Integer> neighbors = graphMap.get(from);
		neighbors.add(to);
		graphMap.put(from, neighbors);
	}

	/**
	 *
	 */
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
		return graphMap;
	}
}