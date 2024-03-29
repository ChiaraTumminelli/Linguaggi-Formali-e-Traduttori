package Exercise_1;

// DFA which recognizes the name 'Chiara' and all the strings obtained by changing a single symbol.

public class Exercise_1_07 {

    public static boolean scan (String s){

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if (ch == 'C')
                        state = 1;
                    else 
                        state = 7;
                    break;

                case 1:
                    if (ch == 'h')
                        state = 2;
                    else 
                        state = 8;
                    break;

                case 2:
                    if (ch == 'i')
                        state =3;
                    else 
                        state = 9;
                    break;

                case 3:
                    if (ch == 'a')
                        state = 4;
                    else 
                        state = 10;
                    break;

                case 4: 
                    if (ch == 'r')
                        state = 5;
                    else 
                        state = 11;
                    break;

                case 5:
                        state = 6;
                    break;

                case 6:
                    break;

                case 7:
                    if (ch == 'h')
                        state = 8;
                    else 
                        state = -1;
                    break;

                case 8: 
                    if (ch == 'i')
                        state = 9;
                    else 
                        state = -1;
                    break;

                case 9:
                    if (ch == 'a')
                        state = 10;
                    else 
                        state = -1;
                    break;

                case 10:
                    if (ch == 'r')
                        state = 11;
                    else 
                        state = -1;
                    break;

                case 11: 
                    if (ch == 'a')
                        state = 6;
                    else 
                        state = -1;
                    break;                   
            }
        }
        return state == 6;
    }


    public static void main (String [] args){

        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
            
    
}
