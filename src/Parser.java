import java.util.*;
import java.io.*;
public class Parser {

    private Lexer lex;
    private HashMap<String, Node> defs = new HashMap<>();
    int rParenCount = 0;

    public Parser( Lexer lexer) {
            lex = lexer;
    }

    public Node parseProgram(){
          Token token = lex.getNextToken();
          if(token.isKind("eof")){
              System.out.println("hitting an eof");
            return new Node("Null","null",null,null);
          }
          errorCheck(token, "LPAREN", "(");
          token = lex.getNextToken();
          if(token.getDetails().equals("define") && token.isKind("KEYWORD")){
              System.out.println("Parsing define...");
              lex.putBackToken(token);
              Node first = parseDefs();
              return new Node("program", first, null);
          }
          else if(token.isKind("KEYWORD")){
              System.out.println("Parsing list...");
              lex.putBackToken(token);
              return parseList();
          }
          // need to handle where a list goes
          error("if type isnt name or defs this isn't a valid input/file");
          return new Node(token);
    }

    public Node parseDefs() {
        System.out.println("-----> parsing <defs>:");
        Node first = parseDef();
        Token token = lex.getNextToken();
        if ( token.isKind("eof") ){
            return new Node("defs", first, null);
        }
        else {
            errorCheck(token, "LPAREN");
            Node second = parseDefs();
            return new Node("defs", first, second);
        }
    }

    public Node parseDef(){
        Node def1;
        System.out.println("-----> parsing <def>:");

        // needs to check for define specifically
        Token token = lex.getNextToken();
        errorCheck(token, "KEYWORD", "define");

        token = lex.getNextToken();
        errorCheck(token, "LPAREN", "(");

        Token name = lex.getNextToken();
        errorCheck(name, "NAME");
        token = lex.getNextToken();

        // if no params
        if ( token.isKind("RPAREN") ) {
            Node first = parseExpr();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            def1 = new Node("def",name.getDetails(),first, null);
        }
        // if params found
        else {
            lex.putBackToken( token );
            Node first = parseParams();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            Node second = parseExpr();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            def1 = new Node("def",name.getDetails(),first, second);
        }

        defs.put(def1.getInfo(), def1);
        return def1;
    }

    public Node parseParams() {
        System.out.println("-----> parsing <params>:");

        Token name = lex.getNextToken();
        errorCheck(name, "NAME");

        Token token = lex.getNextToken();

        if ( token.isKind("RPAREN") ) {
            lex.putBackToken( token );
            return new Node("params", name.getDetails(), null, null);
        }
        else{
            lex.putBackToken( token );
            Node first = parseParams();
            return new Node("params", name.getDetails(), first, null);
        }
    }

    public Node parseExpr() {
        System.out.println("-----> parsing <expr>:");

        Token token = lex.getNextToken();

        // is a list
        if ( token.isKind("LPAREN") ) {
            Node first = parseList();
            return new Node("expr", first, null);
        }
        // is a named variable
        else if ( token.isKind("NAME")){
            return new Node("name", token.getDetails(), null, null);
        }
        // is a num
        else{
            return new Node("expr", token.getDetails(), null, null);
        }
    }

    public Node parseList(){
        System.out.println("-----> parsing <list>:");

        Token token = lex.getNextToken();
        // empty list
        if ( token.isKind("RPAREN") ) {
            return new Node("list", null, null);
        }
        // function call
        else if ( token.isKind("NAME") || token.isKind("KEYWORD")){
            String funcType = token.getDetails(); // will be empty string if it is just a list of items
            token = lex.getNextToken();

            // Some functions/lists don't have any items, so handle these here
            if(token.isKind("RPAREN")){
                return new Node("list", funcType, null, null);
            }
            // encountered a nested list or function call
            else if(token.isKind("LPAREN")){
                // first is a list or function call
                Node first = parseList();

                token = lex.getNextToken();
                // if next item happens to be another list or function call
                if(token.isKind("LPAREN")){
                    Node second = parseList();
                    token = lex.getNextToken();
                    errorCheck(token, "RPAREN"); // close greater list
                    rParenCount++;
                    System.out.println("count: " + rParenCount);
                    return new Node("list", funcType, first, second);
                }
                // if next item is just a number
                else if(!token.isKind("RPAREN")){
                    lex.putBackToken(token);
                    Node second = parseItems();
                    token = lex.getNextToken();
                    errorCheck(token, "RPAREN");
                    rParenCount++;
                    System.out.println("count: " + rParenCount);
                    return new Node("list", funcType, first, second);
                }
                // if there is no next item
                else{
                    errorCheck(token, "RPAREN");
                    rParenCount++;
                    System.out.println("count: " + rParenCount);
                    return new Node("list", funcType, first, null);
                }
            }
            // only one node here, since the items of a function call are treated like a list
            // A list will be treated exactly the same
            lex.putBackToken(token);
            Node first = parseItems();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN");
            rParenCount++;
            System.out.println("count: " + rParenCount);
            return new Node("list", funcType, first, null);
        }
        // just a list
        else {
            lex.putBackToken( token );
            Node first = parseItems();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            return new Node("list", first, null);
        }
    }

    public Node parseItems(){
        // wait, an Value can be an expr, or an expr followed by more items
        System.out.println("-----> parsing <items>:");

        // The required expression, all items must have at least one
        Node first = parseExpr();
        Token token = lex.getNextToken();

        // If there is no rparen, there are more items
        if(!token.isKind("RPAREN")){
            lex.putBackToken(token);
            Node second = parseItems();

            return new Node("items", first, second);
        }

        lex.putBackToken(token);
        return new Node("items", first, null);
    }

    private void error(String message){
      System.out.println(message);
      System.exit(1);
    }

    // check whether token is correct kind
    private void errorCheck( Token token, String kind ) {
        if( ! token.isKind( kind ) ) {
            System.out.println("Error:  expected " + token +
                    " to be of kind " + kind );
              try {
               File fileToDelete = new File("files/repl.txt");

               if (fileToDelete.delete()) {
                System.out.println("File deleted successfully !");
               } else {
                System.out.println("File delete operation failed !");
               }

              } catch (Exception e) {
               e.printStackTrace();
              }
            System.exit(1);
        }
    }

    // check whether token is correct kind and details
    private void errorCheck( Token token, String kind, String details ) {
        if( ! token.isKind( kind ) ||
                ! token.getDetails().equals( details ) ) {
            System.out.println("Error:  expected " + token +
                    " to be kind= " + kind +
                    " and details= " + details );
            System.exit(1);
        }
    }

    public HashMap<String, Node> getDefs(){
        return defs;
    }

}
