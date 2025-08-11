#include <std/mem>
#include <std/hash>

struct PrimaryData {
    u8 Data[0x2770];
};

PrimaryData pD @ 0x10 [[hidden]];
u32 CalculatedCRC32 = std::hash::crc32(pD, 0xFFFFFFFF, 0x4C11DB7, 0x0, true, true) [[export]];

u64 Magic @ 0x00;
u32 SecondaryCRC32 @ 0x08 [[name("Secondary checksum (CRC32)")]];
u32 PrimaryCRC32 @ 0x0C [[name("Primary checksum (CRC32)")]];
u32 Score @ 0x28;
u32 Combos @ 0x34;
u32 OriginalGameStage @ 0x10C8 [[name("Original game stage")]];
u32 Footer @ 0x277C;

enum Control : u8 {
    L2,
    R2,
    L1,
    R1,
    Triangle,
    Circle,
    Cross,
    Square,
    DPadUp = 0x0C,
    DPadRight,
    DPadDown,
    DPadLeft
};

enum Mission : u32 {
    UNDEFINED,
    ZERO_GRAVITY = 0x4f,
    MULTIBALL_1,
    MULTIBALL_2,
    MULTIBALL_3,
    LANE_COUNTS_MISSION_1,
    LANE_COUNTS_MISSION_2,
    LANE_COUNTS_MISSION_3,
    TOTAL_LANE__COUNTS,
    TOTAL_BUMPER_COUNTS,
    EXTRA_BALL,
    EXTRA_CREDIT,
    FREEZE_OVER,
    HIDDEN_PATH_DISCOVERY,
    CIRCLE_OF_LIFE,
    BUMPER_VILLAGE,
    PERFECT_BUMPER_VILLAGE,
    LUCKY_FLAMINGOS,
    HUNGRY_MONKEY,
    COLOR_PUZZLE,
    MONEY_MONEY_MONEY,
    UFO_QUIZ_SHOW,
    MOVE_ON_1,
    MOVE_ON_2,
    SPIDER_CRAB_SHOOT_DOWN,
    STOP_THE_FOUR_SHAFTS_1,
    STOP_THE_FOUR_SHAFTS_2,
    UFO_SHOOT_DOWN,
    CRAB_BABY_SHOOT_DOWN,
    POINT_OF_NO_RETURN_1,
    POINT_OF_NO_RETURN_2,
    POINT_OF_NO_RETURN_3,
    LOOP_THE_LOOP_1,
    LOOP_THE_LOOP_2,
    LOOP_THE_LOOP_3,
    CHU_CHU_MULTIBALL,
    SPACE_WARP,
    ALIEN_HILL,
    AREA_74,
    GALAXY_TENNIS,
    ONE_HUNDRED_BLOCKS,
    WARM_COLORED_BLOCKS
};

enum MissionType : u16 {
    Yellow,
    Red
} [[name("Mission type")]];

enum Directory : u32 {
    JUNGLE1,
    ISEKI1,
    BOSS1,
    RETRO1,
    HIKARI1,
    DEMO1,
    JUNGLE2,
    ISEKI2,
    HIKARI2,
    VS1,
    VS2,
    VS3,
    VS4,
    BOSS2,
    BOSS3,
    BOSS4
};

enum MissionStatus : u8 {
    NotCompleted,
    Started,
    Completed = 0x02 ... 0x03
} [[name("Mission status")]];

struct StageMap {
    Directory BiologyA;
    Directory EvolutionA;
    Directory MetallurgyA;
    Directory EvolutionB;
    Directory OpticsA;
    Directory EvolutionC;
    Directory BiologyB;
    Directory MetallurgyB;
    Directory OpticsB;
    Directory GeometryA;
    Directory EvolutionD;
} [[single_color]];

enum SoundMode : u8 {
    Mono,
    Stereo
};

enum StageCompletion : u8 {
    NotCompleted,
    Started,
    Completed = 0x03
} [[name("Stage completion")]];;

enum Difficulty : u32 {
    Easy,
    Normal,
    Hard
};

struct String {
    const u32 start = $;
    char Initials[while(std::mem::read_unsigned($, 1) != 0x0 && ($ < start + 0x20))];
    padding[while($ < start + 0x20)];
};

struct ScoreRow {
    u32 Score;
    padding[4];
    Difficulty Difficulty;
    u32 Combos;
    String InitialsArea [[name("Initials (reserved area)")]];
    padding[4];
    u32 DefaultScore [[name("Default score")]];
};

