package edutrack.ui;

import edutrack.model.tag.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Tag}.
 */
public class TagCard extends UiPart<Region> {

    private static final String FXML = "TagListCard.fxml";

    public final Tag tag;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label tagName;

    /**
     * Creates a {@code TagCard} with the given {@code Tag} and index to display.
     */
    public TagCard(Tag tag, int displayedIndex) {
        super(FXML);
        this.tag = tag;
        id.setText(displayedIndex + ". ");
        tagName.setText(tag.tagName);
    }
}

