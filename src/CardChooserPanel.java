import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;

// Enums to support SuitFace
enum Suit { 
	SPADES("spades"), HEARTS("hearts"), 
    DIAMONDS("diamonds"), CLUBS("clubs");
    private final String text;
    Suit( String s ) {
    	this.text = s;
    }
    private String text(){
    	return this.text;
    }
}
enum Face { 
	ACE("ace"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"),
	SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"),
	JACK("jack"), QUEEN("queen"), KING("king");
    private final String text;
    Face( String s ) {
    	this.text = s;
    }
    private String text(){
    	return this.text;
    }
}
// Support class
class SuitFace {
	// Private member variables
	private Suit suit;
	private Face face;
	// Constructor
	public SuitFace( Suit s, Face f ){
		this.suit = s;
		this.face = f;
	}
	// Getter methods
	public Suit getSuit(){ return this.suit; }
    public Face getFace(){ return this.face; }
    // Support methods
    public String toString() {
    	return "TBD";
    }
    public boolean equals(Object o) {
        if ( o.getClass() != this.getClass() ) return false;
        if ( ((SuitFace)o).getSuit() != this.suit ) return false;
        if ( ((SuitFace)o).getFace() != this.face ) return false;
        return true;
    }
    public int hashCode() {
        return 13 * suit.ordinal() + face.ordinal();
    }
}

// Support class
class SuitFaceMap {
	// Private member variables
	private HashMap<SuitFace,BufferedImage> hMap;
	private String cardDirName = "images";
	private double imageScaleFactor = 0.30;
	// Constructor
	public SuitFaceMap(){
		hMap = new HashMap <SuitFace, BufferedImage>();
		loadCardImages();
	}
	// Private helper methods
	private void loadCardImages(){
		File dir = new File( cardDirName );
		File[] files = dir.listFiles();
		for( File f : files ){
            try {
                BufferedImage bi = ImageIO.read( f );
                bi = this.scale( bi, imageScaleFactor );
                String str = f.getName();
                String[] tokenized = str.split("_");
                Face face = this.textToFace( tokenized[0] );
                Suit suit = this.textToSuit( tokenized[2].split("\\.")[0] );
                this.putImage( new SuitFace( suit, face ), bi );
            } catch (IOException ioe ) {
                // derp.
            } 
		}
	}
	private SuitFace parseSuitFace( String s ){
		String fStr = s.split("_")[0];
		String sStr = s.split("_")[2].split("\\.")[0];
		return new SuitFace( this.textToSuit(sStr), this.textToFace(fStr) );
	}
    private Suit textToSuit( String s ){
        switch( s ){
            case "spades":   return Suit.SPADES;
            case "hearts":   return Suit.HEARTS;
            case "diamonds": return Suit.DIAMONDS;
            case "clubs":    return Suit.CLUBS;
            default:
                System.out.println( "textToSuit with arg " + s );
                return null;
        }
    }
    private Face textToFace( String s ){
        switch( s ){
            case "ace":   return Face.ACE;
            case "2":     return Face.TWO;
            case "3":     return Face.THREE;
            case "4":     return Face.FOUR;
            case "5":     return Face.FIVE;
            case "6":     return Face.SIX;
            case "7":     return Face.SEVEN;
            case "8":     return Face.EIGHT;
            case "9":     return Face.NINE;
            case "10":    return Face.TEN;
            case "jack":  return Face.JACK;
            case "queen": return Face.QUEEN;
            case "king":  return Face.KING;
            default: 
                System.out.println( "textToSuit with arg " + s );
                return null;
        }
    }
	private void putImage( SuitFace sf, BufferedImage img ){
		this.hMap.put( sf,img );
	}
    // Public methods
	public BufferedImage getImage( SuitFace sf ){
		return hMap.get( sf );
	}
	public BufferedImage scale( BufferedImage img, double factor ){
		int width  = (int)(img.getWidth()*factor);
		int height = (int)(img.getHeight()*factor);
		int type   = img.getType();
		if( 0 == type )  type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage resized = new BufferedImage( width, height, type );
		Graphics2D g = resized.createGraphics();
		g.drawImage( img, 0, 0, width, height, null );
		g.dispose();
		return resized;
	}
}

class CardChooserControl extends JPanel {
	private JComboBox<Suit> suitList;
	private JComboBox<Face> faceList;
	private SuitFace savedValue;

