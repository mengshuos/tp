package edutrack.ui;

import edutrack.model.group.Group;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Group}.
 */
public class GroupCard extends UiPart<Region> {

    private static final String FXML = "GroupListCard.fxml";

    public final Group group;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label groupName;

    /**
     * Creates a {@code GroupCard} with the given {@code Group} and index to display.
     */
    public GroupCard(Group group, int displayedIndex) {
        super(FXML);
        this.group = group;
        id.setText(displayedIndex + ". ");
        groupName.setText(group.groupName);
    }
}
