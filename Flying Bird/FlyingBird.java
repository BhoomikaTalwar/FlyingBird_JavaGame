import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlyingBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 760;
    int boardHeight = 550;
    //images
    Image backgroundImg;
    Image birdImg;
    Image topStickImg;
    Image bottomStickImg;
    //bird class
    int birdX = boardWidth/4;
    int birdY = boardHeight/2;
    int birdWidth = 35;
    int birdHeight = 30;
    //stick class
    int stickX = boardWidth;
    int stickY = 0;
    int stickWidth = 55;  
    int stickHeight = 515;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    class Stick {
        int x = stickX;
        int y = stickY;
        int width = stickWidth;
        int height = stickHeight;
        Image img;
        boolean passed = false;   //to check if bird has passed the stick or not

        Stick(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -5; //move sticks to the left speed
    int velocityY = 0; //move bird up/down speed.
    int gravity = 1;

    ArrayList<Stick> sticks;   //to store sticks in a list
    Random random = new Random();

    Timer gameLoop;
    Timer placeStickTimer;
    boolean gameOver = false;
    double score = 0;

    FlyingBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);    //  
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./backgroundimg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird.png")).getImage();
        topStickImg = new ImageIcon(getClass().getResource("./stick1.png")).getImage();
        bottomStickImg = new ImageIcon(getClass().getResource("./stick2.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        sticks = new ArrayList<Stick>();

        //place sticks timer
        placeStickTimer = new Timer(1500, new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
              // Code to be executed
              placeSticks();
            }
        });
        placeStickTimer.start();
        
		//game timer
		gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames 1s=1000ms 
        gameLoop.start();                    //to continously draw frames 
	}
    
    void placeSticks() {
        int randomStickY = (int) (stickY - stickHeight/4 - Math.random()*(stickHeight/2));
        int openingSpace = boardHeight/4;
    
        Stick topStick = new Stick(topStickImg);
        topStick.y = randomStickY; 
        sticks.add(topStick);
    
        Stick bottomStick = new Stick(bottomStickImg);
        bottomStick.y = topStick.y  + stickHeight + openingSpace;
        sticks.add(bottomStick);
    }
    
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g); 
	}
    
	public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        //sticks
        for (int i = 0; i < sticks.size(); i++) {
            Stick stick = sticks.get(i);
            g.drawImage(stick.img, stick.x, stick.y, stick.width, stick.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Times New Roman", Font.BOLD,30));
        
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
            g.setFont(new Font("Times New Roman", Font.ITALIC,25));
            g.drawString("Press Space to Play Again!",10,61);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // limit the bird.y to top of the canvas

        //sticks
        for (int i = 0; i < sticks.size(); i++) {
            Stick stick = sticks.get(i);
            stick.x += velocityX;

            if (!stick.passed && bird.x > stick.x + stick.width) {
                score += 0.5;           //0.5 because there are 2 sticks,so 0.5*2 = 1, 1 for each set of pipes
                stick.passed = true;
            }

            if (collision(bird, stick)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Stick b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {   //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placeStickTimer.stop();    //stops adding more sticks r=to arraylist
            gameLoop.stop();           //stops repainting
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                sticks.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placeStickTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) throws Exception {
        int boardWidth = 760;
        int boardHeight = 550;
    
        JFrame frame = new JFrame("Flying Bird");
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlyingBird flyingBird = new FlyingBird();
        frame.add(flyingBird);
        frame.pack();
        flyingBird.requestFocus();
        frame.setVisible(true);
    }
}

