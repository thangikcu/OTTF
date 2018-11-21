package vn.poly.hailt.project1.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.poly.hailt.project1.Constant;
import vn.poly.hailt.project1.database.DbHelper;
import vn.poly.hailt.project1.model.Topic;
import vn.poly.hailt.project1.model.Vocabulary;

public class DataAdapter implements Constant {
    private static String TAG = "DataAdapter";

    private Context context;
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public DataAdapter(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    public DataAdapter createDatabase() throws SQLException {
        try {
            dbHelper.createDatabase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException {
        try {
            dbHelper.openDataBase();
            dbHelper.close();
            db = dbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getTestData() {
        try {
            String sql = "SELECT * FROM Numbers";

            Cursor mCur = db.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
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

    public List<Vocabulary> getData() {
        List<Vocabulary> vocabularies = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + VOCABULARIES_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

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

}