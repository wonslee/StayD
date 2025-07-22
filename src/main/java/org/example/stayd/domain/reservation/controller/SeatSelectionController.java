package org.example.stayd.domain.reservation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SeatSelectionController implements Initializable {
    private static final int ROWS = 5;
    private static final int COLS = 4;

    // -------- FXML에서 주입 ----------
    @FXML private GridPane seatGrid;
    @FXML private Button    selectBtn;

    private final ToggleButton[][] seats = new ToggleButton[ROWS][COLS];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int seatNo = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                ToggleButton btn = new ToggleButton(String.valueOf(seatNo));
                // TODO: 상태 표시 - 예약중일 경우 붉은색, 이용가능일 경우 회색

                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);   // 빈 공간 채우기
                btn.getStyleClass().add("available");                 // 초기 회색
                btn.setUserData(seatNo);                              // seatId 저장
                btn.setOnAction(this::seatClicked);                   // 이벤트 공통 연결

                seatGrid.add(btn, col, row);
                GridPane.setFillHeight(btn, true);
                GridPane.setFillWidth(btn, true);

                seats[row][col] = btn;
                seatNo++;
            }
        }
    }

    /** 개별 좌석 클릭 */
    @FXML
    private void seatClicked(ActionEvent e) {
        ToggleButton btn = (ToggleButton) e.getSource();
        // TODO: 예외 처리 - 현재 예약된 좌석일 경우 '좌석 중복' 팝업 표시
        // TODO: 버튼 클릭시 - 초록색으로 색 변경. 해당 좌석 정보 가져와서 DB 저장할 준비
    }

    /** ‘선택’ 버튼 클릭 */
    @FXML
    private void selectClicked(ActionEvent e) {
        // TODO: DB상에 예약 데이터 저장 (스터디카페 예약 정보 모두 가져와야 함)
    }
}
