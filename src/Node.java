/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.awt.*;
import java.util.List;

public class Node {

    public static int count = 0;  // maintain unique id for each node

    private int id;

    private String kind;  // non-terminal or terminal category for the node
    private String info;  // extra information about the node such as
    // the actual identifier for an I

    // references to children in the parse tree
    private Node first, second;

    private StackFrame params;

    // params of memories for all pending calls
    private static ArrayList<MemTable> memStack = new ArrayList<>();
    // convenience reference to top MemTable on params
    private static MemTable table = new MemTable();

    /** built in functions */
    // bif0 = funcs with no params
    private final static String[] bif0 = {"read", "nl"};
    // bif1 = funcs with one param
    private final static String[] bif1 = {"first", "rest", "null", "num", "list",
            "write", "quote"};
    // bif2 = funcs with two params
    private final static String[] bif2 = {"lt", "le", "eq", "ne", "and", "or",
            "not", "plus", "minus", "times", "div", "ins"};

    // construct a common node with no info specified
    public Node(String k, Node one, Node two) {
        kind = k;
        info = "";
        first = one;
        second = two;
        id = count;
        count++;
        System.out.println(this);
    }

    // construct a def node with params frame for params
    public Node(String k, String inf, Node one, Node two, StackFrame stack){
        kind = k;
        info = inf;
        first = one;
        second = two;
        this.params = stack;
        id = count;
        count++;
        System.out.println(this);
    }

    // construct a node with specified info
    public Node(String k, String inf, Node one, Node two) {
        kind = k;
        info = inf;
        first = one;
        second = two;
        id = count;
        count++;
        System.out.println(this);
    }

    // construct a node that is essentially a token
    public Node(Token token) {
        kind = token.getKind();
        info = token.getDetails();
        first = null;
        second = null;
        id = count;
        count++;
        System.out.println(this);
    }

    public String toString() {
        return "#" + id + "[" + kind + "," + info + "]<" + nice(first) +
                " " + nice(second) + ">";
    }

    public String nice(Node node) {
        if (node == null) {
            return "-";
        } else {
            return "" + node.id;
        }
    }

    // produce array with the non-null children
    // in order
    public String getKind(){ return kind; }

    public String getInfo(){
      return info;
    }

    public Node[] getChildren() {
        int count = 0;
        if (first != null) count++;
        if (second != null) count++;
        Node[] children = new Node[count];
        int k = 0;
        if (first != null) {
            children[k] = first;
            k++;
        }
        if (second != null) {
            children[k] = second;
            k++;
        }

        return children;
    }

    //******************************************************
    // graphical display of this node and its subtree
    // in given camera, with specified location (x,y) of this
    // node, and specified distances horizontally and vertically
    // to children
    public void draw(Camera cam, double x, double y, double h, double v) {

        // System.out.println("draw node " + id);

        // set drawing color
        cam.setColor(Color.black);

        String text = kind;
        if (!info.equals("")) text += "(" + info + ")";
        cam.drawHorizCenteredText(text, x, y);

        // positioning of children depends on how many
        // in a nice, uniform manner
        Node[] children = getChildren();
        int number = children.length;
        // System.out.println("has " + number + " children");

        double top = y - 0.75 * v;

        if (number == 0) {
            return;
        } else if (number == 1) {
            children[0].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
        } else if (number == 2) {
            children[0].draw(cam, x - h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x - h / 2, top);
            children[1].draw(cam, x + h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x + h / 2, top);
        } else if (number == 3) {
            children[0].draw(cam, x - h, y - v, h / 2, v);
            cam.drawLine(x, y, x - h, top);
            children[1].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
            children[2].draw(cam, x + h, y - v, h / 2, v);
            cam.drawLine(x, y, x + h, top);
        } else {
            System.out.println("no Node kind has more than 3 children???");
            System.exit(1);
        }

    }// draw

