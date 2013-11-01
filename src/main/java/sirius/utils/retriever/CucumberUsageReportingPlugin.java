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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import sirius.utils.retriever.types.usage.CucumberStep;
import sirius.utils.retriever.types.usage.CucumberStepDuration;
import sirius.utils.retriever.types.usage.CucumberStepSource;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

/**
 * Generates HTML report based on Cucumber usage page. The input is the result of the site:cucumber 
 * goal which is the part of this plugin
 * @author Myk Kolisnyk
 * @goal cucumber-usage
 * @phase site
 */
public class CucumberUsageReportingPlugin extends AbstractMavenReport {

    /**
     * The path to the JSON usage file which is an input for the report generation
     * @parameter expression="${project.reporting.jsonUsageFile}"
     * @required
     */
    private String       jsonUsageFile;

    /**
     * The directory the output should be produced to.
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
    
    public int calculateTotalSteps(SortedMap<Integer,Integer> statistics){
        int totalSteps = 0;

        for(int i:statistics.keySet()){
            totalSteps += i*statistics.get(i);
        }
        return totalSteps;
    }
 
    public int calculateUsedSteps(SortedMap<Integer,Integer> statistics){
        int usedSteps = 0;
        
        for(int i:statistics.keySet()){
            usedSteps += statistics.get(i);
        }
        
        return usedSteps;
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
        int hsize = 400;
        int vsize = 400;
        int hstart = 40;
        int vstart = 30;
        int hend = 350;
        int vend = 300;
        
        int hstep = 0;
        int vstep=0;
        
        int median;
        double average;
        
        SortedMap<Integer,Integer> map = calculateStepsUsageCounts(sources);
        hscale = (double)(hend-2*hstart)/((double)map.lastKey()+1);
        vscale = (double)(vend-2*vstart)/((double)calculateStepsUsageMax(map)+1);
        
        hstep = (int)(30./hscale)+1;
        vstep = (int)(30./vscale)+1;
        
        median = calculateStepsUsageMedian(map);
        average = calculateStepsUsageAverage(map);
        
        String htmlContent = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + (hsize+100) + "\" height=\"" + vsize + "\">" +
        		"<defs>" +
        		"<filter id=\"f1\" x=\"0\" y=\"0\" width=\"200%\" height=\"200%\">" +
        		"<feOffset result=\"offOut\" in=\"SourceAlpha\" dx=\"10\" dy=\"10\" />" +
        		"<feGaussianBlur result=\"blurOut\" in=\"offOut\" stdDeviation=\"10\" />" +
        		"<feBlend in=\"SourceGraphic\" in2=\"blurOut\" mode=\"normal\" />" +
        		"</filter>" +
        		"<radialGradient id=\"grad1\" cx=\"0%\" cy=\"100%\" r=\"150%\" fx=\"0%\" fy=\"100%\">" +
        		"<stop offset=\"0%\" style=\"stop-color:white;stop-opacity:0.1\" />" +
        		"<stop offset=\"100%\" style=\"stop-color:gold;stop-opacity:0.7\" />" +
        		"</radialGradient>" +
        	    "<linearGradient id=\"grad2\" cx=\"0%\" cy=\"100%\" r=\"150%\" fx=\"0%\" fy=\"100%\">" +
                "<stop offset=\"0%\" style=\"stop-color:red;stop-opacity:0.7\" />" +
                "<stop offset=\"50%\" style=\"stop-color:yellow;stop-opacity:0.7\" />" +
                "<stop offset=\"100%\" style=\"stop-color:green;stop-opacity:0.7\" />" +
                "</linearGradient>" +
        		"</defs>" +
        		"<rect width=\"90%\" height=\"90%\" stroke=\"black\" " +
        		  "stroke-width=\"1\" fill=\"url(#grad1)\" filter=\"url(#f1)\" />" +
        		"<line x1=\"" + (hstart) + "\" y1=\"" + (vstart) + "\" x2=\"" + (hstart) + "\" y2=\"" + (vend) + "\" style=\"stroke:black;stroke-width:1\" />" +
        		"<line x1=\"" + (hstart) + "\" y1=\"" + (vend) + "\" x2=\"" + (hend) + "\" y2=\"" + (vend) + "\" style=\"stroke:black;stroke-width:1\" />" +
        		"<polygon points=\""+ (hstart-5) +"," + (vstart+20) + " "+ (hstart) +"," + (vstart) + " "+ (hstart+5) +"," + (vstart+20) + "\" style=\"fill:black;stroke:black;stroke-width:1\"/>" +
        		"<polygon points=\""+(hend)+"," + (vend) + " "+(hend-20)+"," + (vend+5) + " "+(hend-20)+"," + (vend-5) + "\" style=\"fill:black;stroke:black;stroke-width:1\"/>" +
        		"<polygon points=\"" + hstart + "," + vend;
        for(int i=0;i<=map.lastKey()+1;i++){
            int value = 0;
            if(map.containsKey(i)){
                value = map.get(i);
            }
            htmlContent += " " + (hstart + (int)(i*hscale)) + "," + (vend - (int)(value*vscale));
        }
        htmlContent +=  "\" style=\"stroke:black;stroke-width:1\"  fill=\"url(#grad2)\" />";
        
        for(int i=0;i<=map.lastKey();i+=hstep){
            htmlContent += "<line x1=\"" + (hstart + (int)(i*hscale)) + "\" y1=\""+ (vend)+ "\" x2=\"" + (hstart + (int)(i*hscale)) + "\" y2=\""+ (vend+5)+ "\" style=\"stroke:black;stroke-width:1\" />" +
                    "<text x=\"" + (hstart + (int)(i*hscale)) + "\" y=\""+ (vend+10)+ "\" font-size = \"8\">" + i + "</text>";    
        }
        
        for(int i=0;i<=calculateStepsUsageMax(map);i+=vstep){
            htmlContent += "<line x1=\"" + (hstart) + "\" y1=\""+ (vend-(int)(i*vscale))+ "\" x2=\"" + (hstart - 5) + "\" y2=\""+ (vend-(int)(i*vscale))+ "\" style=\"stroke:black;stroke-width:1\" />" +
                    "<text x=\"" + (hstart - 5) + "\" y=\""+ (vend-(int)(i*vscale))+ "\" transform=\"rotate(-90 " + (hstart - 5) + ","+ (vend-(int)(i*vscale))+ ")\" font-size = \"8\">" + i + "</text>";    
        }
                
        float usage = 100.f * (1.f - ((float)calculateUsedSteps(map)/ (float)calculateTotalSteps(map)));
        String statusColor = "silver";
        
        if(usage <= 30.f){
            statusColor = "red";
        }
        else if(usage >= 70){
            statusColor = "green";
        }
        else {
            statusColor = "#BBBB00";
        }
        
        htmlContent += "<line stroke-dasharray=\"10,10\" x1=\"" + (hstart + median * hscale) + "\" y1=\"" + (vstart) + "\" x2=\"" + (hstart + median * hscale) + "\" y2=\"" + vend + "\" style=\"stroke:yellow;stroke-width:3\" />" +
                "<line stroke-dasharray=\"10,10\" x1=\"" + (hstart + (int)(average * hscale)) + "\" y1=\"" + (vstart) + "\" x2=\"" + (hstart + (int)(average * hscale)) + "\" y2=\"" + vend + "\" style=\"stroke:red;stroke-width:3\" />" +
                "<rect x=\"60%\" y=\"20%\" width=\"28%\" height=\"20%\" stroke=\"black\" stroke-width=\"1\" fill=\"white\" filter=\"url(#f1)\" />" +
                "<line x1=\"63%\" y1=\"29%\" x2=\"68%\" y2=\"29%\" stroke-dasharray=\"5,5\" style=\"stroke:red;stroke-width:3\" /><text x=\"73%\" y=\"30%\" font-weight = \"bold\" font-size = \"12\">Average</text>" +
                "<line x1=\"63%\" y1=\"34%\" x2=\"68%\" y2=\"34%\" stroke-dasharray=\"5,5\" style=\"stroke:yellow;stroke-width:3\" /><text x=\"73%\" y=\"35%\" font-weight = \"bold\" font-size = \"12\">Median</text>" +
                "<text x=\"60%\" y=\"55%\" font-weight = \"bold\" font-size = \"40\" fill=\"" + statusColor + "\">" + String.format("%.1f", usage)+ "%</text>" +
                "<text x=\"66%\" y=\"60%\" font-weight = \"bold\" font-size = \"16\" fill=\"" + statusColor + "\">Re-use</text>" +
                "<text x=\"120\" y=\"330\" font-weight = \"bold\" font-size = \"14\" >Step re-use count</text>" +
                "<text x=\"20\" y=\"220\" font-weight = \"bold\" font-size = \"14\" transform=\"rotate(-90 20,220)\">Steps count</text>" +
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
        for(CucumberStepSource source:sources){
            sink.section3();
            sink.sectionTitle3();
            sink.text(source.getSource());
            sink.sectionTitle3_();
            sink.section3_();

            sink.table();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text("Step Name");
            sink.tableHeaderCell_();
            sink.tableHeaderCell();
            sink.text("Duration");
            sink.tableHeaderCell_();
            sink.tableHeaderCell();
            sink.text("Location");
            sink.tableHeaderCell_();
            sink.tableRow_();
            
            for(CucumberStep step:source.getSteps()){
                sink.tableRow();
                sink.tableCell();
                sink.text(step.getName());
                sink.tableCell_();
                sink.tableCell();
                //sink.text("" + step.getAggregatedDurations().getAverage());
                sink.text("-");
                sink.tableCell_();
                sink.tableCell();
                //sink.text("" + step.getAggregatedDurations().getMedian());
                sink.text("-");
                sink.tableCell_();
                sink.tableRow_();
                for(CucumberStepDuration duration:step.getDurations()){
                    sink.tableRow();
                    sink.tableCell();
                    sink.text("");
                    sink.tableCell_();
                    sink.tableCell();
                    sink.text("" + duration.getDuration());
                    sink.tableCell_();
                    sink.tableCell();
                    sink.text(duration.getLocation());
                    sink.tableCell_();
                    sink.tableRow_();
                }
            }
            sink.table_();
        }

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
            sink.text("Cucumber Usage Statistics");
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
            sink.text("Cucumber Usage Detailed Information");
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
