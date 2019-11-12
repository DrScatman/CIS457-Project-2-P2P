import java.util.ArrayList;

public class FileData {
    private String fileName;
    private String fileDescription;

    public FileData(String fileName, String fileDescription) {
        this.fileName = fileName;
        this.fileDescription = fileDescription;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    @Override
    public String toString() {

        return "Filename: " + fileName + " File Descriptions: " + fileDescription;
    }
}
