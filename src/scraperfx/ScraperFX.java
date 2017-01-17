/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.xmappr.Xmappr;
import scraperfx.datasource.DataSourceFactory;
import scraperfx.datasource.DataSourceFactory.SourceAgent;
import scraperfx.output.ESOutput;
import scraperfx.settings.Game;
import scraperfx.settings.GameData;
import scraperfx.settings.GameList;
import scraperfx.settings.MetaData;
import scraperfx.settings.MetaData.MetaDataId;
import scraperfx.settings.SystemSettings;

/**
 *
 * @author goodjaerb
 */
public class ScraperFX extends Application {
    private static Stage mainStage;
    private static SystemSettings settings;
    private static String currentSystemName;
    
    private final ListView<String> systemList;
    
    private final TabPane tabPane;
    
    private final Tab settingsTab;
    private final ToggleGroup scrapeTypeGroup;
    private final RadioButton scrapeAsConsoleButton;
    private final RadioButton scrapeAsArcadeButton;
    private final ComboBox<String> consoleSelectComboBox;
    private final TextField gameSourceField;
    private final Button gameSourceBrowseButton;
    private final TextField filenameRegexField;
    private final TextField ignoreRegexField;
//    private final TextField imageOuputField;
//    private final Button imageOutputBrowseButton;
//    private final TextField defaultScreenshotField;
//    private final Button defaultScreenshotClearButton;
//    private final Button defaultScreenshotBrowseButton;
//    private final TextField defaultBoxArtField;
//    private final Button defaultBoxArtClearButton;
//    private final Button defaultBoxArtBrowseButton;
    private final CheckBox unmatchedOnlyCheckBox;
    
    private final Tab gamesTab;
    private final BorderPane gamesPane;
    private final ScrollPane imagesScroll;
    private final CheckBox lockImagesCheckBox;
    private final ListView<Game> gamesListView;
    private final TextField matchedNameField;
    private final Button matchedNameBrowseButton;
    private final Button matchedNameClearButton;
    private final CheckBox lockMatchedNameCheckBox;
    private final TextField metaNameField;
    private final CheckBox lockNameCheckBox;
    private final TextArea metaDescArea;
    private final CheckBox lockDescCheckBox;
    private final TextField metaRatingField;
    private final CheckBox lockRatingCheckBox;
    private final TextField metaReleaseDateField;
    private final CheckBox lockReleaseDateCheckBox;
    private final TextField metaDeveloperField;
    private final CheckBox lockDeveloperCheckBox;
    private final TextField metaPublisherField;
    private final CheckBox lockPublisherCheckBox;
    private final TextField metaGenreField;    
    private final CheckBox lockGenreCheckBox;
    private final TextField playersField;
    private final CheckBox lockPlayersCheckBox;
    
//    private final Tab outputTab;
//    private final ToggleGroup outputSettingsGroup;
//    private final RadioButton useGlobalSettingsButton;
//    private final RadioButton useSystemSettingsButton;
    
    private final Button saveButton;
    private final Button scanButton;
    private final Button deleteSystemButton;
    private final Button outputToGamelistButton;
    
    private final AtomicBoolean isScanning;
    
    private final List<ImageLoadingTask> imageTaskList;
    
    private GameData gamedata;
    private Scene rootScene;
    private Game currentGame;
    
    public ScraperFX() {
        systemList = new ListView();
        
        tabPane = new TabPane();
        
        settingsTab = new Tab("Settings");
        scrapeTypeGroup = new ToggleGroup();
        scrapeAsConsoleButton = new RadioButton("Console");
        scrapeAsConsoleButton.setToggleGroup(scrapeTypeGroup);
        scrapeAsConsoleButton.setSelected(true);
        scrapeAsArcadeButton = new RadioButton("Arcade");
        scrapeAsArcadeButton.setToggleGroup(scrapeTypeGroup);
        consoleSelectComboBox = new ComboBox<>();
        consoleSelectComboBox.setEditable(false);
        gameSourceField = new TextField();
        gameSourceField.setEditable(false);
        gameSourceBrowseButton = new Button("...");
        filenameRegexField = new TextField();
        ignoreRegexField = new TextField();
        
//        imageOuputField = new TextField();
//        imageOuputField.setEditable(false);
//        imageOutputBrowseButton = new Button("...");
//        defaultScreenshotField = new TextField();
//        defaultScreenshotField.setEditable(false);
//        defaultScreenshotClearButton = new Button("X");
//        defaultScreenshotClearButton.setStyle("-fx-font-weight: bold");
//        defaultScreenshotClearButton.setTextFill(Color.RED);
//        defaultScreenshotBrowseButton = new Button("...");
//        defaultBoxArtField = new TextField();
//        defaultBoxArtField.setEditable(false);
//        defaultBoxArtClearButton = new Button("X");
//        defaultBoxArtClearButton.setStyle("-fx-font-weight: bold");
//        defaultBoxArtClearButton.setTextFill(Color.RED);
//        defaultBoxArtBrowseButton = new Button("...");
        unmatchedOnlyCheckBox = new CheckBox("Scan Unmatched Files Only");
        
        gamesTab = new Tab("Games");
        gamesPane = new BorderPane();
        imagesScroll = new ScrollPane();
        gamesListView = new ListView();
        gamesListView.setCellFactory((ListView<Game> list) -> new GameListCell());
        matchedNameField = new TextField();
        matchedNameField.setEditable(false);
        matchedNameBrowseButton = new Button("...");
        matchedNameClearButton = new Button("X");
        matchedNameClearButton.setStyle("-fx-font-weight: bold");
        matchedNameClearButton.setTextFill(Color.RED);
        metaNameField = new TextField();
        metaDescArea = new TextArea();
        metaDescArea.setWrapText(true);
        metaRatingField = new TextField();
        metaReleaseDateField = new TextField();
        metaDeveloperField = new TextField();
        metaPublisherField = new TextField();
        metaGenreField = new TextField();
        playersField = new TextField();
        
        lockMatchedNameCheckBox = new CheckBox("Lock");
        lockNameCheckBox = new CheckBox("Lock");
        lockDescCheckBox = new CheckBox("Lock");
        lockRatingCheckBox = new CheckBox("Lock");
        lockReleaseDateCheckBox = new CheckBox("Lock");
        lockDeveloperCheckBox = new CheckBox("Lock");
        lockPublisherCheckBox = new CheckBox("Lock");
        lockGenreCheckBox = new CheckBox("Lock");
        lockPlayersCheckBox = new CheckBox("Lock");
        lockImagesCheckBox = new CheckBox("Lock Images");
        
        
//        outputTab = new Tab("Output");
//        outputSettingsGroup = new ToggleGroup();
//        useGlobalSettingsButton = new RadioButton("Use Global Settings");
//        useGlobalSettingsButton.setToggleGroup(outputSettingsGroup);
//        useGlobalSettingsButton.setSelected(true);
//        useSystemSettingsButton = new RadioButton("Use System Specific Settings");
//        useSystemSettingsButton.setToggleGroup(outputSettingsGroup);
        
        saveButton = new Button("Save");
        scanButton = new Button("Scan Now");
        deleteSystemButton = new Button("Delete System");
        outputToGamelistButton = new Button("Output to Gamelist.xml");
        
        isScanning = new AtomicBoolean(false);
        
        imageTaskList = new ArrayList();
    }
    
    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        
        /*
        * System List
        */
        systemList.setPrefSize(175., 500.);
        systemList.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            String newSelection = systemList.getSelectionModel().getSelectedItem();
            if(newSelection != null && !newSelection.equals(currentSystemName)) {
                currentSystemName = newSelection;
                loadCurrentSettings();
                clearCurrentGameFields();
                enableTabs();
                currentGame = null;
            }
        });
                
        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction((e) -> {
            TextInputDialog renameDialog = new TextInputDialog();
            renameDialog.setHeaderText("Rename system '" + currentSystemName + "'");
            renameDialog.setTitle("Rename System");
            renameDialog.setContentText("Name:");
            
            Optional<String> result = renameDialog.showAndWait();
            if(result.isPresent()) {
                settings.renameSystem(currentSystemName, result.get());
                loadSystemList(result.get());
            }
        });
        systemList.setContextMenu(new ContextMenu(renameItem));
        
        Button addSystemButton = new Button("Add System");
        addSystemButton.setOnAction((e) -> addSystemButtonActionPerformed() );
        
        VBox box2 = new VBox(systemList, addSystemButton);
        box2.setAlignment(Pos.CENTER);
        box2.setSpacing(7.);
        // End System List
        
        /*
        * Settings Tab
        */
        scrapeAsConsoleButton.setOnAction((e) -> {
            scrapeAsArcadeSetup(scrapeAsArcadeButton.isSelected());
        });
        
        scrapeAsArcadeButton.setOnAction((e) -> {
            scrapeAsArcadeSetup(scrapeAsArcadeButton.isSelected());
        });
        
        consoleSelectComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            getCurrentSettings().scrapeAs = consoleSelectComboBox.getSelectionModel().getSelectedItem();
        });
        
        
        gameSourceBrowseButton.setOnAction((e) -> {
            File dir = Chooser.getDir("Game Source Directory");
            if(dir != null) {
                gameSourceField.setText(dir.getPath());
                getCurrentSettings().romsDir = dir.getPath();
            }
        });
        
        filenameRegexField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) {
                //lost focus
                getCurrentSettings().substringRegex = filenameRegexField.getText();
            }
        });
        
        ignoreRegexField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue) {
                //lost focus
                getCurrentSettings().ignoreRegex = ignoreRegexField.getText();
            }
        });
        
