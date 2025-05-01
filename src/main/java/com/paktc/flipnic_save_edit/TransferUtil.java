package com.paktc.flipnic_save_edit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class TransferUtil {

    byte[] saveData;
    Path localFile;
    boolean export;

    public TransferUtil(byte[] data, Path localFile, boolean export) {
        this.saveData = data;
        this.localFile = localFile;
        this.export = export;
    }

    public void TransferRanks() throws IOException {
        this.TransferBlock(0x60, 0x10C7);
    }

    public void TransferOptions() throws IOException {
        this.TransferBlock(0x10, 0x23);
    }

    public void TransferMissions() throws IOException {
        this.TransferBlock(0x110C, 0x26AB);
    }

    public void TransferUnlocks() throws IOException {
        this.TransferBlock(0x274C, 0x2776);
    }

    public void TransferStages() throws IOException {
        this.TransferBlock(0x10CC, 0x10F7);
    }

    private void TransferBlock(int startRange, int endRange) throws IOException {
        if (this.export) {
            Files.write(localFile, Arrays.copyOfRange(this.saveData, startRange, endRange+1));
            return;
        }
        byte[] rankData = Files.readAllBytes(this.localFile);
        int offset = startRange;
        for (byte b : rankData) {
            saveData[offset] = b;
            offset++;
            if (offset > endRange) break; // prevent writes outside allocated area
        }
    }

    public byte[] GetData() {
        return saveData;
    }
}
