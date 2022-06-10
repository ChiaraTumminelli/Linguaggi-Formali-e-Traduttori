package Exercise_3.Exercise_3_2;

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

    // PREDICT(A -> B EOF) = FIRST(B) = { assign, print, read, while, if, {} 
    public void prog(){
        switch(look.tag){
            
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '{':
                statlist();
                match(Tag.EOF);
                break;

            default:
                error("Error in prog method");

        }
    }

    //PREDICT(B -> DC) = FIRST(D) = { assign, print, read, while, if, { }
    public void statlist(){
        switch(look.tag){

            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '{':
                stat();
                statlistp();
                break;

            default:
                error("Error in statlist method");
        }
    }

    
    public void statlistp(){
        switch(look.tag){

            //PREDICT(C -> ;DC) = FIRST(;) = {;}
            case ';':
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;  
                
            //PREDICT(C -> eps) = FOLLOW(C) = { $, } }
            case Tag.EOF:
            case '}':
                break;

            default:
                error("Error in statlistp method");

        }
    }

    public void stat(){
        switch(look.tag){

            //PREDICT(D -> assign H to E) = FIRST(assign) = { assign }
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;

            //PREDICT(D -> print (I)) = FIRST(print) = { print }
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            //PREDICT(D -> read (E) ) = FIRST(read) = { read }
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                idlist();
                match(Token.rpt.tag);
                break;

            //PREDICT(D -> while (G) D) = FIRST(while) = { while }
            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                break;

            //PREDICT(D -> if (G) D P ) = FIRST(if) = { if }
            case Tag.IF:
                match(Tag.IF);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                statp();
                break; 

            //PREDICT(D -> { B }) = FIRST({) = { { }
            case '{':
                match(Token.lpg.tag);
                statlist();
                match(Token.rpg.tag);
                break; 

            default:
                error("Error in stat method");

        }
    }

    public void statp(){
        switch(look.tag){

            //PREDICT(P -> end) = FIRST(end) = { end }
            case Tag.END:
                match(Tag.END);
                break;

            //PREDICT(P -> else D end) = FIRST(else) = { else }
            case Tag.ELSE:
                match(Tag.ELSE);
                stat();
                match(Tag.END);
                break;

            default:
                error("Error in statp method");

        }
    }

    //PREDICT(E -> MF) = FIRST(M) = { ID }
    public void idlist(){
        switch(look.tag){

            case Tag.ID:
                match(Tag.ID);
                idlistp();
                break;

            default:
                error("Error in idlist method");
            
        }
    }

    public void idlistp(){
        switch(look.tag){

            //PREDICT(F -> ,MF) = FIRST(,) = {,}
            case ',':
                match(Token.comma.tag);
                match(Tag.ID);
                idlistp();
                break;

            //PREDICT(F -> eps) = FOLLOW(F) = { $, ;, ), end, else, } } 
            case Tag.EOF:
            case ';':
            case ')':
            case Tag.END:
            case Tag.ELSE:
            case '}':
                break;

            default:
                error("Error in idlistp method");
            
        }
    }

    //PREDICT(G -> RELOPHH) = FIRST(RELOP) = { RELOP }
    public void bexpr(){
        switch(look.tag){

            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;

            default:
                error("Error in bexpr method");
            
        }
    }

    public void expr(){
        switch(look.tag){

            //PREDICT(H -> +(I)) = FIRST(+) = { + }
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            //PREDICT(H -> -HH) = FIRST(-) = { - }
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                break;

            //PREDICT(H -> *(I)) = FIRST(*) = { * }
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;

            //PREDICT(H -> /HH) = FIRST(/) = { / }
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                break; 

            //PREDICT(H -> NUM) = FIRST(NUM) = { NUM }
            case Tag.NUM:
                match(Tag.NUM);
                break;

            //PREDICT(H -> ID) = FIRST(ID) = { ID }
            case Tag.ID:
                match(Tag.ID);
                break;

            default:
                error("Error in expr method");
 
        }
    }

    //PREDICT(I -> HL) = FIRST(H) = { ID, +, -, *, / NUM } 
    public void exprlist(){
        switch(look.tag){

            case Tag.ID:
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
                expr();
                exprlistp();
                break;

            default:
                error("Error in exprlist method");
        
        }
    }

    public void exprlistp(){
        switch(look.tag){

            //PREDICT(L -> ,HL) = FIRST(,) = { , }
            case ',':
                match(Token.comma.tag);
                expr();
                exprlistp();
                break;

            //PREDICT(L -> eps) = FOLLOW(L) = { ) }
            case ')':
                break;

            default:
                error("Error in exprlistp method");

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
        } catch (IOException e) {e.printStackTrace();}
    }
    
}
