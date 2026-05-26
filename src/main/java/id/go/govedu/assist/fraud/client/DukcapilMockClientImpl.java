package id.go.govedu.assist.fraud.client;

import id.go.govedu.assist.dto.ekyc.DukcapilRequest;
import id.go.govedu.assist.dto.ekyc.DukcapilResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class DukcapilMockClientImpl implements DukcapilClient {

    @Override
    public DukcapilResponse verifyIdentity(DukcapilRequest request) {
        var nik = request.NIK();

        // Scenario 1: System error - NIK ends with 0000000000000000
        if (nik.equals("0000000000000000")) {
            return new DukcapilResponse(
                    "9999",
                    "Gagal terhubung ke database kependudukan pusat",
                    null
            );
        }

        // Scenario 2: Data mismatch - NIK ends with 9999
        if (nik.endsWith("9999")) {
            return new DukcapilResponse(
                    "0000",
                    "Sukses",
                    new DukcapilResponse.DukcapilData(
                            "Sesuai",
                            "Tidak Sesuai",
                            "Sesuai",
                            "Tidak Sesuai"
                    )
            );
        }

        // Scenario 3: Success - NIK ends with even number or 0001
        var lastDigit = Integer.parseInt(nik.substring(nik.length() - 1));
        if (lastDigit % 2 == 0 || nik.endsWith("0001")) {
            return new DukcapilResponse(
                    "0000",
                    "Sukses",
                    new DukcapilResponse.DukcapilData(
                            "Sesuai",
                            "Sesuai",
                            "Sesuai",
                            "Sesuai"
                    )
            );
        }

        // Default: partial match for odd numbers (not 9999)
        return new DukcapilResponse(
                "0000",
                "Sukses",
                new DukcapilResponse.DukcapilData(
                        "Sesuai",
                        "Tidak Sesuai",
                        "Sesuai",
                        "Sesuai"
                )
        );
    }
}
