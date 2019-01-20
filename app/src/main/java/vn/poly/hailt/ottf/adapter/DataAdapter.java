package vn.poly.hailt.ottf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.database.DbHelper;
import vn.poly.hailt.ottf.model.Topic;
import vn.poly.hailt.ottf.model.Vocabulary;

public class DataAdapter implements Constant {

    private static final String TAG = "DataAdapter";

    private SQLiteDatabase db;
    private final DbHelper dbHelper;

    public DataAdapter(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void createDatabase() throws SQLException {
        try {
            dbHelper.createDatabase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
    }

    public void open() throws SQLException {
        try {
            dbHelper.openDataBase();
            dbHelper.close();
            db = dbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void close() {
        dbHelper.close();
    }

    public List<Topic> getTopics() {
        List<Topic> topics = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TOPICS_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Topic topic = new Topic();
                topic.id = cursor.getInt(cursor.getColumnIndex(CM_COL_ID_TOPIC));
                topic.name = cursor.getString(cursor.getColumnIndex(TP_COL_TOPIC_NAME));
                topic.imageLink = cursor.getString(cursor.getColumnIndex(CM_COL_IMAGE_LINK));
                topics.add(topic);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return topics;
    }

    public List<Vocabulary> getVocabularies(int idTopic) {
        List<Vocabulary> vocabularies = new ArrayList<>();

        String sql = "SELECT * FROM " + VOCABULARIES_TABLE + " WHERE " + CM_COL_ID_TOPIC + " = ?";
        String[] selectionArgs = {String.valueOf(idTopic)};

        Cursor cursor = db.rawQuery(sql, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.id = cursor.getInt(cursor.getColumnIndex(VC_COL_ID));
                vocabulary.idTopic = cursor.getInt(cursor.getColumnIndex(CM_COL_ID_TOPIC));
                vocabulary.english = cursor.getString(cursor.getColumnIndex(VC_COL_ENGLISH));
                vocabulary.imageLink = cursor.getString(cursor.getColumnIndex(CM_COL_IMAGE_LINK));
                vocabulary.vietnamese = cursor.getString(cursor.getColumnIndex(VC_COL_VIETNAMESE));
                vocabulary.caseA = cursor.getString(cursor.getColumnIndex(VC_COL_CASE_A));
                vocabulary.caseB = cursor.getString(cursor.getColumnIndex(VC_COL_CASE_B));
                vocabulary.caseC = cursor.getString(cursor.getColumnIndex(VC_COL_CASE_C));
                vocabulary.caseD = cursor.getString(cursor.getColumnIndex(VC_COL_CASE_D));
                vocabularies.add(vocabulary);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return vocabularies;
    }

    public List<Vocabulary> getAllVocabularies() {
        List<Vocabulary> vocabularies = new ArrayList<>();

        String sql = "SELECT * FROM " + VOCABULARIES_TABLE + " ORDER BY " + VC_COL_ENGLISH;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.id = cursor.getInt(cursor.getColumnIndex(VC_COL_ID));
                vocabulary.english = cursor.getString(cursor.getColumnIndex(VC_COL_ENGLISH));
                vocabulary.imageLink = cursor.getString(cursor.getColumnIndex(CM_COL_IMAGE_LINK));
                vocabulary.vietnamese = cursor.getString(cursor.getColumnIndex(VC_COL_VIETNAMESE));
                vocabularies.add(vocabulary);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return vocabularies;
    }

}