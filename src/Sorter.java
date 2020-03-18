import java.util.Comparator;

public class Sorter implements Comparator<Square> {

    public int compare(Square o1, Square o2){
        return o1.getId() - o2.getId() ;
    }
}
