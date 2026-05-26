package id.go.govedu.assist.exception;

import java.util.Set;

public class IncompleteDocumentsException extends RuntimeException {

    private final String code = "INCOMPLETE_DOCUMENTS";
    private final Set<String> missingDocuments;

    public IncompleteDocumentsException(Set<String> missingDocuments) {
        super("Cannot submit application. Missing required documents: " + missingDocuments);
        this.missingDocuments = missingDocuments;
    }

    public String getCode() {
        return code;
    }

    public Set<String> getMissingDocuments() {
        return missingDocuments;
    }
}
