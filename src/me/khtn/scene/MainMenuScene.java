package me.khtn.scene;

import me.khtn.app.GameActivity;
import me.khtn.app.GameApplication;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager;
import me.khtn.manager.SceneManager.SceneType;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;

public class MainMenuScene extends BaseScene implements
		IOnMenuItemClickListener {
	private Sprite bg;
	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_SEE_HIGH_SCORE = 1;
	private final int MENU_LEARN_HOW_TO_PLAY = 2;
	private final int MENU_MUSIC = 3;
	private final int MENU_ABOUT = 4;
	private boolean mute = false;
	private IMenuItem playMenuItem, seeHighScoreMenuItem, learnHowToPlayMenuItem, musicMenuItem, muteMenuItem,
			aboutMenuItem;	

	@Override
	public void createScene() {		
		createBackground();
		createMenuChildScene();
		createSound();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		bg.detachSelf();
		bg.dispose();
		this.detachSelf();
		this.dispose();
	}

	private void createBackground() {
		bg = new Sprite(0, 0, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.menuBackgroundRegion, vbom);
		bg.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
		attachChild(bg);
	}

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);

		playMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_PLAY, resourcesManager.playMenuRegion, vbom),
				1.7f, 1.5f);
		seeHighScoreMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_SEE_HIGH_SCORE, resourcesManager.seeHighScoreMenuRegion, vbom),
				1.7f, 1.5f);
		learnHowToPlayMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_LEARN_HOW_TO_PLAY, resourcesManager.learnHowToPlayMenuRegion, vbom),
				1.7f, 1.5f);
		musicMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_MUSIC, resourcesManager.musicMenuRegion, vbom),
				1.7f, 1.5f);
		muteMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_MUSIC, resourcesManager.muteMenuRegion, vbom),
				1.7f, 1.5f);
		aboutMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_ABOUT, resourcesManager.aboutMenuRegion, vbom),
				1.7f, 1.5f);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(seeHighScoreMenuItem);
		menuChildScene.addMenuItem(learnHowToPlayMenuItem);
		menuChildScene.addMenuItem(musicMenuItem);
		menuChildScene.addMenuItem(muteMenuItem);
		menuChildScene.addMenuItem(aboutMenuItem);
		menuChildScene.setBackgroundEnabled(false);
		menuChildScene.setCullingEnabled(true);

		playMenuItem.setPosition(0, 50);
		playMenuItem.setScale(1.5f);
		seeHighScoreMenuItem.setPosition(50, -290);
		seeHighScoreMenuItem.setScale(1.5f);
		learnHowToPlayMenuItem.setPosition(200, -290);
		learnHowToPlayMenuItem.setScale(1.5f);
		musicMenuItem.setPosition(-250, -290);
		musicMenuItem.setScale(1.5f);
		muteMenuItem.setPosition(-250, -290);
		muteMenuItem.setScale(1.5f);
		
		mute = resourcesManager.getSoundStatus();
		if (mute) {
			musicMenuItem.setVisible(mute);
			muteMenuItem.setVisible(!mute);
		} else {
			musicMenuItem.setVisible(mute);
			muteMenuItem.setVisible(!mute);
		}
		
		aboutMenuItem.setPosition(-100, -290);	
		aboutMenuItem.setScale(1.5f);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}
	
	private void createSound() {
		resourcesManager.bgSound.play();
		if (!mute)
			resourcesManager.bgSound.pause();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_PLAY:
			//Load Game Scene!
			GameApplication.LEVEL = 1;
			GameApplication.hitChickens = 0;
			GameApplication.hitObstacles = 0;
			GameApplication.score = 0;
			SceneManager.getInstance().createGameScene(engine);
			return true;
		case MENU_SEE_HIGH_SCORE:	
			SceneManager.getInstance().createSeeHighScore(engine);
			return true;
		case MENU_LEARN_HOW_TO_PLAY:			
			SceneManager.getInstance().createLearnHowToPlayScene(engine);
			return true;
		case MENU_MUSIC:	
			if (!mute) {					
				musicMenuItem.setVisible(mute);
				mute = true;
				muteMenuItem.setVisible(mute);
				resourcesManager.bgSound.pause();
				resourcesManager.storeSoundStatus(false);
			} else {				
				musicMenuItem.setVisible(mute);
				mute = false;
				muteMenuItem.setVisible(mute);
				resourcesManager.bgSound.play();
				resourcesManager.storeSoundStatus(true);
			}
			return true;
		case MENU_ABOUT:	
			SceneManager.getInstance().createAboutScene(engine);
			return true;
		default:
			return false;
		}
	}

}
