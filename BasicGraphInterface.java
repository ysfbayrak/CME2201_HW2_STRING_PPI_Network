public interface BasicGraphInterface<T>
{
    public boolean addVertex(T vertexLabel);

    public boolean addEdge(T begin, T end, double edgeWeight);

    public boolean addEdge(T begin, T end);

    public boolean hasEdge(T begin, T end);

    public boolean isEmpty();

    public int getNumberOfVertices();

    public int getNumberOfEdges();

    public void clear();
}