package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Baxter on 1/10/2017.
 */
public abstract class OpGroup {

    public static class ArithmeticAndLogic extends OpGroup {

        public static String[] domain = new String[] {
                "ADD",
                "ADC",
                "SUB",
                "SBC",
                "AND",
                "XOR",
                "OR",
                "CP",
                "INC",
                "DEC"
        };

        public static String[] args = new String[]{
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "L",
                "(HL)",
                "(IX+d)",
                "(IY+d)",
                "n"
        };

        public static int[] destinations = new int[] {0x80, 0x88, 0x90, 0x98, 0xa0, 0xa8, 0xb0, 0xb8, 4, 5};
        public static int[] sources = new int[] {7, 0, 1, 2, 3, 4, 5, 6, 6, 6, 70};
        public static int[] sources2 = new int[] {57, 0, 8, 16, 24, 32, 40, 40, 40};

        public static boolean[] hasTwoArgs = new boolean[]{true, true, false, true, false, false, false, false, false, false};

        public static byte[] machine(String op, String arg1, String arg2) {
            int destination = OpGroup.indexOf(domain, op, -1);
            int source = -1;

            String arg = hasTwoArgs[destination] ? arg2 : arg1;
            source = OpGroup.indexOf(args, arg, 10);
            int value = destinations[destination];
            if(destination == 8 | destination == 9) {
                value += sources2[source];
            } else {
                value += sources[source];
            }

            ArrayList<Byte> bytes = new ArrayList<Byte>();

            if(source == 8) {
                bytes.add((byte) (0xdd + Byte.MIN_VALUE));
            } else if (source == 9) {
                bytes.add((byte) (0xfd + Byte.MIN_VALUE));
            }

            bytes.add((byte) (value + Byte.MIN_VALUE));

            if(source == 10) {
                bytes.add((byte) (parseHex(arg) + Byte.MIN_VALUE));
            }

            byte[] output = new byte[bytes.size()];
            for(int i = 0; i < output.length; i ++) {
                output[i] = bytes.get(i);
            }
            return output;
        }

    }

    public static int indexOf(String[] group, String instance) {
        for(int i = 0; i < group.length; i ++) {
            if(group[i].compareTo(instance) == 0) {
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(String[] group, String instance, int literal) {
        for(int i = 0; i < group.length; i ++) {
            if(group[i].compareTo(instance) == 0) {
                return i;
            }
        }
        if(parseHex(instance) != -1) {
            return literal;
        }
        return -1;
    }

    static char[] hex = new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static int parseHex(String s) {
        if(s.length() == 3) {
            if(s.charAt(2) == 'h') {
                String h = s.substring(0, 2);
                try {
                int i = Integer.parseInt(h, 16);
                    return i;
                } catch (NumberFormatException e) {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

}
