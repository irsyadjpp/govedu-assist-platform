package id.go.govedu.assist.dto.external;

import java.util.List;

public record BansosResponse(
    String status,
    BansosData data
) {
    public record BansosData(
        String nik,
        boolean isReceivingAid,
        List<ActiveProgram> activePrograms
    ) {
        public record ActiveProgram(
            String programName,
            String startDate,
            String status
        ) {}
    }
}
