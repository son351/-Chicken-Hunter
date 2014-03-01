package me.khtn.scene;

import me.khtn.app.GameActivity;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager.SceneType;

import org.andengine.entity.sprite.Sprite;

public class SplashScene extends BaseScene {
	private Sprite splash;

	@Override
	public void createScene() {
		splash = new Sprite(0, 0, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.splashRegion, vbom);
		splash.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
		attachChild(splash);
	}

	@Override
	public void onBackKeyPressed() {
		
	}

	@Override
	public SceneType getSceneType() {		
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
	    splash.dispose();
	    this.detachSelf();
	    this.dispose();
	}

}
