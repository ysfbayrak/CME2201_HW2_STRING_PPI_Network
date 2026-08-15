import java.util.Objects;

public class Protein {
    private String ID;
    private String preferredName;
    private int size;
    private String annotation;

    public Protein(){};

    public String getID() {
        return ID;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public int getSize() {
        return size;
    }

    public String getAnnotation() {
        return annotation;
    }


    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPreferredName(String preferredName) {this.preferredName = preferredName;}

    public void setSize(int size) {
        this.size = size;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }



    @Override
    public boolean equals(Object o) {
        // Checking equality based on protein ID
        if (o == null || getClass() != o.getClass()) return false;
        Protein protein = (Protein) o;
        return Objects.equals(ID, protein.ID);
    }

    @Override
    public int hashCode() {
        // Needed for hashmap keys
        return Objects.hash(ID);
    }
    @Override
    public String toString() {
        // Printing only ID is enough
        return ID;
    }
}