//        imageOutputBrowseButton.setOnAction((e) -> {
//            File dir = Chooser.getDir("Image Output Directory");
//            if(dir != null) {
//                imageOuputField.setText(dir.getPath());
//                getCurrentSettings().imageDir = dir.getPath();
//            }
//        });
        
//        defaultBoxArtBrowseButton.setOnAction((e) -> {
//            File f = Chooser.getFile(Chooser.DialogType.OPEN, "Default Box Art");
//            if(f != null) {
//                defaultBoxArtField.setText(f.getPath());
//                getCurrentSettings().defaultBoxArt = f.getPath();
//            }
//        });
//        
//        defaultScreenshotBrowseButton.setOnAction((e) -> {
//            File f = Chooser.getFile(Chooser.DialogType.OPEN, "Default Screenshot");
//            if(f != null) {
//                defaultScreenshotField.setText(f.getPath());
//                getCurrentSettings().defaultScreenshot = f.getPath();
//            }
//        });
        
//        FlowPane scanNewOnlyPane = new FlowPane(unmatchedOnlyCheckBox);
        unmatchedOnlyCheckBox.setPadding(new Insets(7.));
        unmatchedOnlyCheckBox.setOnAction((e) -> {
            getCurrentSettings().unmatchedOnly = unmatchedOnlyCheckBox.isSelected();
        });

        HBox box21 = new HBox();
        box21.setSpacing(7.);
        box21.setPadding(new Insets(7.));
        
        box21.getChildren().addAll(new Label("Scrap as:"), scrapeAsConsoleButton, scrapeAsArcadeButton);
        
        HBox selectConsoleBox = new HBox();
        selectConsoleBox.setSpacing(7.);
        selectConsoleBox.setPadding(new Insets(7.));
        selectConsoleBox.getChildren().add(new Label("Select Console:"));
        selectConsoleBox.getChildren().add(consoleSelectComboBox);
        
        new Thread(() -> {
            try {
                List<String> consoles = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB).getSystemNames();
                Platform.runLater(() -> {
                    consoleSelectComboBox.getItems().clear();
                    consoleSelectComboBox.getItems().addAll(consoles);
                    if(!getCurrentSettings().scrapeAsArcade) {
                        consoleSelectComboBox.getSelectionModel().select(getCurrentSettings().scrapeAs);
                    }
                    else {
                        consoleSelectComboBox.getSelectionModel().select(0);
                    }
                });
            }
            catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        
//        VBox regexBox = new VBox();
//        regexBox.setSpacing(7.);
//        regexBox.setPadding(new Insets(7.));
//        regexBox.getChildren().addAll(new Label("Filename Removal Regex (Advanced):"), filenameRegexField);
        
        VBox settingsPane = new VBox();
        settingsPane.getChildren().addAll(
                box21,
                selectConsoleBox,
                createBrowseFieldPane("Game Source Directory:", gameSourceField, gameSourceBrowseButton),
//                createBrowseFieldPane("Metadata Images Output Directory:", imageOuputField, imageOutputBrowseButton),
//                createBrowseFieldPane("Default 'No Box Art Image:", defaultBoxArtField, defaultBoxArtClearButton, defaultBoxArtBrowseButton),
//                createBrowseFieldPane("Default 'No Screenshot' Image:", defaultScreenshotField, defaultScreenshotClearButton, defaultScreenshotBrowseButton),
//                scanNewOnlyPane
                createBrowseFieldPane("Regex for Substring Removal (Advanced):", filenameRegexField),
                createBrowseFieldPane("Regex for Files to Ignore (Advanced):", ignoreRegexField),
                unmatchedOnlyCheckBox
        );
        
        settingsTab.setContent(settingsPane);
        settingsTab.setClosable(false);
        settingsTab.setDisable(true);
        // End Settings Tab
        
        /*
        * Games Tab
        */
        gamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gamesListView.setPrefSize(450., 425.);
        gamesListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Game> c) -> {
            Game selected = gamesListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
//                currentGame = getCurrentSettings().getGame(selected);
                currentGame = getGame(selected);
                loadCurrentGameFields(currentGame);
            }
        });
        
        MenuItem lockGamesItem = new MenuItem("Lock Selection");
        lockGamesItem.setOnAction((e) -> {
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            for(Game g : selectedGames) {
//                System.out.println(g);
//                getCurrentSettings().getGame(g).strength = Game.MatchStrength.LOCKED;
                getGame(g).strength = Game.MatchStrength.LOCKED;
            }
            gamesListView.refresh();
            loadCurrentGameFields(currentGame);
        });
        
        MenuItem unlockGamesItem = new MenuItem("Unlock Selection");
        unlockGamesItem.setOnAction((e) -> {
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            for(Game g : selectedGames) {
//                if(getCurrentSettings().getGame(g).matchedName == null) {
//                    getCurrentSettings().getGame(g).strength = Game.MatchStrength.NO_MATCH;
//                }
//                else {
//                    getCurrentSettings().getGame(g).strength = Game.MatchStrength.BEST_GUESS;
//                }
                if(getGame(g).matchedName == null) {
                    getGame(g).strength = Game.MatchStrength.NO_MATCH;
                }
                else {
                    getGame(g).strength = Game.MatchStrength.BEST_GUESS;
                }
            }
            gamesListView.refresh();
            loadCurrentGameFields(currentGame);
        });
        
        MenuItem ignoreItem = new MenuItem("Ignore");
        ignoreItem.setOnAction((e) -> {
            clearCurrentGameFields();
//            int index = getCurrentSettings().games.indexOf(gamesListView.getSelectionModel().getSelectedItem());
//            getCurrentSettings().games.get(index).matchedName = null;
//            getCurrentSettings().games.get(index).metadata = null;
//            getCurrentSettings().games.get(index).strength = Game.MatchStrength.LOCKED;
            currentGame.matchedName = null;
            currentGame.metadata = null;
            currentGame.strength = Game.MatchStrength.IGNORE;
//            lockMatchedNameCheckBox.setSelected(true);
            loadCurrentGameFields(currentGame);
            gamesListView.refresh();
        });
        
        gamesListView.setContextMenu(new ContextMenu(lockGamesItem, unlockGamesItem, ignoreItem));
        
        matchedNameClearButton.setOnAction((e) -> {
            clearCurrentGameFields();
//            int index = getCurrentSettings().games.indexOf(gamesListView.getSelectionModel().getSelectedItem());
//            getCurrentSettings().games.get(index).matchedName = null;
//            getCurrentSettings().games.get(index).metadata = null;
//            getCurrentSettings().games.get(index).strength = Game.MatchStrength.LOCKED;
            currentGame.matchedName = null;
            currentGame.metadata = null;
            currentGame.strength = Game.MatchStrength.LOCKED;
//            lockMatchedNameCheckBox.setSelected(true);
            loadCurrentGameFields(currentGame);
            gamesListView.refresh();
        });
        
        matchedNameBrowseButton.setOnAction((e) -> {
            if(currentGame != null) {
                SingleGameDownloadDialog d = new SingleGameDownloadDialog(getCurrentSettings().scrapeAs);
                d.showAndWait();
                loadCurrentGameFields(currentGame);
                gamesListView.refresh();
            }
        });
        
        lockMatchedNameCheckBox.setOnAction((e) -> {
            if(lockMatchedNameCheckBox.isSelected()) {
                currentGame.strength = Game.MatchStrength.LOCKED;
            }
            else {
                if(currentGame.matchedName == null) {
                    currentGame.strength = Game.MatchStrength.NO_MATCH;
                }
                else {
                    currentGame.strength = Game.MatchStrength.BEST_GUESS;
                }
            }
            gamesListView.refresh();
        });
        lockNameCheckBox.setOnAction((e) ->         lockMetaField(metaNameField, MetaDataId.NAME, currentGame.metadata, lockNameCheckBox.isSelected()));
        lockDescCheckBox.setOnAction((e) ->         lockMetaField(metaDescArea, MetaDataId.DESC, currentGame.metadata, lockDescCheckBox.isSelected()));
        lockDeveloperCheckBox.setOnAction((e) ->    lockMetaField(metaDeveloperField, MetaDataId.DEVELOPER, currentGame.metadata, lockDeveloperCheckBox.isSelected()));
        lockGenreCheckBox.setOnAction((e) ->        lockMetaField(metaGenreField, MetaDataId.GENRE, currentGame.metadata, lockGenreCheckBox.isSelected()));
        lockPublisherCheckBox.setOnAction((e) ->    lockMetaField(metaPublisherField, MetaDataId.PUBLISHER, currentGame.metadata, lockPublisherCheckBox.isSelected()));
        lockRatingCheckBox.setOnAction((e) ->       lockMetaField(metaRatingField, MetaDataId.RATING, currentGame.metadata, lockRatingCheckBox.isSelected()));
        lockReleaseDateCheckBox.setOnAction((e) ->  lockMetaField(metaReleaseDateField, MetaDataId.RELEASE_DATE, currentGame.metadata, lockReleaseDateCheckBox.isSelected()));
        lockPlayersCheckBox.setOnAction((e) ->      lockMetaField(playersField, MetaDataId.PLAYERS, currentGame.metadata, lockPlayersCheckBox.isSelected()));
        lockImagesCheckBox.setOnAction((e) -> {     currentGame.metadata.lockImages = lockImagesCheckBox.isSelected(); });
