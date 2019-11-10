public class FileData {
    private String fileName;
    private String[] fileDescription;

    public FileData(String fileName, String[] fileDescription) {
        this.fileName = fileName;
        this.fileDescription = fileDescription;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDescription() {
        StringBuilder fileDesc = new StringBuilder();
        for (String desc : fileDescription) {
            fileDesc.append(desc).append(" ");
        }
        return fileDesc.toString();
    }


    @Override
    public String toString() {
        return "Filename: " + fileName + " File Descriptions: " + getFileDescription();
    }
}