struct ScoreBoard {
    ScoreRow Rank[5];
} [[single_color]];

struct Ranking {
    ScoreBoard OriginalGame [[name("Original game")]];
    ScoreBoard BiologyA [[name("Biology A")]];
    ScoreBoard MetallurgyA [[name("Metallurgy A")]];
    ScoreBoard OpticsA [[name("Optics A")]];
    ScoreBoard BiologyB [[name("Biology B")]];
    ScoreBoard MetallurgyB [[name("Metallurgy B")]];
    ScoreBoard OpticsB [[name("Optics B")]];
    ScoreBoard GeometryA [[name("Geometry A")]];
    ScoreBoard BiologyATimeAttack [[name("Biology A (Time Attack)")]];
    ScoreBoard MetallurgyATimeAttack [[name("Metallurgy A (Time Attack)")]];
    ScoreBoard OpticsATimeAttack [[name("Optics A (Time Attack)")]];
    ScoreBoard BiologyBTimeAttack [[name("Biology B (Time Attack)")]];
    ScoreBoard MetallurgyBTimeAttack [[name("Metallurgy B (Time Attack)")]];
    ScoreBoard OpticsBTimeAttack [[name("Optics B (Time Attack)")]];
    ScoreBoard GeometryATimeAttack [[name("Geometry A (Time Attack)")]];
};

struct MissionMap {
    Mission BiologyA[0x20];
    Mission EvolutionA[0x20];
    Mission MetallurgyA[0x20];
    Mission EvolutionB[0x20];
    Mission OpticsA[0x20];
    Mission EvolutionC[0x20];
    Mission BiologyB[0x20];
    Mission MetallurgyB[0x20];
    Mission OpticsB[0x20];
    Mission GeometryA[0x20];
    Mission EvolutionD[0x20];
};

struct StageBool {
    StageCompletion BiologyA;
    StageCompletion EvolutionA;
    StageCompletion MetallurgyA;
    StageCompletion EvolutionB;
    StageCompletion OpticsA;
    StageCompletion EvolutionC;
    StageCompletion BiologyB;
    StageCompletion MetallurgyB;
    StageCompletion OpticsB;
    StageCompletion GeometryA;
    StageCompletion EvolutionD;
} [[single_color]];

struct MissionTypes {
    MissionType Color[0x20];
};

struct StageProgression {
    MissionStatus Completion[0x20];
};

struct Subpages {
    u8 MissionSubpageCount[0x20];
};

struct HelpIndicies {
    u8 MissionImageIndex[0x20];
};

Difficulty Difficulty @ 0x30 [[name("Difficulty level")]];
SoundMode SoundMode @ 0x10 [[name("Sound mode")]];
s8 SFXVolume @ 0x11 [[name("SFX volume")]];
s8 BGMVolume @ 0x12 [[name("BGM volume")]];
bool DisableVibration @ 0x13 [[name("Disable vibration")]];;

Control LeftNudge @ 0x16 [[name("Left nudge")]];
Control RightNudge @ 0x17 [[name("Right nudge")]];
Control LeftFlipper @ 0x23  [[name("Left flipper")]];
Control RightFlipper @ 0x19 [[name("Right flipper")]];

StageBool OriginalUnlocks @ 0x274C [[name("Original game unlocks")]];
StageBool FreeplayUnlocks @ 0x275C [[name("Free play unlocks")]];
StageBool LastPlayedStage @ 0x276C [[name("Last stage played")]];

u32 MissionRows[11] @ 0x110C [[name("Mission rows")]];

Ranking Ranking @ 0x60;
StageMap Stages @ 0x10CC;
MissionMap MissionNames @ 0x114C [[name("Mission names")]];;


MissionTypes MissionTypes[11] @ 0x194C [[name("Mission types")]];
StageProgression StageStatusOriginalGame[11] @ 0x214C [[name("Original game stage status")]];
StageProgression StageStatusFreeplay[11] @ 0x234C [[name("Free play stage status")]];
StageProgression StageStatusCurrent[11] @ 0x254C [[name("Last session stage status")]];
Subpages MissionSubpages[11] @ 0x1F4C [[name("Mission subpages")]];;
HelpIndicies MissionImageIndicies[11] @ 0x1D4C [[name("Mission indicies")]];;