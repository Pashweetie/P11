import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Corgi {
    public static void main(String[] args){
        String name;

        if ( args.length == 1 ) {
            name = args[0];
        }
        else {
            System.out.print("Enter name of Corgi program file: ");
            Scanner keys = new Scanner( System.in );
            name = keys.nextLine();
        }

        // Objects relating to predefined functions
        Lexer fileLex = new Lexer(name);
        Parser fileParser = new Parser(fileLex);
        Scanner scan = new Scanner(System.in);
        Node root2 = fileParser.parseProgram();
        fileLex.closeStream();

        ArrayList<Node> defs = fileParser.getDefs(); // will this work?
        ArrayList<String> defNames = fileParser.getDefNames();


        System.out.println("\nPlease input a command below: ");
        System.out.print("\n> ");
        String command = scan.nextLine();

        // REPL bash
        while(!command.equals("(quit)")) {
            // Objects relating to REPL
            Lexer inputLex = new Lexer(command);
            Parser inputParser = new Parser(inputLex);
            // root node for REPL
            Node root = inputParser.parseProgram();

            // display parse tree for debugging/testing:
            //TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root);

            Value ans = root.evaluate(defs, defNames, null);

            // return the answer
            System.out.println(ans.toString());

            // try deleting repl.txt
            try {
                File replFile = new File("files/repl.txt");
                inputLex.closeStream(); // close input stream, so we can delete replFile
                if (replFile.delete()) {
                    System.out.println("File deleted successfully !");
                } else {
                    System.out.println("File delete operation failed !");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.print("\n> ");
            command = scan.nextLine();
        }// REPL bash
    }// main
}
