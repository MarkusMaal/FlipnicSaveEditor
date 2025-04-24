package com.example.flipnic_save_edit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FlipnicSave {

    // declarations
    private List<Byte> dataList = new ArrayList<>();

    private final String[] gameModes = {"Original game", "Biology A", "Biology B", "Metallurgy A", "Metallurgy B", "Optics A", "Optics B", "Geometry A",
            "Biology A (Time Attack)", "Biology B (Time Attack)", "Metallurgy A (Time Attack)", "Metallurgy B (Time Attack)", "Optics A (Time Attack)", "Optics B (Time Attack)", "Geometry A (Time Attack)"};
    private String[] originalModes = {"Biology A", "Evolution A", "Metallurgy A", "Evolution B", "Optics A", "Evolution C", "Biology B", "Metallurgy B", "Optics B", "Geometry A", "Evolution D", "All stages finished"};

    private String[] validStrings = {"ja", "", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "BONUS %dpts", "JACKPOT=%d",
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
    private static String[] inputs = {"L2", "R2", "L1", "R1", "Triangle", "Circle", "Cross", "Square", "Unknown 8", "Unknown 9", "Unknown A", "Unknown B", "DPad Up", "DPad Right", "DPad Down", "DPad Left"};
    private final int[] missionOffsets = {0x114C, 0x124C, 0x134C, 0x144C, 0x14CC, 0x154C, 0x15CC};
    private String[] stageDir = {"JUNGLE1", "ISEKI1", "BOSS1", "RETRO1", "HIKARI1", "DEMO1", "JUNGLE2", "ISEKI2", "HIKARI2", "VS1", "VS2", "VS3", "VS4", "BOSS2", "BOSS3", "BOSS4"};

    public enum Options {
        SOUND_MODE,
        SFX_VOLUME,
        BGM_VOLUME,
        VIBRATION
    }

    public enum Control {
        LEFT_NUDGE,
        LEFT_FLIPPER,
        RIGHT_NUDGE,
        RIGHT_FLIPPER
    }

    public enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    // primary constructor
    public FlipnicSave(byte[] data) {
        for (byte b : data) {
            this.dataList.add(b);
        }
    }

    public void Save(String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            for (byte b : this.dataList) {
                fos.write(b);
            }
        }
    }

    public List<Byte> Read() {
        return this.dataList;
    }

    // internal methods
    private byte ReadByte(int addr, Byte... data) {
        List<Byte> dataList = this.dataList;
        if (data.length > 0) dataList = Arrays.stream(data).toList();

        if (addr >= dataList.size()) {
            System.out.println("Offset " + addr + " out of range!");
            return 0x00;
        }
        return dataList.get(addr);
    }

    private byte[] ReadBytes(int addr, int count, Byte... data) {
        List<Byte> dataList = this.dataList;
        if (data.length > 0) dataList = Arrays.stream(data).toList();

        if (addr + count > dataList.size()) {
            System.out.println("Invalid range: " + addr + " to " + (addr+count));
            return new byte[count];
        }
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr; i < addr + count; i++) {
            returnArray[j] = dataList.get(i);
            j++;
        }
        return returnArray;
    }
    private byte[] ReadBytesLE(int addr, int count, Byte... data) {
        List<Byte> dataList = this.dataList;
        if (data.length > 0) dataList = Arrays.stream(data).toList();

        if (addr + count > dataList.size()) {
            System.out.println("Invalid range: " + addr + " to " + (addr+count));
            return new byte[count];
        }
        byte[] returnArray = new byte[count];
        int j = 0;
        for (int i = addr + count - 1; i >= addr; i--) {
            returnArray[j] = dataList.get(i);
            j++;
        }
        return returnArray;
    }

    private byte[] ReverseArray(byte[] array) {
        // Initialize left to the beginning and right to the end
        int left = 0, right = array.length - 1;

        // Iterate till left is less than right
        while (left < right) {

            // Swap the elements at left and right position
            byte temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // Increment the left pointer
            left++;

            // Decrement the right pointer
            right--;
        }
        return array;
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
        if (input < inputs.length) {
            return inputs[input];
        } else {
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

    // general methods with abstraction layer

    public String[] GetStrings() {
        return this.validStrings;
    }

    public static String[] GetAllInputs() {
        return inputs;
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

    public void UpdateChecksum() {
        byte[] cs1 = CalcChecksum2();
        this.WriteByte(0x0C, cs1[0]);
        this.WriteByte(0x0D, cs1[1]);
        this.WriteByte(0x0E, cs1[2]);
        this.WriteByte(0x0F, cs1[3]);
        byte[] cs2 = CalcChecksum1();
        this.WriteByte(0x08, cs2[0]);
        this.WriteByte(0x09, cs2[1]);
        this.WriteByte(0x0A, cs2[2]);
        this.WriteByte(0x0B, cs2[3]);
    }

    public boolean isLoaded() {
        return !this.dataList.isEmpty();
    }

    public boolean isValidSave() {
        return ConfirmChecksums(true) && ConfirmChecksums(false);
    }

    public boolean isValidHeader() {
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
        int backupStageId = 11;
        for (int offset = 0x2756; offset > 0x274B; offset--) {
            if (ReadByte(offset) == 0x03) {
                break;
            }
            backupStageId--;
        }
        if (stageId == 0x0B) stageId = backupStageId;
        if ((stageId < originalModes.length) && (stageId >= 0)) {
            return originalModes[stageId];
        } else {
            return "Out of range";
        }
    }

    public String GetLastPlayedStage() {
        boolean started = false;
        String stgName = "N/A";
        int i = 0;
        for (int offset = 0x276C; offset < 0x2778; offset++) {
            byte cByte = ReadByte(offset);
            if (cByte > 0x00) {
                started = cByte == 1;
                stgName = originalModes[i];
                break;
            }
            i++;
        }
        if (!stgName.equals("N/A")) {
            if (started) return stgName + " (Started)";
            else return stgName + " (Completed)";
        }
        return stgName;
    }

    public void SetCurrentStage(int value) {
        WriteByte(0x10C8, (byte)value);
    }

    public int GetExplicitStage() {
        return ByteBuffer.wrap(this.ReadBytesLE(0x10C8, 0x4)).getInt();
    }

    public void SetScore(int mode, int idx, int score, String initials, int combos, Difficulty difficulty) {
        if (!isLoaded()) {
            return;
        }
        int offset = 0x60+((5*mode+idx) * 0x38);
        byte[] scoreData = ReadBytes(offset, 0x38);
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(score);
        ByteBuffer comboBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(combos);
        ByteBuffer diffBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(difficulty.ordinal());
        CharBuffer chr = CharBuffer.wrap(initials);
        ByteBuffer cb = StandardCharsets.UTF_8.encode(chr);
        byte[] inb = Arrays.copyOfRange(cb.array(), cb.position(), cb.limit());
        scoreData[0] = b.array()[0];
        scoreData[1] = b.array()[1];
        scoreData[2] = b.array()[2];
        scoreData[3] = b.array()[3];
        scoreData[0x10] = inb[0];
        scoreData[0x11] = inb[1];
        scoreData[0x12] = inb[2];
        scoreData[0x8] = diffBuffer.array()[0];
        scoreData[0x9] = diffBuffer.array()[1];
        scoreData[0xa] = diffBuffer.array()[2];
        scoreData[0xb] = diffBuffer.array()[3];
        scoreData[0xc] = comboBuffer.array()[0];
        scoreData[0xd] = comboBuffer.array()[1];
        scoreData[0xe] = comboBuffer.array()[2];
        scoreData[0xf] = comboBuffer.array()[3];
        for (int i = offset; i < offset + scoreData.length; i++) {
            WriteByte(i, scoreData[i - offset]);
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
        String difficulty = switch (difficultyId) {
            case 0 -> "Easy";
            case 1 -> "Normal";
            case 2 -> "Hard";
            default -> "(null)";
        };
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

    public void SetOption(Options idx, byte value) {
        WriteByte(0x10+idx.ordinal(), value);
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

    public void SetControl(FlipnicSave.Control control, byte value) {
        if (!isLoaded()) return;
        int offset = 0x16;
        offset += control.ordinal();
        if (control == Control.LEFT_FLIPPER) {
            offset = 0x23;
        } else if (control == Control.RIGHT_FLIPPER) {
            offset = 0x19;
        }
        WriteByte(offset, value);
    }

    public boolean[] getUnlocks(boolean isFreePlay) {
        boolean[] unlocks = new boolean[12];
        byte[] unlockBytes;
        if (!isFreePlay) {
            unlockBytes = ReadBytes(0x274C, 12);
        } else {
            unlockBytes = ReadBytes(0x275C, 12);
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

    public String[] GetMissionTypes(int idx) {
        try {
            System.out.println(0x194C + (idx * 0x40));
            if (0x194C + (idx * 0x64) >= this.dataList.size()) {
                return new String[0];
            } else {
                int offset = 0x194C + (idx * 0x40);
                ArrayList<String> types = new ArrayList<>();

                while (offset < 0x194C + (idx * 0x40) + 0x40) {
                    try {
                        if (ReadByte(offset) > 0) {
                            types.add("Red");
                            System.out.print("RED " + offset + " ");
                        } else {
                            types.add("Yellow");
                            System.out.print("YELLOW " + offset + " ");
                        }
                    } catch (Exception ex) {
                        types.add("Invalid");
                    }
                    offset += 2;
                }
                return types.toArray(new String[0]);
            }
        } catch (Exception ex) {
            String[] error = {"Corrupted data"};
            return error;
        }
    }

    public void SetMissionType(int stage, int idx, boolean isRed) {
        if (0x194C + (stage * 0x40) + (idx*2) >= this.dataList.size()) {
            return;
        }
        int offset = 0x194C + (stage * 0x40) + (idx*2);
        byte value = (byte) (isRed ? 0x01 : 0x00);
        WriteByte(offset, value);
    }


    public void SetMission(int stage, int idx, String value) {
        if (0x114C + (stage * 0x80) + idx >= this.dataList.size()) {
            return;
        }
        int offset = 0x114C + (stage * 0x80) + idx;
        byte stringIdx = 0;
        for (String str : validStrings) {
            if (str.equals(value)) {
                break;
            }
            stringIdx++;
        }
        WriteByte(offset, stringIdx);
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

    public void SetStageStatus(int stage, int idx, String value) {
        if (0x214C + (stage * 0x20) + idx >= this.dataList.size()) {
            return;
        }
        int offset = 0x214C + (stage * 0x20) + idx;
        byte val = 0x00;
        switch (value) {
            case "Started":
                val = 0x01;
                break;
            case "Completed":
                val = 0x03;
                break;
            default:
                break;
        }
        WriteByte(offset, val);

    }

    public String[] getStageDirs() {
        List<String> dirs = new ArrayList<>();
        for (int offset = 4300; offset < 4344; offset+=4)
            dirs.add(stageDir[ByteBuffer.wrap(ReadBytesLE(offset, 4)).getInt()]);
        return dirs.toArray(new String[0]);
    }

    public void setStageDir(int idx, int value) {
        WriteByte(4300+(byte)(idx*4), (byte)value);
    }

    public String[] allStageDirs() {
        return this.stageDir;
    }

    public void ToggleJpNames(boolean enabled) {
        List<String> originalModes = new ArrayList<>(Arrays.stream(this.originalModes).toList());
        originalModes.replaceAll(s -> s.replace(enabled ? "Evolution" : "Theology", enabled ? "Theology" : "Evolution"));
        this.originalModes = originalModes.toArray(new String[0]);
    }

    public boolean ImportMessageFile(String path) throws IOException {
        // verify magic
        byte[] fileData = Files.readAllBytes(Path.of(path));
        byte[] reference = HexFormat.of().parseHex("46706e4d73673030");
        byte[] actual = Arrays.copyOfRange(fileData, 0x00, 0x08);
        if (!Arrays.equals(reference, actual)) return false;
        // determine TOC offsets
        int tocStart = ByteBuffer.wrap(this.ReverseArray(Arrays.copyOfRange(fileData, 0x08, 0x0C))).getInt();
        int tocEnd = ByteBuffer.wrap(this.ReverseArray(Arrays.copyOfRange(fileData, 0x0C, 0x10))).getInt() * 8 + tocStart;
        // process data
        byte[] toc = Arrays.copyOfRange(fileData, tocStart, tocEnd);
        List<String> validStrings = new ArrayList<>();
        for (int offset = 0; offset < tocEnd - tocStart; offset+=8) {
            int stringOffset = ByteBuffer.wrap(this.ReverseArray(Arrays.copyOfRange(toc, offset, offset+4))).getInt();
            short charsCnt = ByteBuffer.wrap(this.ReverseArray(Arrays.copyOfRange(toc, offset+4, offset+6))).getShort();
            validStrings.add(new String(Arrays.copyOfRange(fileData, stringOffset, stringOffset + charsCnt), StandardCharsets.UTF_16LE));
        }
        this.validStrings = validStrings.toArray(new String[0]);
        return true;
    }

    public int[] GetMissionIndicies(int idx) {
        int[] indicies = new int[0x20];
        int i = 0;
        for (int offset = 0x1D4C + (idx * 0x20); offset < 0x1D4C + (idx * 0x20) + 0x20; offset++) {
            indicies[i] = ReadByte(offset);
            i++;
        }
        return indicies;
    }

    public int[] GetMissionPages(int idx) {
        int[] pages = new int[0x20];
        int i = 0;
        for (int offset = 0x1F4C + (idx * 0x20); offset < 0x1F4C + (idx * 0x20) + 0x20; offset++) {
            pages[i] = ReadByte(offset);
            i++;
        }
        return pages;
    }


    // diagnostics

    public String[] FixStructure() {
        List<String> fixes = new ArrayList<>();
        if (SizeFix()) fixes.add("Save size fix");
        if (HeaderFix()) fixes.add("Header fix");
        if (FooterFix()) fixes.add("Footer fix");
        if (ChecksumFix()) fixes.add("Checksum fix"); // always do this one last
        return fixes.toArray(new String[0]);
    }

    private boolean FooterFix() {
        byte[] reference = new byte[]{0x22,0x53,0x33,0x02};
        byte[] actual = this.ReadBytes(this.dataList.size() - 4, 4);
        boolean match = Arrays.equals(reference, actual);
        if (match) return false;
        WriteByte(0x277C, (byte)0x22);
        WriteByte(0x277D, (byte)0x53);
        WriteByte(0x277E, (byte)0x33);
        WriteByte(0x277F, (byte)0x02);
        return true;
    }

    private boolean ChecksumFix() {
        if (isValidSave()) return false;
        UpdateChecksum();
        return true;
    }

    private boolean HeaderFix() {
        if (isValidHeader()) return false;
        this.dataList.set(0, (byte)0x34);
        this.dataList.set(1, (byte)0x02);
        this.dataList.set(2, (byte)0xCB);
        this.dataList.set(3, (byte)0x0F);
        this.dataList.set(4, (byte)0x43);
        this.dataList.set(5, (byte)0x55);
        this.dataList.set(6, (byte)0x36);
        this.dataList.set(7, (byte)0x24);
        return true;
    }

    private boolean SizeFix() {
        if (this.dataList.size() == 0x2780) return false;
        if (this.dataList.size() > 0x2780) {
            this.dataList = this.dataList.subList(0, 0x277F);
            return true;
        }
        while (this.dataList.size() < 0x2780) {
            this.dataList.add((byte) 0x00);
        }
        return true;
    }


    public int GetExpectedCount() {
        int expected = 0;
        for (int offset = 0x110C; offset < 0x110C+0x2C; offset+=4) {
            expected += ByteBuffer.wrap(this.ReadBytesLE(offset, 4)).getInt();
        }
        return expected;
    }
}
