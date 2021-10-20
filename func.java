import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class func
{
    private final int earthRad = 6371;
       
    private double[][] track;
    private double[] sLine;
    private String file;
    
    private double maxDistance;
    private double avgDistance;
    private double score;
    
    double[] distance;
    
    public func(String file, double[][] sLine)
    {
        this.file = file;
        build(file);
        
        this.sLine = new double[2];
        double slope = (sLine[1][1]-sLine[0][1])/(sLine[1][0]-sLine[0][0]);
        this.sLine[0] = 0- slope;
        this.sLine[1] = -(sLine[0][1] - slope * sLine[0][0]);
        
        distance = new double[track.length];
        
        maxDistance = 0;
        avgDistance = 0;
        score = 0;
    }
    
    private void build(String url)
    {
       try
       {
            File inputFile = new File(url);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            
            NodeList nList = doc.getElementsByTagName("trkpt");
            track = new double[nList.getLength()][2];
            for(int i = 0; i < nList.getLength(); i++)
            {
                Node n = nList.item(i);
                Element e =  (Element) n;
               track[i][0] = Double.parseDouble(e.getAttribute("lat"));
               track[i][1] = Double.parseDouble(e.getAttribute("lon"));
            }
           
       }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
     public void calcualteValues()
    {
        findDistances();
        
        double total = 0;
        for(int i = 0;i< distance.length;i++)
        {
            total += distance[i];
            
            if(distance[i]> maxDistance)
            {
                maxDistance = distance[i];
            }
        }
        
        avgDistance = total/distance.length ;
   
        score = (total/distance.length) * maxDistance;
    }
     
    //using:
    //https://en.wikipedia.org/wiki/Haversine_formula, 
    //https://en.wikipedia.org/wiki/Great-circle_distance
    public void findDistances()
    {
        
        for(int i = 0; i < track.length; i++)
        {
            //conversions to radians for java Math. functions
            
            double xLine = closestXPoint(track[i][0], track[i][1]);
            
            double yLine = Math.toRadians(-(sLine[0] * xLine + sLine[1]));
            xLine = Math.toRadians(xLine);
            
            double xTrack = Math.toRadians(track[i][0]); 
            double yTrack = Math.toRadians(track[i][1]);
            
            double sinLat = Math.sin((xLine - xTrack)/2); //sin function of the latitude values
            double sinLon = Math.sin((yLine - yTrack)/2); //sin function of the longitude values
           
            distance[i] = 2 * earthRad * Math.asin(Math.sqrt(sinLat * sinLat + Math.cos(xLine)*Math.cos(xTrack)* sinLon * sinLon))*1000;
            // * 1000 to get M insted of Km
        }
    }
    
    //closest X/lat point on sLine to given lat/long
    private double closestXPoint(double x, double y)
    {
        double close = (x-sLine[0]*y - sLine[0] * sLine[1]) / (sLine[0]* sLine[0] +1);  
        
        return close;
    }
    
    public double getMax()
    {   
        if(maxDistance == 0)
        {
            calcualteValues();
        }
        return maxDistance;
    }
    
    public double getAvg()
    {
        if(avgDistance == 0)
        {
            calcualteValues();
        }
        return avgDistance;
    }
    
    public double getScore()
    {
        if(score == 0)
        {
            calcualteValues();
        }
        return score;
    }
        
}

