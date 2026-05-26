package id.go.govedu.assist.fraud.client;

import id.go.govedu.assist.dto.ekyc.DukcapilRequest;
import id.go.govedu.assist.dto.ekyc.DukcapilResponse;

public interface DukcapilClient {

    DukcapilResponse verifyIdentity(DukcapilRequest request);
}
