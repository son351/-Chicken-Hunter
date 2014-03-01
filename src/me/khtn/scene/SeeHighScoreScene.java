package me.khtn.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;

import android.os.Environment;
import me.khtn.app.GameActivity;
import me.khtn.app.GameApplication;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager;
import me.khtn.manager.SceneManager.SceneType;
import me.khtn.model.ListPlayerDTO;
import me.khtn.model.PlayerScore;

public class SeeHighScoreScene extends BaseScene implements IOnMenuItemClickListener {
	private Sprite backgroundSprite;
	private Sprite frameSprite;
	private ListPlayerDTO list;
	private File myFilesDir;
	private Text first, second, third;
	private HUD gameHud;
	private IMenuItem backToMain;
	private List<PlayerScore> newList;
	
	@Override
	public void createScene() {
		newList = new ArrayList<PlayerScore>();
		createBackground();
		GameApplication.GetScore task = new GameApplication.GetScore();
		try {
			list = task.execute(loadJsonFile()).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if (list.getList().size() == 1)
			newList.add(list.getList().get(0));
		else 
			get3Highest();
		
		createHUD();
		createMenu();
	}
	
	public void createMenu() {
		MenuScene menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		backToMain = new ScaleMenuItemDecorator(
				new SpriteMenuItem(1, resourcesManager.backToMainMenuSeeHighScoreRegion, vbom),
				1.7f, 1.5f);
		backToMain.setPosition(200, 200);
		menuChildScene.addMenuItem(backToMain);
		menuChildScene.setBackgroundEnabled(false);
		menuChildScene.setCullingEnabled(true);
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}
	
	public void get3Highest() {		
		boolean flag = false;
		if (list.getList().size() > 0) {
			for (int i = 0; i < list.getList().size(); i++) {
				flag = false;
				for (int j = 0; j < list.getList().size(); j++) {
					if (list.getList().get(i).getScore() < list.getList().get(j).getScore() && i != j) {
						flag = true;						
					}
				}
				if (!flag) {
					if (newList.size() < 3)
						newList.add(list.getList().get(i));
					
					list.getList().remove(i);
					get3Highest();
				}					
			}
		}
	}
	
	public void createHUD(){
		gameHud = new HUD();
		first = new Text(GameActivity.WIDTH/2, GameActivity.HEIGHT/2 + 100, resourcesManager.font, "--- WELCOME EVERYONE ---", new TextOptions(HorizontalAlign.LEFT), vbom);
		second = new Text(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, resourcesManager.font, "CHICKEN HUNTER GAME", new TextOptions(HorizontalAlign.LEFT), vbom);
		third = new Text(GameActivity.WIDTH/2, GameActivity.HEIGHT/2 - 100, resourcesManager.font, "PLAY NOW!!!", new TextOptions(HorizontalAlign.LEFT), vbom);		
		gameHud.attachChild(first);
		gameHud.attachChild(second);
		gameHud.attachChild(third);	
		if (newList.size() == 1) {
			first.setText("1) " + newList.get(0).getName() + " - " + newList.get(0).getScore());
			second.setText("");
			third.setText("");
		}
		else if (newList.size() == 2) {			
			first.setText("1) " + newList.get(0).getName() + " - " + newList.get(0).getScore());
			second.setText("2) " + newList.get(1).getName() + " - " + newList.get(1).getScore());
			third.setText("");
		} else if (newList.size() == 3) {
			first.setText("1) " + newList.get(0).getName() + " - " + newList.get(0).getScore());
			second.setText("2) " + newList.get(1).getName() + " - " + newList.get(1).getScore());
			third.setText("3) " + newList.get(2).getName() + " - " + newList.get(2).getScore());
		}
		camera.setHUD(gameHud);
	}
	
	public String loadJsonFile() {
		String obStr = "";
		String packageName = activity.getPackageName();
		myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/"
				+ packageName + "/files/score.json");
		if (myFilesDir.exists()) {
			try {
				FileInputStream fIn = new FileInputStream(myFilesDir);
				BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
				String aDataRow = "";
				while ((aDataRow = myReader.readLine()) != null) {
					obStr += aDataRow;
				}
				myReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obStr;
	}

	private void createBackground() {
		backgroundSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.seeHighScoreBackgroundRegion, vbom);
		attachChild(backgroundSprite);		
		
		frameSprite = new Sprite(GameActivity.WIDTH/2, GameActivity.HEIGHT/2, resourcesManager.frameSeeHighScoreRegion, vbom);
		frameSprite.setScale(1.5f);
		attachChild(frameSprite);
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SEE_HIGH_SCORE;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
		backgroundSprite.detachSelf();
		backgroundSprite.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		if (pMenuItem.getID() == 1) {
			first.setVisible(false);
			second.setVisible(false);
			third.setVisible(false);
			SceneManager.getInstance().loadMenuScene(engine);
		}
		return false;
	}
}
