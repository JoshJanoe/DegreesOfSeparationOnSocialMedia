package CapstoneProject;

import org.junit.Before;
import org.junit.Test;
import util.GraphLoader;
import warmup.Graph;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class CapstoneTester {

    private CapstoneDoSGraph testCG;
    private int numNodes = 7;
    private String filename = "data/facebook_ucsd.txt";
    //private String filename = "data/mutualFriendsTest.txt";
    //private String filename = "data/nthDegreeConnectionsTest.txt";

    public CapstoneTester(){

    }

    //@Before
    public void smallTestSetup(){
        testCG = new CapstoneDoSGraph();

        testCG.addVertex(0);        testCG.addVertex(1);        testCG.addVertex(2);
        testCG.addVertex(3);        testCG.addVertex(4);        testCG.addVertex(5);
        testCG.addVertex(6);

        testCG.addEdge(0, 6);        testCG.addEdge(1, 3);        testCG.addEdge(2, 1);
        testCG.addEdge(2, 5);        testCG.addEdge(3, 4);        testCG.addEdge(3, 5);
        testCG.addEdge(4, 3);        testCG.addEdge(4, 5);        testCG.addEdge(5, 0);
        testCG.addEdge(6, 0);        testCG.addEdge(6, 2);        testCG.addEdge(6, 4);
    }

    //@Test
    public void smallTest(){
        smallTestSetup();
        assertEquals(numNodes,getNumNodes());
        assertEquals(12,getNumEdges());
        System.out.println("Original Graph:");
        printGraph();
        //
        System.out.println("-------");
        //
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, "data/small_test_1.txt");
        assertEquals(numNodes,getNumNodes());
        assertEquals(18,getNumEdges());
        System.out.println("Original Graph:");
        printGraph();
    }

    //@Test
    public void connectionsTest (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);

        //Connections Test
        assertEquals(9,testCG.getConnections(0).size());
        printConnections(0);
        System.out.println("----------------");
    }

    //@Test
    public void mutualFriendsTest (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);
        assertEquals(4, testCG.getMutualFriends(3,8).size());
        printMutualFriends(3,8);
        System.out.println("----------------");
    }

    //@Test
    public void degreeOfSeparationTest (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);
        Integer testStart = 2;
        Integer testEnd = 8;
        Integer target = 3;
        Integer dOS = testCG.getDegreeOfSeparation(testStart,testEnd);
        assertEquals(target, dOS);
        System.out.println("Degrees of Separation: "+dOS);
        printPath(testStart,testEnd);
        System.out.println("----------------");
    }

    //@Test
    public void nthDegreeConnectionsTest (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);
        Integer userN = 1;
        printNthDegreeConnections(userN,0);
        printNthDegreeConnections(userN,1);
        printNthDegreeConnections(userN,2);
        printNthDegreeConnections(userN,3);
        printNthDegreeConnections(userN,4);
        printNthDegreeConnections(userN,5);
        printNthDegreeConnections(userN,6);
        printNthDegreeConnections(userN,7);
        System.out.println("----------------");
    }

    //@Test
    public void secondDegreeMutualConnectionsTest (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);
        Integer baseUser = 7;
        //Integer firstDegreeConnection = 483;
        //printSecondDegreeMutualFriends(baseUser, firstDegreeConnection);
        printSecondDegreeMutualFriends(baseUser);
        System.out.println("----------------");
    }

    @Test
    public void getAverageConnectionsAcrossDegreesOfSeparation (){
        testCG = new CapstoneDoSGraph();
        loadGraph(testCG, filename);
        for (int i=1; i<=9; i++){
            int avgAtDegree = getAverageConnectionsAtNDegrees(testCG,i);
            System.out.println("Average of "+ avgAtDegree+" connections at "+i+" degrees of separation");
            if (avgAtDegree == 0){
                break;
            }
        }
    }

    private int getAverageConnectionsAtNDegrees (CapstoneDoSGraph g, Integer degree){
        int total = 0;
        for (int i=1; i < getNumNodes(); i++){
            HashSet<Integer> connections = testCG.getNDegreeConnections(i, degree);
            total += connections.size();
        }
        return total/getNumNodes();
    }

    /**
	 * helper getter methods includes:
	 * getNodes, getNumNodes, getNumEdges
	 */

	//returns list of all nodes in graph
	public Set<Integer> getNodes(){
		return testCG.graphMap.keySet();
	}

	//returns number of nodes in graph
	public int getNumNodes() {
		return testCG.graphMap.size();
	}

	//returns total number of edges in graph
	public int getNumEdges() {
		int numEdges = 0;
		for (Integer i : testCG.graphMap.keySet()) {
            int currEdgeCount = testCG.graphMap.get(i).size();
			numEdges+=currEdgeCount;
		}
		return numEdges;
	}

    /**
     * Helper methods for debugging and testing
     * includes printGraph, printMap, printPath,
     *          printConnections, printMutualFriends
     */
    //prints a given graph for debugging/testing purposes
    public void printGraph() {
        for (Integer i : testCG.graphMap.keySet()) {
            Set<Integer> edges = testCG.graphMap.get(i);
            for (Integer k : edges) {
                System.out.println(i+" -> "+k);
            }
        }
    }

    //prints a given HashMap for debugging/testing purposes
    public void printMap(HashMap<Integer,HashSet<Integer>> map) {
        for (Integer i : map.keySet()) {
            Set<Integer> edges = map.get(i);
            for (Integer k : edges) {
                System.out.println(i+" -> "+k);
            }
        }
    }

    public void printPath(Integer start, Integer end){
        List<Integer> path = testCG.getPath(start,end);
        if(path != null){
            for (int i=0; i<path.size(); i++){
                Integer currNode = path.get(i);
                if (i != path.size()){ //if last int
                    System.out.print(currNode +" --> ");
                }
            }
            System.out.println(start);
        }
        if(path==null) {
            System.out.println("No path possible");
        }
    }

    public void printConnections (Integer user){
        HashSet<Integer> connections = testCG.getConnections(user);
        System.out.print("Connections for user "+user+": {");
        for(Integer connection : connections){
            System.out.print(connection+", ");
        }
        System.out.println(" }");
    }

    public void printMutualFriends(Integer user1, Integer user2){
        HashSet<Integer> connections = testCG.getMutualFriends(user1,user2);
        System.out.print("Mutual connections for users "+user1+" & " +user2+": {");
        for(Integer connection : connections){
            System.out.print(connection+", ");
        }
        System.out.println(" }");
    }

    public void printNthDegreeConnections(Integer user, int degree){
        HashSet<Integer> connections = testCG.getNDegreeConnections(user, degree);
        System.out.println("There are "+connections.size()+" "+degree+" degree connections for user " + user);
        System.out.print("{");
        for(Integer connection : connections){
            System.out.print(connection+", ");
        }
        System.out.println(" }");
    }

    public void printSecondDegreeMutualFriends(Integer baseUser) {
        HashSet<Integer> commonConnections = testCG.getSecondDegreeMutualFriends(baseUser);
        //print user friends
        System.out.print("Primary ");
        printConnections(baseUser);
        //print firstDegreeConnection friends
        System.out.println("Second Degree Connections: ");
        printNthDegreeConnections(baseUser, 2);
        //print potential shared connections
        System.out.println("Third Degree Connections: ");
        for (Integer connection : testCG.getNDegreeConnections(baseUser,2)){
            printConnections(connection);
        }
        //print shared connections
        System.out.println("Mutual Connections with 2nd degree associations:");
        System.out.print("{ ");
        for (Integer i : commonConnections){
            System.out.print(i+" ");
        }
        System.out.println("} ");
    }

    public List<Integer> sortHashSet (HashSet<Integer> set){
        List<Integer> sortedList = new ArrayList<Integer>(set);
        Collections.sort(sortedList);
        return sortedList;
    }

    /**
     * method to loadGraph from simple txt file
     *
     * @param g
     * @param filename
     */
    public static void loadGraph(CapstoneDoSGraph g, String filename) {
        Set<Integer> seen = new HashSet<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding new
        // vertices as they are found and connecting them with edges.
        while (sc.hasNextInt()) {
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            if (!seen.contains(v1)) {
                g.addVertex(v1);
                seen.add(v1);
            }
            if (!seen.contains(v2)) {
                g.addVertex(v2);
                seen.add(v2);
            }
            g.addEdge(v1, v2);
        }

        sc.close();
    }
}
