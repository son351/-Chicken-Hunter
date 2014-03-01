package me.khtn.scene;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import me.khtn.app.GameActivity;
import me.khtn.app.GameApplication;
import me.khtn.base.BaseScene;
import me.khtn.manager.SceneManager;
import me.khtn.manager.SceneManager.SceneType;
import me.khtn.model.PlayerScore;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.EditText;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

public class GameScene extends BaseScene implements IOnMenuItemClickListener{
	private HUD gameHUD;
	private Text scoreText, missText, countText, levelText;
	private int score = 0, missCount = 0;
	private PhysicsWorld physicsWorld;
	private Sprite backgroundSprite, resultFrameSprite, goodJobSprite, startSprite, 
		gameOverSprite, scoreSprite, blackInfoSprite;
	private ArrayList<AnimatedSprite> chickens;
	private ArrayList<AnimatedSprite> obstacles;
	private int i = 0, j = 0;
	private boolean isPaused = false;
	private boolean hit = false;
	private boolean hitObstacle = false;
	private boolean missFlag = false;
	private final int MENU_PLAY_AGAIN = 0;
	private final int MENU_BACK_TO_MAIN = 1;
	private final int MENU_SHARE_FB = 2;
	private final int MENU_NEXT_LEVEL = 3;
	private IMenuItem playAgainMenu, backToMainMenu, shareFbMenu, nextLevelMenu;
	private MenuScene menu;
	private ArrayList<IEntity> listEntities;
	private static final GameScene INSTANCE = new GameScene();
	
