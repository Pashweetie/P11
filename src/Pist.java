import java.util.List;

public class Pist {
    private double num;
    private List<Double> list;

    public Pist(double n, List<Double> li){
        this.num = n;
        this.list = li;
    }

    public double getNum(){
        return this.num;
    }
    public List<Double> getList() { return this.list; }
}
