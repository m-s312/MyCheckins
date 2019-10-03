package android.bignerdranch.mycheckins;
import android.bignerdranch.mycheckins.RecordDBSchema.RecordTable;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class RecordCursorWrapper extends CursorWrapper
{
    public RecordCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Record getRecord() {
        String uuidString = getString(getColumnIndex(RecordTable.Cols.UUID));
        String title = getString(getColumnIndex(RecordTable.Cols.TITLE));
        long date = getLong(getColumnIndex(RecordTable.Cols.DATE));
        String place = getString(getColumnIndex(RecordTable.Cols.PLACE));
        String details= getString(getColumnIndex(RecordTable.Cols.DETAILS));

        Record record = new Record(UUID.fromString(uuidString));

        record.setTitle(title);
        record.setDate(new Date(date));
        record.setPlace(place);
        record.setDetails(details);

        return record;
    }
}