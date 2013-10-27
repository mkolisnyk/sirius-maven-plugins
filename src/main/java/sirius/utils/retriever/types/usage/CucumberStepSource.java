/**
 * 
 */
package sirius.utils.retriever.types.usage;

/**
 * @author Myk Kolisnyk
 *
 */
public class CucumberStepSource {
    /**
     * .
     */
    private String source;
    /**
     * .
     */
    private CucumberStep[] steps;
    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }
    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }
    /**
     * @return the steps
     */
    public CucumberStep[] getSteps() {
        return steps;
    }
    /**
     * @param steps the steps to set
     */
    public void setSteps(CucumberStep[] steps) {
        this.steps = steps;
    }
}
