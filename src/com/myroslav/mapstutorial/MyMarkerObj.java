package com.myroslav.mapstutorial;

public class MyMarkerObj {
	private long id;
	private String snippet;
	private String position;
	private String full_desc;
	private int rating;
	private int resetable_rating;
	
	public MyMarkerObj() {
		
	}
	
	public MyMarkerObj( String full_desc, String position) {
		Integer lngth = 50;
		if (full_desc.length() < lngth){
			lngth = full_desc.length();
		}
		
		this.snippet = full_desc.substring(0, lngth);
		this.full_desc = full_desc;		
		this.position = position;		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getFullDesc() {
		return full_desc;
	}

	public void setFullDesc(String full_desc) {
		this.full_desc = full_desc;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public int getResetableRating() {
		return resetable_rating;
	}

	public void setResetableRating(int resetable_rating) {
		this.resetable_rating = resetable_rating;
	}
}
