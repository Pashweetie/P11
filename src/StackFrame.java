/*  a stack frame
    holds names and
    values of all the
    parameters in the
    current function call
*/

import java.util.ArrayList;

public class StackFrame {

    private ArrayList<String> names;
    private ArrayList<Value> values;

    public StackFrame() {
        names = new ArrayList<String>();
        values = new ArrayList<Value>();
    }

    // return value bound to name in this
    // stack frame
    public Value retrieve( String name ) {
        for (int k=0; k<names.size(); k++) {
            if (names.get(k).equals(name)) {
                return values.get(k);
            }
        }
        System.out.println("Oops, couldn't find [" +
                name + "] in current stack frame");
        System.exit(1);
        return null;
    }

    public void add( String n, Value v ) {
        names.add( n );
        values.add( v );
    }

    public String toString() {
        String s = "";
        for (int k=0; k<names.size(); k++) {
            s += names.get(k) + " " + values.get(k) + "\n";
        }
        return s;
    }

}
