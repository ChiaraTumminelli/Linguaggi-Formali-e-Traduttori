package Exercise_1;

// DFA which recognizes 

public class Exercise_08 {

    public static boolean scan (String s){

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if (Character.isDigit(ch))
                        state = 1;
                    else if (ch == '+' || ch == '-')
                        state = 2;
                    else if (ch == '.')
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 1:
                    if (Character.isDigit(ch))
                        state = 1;
                    else if (ch == '.')
                        state = 3;
                    else if (ch == 'e')
                        state = 0;
                    else 
                        state = -1;
                    break;

                case 2:
                    if (Character.isDigit(ch))
                        state = 1;
                    else if (ch == '.')
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 3:
                    if (Character.isDigit(ch))
                        state = 4;
                    else 
                        state = -1;
                    break;

                case 4: 
                    if (Character.isDigit(ch))
                        state = 4;
                    else if (ch == 'e')
                        state = 0;
                    else 
                        state = -1;
                    break;                
            }
        }
        return state == 1 || state == 4;
    }


    public static void main (String [] args){

        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
            
    
}
