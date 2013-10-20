/**
 * 
 */
package sirius.utils.retriever.interfaces;

import java.util.ArrayList;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHMilestone;

/**
 * @author Myk Kolisnyk
 * 
 */
public interface IStoryFormatter {
    public String GetFooter(ArrayList<GHIssue> issues);

    public String GetHeader(ArrayList<GHIssue> issues);

    public String GetIssue(GHIssue issue);

    public String GetLabels(GHIssue issue);

    public String GetMilestone(GHMilestone milestone);

    public void Out(String text);
}
