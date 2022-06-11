package Exercise_5.Optional_5_2;

import java.util.*;

// Symbol table to keep track of identifiers

public class SymbolTable {

     Map <String, Integer> OffsetMap = new HashMap <String,Integer>();

    // It inserts a new element into the table if an element with the same lexeme does not already exist
	public void insert( String s, int address ) {
            if( !OffsetMap.containsValue(address) ) 
                OffsetMap.put(s,address);
            else 
                throw new IllegalArgumentException("Reference to a memory location already occupied by another variable");
	}

    // It returns the address of the table element that matches the lexeme, or -1 if there are no table elements that match the lexeme.
	public int lookupAddress ( String s ) {
            if( OffsetMap.containsKey(s) ) 
                return OffsetMap.get(s);
            else
                return -1;
	}
}
