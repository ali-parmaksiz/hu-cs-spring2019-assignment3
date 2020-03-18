import java.util.ArrayList;

public class Player extends User {

    private int position = 1;
    private int count = 0 ;
    ArrayList<String> places = new ArrayList<>();
    private int havingRR = 0;


    public Player(String name, int money) {
        super(name, money);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position += position;
    }

    public int getCount() {
        return count;
    }

    public void setCount() {
        if (getCount() == 4){
            this.count = 0;
        }
        else {
            this.count ++;
        }
    }

    public ArrayList<String> getPlaces() {
        return places;
    }

    public void setPlaces(String place) {
        this.places.add(place);
    }

    public int getHavingRR() {
        return havingRR;
    }

    public void setHavingRR() {
        this.havingRR ++;
    }
}
