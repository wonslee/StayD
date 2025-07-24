package org.example.stayd.domain.review.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.example.stayd.domain.review.dto.ReviewListDTO;

import java.io.IOException;

public class ReviewListCell extends ListCell<ReviewListDTO> {

    @Override
    protected void updateItem(ReviewListDTO dto, boolean empty) {
        super.updateItem(dto, empty);

        if (empty || dto == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewListCell.fxml"));
                Parent root = loader.load();
                ReviewListCellController controller = loader.getController();
                controller.setData(dto);
                setGraphic(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}