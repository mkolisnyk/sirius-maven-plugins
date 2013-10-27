/**
 * 
 */
package sirius.utils.retriever.types.usage;

/**
 * @author Myk Kolisnyk
 *
 */
public class CucumberStepDuration {
    private Double duration;
    private String location;
    /**
     * @return the duration
     */
    public Double getDuration() {
        return duration;
    }
    /**
     * @param duration the duration to set
     */
    public void setDuration(Double duration) {
        this.duration = duration;
    }
    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }
    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
