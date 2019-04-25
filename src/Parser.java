import java.util.*;
import java.io.*;
public class Parser {

    private Lexer lex;
    private Parser parsed;

    public Parser( Lexer lexer, Parser parser ) {
        lex = lexer;
        parsed = parser;
    }
    public Node parseProgram(){
      Token token = lex.getNextToken();
      errorCheck(token, "LPAREN", "(");
      token = lex.getNextToken();
      if(token.isKind("NAME")){
        Node first = parseName();
        lex.putBackToken(token);
        return new Node("PROGRAM",first,null,null);
      }
      else if(token.isKind("defs")){
        Node first = parseDefs();
        lex.putBackToken(token);
        return new Node("PROGRAM",first,null,null);
      }
      error("if type isnt name or defs this isn't a valid input/file");
      return new Node(token);
    }
    public Node parseName(){
      Token token = lex.getNextToken();
      Node thisNode = new Node("NAME",token.getDetails(),null,null,null);
      Node first = findNode(parsed.parseProgram(),thisNode);
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
            return new Node("def", name.getDetails(),
                    first, null, null);
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
            return new Node("def", name.getDetails(),
                    first, second, null);
        }
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
        errorCheck(token, "LPAREN", "(");

        token = lex.getNextToken();

        // empty list
        if ( token.isKind("RPAREN") ) {
            return new Node("list", null, null, null);
        }
        // function call
        else if ( token.isKind("NAME")){
            Node first = parseItems();
            String funcType = token.getDetails();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
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
        System.out.println("-----> parsing <items>:");

        Node first = parseExpr();
        Token token = lex.getNextToken();

        // end of items
        if ( token.isKind("RPAREN") ) {
            lex.putBackToken( token );
            return new Node("items", first, null, null);
        }
        // either a list or a
        else {
            lex.putBackToken( token );
            Node second = parseItems();
            return new Node("items", first, second, null);
        }
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
    private Node findNode(Node start, Node end){
      Node[] getStartChildren = start.getChildren();

      if(start.getKind().equals(end.getKind())&&start.getInfo().equals(end.getInfo())){
        return start;
      }
      else{
        if(getStartChildren.length==0){
          error("It aint there dog");
        }
        else if(getStartChildren.length==1){
          findNode(getStartChildren[0],end);
        }
        else if(getStartChildren.length==2){
          findNode(getStartChildren[0],end);
          findNode(getStartChildren[1],end);
        }
        error("What even happened");
        return end;
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

}
