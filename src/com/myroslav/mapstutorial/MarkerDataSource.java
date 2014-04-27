package com.myroslav.mapstutorial;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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

	public List<MyMarkerObj> getMyMarkers(String text) {
		List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();

		String queryString = "SELECT * FROM " + MySQLHelper.TABLE_NAME + 
				" WHERE " + MySQLHelper.FULL_DESC + " LIKE '%" + text + "%'";

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

	public JSONArray getAllRatings() {

		String queryString = "SELECT " + MySQLHelper.POSITION + ", "
				+ MySQLHelper.RESETABLE_RATING + " FROM "
				+ MySQLHelper.TABLE_NAME + " WHERE "
				+ MySQLHelper.RESETABLE_RATING + ">0";

		Log.d("SQL", queryString);

		Cursor cursor = db.rawQuery(queryString, null);

		cursor.moveToFirst();

		JSONArray resultSet = new JSONArray();

		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {

			int totalColumn = cursor.getColumnCount();
			JSONObject rowObject = new JSONObject();

			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {

					try {

						if (cursor.getString(i) != null) {
							// Log.d("TAG_NAME", cursor.getString(i) );
							rowObject.put(cursor.getColumnName(i),
									cursor.getString(i));
						} else {
							rowObject.put(cursor.getColumnName(i), "");
						}
					} catch (Exception e) {
						Log.d("TAG_NAME", e.getMessage());
					}
				}

			}

			resultSet.put(rowObject);
			cursor.moveToNext();
		}

		cursor.close();
		Log.d("TAG_NAME", resultSet.toString());
		return resultSet;
	}

	public void clearResetableRatings() {
		ContentValues v = new ContentValues();

		v.put(MySQLHelper.RESETABLE_RATING, 0);

		db.update(MySQLHelper.TABLE_NAME, v, null, null);

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
