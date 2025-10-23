package edutrack.ui;

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
     * Updates the statistics display with current data.
     */
    private void updateStatsDisplay() {
        StringBuilder statsText = new StringBuilder();
        
        // Get total number of groups
        int totalGroups = logic.getAddressBook().getGroupList().size();
        
        statsText.append("=== TOTAL STATS ===\n");
        statsText.append("Total Groups: ").append(totalGroups).append("\n");
        
        statsMessage.setText(statsText.toString());
    }

}
