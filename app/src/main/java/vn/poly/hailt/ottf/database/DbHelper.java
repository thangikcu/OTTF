package vn.poly.hailt.ottf.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import vn.poly.hailt.ottf.Constant;

public class DbHelper extends SQLiteOpenHelper implements Constant {

    private static String DB_NAME = "Vocabularies.sqlite";
    private static String DB_PATH = "";
    private SQLiteDatabase db;
    private Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        }
        this.context = context;
    }

    public void createDatabase() throws IOException {
        boolean isDatabaseExist = checkDatabase();
        if (!isDatabaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabase();
                Log.e("Create Database", "Database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDatabase");
            }
        }
    }


    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDatabase() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return db != null;
    }

    @Override
    public synchronized void close() {
        if (openDataBase())
            db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
