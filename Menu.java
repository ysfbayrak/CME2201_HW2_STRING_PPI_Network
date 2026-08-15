import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Menu implements MenuInterface{
    Scanner sc = new Scanner(System.in);

    private DirectedGraph<Protein> mainGraph;
    private final Map<String,Protein> proteinMap=loadProteins();

    public Menu(){while(true) menu();}

    private void menu() {

        switch (takeCommand()) {
            case 1:
                loadGraph();
                break;
            case 2:
                searchProtein();
                break;
            case 3:
                checkInteraction();
                break;
            case 4:
                findMostConfidentPath();
                break;
            case 5:
                getGraphMetrics();
                break;
            case 6:
                getTraversals();
                break;
            default:
                System.out.println("Invalid command.");
        }
    }


    public DirectedGraph<Protein> initializeGraph(int threshold){
        long startTime = System.nanoTime();

        DirectedGraph<Protein> tempGraph= new DirectedGraph<>();
        try (BufferedReader br = new BufferedReader(new FileReader("9606.protein.links.v12.0.txt"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                double weight = Double.parseDouble(tokens[2]);
                if(weight<(double)threshold) continue;

                Protein begin =proteinMap.get(tokens[0]);
                Protein end =proteinMap.get(tokens[1]);

                tempGraph.addVertex(begin);
                tempGraph.addVertex(end);
                tempGraph.addEdge(begin,end,weight);
            }
        } catch (IOException e) {e.printStackTrace();}

        long finishTime = System.nanoTime();
        System.out.println("Vertex Count : "+ tempGraph.getNumberOfVertices());
        System.out.println("Edge Count : "+tempGraph.getNumberOfEdges());

        long elapsedTimeNano = finishTime - startTime;
        double elapsedTimeMili = elapsedTimeNano / 1_000_000.0;
        System.out.println("Graph Load Time : " + elapsedTimeMili + " ms.");
        return tempGraph;
    }

    private boolean notInitialized(){
        if(mainGraph!=null) return false;

        System.out.println("Graph is not initialized. Please initialize the graph by entering command '1' . ");
        return true;
    }

    public void loadGraph(){
        mainGraph=initializeGraph(takeThreshold());
    }

    public void searchProtein(){
        String id=takeID();
        if(!proteinMap.containsKey(id))System.out.println("not exists.");
        else{
            System.out.println("Protein ID : "+id);
            System.out.println("Preferred Name : "+proteinMap.get(id).getPreferredName());
            System.out.println("Protein Size : "+proteinMap.get(id).getSize());
            System.out.println("Annotation : "+proteinMap.get(id).getAnnotation());
        }
    }

    private boolean proteinNotExists(String id){
        return !proteinMap.containsKey(id);
    }

    public Map<String,Protein> loadProteins(){
        Map<String,Protein> tempMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("9606.protein.info.v12.0.txt"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                Protein temp= new Protein();
                String[] tokens = line.split("\\s+", 4); //split by whitespace

                temp.setID(tokens[0]);
                temp.setPreferredName(tokens[1]);
                temp.setSize(Integer.parseInt(tokens[2]));
                temp.setAnnotation(tokens[3]);

                tempMap.put(temp.getID(),temp);
            }
        } catch (IOException e) {e.printStackTrace();}
        return tempMap;
    }

    public void checkInteraction(){
        if(notInitialized())return;

        String firstID= takeFirstID();
        String secondID= takeSecondID();
        if(proteinNotExists(firstID) || proteinNotExists(secondID)) {
            System.out.println("Invalid entry.");
            return;
        }
        if(mainGraph.hasEdge(proteinMap.get(firstID),proteinMap.get(secondID)))
            System.out.println(firstID+ " and " +secondID+ " have interaction." );
        else System.out.println(firstID+ " and " +secondID+ " don't have interaction." );


    }

    public void findMostConfidentPath(){
        if(notInitialized())return;

        String firstID= takeFirstID();
        String secondID= takeSecondID();

        if(proteinNotExists(firstID) || proteinNotExists(secondID)){System.out.println("Invalid entry.");}
        else printMostConfidentPath(firstID,secondID);
    }

    public void getGraphMetrics(){
        if(notInitialized())return;

        System.out.println("Vertex Count : "+ mainGraph.getNumberOfVertices());
        System.out.println("Edge Count : "+mainGraph.getNumberOfEdges());

        double averageDegree= (2*(double)mainGraph.getNumberOfEdges())/mainGraph.getNumberOfVertices(); //Calculate by formula

        System.out.println("Average Degree : "+ averageDegree);
        System.out.println("Diameter : "+ mainGraph.calcDiameter());
        System.out.println("Reciprocity : "+ mainGraph.getReciprocity());
    }



    public void getTraversals(){

        if(notInitialized())return;
        String id= takeID();

        if(proteinNotExists(id))System.out.println("not exists.");
        else{
            printBreadthFirstTraversal(id);
            printDepthFirstTraversal(id);
        }
    }
    //helper methods to handle exceptions taking input from the user.
    private int takeCommand(){
        int command=0;
        boolean isValid=false;
        while (!isValid) {
            System.out.println("Enter command: ");
            System.out.print("> ");
            String input=sc.nextLine();
            try {
                command=Integer.parseInt(input);
                isValid=true;
            } catch (NumberFormatException e) {System.out.println("Invalid entry. Please enter a number.");}
        }
        return command;
    }

    private int takeThreshold(){
        int threshold=0;
        boolean isValid=false;
        while (!isValid) {
            System.out.println("Enter threshold : ");
            System.out.print("> ");
            String input=sc.nextLine();
            try {
                threshold=Integer.parseInt(input);
                isValid=true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry. Please enter a number.");
            }
        }
        return threshold;
    }

    private String takeID(){
        String id="";
        System.out.println("Enter ID: ");
        System.out.print("> ");
        id = sc.nextLine();
        return id;
    }
    private String takeFirstID(){
        String id="";
        System.out.println("Enter the first ID: ");
        System.out.print("> ");
        id = sc.nextLine();
        return id;
    }
    private String takeSecondID(){
        String id="";
        System.out.println("Enter the second ID: ");
        System.out.print("> ");
        id = sc.nextLine();
        return id;
    }

    //helper methods to print traversals
    private void printBreadthFirstTraversal(String id){
        Queue<Protein> breadthFirst= mainGraph.getBreadthFirstTraversal(proteinMap.get(id));
        System.out.println("Breadth-First Traversal : ");
        while(!breadthFirst.isEmpty()){
            System.out.println(breadthFirst.poll());
        }
    }

    private void printDepthFirstTraversal(String id){
        Queue<Protein> depthFirst= mainGraph.getDepthFirstTraversal(proteinMap.get(id));
        System.out.println("Depth-First Traversal : ");
        while(!depthFirst.isEmpty()){
            System.out.println(depthFirst.poll());
        }
    }

    private void printMostConfidentPath(String firstID,String secondID){
        Stack<Protein> path= new Stack<>();
        System.out.println();
        System.out.println("Total Weight : "+ mainGraph.getMostConfidentPath(proteinMap.get(firstID),proteinMap.get(secondID),path));
        while (!path.isEmpty()) {
            System.out.print(path.pop());
            if(!path.isEmpty()) System.out.print(" -> ");
        }
        System.out.println();
    }



}
