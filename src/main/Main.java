package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Created by Baxter on 1/10/2017.
 */
public class Main implements ActionListener {

    public static void main(String[] args) {
        new Main();
    }

    JFrame frame;
    JTextArea in, out;
    JButton assemble;

    public Main() {
        frame = new JFrame("Z80 Assembler");
        FlowLayout flo = new FlowLayout();
        JPanel pane = new JPanel();
        BoxLayout box = new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS);
        frame.getContentPane().setLayout(box);
        frame.add(new JLabel("Assembly Language:"));
        in = new JTextArea(10, 20);
        in.setFont(new Font("Monospaced", Font.PLAIN, 12));
        frame.add(in);
        assemble = new JButton("Assemble");
        assemble.addActionListener(this);
        frame.add(assemble);
        frame.add(new JLabel("Machine Language:"));
        out = new JTextArea(10, 20);
        out.setFont(new Font("Monospaced", Font.PLAIN, 12));
        frame.add(out);
        frame.add(new JLabel("Case insensitive"));
        frame.add(new JLabel("Can assemble only arithmetic, logic, load, and exchange operations"));
        frame.add(new JLabel("Example Literal: 22h"));
        frame.add(new JLabel("Example Extended Address: (2222)"));
        frame.add(new JLabel("Example Indexed Address: (IX+22)"));
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == assemble) {
            String assembly = in.getText();
            String machine = "";
            machine = assemble(assembly);
            out.setText(machine);
        }
    }

    public String assemble(String in) {
        in = in.toUpperCase();
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        String instruction = "";
        for(int i = 0; i < in.length(); i ++) {
            if (in.charAt(i) == '\n' | i == in.length() - 1) {
                if (i == in.length() - 1) instruction += in.charAt(i);
                instructions.add(new Instruction(instruction));
                instruction = "";
            } else {
                instruction += in.charAt(i);
            }
        }
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        for(Instruction i : instructions) {
            byte[] machine = i.machine();
            for(byte b : machine) {
                bytes.add(b);
            }
        }
        String output = "";
        for(Byte b : bytes) {
            int i = b.intValue() - Byte.MIN_VALUE;
            output += Integer.toHexString(i).toUpperCase();
            output += " ";
            for(int j = 7; j >= 0; j --) {
                double p = Math.pow(2, j);
                int o = i >= p ? 1 : 0;
                output += o;
                i -= p * o;
                if(j == 4) output += " ";
            }
            output += '\n';
        }
        return output;
    }

}
