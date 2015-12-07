import javax.swing.ImageIcon;

public class Brick extends Sprite{

    private boolean destroyed;
    private int hits;
    private int score;

    public Brick(int x, int y){
        
        this.x = x;
        this.y = y;
        hits = 1;
        
        setImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);

        destroyed = false;
        score = 100;
    }

    public Brick(int x, int y, int hits){
        
        this.x = x;
        this.y = y;
        this.hits = hits;
        
        setImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);

        destroyed = false;
        score = 100 * hits;
    }
    
    public void setImage(){
    	
    	ImageIcon ii;
    	
    	switch(this.hits){
    	case 1: ii = new ImageIcon("./Sprites/brick1.png");
        		image = ii.getImage();
        		break;
    	case 2: ii = new ImageIcon("./Sprites/brick2.png");
				image = ii.getImage();
				break;
    	case 3: ii = new ImageIcon("./Sprites/brick3.png");
				image = ii.getImage();
				break;
    	}
    }
    
    public boolean isDestroyed(){
        
        return destroyed;
    }

    public void setDestroyed(boolean choice){
        
        destroyed = choice;
    }
    
    public int getHits(){
    	return hits;
    }
    
    public void isHit(){
    	hits = hits -1;
    }
    
    public int getScore(){
    	return score;
    }
}