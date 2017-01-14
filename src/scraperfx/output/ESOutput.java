/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scraperfx.ScraperFX;
import scraperfx.settings.Game;
import scraperfx.settings.Image;

/**
 *
 * @author goodjaerb
 */
public class ESOutput {
    
    public enum ESImageTag {
        IMAGE("image", Image.ImageType.BOX_FRONT, Image.ImageType.SCREENSHOT, Image.ImageType.TITLE, Image.ImageType.DECAL),
        BG_IMAGE("bgImage", Image.ImageType.SCREENSHOT, Image.ImageType.FANART, Image.ImageType.GAME, Image.ImageType.TITLE),
        //LOGO("logo", Image.ImageType.LOGO, Image.ImageType.DECAL),
        //BG_LOGO("bgLogo", Image.ImageType.LOGO, Image.ImageType.DECAL),
        //BOX_FRONT("boxart-front", Image.ImageType.BOX_FRONT, Image.ImageType.TITLE),
        //BOX_BACK("boxart-back", Image.ImageType.BOX_BACK),
        SCREENSHOT("screenshot", Image.ImageType.SCREENSHOT, null, Image.ImageType.GAME, null),
        //FANART("fanart", Image.ImageType.FANART)
        ;
        
        private final String tag;
        private final Image.ImageType defaultConsoleType;
        private final Image.ImageType secondaryConsoleType;
        private final Image.ImageType defaultArcadeType;
        private final Image.ImageType secondaryArcadeType;
        
        ESImageTag(String s) {
            this(s, null, null);
        }
        
        ESImageTag(String s, Image.ImageType consoleDefault, Image.ImageType consoleSecondary) {
            this(s, consoleDefault, consoleSecondary, null, null);
        }
        
        ESImageTag(String s, Image.ImageType consoleDefault, Image.ImageType consoleSecondary, Image.ImageType arcadeDefault, Image.ImageType arcadeSecondary) {
            this.tag = s;
            this.defaultConsoleType = consoleDefault;
            this.secondaryConsoleType = consoleSecondary;
            this.defaultArcadeType = arcadeDefault;
            this.secondaryArcadeType = arcadeSecondary;
        }
        
        public Image.ImageType getConsoleDefault() {
            return defaultConsoleType;
        }
        
        public Image.ImageType getConsoleSecondary() {
            return secondaryConsoleType;
        }
        
        public Image.ImageType getArcadeDefault() {
            return defaultArcadeType;
        }
        
        public Image.ImageType getArcadeSecondary() {
            return secondaryArcadeType;
        }
        
        public String getTag() {
            return tag;
        }
    }
    
    public ESOutput() {
        
    }
    
    public void output(List<Game> games, String outputPathStr, String imagesPathStr, boolean arcade) {
        OutputDialog d = new OutputDialog(games, outputPathStr, imagesPathStr, arcade);
        d.showAndWait();
    }
   
    private class OutputDialog extends Stage {
        
        private final List<String> esTags;
        private final List<CheckBox> enableTagCheckBoxes;
        private final List<ComboBox<String>> primaryMetaDataTypes;
        private final List<ComboBox<String>> secondaryMetaDataTypes;
        
        private final Button startButton;
        private final Button cancelButton;
        private final ProgressBar progressBar;
        private final TextArea messageArea;
        
        private final boolean arcade;
        
