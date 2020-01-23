package com.emu.chip8;

public class Main {

    private static Chip8 myChip8;

    public static void main(String args[]) throws InterruptedException {
        // Initialize the Chip8 system and load the game into the memory
        myChip8 = new Chip8();

        // Set up render system and register input callbacks
        myChip8.setupGraphics();
        myChip8.setupInput();

        // Load the game into the memory
        myChip8.loadProgram("test.ch8");

        long taskTime;
        long minCycleTime = 1000/60; // 60Hz - 60/sec
        // Emulation loop
        for (;;) {
            // Start cycle timer
            taskTime = System.currentTimeMillis();

            // Emulate one cycle
            myChip8.emulateCycle();

            // If the draw flag is set, update the screen
            if (myChip8.getDrawFlag()) {
                myChip8.drawGraphics();
                myChip8.setDrawFlag(false);
            }

            // Get time spent running current cycle
            taskTime = System.currentTimeMillis() - taskTime;

            // If less then minCycleTime, wait remaining time
            if (minCycleTime - taskTime > 0) {
                Thread.sleep(minCycleTime - taskTime);
            }
        }
    }

}
