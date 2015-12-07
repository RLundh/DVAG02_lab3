import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class Board extends JPanel implements Common {

    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick bricks[];
    private boolean ingame = true;
    private int score;

    public Board() {

        initBoard();
    }

    private void initBoard() {

    	score = 0;
    	
        addKeyListener(new TAdapter());
        setFocusable(true);

        bricks = new Brick[N_OF_BRICKS];
        setDoubleBuffered(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), DELAY, PERIOD);
    }

    @Override
    public void addNotify() {

        super.addNotify();
        gameInit();
    }

    private void gameInit() {

        ball = new Ball();
        paddle = new Paddle();

        Brick testBrick = new Brick(0,0);
        testBrick.setDestroyed(true);
        int brickWidth = testBrick.getWidth();
        int brickHeight = testBrick.getHeight();
        
        int k = 0;
        int hits = 0;
        for (int i = 0; i < 5; i++) {
        	if(i == 0){
        		hits = 3;
        	}
        	if(i == 1 || i == 2){
        		hits = 2;
        	}
        	if(i == 3 || i == 4){
        		hits = 1;
        	}
        	
            for (int j = 0; j < 10; j++) {
                bricks[k] = new Brick(j * brickWidth, i * brickHeight + 50, hits);
                k++;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (ingame) {
            
            drawObjects(g2d);
        } else {

            gameFinished(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }
    
    private void drawObjects(Graphics2D g2d) {
        
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight(), this);
        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getWidth(), paddle.getHeight(), this);

        for (int i = 0; i < N_OF_BRICKS; i++) {
            if (!bricks[i].isDestroyed()) {
                g2d.drawImage(bricks[i].getImage(), bricks[i].getX(),
                        bricks[i].getY(), bricks[i].getWidth(),
                        bricks[i].getHeight(), this);
            }
        }
    }
    
    private void gameFinished(Graphics2D g2d) {

        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metrics = this.getFontMetrics(font);

        String finalScore = "Score: " + score;
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(message,
                (Common.WIDTH - metrics.stringWidth(message)) / 2,
                Common.WIDTH / 2);
        g2d.drawString(finalScore,
                (Common.WIDTH - metrics.stringWidth(finalScore)) / 2,
                Common.WIDTH / 2 + metrics.getHeight() + 10);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            ball.move();
            paddle.move();
            checkCollision();
            repaint();
        }
    }

    private void stopGame() {

        ingame = false;
        timer.cancel();
    }

    private void checkCollision() {

        if (ball.getRect().getMaxY() > Common.BOTTOM_EDGE) {
            stopGame();
        }

        for (int i = 0, j = 0; i < N_OF_BRICKS; i++) {
            
            if (bricks[i].isDestroyed()) {
                j++;
            }
            
            if (j == N_OF_BRICKS) {
                message = "Victory\n" + score;
                stopGame();
            }
        }

        if ((ball.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();
            int paddleOffset = paddle.getWidth() / 5;

            int first = paddleLPos + paddleOffset;
            int second = paddleLPos + paddleOffset * 2;
            int third = paddleLPos + paddleOffset * 3;
            int fourth = paddleLPos + paddleOffset * 4;

            if (ballLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
            }

            if (ballLPos >= first && ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }

        for (int i = 0; i < N_OF_BRICKS; i++) {
            
            if ((ball.getRect()).intersects(bricks[i].getRect())) {

                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();

                Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                Point pointLeft = new Point(ballLeft - 1, ballTop);
                Point pointTop = new Point(ballLeft, ballTop - 1);
                Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!bricks[i].isDestroyed()) {
                    if (bricks[i].getRect().contains(pointRight)) {
                        ball.setXDir(-1);
                    } else if (bricks[i].getRect().contains(pointLeft)) {
                        ball.setXDir(1);
                    }

                    if (bricks[i].getRect().contains(pointTop)) {
                        ball.setYDir(1);
                    } else if (bricks[i].getRect().contains(pointBottom)) {
                        ball.setYDir(-1);
                    }
                    
                    if(bricks[i].getHits() > 0){
                    	bricks[i].isHit();
                    	bricks[i].setImage();
                    	this.score = this.score + 50;
                    }
                    if(bricks[i].getHits() == 0){
                    	this.score = this.score + bricks[i].getScore();
                    	bricks[i].setDestroyed(true);
                    }
                }
            }
        }
    }
}