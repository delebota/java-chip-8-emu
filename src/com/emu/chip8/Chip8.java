package com.emu.chip8;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;

public class Chip8 {
    public Chip8() {
        // Program counter starts at 0x200
        programCounter = 0x200;

        // Clear chars
        opcode       = 0;
        I            = 0;
        stackPointer = 0;

        // Clear arrays
        stack  = new char[16];
        gfx    = new byte[64 * 32];
        V      = new byte[16];
        memory = new byte[4096];
        key    = new byte[16];

        // Load fontset
        System.arraycopy(Utils.fontset, 0, memory, 0, 80);

        // Reset timers
        delayTimer = 0;
        soundTimer = 0;
    }

    // Variables
    // OpCode
    private char opcode;

    // Memory Array
    private byte[] memory;

    // Register Array
    private byte[] V;

    // Index Register
    private char I;

    // Program Counter
    private char programCounter;

    // Graphics Array
    private byte[] gfx;

    // Draw Flag
    private boolean drawFlag;

    // Timers
    private byte delayTimer;
    private byte soundTimer;

    // Stack
    private char[] stack;
    private char stackPointer;

    // Keyboard
    private byte[] key;

    // Getters and Setters
    // code fold line - ignore
    public char getOpcode() {
        return opcode;
    }

    public void setOpcode(char opcode) {
        this.opcode = opcode;
    }

    public byte[] getMemory() {
        return memory;
    }

    public void setMemory(byte[] memory) {
        this.memory = memory;
    }

    public byte[] getV() {
        return V;
    }

    public void setV(byte[] v) {
        V = v;
    }

    public char getI() {
        return I;
    }

    public void setI(char i) {
        I = i;
    }

    public char getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(char programCounter) {
        this.programCounter = programCounter;
    }

    public byte[] getGfx() {
        return gfx;
    }

    public void setGfx(byte[] gfx) {
        this.gfx = gfx;
    }

    public boolean getDrawFlag() {
        return drawFlag;
    }

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    public byte getDelayTimer() {
        return delayTimer;
    }

    public void setDelayTimer(byte delayTimer) {
        this.delayTimer = delayTimer;
    }

    public byte getSoundTimer() {
        return soundTimer;
    }

    public void setSoundTimer(byte soundTimer) {
        this.soundTimer = soundTimer;
    }

    public char[] getStack() {
        return stack;
    }

    public void setStack(char[] stack) {
        this.stack = stack;
    }

    public char getStackPointer() {
        return stackPointer;
    }

