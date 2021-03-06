/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.output;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author goodjaerb
 */
public class ESOutput {

    public enum ESImageTag {
        IMAGE("image", Image.ImageType.BOX_FRONT, Image.ImageType.SCREENSHOT, Image.ImageType.TITLE, Image.ImageType.GAME),
        BG_IMAGE("bgImage", Image.ImageType.SCREENSHOT, Image.ImageType.FANART, Image.ImageType.GAME, Image.ImageType.TITLE),
        SCREENSHOT("screenshot", Image.ImageType.SCREENSHOT, null, Image.ImageType.GAME, null),
        ;

        private final String          tag;
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

    public static final String GAMELISTS_DIR = "gamelists";
    public static final String IMAGES_DIR    = "images";
    public static final String VIDEOS_DIR    = "videos";

    public void output(List<Game> games, Path outputPath, Path imagesPath, Path videoPath, boolean arcade, Window parentWindow) {
        final OutputDialog d = new OutputDialog(games, outputPath, imagesPath, videoPath, arcade, parentWindow);
        d.showAndWait();
    }

    private static class OutputDialog extends Stage {

        private final List<String>           esTags                 = new ArrayList<>();
        private final List<CheckBox>         enableTagCheckBoxes    = new ArrayList<>();
        private final List<ComboBox<String>> primaryMetaDataTypes   = new ArrayList<>();
        private final List<ComboBox<String>> secondaryMetaDataTypes = new ArrayList<>();
        private final CheckBox               skipUnmatchedCheckBox  = new CheckBox("Skip Unmatched Files");
        private final CheckBox               downloadVideosCheckBox = new CheckBox("Download Videos");

        private final Button      startButton  = new Button("Start");
        private final Button      cancelButton = new Button("Cancel");
        private final ProgressBar progressBar  = new ProgressBar();
        //        private final QueuedMessageBox  messageArea = new QueuedMessageBox("Press START to begin.\n");
        private final TextArea    messageArea  = new TextArea("Press START to begin.\n");

