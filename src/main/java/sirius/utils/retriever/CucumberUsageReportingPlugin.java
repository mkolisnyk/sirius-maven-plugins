/**
 * 
 */
package sirius.utils.retriever;

import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

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
    private String outputDirectory;
 
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
    private Renderer siteRenderer;
    
    /**
     * @parameter
     * @required
     * @readonly
     */
    private String jsonFile;
    
    /* (non-Javadoc)
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    @Override
    public String getDescription(Locale arg0) {
        return "HTML formatted Cucumber keywords usage report";
    }

    /* (non-Javadoc)
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    @Override
    public String getName(Locale arg0) {
        return "Cucumber usage report";
    }

    /* (non-Javadoc)
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    @Override
    public String getOutputName() {
        return "cucumber-usage-report";
    }

    /* (non-Javadoc)
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    @Override
    protected String getOutputDirectory() {
        return this.outputDirectory;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    @Override
    protected MavenProject getProject() {
        return this.project;
    }

    /* (non-Javadoc)
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
     * @param jsonFile the jsonFile to set
     */
    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    /**
     * @param outputDirectory the outputDirectory to set
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @param project the project to set
     */
    public void setProject(MavenProject project) {
        this.project = project;
    }

    /**
     * @param siteRenderer the siteRenderer to set
     */
    public void setSiteRenderer(Renderer siteRenderer) {
        this.siteRenderer = siteRenderer;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    @Override
    protected void executeReport(Locale arg0) throws MavenReportException {
        ;
    }
}
