package com.example.gldb;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class HelloController {
    private final FileChooser fileChooser = new FileChooser();
    private File file;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField filePath;
    @FXML
    private TextArea output;

    @FXML
    private void initialize() {

        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

    }


    @FXML
    protected void onConvertButtonClick() throws FileNotFoundException {
        String path;
        path = file.getPath();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            StringBuilder result = new StringBuilder();
            result.append("INSERT INTO `")
                    .append(filePath.getText())
                    .append("` ")
                    .append("(");

            String[] columns = br.readLine().split(";");
            for (int i = 0; i < columns.length; i++) {
                result.append("`").append(columns[i]).append("`");
            }
            result.append(")")
                    .append(System.lineSeparator())
                    .append("VALUES")
                    .append(System.lineSeparator());
            while ((line = br.readLine()) != null) {

                // Hoffentlich keine Dates im Datensatz
                line = line.replace("'", "\"");
                line = line.replace(";", ",");

                result.append("(").append(line).append("),").append(System.lineSeparator());
                System.out.println(line);
            }
            output.setText(result.toString());
        } catch (Exception e) {
            System.out.println("File Not Found");
        }

    }

    @FXML
    protected void onFileChooseClick() {

        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);

    }
}