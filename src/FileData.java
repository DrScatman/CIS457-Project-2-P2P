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
}
