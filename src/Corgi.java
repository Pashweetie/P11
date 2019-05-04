import java.io.File;
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
        Parser fileParser = new Parser(fileLex,null );
        Scanner scan = new Scanner(System.in);
        Node root2 = fileParser.parseProgram();

        System.out.println("\nPlease input a command below: ");
        System.out.print("\n> ");
        String command = scan.nextLine();

        // REPL bash
        while(!command.equals("exit")) {
            // Objects relating to REPL
            Lexer inputLex = new Lexer(command);
            Parser inputParser = new Parser(inputLex, fileParser);

            // root node for REPL
            Node root = inputParser.parseProgram();

            // display parse tree for debugging/testing:
            //TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root);

            Item ans = root.evaluate();

            // return the answer
            if (ans.getList() == null) System.out.println(command + " = " + ans.getNum());
            else System.out.println(command + " = " + ans.getList());

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
        }


    }// main

}
