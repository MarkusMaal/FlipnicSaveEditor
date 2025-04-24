u64 magic @ 0x00;
u32 secondary_crc32 @ 0x08;
u32 primary_crc32 @ 0x0C;
u32 score @ 0x28;
u32 combos @ 0x34;
u32 original_game_stage @ 0x10C8;
u32 footer @ 0x277C;

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
};

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
};

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
};

enum SoundMode : u8 {
    Mono,
    Stereo
};

enum StageCompletion : u8 {
    NotCompleted,
    Started,
    Completed = 0x03
};

enum Difficulty : u32 {
    Easy,
    Normal,
    Hard
};

struct ScoreRow {
    u32 score;
    padding[4];
    Difficulty difficulty;
    u32 combos;
    char initials[0x20];
    padding[4];
    u32 default_score;
};

struct ScoreBoard {
    ScoreRow rank[5];
};

struct Ranking {
    ScoreBoard original_game;
    ScoreBoard biology_a;
    ScoreBoard metallurgy_a;
    ScoreBoard optics_a;
    ScoreBoard biology_b;
    ScoreBoard metallurgy_b;
    ScoreBoard optics_b;
    ScoreBoard geometry_a;
    ScoreBoard biology_a_time_attack;
    ScoreBoard metallurgy_a_time_attack;
    ScoreBoard optics_a_time_attack;
    ScoreBoard biology_b_time_attack;
    ScoreBoard metallurgy_b_time_attack;
    ScoreBoard optics_b_time_attack;
    ScoreBoard geometry_a_time_attack;
};

struct MissionMap {
    Mission biologyA[0x20];
    Mission evolutionA[0x20];
    Mission metallurgyA[0x20];
    Mission evolutionB[0x20];
    Mission opticsA[0x20];
    Mission evolutionC[0x20];
    Mission biologyB[0x20];
    Mission metallurgyB[0x20];
    Mission opticsB[0x20];
    Mission geometryA[0x20];
    Mission evolutionD[0x20];
};

struct StageBool {
    StageCompletion biologyA;
    StageCompletion evolutionA;
    StageCompletion metallurgyA;
    StageCompletion evolutionB;
    StageCompletion opticsA;
    StageCompletion evolutionC;
    StageCompletion biologyB;
    StageCompletion metallurgyB;
    StageCompletion opticsB;
    StageCompletion geometryA;
    StageCompletion evolutionD;
};

struct MissionTypes {
    MissionType color[0x20];
};

struct StageProgression {
    MissionStatus completion[0x20];
};

struct Subpages {
    u8 mission_subpage_count[0x20];
};

struct HelpIndicies {
    u8 mission_image_index[0x20];
};

Difficulty difficulty @ 0x30;
SoundMode sound_mode @ 0x10;
s8 sfx_volume @ 0x11;
s8 bgm_volume @ 0x12;
bool disable_vibration @ 0x13;

Control left_nudge @ 0x16;
Control right_nudge @ 0x17;
Control left_flipper @ 0x23;
Control right_flipper @ 0x19;

StageBool original_unlocks @ 0x274C;
StageBool freeplay_unlocks @ 0x275C;
StageBool last_played_stage @ 0x276C;
//unsure what this is
//StageBool finished_bosses @ 0x258C;

u32 mission_rows[11] @ 0x110C;

Ranking ranking @ 0x60;
StageMap stages @ 0x10CC;
MissionMap mission_names @ 0x114C;

MissionTypes missionTypes[11] @ 0x194C;
StageProgression stage_status_original_game[11] @ 0x214C;
StageProgression stage_status_freeplay[11] @ 0x234C;
Subpages mission_subpages[11] @ 0x1F4C;
HelpIndicies mission_image_indicies[11] @ 0x1D4C;