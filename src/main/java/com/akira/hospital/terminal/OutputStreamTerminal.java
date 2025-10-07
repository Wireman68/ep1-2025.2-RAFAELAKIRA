package com.akira.hospital.terminal;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamTerminal extends OutputStream
{
    private final JTextArea area;

    public OutputStreamTerminal(JTextArea area) {
        this.area = area;
    }

    @Override
    public void write(int b) throws IOException {
        area.append(String.valueOf((char) b));
        area.setCaretPosition(area.getDocument().getLength());
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException
    {
        area.append(new String(b, off, len));
        area.setCaretPosition(area.getDocument().getLength());
    }
}
