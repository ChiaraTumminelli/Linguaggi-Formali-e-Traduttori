package Exercise_3.Exercise_3_1;

/* To better understand comments:
** Step-by-step calculations are specified in the First&Follow.txt file. */

import java.io.*;

public class Parser {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser (Lexer l, BufferedReader br){
        lex = l;
        pbr = br;
        move();
    }    

    void move(){
        look = lex.lexical_scan(pbr);
        System.out.println("Token = " + look);
    }

    void error (String s){
        throw new Error ("Near line " + Lexer.line + ": " + s);
    }

    void match (int t){
        if (look.tag == t){
            if (look.tag != Tag.EOF) move();
        } else error ("Syntax error");
    }

    // PREDICT(S->E) = FIRST(E) = {(,NUM} 
    public void start() {
        switch(look.tag){
            case '(':
            case Tag.NUM:
                expr();
                match(Tag.EOF);
                break;

            default:                
                error("start");         
        }
    }

    // PREDICT(E->TE') = FIRST(T) = {(,NUM}
    private void expr() {
        switch(look.tag){
            case '(':
            case Tag.NUM:
                term();
                exprp();
                break;

            default:                
                error("expr");         
        }
    }
    
    private void exprp() {
        switch (look.tag){

        //PREDICT(E'->+TE') = FIRST(+TE') = FIRST(+) = {+}
        case '+':
            match(Token.plus.tag);
            term();
            exprp();
            break;

        //PREDICT(E'->-TE') = FIRST(-TE') = FIRST(-) = {-}
        case '-':
            match(Token.minus.tag);
            term();
            exprp();
            break;

        // PREDICT(E'->eps) = FOLLOW(E') = {$,)} 
        case ')':
        case Tag.EOF:
            break;

        default:
            error("exprp");
        
        }
    }
       
    // PREDICT(T -> FT') = FIRST(FT') = FIRST(F) = {(,NUM}
    private void term() {
        switch(look.tag){
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;

            default:                
                error("term");
        }
    }

    private void termp() {
        switch(look.tag){

            // PREDICT(T'->*FT') = FIRST(*FT') = FIRST(*) = {*}
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
                break;
            
            // PREDICT(T'->/FT') = FIRST(/FT') = FIRST(/) = {/}
            case '/':
                match(Token.div.tag);
                fact();
                termp();
                break;

            // PREDICT(T'->eps) = FOLLOW(T') = {$,+,-,)}    
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                break;

            default:                
                error("termp");
        }
    }
    
    private void fact() {
        switch(look.tag){

            // PREDICT(F->(E)) = FIRST((E)) = FIRST(() = {(}
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;

            // PREDICT(F->NUM) = FIRST(NUM) = {NUM}
            case Tag.NUM:
                match(Tag.NUM);
                break;

            default:            
                error("fact");
        }
    }
        
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Exercise_3/Exercise_3_1/Example.txt"; 
        try {
        BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
