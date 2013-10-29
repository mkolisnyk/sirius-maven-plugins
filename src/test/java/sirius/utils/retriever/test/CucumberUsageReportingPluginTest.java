/**
 * 
 */
package sirius.utils.retriever.test;

import static org.junit.Assert.*;

import java.util.SortedMap;
import java.util.TreeMap;

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

    @Test
    public void testCalculateStepsUsageCounts() throws Exception {
        SortedMap<Integer,Integer> expectedMap = new TreeMap<Integer,Integer>();
        expectedMap.put(1, 13);
        expectedMap.put(2, 3);
        expectedMap.put(3, 5);
        expectedMap.put(4, 2);
        expectedMap.put(5, 2);
        expectedMap.put(6, 1);

        CucumberUsageReportingPlugin plugin = new CucumberUsageReportingPlugin();
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        SortedMap<Integer,Integer> map = plugin.calculateStepsUsageCounts(sources);
        
        for(int key:expectedMap.keySet()){
            Assert.assertTrue("The " + key + " key wasn't found",map.containsKey(key));
            Assert.assertEquals(expectedMap.get(key), map.get(key));
        }
    }
}
