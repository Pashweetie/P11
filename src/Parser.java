import java.util.*;
import java.io.*;
public class Parser {

    private Lexer lex;
    private Parser parsed;
    private ArrayList<Node> defs = new ArrayList<>();

    public Parser( Lexer lexer, Parser parser ) {
        lex = lexer;
        parsed = parser;
    }
    public Node parseProgram(){
      Token token = lex.getNextToken();
      if(token.isKind("eof")){
          System.out.println("hitting an eof");
        return new Node("Null","null",null,null,null);
      }
      errorCheck(token, "LPAREN", "(");
      token = lex.getNextToken();
      if(token.isKind("NAME")){
          lex.putBackToken(token);
        Node first = parseName();
        return new Node("program",first,null,null);
      }
      else if(token.isKind("defs")){
        Node first = parseDefs();
        lex.putBackToken(token);
        return new Node("program",first,null,null);
      }
      else if(token.isKind("KEYWORD")){
          lex.putBackToken(token);
          return parseList();
      }
      error("if type isnt name or defs this isn't a valid input/file");
      return new Node(token);
    }
    public Node parseName(){
      Token token = lex.getNextToken();
      Node thisNode = new Node("NAME",token.getDetails(),null,null,null);
      Node first = null;
      if(parsed.defs.contains(thisNode)){
          first = findNode(thisNode);
      } else{
          System.out.println("ParserNameError: Def " + token.getDetails() + " does not exist.");
          System.exit(1);
      }
      if(first!=null){
        return new Node("NAME",token.getDetails(),first,null,null);
      }
      else{
        return new Node("NAME",token.getDetails(),null,null,null);
      }

    }
    public Node parseDefs() {
        System.out.println("-----> parsing <defs>:");
        Node first = parseDef();
        Token token = lex.getNextToken();
        System.out.println("does this even happen?");
        if ( token.isKind("eof") ){
            return new Node("defs", first, null, null);
        }
        else {
            lex.putBackToken(token);
            Node second = parseDefs();
            return new Node("defs", first, second, null);
        }
    }
    public Node parseDef(){
        System.out.println("-----> parsing <def>:");

        Token token = lex.getNextToken();
        errorCheck(token, "LPAREN", "(");

        // needs to check for define specifically
        token = lex.getNextToken();
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
            Node def1 = new Node("def",name.getDetails(),first, null,null);
            System.out.println("happens");
            defs.add(def1);
            return def1;
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
            Node def1 = new Node("def",name.getDetails(),first, second,null);
            System.out.println("happens");
            defs.add(def1);
            return def1;
        }
    }
    public ArrayList<Node> getDefs(){
      return defs;
    }
    public Node parseParams() {
        System.out.println("-----> parsing <params>:");

        Token name = lex.getNextToken();
        errorCheck(name, "NAME");

        Token token = lex.getNextToken();

        if ( token.isKind("RPAREN") ) {
            lex.putBackToken( token );
            return new Node("params", name.getDetails(),
                    null, null, null);
        }
        else{
            lex.putBackToken( token );
            Node first = parseParams();
            return new Node("params", name.getDetails(),
                    first, null, null);
        }
    }

    public Node parseExpr() {
        System.out.println("-----> parsing <expr>:");

        Token token = lex.getNextToken();

        // is a list
        if ( token.isKind("LPAREN") ) {
            lex.putBackToken( token );
            Node first = parseList();
            return new Node("expr", first, null, null);
        }
        // is a num
        else{
            return new Node("expr", token.getDetails(),
                    null, null, null);
        }
    }

    public Node parseList(){
        System.out.println("-----> parsing <list>:");

        Token token = lex.getNextToken();

        // empty list
        if ( token.isKind("RPAREN") ) {
            return new Node("list", null, null, null);
        }
        // function call
        else if ( token.isKind("NAME") || token.isKind("KEYWORD")){
            String funcType = token.getDetails();
            token = lex.getNextToken();

            // Some functions don't have any args, so handle these here
            if(token.isKind("RPAREN")){
                return new Node("list", funcType, null, null, null);
            }
            lex.putBackToken(token);

            // only one node here, since the items of a function call are defined as one unit, with micro expressions
            Node first = parseItems();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN");
            return new Node("list", funcType, first, null, null);
        }
        // just a list
        else {
            lex.putBackToken( token );
            Node first = parseItems();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            return new Node("list", first, null, null);
        }
    }

    public Node parseItems(){
        // wait, an Item can be an expr, or an expr followed by more items
        System.out.println("-----> parsing <items>:");

        // The required expression, all items must have at least one
        Node first = parseExpr();
        Token token = lex.getNextToken();

        // If there is no rparen, there are more items
        if(!token.isKind("RPAREN")){
            lex.putBackToken(token);
            Node second = parseItems();
            return new Node("items", first, second, null);
        }
        return new Node("items", first, null, null);
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
    private Node findNode(Node start){
      for(Node node : parsed.defs){
          if(start.getInfo().equals(node.getInfo())){
              return node;
          }
      }
      return null;
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

}