//        lockMatchedNameCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) -> {
//            if(newValue) {
//                currentGame.strength = Game.MatchStrength.LOCKED;
//            }
//            else {
//                if(currentGame.matchedName == null) {
//                    currentGame.strength = Game.MatchStrength.NO_MATCH;
//                }
//                else {
//                    currentGame.strength = Game.MatchStrength.BEST_GUESS;
//                }
//            }
//            gamesListView.refresh();
//        });
//        lockNameCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->         lockMetaField(metaNameField, MetaDataId.NAME, currentGame.metadata, newValue));
//        lockDescCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->         lockMetaField(metaDescArea, MetaDataId.DESC, currentGame.metadata, newValue));
//        lockDeveloperCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->    lockMetaField(metaDeveloperField, MetaDataId.DEVELOPER, currentGame.metadata, newValue));
//        lockGenreCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->        lockMetaField(metaGenreField, MetaDataId.GENRE, currentGame.metadata, newValue));
//        lockPublisherCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->    lockMetaField(metaPublisherField, MetaDataId.PUBLISHER, currentGame.metadata, newValue));
//        lockRatingCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->       lockMetaField(metaRatingField, MetaDataId.RATING, currentGame.metadata, newValue));
//        lockReleaseDateCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->  lockMetaField(metaReleaseDateField, MetaDataId.RELEASE_DATE, currentGame.metadata, newValue));
//        lockPlayersCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) ->      lockMetaField(playersField, MetaDataId.PLAYERS, currentGame.metadata, newValue));

        VBox metaBox = new VBox();
        metaBox.getChildren().addAll(
                createMetaFieldPane("Matched Game:", matchedNameField, matchedNameClearButton, matchedNameBrowseButton, lockMatchedNameCheckBox),
                createMetaFieldPane("Name:", metaNameField, lockNameCheckBox),
                createMetaFieldPane("Description:", metaDescArea, lockDescCheckBox),
                createMetaFieldPane("Rating:", metaRatingField, lockRatingCheckBox),
                createMetaFieldPane("Relase Date:", metaReleaseDateField, lockReleaseDateCheckBox),
                createMetaFieldPane("Developer:", metaDeveloperField, lockDeveloperCheckBox),
                createMetaFieldPane("Publisher:", metaPublisherField, lockPublisherCheckBox),
                createMetaFieldPane("Genre:", metaGenreField, lockGenreCheckBox),
                createMetaFieldPane("Players:", playersField, lockPlayersCheckBox)
        );
        
        imagesScroll.setPrefSize(200., 475.);
        
        VBox imagesBox = new VBox();
        imagesBox.setPadding(new Insets(7.));
        imagesBox.setSpacing(7.);
        imagesBox.getChildren().addAll(lockImagesCheckBox, imagesScroll);
        
        gamesPane.setPadding(new Insets(7.));
        gamesPane.setLeft(gamesListView);
        gamesPane.setCenter(metaBox);
        gamesPane.setRight(imagesBox);
        
        gamesTab.setContent(gamesPane);
        gamesTab.setClosable(false);
        gamesTab.setDisable(true);
        // End Games Tab
        
        scanButton.setOnAction((ActionEvent e) -> {
            FileSystem fs = FileSystems.getDefault();
            Path gamesPath = fs.getPath(gameSourceField.getText());
            if(Files.exists(gamesPath)) {
                tabPane.getSelectionModel().select(gamesTab);
                
                ScanTask scanner = new ScanTask(gamesPath);
                ScanProgressDialog scanProgressDialog = new ScanProgressDialog(scanner);
                
                Thread t = new Thread(scanner);
                t.setDaemon(true);
                t.start();
                scanProgressDialog.showAndWait();
            }
        });
        
        saveButton.setOnAction((e) -> {
            try {
                writeSettings();
                writeGameData();
            }
            catch(IOException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                
                new Alert(Alert.AlertType.ERROR, "Error saving settings.", ButtonType.OK).showAndWait();
            }
        });
        
        outputToGamelistButton.setOnAction((e) -> {
//            Collections.sort(getCurrentSettings().games);
            Collections.sort(getSystemGameData());
            new ESOutput().output(
//                    getCurrentSettings().games,
                    getSystemGameData(),
                    System.getProperty("user.home") + File.separator + ".scraperfx" + File.separator + "gamelists" + File.separator + getCurrentSettings().name,
                    getCurrentSettings().romsDir + File.separator + "images",
                    getCurrentSettings().scrapeAsArcade);
//            try {
//                FileSystem fs = FileSystems.getDefault();
//                Path outputDir = fs.getPath(System.getProperty("user.home"), ".scraperfx", "gamelists", getCurrentSettings().name);
//                Files.createDirectories(outputDir);
//                
//                File outputFile = new File(outputDir.toString() + File.separator + "gamelist.xml");
//                outputFile.createNewFile();
//                
//                try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
//                    writer.append("<gameList>\n");
//                    
//                    for(Game g : getCurrentSettings().games) {
//                        System.out.println("writing for " + g.fileName);
//                        writer.append("\t<game>\n");
//                        
//                        writer.append("\t\t<path>./" + g.fileName + "</path>\n");
//                        writer.append("\t\t<filename>" + g.fileName + "</filename>\n");
//                        if(g.metadata.metaName != null)         writer.append("\t\t<name>" + g.metadata.metaName + "</name>\n");
//                        if(g.metadata.metaDesc != null)         writer.append("\t\t<desc>" + g.metadata.metaDesc + "</desc>\n");
//                        if(g.metadata.metaReleaseDate != null)  writer.append("\t\t<releasedate>" + (g.metadata.metaReleaseDate.substring(6) + g.metadata.metaReleaseDate.substring(0, 2) + g.metadata.metaReleaseDate.substring(3, 5) + "T000000") + "</releasedate>\n");
//                        if(g.metadata.metaDeveloper != null)    writer.append("\t\t<developer>" + g.metadata.metaDeveloper + "</developer>\n");
//                        if(g.metadata.metaPublisher != null)    writer.append("\t\t<publisher>" + g.metadata.metaPublisher + "</publisher>\n");
//                        if(g.metadata.metaGenre != null)        writer.append("\t\t<genre>" + g.metadata.metaGenre + "</genre>\n");
//                        if(g.metadata.players != null)          writer.append("\t\t<players>" + g.metadata.players + "</players>\n");
//                        
//                        if(g.metadata.esimage != null && writeImageToFile(fs.getPath(getCurrentSettings().romsDir, "images"), g.fileName + "-image", g.metadata.esimage)) {
//                            writer.append("\t\t<image>./images/" + g.fileName + "-image.png</image>\n");
//                        }
//                        for(scraperfx.settings.Image img : g.metadata.images) {
//                            if(writeImageToFile(fs.getPath(getCurrentSettings().romsDir, "images"), g.fileName + "-" + img.type, img.path)) {
//                                writer.append("\t\t<" + img.type + ">./images/" + g.fileName + "-" + img.type + ".png</" + img.type + ">\n");
//                            }
//                        }
//                        
//                        writer.append("\t</game>\n");
//                    }
//                    
//                    writer.append("</gameList>\n");
//                    System.out.println("completed gamelist.xml");
//                }
//            ESOutput output = new ESOutput();
//            
//            for(Game g : gamesListView.getItems()) {
//                ESGame gameout = new ESGame();
//                
//                if(scrapeAsConsoleButton.isSelected()) {
//                    gameout.desc = g.metadata.metaDesc;
//                    gameout.developer = g.metadata.metaDeveloper;
//                    gameout.genre = g.metadata.metaGenre.toString();
//                    gameout.name = g.metadata.metaName;
//                    gameout.filename = g.fileName;
//                    gameout.path = "./" + g.fileName;
//                    gameout.publisher = g.metadata.metaPublisher;
//                    gameout.rating = g.metadata.metaRating;
//                    
//                    String date = g.metadata.metaReleaseDate;
//                    gameout.releasedate = date.substring(6) + date.substring(0, 2) + date.substring(3, 5) + "T000000";
//                    
//                    try {
//                        Path p = new File(getCurrentSettings().romsDir + File.separator + "images").toPath();
//                        Files.createDirectories(p);
//                        if(writeImageToFile(p, g.fileName + ".png", g.getImageUrlByType("box-front"))) {
//                            gameout.boxartFront = "./images/" + g.fileName + ".png";
//                        }
//                    }
//                    catch(IOException ex) {
//                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    
//                }
//                else {
//                }                            
//            }
//            }
//            catch(IOException ex) {
//                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
//            }
        });
        
        deleteSystemButton.setOnAction((e) -> deleteSystemButtonOnActionPerformed() );
        
        FlowPane settingsButtonPane = new FlowPane(saveButton, deleteSystemButton, scanButton, outputToGamelistButton);
        settingsButtonPane.setHgap(7.);
        settingsButtonPane.setAlignment(Pos.CENTER);
        
        tabPane.getTabs().add(settingsTab);
        tabPane.getTabs().add(gamesTab);
