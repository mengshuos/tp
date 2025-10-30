package edutrack.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edutrack.commons.core.LogsCenter;
import edutrack.logic.Logic;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for a stats page
 */
public class StatsWindow extends UiPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(StatsWindow.class);
    private static final String FXML = "StatsWindow.fxml";

    @FXML
    private Label statsMessage;

    private final Logic logic;

    /**
     * Creates a new StatsWindow.
     *
     * @param root Stage to use as the root of the StatsWindow.
     * @param logic Logic to access data for statistics.
     */
    public StatsWindow(Stage root, Logic logic) {
        super(FXML, root);
        this.logic = logic;
        updateStatsDisplay();
    }

    /**
     * Creates a new StatsWindow.
     */
    public StatsWindow(Logic logic) {
        this(new Stage(), logic);
    }

    /**
     * Shows the stats window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing stats page about the students.");
        updateStatsDisplay(); // Refresh statistics before showing

        // Set minimum size (smaller than main window but same ratio)
        getRoot().setMinWidth(400);
        getRoot().setMinHeight(300);
        // Set maximum height to prevent window from elongating too much
        getRoot().setMaxHeight(1000);
        getRoot().setMaxWidth(600);

        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the stats window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the stats window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the stats window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Returns true if the stats window is currently iconified (minimized).
     */
    public boolean isIconified() {
        return getRoot().isIconified();
    }

    /**
     * Sets the iconified (minimized) state of the stats window.
     */
    public void setIconified(boolean iconified) {
        getRoot().setIconified(iconified);
    }

    /**
     * Updates the statistics display with current data.
     */
    private void updateStatsDisplay() {
        StringBuilder statsText = new StringBuilder();

        // Get all persons and groups
        var personList = logic.getAddressBook().getPersonList();
        int totalStudents = personList.size();

        // Collect all tags and count them
        Map<String, Integer> tagCounts = new HashMap<>();
        for (var person : personList) {
            for (var tag : person.getTags()) {
                String tagName = tag.tagName.toLowerCase(); // Case-insensitive counting
                tagCounts.put(tagName, tagCounts.getOrDefault(tagName, 0) + 1);
            }
        }

        // Debug: Log tag information
        logger.info("Found " + tagCounts.size() + " unique tags: " + tagCounts.keySet());

        // Sort tags alphabetically
        List<String> sortedTags = new ArrayList<>(tagCounts.keySet());
        Collections.sort(sortedTags);

        // Build the statistics text
        statsText.append("=== TOTAL STATS ===\n");
        statsText.append("Total Students: ").append(totalStudents).append("\n");
        statsText.append("Total Unique Tags in use: ").append(tagCounts.size()).append("\n");
        statsText.append("Tags in use:\n");

        if (sortedTags.isEmpty()) {
            statsText.append("  (No tags found)\n");
        } else {
            for (int i = 0; i < sortedTags.size(); i++) {
                String tagName = sortedTags.get(i);
                int count = tagCounts.get(tagName);
                statsText.append("  ")
                         .append(i + 1)
                         .append(". ")
                         .append(tagName)
                         .append(": ")
                         .append(count)
                         .append("\n");
            }
        }

        // Add group statistics
        statsText.append("\n=== GROUP STATS ===\n");
        var groupList = logic.getAddressBook().getGroupList();

        if (groupList.isEmpty()) {
            statsText.append("(No groups found)\n");
        } else {
            for (var group : groupList) {
                // Get students in this group
                List<edutrack.model.person.Person> studentsInGroup = new ArrayList<>();
                for (var person : personList) {
                    if (person.getGroups().contains(group)) {
                        studentsInGroup.add(person);
                    }
                }
                // Count tags for this group
                Map<String, Integer> groupTagCounts = new HashMap<>();
                for (var student : studentsInGroup) {
                    for (var tag : student.getTags()) {
                        String tagName = tag.tagName.toLowerCase();
                        groupTagCounts.put(tagName, groupTagCounts.getOrDefault(tagName, 0) + 1);
                    }
                }
                // Sort group tags alphabetically
                List<String> sortedGroupTags = new ArrayList<>(groupTagCounts.keySet());
                Collections.sort(sortedGroupTags);
                // Add group statistics
                statsText.append("\nGroup: ").append(group.groupName).append("\n");
                statsText.append("  Students: ").append(studentsInGroup.size()).append("\n");
                statsText.append("  Unique Tags: ").append(groupTagCounts.size()).append("\n");
                statsText.append("  Tags:\n");
                if (sortedGroupTags.isEmpty()) {
                    statsText.append("    (No tags found)\n");
                } else {
                    for (int i = 0; i < sortedGroupTags.size(); i++) {
                        String tagName = sortedGroupTags.get(i);
                        int count = groupTagCounts.get(tagName);
                        statsText.append("    ")
                                 .append(i + 1)
                                 .append(". ")
                                 .append(tagName)
                                 .append(": ")
                                 .append(count)
                                 .append("\n");
                    }
                }
            }
        }
        statsMessage.setText(statsText.toString());
    }

}
