package id.go.govedu.assist.service;

import id.go.govedu.assist.exception.IncompleteDocumentsException;
import id.go.govedu.assist.exception.StateTransitionDeniedException;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.Document;
import id.go.govedu.assist.model.DocumentType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ApplicationStateMachine {

    public enum Event {
        SUBMIT,
        CLAIM_FOR_REVIEW,
        APPROVE,
        REJECT
    }

    public Application.ApplicationStatus transition(Application.ApplicationStatus current, Event event) {
        return switch (current) {
            case DRAFT -> switch (event) {
                case SUBMIT -> Application.ApplicationStatus.SUBMITTED;
                default -> throw new StateTransitionDeniedException(current.name(), event.name());
            };
            case SUBMITTED -> switch (event) {
                case CLAIM_FOR_REVIEW -> Application.ApplicationStatus.IN_REVIEW;
                default -> throw new StateTransitionDeniedException(current.name(), event.name());
            };
            case IN_REVIEW -> switch (event) {
                case APPROVE -> Application.ApplicationStatus.APPROVED;
                case REJECT -> Application.ApplicationStatus.REJECTED;
                default -> throw new StateTransitionDeniedException(current.name(), event.name());
            };
            case APPROVED, DISBURSED, REJECTED -> throw new StateTransitionDeniedException(current.name(), event.name());
        };
    }

    public void validateSubmitGuard(Application application) {
        Set<String> missingDocuments = new HashSet<>();

        for (var docType : DocumentType.values()) {
            if (docType.isRequired()) {
                boolean hasVerifiedDoc = application.getDocuments().stream()
                        .anyMatch(doc -> doc.getDocType() == docType && doc.getIsVerified());

                if (!hasVerifiedDoc) {
                    missingDocuments.add(docType.name());
                }
            }
        }

        if (!missingDocuments.isEmpty()) {
            throw new IncompleteDocumentsException(missingDocuments);
        }
    }

    public boolean canTransition(Application.ApplicationStatus current, Event event) {
        return switch (current) {
            case DRAFT -> event == Event.SUBMIT;
            case SUBMITTED -> event == Event.CLAIM_FOR_REVIEW;
            case IN_REVIEW -> event == Event.APPROVE || event == Event.REJECT;
            case APPROVED, DISBURSED, REJECTED -> false;
        };
    }
}
