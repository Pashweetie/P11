import java.util.Scanner;

public class Corgi {

    public static void main(String[] args) throws Exception {

        String name;
        String ins;
        Scanner keys;
        Lexer exLex;
        Parser exParser;
        Node exNode;

        if ( args.length == 1 ) {
            name = args[0];
        }
        else {
            System.out.print("Enter name of Corgi program file: ");
            keys = new Scanner( System.in );
            name = keys.nextLine();
        }

        Lexer lex = new Lexer( name, true);
        Parser parser = new Parser( lex );

        // start with <statements>
        Node root = parser.parseProgram();

        // display parse tree for debugging/testing:
        TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root );

        // execute the parse tree
        do {
            System.out.print("Enter Execution: ");
            keys = new Scanner(System.in);
            ins = keys.nextLine();
            exLex = new Lexer( name, true);
            exParser = new Parser( exLex );
            exNode = exParser.parseList();
            exNode.execute();
            System.out.print("\n");
        }
        while(!ins.equals("exit"));

    }// main

}