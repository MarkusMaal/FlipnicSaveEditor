package com.example.flipnic_save_edit;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FlipnicSave {

    // declarations
    private final List<Byte> dataList = new ArrayList<>();

    private final String[] gameModes = {"Original game", "Biology A", "Biology B", "Metallurgy A", "Metallurgy B", "Optics A", "Optics B", "Geometry A",
            "Biology A (Time Attack)", "Biology B (Time Attack)", "Metallurgy A (Time Attack)", "Metallurgy B (Time Attack)", "Optics A (Time Attack)", "Optics B (Time Attack)", "Geometry A (Time Attack)"};
    private final String[] originalModes = {"Biology A", "Evolution A", "Metallurgy A", "Evolution B", "Optics A", "Evolution C", "Biology B", "Metallurgy B", "Optics B", "Geometry A", "Evolution D", "All stages finished"};

    private final String[] validStrings = {"ja", "", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "BONUS %dpts", "JACKPOT=%d",
            "IN", "%d", "?Go ?Back", "Yes", "No", "CONTINUE?", "RESULT", "FLAMINGO COUNTS", "FLAMINGO BONUS",
            " % d Pts.", " ----- Pts.", "PERFECT BONUS", "TOTAL SCORE", "WHY DON'T YOU", "GET STARS ?", "YOU GET",
            "ALL", "FLAMINGOS!", "%d/10", "COMBO %d", "SLOT CHANCE!", "%d Pts.", "ALIEN HILL !", "GALAXY TENNIS !",
            "AREA 74 !", "SPACE WARP !", "100_BLOCKS !", "WARNING!", "BUMPER AREA", "NON STOP AREA", "AREA EXIT",
            "CREDIT(S) %d", "NO BONUS", "EXP COUNTS %d", "POINTS × %d", "READY", "%dPts.", "DANGER!", "LEVEL %d",
            "BINGO %d!¥n%ldpts.!¥n", "ANSWER", "GOOD!", "FLAMINGO BONUS", "BANANA BONUS", "BONUS", "BONUS", "REST BALL BONUS",
            "PERFECT BONUS", "LEVEL BONUS", "PERFECT BONUS", "Pts.", "TOTAL SCORE", "GET ALL THE COLORS!", "GET FIVE RED COLORS!",
            "YOU GOT A COLOR", "THIS IS NOT REQUIRED", "START", "EXIT", "KICK OFF", "TIP OFF", "READY", "READY", "GOAL!",
            "%dP WINS", "DRAW", "ZERO GRAVITY", "MULTIBALL 1", "MULTIBALL 2", "MULTIBALL 3", "LANE COUNTS MISSION 1", "LANE COUNTS MISSION 2",
            "LANE COUNTS MISSION 3", "TOTAL LANE COUNTS", "TOTAL BUMPER COUNTS", "EXTRA BALL", "EXTRA CREDIT", "FREEZE OVER", "HIDDEN PATH DISCOVERY",
            "CIRCLE OF LIFE", "BUMPER VILLAGE", "PERFECT BUMPER VILLAGE", "LUCKY FLAMINGOS", "HUNGRY MONKEY", "COLOR PUZZLE", "MONEY MONEY MONEY",
            "UFO QUIZ SHOW", "MOVE ON 1", "MOVE ON 2", "SPIDER CRAB SHOOT-DOWN", "STOP THE FOUR SHAFTS 1", "STOP THE FOUR SHAFTS 2", "UFO SHOOT-DOWN",
            "CRAB BABY SHOOT-DOWN", "POINT OF NO RETURN 1", "POINT OF NO RETURN 2", "POINT OF NO RETURN 3", "LOOP THE LOOP 1", "LOOP THE LOOP 2",
            "LOOP THE LOOP 3", "CHU CHU MULTIBALL", "SPACE WARP", "ALIEN HILL", "AREA 74", "GALAXY TENNIS", "100 BLOCKS", "WARM-COLORED BLOCKS"};

    private final int[] missionOffsets = {0x114C, 0x124C, 0x134C, 0x144C, 0x14CC, 0x154C, 0x15CC};

    // primary constructor
    public FlipnicSave(byte[] data) {
        for (byte b : data) {
            this.dataList.add(b);
        }
    }

    // internal methods
    private byte ReadByte(int addr) {
        if (addr >= this.dataList.size()) {
            System.out.println("Offset " + addr + " out of range!");
            return 0x00;
        }
        return dataList.get(addr);
    }

    private byte[] ReadBytes(int addr, int count) {
        if (addr + count > this.dataList.size()) {
            System.out.println("Invalid range: " + addr + " to " + (addr+count));
            return new byte[count];
        }
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr; i < addr + count; i++) {
            returnArray[j] = this.dataList.get(i);
            j++;
        }
        return returnArray;
    }
    private byte[] ReadBytesLE(int addr, int count) {
        if (addr + count > this.dataList.size()) {
            System.out.println("Invalid range: " + addr + " to " + (addr+count));
            return new byte[count];
        }
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr + count - 1; i >= addr; i--) {
            returnArray[j] = this.dataList.get(i);
            j++;
        }
        return returnArray;
    }

    private void WriteByte(int addr, byte value) {
        if (addr >= this.dataList.size()) {
            System.out.println("Offset " + addr + " out of range!");
            return;
        }
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

    private String DecodeString(byte idx) {
        if (idx >= this.validStrings.length) {
            return "(null)";
        } else if (idx >= 0) {
            return this.validStrings[idx];
        } else {
            return "(null)";
        }
    }

    // calculates the first checksum, found at offsets 0x8-0xb
    public byte[] CalcChecksum1() {
        byte[] gameData = ReadBytes(0xc, this.dataList.size() - 0xc);
        Checksum chkSum = new CRC32();
        chkSum.update(gameData, 0, gameData.length);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(chkSum.getValue()  ^ 0xFFFFFFFFL);
        byte[] BeArray = buffer.array();
        return new byte[]{BeArray[7],BeArray[6],BeArray[5],BeArray[4]}; // output 32-bit LE
    }

    // calculates the second checksum, found at offsets 0xc-0xf
    public byte[] CalcChecksum2() {
        byte[] gameData = ReadBytes(0x10, this.dataList.size() - 0x10);
        Checksum chkSum = new CRC32();
        chkSum.update(gameData, 0, gameData.length);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(chkSum.getValue()  ^ 0xFFFFFFFFL);
        byte[] BeArray = buffer.array();
        return new byte[]{BeArray[7],BeArray[6],BeArray[5],BeArray[4]}; // output 32-bit LE
    }

    public boolean ConfirmChecksums(boolean secondary) {
        return Arrays.equals(ReadBytes(secondary ? 0xc : 0x8, 0x4), secondary ? CalcChecksum2() : CalcChecksum1());
    }

    // general methods with abstraction layer
    public String GetChecksum(boolean secondary) {
        byte[] rawChecksum = ReadBytes(secondary ? 0xc : 0x8, 0x4);
        byte[] onlineChecksum = secondary ? CalcChecksum2() : CalcChecksum1();
        boolean match = true;
        StringBuilder decoded = new StringBuilder();
        StringBuilder decodedOnline = new StringBuilder();
        for (int i = rawChecksum.length - 1; i >= 0; i--) {
            if (match) {
                match = rawChecksum[i] == onlineChecksum[i];
            }
            decoded.append(byteToHex(rawChecksum[i]));
            decodedOnline.append(byteToHex(onlineChecksum[i]));
        }
        System.out.println("Offline checksum: " + decoded.toString().toUpperCase());
        System.out.println("Online checksum: " + decodedOnline.toString().toUpperCase());
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
        return Arrays.equals(reference, actual) && ConfirmChecksums(true) && ConfirmChecksums(false);
    }

    public int GetCurrentScore() {
        byte[] scoreData = this.ReadBytesLE(0x28, 0x4);
        return ByteBuffer.wrap(scoreData).getInt();
    }

    public String GetCurrentStage() {
        byte[] stageIdBytes = this.ReadBytesLE(0x10C8, 0x4);
        int stageId = ByteBuffer.wrap(stageIdBytes).getInt();
        if ((stageId < originalModes.length) && (stageId >= 0)) {
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


    public String[] GetMissions(int idx) {
        try {
            if (0x114C + (idx * 0x80) >= this.dataList.size()) {
                return new String[0];
            } else {
                int offset = 0x114C + (idx * 0x80);
                ArrayList<String> missions = new ArrayList<>();
                String decodedString = "";

                while (offset < 0x114C + (idx * 0x80) + 0x80) {
                    try {
                        decodedString = DecodeString(ReadByte(offset));
                        if (!decodedString.equals("ja")) {
                            missions.add(decodedString);
                        }
                        offset += 4;
                    } catch (Exception ex) {
                        missions.add("Bad mission");
                        offset += 4;
                    }
                }
                return missions.toArray(new String[0]);
            }
        } catch (Exception ex) {
            String[] error = {"Corrupted data"};
            return error;
        }
    }

    public String[] GetStageStatus(int idx) {
        if (0x214C + (idx * 0x20) >= this.dataList.size()) {
            return new String[0];
        } else {
            int offset = 0x214C + (idx * 0x20);
            ArrayList<String> status = new ArrayList<>();
            for (int x = offset; x < offset + 0x20; x++) {
                switch (ReadByte(x))
                {
                    case 0x0:
                        status.add("Not completed");
                        break;
                    case 0x1:
                        status.add("Started");
                        break;
                    case 0x2:
                    case 0x3:
                        status.add("Completed");
                        break;
                    default:
                        status.add("Invalid");
                        break;
                }
            }
            return status.toArray(new String[0]);
        }
    }
}
