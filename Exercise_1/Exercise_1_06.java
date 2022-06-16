package Exercise_1;

/* DFA that recognizes strings containing uni serial number followed by
** the surname of the students of T2 or T3 turn of LFT laboratory */

public class Exercise_1_06 {

    public static boolean isEven(char ch) {
        return Character.isDigit(ch) && ch % 2 == 0;  
    }
    
    public static boolean isOdd(char ch) {
        return Character.isDigit(ch) && (ch % 2 == 1);  
    }
    
    public static boolean isAtoK(char ch) {
        return ch >= 65 && ch <= 75;
    }
    
    public static boolean isLtoZ(char ch) {
        return ch >= 76 && ch <= 90;
    }
    
    public static boolean scan(String s){
        
        int state = 0;
        int i = 0;
    
        while (state >= 0 && i < s.length()){
            
            final char ch = s.charAt(i++);
    
            switch (state) {
            
            case 0:
                if (isEven(ch))
                    state = 2;
                else if (isOdd(ch))
                    state = 1;
                else 
                    state = -1;
                break;
    
            case 1:
                if (isOdd(ch))
                    state = 1;
                else if (isEven(ch))
                    state = 2;
                else if (isLtoZ(ch))
                    state = 3;
                else 
                    state = -1;
                break;
        
            case 2: 
                if (isEven(ch))
                    state = 2;
                else if (isOdd(ch))
                    state = 1;
                else if (isAtoK(ch))
                    state = 3;
                else 
                    state = -1;
                break;
        
            case 3:
                if (Character.isLetter(ch))
                    state = 3;
                else 
                    state = -1;
                break;
            }
        }
    
        return state == 3;
    }


    public static void main (String [] args){

        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
        
    
}

