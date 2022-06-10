package Exercise_3.Exercise_3_2;

/* To better understand comments:
** Step-by-step calculations are specified in the First&Follow.txt file. */

import java.io.*;

public class Parser {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    // PREDICT(A->B) = FIRST(B) = {=,print,read,cond,while,{}
    public void prog() {
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                statlist();
                match(Tag.EOF);
                break;

            // If input is empty, it must not return an error
            case Tag.EOF:
                break;
            
            default:
                error("error in prog method");
        }
    }

    // PREDICT(B->DC) = FIRST(D) = {=,print,read,cond,while,{}
    public void statlist() {
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat();
                statlistp();
                break;
            
            default:
                error("error in statlist method");       
        }
    }

    public void statlistp() {
        switch(look.tag){
            
            //PREDICT(C->;DC) = FIRST(;) = {;}
            case ';':
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;

            //PREDICT(C->eps) = FOLLOW(C) = {$,}}
            case Tag.EOF:
                break;

            case '}':
                break;

            default:
                error("error in statlistp method");

        }
    }

    public void stat() {
        switch(look.tag){

            // PREDICT(D->=IDI) = FIRST(=) = {=}
            case '=':
                match(Token.assign.tag);
                match(Tag.ID);
                expr();
                break;

            // PREDICT(D->print(L)) = FIRST(print) = {print} 
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            // PREDICT(D->read(ID)) = FIRST(read) = {read}
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                match(Tag.ID);
                match(Token.rpt.tag);
                break;

            // PREDICT(D->cond E else D) = FIRST(cond) = {cond}
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;

            // PREDICT(D->while(H)D) = FIRST(while) = {while}
            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                break;

            // PREDICT(D->{B}) = FIRST({B}) = FIRST({) = {{}
            case '{':
                match(Token.lpg.tag);
                statlist();
                match(Token.rpg.tag);
                break;

            default:
                error("error in stat method");

        }
    }

    // PREDICT(E->GF) = FIRST(G) = {when}
    public void whenlist() {
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;

            default:
                error("error in whenlist method");
        }
    }

    public void whenlistp() {
        switch(look.tag){

            // PREDICT(F->GF) = FIRST(G) = {when}
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;

            // PREDICT(F->eps) = FOLLOW(F) = {else}
            case Tag.ELSE:
                break;
                
            default:
                error("error in whenlistp method");
        }
    }

    // PREDICT(G->when(H) do D) = FIRST(when) = {when}
    public void whenitem() {
        switch(look.tag){
            case Tag.WHEN:
                match(Tag.WHEN);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                match(Tag.DO);
                stat();
                break;

            default:
                error("error in whenitem method");

        }
    }

    // PREDICT(H->RELOPII) = FIRST(RELOP) = {RELOP}
    public void bexpr() {
        switch(look.tag){
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;

            default:
                error("error in bexpr method");
        }
    }

    public void expr() {
        switch(look.tag){

            // PREDICT(I->+(L)) = FIRST(+) = {+}
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            // PREDICT(I->-II) = FIRST(-) = {-}
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                break;

            // PREDICT(I->*(L)) = FIRST(*) = {*}
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            // PREDICT(I->/II) = FIRST(/) = (/)
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                break;

            // PREDICT(I->NUM) = FIRST(NUM) = {NUM}
            case Tag.NUM:
                match(Tag.NUM);
                break;

            // PREDICT(I->ID) = FIRST(ID) = {ID}
            case Tag.ID:
                match(Tag.ID);
                break;

            default:
                error("error in expr method");

        }
    }

    // PREDICT(L->IM) = FIRST(I) = {+,-,*,/,NUM,ID}
    public void exprlist() {
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;

            default:
                error("error in exprlist method");

        }
    }

    public void exprlistp() {
        switch(look.tag){

            // PREDICT(M->IM) = FIRST(I) = {+,-,*,/,NUM,ID}
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;

            // PREDICT(M -> eps) = FOLLOW(M) = {)}
            case ')':
                break;

            default:
                error("error in exprlistp method");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Exercise_3/Exercise_3_2/Example.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    
