package main;

/**
 * Created by Baxter on 1/10/2017.
 */
public class Instruction {

    char[] text;
    String op;
    String arg1;
    String arg2;

    public Instruction(String assembly) {
        this.text = assembly.toCharArray();
        op = "";
        int i = 0;
        for(; i < text.length; i ++) {
            if(text[i] == ' ') {
                break;
            } else {
                op += text[i];
            }
        }
        i++;
        arg1 = "";
        for(; i < text.length; i ++) {
            if(text[i] == ',') {
                break;
            } else {
                arg1 += text[i];
            }
        }
        i++;
        arg2 = "";
        for(; i < text.length; i ++) {
            arg2 += text[i];
        }
    }

    public byte[] machine() {
        byte[] bytes = new byte[0];
        if(contains(OpGroup.ArithmeticAndLogic.domain, op)) {
            bytes = OpGroup.ArithmeticAndLogic.machine(op, arg1, arg2);
        } else if(op.compareTo(OpGroup.LoadAndExchange.domain) == 0) {
            bytes = OpGroup.LoadAndExchange.machine(op, arg1, arg2);
        }
        return bytes;
    }

    public boolean contains(String[] group, String instance) {
        for(String s : group) {
            if(s.compareTo(instance) == 0) {
                return true;
            }
        }
        return false;
    }

}
