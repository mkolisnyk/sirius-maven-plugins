/**
 * 
 */
package sirius.utils.retriever.types.usage;

/**
 * @author Myk Kolisnyk
 *
 */
public class CucumberAggregatedDuration {
    /**
     * .
     */
    private Double average;
    /**
     * .
     */
    private Double median;    
    /**
     * @return the average
     */
    public Double getAverage() {
        return average;
    }
    /**
     * @param average the average to set
     */
    public void setAverage(Double average) {
        this.average = average;
    }
    /**
     * @return the median
     */
    public Double getMedian() {
        return median;
    }
    /**
     * @param median the median to set
     */
    public void setMedian(Double median) {
        this.median = median;
    }
}
