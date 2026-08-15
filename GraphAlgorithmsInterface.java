import java.util.*;
public interface GraphAlgorithmsInterface<T>
{
    public Queue<T> getBreadthFirstTraversal(T origin);

    public Queue<T> getDepthFirstTraversal(T origin);

    public double getMostConfidentPath(T begin, T end, Stack<T> path);
}