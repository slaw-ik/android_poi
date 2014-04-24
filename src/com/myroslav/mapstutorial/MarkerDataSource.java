package com.myroslav.mapstutorial;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MarkerDataSource {
	MySQLHelper dbhelper;
	SQLiteDatabase db;

	String[] cols = { MySQLHelper.SNIPPET, MySQLHelper.FULL_DESC,
			MySQLHelper.POSITION };

	public MarkerDataSource(Context c) {
		dbhelper = new MySQLHelper(c);
	}

	public void open() {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public void addMarker(MyMarkerObj m) {
		ContentValues v = new ContentValues();

		v.put(MySQLHelper.SNIPPET, m.getSnippet());
		v.put(MySQLHelper.FULL_DESC, m.getFullDesc());
		v.put(MySQLHelper.POSITION, m.getPosition());

		db.insert(MySQLHelper.TABLE_NAME, null, v);
	}

	public void increaseRating(MyMarkerObj m, int i) {
		ContentValues v = new ContentValues();

		v.put(MySQLHelper.RATING, m.getRating() + i);
		v.put(MySQLHelper.RESETABLE_RATING, m.getResetableRating() + i);

		String where = "loc_position='" + m.getPosition() + "'";

		db.update(MySQLHelper.TABLE_NAME, v, where, null);
	}

	public List<MyMarkerObj> getMyMarkers() {
		List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();

		// Cursor cursor = db.query(MySQLHelper.TABLE_NAME, cols, null, null,
		// null, null, null);

		String queryString = "SELECT * FROM " + MySQLHelper.TABLE_NAME;

		Log.d("SQL", queryString);

		Cursor cursor = db.rawQuery(queryString, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MyMarkerObj m = cursorToMarker(cursor);
			markers.add(m);
			cursor.moveToNext();
		}
		cursor.close();
		return markers;
	}

	public List<String> getAllRatings() {
		List<String> markers = new ArrayList<String>();

		// Cursor cursor = db.query(MySQLHelper.TABLE_NAME, cols, null, null,
		// null, null, null);

		String queryString = "SELECT "+MySQLHelper.ID_COL+", " + MySQLHelper.RESETABLE_RATING
				+ " FROM " + MySQLHelper.TABLE_NAME + " WHERE "
				+ MySQLHelper.RESETABLE_RATING + ">0";

		Log.d("SQL", queryString);

		Cursor cursor = db.rawQuery(queryString, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String id = Integer.toString(cursor.getInt(0));
			String rating = Integer.toString(cursor.getInt(1));

			markers.add(id + " -> " + rating);
			Log.d("Ratings:", id + " -> " + rating);
			cursor.moveToNext();
		}
		cursor.close();
		return markers;
	}

	private MyMarkerObj cursorToMarker(Cursor cursor) {
		MyMarkerObj m = new MyMarkerObj();
		m.setSnippet(cursor.getString(1));
		m.setFullDesc(cursor.getString(2));
		m.setPosition(cursor.getString(3));
		m.setRating(cursor.getInt(4));
		m.setResetableRating(cursor.getInt(5));
		return m;
	}

	public MyMarkerObj getMarkerByPositon(String position) {

		String queryString = "SELECT * FROM " + MySQLHelper.TABLE_NAME
				+ " WHERE loc_position = '" + position + "'";

		Log.d("SQL", queryString);

		Cursor cursor = db.rawQuery(queryString, null);
		cursor.moveToFirst();
		MyMarkerObj m = cursorToMarker(cursor);
		cursor.close();
		return m;
	}

}
