package me.khtn.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;

import me.khtn.app.GameActivity;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager;
import me.khtn.manager.SceneManager.SceneType;

public class LearnHowToPlayScene extends BaseScene implements IOnMenuItemClickListener {
	private Sprite backgroundSprite;
	private Sprite frameSprite;
	private Sprite instructionSprite;
	private IMenuItem backMenuItem;
	private MenuScene menuChildScene;	

	@Override
	public void createScene() {
		createBackground();
		createMenu();
	}
	
	public void createMenu() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		backMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(1, resourcesManager.backToMainMenuLearnHowToPlayRegion, vbom),
				1.7f, 1.5f);
		backMenuItem.setPosition(150, 150);
		menuChildScene.addMenuItem(backMenuItem);
		menuChildScene.setBackgroundEnabled(false);
		menuChildScene.setCullingEnabled(true);
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	private void createBackground() {
		backgroundSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, 
				GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.learnHowToPlayBackgroundRegion, vbom);
		attachChild(backgroundSprite);		
		
		frameSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, 
				resourcesManager.resultLearnHowToPlayRegion, vbom);
		frameSprite.setScale(1.7f);
		attachChild(frameSprite);
		
		instructionSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, 
				GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.learnHowToPlayInstructionRegion, vbom);
		attachChild(instructionSprite);
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEARN_HOW_TO_PLAY;
	}

	@Override
	public void disposeScene() {
		backgroundSprite.detachSelf();
		backgroundSprite.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		if (pMenuItem.getID() == 1) {
			SceneManager.getInstance().loadMenuScene(engine);
		}
		return false;
	}

}
