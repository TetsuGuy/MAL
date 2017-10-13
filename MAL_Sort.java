package animeLinkSorter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
@SuppressWarnings("serial")
public class MAL_Sort extends JFrame{
    public static void main(String [] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	MAL_Sort 
    		fenster = new MAL_Sort();
	    	fenster.setSize(500, 100);
	    	fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	fenster.setTitle("AniSort - sort MyAnimelist Watchlists");
	    	fenster.setVisible(true);
	    	fenster.setResizable(false);
    	JButton 
    		text = new JButton("Program started. Choose html file to process your list...");
    		text.setFont(new Font("Arial",Font.BOLD, 15));
    		fenster.getContentPane().add(text, BorderLayout.CENTER);
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
                        	
                        	text.setText(line.subSequence(line.lastIndexOf("/")+1, line.length())+"  ("+score+")");
                        	
                        }
                        
                        if(inputLine.contains("/pics") && image == ""){
                        	String raw = in2.readLine();
                        	image= raw.substring(raw.indexOf("https://myanimelist.cdn-dena.com/images/"), raw.indexOf("alt")-2);
                        	
                        }
                    }
                	liste.add(new Entry(line, score, image));
                    System.out.println("Entry:"+score+" "+line+" "+" "+image);                  

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
}
