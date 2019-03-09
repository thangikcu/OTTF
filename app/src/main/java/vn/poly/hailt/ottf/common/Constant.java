package vn.poly.hailt.ottf.common;

public interface Constant {
    String PREF_SOUND = "SoundPref";
    String IS_SOUND = "isSound";

    String PREF_HIGH_SCORE = "HighScorePref";
    String H_SCORE_SELECTION_MODE = "highScoreSelectionMode";
    String H_SCORE_WORD_GUESS_MODE = "highScoreWordGuessMode";

    String TOPICS_TABLE = "Topics";
    String CM_COL_ID_TOPIC = "_idTopic";
    String TP_COL_TOPIC_NAME = "Name";

    String VOCABULARIES_TABLE = "Vocabularies";
    String VC_COL_ID = "_id";
    String VC_COL_ENGLISH = "English";
    String VC_COL_VIETNAMESE = "Vietnamese";
    String VC_COL_TRANSCRIPTION = "Transcription";
    String VC_COL_CASE_A = "CaseA";
    String VC_COL_CASE_B = "CaseB";
    String VC_COL_CASE_C = "CaseC";
    String VC_COL_CASE_D = "CaseD";

    String PREF_KEY_DB_VER = "dbVer";

    int FROM_ACTION_LEARN = 0;
    int FROM_SELECTION_MODE = 1;
    int FROM_WORD_GUESS_MODE = 2;

    int TEXT_DEFAULT_SIZE = 16;
    int TEXT_SMALL_SIZE = 14;
    int MAX_NUMBER_LETTER_BUTTON = 13;

}
