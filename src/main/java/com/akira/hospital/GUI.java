package com.akira.hospital;

import com.akira.hospital.terminal.OutputStreamTerminal;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GUI
{
    private static JTextArea textArea;
    private static JTextField textField;
    private static final BlockingQueue<String> INPUT_DEFERIDO = new LinkedBlockingQueue<>();

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> criarTerminal());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new OutputStreamTerminal(textArea));
        System.setOut(printStream);
        System.setErr(printStream);

        new Thread(() -> Hospital.main(new String[]{})).start();
    }

    public static void criarTerminal()
    {
        JFrame frame = new JFrame("SGH - FCTE");
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 24));
        JScrollPane scrollPane = new JScrollPane(textArea);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 24));

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);

        textField.addActionListener(e -> {
            String input = textField.getText();
            INPUT_DEFERIDO.offer(input);
            textField.setText("");
        });

        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setBackground(Color.black);
        textField.requestFocusInWindow();

        System.setOut(new PrintStream(new OutputStreamTerminal(textArea)));
        System.setErr(new PrintStream(new OutputStreamTerminal(textArea)));
    }

    public static void print(String s) {
        SwingUtilities.invokeLater(() -> textArea.append(s));
    }

    public static void println(String s) {
        print(s + "\n");
    }

    public static void cls() {
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }

    public static String nextLine() {
        while (true) {
            synchronized (INPUT_DEFERIDO) {
                if (!INPUT_DEFERIDO.isEmpty()) {
                    return INPUT_DEFERIDO.poll();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int nextInt() {
        while (true) {
            String line = nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Inválido.");
            }
        }
    }

    public static double nextDouble() {
        while (true) {
            String line = nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Inválido.");
            }
        }
    }
}
