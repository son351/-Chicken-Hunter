package me.khtn.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerScore implements Parcelable {
	private String name;
	private int score;
	private int episode;
	private int target;
	private int hitChickens;
	private int hitObstacles;
	private String date;

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the episode
	 */
	public int getEpisode() {
		return episode;
	}

	/**
	 * @param episode the episode to set
	 */
	public void setEpisode(int episode) {
		this.episode = episode;
	}

	/**
	 * @return the target
	 */
	public int getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(int target) {
		this.target = target;
	}

	/**
	 * @return the hitChickens
	 */
	public int getHitChickens() {
		return hitChickens;
	}

	/**
	 * @param hitChickens the hitChickens to set
	 */
	public void setHitChickens(int hitChickens) {
		this.hitChickens = hitChickens;
	}

	/**
	 * @return the hitObstacles
	 */
	public int getHitObstacles() {
		return hitObstacles;
	}

	/**
	 * @param hitObstacles the hitObstacles to set
	 */
	public void setHitObstacles(int hitObstacles) {
		this.hitObstacles = hitObstacles;
	}
	
	public static PlayerScore fromJSON(JSONObject jsonObject) throws JSONException {
		PlayerScore result = new PlayerScore();
		if (jsonObject.has("name"))
			result.setName(jsonObject.getString("name"));
		if (jsonObject.has("score"))
			result.setScore(jsonObject.getInt("score"));
		if (jsonObject.has("episode"))
			result.setEpisode(jsonObject.getInt("episode"));
		if (jsonObject.has("target"))
			result.setTarget(jsonObject.getInt("target"));
		if (jsonObject.has("hit_chicken"))
			result.setHitChickens(jsonObject.getInt("hit_chicken"));
		if (jsonObject.has("hit_obstacle"))
			result.setHitObstacles(jsonObject.getInt("hit_obstacle"));
		if (jsonObject.has("date"))
			result.setDate(jsonObject.getString("date"));
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(date);
		dest.writeInt(episode);
		dest.writeInt(hitChickens);
		dest.writeInt(hitObstacles);
		dest.writeInt(score);
		dest.writeInt(target);
	}
	
	public static final Parcelable.Creator<PlayerScore> CREATOR = new Creator<PlayerScore>() {

		@Override
		public PlayerScore createFromParcel(Parcel source) {
			return new PlayerScore(source);
		}

		@Override
		public PlayerScore[] newArray(int size) {
			return new PlayerScore[size];
		}
	};
	
	public PlayerScore(){		
	}
	
	public PlayerScore(Parcel in) {
		this.date = in.readString();
		this.episode = in.readInt();
		this.hitChickens = in.readInt();
		this.hitObstacles = in.readInt();
		this.name = in.readString();
		this.date = in.readString();
		this.target = in.readInt();
	}

}
