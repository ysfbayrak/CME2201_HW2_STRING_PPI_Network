import java.util.*;

class Vertex<T> implements VertexInterface<T>
{
    private T label;
    private List<Edge> edgeList; //List of neighbors
    private boolean visited;
    private VertexInterface<T> previousVertex;
    private double cost;

    public Vertex (T vertexLabel)
    {
        label = vertexLabel;
        edgeList = new LinkedList<>();
        visited = false;
        previousVertex = null;
        cost = 0;
    } // end constructor

    public T getLabel() {
        return label;
    }

    public void visit() {visited=true;}

    public void unvisit() {visited=false;}

    public boolean isVisited() {return visited;}


    public boolean connect (VertexInterface<T> endVertex, double edgeWeight) {
        boolean result = false;
        if (!this.equals(endVertex))
        {
            Iterator<VertexInterface<T>> neighbors = getNeighborIterator();
            //Checking if edge already exists.
            boolean duplicateEdge = false;
            while (!duplicateEdge && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (endVertex.equals(nextNeighbor)) duplicateEdge = true;
            }
            if (!duplicateEdge) {
                edgeList.add(new Edge (endVertex, edgeWeight));
                result = true;
            }
        }
        return result;
    }

    public boolean connect (VertexInterface<T> endVertex) {
        return connect(endVertex, 0);
    }

    //Implementing Iterator interfaces
    private class NeighborIterator implements Iterator<VertexInterface<T>>
    {
        private Iterator<Edge> edges;

        private NeighborIterator()
        {
            edges = edgeList.iterator();
        }

        public boolean hasNext()
        {
            return edges.hasNext();
        }

        public VertexInterface<T> next()
        {
            VertexInterface<T> nextNeighbor = null;
            if (edges.hasNext())
            {
                Edge edgeToNextNeighbor = edges.next();
                nextNeighbor = edgeToNextNeighbor.getEndVertex();
            }
            else
            {
                throw new NoSuchElementException();
            }
            return nextNeighbor;
        } // end next

        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    } // end NeighborIterator

    private class WeightIterator implements Iterator<Double>
    {
        private Iterator<Edge> edges;
        private WeightIterator() {edges = edgeList.iterator();}

        public boolean hasNext(){return edges.hasNext();}

        public Double next()
        {
            double nextWeight = 0;
            if (edges.hasNext())
            {
                Edge edgeToNextNeighbor = edges.next();
                nextWeight = edgeToNextNeighbor.getWeight();
            }
            else throw new NoSuchElementException();
            return nextWeight;
        } // end next

        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    } // end WeightIterator

    public Iterator<VertexInterface<T>> getNeighborIterator() {return new NeighborIterator();}

    public Iterator<Double> getWeightIterator() {
        return new WeightIterator();
    }


    public boolean hasNeighbor() {
        return !edgeList.isEmpty();
    }

    public VertexInterface<T> getUnvisitedNeighbor()
    {
        VertexInterface<T> result = null;
        Iterator<VertexInterface<T>> neighbors = getNeighborIterator();

        while ( neighbors.hasNext() && (result == null) ) {
            VertexInterface<T> nextNeighbor = neighbors.next();
            if (!nextNeighbor.isVisited()) result = nextNeighbor;
        } // end while

        return result;
    } // end getUnvisitedNeighbor


    public void setPredecessor(VertexInterface<T> predecessor){previousVertex = predecessor;}

    public VertexInterface<T> getPredecessor() {return previousVertex;}

    public boolean hasPredecessor() {return previousVertex != null;}


    public void setCost(double newCost) {cost=newCost;}

    public double getCost() {return cost;}


    private class Edge
    {
        private VertexInterface<T> vertex;
        private double weight;

        private Edge(VertexInterface<T> endVertex, double edgeWeight)
        {
            vertex = endVertex;
            weight = edgeWeight;
        }

        private VertexInterface<T> getEndVertex() {return vertex;}
        double getWeight() {return weight;}
    }// end Edge



} // end Vertex