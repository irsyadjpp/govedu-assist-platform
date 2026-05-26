package id.go.govedu.assist.dto.ekyc;

public record DukcapilResponse(
    String status,
    String pesan,
    DukcapilData data
) {
    public record DukcapilData(
        String NIK,
        String NAMA_LGKP,
        String TMPT_LHR,
        String TGL_LHR
    ) {}
}
