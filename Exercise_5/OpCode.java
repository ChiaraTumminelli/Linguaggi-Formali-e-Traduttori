package Exercise_5;

// Enumeration of the mnemonic names of the object language instructions.

public enum OpCode { 
    ldc, imul, ineg, idiv, iadd, 
    isub, istore, ior, iand, iload,
    if_icmpeq, if_icmple, if_icmplt, if_icmpne, if_icmpge, 
    if_icmpgt, ifne, GOto, invokestatic, label, dup }
