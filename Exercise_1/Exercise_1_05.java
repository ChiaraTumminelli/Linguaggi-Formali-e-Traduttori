package Exercise_1;

/* DFA that recognizes strings containing uni serial number followed by
** the surname of the students of of course A with even uni serial number
** or course B with odd uni serial number of LFT laboratory
** with uni serial number and surname reversed  */

public class Exercise_1_05 {

    public static boolean isEven(char ch){
        return Character.isDigit(ch) && ch % 2 == 0;
    }

    public static boolean isOdd(char ch){
        return Character.isDigit(ch) && ch % 2 == 1;
    }

    public static boolean isAtoK(char ch){
        return ch >= 65 && ch <= 75;
    }

    public static boolean isLtoZ(char ch){
        return ch >= 76 && ch <= 90;
    }

    public static boolean scan(String s){

        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if (isLtoZ(ch))
                        state = 1;
                    else if (isAtoK(ch))
                        state = 4;
                    else 
                        state = -1;
                    break;

                case 1:
                    if (Character.isLetter(ch))
                        state = 1;
                    else if (isOdd(ch))
                        state = 2;
                    else if (isEven(ch))
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 2: 
                    if (isOdd(ch))
                        state = 2;
                    else if (isEven(ch))
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 3:
                    if (isOdd(ch))
                        state = 2;
                    else if (isEven(ch))
                        state = 3;
                    else 
                        state = -1;
                    break;

                case 4:
                    if (Character.isLetter(ch))
                        state = 4;
                    else if (isOdd(ch))
                        state = 5;
                    else if (isEven(ch))
                        state = 6;
                    else 
                        state = -1;
                    break;

                case 5:
                    if (isOdd(ch))
                        state = 5;
                    else if (isEven(ch))
                        state = 6;
                    else 
                        state = -1;
                    break;

                case 6: 
                    if (isOdd(ch))
                        state = 5;
                    else if (isEven(ch))
                        state = 6;
                    else 
                        state = -1;
                    break;              
            }
        }
        return state == 2 || state == 6;
    }


    public static void main (String [] args){

        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
            
    
}
