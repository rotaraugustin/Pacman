package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Logic extends JPanel implements ActionListener {

    private Dimension dimension;
    private final Font font=new Font("Arial",Font.ITALIC,15);
    private int nGhosts=6;
    private int lives,score;
    private int [] dx,dy;
    private int [] ghostX,ghostY,ghostDx,ghostDy,ghostSpeed;
    private boolean inGame=false;
    private boolean dying=false;
    private final int blockSize=24;
    private final int nBlocks=15;
    private final int screenSize=blockSize*nBlocks;
    private final int maxGhosts=12;
    private final int pacmanSpeed=6;
    private Image heart,ghost;
    private Image up,down,left,right;
    private int pacmanX,pacmanY,pacmanDx,pacmanDy;
    private int reqDx,reqDy;
    private final int validSpeeds[]={1,2,3,4,6,8};
    private final int maxSpeed=6;
    private int currentSpeed=3;
    private short [] screenData;
    private Timer timer;
    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };//0-obstacle,1-left border,2-top border,4-right-border,8-bottom border,16-white dots

    public Logic(){
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();//start game
    }

    private void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        nGhosts = 6;
        currentSpeed = 3;
    }
    private void playGame(Graphics2D g2d) {
        if (dying) {
            

        } else {

            movePacman();
            drawPacman(g2d);
        }
    }

    public void paintComponent(Graphics g) {

    }
    private void initLevel() {
        int i;
        for (i = 0; i < nBlocks * nBlocks; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;
        int random;

        for (int i = 0; i < nGhosts; i++) {

            ghostY[i] = 4 * blockSize; //start position
            ghostX[i] = 4 * blockSize;
            ghostDy[i] = 0;
            ghostDx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacmanX = 7 * blockSize;  //start position
        pacmanY = 11 * blockSize;
        pacmanDx = 0;	//reset direction move
        pacmanDy = 0;
        reqDx = 0;		// reset direction controls
        reqDy = 0;
        dying = false;
    }

    private void initVariables() {
        screenData=new short[nBlocks*nBlocks];
        dimension = new Dimension(400, 400);
        ghostX = new int[maxGhosts];
        ghostDx = new int[maxGhosts];
        ghostY = new int[maxGhosts];
        ghostDy = new int[maxGhosts];
        ghostSpeed = new int[maxGhosts];
        dx = new int[4];
        dy = new int[4];
        timer = new Timer(40, this);//speed of the game
        timer.start();
    }

    private void loadImages() {
        down = new ImageIcon("src/pacman/images/down.gif").getImage();
        up = new ImageIcon("/src/pacman/images/up.gif").getImage();
        left = new ImageIcon("/src/pacman/images/left.gif").getImage();
        right = new ImageIcon("/src/pacman/images/right.gif").getImage();
        ghost = new ImageIcon("/src/pacman/images/ghost.gif").getImage();
        heart = new ImageIcon("/src/pacman/images/heart.png").getImage();
    }
    private void movePacman() {

        int pos;
        short ch;

        if (pacmanX % blockSize == 0 && pacmanY % blockSize == 0) {
            pos = pacmanX/blockSize + nBlocks * (int) (pacmanY/blockSize);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx == -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx == 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy == -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy == 1 && (ch & 8) != 0))) {
                    pacmanDx = reqDx;
                    pacmanDy = reqDy;
                }
            }

            // Check for standstill
            if ((pacmanDx == -1 && pacmanDy == 0 && (ch & 1) != 0)
                    || (pacmanDx == 1 && pacmanDy == 0 && (ch & 4) != 0)
                    || (pacmanDx == 0 && pacmanDy == -1 && (ch & 2) != 0)
                    || (pacmanDx == 0 && pacmanDy == 1 && (ch & 8) != 0)) {
                pacmanDx = 0;
                pacmanDy = 0;
            }
        }
        pacmanX = pacmanX + pacmanSpeed * pacmanDx;
        pacmanY = pacmanY + pacmanSpeed * pacmanDy;
    }
    private void drawPacman(Graphics2D g2d) {

        if (reqDx == -1) {
            g2d.drawImage(left, pacmanX + 1, pacmanY + 1, this);
        } else if (reqDx == 1) {
            g2d.drawImage(right, pacmanX + 1, pacmanY + 1, this);
        } else if (reqDy == -1) {
            g2d.drawImage(up, pacmanX + 1, pacmanY + 1, this);
        } else {
            g2d.drawImage(down, pacmanX + 1, pacmanY + 1, this);
        }
    }
    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    reqDx = -1;
                    reqDy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    reqDx = 1;
                    reqDy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    reqDx = 0;
                    reqDy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    reqDx = 0;
                    reqDy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
