import java.util.*;

public class DirectedGraph<T> implements GraphInterface<T> {
    private Map<T, VertexInterface<T>> vertices;
    private int edgeCount;

    public DirectedGraph() {
        vertices = new HashMap<>();
        edgeCount = 0;
    } // end default constructor


    public boolean addVertex(T vertexLabel) {
        if (vertices.containsKey(vertexLabel)) return false;

        vertices.put(vertexLabel, new Vertex<>(vertexLabel));
        return true;
    }

    public boolean addEdge(T begin, T end, double edgeWeight) {
        boolean result = false;
        VertexInterface<T> beginVertex = vertices.get(begin);
        VertexInterface<T> endVertex = vertices.get(end);

        if ((beginVertex != null) && (endVertex != null) ) result = beginVertex.connect(endVertex, edgeWeight);
        if (result) edgeCount++;

        return result;
    } // end addEdge

    public boolean addEdge(T begin, T end) {
        return addEdge(begin, end, 0);
    } // end addEdge

    public boolean hasEdge(T begin, T end) {
        boolean found = false;
        VertexInterface<T> beginVertex = vertices.get(begin);
        VertexInterface<T> endVertex = vertices.get(end);

        if ((beginVertex != null) && (endVertex != null))
        {
            Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();

            while (!found && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (endVertex.equals(nextNeighbor)) found = true;
            } // end while
        } // end if
        return found;
    } // end hasEdge

    public boolean isEmpty() {return vertices.isEmpty();}

    public void clear() {
        vertices.clear();
        edgeCount = 0;
    } // end clear

    public int getNumberOfVertices() {return vertices.size();}

    public int getNumberOfEdges() {return edgeCount;}


    public Queue<T> getBreadthFirstTraversal(T origin) {
        resetVertices();
        Queue<T> traversalOrder=new LinkedList<>();
        Queue< VertexInterface<T> > vertexQueue=new LinkedList<>();

        VertexInterface<T> originVertex=vertices.get(origin);
        if (originVertex == null) return traversalOrder;
        originVertex.visit();
        traversalOrder.add(originVertex.getLabel());
        vertexQueue.add(originVertex);

        while(!vertexQueue.isEmpty()){
            VertexInterface<T> frontVertex=vertexQueue.poll();
            Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
            // Visits all the neighbors before the next vertex.
            while(neighbors.hasNext()){
                VertexInterface<T> nextNeighbor=neighbors.next();

                if(!nextNeighbor.isVisited()){
                    nextNeighbor.visit();
                    traversalOrder.add(nextNeighbor.getLabel());
                    vertexQueue.add(nextNeighbor); // Add to queue to explore its neighbors later
                }

            }
        }
        return traversalOrder;
    }

    public Queue<T> getDepthFirstTraversal(T origin) {
        resetVertices();
        Queue<T>traversalOrder= new LinkedList<>();
        Stack<VertexInterface<T>> vertexStack= new Stack<>();

        VertexInterface<T> originVertex=vertices.get(origin);
        if (originVertex == null) return traversalOrder;    
        traversalOrder.add(originVertex.getLabel());
        originVertex.visit();
        vertexStack.push(originVertex);

        while(!vertexStack.isEmpty()){
            VertexInterface<T> topVertex=vertexStack.peek();
            //Try to find an unvisited neighbor
            if(topVertex.getUnvisitedNeighbor()!=null){
                VertexInterface<T> nextNeighbor=topVertex.getUnvisitedNeighbor();
                nextNeighbor.visit();
                traversalOrder.add(nextNeighbor.getLabel());
                vertexStack.push(nextNeighbor);
            }else{
                //If no neighbors left, backtrack (pop from stack)
                vertexStack.pop();
            }
        }

        return traversalOrder;
    }


