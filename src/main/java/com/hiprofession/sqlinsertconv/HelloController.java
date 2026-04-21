package com.hiprofession.sqlinsertconv;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;


public class HelloController {
    private final FileChooser fileChooser = new FileChooser();
    private File file;
    @FXML
    private Label welcomeText; // Not currently used, could be used for status messages
    @FXML
    private TextField filePath; // Used to display file path, and implicitly for table name
    @FXML
    private TextArea output;

    @FXML
    private void initialize() {
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    @FXML
    protected void onConvertButtonClick() {
        if (file == null) {
            output.setText("Bitte wählen Sie zuerst eine Datei aus.");
            return;
        }

        String tableName = getTableNameFromFile(file); // Derive table name from file name

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder result = new StringBuilder();
            result.append("INSERT INTO `").append(tableName).append("` (");

            String line;
            // Read header for columns
            if ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                for (int i = 0; i < columns.length; i++) {
                    result.append("`").append(columns[i].trim()).append("`");
                    if (i < columns.length - 1) {
                        result.append(",");
                    }
                }
                result.append(")").append(System.lineSeparator()).append("VALUES").append(System.lineSeparator());
            } else {
                output.setText("Die ausgewählte Datei ist leer oder konnte nicht gelesen werden.");
                return;
            }

            // Datenzeilen verarbeiten
            boolean firstValueRow = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (!firstValueRow) {
                    result.append(",").append(System.lineSeparator()); // Add comma before subsequent value sets
                }

                String[] values = line.split(";", -1); // -1 behält leere Felder am Ende
                result.append("(");
                for (int i = 0; i < values.length; i++) {
                    result.append(formatValue(values[i]));
                    if (i < values.length - 1) {
                        result.append(",");
                    }
                }
                result.append(")");
                firstValueRow = false;
            }

            if (!firstValueRow) { // Only append semicolon if there were values
                result.append(";");
            } else {
                output.setText("Keine Datenzeilen gefunden.");
                return;
            }

            output.setText(result.toString());

        } catch (FileNotFoundException e) {
            output.setText("Fehler: Datei nicht gefunden.");
        } catch (IOException e) {
            output.setText("Fehler beim Lesen der Datei: " + e.getMessage());
        } catch (Exception e) {
            output.setText("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    /**
     * Formatiert einen CSV-Wert für SQL basierend auf Heuristiken.
     */
    private String formatValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "NULL";
        }

        String trimmed = value.trim();

        if (trimmed.equalsIgnoreCase("NULL")) {
            return "NULL";
        }

        // Heuristik für Zahlen (Ganzzahlen oder Dezimalzahlen mit Punkt)
        if (trimmed.matches("-?\\d+(\\.\\d+)?")) {
            return trimmed;
        }

        // Standard: Als String behandeln und einfache Anführungszeichen escapen
        return "'" + trimmed.replace("'", "''") + "'";
    }

    @FXML
    protected void onFileChooseClick() {
        // Using null for the parent stage makes the dialog modal to the application.
        // !TODO For a more specific parent, you would pass the primary stage here.
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            this.file = selectedFile;
            filePath.setText(file.getAbsolutePath()); // Display the selected file path
            output.setText(""); // Clear previous output
        }
    }

    /**
     * Extracts the table name from the file name, removing the extension.
     * E.g., "my_data.csv" -> "my_data"
     *
     * @param file The selected file.
     * @return The derived table name.
     */
    private String getTableNameFromFile(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName; // Return full name if no extension or starts with dot
    }
}