        OutputDialog(List<Game> games, Path outputPath, Path imagesPath, Path videoPath, boolean arcade, Window parentWindow) {
            super();

            final VBox tagsBox = new VBox();
            tagsBox.setSpacing(7.);
            tagsBox.setPadding(new Insets(7.));
            tagsBox.getChildren().add(new Label("EmulationStation Image Tags"));

            for(final ESImageTag tag : ESImageTag.values()) {
                esTags.add(tag.getTag());
                final TextField tagField = new TextField(tag.getTag());
                tagField.setEditable(false);

                final CheckBox enableTagCheckBox = new CheckBox();
                enableTagCheckBox.setSelected(true);
                ComboBox<String> primaryTypeBox = new ComboBox<>();
                ComboBox<String> secondaryTypeBox = new ComboBox<>();

                enableTagCheckBoxes.add(enableTagCheckBox);
                primaryMetaDataTypes.add(primaryTypeBox);
                secondaryMetaDataTypes.add(secondaryTypeBox);

                primaryTypeBox.getItems().add("");
                primaryTypeBox.getSelectionModel().select(0);
                secondaryTypeBox.getItems().add("");
                secondaryTypeBox.getSelectionModel().select(0);
                for(final Image.ImageType type : Image.ImageType.values()) {
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

                final HBox box = new HBox();
                box.setSpacing(7.);
                box.getChildren().addAll(enableTagCheckBox, tagField, primaryTypeBox, secondaryTypeBox);

                tagsBox.getChildren().add(box);
            }

            final HBox buttonBox = new HBox();
            buttonBox.setSpacing(7.);
            buttonBox.getChildren().addAll(progressBar, startButton, cancelButton);

            tagsBox.getChildren().add(skipUnmatchedCheckBox);
            tagsBox.getChildren().add(downloadVideosCheckBox);
            tagsBox.getChildren().add(messageArea);
            tagsBox.getChildren().add(buttonBox);

            final OutputTask task = new OutputTask(games, outputPath, imagesPath, videoPath);

            task.messageProperty().addListener((observable, oldValue, newValue) -> messageArea.appendText("\n" + newValue));
            task.progressProperty().addListener((observable, oldValue, newValue) -> progressBar.setProgress((double) newValue));

            task.setOnSucceeded((e) -> {
//                messageArea.queueMessage("Output complete!");
                messageArea.appendText("Output complete!");
                cancelButton.setText("Close");
            });

            task.setOnCancelled((e) -> {
                messageArea.appendText("Output cancelled!");
//                messageArea.queueMessage("Output cancelled!");
                cancelButton.setText("Close");
            });

            startButton.setOnAction((e) -> {
                startButton.setDisable(true);
//                messageArea.start();
                final Thread t = new Thread(task);
//                t.setDaemon(true);
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
//                messageArea.stop();
                task.cancel();
            });

            final Scene scene = new Scene(tagsBox);

            setTitle("EmulationStation Output");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(parentWindow);
            setScene(scene);
        }

        private class OutputTask extends Task<Void> {
            private final List<Game> games;
            private final Path       outputPath;
            private final Path       imagesPath;
            private final Path       videoPath;

//            private final Consumer<String> messageConsumer;

            OutputTask(List<Game> games, Path outputPath, Path imagesPath, Path videoPath) {
                this.games = games;
                this.outputPath = outputPath.resolve("gamelist.xml");
                this.imagesPath = imagesPath;
                this.videoPath = videoPath;

//                this.messageConsumer = messageConsumer;
            }

            @Override
            protected Void call() {
                final AtomicBoolean isScanning = new AtomicBoolean(true);
                try {
                    Files.createDirectories(outputPath.getParent());
                    try {
                        Files.createFile(outputPath);
                    }
                    catch(FileAlreadyExistsException ex) {
                        // good!
                    }

                    try(final BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                        writer.append("<gameList>\n");

                        int fileCount = 0;
                        for(final Game g : games) {
                            if(isCancelled()) {
                                break;
                            }

                            if(g.strength != Game.MatchStrength.IGNORE && (!skipUnmatchedCheckBox.isSelected() || g.strength != Game.MatchStrength.NO_MATCH)) {
                                Logger.getLogger(ESOutput.class.getName()).log(Level.INFO, "Beginning output of ''{0}''.", g);
                                writer.append("\t<game>\n");

                                writer.append("\t\t<path>./").append(g.fileName).append("</path>\n");
                                writer.append("\t\t<filename>").append(g.fileName).append("</filename>\n");
                                if(g.metadata == null) {
                                    String noMatchMetaName = g.fileName;
                                    if(ScraperFX.getCurrentSettings().substringRegex != null && !"".equals(ScraperFX.getCurrentSettings().substringRegex)) {
                                        final Pattern pattern = Pattern.compile(".*" + ScraperFX.getCurrentSettings().substringRegex + ".*");
                                        final Matcher m = pattern.matcher(noMatchMetaName);
                                        if(m.matches()) {
                                            for(int i = 1; i <= m.groupCount(); i++) {
                                                noMatchMetaName = noMatchMetaName.replaceAll(m.group(i), "");
                                            }
                                        }
                                    }
                                    noMatchMetaName = noMatchMetaName.replaceAll("(\\(.*\\)|\\[.*\\])", "");
                                    noMatchMetaName = noMatchMetaName.substring(0, noMatchMetaName.lastIndexOf(".")).trim();
                                    writer.append("\t\t<name>").append(noMatchMetaName).append("</name>\n");
                                }
                                else {
                                    Logger.getLogger(ESOutput.class.getName()).log(Level.INFO, "Outputting metadata ''{0}''.", g);
                                    if(g.metadata.metaName != null) {
                                        writer.append("\t\t<name>").append(g.metadata.metaName).append("</name>\n");
                                    }
                                    if(g.metadata.metaSortName != null && !g.metadata.metaSortName.isEmpty()) {
                                        writer.append("\t\t<sortname>").append(g.metadata.metaSortName).append("</sortname>\n");
                                    }
                                    if(g.metadata.metaDesc != null && !g.metadata.metaDesc.isEmpty()) {
                                        writer.append("\t\t<desc>").append(g.metadata.metaDesc).append("</desc>\n");
                                    }
                                    if(g.metadata.metaReleaseDate != null && !g.metadata.metaReleaseDate.isEmpty()) {
                                        final int dateLength = g.metadata.metaReleaseDate.length();
                                        if(dateLength == 4 || dateLength == 5) {
                                            //arcadeitalia tends to just have a year for a release date but often times will add a '?' to the end so
                                            //this will take care of that.
                                            writer.append("\t\t<releasedate>").append(g.metadata.metaReleaseDate.replaceAll("\\p{Punct}", "")).append("0101T000000</releasedate>\n");
                                        }
                                        else {
                                            writer.append("\t\t<releasedate>").append(g.metadata.metaReleaseDate.substring(6)).append(g.metadata.metaReleaseDate.substring(0, 2)).append(g.metadata.metaReleaseDate.substring(3, 5)).append("T000000</releasedate>\n");
                                        }
                                    }
                                    if(g.metadata.metaDeveloper != null && !g.metadata.metaDeveloper.isEmpty() && !"<generic>".equals(g.metadata.metaDeveloper)) {
                                        writer.append("\t\t<developer>").append(g.metadata.metaDeveloper.replace("<unknown> / ", "")).append("</developer>\n");
                                    }
                                    if(g.metadata.metaPublisher != null && !g.metadata.metaPublisher.isEmpty()) {
                                        writer.append("\t\t<publisher>").append(g.metadata.metaPublisher).append("</publisher>\n");
                                    }
                                    if(g.metadata.metaGenre != null && !g.metadata.metaGenre.isEmpty()) {
                                        writer.append("\t\t<genre>").append(g.metadata.metaGenre).append("</genre>\n");
                                    }
                                    if(g.metadata.players != null && !g.metadata.players.isEmpty()) {
                                        writer.append("\t\t<players>").append(g.metadata.players).append("</players>\n");
                                    }
                                    if(g.metadata.favorite) {
                                        writer.append("\t\t<favorite>true</favorite>\n");
                                    }

                                    if(g.metadata.videodownload != null && !g.metadata.videodownload.isEmpty() && downloadVideosCheckBox.isSelected()) {
                                        Logger.getLogger(ESOutput.class.getName()).log(Level.INFO, "Downloading video for ''{0}''.", g);
                                        if(ScraperFX.saveVideo(videoPath, g.fileName + "_video.mp4", g.metadata.videodownload)) {
                                            Logger.getLogger(ESOutput.class.getName()).log(Level.INFO, "Video downloaded.");
                                            writer.append("\t\t<video>./videos/").append(g.fileName).append("_video.mp4</video>\n");
                                        }
                                    }

                                    if(g.metadata.videoembed != null && !g.metadata.videoembed.isEmpty()) {
                                        writer.append("\t\t<videoEmbed>").append(g.metadata.videoembed).append("</videoEmbed>\n");
                                    }

                                    if(g.metadata.images != null && !g.metadata.images.isEmpty()) {
                                        for(int i = 0; i < esTags.size(); i++) {
                                            if(enableTagCheckBoxes.get(i).isSelected()) {
                                                final String primary = primaryMetaDataTypes.get(i).getSelectionModel().getSelectedItem();
                                                final String secondary = secondaryMetaDataTypes.get(i).getSelectionModel().getSelectedItem();
                                                final String primaryPath = g.metadata.getSelectedImageUrl(primary);
                                                final String secondaryPath = g.metadata.getSelectedImageUrl(secondary);

//                                                String primaryImageType = g.metadata.getSelectedImageType(primary);
//                                                String secondaryImageType = g.metadata.getSelectedImageType(secondary);
//                                                if(primaryImageType == null && primaryPath != null) primaryImageType = primaryPath.substring(primaryPath.lastIndexOf(".") + 1);//determineOutputType(primaryPath);
//                                                if(secondaryImageType == null && secondaryPath != null) secondaryImageType = secondaryPath.substring(secondaryPath.lastIndexOf(".") + 1);//determineOutputType(secondaryPath);
                                                final String primaryImageType = "png";
                                                final String secondaryImageType = "png";
                                                if(!primary.equals("") && ScraperFX.writeImageToFile(imagesPath, g.fileName + "-" + primary, primaryImageType, primaryPath)) {
                                                    writer.append("\t\t<").append(esTags.get(i)).append(">./images/").append(g.fileName).append("-").append(primary).append(".").append(primaryImageType).append("</").append(esTags.get(i)).append(">\n");
                                                    continue;
                                                }
                                                if(!secondary.equals("") && ScraperFX.writeImageToFile(imagesPath, g.fileName + "-" + secondary, secondaryImageType, secondaryPath)) {
                                                    writer.append("\t\t<").append(esTags.get(i)).append(">./images/").append(g.fileName).append("-").append(secondary).append(".").append(secondaryImageType).append("</").append(esTags.get(i)).append(">\n");
                                                    continue;
                                                }

                                                //hardcode flyer/marquee as last resorts because i can't be bothered to update the gui.
                                                //for the life of me i don't know why i do this??
                                                final String flyerPath = g.metadata.getSelectedImageUrl("flyer");
                                                final String marqueePath = g.metadata.getSelectedImageUrl("marquee");
                                                if(flyerPath != null && "image".equals(esTags.get(i))) {
                                                    final String flyerImageType = "png";//flyerPath.substring(flyerPath.lastIndexOf(".") + 1);
                                                    if(ScraperFX.writeImageToFile(imagesPath, g.fileName + "-flyer", flyerImageType, flyerPath)) {
                                                        writer.append("\t\t<").append(esTags.get(i)).append(">./images/").append(g.fileName).append("-flyer").append(".").append(flyerImageType).append("</").append(esTags.get(i)).append(">\n");
                                                        continue;
                                                    }
                                                }
                                                if(marqueePath != null && "bgImage".equals(esTags.get(i))) {
                                                    final String marqueeImageType = "png";//marqueePath.substring(marqueePath.lastIndexOf(".") + 1);
                                                    if(ScraperFX.writeImageToFile(imagesPath, g.fileName + "-marquee", marqueeImageType, marqueePath)) {
                                                        writer.append("\t\t<").append(esTags.get(i)).append(">./images/").append(g.fileName).append("-marquee").append(".").append(marqueeImageType).append("</").append(esTags.get(i)).append(">\n");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                writer.append("\t</game>\n");
                                updateMessage("Completed " + g.fileName + " output to gamelist.xml");
//                                messageConsumer.accept("Completed " + g.fileName + " output to gamelist.xml");
                            }
                            updateProgress(++fileCount, games.size());
                        }

                        writer.append("</gameList>\n");
                        isScanning.set(false);
                    }
                    catch(IOException ex) {
                        Logger.getLogger(ESOutput.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
