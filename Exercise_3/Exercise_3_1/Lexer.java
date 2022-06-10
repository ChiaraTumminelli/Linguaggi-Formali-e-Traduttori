package Exercise_3.Exercise_3_1;

import java.io.*; 

// Exercise 2.1
public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    // Case of error
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    // ... Method to be completed ...
    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r' || peek == '/') {

            if (peek == '\n') {
                line++;
            }

            // Exercise 2.3
            // Difference between multiline comment and single line comment  
            if (peek == '/') {
                readch(br);

                // Single line comment
                if (peek == '/') {
                    // It reads until it finds new line escape sequence '\n'
                    while (peek != '\n') {
                        readch(br);
                    }
                    line++;
                }

                // Multiline comment
                else if (peek == '*') {
                    // It reads until it finds '*/'
                    String c_close = "";
                    readch(br);
                    while (peek != (char) -1 && !c_close.equals("*/")) {
                        if ((peek == '*' && c_close.isEmpty()) || (peek == '/' && c_close.equals("*"))) {
                            c_close += peek;
                        } else {
                            c_close = "";
                        }
                        readch(br);
                    }

                    // If it finds EOF before the symbol '*/' then I have an error 
                    if (!c_close.equals("*/")) {
                        System.err.println("Missing multi-line comment close!");
                        return null; 
                    }
                }

                // If there are no other characters, it is the division symbol
                else {
                    return Token.div;
                }
            } else {
                readch(br);
            }

        }


        // Characters taken from the Token class
        switch (peek) {
            
            case '!':
                peek = ' ';
                return Token.not;
    
            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;
    
            // Characters taken from the Word class
            // Early reading with double buffer and sentinels cases
            // Case of logical OR
            case '|':
                readch(br);
                if (peek == '|'){
                    peek = ' ';
                    return Word.or;
                } else {
                    System.out.println("Erroneous character" + " after | : " + peek);
                    return null;
                }

            // Case of logical AND
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after & : "  + peek );
                    return null;
                }

            // Case of <=, case of <> and case of <
            case '<':
                readch(br);
                if (peek == ' '){
                    peek = ' ';
                    return Word.lt;
                } else if (peek == '='){
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>'){
                    peek = ' ';
                    return Word.ne;
                } else {
                    System.out.println("Erroneous character" + " after < : " + peek);
                    return null;
                }

            // Case of > and case of >=
            case '>':
                readch(br);
                if (peek == ' '){
                    peek = ' ';
                    return Word.gt;
                } else if (peek == '='){
                    peek = ' ';
                    return Word.ge;
                } else {
                    System.out.println("Erroneous character" + " after > : " + peek);
                    return null;
                }

            // Case of = and case of ==
            case '=':
                readch(br);
                if (peek == '='){
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.out.println("Erroneous character" + " after = : " + peek);
                    return null;
                }

            // Case of End Of File (EOF)
            case (char)-1:
                return new Token(Tag.EOF);

            // Exercise 2.2
            // Case of identifiers, keywords and values
            default:
                String str = "";

                if (Character.isLetter(peek) || peek == '_') {
                    boolean valid_underscore = false;
                    str += peek;
                    readch(br);

                    if(str.equals("_")){
                        while(!valid_underscore){
                            if(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){
                                str += peek;
                                if(Character.isLetter(peek) || Character.isDigit(peek)){
                                    valid_underscore = true;
                                }
                                readch(br);
                            }
                            else if(!Character.isLetter(peek) && !Character.isDigit(peek)){
                                System.err.println("Only underscore var");
                                return null;     
                            }
                        }
                    } 


                    while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                        str += peek;
                        readch(br);
                    }

                    
                    switch (str) {
                        case "assign":
                            return Word.assign;
                        case "to":
                            return Word.to;
                        case "if":
                            return Word.iftok;
                        case "else":
                            return Word.elsetok;
                        case "while":
                            return Word.whiletok;
                        case "begin":
                            return Word.begin;
                        case "end":
                            return Word.end;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read;
                        default:
                            return new Word(Tag.ID, str);
                    }

                } 
                 // Case of numbers
                 else if (Character.isDigit(peek)) {

                    str += peek;
                    readch(br);
                    while (Character.isDigit(peek)) {
                        str += peek;
                        readch(br);
                    }

                    // Check the last character read 
                    if (Character.isLetter(peek) || peek == '_') {
                        System.err.println("Illegal number pattern detected");
                        return null;

                    } else {
                        return new NumberTok(Tag.NUM, Integer.parseInt(str));
                    }

                } else {
                    System.err.println("Unrecognised character detected");
                    return null;
                }
        }
    }

}
