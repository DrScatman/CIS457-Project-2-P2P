import java.util.ArrayList;

public class FileData {
    private String fileName;
    private ArrayList<String> fileDescription;

    public FileData(String fileName, ArrayList<String> fileDescription) {
        this.fileName = fileName;
        this.fileDescription = fileDescription;
    }

    public String getFileName() {
        return fileName;
    }

    public ArrayList<String> getFileDescription() {
        return fileDescription;
    }


    @Override
    public String toString() {
        StringBuilder fileDesc = new StringBuilder();
        for (String desc : fileDescription) {
            fileDesc.append(desc).append(" ");
        }

        return "Filename: " + fileName + " File Descriptions: " + fileDesc.toString();
    }
}
