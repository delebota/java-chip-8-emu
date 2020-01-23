package com.emu.chip8;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Input extends KeyAdapter {
    Chip8 chip8;

    public Input(Chip8 chip8) {
        this.chip8 = chip8;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        byte[] keys = chip8.getKey();
        char key = Character.toUpperCase(e.getKeyChar());

        switch (key) {
            case '0':
                keys[0x0] = 1;
                break;
            case '1':
                keys[0x1] = 1;
                break;
            case '2':
                keys[0x2] = 1;
                break;
            case '3':
                keys[0x3] = 1;
                break;
            case '4':
                keys[0x4] = 1;
                break;
            case '5':
                keys[0x5] = 1;
                break;
            case '6':
                keys[0x6] = 1;
                break;
            case '7':
                keys[0x7] = 1;
                break;
            case '8':
                keys[0x8] = 1;
                break;
            case '9':
                keys[0x9] = 1;
                break;
            case 'A':
                keys[0xA] = 1;
                break;
            case 'B':
                keys[0xB] = 1;
                break;
            case 'C':
                keys[0xC] = 1;
                break;
            case 'D':
                keys[0xD] = 1;
                break;
            case 'E':
                keys[0xE] = 1;
                break;
            case 'F':
                keys[0xF] = 1;
                break;
            default:
                break;
        }

        chip8.setKey(keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);

        byte[] keys = chip8.getKey();
        char key = Character.toUpperCase(e.getKeyChar());

        switch (key) {
            case '0':
                keys[0x0] = 0;
                break;
            case '1':
                keys[0x1] = 0;
                break;
            case '2':
                keys[0x2] = 0;
                break;
            case '3':
                keys[0x3] = 0;
                break;
            case '4':
                keys[0x4] = 0;
                break;
            case '5':
                keys[0x5] = 0;
                break;
            case '6':
                keys[0x6] = 0;
                break;
            case '7':
                keys[0x7] = 0;
                break;
            case '8':
                keys[0x8] = 0;
                break;
            case '9':
                keys[0x9] = 0;
                break;
            case 'A':
                keys[0xA] = 0;
                break;
            case 'B':
                keys[0xB] = 0;
                break;
            case 'C':
                keys[0xC] = 0;
                break;
            case 'D':
                keys[0xD] = 0;
                break;
            case 'E':
                keys[0xE] = 0;
                break;
            case 'F':
                keys[0xF] = 0;
                break;
            default:
                break;
        }

        chip8.setKey(keys);
    }
}
