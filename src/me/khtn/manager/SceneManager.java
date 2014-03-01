package me.khtn.manager;

import me.khtn.app.GameApplication;
import me.khtn.base.BaseScene;
import me.khtn.scene.AboutScene;
import me.khtn.scene.GameScene;
import me.khtn.scene.LearnHowToPlayScene;
import me.khtn.scene.LoadingScene;
import me.khtn.scene.MainMenuScene;
import me.khtn.scene.SeeHighScoreScene;
import me.khtn.scene.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {
	private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    private BaseScene aboutScene;
    private BaseScene seeHighScoreScene;
    private BaseScene learnHowToPlayScene;
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;
    
    private BaseScene currentScene;
    
    private Engine engine = ResourcesManager.getInstance().engine;
    
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
        SCENE_ABOUT,
        SCENE_LEARN_HOW_TO_PLAY,
        SCENE_SEE_HIGH_SCORE
    }    
    
    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            case SCENE_ABOUT:
                setScene(aboutScene);
                break;
            case SCENE_LEARN_HOW_TO_PLAY:
                setScene(learnHowToPlayScene);
                break;
            case SCENE_SEE_HIGH_SCORE:
                setScene(seeHighScoreScene);
                break;
            default:
                break;
        }
    }    
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
    
    /* ------------------- LOAD SPLASH SCENE ------------------- */
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourcesManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene();
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
    
    private void disposeSplashScene() {
        ResourcesManager.getInstance().unloadSplashScreen();
        splashScene.disposeScene();
        splashScene = null;
    }
    
    /* ------------------- LOAD MENU SCENE ------------------- */
    public void createMenuScene() {
    	ResourcesManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene();
        loadingScene = new LoadingScene();
        SceneManager.getInstance().setScene(menuScene);
        disposeSplashScene();
    }
    
    public void loadMenuScene(final Engine mEngine)
    {
        setScene(loadingScene);
        if (gameScene != null) {
        	if (!gameScene.isDisposed())
        		gameScene.disposeScene();
            ResourcesManager.getInstance().unloadGameScreen();
		} else if (seeHighScoreScene != null) {
			if (!seeHighScoreScene.isDisposed())
				seeHighScoreScene.disposeScene();
			ResourcesManager.getInstance().unloadSeeHighScoreScreen();
		} else if (aboutScene != null) {
			if (!aboutScene.isDisposed())
				aboutScene.disposeScene();
			ResourcesManager.getInstance().unloadAboutScreen();
		} else if (learnHowToPlayScene != null) {
			if (!learnHowToPlayScene.isDisposed())
				learnHowToPlayScene.disposeScene();
			ResourcesManager.getInstance().unloadLearnHowToPlayScreen();
		}   
        mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMenuTextures();
                menuScene = new MainMenuScene();
                setScene(menuScene);
            }
        }));
    }
    
    @SuppressWarnings("unused")
	private void disposeMenuScene() {
    	ResourcesManager.getInstance().unloadMenuScreen();
    	menuScene.disposeScene();
    	menuScene = null;
    }
    
    /* ------------------- LOAD GAME SCENE ------------------- */
    public void createGameScene(final Engine mEngine) {
    	loadingScene = new LoadingScene();
    	setScene(loadingScene);
    	mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
		    	gameScene = new GameScene();
		    	GameApplication.LEVEL = 1;
		    	SceneManager.getInstance().setScene(gameScene);
			}
		}));
    }
    
    public void loadGameScene(final Engine mEngine)
    {
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuScreen();
        mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadGameTexture();
                gameScene = new GameScene();
                setScene(gameScene);
            }
        }));
    }
    
    @SuppressWarnings("unused")
	private void disposeGameScene() {
    	ResourcesManager.getInstance().unloadGameScreen();
    	gameScene.disposeScene();
    	gameScene = null;
    }
    
    /* ------------------- LOAD LEARN HOW TO PLAY SCENE ------------------- */
    public void createLearnHowToPlayScene(final Engine mEngine) {
    	loadingScene = new LoadingScene();
    	setScene(loadingScene);
    	mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadLearnHowToPlayResources();
				learnHowToPlayScene = new LearnHowToPlayScene();
				SceneManager.getInstance().setScene(learnHowToPlayScene);
			}
		}));
    }
    
    public void loadLearnHowToPlayScene(final Engine mEngine) {
    	setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuScreen();
        mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadLearnHowToPlayTexture();
				learnHowToPlayScene = new LearnHowToPlayScene();
				setScene(learnHowToPlayScene);
			}
		}));
    }
    
    @SuppressWarnings("unused")
	private void disposeLearnHowToPlayScene() {
    	ResourcesManager.getInstance().unloadLearnHowToPlayScreen();
    	learnHowToPlayScene.disposeScene();
    	learnHowToPlayScene = null;
    }
    
    /* ------------------- LOAD ABOUT SCENE ------------------- */
    public void createAboutScene(final Engine mEngine) {
    	loadingScene = new LoadingScene();
    	setScene(loadingScene);
    	mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadAboutResources();
				aboutScene = new AboutScene();
				SceneManager.getInstance().setScene(aboutScene);
			}
		}));
    }
    
    public void loadAboutScene(final Engine	mEngine) {
    	setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuScreen();
        mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadAboutTexture();
				aboutScene = new AboutScene();
				setScene(aboutScene);
			}
		}));
    }
    
    @SuppressWarnings("unused")
	private void disposeLoadAboutScene() {
    	ResourcesManager.getInstance().unloadAboutScreen();
    	aboutScene.disposeScene();
    	aboutScene = null;
    }
    
    /* ------------------- LOAD SEE HIGH SCORE SCENE ------------------- */
    public void createSeeHighScore(final Engine mEngine) {
    	loadingScene = new LoadingScene();
    	setScene(loadingScene);
    	mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadSeeHighScoreResources();
				seeHighScoreScene = new SeeHighScoreScene();
				SceneManager.getInstance().setScene(seeHighScoreScene);
			}
		}));
    }
    
    public void loadSeeHighScoreScene(final Engine mEngine) {
    	setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuScreen();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadSeeHighScoreTexture();
				seeHighScoreScene = new SeeHighScoreScene();
				setScene(seeHighScoreScene);
			}
		}));
    }
    
    @SuppressWarnings("unused")
	private void disposeLoadSeeHighScoreScene() {
    	ResourcesManager.getInstance().unloadSeeHighScoreScreen();
    	seeHighScoreScene.disposeScene();
    	seeHighScoreScene = null;
    }
}