        public OutputDialog(List<Game> games, String outputPathStr, String imagesPathStr, boolean arcade) {
            super();
            this.arcade = arcade;
            
            messageArea = new TextArea("Press START to begin.\n");
            messageArea.setEditable(false);
            progressBar = new ProgressBar();
            startButton = new Button("Start");
            cancelButton = new Button("Cancel");
            
            VBox tagsBox = new VBox();
            tagsBox.setSpacing(7.);
            tagsBox.setPadding(new Insets(7.));
            tagsBox.getChildren().add(new Label("EmulationStation Image Tags"));
            
            esTags = new ArrayList();
            enableTagCheckBoxes = new ArrayList();
            primaryMetaDataTypes = new ArrayList();
            secondaryMetaDataTypes = new ArrayList();
            
            for(ESImageTag tag : ESImageTag.values()) {
                esTags.add(tag.getTag());
                TextField tagField = new TextField(tag.getTag());
                tagField.setEditable(false);
                
                CheckBox enableTagCheckBox = new CheckBox();
                enableTagCheckBox.setSelected(true);
                ComboBox<String> primaryTypeBox = new ComboBox();
                ComboBox<String> secondaryTypeBox = new ComboBox();
                
                enableTagCheckBoxes.add(enableTagCheckBox);
                primaryMetaDataTypes.add(primaryTypeBox);
                secondaryMetaDataTypes.add(secondaryTypeBox);
                
                primaryTypeBox.getItems().add("");
                primaryTypeBox.getSelectionModel().select(0);
                secondaryTypeBox.getItems().add("");
                secondaryTypeBox.getSelectionModel().select(0);
                for(Image.ImageType type : Image.ImageType.values()) {
                    if(arcade == type.isArcadeImage()) {
                        primaryTypeBox.getItems().add(type.getName());
                        secondaryTypeBox.getItems().add(type.getName());
                    }
                }
                
                if(arcade) {
                    if(tag.getArcadeDefault() != null) {
                        primaryTypeBox.getSelectionModel().select(tag.getArcadeDefault().getName());
                    }
                    if(tag.getArcadeSecondary() != null) {
                        secondaryTypeBox.getSelectionModel().select(tag.getArcadeSecondary().getName());
                    }
                }
                else {
                    if(tag.getConsoleDefault() != null) {
                        primaryTypeBox.getSelectionModel().select(tag.getConsoleDefault().getName());
                    }
                    if(tag.getConsoleSecondary() != null) {
                        secondaryTypeBox.getSelectionModel().select(tag.getConsoleSecondary().getName());
                    }
                }
                
                HBox box = new HBox();
                box.setSpacing(7.);
                box.getChildren().addAll(enableTagCheckBox, tagField, primaryTypeBox, secondaryTypeBox);
                
                tagsBox.getChildren().add(box);
            }
            
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(7.);
            buttonBox.getChildren().addAll(progressBar, startButton, cancelButton);
            
            tagsBox.getChildren().add(messageArea);
            tagsBox.getChildren().add(buttonBox);
            
            OutputTask task = new OutputTask(games, outputPathStr, imagesPathStr);//, arcade);

            task.messageProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                messageArea.appendText(newValue + "\n");
            });