//        tabPane.getTabs().add(outputTab);
        tabPane.getSelectionModel().select(0);
        
        BorderPane rightPane = new BorderPane();
        rightPane.setCenter(tabPane);
        rightPane.setBottom(settingsButtonPane);

        HBox mainBox = new HBox(box2, rightPane);
        mainBox.setSpacing(7.);
        
        StackPane root = new StackPane(mainBox);
        StackPane.setMargin(mainBox, new Insets(7.));
        
        rootScene = new Scene(root);

        try {
            readGameData();
            readSettings();
        }
        catch(IOException ex) {
            Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        primaryStage.setTitle("ScraperFX");
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
    
    private final String[] numbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };
    private final String[] romans = { "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii", "xiii", "xiv", "xv" };
    
    private boolean isNumeral(String s) {
        return Arrays.asList(numbers).contains(s) || Arrays.asList(romans).contains(s);
    }
    
    private String asDigit(String s) {
        if(Arrays.asList(numbers).contains(s)) {
            return s;
        }
        return numbers[Arrays.asList(romans).indexOf(s)];
    }
    
    private String asRoman(String s) {
        if(Arrays.asList(romans).contains(s)) {
            return s;
        }
        return romans[Arrays.asList(numbers).indexOf(s)];
    }
    
    public static Stage getPrimaryStage() {
        return mainStage;
    }
    
    private void lockMetaField(TextInputControl textControl, MetaDataId id, MetaData data, Boolean lock) {
        if(lock) {
            textControl.setEditable(false);
            data.updateMetaData(id, textControl.getText());
            data.lockMetaData(id, true);
        }
        else {
            textControl.setEditable(true);
            data.lockMetaData(id, false);
        }
    }
    
