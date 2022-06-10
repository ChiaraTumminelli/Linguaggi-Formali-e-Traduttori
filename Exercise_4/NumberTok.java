package Exercise_4;

public class NumberTok extends Token {

    // ... To complete ...
    public int num;
    public NumberTok (int tag, int s) { super(tag); num = s; }
    public String toString(){ return "<" + tag + ", " + num + ">"; }

  
}
