package me.khtn.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.khtn.manager.ResourcesManager;
import me.khtn.manager.SceneManager;
import me.khtn.model.PlayerScore;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.IResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class GameActivity extends BaseGameActivity {
	public static Camera mCamera;
	@SuppressWarnings("unused")
	private ResourcesManager resourcesManager;
	public static int WIDTH = 0;
    public static int HEIGHT = 0;
    String fileFolderForRatio = "";
    private static final GameActivity INSTANCE = new GameActivity();
    
    /* ------------------------ FACEBOOK INTEGRATION -----------------------------*/
    private static final String PERMISSION = "publish_actions";
	private final String PENDING_ACTION_BUNDLE_KEY = "me.khtn.app:PendingAction";
	private PendingAction pendingAction = PendingAction.NONE;
	private boolean canPresentShareDialog;
	private UiLifecycleHelper uiHelper;
	
	private enum PendingAction {
		NONE,
		POST_STATUS_UPDATE
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(Session.getActiveSession() != null)
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}
	
	private Session.StatusCallback statusCallback = new StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
    };
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException)) {                
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }
    
    @SuppressWarnings({ "incomplete-switch" })
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
	}
    
    private void postStatusUpdate() {
		if (hasPublishPermission()) {
			publishFeedDialog();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}
    
    private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
			}
		}
		
		if (allowNoSession) {
			pendingAction = action;
			handlePendingAction();
		}
	}
    
    private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Chicken Hunter");
	    params.putString("caption", "Chicken Hunter");
	    params.putString("description", 100 + "");
	    params.putString("link", "https://www.fit.hcmus.edu.vn");
	    params.putString("picture", "http://www.hcmus.edu.vn/images/stories/logo-khtn.png");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params))
	        .setOnCompleteListener(new WebDialog.OnCompleteListener() {
				
				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(GameActivity.this, "Success!!!", Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(GameActivity.this, "Canceled!!!", Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(GameActivity.this, "Canceled!!!", Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(GameActivity.this, "Network error!!!", Toast.LENGTH_SHORT).show();
	                }
				}
			}).build();
	    feedDialog.show();
	}
    
    private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}

	public void postMsg(int score) {
		Session session = Session.getActiveSession();
		// Already logged in
		if (session.isOpened()) {
			onClickPostStatusUpdate();
		} else {
			onClickLoginFacebook();
		}
	}
	
	private void onClickPostStatusUpdate() {
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}
	
	private void onClickLoginFacebook() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(GameActivity.this).setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		// Facebook
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(pSavedInstanceState);
		if (pSavedInstanceState != null) {
			String name = pSavedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}
				
		Session session = Session.getActiveSession();
		if (session == null) {
			if (pSavedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, pSavedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}
		
		// CREATE JSON FILE FOR SAVING PLAYER SCORE
		createJSON();
	}
	
	////////////////////////////////////////////
    
    public static GameActivity getInstance() {
    	return INSTANCE;
    }
    
	public int getWIDTH() {
		return WIDTH;
	}
	public void setWIDTH(int wIDTH) {
		WIDTH = wIDTH;
	}
	public int getHEIGHT() {
		return HEIGHT;
	}
	public void setHEIGHT(int hEIGHT) {
		HEIGHT = hEIGHT;
	}

	public void setWidthHeight() {
		Point screenSize = getScreenSizeByRatio();
		WIDTH = screenSize.x;
		HEIGHT = screenSize.y;
	}
	
	public Point getScreenSizeByRatio()
    {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		double sw = displaymetrics.heightPixels;
		double sh = displaymetrics.widthPixels;
        double ratio = sw / sh;
        if (sw < sh) ratio = sh/sw;

        Point screenPoints = new Point();
        if (ratio > 1.3 && ratio < 1.4)
        {

            fileFolderForRatio = "1024x768";
            screenPoints.x = 1024;
            screenPoints.y = 768;

        } else
        {

            fileFolderForRatio = "1280x768";
            screenPoints.x = 1280;
            screenPoints.y = 768;

        }

        Debug.e("RATIO", fileFolderForRatio);

        return screenPoints;
    }

	@Override
	public EngineOptions onCreateEngineOptions() {
		setWidthHeight();
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		IResolutionPolicy resolutionPolicy = new RatioResolutionPolicy(WIDTH, HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
				resolutionPolicy, GameActivity.mCamera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {
		ResourcesManager.prepareManager(mEngine, this, GameActivity.mCamera, this.getVertexBufferObjectManager());
		resourcesManager = ResourcesManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            SceneManager.getInstance().createMenuScene();
	        }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    if(this.mEngine != null && !this.mEngine.isRunning())
	    	this.mEngine.start();
	}
	 
	@Override
	protected void onPause() {
		super.onResume();
	    if(this.mEngine != null && this.mEngine.isRunning())
	    	this.mEngine.stop();
	}
	
	/* ---------------------------------- CREATE JSON FILE ------------------------------------------- */		
	private void createJSON() {
		String packageName =  this.getPackageName();		
		File myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/Android/data/" + packageName + "/files");
		if (!myFilesDir.isDirectory())
			myFilesDir.mkdirs();
		GameApplication.jsonFile = getFile();
	}
	
	private File getFile() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" 
						+ this.getPackageName() + "/files" + "/score.json";
		File file = new File(path);
		if (file.exists())
			return file;		
		return createJsonStructure(file);
	}

	private File createJsonStructure(File file) {
		try {
			file.createNewFile();
			String jsonString = "{\"data\":[]}";
			writeJsonFile(file, jsonString);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return file;
	}
	
	// write data to json file
	public static void writeJsonFile(File file, String json) 
	{
	    BufferedWriter bufferedWriter = null;
	    try {
		        if(!file.exists()){
	            file.createNewFile();
	        }
	        FileWriter fileWriter = new FileWriter(file);
	        bufferedWriter = new BufferedWriter(fileWriter);
	        bufferedWriter.write(json);

	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
	        try {
	            if (bufferedWriter != null){
	                bufferedWriter.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}
		
	// Open the json file to get the string format, and prepare to insert a new item
	public static String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(fl);
	    String ret = convertStreamToString(fin);
	    fin.close();        
	    return ret;
	}
		
	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    return sb.toString();
	}
	
	public void saveScore(PlayerScore model) {
		String strFileJson;
		try {
			strFileJson = getStringFromFile(GameApplication.jsonFile.toString());
			JSONObject ob = new JSONObject(strFileJson);
			Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            String jsonStr = jsonParser.parse(gson.toJson(model)).toString();
            JSONObject newOb = new JSONObject(jsonStr);
            ob.getJSONArray("data").put(newOb);
            
            writeJsonFile(GameApplication.jsonFile, ob.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
