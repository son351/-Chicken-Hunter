package me.khtn.scene;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import me.khtn.app.GameActivity;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager;
import me.khtn.manager.SceneManager.SceneType;

public class AboutScene extends BaseScene {
	private Sprite backgroundSprite;
	private Sprite frameSprite;
	private Sprite backToMainMenu;
	private Sprite contentSprite;
	private Sprite badgeSprite;

	@Override
	public void createScene() {
		createBackground();
	}

	private void createBackground() {
		backgroundSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.aboutBackgroundRegion, vbom);
		attachChild(backgroundSprite);
		
		frameSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, resourcesManager.aboutFrameRegion, vbom);
		frameSprite.setScale(1.5f);
		attachChild(frameSprite);
		
		backToMainMenu = new Sprite(0, 0, resourcesManager.backToMainMenuAboutRegion, vbom){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				this.setScale(1.3f);
				SceneManager.getInstance().loadMenuScene(engine);
				return true;
			}
		};
		backToMainMenu.setScale(1.2f);
		registerTouchArea(backToMainMenu);
		backToMainMenu.setPosition(200, 200);
		attachChild(backToMainMenu);
		
		contentSprite = new Sprite(0, 0, resourcesManager.infoAboutRegion, vbom);
		contentSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
		contentSprite.setScale(2.3f);
		attachChild(contentSprite);
		
		badgeSprite = new Sprite(0, 0, resourcesManager.badgeAboutRegion, vbom);
		badgeSprite.setPosition(GameActivity.WIDTH/2 + 430, GameActivity.HEIGHT/2 + 170);
		badgeSprite.setScale(1.8f);
		attachChild(badgeSprite);
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_ABOUT;
	}

	@Override
	public void disposeScene() {
		backgroundSprite.detachSelf();
		backgroundSprite.dispose();
		this.detachSelf();
		this.dispose();
	}

}
