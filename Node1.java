/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class Node {

    public static int count = 0;  // maintain unique id for each node

    private int id;

    private String kind;  // non-terminal or terminal category for the node
    private String info;  // extra information about the node such as
    // the actual identifier for an I

    // references to children in the parse tree
    private Node first, second, third;

    // stack of memories for all pending calls
    private static ArrayList<MemTable> memStack = new ArrayList<MemTable>();
    // convenience reference to top MemTable on stack
    private static MemTable table = new MemTable();

    // status flag that causes <stmts> nodes to abort asking second
    // to execute
    private static boolean returning = false;

    // value being returned
    private static double returnValue = 0;

    private static Node root = null;  // root of the entire parse tree

    private static Scanner keys = new Scanner( System.in );

    // construct a common node with no info specified
    public Node( String k, Node one, Node two, Node three ) {
        kind = k;  info = "";
        first = one;  second = two;  third = three;
        id = count;
        count++;
        System.out.println( this );
    }

    // construct a node with specified info
    public Node( String k, String inf, Node one, Node two, Node three ) {
        kind = k;  info = inf;
        first = one;  second = two;  third = three;
        id = count;
        count++;
        System.out.println( this );
    }

    // construct a node that is essentially a token
    public Node( Token token ) {
        kind = token.getKind();  info = token.getDetails();
        first = null;  second = null;  third = null;
        id = count;
        count++;
        System.out.println( this );
    }

    public String toString() {
        return "#" + id + "[" + kind + "," + info + "]<" + nice(first) +
                " " + nice(second) + ">";
    }

    public String nice( Node node ) {
        if ( node == null ) {
            return "-";
        }
        else {
            return "" + node.id;
        }
    }

    // produce array with the non-null children
    // in order
    private Node[] getChildren() {
        int count = 0;
        if( first != null ) count++;
        if( second != null ) count++;
        if( third != null ) count++;
        Node[] children = new Node[count];
        int k=0;
        if( first != null ) {  children[k] = first; k++; }
        if( second != null ) {  children[k] = second; k++; }
        if( third != null ) {  children[k] = third; k++; }

        return children;
    }

    //******************************************************
    // graphical display of this node and its subtree
    // in given camera, with specified location (x,y) of this
    // node, and specified distances horizontally and vertically
    // to children
    public void draw( Camera cam, double x, double y, double h, double v ) {

        System.out.println("draw node " + id );

        // set drawing color
        cam.setColor( Color.black );

        String text = kind;
        if( ! info.equals("") ) text += "(" + info + ")";
        cam.drawHorizCenteredText( text, x, y );

        // positioning of children depends on how many
        // in a nice, uniform manner
        Node[] children = getChildren();
        int number = children.length;
        System.out.println("has " + number + " children");

        double top = y - 0.75*v;

        if( number == 0 ) {
            return;
        }
        else if( number == 1 ) {
            children[0].draw( cam, x, y-v, h/2, v );     cam.drawLine( x, y, x, top );
        }
        else if( number == 2 ) {
            children[0].draw( cam, x-h/2, y-v, h/2, v );     cam.drawLine( x, y, x-h/2, top );
            children[1].draw( cam, x+h/2, y-v, h/2, v );     cam.drawLine( x, y, x+h/2, top );
        }
        else if( number == 3 ) {
            children[0].draw( cam, x-h, y-v, h/2, v );     cam.drawLine( x, y, x-h, top );
            children[1].draw( cam, x, y-v, h/2, v );     cam.drawLine( x, y, x, top );
            children[2].draw( cam, x+h, y-v, h/2, v );     cam.drawLine( x, y, x+h, top );
        }
        else {
            System.out.println("no Node kind has more than 3 children???");
            System.exit(1);
        }

    }// draw

    public static void error( String message ) {
        System.out.println( message );
        System.exit(1);
    }

    // ===============================================================
    //   execute/evaluate nodes
    // ===============================================================

    // ask this node to execute itself
    // (for nodes that don't return a value)
    public void execute() {

//      System.out.println("Executing node " + id + " of kind " + kind );

        if ( kind.equals("list") ) {
            if (first != null){
                first.execute();  // execute the "main" funcCall
            }


        }// list
        else if ( kind.equals("expr") ) {
            


        }// expr

        else if ( kind.equals("params") ) {
            // execute a function call as a statement

            String funcName = info;

            // handle bifs
            if ( funcName.equals("print") ) {
                // evaluate the single <expr>
                double value = first.first.evaluate();
                if ( (int) value == value )
                    System.out.print( (int) value );
                else
                    System.out.print( value );
            }
            else if ( funcName.equals("nl") ) {
                System.out.println();
            }

            else {// user-defined function

                Node body = passArgs( this, funcName );
                body.second.execute();

                returning = false;

            }// user-defined function

        }// params

        else if ( kind.equals("expr") ) {
            System.out.print( info );
        }// str

        else if ( kind.equals("list") ) {
            evaluate();

        }// list

        else if ( kind.equals("items") ) {
            double question = first.evaluate();
            if ( question != 0 ) {
                second.execute();
            }
            else {
                third.execute();
            }
        }// if

        else {
            error("Executing unknown kind of node [" + kind + "]");
        }

    }// execute

    // compute and return value produced by this node
    public double evaluate() {

//      System.out.println("Evaluating node " + id + " of kind " + kind );

        if ( kind.equals("list") ) {
            String funcName = first.info;
            if ( funcName.equals("ins") ) {
                x = first.first.info;
                y = first.second.info;
                ArrayList<> n = new ArrayList<>(table.retreive(y));
                n.add(0, x);
                return n;

            }
            else if ( funcName.equals("first") ) {
                System.out.println();
            }
            else if ( funcName.equals("rest") ) {
                System.out.println();
            }
            else if ( funcName.equals("num") ) {
                System.out.println();
            }
            else if ( funcName.equals("list") ) {
                System.out.println();
            }
            else if ( funcName.equals("read") ) {
                System.out.println();
            }
            else if ( funcName.equals("write") ) {
                System.out.println();
            }
            else if ( funcName.equals("nl") ) {
                System.out.println();
            }
            else if ( funcName.equals("quote") ) {
                System.out.println();
            }
            else if ( funcName.equals("quit") ) {
                System.out.println();
            }
            else if ( funcName.equals("plus") ) {
                System.out.println();
            }
            else if ( funcName.equals("minus") ) {
                System.out.println();
            }
            else if ( funcName.equals("times") ) {
                System.out.println();
            }
            else if ( funcName.equals("div") ) {
                System.out.println();
            }
            else if ( funcName.equals("lt") ) {
                System.out.println();
            }
            else if ( funcName.equals("le") ) {
                System.out.println();
            }
            else if ( funcName.equals("eq") ) {
                System.out.println();
            }
            else if ( funcName.equals("ne") ) {
                System.out.println();
            }
            else if ( funcName.equals("and") ) {
                System.out.println();
            }
            else if ( funcName.equals("or") ) {
                System.out.println();
            }
            else if ( funcName.equals("not") ) {
                System.out.println();
            }
            else if ( funcName.equals("null") ) {
                System.out.println();
            }
            else if ( funcName.equals("") ) {

            }
            else {// user-defined function

                Node body = passArgs(this, funcName);
                body.second.execute();

                returning = false;
            }
        }// list
        }// funcCall

        else {
            error("Evaluating unknown kind of node [" + kind + "]" );
            return -1;
        }

    }// evaluate

    private final static String[] bif0 = { "input", "nl" };
    private final static String[] bif1 = { "sqrt", "cos", "sin", "atan",
            "round", "trunc", "not" };
    private final static String[] bif2 = { "lt", "le", "eq", "ne", "pow",
            "or", "and"
    };

    // return whether target is a member of array
    private static boolean member( String target, String[] array ) {
        for (int k=0; k<array.length; k++) {
            if ( target.equals(array[k]) ) {
                return true;
            }
        }
        return false;
    }

    // given a funcCall node, and for convenience its name,
    // locate the function in the function defs and
    // create new memory table with arguments values assigned
    // to parameters
    // Also, return root node of body of the function being called
    private static Node passArgs( Node funcCallNode, String funcName, Node defRootNode ) {

        // locate the function in the function definitions

        Node node = defRootNode;  // the program node
        node = node.second;  // now is the funcDefs node
        Node fdnode = null;
        while ( node != null && fdnode == null ) {
            if ( node.first.info.equals(funcName) ) {// found it
                fdnode = node.first;
                // System.out.println("located " + funcName + " at node " +
                //                     fdnode.id );
            }
            else
                node = node.second;
        }

        MemTable newTable = new MemTable();

        if ( fdnode == null ) {// function not found
            error( "Function definition for [" + funcName + "] not found" );
            return null;
        }
        else {// function name found
            Node pnode = fdnode.first; // current params node
            Node anode = funcCallNode.first;  // current args node
            while ( pnode != null && anode != null ) {
                // store argument value under parameter name
                newTable.store( pnode.first.info,
                        anode.first.evaluate() );
                // move ahead
                pnode = pnode.second;
                anode = anode.second;
            }

            // detect errors
            if ( pnode != null ) {
                error("there are more parameters than arguments");
            }
            else if ( anode != null ) {
                error("there are more arguments than parameters");
            }

//         System.out.println("at start of call to " + funcName +
//                           " memory table is:\n" + newTable );

            // manage the memtable stack
            memStack.add( newTable );
            table = newTable;

            return fdnode;

        }// function name found

    }// passArguments

}// Node