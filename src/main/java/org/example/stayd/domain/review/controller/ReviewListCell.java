package org.example.stayd.domain.review.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.example.stayd.domain.review.dto.ReviewListDTO;

import java.io.IOException;
/**
 * 카페 상세 페이지에서 사용하는 리뷰 셀 클래스
 * - 리뷰 정보를 커스텀 셀 형식으로 출력
 */
public class ReviewListCell extends ListCell<ReviewListDTO> {
    /**
     * 셀의 내용을 업데이트
     * 비어 있지 않은 경우 FXML을 로드하여 내용 표시
     *
     * @param dto 리뷰 DTO
     * @param empty 셀이 비어 있는지 여부
     */
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