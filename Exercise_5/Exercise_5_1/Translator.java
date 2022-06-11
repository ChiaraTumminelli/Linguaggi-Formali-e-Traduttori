package Exercise_5.Exercise_5_1;

/* To better understand comments:
** Step-by-step calculations are specified in the First&Follow.txt file. */

import java.io.*;

public class Translator {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public final static int READ_VAR = 0;
    public final static int PRINT_VAR = 1;
    public final static int ASSIGN_VAR = 2;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator (Lexer l, BufferedReader br){
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
                int lnext_prog = code.newLabel();
                statlist(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);

                try {
                    code.toJasmin();
                }
                catch(java.io.IOException e) {
                    System.out.println("IO error\n");
                };
                break;

            default:
                error("Error in prog method");

        }
    }

    //PREDICT(B -> DC) = FIRST(D) = { assign, print, read, while, if, { }
    public void statlist(int lnext){
        switch(look.tag){

            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '{':
                int lnext_stat = code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp();
                code.emit(OpCode.GOto, lnext);
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
                int lnext_stat = code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
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

    public void stat(int lnext){
        switch(look.tag){

            //PREDICT(D -> assign H to E) = FIRST(assign) = { assign }
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(ASSIGN_VAR);
                code.emit(OpCode.GOto, lnext);
                break;

            //PREDICT(D -> print (I)) = FIRST(print) = { print }
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist(OpCode.invokestatic);
                match(Token.rpt.tag);
                code.emit(OpCode.GOto, lnext);
                break;

            //PREDICT(D -> read (E) ) = FIRST(read) = { read }
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                idlist(READ_VAR);
                match(Token.rpt.tag);
                code.emit(OpCode.GOto, lnext);
                break;

            //PREDICT(D -> while (G) D) = FIRST(while) = { while }
            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                int lwhile_start = code.newLabel();
                code.emitLabel(lwhile_start);
                int lwhile_condtrue = code.newLabel();
                int lwhile_next = code.newLabel();
                bexpr(lwhile_condtrue, lwhile_next);
                match(Token.rpt.tag);
                code.emitLabel(lwhile_condtrue);
                stat(lwhile_start);
                code.emitLabel(lwhile_next);
                code.emit(OpCode.GOto,lnext);
                break;

            //PREDICT(D -> if (G) D P ) = FIRST(if) = { if }
            case Tag.IF:
                match(Tag.IF);
                match(Token.lpt.tag);
                int if_cond_true = code.newLabel();
                int if_cond_false = code.newLabel();
                bexpr(if_cond_true, if_cond_false);
                match(Token.rpt.tag);
                code.emitLabel(if_cond_true);
                stat(lnext);
                statp(if_cond_false, lnext);
                break; 

            //PREDICT(D -> { B }) = FIRST({) = { { }
            case '{':
                match(Token.lpg.tag);
                statlist(lnext);
                match(Token.rpg.tag);
                break; 

            default:
                error("Error in stat method");

        }
    }

    public void statp(int lfalse, int lnext){
        switch(look.tag){

            //PREDICT(P -> end) = FIRST(end) = { end }
            case Tag.END:
                match(Tag.END);
                code.emitLabel(lfalse);
                break;

            //PREDICT(P -> else D end) = FIRST(else) = { else }
            case Tag.ELSE:
                match(Tag.ELSE);
                code.emitLabel(lfalse);
                stat(lnext);
                match(Tag.END);
                break;

            default:
                error("Error in statp method");

        }
    }

    //PREDICT(E -> MF) = FIRST(M) = { ID }
    public void idlist(int readMode){
        switch(look.tag){

            case Tag.ID:
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    idlistp(readMode,id_addr);
                }
                break;

            default:
                error("Error in idlist method");
            
        }
    }

    public void idlistp(int readMode, int previousVarAddress){
        switch(look.tag){

            //PREDICT(F -> ,MF) = FIRST(,) = {,}
            case ',':
                match(Token.comma.tag);
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    if (readMode == READ_VAR){
                        code.emit(OpCode.invokestatic,READ_VAR);
                    } else if (readMode == ASSIGN_VAR){
                        code.emit(OpCode.dup);
                    }
                    code.emit(OpCode.istore,previousVarAddress);
                    idlistp(readMode,id_addr);
                }
                break;

            //PREDICT(F -> eps) = FOLLOW(F) = { $, ;, ), end, else, } } 
            case Tag.EOF:
            case ';':
            case ')':
            case Tag.END:
            case Tag.ELSE:
            case '}':
                if (readMode == READ_VAR){
                    code.emit(OpCode.invokestatic, READ_VAR);
                }
                code.emit(OpCode.istore, previousVarAddress);
                break;

            default:
                error("Error in idlistp method");
            
        }
    }

    //PREDICT(G -> RELOPHH) = FIRST(RELOP) = { RELOP }
    public void bexpr(int ltrue, int lfalse){
        switch(look.tag){

            case Tag.RELOP:
                String cond = (((Word) look).lexeme);
                match(Tag.RELOP);
                switch(cond){
                    
                    case "==":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmpeq, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;

                    case "<":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmplt, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;

                    case ">":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmpgt, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;

                    case "<=":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmple, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;

                    case ">=":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmpge, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;

                    case "<>":
                        expr();
                        expr();
                        code.emit(OpCode.if_icmpne, ltrue);
                        code.emit(OpCode.GOto,lfalse);
                        break;
                }

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
                exprlist(OpCode.iadd);
                match(Token.rpt.tag);
                break;

            //PREDICT(H -> -HH) = FIRST(-) = { - }
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                code.emit(OpCode.isub);
                break;

            //PREDICT(H -> *(I)) = FIRST(*) = { * }
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist(OpCode.imul);
                match(Token.rpt.tag);
                break;

            //PREDICT(H -> /HH) = FIRST(/) = { / }
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);
                break; 

            //PREDICT(H -> NUM) = FIRST(NUM) = { NUM }
            case Tag.NUM:
                code.emit(OpCode.ldc,((NumberTok) look).num);   //emit(NUM)
                match(Tag.NUM);
                break;

            //PREDICT(H -> ID) = FIRST(ID) = { ID }
            case Tag.ID:
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    code.emit(OpCode.iload,id_addr);
                }
                break;

            default:
                error("Error in expr method");
 
        }
    }

    //PREDICT(I -> HL) = FIRST(H) = { ID, +, -, *, / NUM } 
    public void exprlist(OpCode pcode){
        switch(look.tag){

            case Tag.ID:
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
                expr();
                if (pcode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, PRINT_VAR);
                }
                exprlistp(pcode);
                break;

            default:
                error("Error in exprlist method");
        
        }
    }

    public void exprlistp(OpCode pcode){
        switch(look.tag){

            //PREDICT(L -> ,HL) = FIRST(,) = { , }
            case ',':
                match(Token.comma.tag);
                expr();
                if (pcode == OpCode.iadd || pcode == OpCode.imul){
                    code.emit(pcode);
                } else if (pcode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic,PRINT_VAR);
                }
                exprlistp(pcode);
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
        String path = "Exercise_5/Exercise_5_1/Example.lft"; 
        try {
        BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
    
}
