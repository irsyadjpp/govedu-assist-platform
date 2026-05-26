package id.go.govedu.assist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SftpFileService {

    private final DefaultSftpSessionFactory sftpSessionFactory;

    public String downloadReconciliationFile() {
        String fileName = "GOVEDU_RECON_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        String remotePath = "/inbound/" + fileName;
        String localPath = "/tmp/" + fileName;

        try (Session session = sftpSessionFactory.getSession()) {
            if (session.exists(remotePath)) {
                session.read(remotePath, new FileOutputStream(localPath));
                log.info("Downloaded reconciliation file: {}", fileName);
                return localPath;
            } else {
                log.warn("Reconciliation file not found on SFTP: {}", remotePath);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to download reconciliation file", e);
            return null;
        }
    }

    public void archiveFile(String remotePath) {
        String archivePath = "/archive/" + new File(remotePath).getName();

        try (Session session = sftpSessionFactory.getSession()) {
            session.rename(remotePath, archivePath);
            log.info("Archived file from {} to {}", remotePath, archivePath);
        } catch (Exception e) {
            log.error("Failed to archive file: {}", remotePath, e);
        }
    }

    private static class FileOutputStream extends java.io.FileOutputStream {
        public FileOutputStream(String path) throws java.io.FileNotFoundException {
            super(path);
        }
    }
}
