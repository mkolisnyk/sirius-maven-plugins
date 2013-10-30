/**
 * 
 */
package sirius.utils.retriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import sirius.utils.retriever.types.usage.CucumberStepSource;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

/**
 * @author Myk Kolisnyk
 * @goal cucumber-usage
 * @phase site
 */
public class CucumberUsageReportingPlugin extends AbstractMavenReport {

    /**
     * Directory where reports will go.
     * 
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    private String       outputDirectory;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    private Renderer     siteRenderer;

    /**
     * @parameter
     * @required
     * @readonly
     */
    private String       jsonUsageFile;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    @Override
    public String getDescription(Locale arg0) {
        return "HTML formatted Cucumber keywords usage report";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    @Override
    public String getName(Locale arg0) {
        return "Cucumber usage report";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    @Override
    public String getOutputName() {
        return "cucumber-usage-report";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    @Override
    protected String getOutputDirectory() {
        return this.outputDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    @Override
    protected MavenProject getProject() {
        return this.project;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    @Override
    protected Renderer getSiteRenderer() {
        return this.siteRenderer;
    }

    /**
     * @return the jsonUsageFile
     */
    public String getJsonUsageFile() {
        return jsonUsageFile;
    }

    /**
     * @param jsonUsageFile
     *            the jsonUsageFile to set
     */
    public void setJsonUsageFile(String jsonUsageFile) {
        this.jsonUsageFile = jsonUsageFile;
    }

    /**
     * @param outputDirectory
     *            the outputDirectory to set
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(MavenProject project) {
        this.project = project;
    }

    /**
     * @param siteRenderer
     *            the siteRenderer to set
     */
    public void setSiteRenderer(Renderer siteRenderer) {
        this.siteRenderer = siteRenderer;
    }

    public SortedMap calculateStepsUsageCounts(CucumberStepSource[] sources){
        SortedMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        for(CucumberStepSource source:sources){
            int stepsCount = source.getSteps().length;
            if(!map.containsKey(stepsCount)){
                map.put(stepsCount, 1);
            }
            else {
                int prevNum = map.get(stepsCount);
                prevNum++;
                map.remove(stepsCount);
                map.put(stepsCount, prevNum);
            }
        }
        return map;
    }
    
    public double calculateStepsUsageAverage(SortedMap<Integer,Integer> statistics){
        int totalSteps = 0;
        int totalUniqueSteps = 0;
        
        for(int i:statistics.keySet()){
            totalSteps += i*statistics.get(i);
            totalUniqueSteps += statistics.get(i);
        }
        if(totalUniqueSteps==0){
            totalUniqueSteps=1;
        }
        return (double)totalSteps/(double)totalUniqueSteps;
    }
    
    public int calculateStepsUsageMedian(SortedMap<Integer,Integer> statistics){
        int totalSteps = 0;
        int usedSteps = 0;
        int median = 0;
        for(int i:statistics.keySet()){
            totalSteps += statistics.get(i);
        }
        
        for(int i:statistics.keySet()){
            usedSteps += statistics.get(i);
            if(usedSteps*2 >= totalSteps){
                median=i;
                break;
            }
        }
        
        return median;
    }
    
    public int calculateStepsUsageMax(SortedMap<Integer,Integer> statistics){
        int max=0;
        for(int i:statistics.keySet()){
            max = Math.max(max, statistics.get(i));
        }
        return max;
    }
    
    /**
     * .
     * @param sink .
     * @param sources .
     */
    protected void generateUsageOverviewGraphReport(Sink sink, CucumberStepSource[] sources){
        double hscale;
        double vscale;
        int hoffset = 20;
        int voffset = 200;
        int median;
        double average;
        
        SortedMap<Integer,Integer> map = calculateStepsUsageCounts(sources);
        hscale = 180./(double)map.lastKey();
        vscale = 180./(double)calculateStepsUsageMax(map);
        median = calculateStepsUsageMedian(map);
        average = calculateStepsUsageAverage(map);
        
        String htmlContent = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"300\" height=\"300\">" +
        		"<defs>" +
        		"<filter id=\"f1\" x=\"0\" y=\"0\" width=\"200%\" height=\"200%\">" +
        		"<feOffset result=\"offOut\" in=\"SourceAlpha\" dx=\"10\" dy=\"10\" />" +
        		"<feGaussianBlur result=\"blurOut\" in=\"offOut\" stdDeviation=\"10\" />" +
        		"<feBlend in=\"SourceGraphic\" in2=\"blurOut\" mode=\"normal\" />" +
        		"</filter>" +
        		"<radialGradient id=\"grad1\" cx=\"0%\" cy=\"100%\" r=\"150%\" fx=\"0%\" fy=\"100%\">" +
        		"<stop offset=\"0%\" style=\"stop-color:white;stop-opacity:0.1\" />" +
        		"<stop offset=\"100%\" style=\"stop-color:silver;stop-opacity:0.7\" />" +
        		"</radialGradient>" +
        		"</defs>" +
        		"<rect width=\"90%\" height=\"90%\" stroke=\"black\" " +
        		  "stroke-width=\"1\" fill=\"url(#grad1)\" filter=\"url(#f1)\" />" +
        		"<line x1=\"20\" y1=\"10\" x2=\"20\" y2=\"200\" style=\"stroke:black;stroke-width:1\" />" +
        		"<line x1=\"20\" y1=\"200\" x2=\"230\" y2=\"200\" style=\"stroke:black;stroke-width:1\" />" +
        		"<polygon points=\"15,30 20,10 25,30\" style=\"fill:black;stroke:black;stroke-width:1\"/>" +
        		"<polygon points=\"230,200 210,205 210,195\" style=\"fill:black;stroke:black;stroke-width:1\"/>" +
        		"<polygon points=\"" + hoffset + "," + voffset;
        for(int i:map.keySet()){
            htmlContent += " " + (hoffset + (int)(i*hscale)) + "," + (voffset - (int)(map.get(i)*vscale));
        }
        htmlContent +=	" " + (hoffset + (int)(map.lastKey()*hscale)) + "," + voffset + 
                "\" style=\"fill:green;stroke:black;stroke-width:1\"/>" +
                "<line stroke-dasharray=\"10,10\" x1=\"" + (hoffset + median * hscale) + "\" y1=\"20\" x2=\"" + (hoffset + median * hscale) + "\" y2=\"200\" style=\"stroke:yellow;stroke-width:3\" />" +
                "<line stroke-dasharray=\"10,10\" x1=\"" + (hoffset + (int)(average * hscale)) + "\" y1=\"20\" x2=\"" + (hoffset + (int)(average * hscale)) + "\" y2=\"200\" style=\"stroke:red;stroke-width:3\" />" +
                "</svg>";
        sink.rawText(htmlContent);
    }
    
    /**
     * .
     * @param sink .
     * @param sources .
     */
    protected void generateUsageOverviewTableReport(Sink sink, CucumberStepSource[] sources){
        sink.table();
        sink.tableRow();
        sink.tableHeaderCell();
        sink.text("Expression");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Occurences");
        sink.tableHeaderCell_();
        sink.tableRow_();
        for(CucumberStepSource source:sources){
            sink.tableRow();
            sink.tableCell("80%");
            sink.text(source.getSource());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + source.getSteps().length);
            sink.tableCell_();
            sink.tableRow_();
        }
        sink.table_();
    }
    
    /**
     * .
     * @param sink .
     * @param sources .
     */
    protected void generateUsageDetailedReport(Sink sink, CucumberStepSource[] sources){
        ;
    }
    
    public CucumberStepSource[] getStepSources(String filePath) throws Exception{
        FileInputStream fis = null;
        JsonReader jr = null;
        Log logger = this.getLog();

        logger.debug("Looking for the file '" + filePath + "'");
        File file = new File(filePath);

        if (!(file.exists() && file.isFile())) {
            logger.error("The file '" + filePath
                    + "' either doesn't exist or not a file.");
            throw new FileNotFoundException();
        }

        fis = new FileInputStream(file);
        jr = new JsonReader(fis,true);
        JsonObject<String,Object> source = (JsonObject<String,Object>)jr.readObject();
        Object[] objs = (Object[])source.get("@items");
        
        CucumberStepSource[] sources = new CucumberStepSource[objs.length];
        for(int i=0;i<objs.length;i++){
            sources[i] = new CucumberStepSource((JsonObject<String,Object>)objs[i]);
        }
        jr.close();
        fis.close();
        return sources;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util
     * .Locale)
     */
    @Override
    protected void executeReport(Locale arg0) throws MavenReportException {
        try {
            
            CucumberStepSource[] sources = getStepSources(jsonUsageFile);
            
            Sink sink = getSink();
            sink.head();
            sink.title();
            sink.text("Cucumber Steps Usage Report");
            sink.title_();
            sink.head_();
         
            sink.body();
            sink.section1();
            sink.sectionTitle1();
            sink.text("Usage Statistics");
            sink.sectionTitle1_();
            
            sink.section2();
            sink.sectionTitle2();
            sink.text("Overview Graph");
            sink.sectionTitle2_();
            sink.paragraph();
            generateUsageOverviewGraphReport(sink,sources);
            sink.paragraph_();
            sink.section2_();
            
            sink.section2();
            sink.sectionTitle2();
            sink.text("Overview Table");
            sink.sectionTitle2_();
            sink.paragraph();
            generateUsageOverviewTableReport(sink,sources);
            sink.paragraph_();
            sink.section2_();

            sink.section1();
            sink.sectionTitle1();
            sink.text("Usage Detailed Information");
            sink.sectionTitle1_();
            sink.paragraph();
            generateUsageDetailedReport(sink,sources);
            sink.paragraph_();
            sink.section1_();
            sink.flush();
            sink.close();
            
        } catch (Exception e) {
            throw new MavenReportException(
                    "Error occured while generating Cucumber usage report", e);
        }
    }
}
