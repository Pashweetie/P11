import java.util.List;

public class Item {
    private double num;
    private List<Double> list;

    public Item(double n, List<Double> li){
        this.num = n;
        this.list = li;
    }

    public double getNum(){
        return this.num;
    }
    public List<Double> getList() { return this.list; }
}
