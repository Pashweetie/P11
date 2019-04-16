import java.util.List;

public class Pist {
    private List<Pist> list;
    private double num;

    public Pist(List<Pist> li, double n){
        this.list = li;
        this.num = n;
    }

    public List<Pist> getList(){
        return this.list;
    }

    public double getNum(){
        return this.num;
    }
}