	public static GameScene getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void createScene() {
		score = 0;
		chickens = new ArrayList<AnimatedSprite>();
		obstacles = new ArrayList<AnimatedSprite>();
		listEntities = new ArrayList<IEntity>();
		createBackground();
		createMenuGame();
		createHUD();
		createPhysics();
		setTouchAreaBindingOnActionDownEnabled(true);
		registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				addTarget();
			}
		}));
		
		registerUpdateHandler(new TimerHandler(4f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				addObstacle();
			}
		}));
		
		registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (hit) {
							hit = false;
							score += 10;
							scoreText.setText("Score: " + score);
							GameApplication.hitChickens++;
						}
						if (hitObstacle) {
							hitObstacle = false;
							if (GameApplication.LEVEL == 1)
								score -= 10;
							else if (GameApplication.LEVEL == 2)
								score -= 20;
							else if (GameApplication.LEVEL == 3)
								score -= 30;
							else if (GameApplication.LEVEL == 4)
								score -= 40;
							else if (GameApplication.LEVEL == 5)
								score -= 50;
							
							scoreText.setText("Score: " + score);
							GameApplication.hitObstacles++;
						}
						if ((score >= 100 && GameApplication.LEVEL == 1) || (score >= 200 && GameApplication.LEVEL == 2) 
								|| (score >= 300 && GameApplication.LEVEL == 3) 
								|| (score >= 400 && GameApplication.LEVEL == 4)
								|| (score >= 500 && GameApplication.LEVEL == 5))
							win();
						if (missFlag) {
							missFlag = false;
							missCount++;
							missText.setText("Miss: " + missCount + "/3");
						}
						if (missCount == 3)
							gameOver();
					}
				});
			}
		}));
	}

	private void createHUD() {
		gameHUD = new HUD();

		// CREATE SCORE TEXT, MISS TEXT, COUNT TEXT WHEN GAME FINISHED
		scoreText = new Text(20, GameActivity.HEIGHT - 100, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setAnchorCenter(0, 0);
		scoreText.setText("Score: 0");
		
		missText = new Text(0, 0, resourcesManager.font, "Miss 0/3", new TextOptions(HorizontalAlign.RIGHT), vbom);
		missText.setPosition(GameActivity.WIDTH - 180, GameActivity.HEIGHT - 50);
		missText.setText("Miss: 0/3");
		
		countText = new Text(0, 0, resourcesManager.font, "0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
		countText.setPosition(GameActivity.WIDTH/2 + 120, GameActivity.HEIGHT/2);
		countText.setVisible(false);
		
		levelText = new Text(0, 0, resourcesManager.font, "Level: 1/5", new TextOptions(HorizontalAlign.CENTER), vbom);
		levelText.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT - 50);
		levelText.setText("Level " + GameApplication.LEVEL);
		
		gameHUD.attachChild(levelText);
		gameHUD.attachChild(countText);
		gameHUD.attachChild(missText);
		gameHUD.attachChild(scoreText);

		camera.setHUD(gameHUD);
	}
	
	private void createMenuGame() {
		menu = new MenuScene(camera);
		menu.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
		playAgainMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY_AGAIN, resourcesManager.playAgainMenuRegion, vbom),
				1.3f, 1f);
		nextLevelMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_NEXT_LEVEL, resourcesManager.nextLevelMenuRegion, vbom),
				1.3f, 1f);
		backToMainMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK_TO_MAIN, resourcesManager.backToMainMenuRegion, vbom),
				1.5f, 1.3f);
		shareFbMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SHARE_FB, resourcesManager.shareFbMenuRegion, vbom),
				1.5f, 1.3f);
		listEntities.add(playAgainMenu);
		listEntities.add(nextLevelMenu);
		listEntities.add(backToMainMenu);
		listEntities.add(shareFbMenu);
		menu.addMenuItem(backToMainMenu);
		menu.addMenuItem(nextLevelMenu);
		menu.addMenuItem(playAgainMenu);
		menu.addMenuItem(shareFbMenu);
		menu.setBackgroundEnabled(false);
		menu.setCullingEnabled(true);
		menu.setOnMenuItemClickListener(this);
		playAgainMenu.setPosition(-200, -200);
		playAgainMenu.setVisible(false);
		nextLevelMenu.setPosition(180, -200);
		nextLevelMenu.setVisible(false);
		shareFbMenu.setPosition(-480, -50);
		shareFbMenu.setVisible(false);
		backToMainMenu.setPosition(-480, -200);
		backToMainMenu.setVisible(false);
		setChildScene(menu);
		menu.setIgnoreUpdate(true);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		registerUpdateHandler(physicsWorld);
	}

	private void createBackground() {
		if (GameApplication.LEVEL == 1 || GameApplication.LEVEL == 3 || GameApplication.LEVEL == 5) {
			backgroundSprite = new Sprite(0, 0, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.gameBackgroundLevel1Region, vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					missFlag = true;
					return false;
				}
			};
			backgroundSprite.setScale(1.5f);
			registerTouchArea(backgroundSprite);
			backgroundSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
			attachChild(backgroundSprite);	
			listEntities.add(backgroundSprite);
		} else if (GameApplication.LEVEL == 2 || GameApplication.LEVEL == 4) {
			backgroundSprite = new Sprite(0, 0, GameActivity.WIDTH, GameActivity.HEIGHT, resourcesManager.gameBackgroundLevel2Region, vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					missFlag = true;
					return false;
				}
			};
			backgroundSprite.setScale(1.5f);
			registerTouchArea(backgroundSprite);
			backgroundSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);
			attachChild(backgroundSprite);	
			listEntities.add(backgroundSprite);
		}
		
		resultFrameSprite = new Sprite(0, 0, resourcesManager.resultRegion, vbom);
		resultFrameSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2 - 70);
		resultFrameSprite.setVisible(false);
		attachChild(resultFrameSprite);
		listEntities.add(resultFrameSprite);
		
		goodJobSprite = new Sprite(0, 0, resourcesManager.goodJobRegion, vbom);
		goodJobSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2 + 120);
		goodJobSprite.setVisible(false);
		attachChild(goodJobSprite);
		listEntities.add(goodJobSprite);
		
		gameOverSprite = new Sprite(0, 0, resourcesManager.gameOverRegion, vbom);
		gameOverSprite.setPosition(GameActivity.WIDTH/2, GameActivity.HEIGHT/2 + 120);
		gameOverSprite.setVisible(false);
		attachChild(gameOverSprite);
		listEntities.add(gameOverSprite);
		
		startSprite = new Sprite(0, 0, resourcesManager.startRegion, vbom);
		startSprite.setPosition(GameActivity.WIDTH/2 - 250, GameActivity.HEIGHT/2);
		startSprite.setVisible(false);
		attachChild(startSprite);
		listEntities.add(startSprite);		
		
		scoreSprite = new Sprite(0, 0, resourcesManager.scoreRegion, vbom);
		scoreSprite.setPosition(GameActivity.WIDTH/2 - 110, GameActivity.HEIGHT/2);
		scoreSprite.setVisible(false);
		attachChild(scoreSprite);
		listEntities.add(scoreSprite);
		
		blackInfoSprite = new Sprite(0, 0, resourcesManager.blackRegion, vbom);
		blackInfoSprite.setPosition(GameActivity.WIDTH/2 + 120, GameActivity.HEIGHT/2);
		blackInfoSprite.setVisible(false);
		attachChild(blackInfoSprite);
		listEntities.add(blackInfoSprite);
	}

	private void gameOver() {
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        final String createdDate = sdf.format(calendar.getTime());
        
		activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					final EditText nameText = new EditText(activity);
					nameText.setGravity(Gravity.CENTER_HORIZONTAL);
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setView(nameText);
					builder.setTitle("Please enter your name: ");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlayerScore model = new PlayerScore();
							model.setDate(createdDate);
							model.setEpisode(GameApplication.LEVEL);
							model.setHitChickens(GameApplication.hitChickens);
							model.setHitObstacles(GameApplication.hitObstacles);
							if (nameText.getText().toString().equals(""))
								model.setName("Player");
							else
								model.setName(nameText.getText().toString());
							model.setScore(GameApplication.score + score);							
							int target = 0;
							if (GameApplication.LEVEL == 1)
								target = 100;
							else if (GameApplication.LEVEL == 2)
								target = 200;
							else if (GameApplication.LEVEL == 3)
								target = 300;
							else if (GameApplication.LEVEL == 4)
								target = 400;
							else if (GameApplication.LEVEL == 5)
								target = 500;
							model.setTarget(target);
							GameActivity.getInstance().saveScore(model);
						}
					});
					builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			});		
		isPaused = true;
		gameOverSprite.setVisible(true);
		resultFrameSprite.setVisible(true);
		startSprite.setVisible(true);
		scoreSprite.setVisible(true);
		blackInfoSprite.setVisible(true);
		blackInfoSprite.setScale(1f);
		scoreSprite.setScale(1.5f);
		playAgainMenu.setVisible(true);
		backToMainMenu.setVisible(true);
		countText.setVisible(true);
		countText.setText(String.valueOf(score));
		this.setIgnoreUpdate(true);			
	}
	
	public void win() {
		GameApplication.score = score;
		isPaused = true;
		goodJobSprite.setVisible(true);
		resultFrameSprite.setVisible(true);
		startSprite.setVisible(true);
		scoreSprite.setVisible(true);
		blackInfoSprite.setVisible(true);
		blackInfoSprite.setScale(1f);
		scoreSprite.setScale(1.5f);
		playAgainMenu.setVisible(true);
		backToMainMenu.setVisible(true);
		if (GameApplication.LEVEL < 5)
			nextLevelMenu.setVisible(true);
		shareFbMenu.setVisible(true);
		countText.setVisible(true);
		countText.setText(String.valueOf(score));
		this.setIgnoreUpdate(true);
	}
	
	public void addObstacle() {
		Random rand = new Random();
		int x = -10;
		int minY = 10;
	    int maxY = GameActivity.HEIGHT - 50;
	    int rangeY = maxY - minY;
	    int y = rand.nextInt(rangeY) + minY;
		obstacles.add(new AnimatedSprite(x, y, resourcesManager.obstacleRegion, vbom) {
			@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) {					
	    		if (!isPaused) {
	    			AnimatedSprite explosion = new AnimatedSprite(
	        				pTouchAreaLocalX, pTouchAreaLocalY, resourcesManager.explosionRegion, vbom);
	        		explosion.animate(100, false);
	        		attachChild(explosion);
	        		hitObstacle = true;
	        		missFlag = false;
	        		registerUpdateHandler(new TimerHandler(0.4f, new ITimerCallback() {
						
						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {
							engine.runOnUpdateThread(new Runnable() {
								
								@Override
								public void run() {
									setVisible(false);	
								}
							});
						}
					}));   
				}	    		     	
	    		return true;
	    	}
		});
		int minDuration = 5;
		int maxDuration = 9;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;
	    MoveXModifier mod = new MoveXModifier(actualDuration, -10, GameActivity.WIDTH + 100);
	    obstacles.get(j).registerEntityModifier(mod.deepCopy());
	    obstacles.get(j).setPosition(x, y);
	    obstacles.get(j).setCullingEnabled(true);
	    obstacles.get(j).animate(70);
	    registerTouchArea(obstacles.get(j));
	    attachChild(obstacles.get(j));
	    j++;
	}

	public void addTarget() {
		Random rand = new Random();
		int x = -10;
		int minY = 10;
	    int maxY = GameActivity.HEIGHT - 50;
	    int rangeY = maxY - minY;
	    int y = rand.nextInt(rangeY) + minY;
	    chickens.add(new AnimatedSprite(x, y, resourcesManager.chickenRegion.deepCopy(), vbom){
	    	@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) {		    		
	    		if (!isPaused) {
	    			AnimatedSprite explosion = new AnimatedSprite(
	        				pTouchAreaLocalX, pTouchAreaLocalY, resourcesManager.explosionRegion, vbom);
	        		explosion.animate(100, false);
	        		attachChild(explosion);
	        		hit = true;
	        		missFlag = false;
	        		registerUpdateHandler(new TimerHandler(0.4f, new ITimerCallback() {
						
						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {
							engine.runOnUpdateThread(new Runnable() {
								
								@Override
								public void run() {
									setVisible(false);		
								}
							});
						}
					}));   
				}	    		     	
	    		return true;
	    	}
	    });		    
	    chickens.get(i).setPosition(x, y);
	    chickens.get(i).setCullingEnabled(true);	    
	    int minDuration = 0;
		int maxDuration = 0;
	    if (GameApplication.LEVEL == 1) {
			minDuration = 8;
			maxDuration = 12;
			chickens.get(i).animate(70);
		} else if (GameApplication.LEVEL == 2) {
			minDuration = 5;
			maxDuration = 8;
			chickens.get(i).animate(60);
		} else if (GameApplication.LEVEL == 3) {
			minDuration = 3;
			maxDuration = 5;
			chickens.get(i).animate(50);
		} else if (GameApplication.LEVEL == 4) {
			minDuration = 2;
			maxDuration = 4;
			chickens.get(i).animate(40);
		} else if (GameApplication.LEVEL == 5) {
			minDuration = 1;
			maxDuration = 3;
			chickens.get(i).animate(30);
		}	    
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;
	    MoveXModifier mod = new MoveXModifier(actualDuration, x, GameActivity.WIDTH + 100);
	    chickens.get(i).registerEntityModifier(mod.deepCopy());
	    registerTouchArea(chickens.get(i));	    
	    attachChild(chickens.get(i));
	    i++;
	}

	@Override
	public void onBackKeyPressed() {
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(GameActivity.WIDTH/2, GameActivity.HEIGHT/2);

		// TODO code responsible for disposing scene
		// removing all game scene objects.
		clearPhysicsWorld(physicsWorld);
		if (listEntities != null) {
			for (IEntity entity : listEntities) {
				entity.clearEntityModifiers();
				entity.clearUpdateHandlers();
				entity.detachSelf();
				
				if (!entity.isDisposed())
				{
					entity.dispose();
				}
			}
			listEntities.clear();
			listEntities = null;
		}	
		this.detachSelf();
		this.dispose();
	}
	
	protected void clearPhysicsWorld(PhysicsWorld physicsWorld)
	{
		Iterator<Joint> allMyJoints = physicsWorld.getJoints();
		while (allMyJoints.hasNext())
		{
			try
			{
				final Joint myCurrentJoint = allMyJoints.next();
				physicsWorld.destroyJoint(myCurrentJoint);
			} 
			catch (Exception localException)
			{
				Debug.d("SPK - THE JOINT DOES NOT WANT TO DIE: " + localException);
			}
		}
		
		Iterator<Body> localIterator = physicsWorld.getBodies();
		while (true)
		{
			if (!localIterator.hasNext())
			{
				physicsWorld.clearForces();
				physicsWorld.clearPhysicsConnectors();
				physicsWorld.reset();
				physicsWorld.dispose();
				physicsWorld = null;
				return;
			}
			try
			{
				physicsWorld.destroyBody(localIterator.next());
			} 
			catch (Exception localException)
			{
				Debug.d("SPK - THE BODY DOES NOT WANT TO DIE: " + localException);
			}
		}
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		if (isPaused) {
			switch (pMenuItem.getID()) {
			case MENU_PLAY_AGAIN:
				GameApplication.score = 0;
				GameApplication.LEVEL = 1;
				GameApplication.hitChickens = 0;
				GameApplication.hitObstacles = 0;
				this.disposeScene();
				GameScene gameScene = new GameScene();
				SceneManager.getInstance().setScene(gameScene);
				break;
			case MENU_BACK_TO_MAIN:		
				scoreText.setVisible(false);
				missText.setVisible(false);
				countText.setVisible(false);
				levelText.setVisible(false);
				SceneManager.getInstance().loadMenuScene(engine);
				break;
			case MENU_SHARE_FB:
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(activity);
						builder.setMessage("This feature is not available right now!");
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.show();
					}
				});				
//				GameActivity.getInstance().postMsg(score);
				break;
			case MENU_NEXT_LEVEL:
				GameApplication.LEVEL++;
				this.disposeScene();
				GameScene gameSceneNext = new GameScene();					
				SceneManager.getInstance().setScene(gameSceneNext);
				break;
			}
		}		
		return true;
	}
}
