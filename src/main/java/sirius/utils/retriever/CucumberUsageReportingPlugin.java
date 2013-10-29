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
 * 
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
    private String       jsonFile;

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
     * @return the jsonFile
     */
    public String getJsonFile() {
        return jsonFile;
    }

    /**
     * @param jsonFile
     *            the jsonFile to set
     */
    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
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

    /**
     * .
     * @param sink .
     * @param sources .
     */
    protected void generateUsageOverviewGraphReport(Sink sink, CucumberStepSource[] sources){
        ;
    }
    
    /**
     * .
     * @param sink .
     * @param sources .
     */
    protected void generateUsageOverviewTableReport(Sink sink, CucumberStepSource[] sources){
        ;
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
            
            CucumberStepSource[] sources = getStepSources(jsonFile);
            
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
            
        } catch (Exception e) {
            throw new MavenReportException(
                    "Error occured while generating Cucumber usage report", e);
        }
    }
}
