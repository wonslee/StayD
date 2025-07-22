package org.example.stayd.domain.reservation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.stayd.common.YesNoBooleanConverter;

@Data
@NoArgsConstructor
@Builder
public class Seat {
    @NotNull
    private Long seatId;
    @NotNull
    private Long cafeId;

    @NotBlank
    @Size(max = 25)
    private String seatNumber;

    /**
     * DB 컬럼 = CHAR(1) 'Y'/'N'
     */
    @NotNull
    private boolean isAvailable;

    private Instant createdAt;

    /* JDBC용 변환자 */
    public char getAvailableYn() {
        return YesNoBooleanConverter.toChar(isAvailable);
    }

    public void setAvailableYn(char c) {
        this.isAvailable = YesNoBooleanConverter.toBoolean(c);
    }

    // TODO: is_available NULL 값 들어올시 boolean(false)로 변환
    public Seat(Long seatId, Long cafeId, String seatNumber, boolean isAvailable, Instant createdAt) {
        this.seatId = seatId;
        this.cafeId = cafeId;
        this.seatNumber = seatNumber;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
    }
}