            task.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                progressBar.setProgress((double)newValue);
            });

            task.setOnSucceeded((e) -> {
                messageArea.appendText("Output complete!");
                cancelButton.setText("Close");
            });

            task.setOnCancelled((e) -> {
                messageArea.appendText("Output cancelled!");
                cancelButton.setText("Close");
            });
                
            startButton.setOnAction((e) -> {
                startButton.setDisable(true);

                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();
            });
            
            cancelButton.setOnAction((e) -> {
                if(task.isRunning()) {
                    task.cancel();
                }
                else {
                    hide();
                }
            });
            
            setOnHidden((e) -> {
                task.cancel();
            });

            Scene scene = new Scene(tagsBox);
            
            setTitle("EmulationStation Output");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(ScraperFX.getPrimaryStage());
            setScene(scene);
        }
        
        private class OutputTask extends Task<Void> {
            private final List<Game> games;
            private final String outputPathStr;
            private final String imagesPathStr;

            public OutputTask(List<Game> games, String outputPathStr, String imagesPathStr) {
                this.games = games;
                this.outputPathStr = outputPathStr;
                this.imagesPathStr = imagesPathStr;
            }
            
//            private String determineOutputType(String path) {
//                String imageType = null;
//                if(arcade) {
//                    // png files from arcadeitalia are tiny so i'm ok with them here, or if there's a random jpg, which there probably isn't.
//                    if(path.toLowerCase().endsWith("png")) {
//                        imageType = "png";
//                    }
//                    else {
//                        imageType = "jpg";
//                    }
//                }
//                else {
//                    // otherwise the size of png files from gamesdb is pretty bonkers compared to if i just save them as jpg.
//                    imageType = "jpg";
//                }
//                return imageType;
//            }

            @Override
            protected Void call()  {
                AtomicBoolean isScanning = new AtomicBoolean(true);
                try {
                    FileSystem fs = FileSystems.getDefault();
                    Path imagesPath = fs.getPath(imagesPathStr);
                    Path outputPath = fs.getPath(outputPathStr, "gamelist.xml");
                    Files.createDirectories(outputPath.getParent());
                    try {
                        Files.createFile(outputPath);
                    }
                    catch(FileAlreadyExistsException ex) {
                        // good!
                    }

                    File outputFile = outputPath.toFile();
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                        writer.append("<gameList>\n");

                        int fileCount = 0;
                        for(Game g : games) {
                            if(isCancelled()) {
                                break;
                            }
                            if(g.strength != Game.MatchStrength.IGNORE) {
                                writer.append("\t<game>\n");

                                writer.append("\t\t<path>./" + g.fileName + "</path>\n");
                                writer.append("\t\t<filename>" + g.fileName + "</filename>\n");
                                if(g.metadata != null) {
                                    if(g.metadata.metaName != null)         writer.append("\t\t<name>" + g.metadata.metaName + "</name>\n");
                                    if(g.metadata.metaDesc != null)         writer.append("\t\t<desc>" + g.metadata.metaDesc + "</desc>\n");
                                    if(g.metadata.metaReleaseDate != null) {
                                        if(g.metadata.metaReleaseDate.length() == 4) {
                                            writer.append("\t\t<releasedate>" + g.metadata.metaReleaseDate + "0101T000000</releasedate>\n");
                                        }
                                        else {
                                            writer.append("\t\t<releasedate>" + g.metadata.metaReleaseDate.substring(6) + g.metadata.metaReleaseDate.substring(0, 2) + g.metadata.metaReleaseDate.substring(3, 5) + "T000000</releasedate>\n");
                                        }
                                    }
    //                                if(g.metadata.metaReleaseDate != null)  writer.append("\t\t<releasedate>" + (g.metadata.metaReleaseDate.substring(6) + g.metadata.metaReleaseDate.substring(0, 2) + g.metadata.metaReleaseDate.substring(3, 5) + "T000000") + "</releasedate>\n");
                                    if(g.metadata.metaDeveloper != null)    writer.append("\t\t<developer>" + g.metadata.metaDeveloper + "</developer>\n");
                                    if(g.metadata.metaPublisher != null)    writer.append("\t\t<publisher>" + g.metadata.metaPublisher + "</publisher>\n");
                                    if(g.metadata.metaGenre != null)        writer.append("\t\t<genre>" + g.metadata.metaGenre + "</genre>\n");
                                    if(g.metadata.players != null)          writer.append("\t\t<players>" + g.metadata.players + "</players>\n");

                                    if(g.metadata.images != null && !g.metadata.images.isEmpty()) {
                                        for(int i = 0; i < esTags.size(); i++) {
                                            if(enableTagCheckBoxes.get(i).isSelected()) {
                                                String primary = primaryMetaDataTypes.get(i).getSelectionModel().getSelectedItem();
                                                String secondary = secondaryMetaDataTypes.get(i).getSelectionModel().getSelectedItem();
                                                String primaryPath = g.metadata.getSelectedImagePath(primary);
                                                String secondaryPath = g.metadata.getSelectedImagePath(secondary);

                                                String primaryImageType = null;
                                                String secondaryImageType = null;
                                                if(primaryPath != null) primaryImageType = primaryPath.substring(primaryPath.lastIndexOf(".") + 1);//determineOutputType(primaryPath);
                                                if(secondaryPath != null) secondaryImageType = secondaryPath.substring(secondaryPath.lastIndexOf(".") + 1);//determineOutputType(secondaryPath);

                                                if(!primary.equals("") && ScraperFX.writeImageToFile(imagesPath, g.fileName + "-" + primary, primaryImageType, primaryPath)) {
                                                    writer.append("\t\t<" + esTags.get(i) + ">./images/" + g.fileName + "-" + primary + "." + primaryImageType + "</" + esTags.get(i) + ">\n");
                                                    continue;
                                                }
                                                if(!secondary.equals("") && ScraperFX.writeImageToFile(imagesPath, g.fileName + "-" + secondary, secondaryImageType, secondaryPath)) {
                                                    writer.append("\t\t<" + esTags.get(i) + ">./images/" + g.fileName + "-" + secondary + "." + secondaryImageType + "</" + esTags.get(i) + ">\n");
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }

                                writer.append("\t</game>\n");

                                updateProgress(++fileCount, games.size());
                                updateMessage("Completed " + g.fileName + " output to gamelist.xml");
                            }
                            
                            try {
                                Thread.sleep(60);
                            }
                            catch(InterruptedException interrupted) {
                                if(isCancelled()) {
                                    break;
                                }
                            }
                        }

                        writer.append("</gameList>\n");
                    }
                    catch(IOException ex) {
                        Logger.getLogger(ESOutput.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    isScanning.set(false);
                }
                catch(IOException ex) {
                    Logger.getLogger(ESOutput.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    if(isScanning.get()) {
                        isScanning.set(false);
                        cancel();
                    }
                }
                return null;
            }
        }
    }
}