//    private void lockMetaImages() {
//            data.lockMetaData(MetaData.MetaDataId.IMAGES, true);
//    }
    
    private Image getImageFromURL(String url) throws MalformedURLException, IOException {
        int retry = 0;
        while(retry < 3) {
            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedImage img = ImageIO.read(connection.getInputStream());
                return SwingFXUtils.toFXImage(img, null);
            }
            catch(MalformedURLException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch(IOException ex) {
//                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                if(++retry < 3) {
                    System.out.println("Error retrieving image from " + url + ". Retrying...");
                }
            }
        }
        return null;
    }
    
    public static boolean writeImageToFile(Path path, String imageFileName, String imageOutputType, String url) throws MalformedURLException, IOException {
        if(url == null) {
            return false;
        }
        
        Files.createDirectories(path);
                    
        File outputFile = new File(path.toString() + File.separator + imageFileName + "." + imageOutputType);
//        System.out.println("writing image " + url + " to " + outputFile.getPath());
        if(outputFile.exists()) {
            return true;
        }
        else {
            int retry = 0;
            while(retry < 3) {
                try {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedImage img = ImageIO.read(connection.getInputStream());
                    
                    if(img.getWidth() <= 640) {
                        ImageIO.write(img, imageOutputType, outputFile);
                    }
                    else {
                        java.awt.Image scaled = img.getScaledInstance(640, -1, java.awt.Image.SCALE_SMOOTH);
                        BufferedImage scaledBuff = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
                        scaledBuff.getGraphics().drawImage(scaled, 0, 0, null);
                        ImageIO.write(scaledBuff, imageOutputType, outputFile);
                    }
                    return true;
    //                return SwingFXUtils.toFXImage(img, null);
                }
                catch(MalformedURLException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                catch(IOException ex) {
    //                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
    //                if(++retry == 3) {
    //                    throw ex;
    //                }
                    if(++retry < 3) {
                        System.out.println("Error retrieving image from " + url + ". Retrying...");
                    }
                }
            }
        }
//        return null;
        return false;
    }
    
    private Pane createMetaFieldPane(String labelStr, TextInputControl field, CheckBox lockCheckBox) {
        return createMetaFieldPane(labelStr, field, null, null, lockCheckBox);
    }
    
//    private Pane createMetaFieldPane(String labelStr, TextInputControl field, Button clearButton, Button browseButton) {
//        return createMetaFieldPane(labelStr, field, clearButton, browseButton, null);
//    }
    
    private Pane createMetaFieldPane(String labelStr, TextInputControl field, Button clearButton, Button browseButton, CheckBox lockCheckBox) {
        Label label = new Label(labelStr);
        label.setPadding(new Insets(7., 7., 0., 7.));
//        field.setEditable(false);
        if(field instanceof TextField) {
            field.setPrefWidth(275.);
        }
        else if(field instanceof TextArea) {
            field.setPrefSize(275., 125.);
        }
        
        FlowPane pane = new FlowPane();
        pane.getChildren().add(field);
        if(browseButton != null) {
            pane.getChildren().add(browseButton);
        }
        if(clearButton != null) {
            pane.getChildren().add(clearButton);
        }
        if(lockCheckBox != null) {
            pane.getChildren().add(lockCheckBox);
        }
        
        pane.setHgap(7.);
        pane.setPadding(new Insets(0., 7., 0., 7.));
        
        VBox box = new VBox(label, pane);
        return box;
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field) {
        return createBrowseFieldPane(labelStr, field, null, null);
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field, Button browseButton) {
        return createBrowseFieldPane(labelStr, field, null, browseButton);
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field, Button clearButton, Button browseButton) {
        Label label = new Label(labelStr);
        label.setPadding(new Insets(7., 7., 0., 7.));
        field.setPrefWidth(275.);
        
        FlowPane pane = new FlowPane();
        pane.getChildren().add(field);
        if(browseButton != null) {
            pane.getChildren().add(browseButton);
        }
        if(clearButton != null) {
            pane.getChildren().add(clearButton);
            
            clearButton.setOnAction((e) -> {
                field.clear();
            });
        }
        
        pane.setHgap(7.);
        pane.setPadding(new Insets(0., 7., 0., 7.));
        
        VBox box = new VBox(label, pane);
        return box;
    }
    
    public static scraperfx.settings.System getCurrentSettings() {
        return getSettings(currentSystemName);
    }
    
    private static scraperfx.settings.System getSettings(String systemName) {
        return settings.get(systemName);
    }
    
    private List<Game> getSystemGameData() {
        return getSystemGameData(currentSystemName);
    }
    
    private List<Game> getSystemGameData(String systemName) {
        if(gamedata == null) {
            return null;
        }
        
        if(gamedata.getSystemData(systemName) == null) {
            gamedata.gamelist.add(new GameList(systemName));
        }
        return gamedata.getSystemData(systemName);
    }
    
    //gets the reference to game out of the gamedata that this game is referring to... if that makes sense!
    private Game getGame(Game g) {
        List<Game> games = getSystemGameData();
        if(games == null) {
            return null;
        }
        int index = games.indexOf(g);
        if(index == -1) {
            return null;
        }
        return games.get(index);
    }
    
//    private void removeGame(Game g) {
//        List<Game> games = getSystemGameData();
//        if(games != null) {
//            games.remove(g);
//        }
//    }
//    
//    private void addGame(Game g) {
//        List<Game> games = getSystemGameData();
////        if(games == null) {
////            gamedata.gamelist.add(new GameList(currentSystemName));
////            
////        }
//        games.add(g);
//    }
    
    private void clearCurrentGameFields() {
        matchedNameField.clear();
        metaNameField.clear();
        metaDescArea.clear();
        metaRatingField.clear();
        metaReleaseDateField.clear();
        metaDeveloperField.clear();
        metaPublisherField.clear();
        metaGenreField.clear();
        playersField.clear();
        imagesScroll.setContent(null);
        
        lockMatchedNameCheckBox.setSelected(false);
        lockNameCheckBox.setSelected(false);
        lockDescCheckBox.setSelected(false);
        lockRatingCheckBox.setSelected(false);
        lockReleaseDateCheckBox.setSelected(false);
        lockDeveloperCheckBox.setSelected(false);
        lockPublisherCheckBox.setSelected(false);
        lockGenreCheckBox.setSelected(false);
        lockPlayersCheckBox.setSelected(false);
    }
    
    private void loadCurrentGameFields(Game g) {
        clearCurrentGameFields();
        
        matchedNameField.setText(g.matchedName);
        lockMatchedNameCheckBox.setSelected(g.strength == Game.MatchStrength.LOCKED);
        if(g.metadata != null) {
            metaNameField.setText(g.metadata.metaName);
            metaDescArea.setText(g.metadata.metaDesc);
            metaRatingField.setText(g.metadata.metaRating);
            metaReleaseDateField.setText(g.metadata.metaReleaseDate);
            metaDeveloperField.setText(g.metadata.metaDeveloper);
            metaPublisherField.setText(g.metadata.metaPublisher);
            metaGenreField.setText((g.metadata.metaGenre == null ? "" : g.metadata.metaGenre));
            playersField.setText(g.metadata.players);
            
            lockNameCheckBox.setSelected(g.metadata.lockName);
            metaNameField.setEditable(!g.metadata.lockName);
            lockDescCheckBox.setSelected(g.metadata.lockDesc);
            metaDescArea.setEditable(!g.metadata.lockDesc);
            lockRatingCheckBox.setSelected(g.metadata.lockRating);
            metaRatingField.setEditable(!g.metadata.lockRating);
            lockReleaseDateCheckBox.setSelected(g.metadata.lockReleasedate);
            metaReleaseDateField.setEditable(!g.metadata.lockReleasedate);
            lockDeveloperCheckBox.setSelected(g.metadata.lockDeveloper);
            metaDeveloperField.setEditable(!g.metadata.lockDeveloper);
            lockPublisherCheckBox.setSelected(g.metadata.lockPublisher);
            metaPublisherField.setEditable(!g.metadata.lockPublisher);
            lockGenreCheckBox.setSelected(g.metadata.lockGenre);
            metaGenreField.setEditable(!g.metadata.lockGenre);
            lockPlayersCheckBox.setSelected(g.metadata.lockPlayers);
            playersField.setEditable(!g.metadata.lockPlayers);
            
            lockImagesCheckBox.setSelected(g.metadata.lockImages);

            if(!isScanning.get()) {
                Platform.runLater(() -> {
                    imageTaskList.stream().forEach((task) -> {
                        task.cancel();
                    });
                    imageTaskList.clear();

                    if(g.metadata != null && g.metadata.images != null && !g.metadata.images.isEmpty()) {
//                        VBox box = new VBox();
//                        box.setSpacing(7.);
//                        box.setPadding(new Insets(7.));
                        MetaImageViewBox box = new MetaImageViewBox();

                        for(scraperfx.settings.Image image : g.metadata.images) {
//                            ImageView view = new ImageView(new Image("scraperfx/images/loading.gif"));
//                            view.setFitHeight(175.);
//                            view.setPreserveRatio(true);
//
//                            box.getChildren().add(new Label(image.type));
//                            box.getChildren().add(view);
                            if(image != null) {
                                MetaImageView metaImage = new MetaImageView(image);
                                box.addView(metaImage);
                            
                                ImageLoadingTask task = new ImageLoadingTask(metaImage, image);
                                imageTaskList.add(task);

                                Thread t = new Thread(task);
                                t.setDaemon(true);
                                t.start();
                            }
                        }

                        imagesScroll.setContent(box);
                    }
                });
            }
        }
    }
    
    private void scrapeAsArcadeSetup(boolean b) {
        if(b) {
            getCurrentSettings().scrapeAsArcade = true;
            scrapeAsArcadeButton.setSelected(true);
            consoleSelectComboBox.setDisable(true);
            matchedNameBrowseButton.setDisable(true);
        }
        else {
            getCurrentSettings().scrapeAsArcade = false;
            scrapeAsConsoleButton.setSelected(true);
            consoleSelectComboBox.setDisable(false);
            matchedNameBrowseButton.setDisable(false);
        }
    }
    
    private void loadCurrentSettings() {
        scrapeAsArcadeSetup(getCurrentSettings().scrapeAsArcade);
        consoleSelectComboBox.getSelectionModel().select(getCurrentSettings().scrapeAs);
        gameSourceField.setText(getCurrentSettings().romsDir);
//        imageOuputField.setText(getCurrentSettings().imageDir);
//        defaultBoxArtField.setText(getCurrentSettings().defaultBoxArt);
//        defaultScreenshotField.setText(getCurrentSettings().defaultScreenshot);
        filenameRegexField.setText(getCurrentSettings().substringRegex);
        ignoreRegexField.setText(getCurrentSettings().ignoreRegex);
        unmatchedOnlyCheckBox.setSelected(getCurrentSettings().unmatchedOnly);
        
        gamesListView.getItems().clear();
//        getCurrentSettings().games.stream().forEach((g) -> {
        if(getSystemGameData(getCurrentSettings().name) != null) {
            getSystemGameData(getCurrentSettings().name).stream().forEach((g) -> {
                gamesListView.getItems().add(g);
            });
        
            ObservableList<Game> list = gamesListView.getItems();
            Collections.sort(list);
            gamesListView.setItems(list);
        }
    }
    
    private void loadSystemList(String selectAfter) {
        systemList.getItems().clear();
        settings.systems.stream().forEach((sys) -> {
            systemList.getItems().add(sys.name);
        });
        if(selectAfter == null) {
            systemList.getSelectionModel().select(0);
        }
        else {
            systemList.getSelectionModel().select(selectAfter);
        }
        tabPane.getSelectionModel().select(settingsTab);
    }
    
    private void clearSettingsTab() {
        gameSourceField.clear();
        filenameRegexField.clear();
        ignoreRegexField.clear();
//        imageOuputField.clear();
        unmatchedOnlyCheckBox.setSelected(false);
    }
    
    private void disableTabs() {
        settingsTab.setDisable(true);
        gamesTab.setDisable(true);
    }
    
    private void enableTabs() {
        settingsTab.setDisable(false);
        gamesTab.setDisable(false);
    }
    
    private void addSystemButtonActionPerformed() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a name for your system.\n\nIf you are using EmulationStation, it is\nsuggested you name your systems to match\nyour rom folder names.");
        dialog.setContentText("Name:");
        dialog.setTitle("Name System");
        Optional<String> result = dialog.showAndWait();
        
        if(result.isPresent() && result.get().trim().length() > 0) {
            String name = result.get();
            if(systemList.getItems().contains(name)) {
                new Alert(Alert.AlertType.ERROR, "A system already exists with that name.", ButtonType.OK).showAndWait();
            }
            else {
                clearSettingsTab();
                settings.systems.add(new scraperfx.settings.System(name));
                loadSystemList(name);
            }
        }
    }
    
    private void deleteSystemButtonOnActionPerformed() {
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove '" + currentSystemName + "' from your systems?", ButtonType.YES, ButtonType.CANCEL).showAndWait();

        if(result.isPresent() && result.get() == ButtonType.YES) {
            gamedata.remove(currentSystemName);
            settings.systems.remove(getCurrentSettings());
            systemList.getItems().remove(currentSystemName);

            if(systemList.getItems().isEmpty()) {
                disableTabs();
                clearSettingsTab();
            }
        }
    }
    
    private void readSettings() throws IOException {
        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "scraperfx.conf");
        if(Files.exists(settingsPath)) {
            Xmappr xm = new Xmappr(SystemSettings.class);
            
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(settingsPath.toFile()));
            final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetDecoder))) {
                settings = (SystemSettings)xm.fromXML(reader);
                loadSystemList(null);
            }
            catch(IOException ex) {
                throw ex;
            }
        }
        else {
            settings = new SystemSettings();
        }
    }
    
    private void readGameData() throws IOException {
        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "gamedata.conf");
        if(Files.exists(settingsPath)) {
            Xmappr xm = new Xmappr(GameData.class);
            
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(settingsPath.toFile()));
            final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetDecoder))) {
                gamedata = (GameData)xm.fromXML(reader);
            }
            catch(IOException ex) {
                throw ex;
            }
        }
        else {
            gamedata = new GameData();
        }
    }
    
    private void writeSettings() throws IOException {
        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "scraperfx.conf");
        if(!Files.exists(settingsPath)) {
            Files.createDirectories(settingsPath.getParent());
            Files.createFile(settingsPath);
        }
        
        Xmappr xm = new Xmappr(SystemSettings.class);
        xm.setPrettyPrint(true);
        try(BufferedWriter writer = Files.newBufferedWriter(settingsPath)) {
            xm.toXML(settings, writer);
        }
        catch(IOException ex) {
            throw ex;
        }
    }
    
    private void writeGameData() throws IOException {
        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "gamedata.conf");
        if(!Files.exists(settingsPath)) {
            Files.createDirectories(settingsPath.getParent());
            Files.createFile(settingsPath);
        }
        
        Xmappr xm = new Xmappr(GameData.class);
        xm.setPrettyPrint(true);
        try(BufferedWriter writer = Files.newBufferedWriter(settingsPath)) {
            xm.toXML(gamedata, writer);
        }
        catch(IOException ex) {
            throw ex;
        }
    }
    
    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch(NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private class MetaImageViewBox extends VBox {
        private final List<MetaImageView> views;
        
        public MetaImageViewBox() {
            super();
            setSpacing(7.);
            setPadding(new Insets(7.));
            
            views = new ArrayList();
            
//            addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
//                System.out.println(e.getTarget());
//            });
//            setOnMouseClicked((e) -> {
//                EventTarget et = e.getTarget();
//                System.out.println(et);
//                if(et instanceof MetaImageView) {
//                    MetaImageView clickedView = (MetaImageView)e.getTarget();
//                    if(clickedView.getImage() != null) {
//                    }
//                }
//            });
        }
        
        public void selectImage(scraperfx.settings.Image image) {
            currentGame.metadata.selectImage(image);
            for(MetaImageView view : views) {
                if(view.getImage().selected) {
                    view.setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
                }
                else {
                    view.setBorder(null);
                }
            }
        }
        
        public void addView(MetaImageView view) {
            getChildren().add(view);
            views.add(view);
        }
    }
    
    private class MetaImageView extends VBox {
        private final ImageView view;
        private final scraperfx.settings.Image image;
        
        public MetaImageView(scraperfx.settings.Image image) {
            super();
            this.image = image;
            view = new ImageView("scraperfx/images/loading.gif");
            view.setFitHeight(175.);
            view.setPreserveRatio(true);

            getChildren().add(new Label(image.type));
            getChildren().add(view);

            if(image.selected) {
                setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
            }
            addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                ((MetaImageViewBox)getParent()).selectImage(image);
            });
        }
        
