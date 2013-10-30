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
        System.out.println("Before getting data");
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        System.out.println("After getting data");
        
        Assert.assertTrue(sources.length > 0);
    }

    @Test
    public void testCalculateStepsUsageCounts() throws Exception {
        SortedMap<Integer,Integer> expectedMap = new TreeMap<Integer,Integer>();
        expectedMap.put(1, 13); // 13
        expectedMap.put(2, 3);  // 6
        expectedMap.put(3, 5);  // 15
        expectedMap.put(4, 2);  // 8
        expectedMap.put(5, 2);  // 10
        expectedMap.put(6, 1);  // 6

        CucumberUsageReportingPlugin plugin = new CucumberUsageReportingPlugin();
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        SortedMap<Integer,Integer> map = plugin.calculateStepsUsageCounts(sources);
        
        for(int key:expectedMap.keySet()){
            Assert.assertTrue("The " + key + " key wasn't found",map.containsKey(key));
            Assert.assertEquals(expectedMap.get(key), map.get(key));
        }
    }
    
    @Test
    public void testCalculateAverage() throws Exception {
        CucumberUsageReportingPlugin plugin = new CucumberUsageReportingPlugin();
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        SortedMap<Integer,Integer> map = plugin.calculateStepsUsageCounts(sources);
        double average = plugin.calculateStepsUsageAverage(map);
        Assert.assertEquals(58./26., average,0.01);
    }

    @Test
    public void testCalculateMedian() throws Exception {
        CucumberUsageReportingPlugin plugin = new CucumberUsageReportingPlugin();
        CucumberStepSource[] sources = plugin.getStepSources("src/test/resources/cucumber-usage.json");
        SortedMap<Integer,Integer> map = plugin.calculateStepsUsageCounts(sources);
        int median = plugin.calculateStepsUsageMedian(map);
        Assert.assertEquals(1, median);
    }
}
