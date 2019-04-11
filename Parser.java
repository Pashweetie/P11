/*
    This class provides a recursive descent parser
    for Corgi (the new version),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

import java.util.*;
import java.io.*;

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

        token = lex.getNextToken();
        errorCheck(token, "KEYWORD");

        token = lex.getNextToken();
        errorCheck(token, "LPAREN", "(");

        Token name = lex.getNextToken();
        errorCheck(name, "NAME");

        token = lex.getNextToken();

        if ( token.isKind("RPAREN") ) {
            Node first = parseExpr();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            return new Node("def", name.getDetails(),
                    first, null, null);
        }
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

        if ( token.isKind("NUMBER") )
        {
            return new Node(token);
        }
        else if ( token.isKind("LPAREN") ) {
            lex.putBackToken( token );
            Node first = parseList();
            return new Node("expr", first, null, null);
        }
        else
        {
        return new Node("expr", token.getDetails(), null, null, null);
        }
    }

    public Node parseList(){
        System.out.println("-----> parsing <list>:");

        Token token = lex.getNextToken();
        errorCheck(token, "LPAREN", "(");

        token = lex.getNextToken();

        if ( token.isKind("RPAREN") ) {
            return new Node("list", null, null, null);
        }
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

        if ( token.isKind("RPAREN") ) {
            lex.putBackToken( token );
            return new Node("items", first, null, null);
        }
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
