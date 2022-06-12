package Exercise_4;

/* To better understand comments:
** Step-by-step calculations are specified in the First&Follow.txt file. */

import java.io.*;

public class Evaluator {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Evaluator (Lexer l, BufferedReader br){
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
    // <start> ::= <expr> EOF {print(expr.val)}
    public void start() { 
	    int expr_val = 0;
        switch(look.tag) {
            
            case '(':
               expr_val = expr();
               match(Tag.EOF);
               System.out.println(expr_val);
               break;
            
            case Tag.NUM:
               expr_val = expr();
               match(Tag.EOF);
               System.out.println(expr_val);
               break;

            default:
                error("Error in start method"); 
        }
    }

    // PREDICT(E->TE') = FIRST(T) = {(,NUM}
    // <expr> ::= <term> {exprp.i = term.val} <exprp>{expr.val = exprp.val}
    private int expr() { 
	    int term_val, exprp_val = 0;
        switch(look.tag) {
            
            case '(':
                term_val = term();
                exprp_val = exprp(term_val);
                break;

            case Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                break;

            default: 
                error("Error in expr method");
        }

        return exprp_val;
    }

    private int exprp(int exprp_i) {
	    int term_val, exprp_val = 0;
        switch (look.tag) {
            
            // PREDICT(E'->+TE') = FIRST(+TE') = FIRST(+) = {+}
            // <exprp> ::= + <term> {exprp1.i = exprp.i + term.val} <exprp1> {exprp.val = exprp1.val}
            case '+' : 
                match(Token.plus.tag);
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;

            // PREDICT(E'->-TE') = FIRST(-TE') = FIRST(-) = {-}
            // <exprp> ::= - <term> {exprp1.i = exprp.i - term.val} <exprp1> {exprp.val = exprp1.val} 
            case '-':
                match(Token.minus.tag);
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break; 

            // PREDICT(E'->eps) = FOLLOW(E') = {$,)} */
            // <exprp> ::= eps {exprp.val = exprp.i}
            case ')':
                exprp_val = exprp_i;
                break;

            case Tag.EOF:
                exprp_val = exprp_i;
                break;

            default:
                error("Error in exprp method");
        }
	    return exprp_val;
    }

    // PREDICT(T -> FT') = FIRST(FT') = FIRST(F) = {(,NUM}
    // <term> ::= <fact> {termp.i = fact.val} <termp> {term.val = termp.val}
    private int term() {
        int fact_val, termp_val = 0;
        switch(look.tag) {
            
            case '(':
                fact_val = fact();
                termp_val = termp(fact_val);
                break;

            case Tag.NUM:
                fact_val = fact();
                termp_val = termp(fact_val);
                break;

            default:
                error("Error in term method");
        } 
        return termp_val;
    }
    
    private int termp(int termp_i) {
        int fact_val, termp_val = 0;
        switch(look.tag){

            // PREDICT(T'->*FT') = FIRST(*FT') = FIRST(*) = {*}
            // <termp> ::= * <fact> {termp1.i = termp.i * fact.val} <termp1> {termp.val=termp1.val}
            case '*':
                match(Token.mult.tag);
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;

            // PREDICT(T'->/FT') = FIRST(/FT') = FIRST(/) = {/}
            // <termp> ::= / <fact> {termp1.i = termp.i / fact.val} <termp1> {termp.val=termp1.val}
            case '/':
                match(Token.div.tag);
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;

            // PREDICT(T'->eps) = FOLLOW(T') = {$,+,-,)}
            // <termp> ::= eps {termp.val = termp.i}
            case '+':
                termp_val = termp_i;
                break;

            case '-':
                termp_val = termp_i;
                break; 

            case ')':
                termp_val = termp_i;
                break;
                 
            case Tag.EOF:
                termp_val = termp_i;
                break; 

            default:
                error("Error in termp method");
        }
        return termp_val;
    }
    
    private int fact() {
        int fact_val = 0;
        switch(look.tag){

            // PREDICT(F->(E)) = FIRST((E)) = FIRST(() = {(}
            // <fact> ::= ( <expr> ) {fact.val = expr.val}
            case '(':
                match(Token.lpt.tag);
                fact_val = expr();
                match(Token.rpt.tag);
                break;
            
            // PREDICT(F->NUM) = FIRST(NUM) = {NUM}
            // <fact> ::= NUM {fact.val = NUM.value}
            case Tag.NUM:
                NumberTok n = (NumberTok) look;
                fact_val = n.num;
                match(Tag.NUM);
                break;

            default:
                error("Error in fact method");
        }  
        return fact_val;	  
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Exercise_4/Example.txt"; 
        try {
        BufferedReader br = new BufferedReader(new FileReader(path));
            Evaluator evaluator = new Evaluator(lex, br);
            evaluator.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }

}