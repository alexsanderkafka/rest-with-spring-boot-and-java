package kafka.system.RestApi.data.vo.v1;

import java.io.Serial;
import java.io.Serializable;

public class UploadFileResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String fileDownloandUri;
    private String fileType;
    private long fileSize;

    public UploadFileResponseVO() {
    }

    public UploadFileResponseVO(String fileName, String fileDownloandUri, String fileType, long fileSize) {
        this.fileName = fileName;
        this.fileDownloandUri = fileDownloandUri;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloandUri() {
        return fileDownloandUri;
    }

    public void setFileDownloandUri(String fileDownloandUri) {
        this.fileDownloandUri = fileDownloandUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
