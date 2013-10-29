/**
 * 
 */
package sirius.utils.retriever.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;

import sirius.utils.retriever.CucumberUsageReportingPlugin;
import sirius.utils.retriever.types.usage.CucumberStepSource;

/**
 * @author Myk Kolisnyk
 *
 */
public class CucumberUsageReportingPluginTest {

    /**
     * Test method for {@link sirius.utils.retriever.CucumberUsageReportingPlugin#getStepSources(java.lang.String)}.
     * @throws Exception 
     */
    @Test
    public void testGetStepSources() throws Exception {
        CucumberUsageReportingPlugin plugin = new CucumberUsageReportingPlugin();
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        
        Assert.assertTrue(sources.length > 0);
    }

}
