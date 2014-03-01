package me.khtn.manager;

import java.io.IOException;

import me.khtn.app.GameActivity;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

public class ResourcesManager {
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	public static final String SOUND_STATUS = "IS_SOUND_ON";
	public static final String CHICKEN_REF = "CHICKEN_REF";
	public Engine engine;
    public GameActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
    public Font font;
    
    // Splash
    public ITextureRegion splashRegion;
    private BitmapTextureAtlas splashTextureAtlas;
    
    // Menu 
    public ITextureRegion menuBackgroundRegion;
    public ITextureRegion playMenuRegion;
    public ITextureRegion seeHighScoreMenuRegion;
    public ITextureRegion learnHowToPlayMenuRegion;
    public ITextureRegion musicMenuRegion, muteMenuRegion;
    public ITextureRegion aboutMenuRegion; 
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    public Music bgSound;
    
    // Game
    public ITextureRegion gameBackgroundLevel1Region;
    public ITextureRegion gameBackgroundLevel2Region;
    public ITextureRegion resultRegion;
    public ITextureRegion blackRegion;
    public ITextureRegion goodJobRegion;
    public ITextureRegion gameOverRegion;
    public ITextureRegion scoreRegion;
    public ITextureRegion startRegion;
    public ITextureRegion playAgainMenuRegion;
    public ITextureRegion nextLevelMenuRegion;
    public ITextureRegion backToMainMenuRegion;
    public ITextureRegion shareFbMenuRegion;
    public ITiledTextureRegion chickenRegion;
    public ITiledTextureRegion obstacleRegion; 
    public ITiledTextureRegion explosionRegion;
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    
    // ABOUT
    private BuildableBitmapTextureAtlas aboutTextureAtlas;
    public ITextureRegion aboutBackgroundRegion;
    public ITextureRegion aboutFrameRegion;
    public ITextureRegion backToMainMenuAboutRegion;
    public ITextureRegion infoAboutRegion;
    public ITextureRegion badgeAboutRegion;
    
    // LEARN HOW TO PLAY
    private BuildableBitmapTextureAtlas learnHowToPlayAtlas;
    public ITextureRegion learnHowToPlayBackgroundRegion;
    public ITextureRegion backToMainMenuLearnHowToPlayRegion;
    public ITextureRegion learnHowToPlayInstructionRegion;
    public ITextureRegion resultLearnHowToPlayRegion;
    
    // SEE HIGH SCORE
    private BuildableBitmapTextureAtlas seeHighScoreAtlas;
    public ITextureRegion seeHighScoreBackgroundRegion;
    public ITextureRegion backToMainMenuSeeHighScoreRegion;
    public ITextureRegion frameSeeHighScoreRegion;
    
