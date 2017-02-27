import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CardChooserPanel extends JPanel {

	// Class objects
    private CardImagePanel imagepanel;
    private JButton        chooserbtn;
    private JFileChooser   filechooser;

    // Constructor
    public CardChooserPanel() {
    	imagepanel  = new CardImagePanel();
        chooserbtn  = new JButton( "Clickable TBD" );
        filechooser = new JFileChooser();
        filechooser.setFileSelectionMode( JFileChooser.FILES_ONLY ); // TBD
        filechooser.setCurrentDirectory( new File( "images" ) );
        filechooser.setFileFilter( new FileNameExtensionFilter( "PNG Graphics Files", "png" ) );
        this.add( imagepanel );
        this.add( chooserbtn );
        chooserbtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ){
                int retval = filechooser.showOpenDialog( null );
                if( JFileChooser.APPROVE_OPTION == retval ){
                	File imageFile = filechooser.getSelectedFile();
                	//File imageFile   = new File( fileName );
                	try {
                		//chooserbtn.setVisible( false );
                        BufferedImage bi = ImageIO.read( imageFile );
                        imagepanel.setImage( bi );
                        CardChooserPanel.this.revalidate();

                    } catch (IOException ioe ) {
                    	// derp.
                    } 
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

class CardImagePanel extends JPanel {

	// Class object
    private BufferedImage img;

    // Constructor
    public CardImagePanel() {
        super( true );
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
}

class Tester {
    public static void main( String[] args ){
    	CardChooserPanel.buildGUI();
    }
}