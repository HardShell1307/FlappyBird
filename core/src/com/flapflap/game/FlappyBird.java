package com.flapflap.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Map;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Sprite skin;
    Texture background;
    Texture playBtn;
    Texture gameover;
    ShapeRenderer shapeRenderer;
    Texture[] birds;
    Texture topTube;
    Texture bottomTube;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle;
    int gameState = 0;
    float gravity = 2;
    float gap = 400;
    float maxTubeOffset;
    Random randomGenerator;

    float tubeVelocity = 4;

    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTheTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;
    BitmapFont customFont;
    BitmapFont customFont2;
    BitmapFont customFont3;
    Music music;
    Music clicksound;
    Music hitsound;
    boolean hit;
    float y;
    float speed;
    boolean displayStartMessage = true;
    private static final float FLAP_DELAY = 0.125f; // Adjust the value as desired
    private float flapTimer = 0f;
    int highScore ;
        boolean displayScore;

    {
        displayScore = true;
    }


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg3.png");
        gameover = new Texture("gameover.png");
        birds = new Texture[2];
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        playBtn=new Texture("playbutton.png");
        skin=new Sprite(playBtn);
        birds[0] = new Texture("bird2.1.png");
        birds[1] = new Texture("bird2.2.png");
        font = new BitmapFont();
        topTube = new Texture("toptube1.png");
        bottomTube = new Texture("bottomtube1.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTheTubes = Gdx.graphics.getWidth() * 2/3;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        displayStartMessage = true;
        hit=false;
        hitsound=Gdx.audio.newMusic((Gdx.files.internal("hit.mp3")));
        hitsound.setVolume(0.5f);
        clicksound=Gdx.audio.newMusic((Gdx.files.internal("click.mp3")));
        clicksound.setVolume(0.5f);
        music=Gdx.audio.newMusic(Gdx.files.internal("bgmusic2.mp3"));
        music.setLooping(true);
        music.setVolume(0.060f);

        customFont = new BitmapFont(Gdx.files.internal("PS2P.fnt"));
        customFont2 = new BitmapFont(Gdx.files.internal("PS2P.fnt"));
        customFont3 = new BitmapFont(Gdx.files.internal("PS2P.fnt"));



        startGame();

    }

    public void startGame() {
        displayStartMessage = true;
        birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
        y=Gdx.graphics.getHeight();
        speed=500f;


        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (maxTubeOffset);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTheTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }
    }

    @Override
    public void render() {
        customFont.getData().setScale(5f);  // Set the font scale
        customFont.setColor(Color.BLACK);
        customFont2.getData().setScale(1.3f);  // Set the font scale
        customFont2.setColor(Color.WHITE);

        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (displayStartMessage) {
            String message = "Click anywhere to start!";
            displayScore=false;
            GlyphLayout layout = new GlyphLayout(customFont2, message);
            float textX = (Gdx.graphics.getWidth() - layout.width) / 2;
            float textY = (Gdx.graphics.getHeight() - layout.height) *3/4;
            customFont2.draw(batch, message, textX, textY); }
        if (gameState == 1) {
            displayScore = true;

            music.play();
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
                if (score > highScore) {
                    highScore = score;
                }
            }
            if (Gdx.input.justTouched()) {
                displayStartMessage = false;

                clicksound.play();
                velocity = -30;
            }
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTheTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (maxTubeOffset);
                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;


                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap*29/50  + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap *29/50 - bottomTube.getHeight() + tubeOffset[i]);
                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap*29/50 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap*29/50 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }
            int top=Gdx.graphics.getHeight();
            if ((birdY > 0)&&(birdY<top)) {

                velocity += gravity;
                birdY -= velocity;
            } else {

                if(!hit)
                {
                    hitsound.play();
                    hit=true;
                }
                displayStartMessage =false;
                gameState = 2;
            }
        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        }else if(gameState==3)
        {
            if (Gdx.input.justTouched()){
                displayScore = true;
            gameState = 1;
            startGame();
                if (score > highScore) {
                    highScore = score;
                }
            score = 0;
            hit=false;
            scoringTube = 0;
            velocity = 0;
        }
            else {
                displayScore = false; // Hide the score in gameState 3 until clicked
            }
        }
        else if (gameState == 2) {
            displayScore = true;
            displayStartMessage = false;
            birdY-=25;
            if (score > highScore) {
                highScore = score; // Update the high score if the current score is higher
            }
                y -= speed * Gdx.graphics.getDeltaTime();

                if(y>Gdx.graphics.getHeight()*3/4) {
                    batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, y);
                }else{
                    batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight()*3/4);

                }


            if (Gdx.input.justTouched()) {
                displayStartMessage = true;
                gameState=3;

            }
        }


        flapTimer += Gdx.graphics.getDeltaTime();
        if (flapTimer >= FLAP_DELAY) {
            flapState = flapState == 0 ? 1 : 0;
            flapTimer = 0f;
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        customFont.getData().setScale(5f);  // Set the font scale
        customFont.setColor(Color.BLACK);       // Set the font color
        if (displayScore) {
            customFont.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - gap / 6, 200);
        }
        customFont3.getData().setScale(0.85f);  // Set the font scale
        customFont3.setColor(Color.BLACK);
        customFont3.draw(batch, "Current Highscore: " + String.valueOf(highScore), Gdx.graphics.getWidth() / 2 - gap / 5, Gdx.graphics.getHeight() - 80);

        batch.end();


        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i])||Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                if(!hit)
                {
                    hitsound.play();
                    hit=true;
                }
                gameState = 2;
            }
        }

            //shapeRenderer.end();

        }
        @Override
        public void dispose() {

        }


    }
