public class Land extends Property {

    public Land(int id, String name, int cost) {
        super(id, name, cost);
    }

    @Override
    public double getRent() {
        if (getCost() <= 2000){
            return getCost() * 0.4 ;
        }
        else if (2001 <= getCost() && getCost() <= 3000){
            return (getCost()*0.3);
        }
        else if (3001 <= getCost() && getCost() <= 4000){
            return getCost()*0.35 ;
        }
        else return 0;
    }
}
