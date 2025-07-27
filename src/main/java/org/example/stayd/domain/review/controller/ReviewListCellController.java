package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.example.stayd.domain.review.dto.ReviewListDTO;

import java.io.IOException;


/**
 * 리뷰 1건을 출력하는 셀 컨트롤러 (커스텀 셀)
 * - 사용자 ID, 리뷰 내용, 평점, 작성일 표시
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
    /**
     * 셀 내용을 업데이트하여 사용자 ID, 평점, 작성일, 내용을 출력
     *
     * @param review 리뷰 DTO
     * @param empty 셀이 비어 있는지 여부
     */
    @Override
    protected void updateItem(ReviewListDTO review, boolean empty) {
        super.updateItem(review, empty);

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
            setData(review);

            setText(null);
            setGraphic(root);
        }
    }
    /**
     * FXML 컴포넌트에 리뷰 데이터를 바인딩
     */
    public void setData(ReviewListDTO dto) {
        userIdLabel.setText(dto.getLoginId());
        contentLabel.setText(dto.getContent());
        ratingLabel.setText("★ " + dto.getRating());
        dateLabel.setText(dto.getCreatedAt().toLocalDate().toString());
    }
}