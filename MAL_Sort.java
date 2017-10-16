package animeLinkSorter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class MAL_Sort extends JFrame{
    public static void main(String [] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	MAL_Sort 
    		fenster = new MAL_Sort();
	    	fenster.setSize(500, 450);
	    	fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	fenster.setTitle("AniSort - sort MyAnimelist Watchlists");
	    	fenster.setVisible(true);
	    	fenster.setResizable(false);
	    	fenster.setBackground(Color.white);
	    	fenster.getContentPane().setBackground(Color.white);
	    JPanel 
	    	toppanel = new JPanel();
	    	toppanel.setSize(fenster.getWidth(), 100);
	    	fenster.getContentPane().add(toppanel, BorderLayout.NORTH);
	   
	   JPanel 
	    	centerpanel = new JPanel();
	    	centerpanel.setSize(fenster.getWidth(), 200);
	    	BoxLayout grid = new BoxLayout(centerpanel,BoxLayout.Y_AXIS);
	    	centerpanel.setLayout(grid);
	    JLabel 
	    	textGenre = new JLabel("");
		    textGenre.setFont(new Font("Arial",Font.BOLD, 15));
		    textGenre.setAlignmentX(CENTER_ALIGNMENT);
		    textGenre.setHorizontalAlignment(textGenre.CENTER);	    
	    JLabel 
    		textScore = new JLabel("");
	    	textScore.setFont(new Font("Arial",Font.BOLD, 15));
	    	textScore.setAlignmentX(CENTER_ALIGNMENT);
	    	textScore.setHorizontalAlignment(textScore.CENTER);
    	JLabel 
    		text = new JLabel("Program started. Choose html file to process your list...");
    		text.setFont(new Font("Arial",Font.BOLD, 15));
    		toppanel.add(text);
    	
    	
    	
    	BufferedImage imagePic = new BufferedImage(500,400,BufferedImage.TYPE_3BYTE_BGR);
    	ImageIcon
    		imageIcon = new ImageIcon(imagePic);
    	JLabel label = new JLabel();
    		label.setIcon(imageIcon);
    		label.setAlignmentY(CENTER_ALIGNMENT);
    		label.setAlignmentX(CENTER_ALIGNMENT);
    		
    		centerpanel.add(textScore);centerpanel.add(label);centerpanel.add(textGenre);    	
    	fenster.getContentPane().add(centerpanel, BorderLayout.CENTER);	
    	
    	JProgressBar 
    		loader = new JProgressBar();  
	    	loader.setValue(0);
	        loader.setStringPainted(true);
        	fenster.getContentPane().add(loader, BorderLayout.SOUTH);   
        JFileChooser 
        	chooser = new JFileChooser();
        	chooser.showOpenDialog(null);
        while(chooser.getSelectedFile()==null){
        	chooser.showOpenDialog(null);
        }
        
        ArrayList<Entry> 
        	liste 	= new ArrayList<Entry>();
        String 
        	line 	= null;
        String
        	genreTag ="";
        
        try {        
            //Preprocess List, nr holds the count of links 
            BufferedReader preRead	= new BufferedReader(new FileReader(chooser.getSelectedFile()));            
            int nr=0;
            
            while((line = preRead.readLine()) != null) {
            	if(line.contains("<a href=\"https://myanimelist.net/anime/"))	nr++;
            } preRead.close();   

            //Process List
            FileReader fileReader 			= new FileReader(chooser.getSelectedFile());
            BufferedReader bufferedReader 	= new BufferedReader(fileReader);
            int done=0;            
                                
            while((line = bufferedReader.readLine()) != null) {
            	if(line.contains("<a href=\"https://myanimelist.net/anime/")){
            		done++;
            		line=(line.substring(line.indexOf("\"")+1, line.length()));
            		line=line.substring(0,line.indexOf("\""));
            		
            		//Get Score, Print Score then Println link!
                    BufferedReader in2 = new BufferedReader(new InputStreamReader(new URL(line).openStream()));
                    String inputLine = "";
                    String image = "";
                    Float score= null;
                    ArrayList<String> tags = new ArrayList<String>();
                    while ((inputLine = in2.readLine()) != null)
                    {
                        if(score==null && inputLine.contains("data-title=\"score\""))
                        {
                        	inputLine=in2.readLine();
                        	if(inputLine.contains("N/A"))
                        	{
                        		score=0F;
                        	}
                        	else
                        		score = Float.valueOf(inputLine);
                        	
                        	
                        }
                        
                        if(inputLine.contains("/pics") && image == ""){
                        	String raw = in2.readLine();
                        	image= raw.substring(raw.indexOf("https://myanimelist.cdn-dena.com/images/"), raw.indexOf("alt")-2);
                        	URL url = new URL(image);
                        	BufferedImage c = ImageIO.read(url);
                        	imageIcon = new ImageIcon(c);
                        	//fenster.setSize(c.getWidth(), c.getHeight());           	
                        }
                        
                        if(inputLine.contains(">Genres:<"))
                        {
                        	while(!inputLine.contains("</div>"))
                        	{
                        		inputLine=in2.readLine();
                        		//Find all tags
                        		if(inputLine.contains("title="))
                        		{
                        			parseTags(inputLine);
                        			String tag = inputLine.substring(inputLine.indexOf(">")+1,inputLine.indexOf("</a"));
                        			System.out.println("Tag found:"+tag+".");
                        			genreTag=tag;
                        			tags.add(tag);
                        		}
                        	}
                        }
                        
                    }
                	liste.add(new Entry(line, score, image,tags));
                    System.out.println("Entry:"+score+" "+line+" "+" "+image);
                    String title = ((String) line.subSequence(line.lastIndexOf("/")+1, line.length())).replace('_', ' ');
                    title=title.replace('%', ' ');
                	label.setIcon(imageIcon);
                	label.setHorizontalAlignment(SwingConstants.CENTER); 
                    text.setFont(new Font("Arial", Font.BOLD, 20));
                    text.setHorizontalAlignment(SwingConstants.CENTER);
                    text.setVerticalAlignment(SwingConstants.CENTER);
                    
                    text.setText(title);
                    textScore.setText("Score: "+score);
                    textGenre.setText(genreTag);
                    
                    in2.close();
            	} loader.setValue((int)100*done/nr);
            } bufferedReader.close();                  
            
            //Sort List
            Collections.sort(liste, new Comparator<Entry>() {public int compare(Entry a, Entry b){return  a.value.compareTo(b.value);}});     
                                       
        } 	catch(FileNotFoundException ex) {text.setText("Unable to open file '" +chooser.getSelectedFile().getName() + "'");}        
        	catch(IOException ex) 			{text.setText("Error reading file '"+ chooser.getSelectedFile().getName() + "'"); }       
        
        //Generate Output File
        File output = new File("./output.html"); 
        text.setText("Finished.Your File is ready at: "+output.getCanonicalPath());
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
	        writer.write("<html><head><title>MyList</title></head><body><table style=\"font-size:40;\">");
		    for(int i=liste.size()-1;i>=0;i--)
		    	writer.write("<tr><td>"+liste.get(i).value+"</td><td><a href=\""+liste.get(i).name+"\"><img src=\""+liste.get(i).image+"\"></img></a></td>"+"<td>"+liste.get(i).title+"</td></tr>"); 
		    writer.write("</table></body></html>");
		    writer.close();
		
		fenster.dispose();
    }
    
    private static void parseTags(String s){
    	//Until s is not empty, get every tag there is in there
    	//Until s contains no "title" anymore
    }
}
