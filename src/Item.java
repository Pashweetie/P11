import java.util.List;

public class Item {
    private double num;
    private List<Item> list;

    public Item(double n, List<Item> li){
        this.num = n;
        this.list = li;
    }

    public double getNum(){
        return this.num;
    }
    public List<Item> getList() { return this.list; }
}
