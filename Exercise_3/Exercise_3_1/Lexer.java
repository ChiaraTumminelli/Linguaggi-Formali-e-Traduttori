package Exercise_3.Exercise_3_1;

import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    // Case of error 
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; 
        }
    }

    // Case of error 
    private Token wrongToken(String details) {
        System.err.println("Illegal character detected: " + "(" + peek + ")" + " character was after " + "(" + details + ") character.");
        return null;
    }

    // ... Method to be completed  ...
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
                        return wrongToken("Missing multi-line comment close!");
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

            // Early reading with double buffer and sentinels cases  
            // Case of logical AND (&&)
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    return wrongToken("&");
                }
  
            // Case of logical OR (||)
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    return wrongToken("|");
                }

            //Case of = and case of ==
            case '=':
                readch(br);
                if (peek == '=') {
                  peek = ' ';
                  return Word.eq;
                } else if (peek != '=') {
                  return Token.assign;
                } else {
                  return wrongToken("=");
                }

            // Case of <=, case of <> and case of <
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else if (peek == ' ') {
                    peek = ' ';
                    return Word.lt;
                } else {
                    return wrongToken("<");
                }

            // Case of >= and case of >
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else if (peek == ' ') {
                    peek = ' ';
                    return Word.gt;
                } else {
                    return wrongToken(">");
                }

            // Case of End Of File
            case (char) -1:
                return new Token(Tag.EOF);

            // Case of identifiers, keywords and values
            default:
                String str = "";

                // Exercise 2.2
                // Case of identifiers and keywords
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
                                return wrongToken("Only underscore var");
                            }
                        }
                    } 


                    while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                        str += peek;
                        readch(br);
                    }

                    
                    switch (str) {
                        case "cond":
                            return Word.cond;
                        case "when":
                            return Word.when;
                        case "then":
                            return Word.then;
                        case "else":
                            return Word.elsetok;
                        case "while":
                            return Word.whiletok;
                        case "do":
                            return Word.dotok;
                        case "seq":
                            return Word.seq;
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
                        return wrongToken("Illegal number pattern detected");
                    } else {
                        return new NumberTok(Tag.NUM, Integer.parseInt(str));
                    }

                } else {
                    return wrongToken("Unrecognised character detected");
                }
        }
    }

}
