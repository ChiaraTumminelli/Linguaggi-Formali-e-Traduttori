package Exercise_1;

/* DFA that recognizes strings containing uni serial number followed by
** the surname of the students of course A with even uni serial number 
** or course B with odd uni serial number with any leading and trailing 
** spaces or between the number and surname */

public class Exercise_1_04 {

    public static boolean isEven(char ch){
        return Character.isDigit(ch) && ch % 2 == 0;
    }

    public static boolean isOdd(char ch){
        return Character.isDigit(ch) && ch % 2 == 1;
    }

    public static boolean isAtoK(char ch){
        return (ch >= 65 && ch <= 75);
    }

    public static boolean isLtoZ(char ch){
        return (ch >= 76 && ch <= 90);
    }

    public static boolean scan(String s){

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if (ch == ' ')              
                        state = 0;          
                    else if (isEven(ch))
                        state = 1;
                    else if (isOdd(ch))
                        state = 2;
                    else 
                        state = -1;
                    break;

                case 1: 
                    if (isEven(ch))
                        state = 1;
                    else if (isOdd(ch))
                        state = 2;
                    else if (isAtoK(ch))
                        state = 5;
                    else if (ch == ' ')
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 2: 
                    if (isEven(ch))
                        state = 1;
                    else if (isOdd(ch))
                        state = 2;
                    else if (isLtoZ(ch))
                        state = 5;
                    else if (ch == ' ')
                        state = 4;
                    else 
                        state = -1;
                    break;

                case 3:
                    if (ch == ' ')
                        state = 3;
                    else if (isAtoK(ch))
                        state = 5;
                    else 
                        state = -1;
                    break;

                case 4:
                    if (ch == ' ')
                        state = 3;
                    else if (isLtoZ(ch))
                        state = 5;
                    else 
                        state = -1;
                    break;

                case 5: 
                    if (Character.isLetter(ch))
                        state = 5;
                    else if (ch == ' ')
                        state = 6;
                    else 
                        state = -1;
                    break;

                case 6:
                    if (isAtoK(ch) || isLtoZ(ch))
                        state = 5;
                    else if (ch == ' ')
                        state = 6;
                    else 
                        state = -1;
                    break;
            }    
        }
        return (state == 5) || (state == 6);
    }


    public static void main (String [] args){

        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
            
    
}