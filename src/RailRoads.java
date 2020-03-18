public class RailRoads extends Property {
    public RailRoads(int id, String name, int cost) {
        super(id, name, cost);
    }

    @Override
    public double getRent() {
        return super.getRent();
    }

    @Override
    public void setRent(int havingRR) {
        super.setRent(havingRR*25);
    }
}
