package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Baxter on 1/10/2017.
 */
public abstract class OpGroup {

    public static class LoadAndExchange extends OpGroup {

        public static String domain = "LD";

        public static String[] args = new String[]{
                "A",        //0
                "B",        //1
                "C",        //2
                "D",        //3
                "E",        //4
                "F",        //5
                "L",        //6
                "(HL)",     //7
                "(BC)",     //8
                "(DE)",     //9
                "(IX+D)",   //10
                "(IY+D)",   //11
                "I",        //12
                "R",        //13
                "(nn)",     //14
                "n"         //15
        };

        public static final int ADDRESS = 14, LITERAL = 15, IX = 10, IY = 11, I = 12, R = 13;
        public static final int[] prefixes = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0xDD, 0xFD, 0xED, 0xED, -1, -1};

        public static int[] sources = new int[]{0x7,0x0,0x1,0x2,0x3,0x4,0x5,0x6,-0x6E, -0x5E,0x6,0x6,-0x21,-0x19,-0x3E,-0x3A};
        public static int[] destinations = new int[]{0x78,0x40,0x48,0x50,0x58,0x60,0x68,0x70,-0x5,0xB,0x70,0x70,0x40,0x48, 0x2B};

        public static byte[] machine(String op, String arg1, String arg2){
            int source = indexOf(args, arg2, ADDRESS,LITERAL,IX,IY);
            int destination = indexOf(args, arg1, ADDRESS,-1,IX,IY);
            int value = destinations[destination];
            value += sources[source];

            ArrayList<Byte> bytes = new ArrayList<Byte>();

            if(prefixes[source] != -1) {
                bytes.add(toByte(prefixes[source]));
            }
            if(prefixes[destination] != -1) {
                bytes.add(toByte(prefixes[destination]));
            }

            bytes.add((byte) (value + Byte.MIN_VALUE));

            if(source == IX) bytes.add(toByte(parseIX(arg2)));
            if(source == IY) bytes.add(toByte(parseIY(arg2)));
            if(destination == IX) bytes.add(toByte(parseIX(arg1)));
            if(destination == IY) bytes.add(toByte(parseIY(arg1)));
            if(source == LITERAL) bytes.add(toByte(parseHex(arg2)));
            if(destination == LITERAL) bytes.add(toByte(parseHex(arg1)));
            if(source == ADDRESS) {
                byte[] b = toByteArray(parseAdress(arg2));
                bytes.add(b[0]);
                bytes.add(b[1]);
            }
            if(destination == ADDRESS) {
                byte[] b = toByteArray(parseAdress(arg1));
                bytes.add(b[0]);
                bytes.add(b[1]);
            }

            byte[] output = new byte[bytes.size()];
            for(int i = 0; i < output.length; i ++) {
                output[i] = bytes.get(i);
            }
            return output;
        }

    }

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
                "(IX+D)",
                "(IY+D)",
                "D"
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

    public static byte toByte(int i) {
        return (byte) (i + Byte.MIN_VALUE);
    }

    public static byte[] toByteArray(int i) {
        byte[] bytes = new byte[2];
        int l = i % 256;
        bytes[1] = toByte(l);
        int h = (i - l) / 256;
        bytes[0] = toByte(h);
        return bytes;
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
            if (group[i].compareTo(instance) == 0) {
                return i;
            }
        }
        if(parseHex(instance) != -1) {
            return literal;
        }
        return -1;
    }

    public static int indexOf(String[] group, String instance, int address, int literal, int ix, int iy) {
        for(int i = 0; i < group.length; i ++) {
            if(group[i].compareTo(instance) == 0) {
                return i;
            }
        }
        if(parseAdress(instance) != -1) {
            return address;
        }
        if(parseHex(instance) != -1) {
            return literal;
        }
        if(parseIX(instance) != -1) {
            return ix;
        }
        if(parseIY(instance) != -1) {
            return iy;
        }
        return -1;
    }

    public static final int INDEXED_START_HEX = 4, INDEXED_END_HEX = 6;
    public static final int INDEXED_LENGTH = 7;

    public static int parseIX(String s) {
        if(s.length() == INDEXED_LENGTH) {
            if(s.substring(0,INDEXED_START_HEX).compareTo("(IX+") == 0 && s.charAt(INDEXED_END_HEX) == ')') {
                String h = s.substring(INDEXED_START_HEX, INDEXED_END_HEX);
                try {
                    int i = Integer.parseInt(h, 16);
                    return i;
                } catch (NumberFormatException e) {
                    return -1;
                }
            } else return -1;
        } else return -1;
    }

    public static int parseIY(String s) {
        if(s.length() == INDEXED_LENGTH) {
            if(s.substring(0,INDEXED_START_HEX).compareTo("(IY+") == 0 && s.charAt(INDEXED_END_HEX) == ')') {
                String h = s.substring(INDEXED_START_HEX, INDEXED_END_HEX);
                try {
                    int i = Integer.parseInt(h, 16);
                    return i;
                } catch (NumberFormatException e) {
                    return -1;
                }
            } else return -1;
        } else return -1;
    }

    public static int parseAdress(String s) {
        if(s.length() == 6) {
            if(s.charAt(0) == '(' && s.charAt(5) == ')') {
                String h = s.substring(1, 5);
                try {
                    int i = Integer.parseInt(h, 16);
                    return i;
                } catch (NumberFormatException e){
                    return -1;
                }
            } else return -1;
        } else return -1;
    }

    public static int parseHex(String s) {
        if(s.length() == 3) {
            if(s.charAt(2) == 'H') {
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
