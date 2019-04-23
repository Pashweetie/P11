public class Parser {

    private Lexer lex;

    public Parser( Lexer lexer ) {
        lex = lexer;
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
        // either a list or a num
        else {
            lex.putBackToken( token );
            Node second = parseItems();
            return new Node("items", first, second, null);
        }
    }

    // check whether token is correct kind
    private void errorCheck( Token token, String kind ) {
        if( ! token.isKind( kind ) ) {
            System.out.println("Error:  expected " + token +
                    " to be of kind " + kind );
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

}
