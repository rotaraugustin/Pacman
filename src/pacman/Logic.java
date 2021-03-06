package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

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
            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void death() {
        lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < nBlocks * nBlocks && finished) {

            if ((screenData[i]&48) !=0) {
                finished = false;
            }

            i++;
        }

        if (finished==true) {

            score += 50;

            if (nGhosts < maxGhosts) {
                nGhosts++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }
void drawMaze(Graphics2D g2d)
{

    short i = 0;
    int x, y;

    for (y = 0; y < screenSize; y += blockSize) {
         for (x = 0; x < screenSize; x += blockSize) {

            g2d.setColor(new Color(28, 165, 37));
            g2d.setStroke(new BasicStroke(5));

            if ((levelData[i] == 0)) {
                g2d.fillRect(x, y, blockSize, blockSize);
            }

            if ((screenData[i] & 1) != 0) {
                g2d.drawLine(x, y, x, y + blockSize - 1);
            }

            if ((screenData[i] & 2) != 0) {
                g2d.drawLine(x, y, x + blockSize - 1, y);
            }

            if ((screenData[i] & 4) != 0) {
                g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1,
                        y + blockSize - 1);
            }

            if ((screenData[i] & 8) != 0) {
                g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1,
                        y + blockSize - 1);
            }

            if ((screenData[i] & 16) != 0) {
                g2d.setColor(new Color(241, 190, 63));
                g2d.fillOval(x + 10, y + 10, 6, 6);
            }

            i++;
        }
    }
}
    private void showIntroScreen(Graphics2D g2d) {

        String start = "Press SPACE to start";
        g2d.setColor(Color.orange);
        g2d.drawString(start, (screenSize)/4, 150);
    }
    private void drawScore(Graphics2D g) {
        g.setFont(font);
        g.setColor(new Color(26, 19, 219));
        String s = "Score: " + score;
        g.drawString(s, screenSize / 2 + 96, screenSize + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, screenSize + 1, this);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, dimension.width, dimension.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    private void initLevel() {
        int i;
        for (i = 0; i < nBlocks * nBlocks; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }
public void moveGhosts(Graphics2D g2d){
    int pos;
    int count;

    for (int i = 0; i <nGhosts; i++) {
        if (ghostX[i] % blockSize == 0 && ghostY[i] % blockSize == 0) {
            pos = ghostX[i] / blockSize + nBlocks * (int) (ghostY[i] / blockSize);

            count = 0;

            if ((screenData[pos] & 1) == 0 && ghostDx[i] != 1) {
                dx[count] = -1;
                dy[count] = 0;
                count++;
            }

            if ((screenData[pos] & 2) == 0 && ghostDy[i] != 1) {
                dx[count] = 0;
                dy[count] = -1;
                count++;
            }

            if ((screenData[pos] & 4) == 0 && ghostDx[i] != -1) {
                dx[count] = 1;
                dy[count] = 0;
                count++;
            }

            if ((screenData[pos] & 8) == 0 && ghostDy[i] != -1) {
                dx[count] = 0;
                dy[count] = 1;
                count++;
            }

            if (count == 0) {

                if ((screenData[pos] & 15) == 15) {
                    ghostDx[i] = 0;
                    ghostDy[i] = 0;
                } else {
                    ghostDx[i] = -ghostDx[i];
                    ghostDy[i] = -ghostDy[i];
                }

            } else {

                count = (int) (Math.random() * count);

                if (count > 3) {
                    count = 3;
                }

                ghostDx[i] = dx[count];
                ghostDy[i] = dy[count];
            }

        }

        ghostX[i] = ghostX[i] + (ghostDx[i] * ghostSpeed[i]);
        ghostY[i] = ghostY[i] + (ghostDy[i] * ghostSpeed[i]);
        drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);

        if (pacmanX> (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12)
                && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12)
                && inGame) {

            dying = true;
        }
    }
}

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
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
        up = new ImageIcon("src/pacman/images/up.gif").getImage();
        left = new ImageIcon("src/pacman/images/left.gif").getImage();
        right = new ImageIcon("src/pacman/images/right.gif").getImage();
        ghost = new ImageIcon("src/pacman/images/ghost.gif").getImage();
        heart = new ImageIcon("src/pacman/images/heart.png").getImage();
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
      repaint();
    }
}