    // needs to return Value objects
    public Value evaluate(HashMap<String, Node> defsList, StackFrame params) {
        Value arg1, arg2;

        Value ZERO = new Value( 0 );
        Value ONE = new Value( 1 );

        Value ans = null;
        if(kind.equals("defs")){
            ans = first.evaluate(defsList, null);
            //System.out.println("Evaluating defs..." + ans);
        }
        else if(kind.equals("def")){
            System.out.println("Evaluating def...");
            if(first.getKind().equals("params")) {
                params = first.passParams(); // evaluate the params
                ans = second.evaluate(defsList, params);
            } else{
                ans = first.evaluate(defsList, null);
            }
            // Need to find a place to store custom defs
        }
        else if(kind.equals("name")){
            // retrieves value for name
            return params.retrieve(info);
        }
        else if(kind.equals("expr")){
            //System.out.println("Evaluating expression...");
            if(!info.equals("")){
                return new Value(Double.parseDouble(info));
            }
            else{
                return first.evaluate(defsList, params);
            }
        }
        else if(kind.equals("list")){
            //System.out.println("Evaluating list...");
            if(!info.equals("")){
                if(member(info, bif0)){
                    switch (info) {
                        case "read":
                            Scanner s = new Scanner(System.in);
                            System.out.println("Enter a num: ");
                            System.out.print("> ");

                            String num = s.nextLine();
                            System.out.println(Double.parseDouble(num));
                        case "nl":
                            System.out.print("\n> ");
                    }
                } else if(member(info, bif1)){
                    arg1 = first.evaluate(defsList, params);
                    switch (info) {
                        case "not":
                            if(arg1.getNumber() == 0) return ONE;
                            else return ZERO;
                        case "first":
                            if(arg1.isEmpty()) System.out.println("Error: empty list " + arg1.toString());
                            // might have to return something here, not sure yet though
                            else return arg1.first();
                        case "rest":
                            return arg1.rest();
                        case "null":
                            if(arg1.isEmpty()) return ONE;
                            else return ZERO;
                        case "num":
                            if(arg1.isNumber()) return ONE;
                            else return ZERO;
                        case "list":
                            if(!arg1.isNull()) return ONE;
                            else return ZERO;
                        case "write":
                            System.out.println(arg1 + " ");
                            return arg1;
                        case "quote":
                            return arg1;
                    }
                } else if(member(info, bif2)){

                    // Since list can only possess one "items" node, must create
                    // this special case
                    if(first.getKind().equals("items")) {
                        Value items = first.evaluate(defsList, params);
                            arg1 = items.first();
                            arg2 = items.rest().first();
                    }
                    // For lists and functions
                    else{
                        arg1 = first.evaluate(defsList, params);
                        arg2 = second.evaluate(defsList, params);
                    }
                    switch (info) {
                        case "plus":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double sum = arg1.getNumber() + arg2.getNumber();
                            ans = new Value(sum);
                            return ans;
                        case "minus":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double min = arg1.getNumber() - arg2.getNumber();
                            ans = new Value(min);
                            return ans;
                        case "times":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double mul = arg1.getNumber() * arg2.getNumber();
                            ans = new Value(mul);
                            return ans;
                        case "div":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double div = arg1.getNumber() / arg2.getNumber();
                            ans = new Value(div);
                            return ans;
                        case "lt":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() < arg2.getNumber())
                                ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "le":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() <= arg2.getNumber()) ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "eq":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() == arg2.getNumber())
                                ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "ne":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() != arg2.getNumber())
                                ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "and":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() > 0 && arg2.getNumber() > 0)
                                ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "or":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() > 0 || arg2.getNumber() > 0)
                                ans = ONE;
                            else ans = ZERO;
                            return ans;
                        case "ins":
                            Value newList = new Value();
                            newList.insert(arg1);
                            newList.insert(arg2);
                            return newList;
                    }
                }
            }
            else if(defsList.containsKey(info)){
                Node def = defsList.get(info);
                return def.evaluate(defsList, null);
            }
            else{
                // empty list
                if(first == null){
                    return new Value();
                }
                return first.evaluate(defsList, params);
            }
        }
        else{ // items
            //System.out.println("Evaluating items...");
            Value items = new Value();

            if (second == null){
                items = items.insert(first.evaluate(defsList, params));
                return items;
            } else{
                items = second.evaluate(defsList, params);
                items = items.insert(first.evaluate(defsList, params));
                return items;
            }
        }
        return ans;
    }

    // return whether target is a member of array
    private static boolean member (String target, String[]array ){
        for (int k = 0; k < array.length; k++) {
            if (target.equals(array[k])) {
                return true;
            }
        }
        return false;
    }

    private static StackFrame passParams(){

        return null;
    }

}// Node
