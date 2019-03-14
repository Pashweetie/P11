import java.util.*;
import java.io.*;
public class Lexer {

    public static String margin = "";

    // holds any number of tokens that have been put back
    private Stack<Token> stack;

    // the source of physical symbols
    // (use BufferedReader instead of Scanner because it can
    //  read a single physical symbol)
    private BufferedReader input;

    // one lookahead physical symbol
    private int lookahead;

    // table containing valid escape chars
    private Hashtable<String, Character> escapeChars = new Hashtable<>();

    // construct a Lexer ready to produce tokens from a file
    public Lexer( String fileName ) {
        try {
            input = new BufferedReader( new FileReader( fileName ) );
        }
        catch(Exception e) {
            error("Problem opening file named [" + fileName + "]" );
        }
        stack = new Stack<>();
        lookahead = 0;  // indicates no lookahead symbol present
    }// constructor

    // produce the next token
    private Token getNext() {
        if( ! stack.empty() ) {
            //  produce the most recently putback token
            return stack.pop();
        }
        else {
            // produce a token from the input source

            int state = 0;  // state of FA
            String data = "";  // specific info for the token
            boolean done = false;
            int sym;  // holds current symbol

            String eChar = "";


            do {
                sym = getNextSymbol();

//                System.out.println("current symbol: " + sym + " state = " + state );

                if ( state == 0 ) {
                    if ( sym == 9 || sym == 10 || sym == 13 ||
                            sym == 32 ) {// whitespace
                        state = 0;
                    }
                    else if ( upper(sym) ) {// upercase letter
                        data += (char) sym;
                        state = 1;
                    }
                    else if ( uppercase(sym) ) {
                        data += (char) sym;
                        state = 1;
                    }
                    else if ( lowercase(sym) ){
                        data += (char) sym;
                        sate = 2
                    }
                    else if ( sym == '-') {
                      data += (char) sym;
                      state = 3;
                    }
                    else if ( digit( sym ) ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else if ( sym == '"' ) {
                        data += (char) sym;
                        state = 6;
                    }
                    // '-' in state 3 and '/' in state 9
                    else if ( sym == '+' ||sym == '*' ) {
                        data += (char) sym;
                        state = 8;
                    }
                    // no comma?
                    else if ( sym == '(' || sym == ')' ||
                    sym == '=' || sym == '.' || sym == ',' || sym == ';')
                    {
                        data += (char) sym;
                        state = 16;
                    }
                    else if ( sym == '/' ) {
                        state = 9;
                    }
                    else if ( sym == -1 ) {// end of file
                        state = 15;
                        done = true;
                    }
                    else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }



                //
                //uppercase
                else if ( state == 1 ) {
                    if ( uppercase(sym) || digit(sym)) {
                        data += (char) sym;
                        state = 1;
                    }
                    else {// done with variable token
                        putBackSymbol( sym );
                        done = true;
                    }
                }

                else if (state == 2){
                    if (lowercase(sym) || digit(sym)){
                        data +=(char) sym;
                        state = 2;
                    }
                    else{
                        putBackSymbol( sym );
                        done = true;
                    }
                }


                //

                else if ( state == 3 ) {
                    if ( digit(sym) ) {
                        data += (char) sym;
                        state = 3;
                    }
                    else if ( sym == '.' ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else {// done with number token
                        putBackSymbol( sym );
                        done = true;
                    }

                }

                else if ( state == 4 ) {
                    if ( digit(sym) ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else {// done with number token
                        putBackSymbol( sym );
                        done = true;
                    }
                }

                else if ( state == 5) {
                    if ( digit(sym) ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }

                else if ( state == 6 ) {
                    if ( (' '<=sym && sym<='~') && sym != '\"' && sym != '\\') {
                        data += (char) sym;
                        state = 6;
                    } else if( sym == '\\'){
                        state = 11;
                    }
                    else if ( sym == '\"' ) {
                        state = 7;
                        done = true;
                    }
                }

                // note: states 7, 8, and 9 are accepting states with
                //       no arcs out of them, so they are handled
                //       in the arc going into them

                else if ( state == 10 ) {// saw /, might be single or comment
                    if ( sym == '*' ) {// starting comment
                        state = 11;
                    }
                    else {
                        putBackSymbol( sym );
                        return new Token( "single", "/" );
                    }
                }

                else if ( state == 11 ) {// escape char dig 1
                    if ( digit(sym) ) {
                        state = 12;
                        eChar += (char) sym;
                    }
                    else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }

                else if ( state == 12 ) {// escape char dig 2
                    if ( digit(sym) ) {
                        state = 13;
                        eChar += (char) sym;
                    }
                    else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }

                else if ( state == 13 ) {// escape char dig 3
                    if ( digit(sym) ) {
                        state = 14;
                        eChar += (char) sym;
                    } else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }

                else if ( state == 14 ) {// find escape char, return to char array building
                    if(escapeChars.contains(eChar)){
                        data += escapeChars.get(eChar);
                        eChar = "";
                        state = 6;
                    } else{
                        error("Error in lexical analysis phase: /" + eChar + " is not a valid " +
                                "escape character.");
                    }
                    // Took out an unnecessary symbol for this phase
                    putBackSymbol(sym);
                    /* QUESTIONS:
                     * Can escape chars be used outside of strings?
                     * Should we create an Escape Char token?
                       Or should we find the escape char in state 14?
                     */

                }
                else if(state==8){
                  error("You used a basic operator, this is illegal");
                }
                else if (state==16){
                  data += (char) sym;
                  done = true;
                }

            }while( !done );

            // generate token depending on stopping state
            Token token;

            if (state == 1){
                return new Token("class", data)
            }
            if (state == 2){
                if (data.equals("static") || data.equals("for")||
                    data.equals("return") || data.equals("if") ||
                    data.equals("else") || data.equals("new") ||
                    data.equals("void") || data.equals("this")||
                    data.equals("true") || data.equals("false"))
            }
                return new Token(data, "");
            else{
                return new Token ("var", data);
            }


            if (state == 2 ){
                if ( data.equals("for") || data.equals())
            }
            if ( state == 2 ) {
                // now anything starting with letter is either a
                // key word or a "var"
                if ( data.equals("def") || data.equals("end") ||
                        data.equals("if") || data.equals("else") ||
                        data.equals("return")
                        ) {
                    return new Token( data, "" );
                }
                else {
                    return new Token( "var", data );
                }
            }
            else if ( state == 3 || state == 4 ) {
                return new Token( "num", data );
            }
            else if ( state == 7 ) {
                return new Token( "string", data );
            }
            else if ( state == 9 ) {
                return new Token( "eof", data );
            }
            else if (state == 16) {
                if(data.equals("{")){
                  return new Token ("LBRACE",data);
                } else if(data.equals("}")){
                  return new Token ("RBRACE",data);
                } else if(data.equals("(")){
                  return new Token ("LPAREN",data);
                } else if(data.equals(")")){
                  return new Token ("RPAREN",data);
                } else if(data.equals("=")){
                  return new Token ("EQUALS",data);
                } else if(data.equals(",")){
                  return new Token ("COMMA",data);
                } else if(data.equals(";")){
                  return new Token ("SEMICOLON",data);
                } else if(data.equals(".")){
                  return new Token ("DOT",data);
                } else{
                    return null;
                }
            }

            else {// Lexer error
                error("somehow Lexer FA halted in bad state " + state );
                return null;
            }

        }// else generate token from input

    }// getNext

    public Token getNextToken() {
        Token token = getNext();
        System.out.println("  got token: " + token );
        return token;
    }

    public void putBackToken( Token token )
    {
        System.out.println( margin + "put back token " + token.toString() );
        stack.push( token );
    }

    // next physical symbol is the lookahead symbol if there is one,
    // otherwise is next symbol from file
    private int getNextSymbol() {
        int result = -1;

        if( lookahead == 0 ) {// is no lookahead, use input
            try{  result = input.read();  }
            catch(Exception e){}
        }
        else {// use the lookahead and consume it
            result = lookahead;
            lookahead = 0;
        }
        return result;
    }

    private void putBackSymbol( int sym ) {
        if( lookahead == 0 ) {// sensible to put one back
            lookahead = sym;
        }
        else {
            System.out.println("Oops, already have a lookahead " + lookahead +
                    " when trying to put back symbol " + sym );
            System.exit(1);
        }
    }// putBackSymbol

    /*private boolean letter( int code ) {
        return 'a'<=code && code<='z' ||
                'A'<=code && code<='Z';
    }*/

    private boolean uppercase(int code){
        return 'A'<=code && code<='Z'
    }
    private boolean lowercase(int code){
        return 'a'<=code && code<='z'
    }

    private boolean upper( int code ){
        return 'A'<=code && code<='Z';
    }

    private boolean lower( int code ){
        return 'a'<=code && code<='z';
    }

    private boolean digit( int code ) {
        return '0'<=code && code<='9';
    }

    private boolean printable( int code ) {
        return ' '<=code && code<='~';
    }

    private static void error( String message ) {
        System.out.println( message );
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        System.out.print("Enter file name: ");
        Scanner keys = new Scanner( System.in );
        String name = keys.nextLine();

        Lexer lex = new Lexer( name );
        Token token;

        do{
            token = lex.getNext();
            System.out.println( token.toString() );
        }while( ! token.getKind().equals( "eof" )  );

    }

}