    public static void prepareManager(Engine engine, GameActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
    
    public void storeSoundStatus(boolean status) {
    	SharedPreferences ref = activity.getSharedPreferences(CHICKEN_REF, Context.MODE_PRIVATE);
    	Editor edit = ref.edit();
    	edit.putBoolean(SOUND_STATUS, status);
    	edit.commit();    	
    }
    
    public boolean getSoundStatus() {
    	SharedPreferences ref = activity.getSharedPreferences(CHICKEN_REF, Context.MODE_PRIVATE);
    	boolean soundStatus = ref.getBoolean(SOUND_STATUS, true);
    	return soundStatus;
    }
    
    /* ------------------- LOAD SPLASH SCREEN ------------------- */
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	splashTextureAtlas = new BitmapTextureAtlas(
    			activity.getTextureManager(), GameActivity.WIDTH, GameActivity.HEIGHT, TextureOptions.BILINEAR);
    	splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			splashTextureAtlas, activity, "splash.png", 0, 0);
    	splashTextureAtlas.load();
    }
    
    public void unloadSplashScreen()
    {
    	splashTextureAtlas.unload();
    	splashRegion = null;
    }
    
    /* ------------------- LOAD MENU SCREEN ------------------- */    
    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }    
    
    private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(
    			activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	menuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "bg01.png");
    	playMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "chickenbye_play.png");
    	seeHighScoreMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "chickenbye_cup.png");
    	learnHowToPlayMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "bt_wb.png");
    	musicMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "chickenbye_music.png");
    	muteMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "chickenbye_music2.png");
    	aboutMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			menuTextureAtlas, activity, "chickenbye_information.png");    	       
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    
    private void loadMenuAudio()
    {
    	MusicFactory.setAssetBasePath("mfx/");
        try {
			bgSound = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "bgmusic.mp3");
			bgSound.setLooping(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void loadMenuFonts()
    {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(
        		activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, 
        		activity.getAssets(), "BAUHS93.ttf", 80, true, Color.parseColor("#E69019"), 1, Color.parseColor("#FFFFFF"));
        font.load();
    }
    
    public void unloadMenuScreen()
    {
        menuTextureAtlas.unload();
        menuBackgroundRegion = null;
        playMenuRegion = null;
        seeHighScoreMenuRegion = null;
        learnHowToPlayMenuRegion = null;
        musicMenuRegion = null;
        aboutMenuRegion = null;
        muteMenuRegion = null;
    }
        
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    /* ------------------- LOAD GAME SCREEN ------------------- */
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }
    
    public void loadGameTexture() {
    	gameTextureAtlas.load();
    }

    private void loadGameGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	gameTextureAtlas = new BuildableBitmapTextureAtlas(
    			activity.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
    	playAgainMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "bt_tryagain.png");
    	nextLevelMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "bt_next.png");
    	backToMainMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "button_back.png");
    	shareFbMenuRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "bt_share_fb.png");
    	gameBackgroundLevel1Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "bg02.png");
    	gameBackgroundLevel2Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "bg07.png");
    	resultRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "info_frame.png");
    	blackRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "black_info.png");
    	goodJobRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "good_job.png");
    	gameOverRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "game_over.png");
    	startRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "start.png");    	
    	scoreRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
    			gameTextureAtlas, activity, "score.png");
    	chickenRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
    			gameTextureAtlas, activity, "bird_sprite.png", 4, 2);
    	obstacleRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
    			gameTextureAtlas, activity, "obstacle.png", 3, 1);
    	explosionRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
    			gameTextureAtlas, activity, "explosion.png", 4, 4);
    	try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
    }
    
    private void loadGameFonts()
    {
        
    }
    
    private void loadGameAudio()
    {
        
    }
    
    public void unloadGameScreen()
    {
        gameTextureAtlas.unload();
        playAgainMenuRegion = null;
        nextLevelMenuRegion = null;
    	backToMainMenuRegion = null;
    	shareFbMenuRegion = null;
    	gameBackgroundLevel1Region = null;
    	gameBackgroundLevel2Region = null;
    	resultRegion = null;
    	blackRegion = null;
    	goodJobRegion = null;
    	gameOverRegion = null;
    	startRegion = null;
    	scoreRegion = null;
    	chickenRegion = null;
    	explosionRegion = null;
    }  
    
    /* ------------------- LOAD ABOUT SCREEN ------------------- */
    public void loadAboutResources() {
    	loadAboutGraphics();
        loadAboutFonts();
        loadAboutAudio();
    }
    
    public void loadAboutTexture() {
    	aboutTextureAtlas.load();
    }
    
    private void loadAboutAudio() {
		
	}

	private void loadAboutFonts() {
		
	}

	private void loadAboutGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		aboutTextureAtlas = new BuildableBitmapTextureAtlas(
    			activity.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		aboutBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				aboutTextureAtlas, activity, "bg03.png");
		aboutFrameRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(aboutTextureAtlas, activity, "info_frame.png");		
		backToMainMenuAboutRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(aboutTextureAtlas, activity, "button_back.png");
		infoAboutRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(aboutTextureAtlas, activity, "about_info.png");
		badgeAboutRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(aboutTextureAtlas, activity, "badge.png");
		try 
    	{
    	    this.aboutTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.aboutTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
	}

	public void unloadAboutScreen() {
    	aboutTextureAtlas.unload();
    	aboutBackgroundRegion = null;
    }
    
    /* ------------------- LOAD LEARN HOW TO PLAY SCREEN ------------------- */
    public void loadLearnHowToPlayResources() {
    	loadLearnHowToPlayGraphics();
        loadLearnHowToPlayFonts();
        loadLearnHowToPlayAudio();
    }
    
    public void loadLearnHowToPlayTexture() {
    	learnHowToPlayAtlas.load();
    }
    
    private void loadLearnHowToPlayAudio() {
		
	}

	private void loadLearnHowToPlayFonts() {
		
	}

	private void loadLearnHowToPlayGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		learnHowToPlayAtlas = new BuildableBitmapTextureAtlas(
    			activity.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		learnHowToPlayBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				learnHowToPlayAtlas, activity, "bg05.png");
		learnHowToPlayInstructionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				learnHowToPlayAtlas, activity, "instructions.png");
		backToMainMenuLearnHowToPlayRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				learnHowToPlayAtlas, activity, "button_back.png");
		resultLearnHowToPlayRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				learnHowToPlayAtlas, activity, "info_frame.png");
		try 
    	{
    	    this.learnHowToPlayAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.learnHowToPlayAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
	}

	public void unloadLearnHowToPlayScreen() {
    	learnHowToPlayAtlas.unload();
    	learnHowToPlayBackgroundRegion = null;
    }
    
    /* ------------------- LOAD SEE HIGH SCORE SCREEN ------------------- */
    public void loadSeeHighScoreResources() {
    	loadSeeHighScoreGraphics();
        loadSeeHighScoreFonts();
        loadSeeHighScoreAudio();
    }
    
    public void loadSeeHighScoreTexture() {
    	seeHighScoreAtlas.load();
    }
    
    private void loadSeeHighScoreAudio() {
		
	}

	private void loadSeeHighScoreFonts() {
		
	}

	private void loadSeeHighScoreGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		seeHighScoreAtlas = new BuildableBitmapTextureAtlas(
    			activity.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		seeHighScoreBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				seeHighScoreAtlas, activity, "bg04.png");
		backToMainMenuSeeHighScoreRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				seeHighScoreAtlas, activity, "button_back.png");
		frameSeeHighScoreRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				seeHighScoreAtlas, activity, "info_frame.png");
		try 
    	{
    	    this.seeHighScoreAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.seeHighScoreAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
	}

	public void unloadSeeHighScoreScreen() {
    	seeHighScoreAtlas.unload();
    	seeHighScoreBackgroundRegion = null;
    }
}
