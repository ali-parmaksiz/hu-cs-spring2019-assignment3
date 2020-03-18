public class Company extends Property {


    public Company(int id, String name, int cost) {
        super(id, name, cost);
    }

    @Override
    public double getRent() {
        return super.getRent();
    }

    @Override
    public void setRent(int dice) {
        super.setRent(dice*4);
    }
}
