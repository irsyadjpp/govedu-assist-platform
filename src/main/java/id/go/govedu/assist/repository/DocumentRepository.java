package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByApplicationId(UUID applicationId);

    Optional<Document> findByApplicationIdAndDocType(UUID applicationId, Document.DocumentType docType);

    List<Document> findByApplicationIdAndIsVerifiedTrue(UUID applicationId);

    List<Document> findByIsVerifiedFalse();
}
