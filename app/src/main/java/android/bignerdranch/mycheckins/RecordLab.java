package android.bignerdranch.mycheckins;


import android.bignerdranch.mycheckins.RecordDBSchema.RecordTable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordLab {

    private List<Record> mRecords;
    private static RecordLab sRecordLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;



    public static RecordLab get(Context c) {
        if (sRecordLab == null) {
            sRecordLab = new RecordLab(c);
        }
        return sRecordLab;
    }
    private  RecordLab(Context c){
        mContext = c.getApplicationContext();
        mDatabase = new RecordBaseHelper(mContext).getWritableDatabase();
    }




    public void addRecord(Record r) {
        ContentValues values = getContentValues(r);
        mDatabase.insert(RecordTable.NAME, null, values);

    }
    public List<Record> getRecords() {
        List<Record> records = new ArrayList<>();
        RecordCursorWrapper cursor = queryRecords(null,
                null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(cursor.getRecord());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return records;
    }




    public Record getRecord(UUID id) {
        RecordCursorWrapper cursor = queryRecords(
                RecordTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecord();
        } finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Record record) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,
                record.getPhotoFilename());
    }
    public void deleteRecord(Record mReord){
        String uuidString = mReord.getId().toString();
        mDatabase.delete(RecordTable.NAME, RecordTable.Cols.UUID + " =?", new String[] { uuidString });}

    public void updateCrime(Record record) {
        String uuidString = record.getId().toString();
        ContentValues values = getContentValues(record);
        mDatabase.update(RecordTable.NAME, values,
                RecordTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }
    private RecordCursorWrapper queryRecords(String
                                                   whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RecordTable.NAME,
                null, // columns - null selects allcolumns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new RecordCursorWrapper(cursor);
    }
    private static ContentValues
    getContentValues(Record record) {
        ContentValues values = new ContentValues();
        values.put(RecordTable.Cols.UUID,
                record.getId().toString());
        values.put(RecordTable.Cols.TITLE,
                record.getTitle());
        values.put(RecordTable.Cols.DATE,
                record.getDate().getTime());
        values.put(RecordTable.Cols.PLACE,
                record.getPlace());
        values.put(RecordTable.Cols.DETAILS,
                record.getDetails());

        return values;
    }



}

