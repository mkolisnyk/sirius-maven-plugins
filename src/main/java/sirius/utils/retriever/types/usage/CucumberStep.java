/**
 * 
 */
package sirius.utils.retriever.types.usage;

/**
 * @author Myk Kolisnyk
 *
 */
public class CucumberStep {
    /**
     * .
     */
    private String name;
    /**
     * .
     */
    private CucumberAggregatedDuration aggregatedDurations;
    /**
     * .
     */
    private CucumberStepDuration[] durations;
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the aggregatedDurations
     */
    public CucumberAggregatedDuration getAggregatedDurations() {
        return aggregatedDurations;
    }
    /**
     * @param aggregatedDurations the aggregatedDurations to set
     */
    public void setAggregatedDurations(
            CucumberAggregatedDuration aggregatedDurations) {
        this.aggregatedDurations = aggregatedDurations;
    }
    /**
     * @return the durations
     */
    public CucumberStepDuration[] getDurations() {
        return durations;
    }
    /**
     * @param durations the durations to set
     */
    public void setDurations(CucumberStepDuration[] durations) {
        this.durations = durations;
    }    
}