//        public void setImage(scraperfx.settings.Image image) {
//            this.image = image;
//        }
        
        public scraperfx.settings.Image getImage() {
            return image;
        }
        
        public ImageView getView() {
            return view;
        }
        
//        public String getType() {
//            return type;
//        }
    }
    
    private class ImageLoadingTask extends Task<Void> {
        private final MetaImageView view;
        private final scraperfx.settings.Image scraperImage;
        
        private Image image;

        public ImageLoadingTask(final MetaImageView view, scraperfx.settings.Image scraperImage) {
            this.view = view;
            this.scraperImage = scraperImage;
        }
        
        @Override
        protected Void call() {
            try {
//                System.out.println("loading image " + scraperImage.path);
                image = getImageFromURL(scraperImage.path);
            }
            catch(IOException ex) {
                cancel();
//                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        protected void succeeded() {
//            System.out.println("success");
            view.getView().setImage(image);
        }

        @Override
        protected void cancelled() {
            view.getView().setImage(null);
        }
    }
    
    private class ScanTask extends Task<Void> {
        private final Path gamesPath;
        
        public ScanTask(Path gamesPath) {
            this.gamesPath = gamesPath;
        }
        
        @Override
        protected Void call() throws Exception {
            isScanning.set(true);
            try {
                DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
                try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                    long totalFiles = Files.list(gamesPath).count();
                    long fileCount = 0;
                    for(Path p : stream) {
                        if(isCancelled()) {
                            break;
                        }

                        String filename = p.getFileName().toString();
//                        Game localGame = new Game(filename);
//                        Game g = getGame(localGame);
                        Game localGame = getGame(new Game(filename));
                        if(localGame == null) {
                            localGame = new Game(filename);
                        }
                        if(localGame.strength != Game.MatchStrength.IGNORE) {
                            boolean ignore = false;
                            if(getCurrentSettings().ignoreRegex != null && !"".equals(getCurrentSettings().ignoreRegex)) {
                                Pattern ignorePattern = Pattern.compile(".*" + getCurrentSettings().ignoreRegex + ".*");
                                Matcher ignoreMatcher = ignorePattern.matcher(filename);
    //                            System.out.println(ignorePattern);
                                if(ignoreMatcher.matches()) {
    //                                System.out.println("ignoring " + filename);
                                    localGame.matchedName = null;
                                    localGame.strength = Game.MatchStrength.IGNORE;
                                    ignore = true;
                                }
                            }

                            if(!ignore) {
                                if(unmatchedOnlyCheckBox.isSelected()) {
        //                            int indexOfGame = getCurrentSettings().games.indexOf(localGame);
        //                            if(indexOfGame != -1 && getCurrentSettings().games.get(indexOfGame).strength != Game.MatchStrength.NO_MATCH) {

//                                    if(g != null && g.strength != Game.MatchStrength.NO_MATCH) {
                                    if(localGame.matchedName != null) {
                                        // this game is already matched and we only want unmatched games.
                                        updateMessage("Skipping " + filename + ". Already matched. Scanning unmatched only.");
                                        updateProgress(++fileCount, totalFiles);
                                        // without sleeping, subsequent scans with cached data went so fast
                                        // i thought it was a bug. turns out the scan process is very fast,
                                        // it's downloading the data that is slow.
                                        try {
                                            Thread.sleep(100);
                                        }
                                        catch(InterruptedException interrupted) {
                                            if(isCancelled()) {
                                                break;
                                            }
                                        }
                                        continue;
                                    }
                                }

                                String noExtName = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();
                                if(scrapeTypeGroup.getSelectedToggle() == scrapeAsArcadeButton) {
                                    localGame.matchedName = noExtName;
                                    localGame.metadata = DataSourceFactory.getDataSource(SourceAgent.ARCADE).getMetaData(null, localGame);
                                    if(localGame.metadata == null) {
                                        localGame.matchedName = null;
                                        updateMessage("Could not match " + filename + " to a game.");
                                    }
                                    else {
                                        localGame.strength = Game.MatchStrength.STRONG;
                                        updateMessage("Matched " + filename + " to " + localGame.metadata.metaName + ".");
                                    }
                                }
                                else {
        //                            int indexOfGame = getCurrentSettings().games.indexOf(localGame);
        //                            if(indexOfGame != -1 && getCurrentSettings().games.get(indexOfGame).strength == Game.MatchStrength.LOCKED) {
        //                                localGame = getCurrentSettings().games.get(indexOfGame);
//                                    Game g = getGame(localGame);
//                                    if(g != null && g.strength == Game.MatchStrength.LOCKED) {
//                                        localGame = g;
//                                    }
//                                    else {
                                    if(localGame.strength != Game.MatchStrength.LOCKED) {
                                        if(getCurrentSettings().substringRegex != null && !"".equals(getCurrentSettings().substringRegex)) {
                                            Pattern pattern = Pattern.compile(".*" + getCurrentSettings().substringRegex + ".*");
                                            Matcher m = pattern.matcher(noExtName);
                                            if(m.matches()) {
                                                for(int i = 1; i <= m.groupCount(); i++) {
                                                    noExtName = noExtName.replaceAll(m.group(i), "");
        //                                          noExtName = noExtName.replaceAll(getCurrentSettings().regex, "");
                                                }
                                            }
                                        }
        //                                noExtName = noExtName.replaceAll("'", "");
                                        noExtName = noExtName.replaceAll(" - ", " ");
                                        noExtName = noExtName.replaceAll("\\(.*\\)", "");
                                        noExtName = noExtName.replaceAll("\\[.*\\]", "");
                                        noExtName = noExtName.replaceAll(" and ", " ");
                                        noExtName = noExtName.replaceAll("\\p{Punct}", "");
        //                                noExtName = noExtName.replaceAll(":", "");
        //                                noExtName = noExtName.replaceAll("-", "");
        //                                noExtName = noExtName.replaceAll("\\.", "");
                                        noExtName = noExtName.trim();
        //                                System.out.println("file: " + noExtName);

                                        boolean hundredpercentmatch = false;
                                        List<String> hitNames = new ArrayList();
                                        List<Double> hitPercents = new ArrayList();
                                        List<Double> remoteHitPercents = new ArrayList();
                                        int mostPartsHit = 0;
                                        boolean hitPartIsNumber = false;

                                        List<String> allSysGames = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB).getSystemGameNames(getCurrentSettings().scrapeAs);
                                        for(String gameName : allSysGames) {
                                            String lowerCaseName = gameName.toLowerCase().replaceAll(" - ", " ");//gameName.toLowerCase().replaceAll("'", "");
                                            lowerCaseName = lowerCaseName.replaceAll("\\(.*\\)", "");
                                            lowerCaseName = lowerCaseName.replaceAll("\\[.*\\]", "");
                                            lowerCaseName = lowerCaseName.replaceAll(" and ", " ");
                                            lowerCaseName = lowerCaseName.replaceAll("\\p{Punct}", "");
        //                                    lowerCaseName = lowerCaseName.replaceAll(":", "");
        //                                    lowerCaseName = lowerCaseName.replaceAll(" - ", " ");
        //                                    lowerCaseName = lowerCaseName.replaceAll("-", "");
        //                                    lowerCaseName = lowerCaseName.replaceAll("\\.", "");
                                            lowerCaseName = lowerCaseName.trim();

                                            if(noExtName.equals(lowerCaseName)) {
                                                //100% match, so pretty sure, right?
                                                localGame.matchedName = gameName;// + " (FULL MATCH)";
                                                localGame.strength = Game.MatchStrength.STRONG;
                                                hundredpercentmatch = true;
                                                break;
                                            }
                                            else {
                                                //find based on parts from both local filename and database names.
                                                String[] localParts = noExtName.split("\\s");//"[\\s\\p{Punct}]");
                                                String[] split = lowerCaseName.split("\\s");//"[\\s\\p{Punct}]");

                                                int hits = 0;
                                                int hitLength = 0;
                                                for(int i = 0; i < split.length; i++) {
                                                    if(!"".equals(split[i])) {
                                                        for(int q = 0; q < localParts.length; q++) {
                                                            if(!"".equals(localParts[q])) {
                                                                if(isNumeral(split[i])) {// 1-15, either digits or roman.
                                                                    if(localParts[q].equals(asDigit(split[i])) ||
                                                                            localParts[q].equals(asRoman(split[i]))) {
                                                                        hits++;
                                                                        hitLength += split[i].length();
                                                                        hitPartIsNumber = true;//needed later if this is the only part hit.
                                                                        localParts[q] = "";//piece matched, clear it out to prevent rematching.
                                                                        break;
                                                                    }
                                                                }
                                                                else {
                                                                    if(isInteger(localParts[q]) || isInteger(split[i])) {
                                                                        // i don't want to do contains on numbers because weird things can happen
                                                                        // where a filename with a 000 in it (for whatever reason) will match
                                                                        // any game with say a 2000 in the title (happens a lot).
                                                                        if(localParts[q].equals(split[i])) {
                                                                            hits++;
                                                                            hitLength += split[i].length();
                                                                            localParts[q] = "";//piece matched, clear it out to prevent rematching.
                                                                            break;
                                                                        }
                                                                    }
                                                                    else if(localParts[q].contains(split[i])) {
                                                                        hits++;
                                                                        hitLength += split[i].length();
                                                                        localParts[q] = localParts[q].replace(split[i], "");//piece matched, clear it out to prevent rematching.
                                                                        break;
                                                                    }
                                                                    else if(split[i].contains(localParts[q])) {
                                                                        hits++;
                                                                        hitLength += localParts[q].length();
                                                                        localParts[q] = "";
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if(hits > 0) {
                                                    if(hits > mostPartsHit) {
                                                        mostPartsHit = hits;
                                                    }
                                                    hitNames.add(gameName);
                                                    hitPercents.add((double)hitLength / (double)noExtName.replaceAll("\\s", "").length());
        //                                            System.out.println(lowerCaseName + " " + (double)hitLength / (double)noExtName.replaceAll("\\s", "").length());
                                                    remoteHitPercents.add((double)hitLength / (double)lowerCaseName.length());
                                                }
                                            }
                                        }

                                        if(!hundredpercentmatch && !hitNames.isEmpty()) {
                                            if(mostPartsHit == 1 && hitPartIsNumber) {
                                                // i hope this will solve matching non-existing games with sequels of other games just because
                                                // they both have a number in them.

                                                // do nothing;
                                            }
                                            else {
                                                int index = -1;
                                                double highest = -1;
                                                List<String> tieNames = new ArrayList();

                                                for(int i = 0; i < hitPercents.size(); i++) {
                                                    if(hitPercents.get(i) > highest) {
                                                        tieNames.clear();
                                                        tieNames.add(hitNames.get(i));
                                                        highest = hitPercents.get(i);
                                                        index = i;
                                                    }
                                                    else if(hitPercents.get(i) == highest) {
                                                        tieNames.add(hitNames.get(i));
                                                    }
                                                }

                                                if(highest < .25) {
                                                    localGame.matchedName = null;
                                                    localGame.strength = Game.MatchStrength.LOW_PERCENTAGE;
                                                }
                                                else if(tieNames.size() < 2) {
                                                    localGame.matchedName = hitNames.get(index);// + " (" + (int)(100 * hitPercents.get(index)) + "%) " + "(r=" + (int)(100 * remoteHitPercents.get(index)) + ")";
                                                    localGame.strength = Game.MatchStrength.BEST_GUESS;
                                                }
                                                else {
                                                    //choose tiebreaker by percent used of remote name.
                                                    index = -1;
                                                    highest = -1;
                                                    for(int q = 0; q < tieNames.size(); q++) {
                                                        if(remoteHitPercents.get(hitNames.indexOf(tieNames.get(q))) > highest) {
                                                            highest = remoteHitPercents.get(hitNames.indexOf(tieNames.get(q)));
                                                            index = q;
                                                        }
                                                    }

                                                    localGame.matchedName = tieNames.get(index);// + " via tiebreak (" + (int)(100 * hitPercents.get(hitNames.indexOf(tieNames.get(index)))) + "%) " + "(r=" + (int)(100 * remoteHitPercents.get(hitNames.indexOf(tieNames.get(index)))) + ")";
                                                    localGame.strength = Game.MatchStrength.TIE_BREAKER;
                                                }
                                            }
                                        }
                                    }

                                    if(localGame.matchedName == null) {
                                        updateMessage("Could not match " + filename + " to a game.");
                                    }
                                    else {
                                        //matched a game, get the rest of the data.
                                        localGame.metadata = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB).getMetaData(getCurrentSettings().scrapeAs, localGame);
                                        if(localGame.metadata == null) {
                                            //error occurred while getting metadata.
                                            updateMessage("Error connecting to thegamesdb.net. Please try again.");
                                        }
                                        updateMessage("Matched " + filename + " to " + localGame.matchedName + ".");
                                    }
                                }
                            }
                        }

                        updateProgress(++fileCount, totalFiles);

//                        getCurrentSettings().games.remove(localGame);
//                        getCurrentSettings().games.add(localGame);
                        getSystemGameData().remove(localGame);
                        getSystemGameData().add(localGame);
//                        removeGame(localGame);
//                        addGame(localGame);
                        
                        Game g = new Game(localGame);
                        Platform.runLater(() -> {
                            gamesListView.getItems().remove(g);
                            gamesListView.getItems().add(g);
                            gamesListView.getSelectionModel().selectLast();//temporary?
                        });

                        // without sleeping, subsequent scans with cached data went so fast
                        // i thought it was a bug. turns out the scan process is very fast,
                        // it's downloading the data that is slow.
                        try {
                            Thread.sleep(60);
                        }
                        catch(InterruptedException interrupted) {
                            if(isCancelled()) {
                                break;
                            }
                        }
                    }
                    isScanning.set(false);
                }
                catch(IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                Platform.runLater(() -> {
                    ObservableList<Game> list = gamesListView.getItems();
                    Collections.sort(list);
                    gamesListView.setItems(list);
                });
                if(isScanning.get()) {
                    isScanning.set(false);
                    cancel();
                }
            }
            
            return null;
        }
    }
    
    private class SingleGameDownloadDialog extends Stage {
        private final ComboBox<String> selectGameBox;
        private final Button okButton;
        private final Button cancelButton;
        private final AtomicBoolean working;
        
        private final String systemName;
        
        public SingleGameDownloadDialog(String systemName) {
            super();
            this.systemName = systemName;
        
            working = new AtomicBoolean(false);
            
            selectGameBox = new ComboBox();
            selectGameBox.getItems().add("Loading Games List...");
            selectGameBox.getSelectionModel().select(0);
            
            okButton = new Button("OK");
            okButton.setDisable(true);
            okButton.setOnAction((e) -> onOkButton());
            okButton.setPrefWidth(175);
            cancelButton = new Button("Cancel");
            cancelButton.setOnAction((e) -> onCancelButton());
            
            setOnShown((e) -> onShown());
            setOnCloseRequest((e) -> {
                if(working.get()) {
                    e.consume();
                }
            });
            
            HBox box = new HBox();
            box.setSpacing(7.);
            box.setPadding(new Insets(7.));
            box.getChildren().addAll(okButton, cancelButton);
            
            VBox vbox = new VBox();
            vbox.setSpacing(7.);
            vbox.setPadding(new Insets(7.));
            vbox.getChildren().add(new Label("Select game:"));
            vbox.getChildren().add(selectGameBox);
            vbox.getChildren().add(box);
            
            Scene scene = new Scene(vbox);

            setTitle("Select Game");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(mainStage);
            setScene(scene);
        }
        
        private void onOkButton() {
            working.set(true);
            
            String gameName = selectGameBox.getSelectionModel().getSelectedItem();
            
            okButton.setDisable(true);
            cancelButton.setDisable(true);
            
            new Thread(() -> {
                while(working.get()) {
                    try {
                        Platform.runLater(() -> {
                            int n = (int)(Math.random() * 3 + 1);
                            String text = null;
                            switch(n) {
                                case 1:
                                    text = ".";
                                    break;
                                case 2:
                                    text = "..";
                                    break;
                                case 3:
                                    text = "...";
                                    break;
                            }
                            okButton.setText("Downloading" + text);
                        });
                        Thread.sleep(100);
                    }
                    catch(InterruptedException ex) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
            
            new Thread(() -> {
                try {
                    currentGame.matchedName = gameName;
                    currentGame.metadata = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB).getMetaData(systemName, currentGame);
                    currentGame.strength = Game.MatchStrength.LOCKED;
                }
                catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "An error occured while accessing TheGamesDB.net database.", ButtonType.OK).showAndWait();
                    });
                }
                finally {
                    working.set(false);
                    Platform.runLater(() -> {
                        hide();
                    });
                }
            }).start();
        }
        
        private void onCancelButton() {
            hide();
        }
        
        private void onShown() {
            new Thread(() -> {
                try {
                    List<String> gameList = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB).getSystemGameNames(systemName);
                    Collections.sort(gameList);
                    
                    Platform.runLater(() -> {
                        selectGameBox.getItems().clear();
                        selectGameBox.getItems().addAll(gameList);
                        selectGameBox.getSelectionModel().select(0);
                        okButton.setDisable(false);
                    });
                }
                catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "An error occured while accessing TheGamesDB.net database.", ButtonType.OK).showAndWait();
                    });
                }
            }).start();
        }
    }
    
    private class ScanProgressDialog extends Stage {
        
        private final TextArea messageArea;
        private final ProgressBar progressBar;
        private final Button cancelButton;
        
        public ScanProgressDialog(final ScanTask task) {
            super();
            messageArea = new TextArea();
            messageArea.setEditable(false);
            progressBar = new ProgressBar();
            cancelButton = new Button("Cancel Scan");
            
            task.messageProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                messageArea.appendText(newValue + "\n");
            });
            
            task.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                progressBar.setProgress((double)newValue);
            });
            
            task.setOnSucceeded((e) -> {
                messageArea.appendText("Scan complete!");
                cancelButton.setText("Close");
            });
            
            task.setOnCancelled((e) -> {
                messageArea.appendText("Scan cancelled!");
                cancelButton.setText("Close");
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
            
            FlowPane p = new FlowPane(7., 7., progressBar, cancelButton);
            
            VBox box = new VBox();
            box.setSpacing(7.);
            box.setPadding(new Insets(7.));
            box.getChildren().addAll(
                    messageArea,
                    p
            );
            
            Scene scene = new Scene(box);
            
            setTitle("Scanning in Progress");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(mainStage);
            setScene(scene);
        }
    }
    
    private class GameListCell extends ListCell<Game> {
        
        public GameListCell() {
            
        }
        
        @Override
        protected void updateItem(Game item, boolean empty) {
            super.updateItem(item, empty);
            
            setText(item == null ? "" : item.toString());
            if(item != null) {
                Game realGame = getGame(item);
                setStyle("-fx-control-inner-background: " + realGame.strength.cssBackground + ";");
                if(realGame.strength == Game.MatchStrength.LOCKED && (realGame.matchedName == null || "".equals(realGame.matchedName))) {
                    setStyle("-fx-control-inner-background: " + realGame.strength.cssBackground + ";-fx-text-fill: red");
                }
            }
        }
    }

    private static class Chooser {

        public enum DialogType {
            SAVE,
            OPEN;
        };

        private static final FileChooser fileChooser = new FileChooser();
        private static final DirectoryChooser dirChooser = new DirectoryChooser();

        public static File getFile(DialogType type, String title, String... fileExts) {
            fileChooser.setTitle(title);
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().clear();

            if(fileExts != null && fileExts.length % 2 == 0) {
                for(int i = 0; i < fileExts.length; i += 2) {
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExts[i], fileExts[i + 1]));
                }
            }

            File f = null;
            switch(type) {
                case SAVE:
                    f = fileChooser.showSaveDialog(mainStage);
                    break;
                case OPEN:
                    f = fileChooser.showOpenDialog(mainStage);
                    break;
            }
            return f;
        }

        public static File getDir(String title) {
            dirChooser.setTitle(title);
            dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File f = dirChooser.showDialog(mainStage);
            return f;
        }
    }
}
