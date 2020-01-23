package com.emu.chip8;

import javax.swing.*;
import java.awt.*;

public class GFX extends JPanel {
    byte[] gfx = new byte[64 * 32];

    public void setGfx(byte[] gfx) {
        this.gfx = gfx;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                if (gfx[x + (y * 64)] == 1) {
                    g.setColor(Color.DARK_GRAY);
                } else if (gfx[x + (y * 64)] == 0) {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(x*8, y*8, 8, 8);
            }
        }
    }
}
