import java.util.Scanner;

public class Pist {

    public String type;

    public double dataInt;

    public ArrayList<p11Type> dataArr;

    public p11Type(double dubIn){
        type = "dub"
        dataInt = dubIn;
        dataArr = null;
    }

    public p11Type(ArrayList<p11Type> arrIn){
        type = "arr"
        dataInt = null;
        dataArr = arrIn;
    }
    public p11Type(p11Type pIn){
        type = pIn.type;
        dataInt = pIn.dataInt;
        dataArr = pIn.dataArr;
    }

    public void append(p11Type obj){
        if (dataArr){
            dataArr.add(obj)
        }
    }

    public void add(p11Type obj){
        if (dataArr){
            dataArr.add(0,obj)
        }
        return 0;
    }
    public double size(){
        if (dataArr){
            return double(dataArr.size());
        }
        return 0;
    }


}