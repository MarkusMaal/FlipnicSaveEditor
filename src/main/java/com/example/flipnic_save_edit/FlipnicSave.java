package com.example.flipnic_save_edit;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class FlipnicSave {

    // declarations
    private final List<Byte> dataList = new ArrayList<>();

    private final String[] gameModes = {"Original game", "Biology A", "Biology B", "Metallurgy A", "Metallurgy B", "Optics A", "Optics B", "Geometry A",
            "Biology A (Time Attack)", "Biology B (Time Attack)", "Metallurgy A (Time Attack)", "Metallurgy B (Time Attack)", "Optics A (Time Attack)", "Optics B (Time Attack)", "Geometry A (Time Attack)"};
    private final String[] originalModes = {"Biology A", "Evolution A", "Metallurgy A", "Evolution B", "Optics A", "Evolution C", "Biology B", "Metallurgy B", "Optics B", "Geometry A", "Evolution D", "All stages finished"};

    // primary constructor
    public FlipnicSave(byte[] data) {
        for (byte b : data) {
            this.dataList.add(b);
        }
    }

    // internal methods
    private byte ReadByte(int addr) {
        return dataList.get(addr);
    }

    private byte[] ReadBytes(int addr, int count) {
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr; i < addr + count; i++) {
            returnArray[j] = this.dataList.get(i);
            j++;
        }
        return returnArray;
    }
    private byte[] ReadBytesLE(int addr, int count) {
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr + count - 1; i >= addr; i--) {
            returnArray[j] = this.dataList.get(i);
            j++;
        }
        return returnArray;
    }

    private void WriteByte(int addr, byte value) {
        dataList.set(addr, value);
    }

    private void ClearData() {
        for (long i = 0; i < (long) dataList.size(); i++) {
            dataList.set((int)i, (byte)0);
        }
    }

    // credit: https://www.baeldung.com/java-byte-arrays-hex-strings
    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    private String DecodeInput(byte input) {
        switch (input) {
            case 0x0F:
                return "DPad Left";
            case 0x0E:
                return "DPad Down";
            case 0x0D:
                return "DPad Right";
            case 0x0C:
                return "DPad Up";
            case 0x07:
                return "Square";
            case 0x06:
                return "Cross";
            case 0x05:
                return "Circle";
            case 0x04:
                return "Triangle";
            case 0x03:
                return "R1";
            case 0x02:
                return "L1";
            case 0x01:
                return "R2";
            case 0x00:
                return "L2";
            default:
                return "Unknown";
        }
    }

    // general methods with abstraction layer
    public String GetChecksum() {
        byte[] rawChecksum = ReadBytes(0x8, 0x8);
        StringBuilder decoded = new StringBuilder();
        for (int i = rawChecksum.length - 1; i >= 0; i--) {
            decoded.append(byteToHex(rawChecksum[i]));
        }
        return decoded.toString().toUpperCase();
    }

    public void SetChecksum(String hexSum) {
        String lcase = hexSum.toLowerCase();
        int offset = 0x15;
        for (int i = lcase.length() - 1; i >= 0; i-=2) {
            char digitA = lcase.charAt(i);
            char digitB = lcase.charAt(i - 1);
            String hexByte = digitA + String.valueOf(digitB);
            WriteByte(offset, hexToByte(hexByte));
            offset -= 1;
        }
    }

    public boolean isLoaded() {
        return !this.dataList.isEmpty();
    }

    public boolean isValidSave() {
        byte[] reference = HexFormat.of().parseHex("3402cb0f43553624");
        byte[] actual = this.ReadBytes(0, 8);
        return Arrays.equals(reference, actual);
    }

    public int GetCurrentScore() {
        byte[] scoreData = this.ReadBytesLE(0x28, 0x4);
        return ByteBuffer.wrap(scoreData).getInt();
    }

    public String GetCurrentStage() {
        byte[] stageIdBytes = this.ReadBytesLE(0x10C8, 0x4);
        int stageId = ByteBuffer.wrap(stageIdBytes).getInt();
        if (stageId < originalModes.length) {
            return originalModes[stageId];
        } else {
            return "Out of range";
        }
    }

    public String[] GetScore(int idx) {
        if (!isLoaded()) {
            return new String[0];
        }
        int offset = 0x60+(idx * 0x38);
        byte[] scoreData = ReadBytes(offset, 0x38);
        byte[] scoreValBytes = { scoreData[3], scoreData[2], scoreData[1], scoreData[0] };
        int scoreVal = ByteBuffer.wrap(scoreValBytes).getInt();
        int modeIdx = (idx - (idx % 5)) / 5;
        int rank = idx % 5;
        rank++;
        byte[] initalsBytes = {scoreData[0x10], scoreData[0x11], scoreData[0x12]};
        String initials = new String(initalsBytes, Charset.defaultCharset());
        byte[] combosBytes = {scoreData[0xF], scoreData[0xE], scoreData[0xD], scoreData[0xC]};
        int combos = ByteBuffer.wrap(combosBytes).getInt();
        byte[] difficultyBytes = {scoreData[0xB], scoreData[0xA], scoreData[0x9], scoreData[0x8]};
        int difficultyId = ByteBuffer.wrap(difficultyBytes).getInt();
        String difficulty;
        switch (difficultyId) {
            case 0:
                difficulty = "Easy";
                break;
            case 1:
                difficulty = "Normal";
                break;
            case 2:
                difficulty = "Hard";
                break;
            default:
                difficulty = "(null)";
                break;
        }
        return ((rank) + ";" + initials + ";" + scoreVal + ";" + combos + ";" + offset + ";" + difficulty + ";" + gameModes[modeIdx]).split(";", 0);
    }

    // options
    public String getSoundMode() {
        byte soundMode = ReadByte(0x10);
        if (soundMode == 0x00) {
            return "Mono";
        } else {
            return "Stereo";
        }
    }
    public int getVolumeSfx() {
        return ReadByte(0x11);
    }
    public int getVolumeBgm() {
        return ReadByte(0x12);
    }

    // true = on
    // false = off
    public boolean getVibration() {
        return ReadByte(0x13) == 0x00;
    }



    public String getLeftFlipper() {
        return DecodeInput(ReadByte(0x23));
    }
    public String getRightFlipper() {
        return DecodeInput(ReadByte(0x19));
    }
    public String getLeftNudge() {
        return DecodeInput(ReadByte(0x16));
    }
    public String getRightNudge() {
        return DecodeInput(ReadByte(0x17));
    }

    public boolean[] getUnlocks(boolean isFreePlay) {
        boolean[] unlocks = new boolean[11];
        byte[] unlockBytes;
        if (!isFreePlay) {
            unlockBytes = ReadBytes(0x274C, 11);
        } else {
            unlockBytes = ReadBytes(0x275C, 11);
        }
        for (int i = 0; i < unlockBytes.length; i++) {
            unlocks[i] = unlockBytes[i] == 0x03;
        }
        return unlocks;
    }

    public void ResetGame(boolean isFreePlay) {
        int offset = 0x274C;
        if (isFreePlay) { offset+=0x10; }
        for (int i = offset; i < offset + 0x10; i++) {
            WriteByte(i, (byte) 0x00);
        }
    }

    public void WriteUnlock(boolean isFreePlay, int idx, boolean unlocked) {
        int offset = 0x274C;
        if (isFreePlay) { offset += 0x10; }
        offset += idx;
        WriteByte(offset , (byte)(unlocked?0x03:0x00));
    }
}
