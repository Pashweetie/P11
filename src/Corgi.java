import java.util.Scanner;

public class Corgi {

    public static void main(String[] args) throws Exception {

        String name;

        if ( args.length == 1 ) {
            name = args[0];
        }
        else {
            System.out.print("Enter name of Corgi program file: ");
            Scanner keys = new Scanner( System.in );
            name = keys.nextLine();
        }

        Lexer fileLex = new Lexer( name );
        Parser fileParser = new Parser(fileLex,null );
        Scanner command = new Scanner(System.in);
        Node root2 = fileParser.parseProgram();

        if(fileParser.getDefs()!=null){
          System.out.println("its not null tho");
        }

        Lexer inputLex = new Lexer(command.nextLine());
        Parser inputParser = new Parser( inputLex, fileParser );

        // start with <statements>

        Node root = inputParser.parseProgram();

        // display parse tree for debugging/testing:
        TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root );

        // execute the parse tree
        root.evaluate();

    }// main

}