	public CardChooserControl(){
		super( true );
        suitList = new JComboBox<Suit>( Suit.values() );
        faceList = new JComboBox<Face>( Face.values() );
        this.add( faceList );
        this.add( suitList );
	}

	public SuitFace getDisplayedCard(){
		Suit s = (Suit)suitList.getSelectedItem();
		Face f = (Face)faceList.getSelectedItem();
		return new SuitFace( s, f );
	}
	public SuitFace getSavedCard(){
		return this.savedValue;
	}
	public void saveDisplayedCard() {
		savedValue = getDisplayedCard();
	}
	public void restoreDisplayedCard(){
		suitList.setSelectedItem( savedValue.getSuit() );
		suitList.setSelectedItem( savedValue.getFace() );
	}
}

// Helper class including listeners


class CardImagePanel extends JPanel {
	// Class objects
    private BufferedImage img;
    private CardMover cm;

    // Constructor
    public CardImagePanel() {
        super( true );
        img = null;
        cm = new CardMover();
        addMouseListener( cm );
        addMouseMotionListener( cm );
    }

    // Public methods
    public void setImage( BufferedImage m ) {
        this.img = m;
    }
    public void paintComponent( Graphics g ) {
        if( this.img != null ){
            g.drawImage( this.img, 0, 0, null );
        }
    }
    public Dimension getPreferredSize() {
        if( null != this.img ) {
            return new Dimension( this.img.getWidth(), this.img.getHeight() );
        }
        else {
        	return new Dimension( 0,0 );
        }
    }
    // nested class for event listener
    class CardMover extends MouseAdapter{
        // Private member variables
        private int startx;
        private int starty;
        // Event Listeners
        public void mousePressed( MouseEvent e ){
            this.startx = e.getX();
            this.starty = e.getY();
        }
        // This is moving the card somewhat satisfactorily,
        // but only about 1/2 as far as it should
        public void mouseDragged( MouseEvent e ){
            int deltaX = e.getX() - startx;
            int deltaY = e.getY() - starty;
            this.startx = e.getX();
            this.starty = e.getY();
            CardImagePanel cip = CardImagePanel.this;
            Dimension size = cip.getPreferredSize();
            cip.setBounds( cip.getX() + deltaX, 
                           cip.getY() + deltaY, 
                           size.width, size.height );
        }
    }
}

// Public class
public class CardChooserPanel extends JPanel {

	// Class objects
    private CardImagePanel     imagepanel;
    private CardChooserControl chooserControl;
    private JButton            chooserbtn;
    private JFileChooser       filechooser;
    private SuitFaceMap        sfMap;

    // Constructor
    public CardChooserPanel() {
        this.setLayout( null );
    	imagepanel     = new CardImagePanel();
        chooserControl = new CardChooserControl();
        chooserbtn     = new JButton( "Clickable TBD" );
        sfMap          = new SuitFaceMap();
        this.add( imagepanel );
        this.add( chooserControl );
        this.add( chooserbtn );

        Insets insets =  this.getInsets();
        Dimension size = imagepanel.getPreferredSize();
        imagepanel.setBounds( 20+insets.left, 10+insets.top, size.width, size.height );
        size = chooserControl.getPreferredSize();
        chooserControl.setBounds( 150+insets.left, 350+insets.top, size.width, size.height );
        size = chooserbtn.getPreferredSize();
        chooserbtn.setBounds( 200+insets.left, 400+insets.top, size.width, size.height );
        revalidate();
        repaint();

        // v2 button listener.  still good for v3.
        chooserbtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ){
                try {
                    SuitFace sf = chooserControl.getDisplayedCard();
                    BufferedImage bi = sfMap.getImage( sf );
                    imagepanel.setImage( bi );
                    Dimension size = imagepanel.getPreferredSize();
                    imagepanel.setSize( size.width, size.height );
                    CardChooserPanel.this.revalidate();
                    CardChooserPanel.this.repaint();
                } catch ( Exception ex ) {
                    System.out.println( "Exception." ); 
                }
            }
        } );
    }

    // Public method
    public static void buildGUI() {
        JFrame jf = new JFrame( "Card Chooser" );
        CardChooserPanel ccp = new CardChooserPanel();
        jf.setContentPane( ccp );
        jf.setSize( 600, 480 );
        jf.setVisible( true );
    }
}

// Test driver
class Tester {
    public static void main( String[] args ){
    	CardChooserPanel.buildGUI();
    }
}