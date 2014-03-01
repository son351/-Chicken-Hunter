package me.khtn.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.khtn.model.ListPlayerDTO;
import me.khtn.model.PlayerScore;
import android.app.Application;
import android.os.AsyncTask;

public class GameApplication extends Application {
	public static int LEVEL = 1;
	public static File jsonFile = null;
	public static int hitChickens = 0;
	public static int hitObstacles = 0;
	public static int score = 0;
	
	public static class GetScore extends AsyncTask<String, Void, ListPlayerDTO> {

		@Override
		protected ListPlayerDTO doInBackground(String... params) {
			String obStr = params[0];
			ListPlayerDTO dto = new ListPlayerDTO();
			try {
				JSONObject ob = new JSONObject(obStr);
				JSONArray arr = ob.getJSONArray("data");
				List<PlayerScore> list = new ArrayList<PlayerScore>();
				for (int i = 0; i < arr.length(); i++) {
					list.add(PlayerScore.fromJSON(arr.getJSONObject(i)));
				}
				dto.setList(list);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return dto;
		}
		
	}
}