    public void setStackPointer(char stackPointer) {
        this.stackPointer = stackPointer;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    // Methods
    public void emulateCycle() {
        // Fetch Opcode
        opcode = (char)((char)(memory[programCounter] << 8) | (memory[programCounter + 1] & 0x00FF));

        // Decode & Execute Opcode
        switch (opcode & 0xF000) {
            case 0x0000:
                switch(opcode & 0x000F) {
                    case 0x0000:
                        // 00E0, Clear the screen
                        Arrays.fill(gfx, (byte)0);
                        drawFlag = true;
                        programCounter += 2;
                        break;
                    case 0x000E:
                        // 00EE, Return from subroutine
                        --stackPointer;
                        programCounter = stack[stackPointer];
                        programCounter += 2;
                        break;
                    default:
                        System.out.println("Unknown Opcode: 0x" + Utils.charToHexString(opcode));
                }
                break;
            case 0x1000:
                // 1NNN, Jump to address NNN
                programCounter = (char)(opcode & 0x0FFF);
                break;
            case 0x2000:
                // 2NNN, Call subroutine at NNN
                stack[stackPointer] = programCounter;
                ++stackPointer;
                programCounter = (char)(opcode & 0x0FFF);
                break;
            case 0x3000:
                // 3XNN, Skips the next instruction if VX equals NN
                if (V[(opcode & 0x0F00) >> 8] == (opcode & 0x00FF)) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;
            case 0x4000:
                // 4XNN, Skips the next instruction if VX does not equal NN
                int vX4 = V[(opcode & 0x0F00) >> 8] & 0xFF;
                if (vX4 != (opcode & 0x00FF)) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;
            case 0x5000:
                // 5XY0, Skips the next instruction if VX equals VY
                if (V[(opcode & 0x0F00) >> 8] == V[(opcode & 0x00F0) >> 4]) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;
            case 0x6000:
                // 6XNN, Sets VX to NN
                V[(opcode & 0x0F00) >> 8] = (byte)(opcode & 0x00FF);
                programCounter += 2;
                break;
            case 0x7000:
                // 7XNN, Adds NN to VX
                V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] + (opcode & 0x00FF));
                programCounter += 2;
                break;
            case 0x8000:
                switch(opcode & 0x000F) {
                    case 0x0000:
                        // 8XY0, Sets VX to the value of VY
                        V[(opcode & 0x0F00) >> 8] = V[(opcode & 0x00F0) >> 4];
                        programCounter += 2;
                        break;
                    case 0x0001:
                        // 8XY1, Sets VX to (VX | VY)
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] | V[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;
                    case 0x0002:
                        // 8XY2, Sets VX to (VX & VY)
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] & V[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;
                    case 0x0003:
                        // 8XY3, Sets VX to (VX ^ VY)
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] ^ V[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;
                    case 0x0004:
                        // 8XY4, Add VY to VX, VF is set to 1 when there's a carry, and to 0 when there isn't
                        if (V[(opcode & 0x00F0) >> 4] > (0xFF - V[(opcode & 0x0F00) >> 8])) {
                            V[0xF] = 1; // carry flag
                        } else {
                            V[0xF] = 0;
                        }
                        V[(opcode & 0x0F00) >> 8] += V[(opcode & 0x00F0) >> 4];
                        programCounter += 2;
                        break;
                    case 0x0005:
                        // 8XY5, VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't
                        int vY = V[(opcode & 0x00F0) >> 4] & 0xFF;
                        int vX = V[(opcode & 0x0F00) >> 8] & 0xFF;
                        if (vY > vX) {
                            V[0xF] = 0; // borrow flag
                        } else {
                            V[0xF] = 1;
                        }
                        V[(opcode & 0x0F00) >> 8] -= V[(opcode & 0x00F0) >> 4];
                        programCounter += 2;
                        break;
                    case 0x0006:
                        // 8XY6, Stores the least significant bit of VX in VF and then shifts VX to the right by 1
                        V[0xF] = (byte)(V[(opcode & 0x0F00) >> 8] & 1);
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] >> 1);
                        programCounter += 2;
                        break;
                    case 0x0007:
                        // 8XY7, Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't
                        if (V[(opcode & 0x0F00) >> 8] > V[(opcode & 0x00F0) >> 4]) {
                            V[0xF] = 0; // borrow flag
                        } else {
                            V[0xF] = 1;
                        }
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x00F0) >> 4] - V[(opcode & 0x0F00) >> 8]);
                        programCounter += 2;
                        break;
                    case 0x000E:
                        // 8XYE, Stores the most significant bit of VX in VF and then shifts VX to the left by 1
                        V[0xF] = (byte)(V[(opcode & 0x0F00) >> 8] & 128);
                        V[(opcode & 0x0F00) >> 8] = (byte)(V[(opcode & 0x0F00) >> 8] << 1);
                        programCounter += 2;
                        break;
                    default:
                        System.out.println("Unknown Opcode: 0x" + Utils.charToHexString(opcode));
                }
                break;
            case 0x9000:
                // 9XY0, Skips the next instruction if VX doesn't equal VY
                if (V[(opcode & 0x0F00) >> 8] != V[(opcode & 0x00F0) >> 4]) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;
            case 0xA000:
                // ANNN, Set I to address NNN
                I = (char)(opcode & 0x0FFF);
                programCounter += 2;
                break;
            case 0xB000:
                // BNNN, Jumps to the address NNN plus V0
                programCounter = (char)((opcode & 0x0FFF) + V[0x0]);
                break;
            case 0xC000:
                // CXNN, Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN
                SecureRandom random = new SecureRandom();
                byte[] randomByte = new byte[1];
                random.nextBytes(randomByte);
                V[(opcode & 0x0F00) >> 8] = (byte)(randomByte[0] & (opcode & 0x00FF));
                programCounter += 2;
                break;
            case 0xD000:
                // DXYN, Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
                byte vX = V[(opcode & 0x0F00) >> 8];
                byte vY = V[(opcode & 0x00F0) >> 4];
                byte height = (byte)(opcode & 0x000F);
                byte pixel;

                V[0xF] = 0; // Clear carry flag
                for (int yline = 0; yline < height; yline++) {
                    pixel = memory[I + yline];
                    for (int xline = 0; xline < 8; xline++) {
                        if ((pixel & (0x80 >> xline)) != 0) {
                            if (gfx[(vX + xline + ((vY + yline) * 64))] == 1) {
                                V[0xF] = 1;
                            }
                            gfx[vX + xline + ((vY + yline) * 64)] ^= 1;
                        }
                    }
                }

                drawFlag = true;
                programCounter += 2;
                break;
            case 0xE000:
                switch (opcode & 0x00FF) {
                    case 0x009E:
                        // EX9E, Skips the next instruction if the key stored in VX is pressed
                        byte keyIndex = V[(opcode & 0x0F00) >> 8];
                        if (key[keyIndex] != 0) {
                            programCounter += 4;
                        } else {
                            programCounter += 2;
                        }
                        break;
                    case 0x00A1:
                        // EXA1, Skips the next instruction if the key stored in VX isn't pressed
                        byte keyIndex1 = V[(opcode & 0x0F00) >> 8];
                        if (key[keyIndex1] != 1) {
                            programCounter += 4;
                        } else {
                            programCounter += 2;
                        }
                        break;
                    default:
                        System.out.println("Unknown Opcode: 0x" + Utils.charToHexString(opcode));
                }
                break;
            case 0xF000:
                switch(opcode & 0x00FF) {
                    case 0x0007:
                        // FX07, Sets VX to the value of the delay timer
                        V[(opcode & 0x0F00) >> 8] = delayTimer;
                        programCounter += 2;
                        break;
                    case 0x000A:
                        // FX0A, A key press is awaited, and then stored in VX
                        V[(opcode & 0x0F00) >> 8] = waitForKeyPress();
                        programCounter += 2;
                        break;
                    case 0x0015:
                        // FX15, Sets the delay timer to VX
                        delayTimer = V[(opcode & 0x0F00) >> 8];
                        programCounter += 2;
                        break;
                    case 0x0018:
                        // FX18, Sets the sound timer to VX
                        soundTimer = V[(opcode & 0x0F00) >> 8];
                        programCounter += 2;
                        break;
                    case 0x001E:
                        // FX1E, Adds VX to I. VF is set to 1 when there is a range overflow (I+VX>0xFFF), and to 0 when there isn't
                        if (V[(opcode & 0x0F00) >> 8] + I > 0xFFF) {
                            V[0xF] = 1; // carry flag
                        } else {
                            V[0xF] = 0;
                        }
                        I += V[(opcode & 0x0F00) >> 8];
                        programCounter += 2;
                        break;
                    case 0x0029:
                        // FX29, Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font
                        byte vX1 = V[(opcode & 0x0F00) >> 8];

                        char offset = 0x0;
                        switch (vX1) {
                            case 0x0:
                                break;
                            case 0x1:
                                offset = 0x5;
                                break;
                            case 0x2:
                                offset = 0xA;
                                break;
                            case 0x3:
                                offset = 0xF;
                                break;
                            case 0x4:
                                offset = 0x14;
                                break;
                            case 0x5:
                                offset = 0x19;
                                break;
                            case 0x6:
                                offset = 0x1E;
                                break;
                            case 0x7:
                                offset = 0x23;
                                break;
                            case 0x8:
                                offset = 0x28;
                                break;
                            case 0x9:
                                offset = 0x2D;
                                break;
                            case 0xA:
                                offset = 0x32;
                                break;
                            case 0xB:
                                offset = 0x37;
                                break;
                            case 0xC:
                                offset = 0x3C;
                                break;
                            case 0xD:
                                offset = 0x41;
                                break;
                            case 0xE:
                                offset = 0x46;
                                break;
                            case 0xF:
                                offset = 0x4B;
                                break;
                            default:
                                System.out.println("Failed to set I to the location of the sprite for the character in VX.");
                                System.out.println("opcode (FX29) - Value of vX: " + V[(opcode & 0x0F00) >> 8]);
                                System.out.println("Expected 0-F.");
                        }
                        I = offset;
                        programCounter += 2;
                        break;
                    case 0x0033:
                        // FX33, Stores the Binary-coded decimal representation of VX at the addresses I, I+1, and I+2
                        int vXf3 = V[(opcode & 0x0F00) >> 8] & 0xFF;
                        memory[I]     = (byte)(vXf3 / 100);
                        memory[I + 1] = (byte)((vXf3 / 10) % 10);
                        memory[I + 2] = (byte)((vXf3 % 100) % 10);
                        programCounter += 2;
                        break;
                    case 0x0055:
                        // FX55, Stores V0 to VX (including VX) in memory starting at address I. The offset from I is increased by 1 for each value written, but I itself is left unmodified
                        byte count = (byte)((opcode & 0x0F00) >> 8);
                        for (int i = 0; i <= count; i++) {
                            memory[I + i] = V[i];
                        }
                        programCounter += 2;
                        break;
                    case 0x0065:
                        // FX65, Fills V0 to VX (including VX) with values from memory starting at address I. The offset from I is increased by 1 for each value written, but I itself is left unmodified
                        byte count1 = (byte)((opcode & 0x0F00) >> 8);
                        for (int i = 0; i <= count1; i++) {
                            V[i] = memory[I + i];
                        }
                        programCounter += 2;
                        break;
                    default:
                        System.out.println("Unknown Opcode: 0x" + Utils.charToHexString(opcode));
                }
                break;
            default:
                System.out.println("Unknown Opcode: 0x" + Utils.charToHexString(opcode));
        }

        // Update timers
        if (delayTimer > 0) {
            --delayTimer;
        }

        if (soundTimer > 0) {
            if (soundTimer == 1) {
                System.out.println("*** BEEP! ***");
            }
            --soundTimer;
        }
    }

    public void loadProgram(String romTitle) {
        try {
            Path path = Paths.get("roms\\" + romTitle);
            byte[] bytes = Files.readAllBytes(path);
            System.arraycopy(bytes, 0, memory, 512, bytes.length);
        } catch (Exception e) {
            System.out.println("Failed to load ROM.");
        }
    }

    private byte waitForKeyPress() {
        System.out.print("Waiting for user input (0-F)");
        try {
            boolean waitingForKeypress = true;
            while (waitingForKeypress) {
                int key = System.in.read();
                if (key > 47 && key < 58) {
                    // 0 - 9
                    return (byte)(key - 48);
                } else if (key > 64 && key < 71) {
                    // A - F
                    return (byte)(key - 55);
                } else if (key > 96 && key < 103) {
                    // a - f
                    return (byte)(key - 87);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to read user input.");
        }

        return -1;
    }

    JFrame frame;
    GFX frameGFX = new GFX();
    public void setupGraphics() {
        frame = new JFrame("My Chip 8");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(540, 300);
        frame.add(frameGFX);
        frame.setVisible(true);
    }

    public void drawGraphics() {
        frameGFX.setGfx(gfx);
        frame.repaint();
    }

    public void setupInput() {
        frame.addKeyListener(new Input(this));
    }
}
