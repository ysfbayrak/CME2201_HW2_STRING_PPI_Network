import java.util.Map;

/**
 * Interface defining the core operations for the Protein Interaction Network.
 */
public interface MenuInterface {

    DirectedGraph<Protein> initializeGraph(int threshold);

    void loadGraph();

    void searchProtein();

    Map<String, Protein> loadProteins();

    void checkInteraction();

    void findMostConfidentPath();

    void getGraphMetrics();

    void getTraversals();
}