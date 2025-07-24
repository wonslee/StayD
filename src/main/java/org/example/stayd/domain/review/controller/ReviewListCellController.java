package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.example.stayd.domain.review.dto.ReviewListDTO;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * 리뷰 1건을 출력하는 셀 컨트롤러 (커스텀 셀)
 */
public class ReviewListCellController extends ListCell<ReviewListDTO> {

    @FXML
    private Label userIdLabel;   // 작성자 ID
    @FXML
    private Label contentLabel;  // 리뷰 내용
    @FXML
    private Label ratingLabel;   // 평점
    @FXML
    private Label dateLabel;     // 작성일

    @FXML
    private HBox root;

    private FXMLLoader loader;

    @Override
    protected void updateItem(ReviewListDTO review, boolean empty) {
        super.updateItem(review, empty);
        // updateItem 안에는 updateItem 로직만!

        if (empty || review == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewListCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 여기서 직접 바인딩하거나, 아래 setData(dto) 호출해도 됨
            setData(review);

            setText(null);
            setGraphic(root);
        }
    }

    // ✅ updateItem 바깥에 선언된 setData 메서드
    public void setData(ReviewListDTO dto) {
        userIdLabel.setText(dto.getLoginId());
        contentLabel.setText(dto.getContent());
        ratingLabel.setText("★ " + dto.getRating());
        dateLabel.setText(dto.getCreatedAt().toLocalDate().toString());
    }
}