    public double getMostConfidentPath(T begin, T end, Stack<T> path) {
        // Finds the path with the highest total weight (confidence score).
        resetVertices();
        boolean done = false;
        PriorityQueue<EntryPQ> priorityQueue = new PriorityQueue<>();

        VertexInterface<T> originVertex = vertices.get(begin);
        VertexInterface<T> endVertex = vertices.get(end);

        if (originVertex == null || endVertex == null) return 0;

        originVertex.setCost(0);
        priorityQueue.add(new EntryPQ(originVertex, 0, 0, null));

        while (!done && !priorityQueue.isEmpty()) {
            EntryPQ frontEntry = priorityQueue.poll(); // Get the best current path
            VertexInterface<T> frontVertex = frontEntry.getVertex();

            if (!frontVertex.isVisited()) {
                frontVertex.visit();
                frontVertex.setCost(frontEntry.getCost());
                frontVertex.setPredecessor(frontEntry.getPredecessor()); // Track path for reconstruction

                if (frontVertex.equals(endVertex)) {
                    done = true;
                } else {
                    Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
                    Iterator<Double> edgeWeights = frontVertex.getWeightIterator();
                    // Check neighbors and calculate new costs
                    while (neighbors.hasNext()) {
                        VertexInterface<T> nextNeighbor = neighbors.next();
                        Double edgeWeight = edgeWeights.next();

                        if (!nextNeighbor.isVisited()) {
                            double nextCost = frontVertex.getCost() + edgeWeight;
                            int nextDepth = frontEntry.getDepth() + 1;

                            priorityQueue.add(new EntryPQ(nextNeighbor, nextCost, nextDepth, frontVertex));
                        }
                    }
                }
            }
        }
     // Reconstruct the path backwards
        double pathCost = endVertex.getCost();
        path.push(endVertex.getLabel());

        VertexInterface<T> vertex = endVertex;
        while (vertex.hasPredecessor()) {
            vertex = vertex.getPredecessor();
            path.push(vertex.getLabel());
        }

        return pathCost;
    }

    private class EntryPQ implements Comparable<EntryPQ> {
        private VertexInterface<T> vertex;
        private VertexInterface<T> predecessor;
        private double cost;
        private int depth;

        public EntryPQ(VertexInterface<T> vertex, double cost, int depth, VertexInterface<T> predecessor) {
            this.vertex = vertex;
            this.cost = cost;
            this.depth = depth;
            this.predecessor = predecessor;
        }

        public VertexInterface<T> getVertex() { return vertex; }
        public VertexInterface<T> getPredecessor() { return predecessor; }
        public double getCost() { return cost; }
        public int getDepth() { return depth; }

        @Override
        public int compareTo(EntryPQ other) {
            // Priority 1: BFS logic (shorter path length)
            int depthComparison = Integer.compare(this.depth, other.depth);
            if (depthComparison != 0) {
                return depthComparison;
            }
            // Priority 2: Higher cost (confidence score) is better
            return Double.compare(other.cost, this.cost);
        }
    }


    private void resetVertices()
    {
        for (VertexInterface<T> nextVertex : vertices.values()) {
            nextVertex.unvisit();
            nextVertex.setCost(0);
            nextVertex.setPredecessor(null);
        } // end while
    } // end resetVertices

    public int calcDiameter(){
        int max=0;
        for (VertexInterface<T> temp : vertices.values()) {
            int size = getMaxDistance(temp.getLabel());
            if (size > max) max = size;
        }
        return max;
    }

    public int getMaxDistance(T origin) {
        // Uses BFS to find the maximum number of hops from a specific origin node.
        resetVertices();

        Queue<VertexInterface<T>> vertexQueue = new LinkedList<>();
        VertexInterface<T> originVertex = vertices.get(origin);

        if (originVertex == null) return 0;

        originVertex.visit();
        originVertex.setCost(0);  // Cost represents hop count (distance)
        vertexQueue.add(originVertex);

        int maxDistance = 0;

        while (!vertexQueue.isEmpty()) {
            VertexInterface<T> frontVertex = vertexQueue.poll();

            if (frontVertex.getCost() > maxDistance) { // Update max distance found so far
                maxDistance = (int) frontVertex.getCost();
            }

            Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
            while (neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();

                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    nextNeighbor.setCost(frontVertex.getCost() + 1);
                    vertexQueue.add(nextNeighbor);
                }
            }
        }
        return maxDistance;
    }


    public double getReciprocity(){
        if(getNumberOfEdges()==0) return 0;
        int count=0;
        // Loop through all vertices to check for mutual edges
        for (VertexInterface<T> vertex : vertices.values()) {
            Iterator<VertexInterface<T>> neighbors = vertex.getNeighborIterator();
            while (neighbors.hasNext()) {
                VertexInterface<T> neighbor = neighbors.next();
                if (hasEdge(neighbor.getLabel(), vertex.getLabel())) count++;
            }
        }
        return (double) count / getNumberOfEdges();
    }


} // end DirectedGraph