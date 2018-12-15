/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx;

import com.goodjaerb.scraperfx.dat.Datafile;
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
import com.goodjaerb.scraperfx.datasource.DataSourceFactory;
import com.goodjaerb.scraperfx.datasource.DataSourceFactory.SourceAgent;
import com.goodjaerb.scraperfx.output.ESOutput;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.GameData;
import com.goodjaerb.scraperfx.settings.GameList;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.goodjaerb.scraperfx.settings.MetaData.MetaDataId;
import com.goodjaerb.scraperfx.settings.SystemSettings;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.ini4j.Ini;
import org.xmappr.XmapprException;

/**
 *
 * @author goodjaerb
 */
public class ScraperFX extends Application {
    private static final Path SETTINGS_DIR = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx");
    private static final String GAMEDATA_CONF = "gamedata.conf";
    private static final String SCRAPERFX_CONF = "scraperfx.conf";
    
    private static final String KEYS_FILENAME = "keys.ini";
    private static final Ini KEYS_INI = new Ini();
    
    private static Stage mainStage;
    private static SystemSettings settings;
    private static String currentSystemName;
    
    private final ListView<String> systemList;
    
    private final TabPane tabPane;
    
    private final Tab settingsTab;
    private final ToggleGroup scrapeTypeGroup;
    private final RadioButton scrapeAsConsoleButton;
    private final RadioButton scrapeAsArcadeButton;
    private final ComboBox<DataSourceFactory.SourceAgent> arcadeScraperBox;
    private final ComboBox<String> consoleSelectComboBox;
    private final TextField gameSourceField;
    private final Button gameSourceBrowseButton;
    private final TextField filenameRegexField;
    private final TextField ignoreRegexField;
    private final CheckBox unmatchedOnlyCheckBox;
    private final CheckBox refreshMetaDataCheckBox;
    private final ToggleGroup outputMediaGroup;
    private final RadioButton outputMediaToUserDirButton;
    private final RadioButton outputMediaToRomsDirButton;
    
    private final Tab gamesTab;
    private final BorderPane gamesPane;
    private final ScrollPane imagesScroll;
    private final CheckBox favoriteCheckBox;
    private final CheckBox lockImagesCheckBox;
    private final ToggleGroup sortByGroup;
    private final RadioButton sortByMetaNameRadioButton;
    private final RadioButton sortByFileNameRadioButton;
    private final CheckBox hideIgnoredCheckBox;
    private final CheckBox showOnlyNonMatchedCheckBox;
    private final CheckBox hideLockedCheckBox;
    private final CheckBox showOnlyLockedCheckBox;
    private final TextField filterField;
    private final ObservableList<Game> observableGamesList;
    private final SortedList<Game> sortedGamesList;
    private final FilteredList<Game> filteredGamesList;
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
    private final TextField videoEmbedField;
    private final CheckBox lockVideoEmbedCheckBox;
    private final TextField videoDownloadField;
    private final CheckBox lockVideoDownloadCheckBox;
    
    private final Button saveButton;
    private final Button applyDatFileButton;
//    private final Button scanButton;
    private final Button refreshFileListButton;
    private final Button deleteSystemButton;
    private final Button outputToGamelistButton;
    
    private final AtomicBoolean isScanning;
    
    private final List<ImageLoadingTask> imageTaskList;
    
    private GameData gamedata;
    private Scene rootScene;
    private Game currentGame;
    
    public ScraperFX() {
        systemList = new ListView<>();
        
        tabPane = new TabPane();
        
        settingsTab = new Tab("Settings");
        scrapeTypeGroup = new ToggleGroup();
        scrapeAsConsoleButton = new RadioButton("Console");
        scrapeAsConsoleButton.setToggleGroup(scrapeTypeGroup);
        scrapeAsConsoleButton.setSelected(true);
        scrapeAsArcadeButton = new RadioButton("Arcade");
        scrapeAsArcadeButton.setToggleGroup(scrapeTypeGroup);
        arcadeScraperBox = new ComboBox<>();
        consoleSelectComboBox = new ComboBox<>();
        consoleSelectComboBox.setEditable(false);
        gameSourceField = new TextField();
        gameSourceField.setEditable(false);
        gameSourceBrowseButton = new Button("...");
        filenameRegexField = new TextField();
        ignoreRegexField = new TextField();
        unmatchedOnlyCheckBox = new CheckBox("Don't Re-match matched files");
        refreshMetaDataCheckBox = new CheckBox("Refresh Matched Metadata");
        outputMediaGroup = new ToggleGroup();
        outputMediaToUserDirButton = new RadioButton("Output media to User Dir");
        outputMediaToUserDirButton.setToggleGroup(outputMediaGroup);
        outputMediaToUserDirButton.setSelected(true);
        outputMediaToRomsDirButton = new RadioButton("Output media to Roms Dir");
        outputMediaToRomsDirButton.setToggleGroup(outputMediaGroup);
        
        gamesTab = new Tab("Games");
        gamesPane = new BorderPane();
        imagesScroll = new ScrollPane();
        
        sortByGroup = new ToggleGroup();
        sortByMetaNameRadioButton = new RadioButton("Sort By Game Name");
        sortByMetaNameRadioButton.setToggleGroup(sortByGroup);
        sortByFileNameRadioButton = new RadioButton("Sort By File Name");
        sortByFileNameRadioButton.setToggleGroup(sortByGroup);
        
        hideIgnoredCheckBox = new CheckBox("Hide Ignored Items");
        showOnlyNonMatchedCheckBox = new CheckBox("Show Only Non-Matched Items");
        hideLockedCheckBox = new CheckBox("Hide Locked Items");
        showOnlyLockedCheckBox = new CheckBox("Show Only Locked Items");
        filterField = new TextField();
        
        observableGamesList = FXCollections.observableArrayList();
        sortedGamesList = new SortedList<>(observableGamesList);
        filteredGamesList = new FilteredList<>(sortedGamesList, getHideIgnoredPredicate());
        gamesListView = new ListView<>(filteredGamesList);
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
        videoEmbedField = new TextField();
        videoDownloadField = new TextField();
        
        lockMatchedNameCheckBox = new CheckBox("Lock");
        lockNameCheckBox = new CheckBox("Lock");
        lockDescCheckBox = new CheckBox("Lock");
        lockRatingCheckBox = new CheckBox("Lock");
        lockReleaseDateCheckBox = new CheckBox("Lock");
        lockDeveloperCheckBox = new CheckBox("Lock");
        lockPublisherCheckBox = new CheckBox("Lock");
        lockGenreCheckBox = new CheckBox("Lock");
        lockPlayersCheckBox = new CheckBox("Lock");
        lockVideoEmbedCheckBox = new CheckBox("Lock");
        lockVideoDownloadCheckBox = new CheckBox("Lock");
        lockImagesCheckBox = new CheckBox("Lock Images");
        favoriteCheckBox = new CheckBox("Favorite");
        
        saveButton = new Button("Save");
        applyDatFileButton = new Button("Apply DAT File");
        refreshFileListButton = new Button("Refresh File List");
//        scanButton = new Button("Scan Now");
        deleteSystemButton = new Button("Delete System");
        outputToGamelistButton = new Button("Output to Gamelist.xml");
        
        isScanning = new AtomicBoolean(false);
        
        imageTaskList = new ArrayList<>();
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
        VBox.setVgrow(systemList, Priority.ALWAYS);
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
        
        arcadeScraperBox.getItems().addAll(SourceAgent.ARCADE_ITALIA, SourceAgent.MAMEDB);
        arcadeScraperBox.getSelectionModel().select(SourceAgent.ARCADE_ITALIA);
        
        consoleSelectComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            if(getCurrentSettings() != null) {
                getCurrentSettings().scrapeAs = consoleSelectComboBox.getSelectionModel().getSelectedItem();
            }
        });
        
        gameSourceBrowseButton.setOnAction((e) -> {
            File dir = Chooser.getDir("Game Source Directory");
            if(dir != null) {
                gameSourceField.setText(dir.getPath());
                getCurrentSettings().romsDir = dir.getPath();
                getSystemGameData().clear();
                
//                Alert listFilesAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to preload the game list with the files located in this directory? If not you will have to run a scan of every file before you can continue.", ButtonType.YES, ButtonType.NO);
//                Optional<ButtonType> result = listFilesAlert.showAndWait();
//
//                if(result.isPresent() && result.get() == ButtonType.YES) {
                    FileSystem fs = FileSystems.getDefault();
                    Path gamesPath = fs.getPath(gameSourceField.getText());
                    if(Files.exists(gamesPath)) {
                        final List<Path> paths = new ArrayList<>();
                        DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
                        try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                            for(Path p: stream) {
                                paths.add(p);
                            }
                        }
                        catch(IOException ex) {
                            Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        observableGamesList.clear();
                        paths.forEach((p) -> {
                            Game g = new Game(p.getFileName().toString());
                            getSystemGameData().add(g);
                            observableGamesList.add(g);
                        });
                    }
//                }
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
        
        unmatchedOnlyCheckBox.setPadding(new Insets(7.));
        unmatchedOnlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {//setOnAction((e) -> {
            getCurrentSettings().unmatchedOnly = unmatchedOnlyCheckBox.isSelected();
            
            refreshMetaDataCheckBox.setSelected(false);
            if(unmatchedOnlyCheckBox.isSelected()) {
                refreshMetaDataCheckBox.setDisable(false);
            }
            else {
                refreshMetaDataCheckBox.setDisable(true);
            }
        });
        
        refreshMetaDataCheckBox.setPadding(new Insets(7.));
        refreshMetaDataCheckBox.setDisable(false);

        HBox box21 = new HBox();
        box21.setSpacing(7.);
        box21.setPadding(new Insets(7.));
        
        box21.getChildren().addAll(new Label("Scrap as:"), scrapeAsConsoleButton, scrapeAsArcadeButton, arcadeScraperBox);
        
        HBox selectConsoleBox = new HBox();
        selectConsoleBox.setSpacing(7.);
        selectConsoleBox.setPadding(new Insets(7.));
        selectConsoleBox.getChildren().add(new Label("Select Console:"));
        selectConsoleBox.getChildren().add(consoleSelectComboBox);
        
        new Thread(() -> {
            try {
                List<String> consoles = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB_LEGACY).getSystemNames();
                Platform.runLater(() -> {
                    consoleSelectComboBox.getItems().clear();
                    consoleSelectComboBox.getItems().addAll(consoles);
                    com.goodjaerb.scraperfx.settings.System currentSystem = getCurrentSettings();
                    if(currentSystem != null && !currentSystem.scrapeAsArcade) {
                        consoleSelectComboBox.getSelectionModel().select(currentSystem.scrapeAs);
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
        
        VBox settingsPane = new VBox();
        settingsPane.getChildren().addAll(
                box21,
                selectConsoleBox,
                createBrowseFieldPane("Game Source Directory:", gameSourceField, gameSourceBrowseButton),
                createBrowseFieldPane("Regex for Substring Removal (Advanced):", filenameRegexField),
                createBrowseFieldPane("Regex for Files to Ignore (Advanced):", ignoreRegexField),
                unmatchedOnlyCheckBox,
                refreshMetaDataCheckBox,
                outputMediaToUserDirButton,
                outputMediaToRomsDirButton
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
                currentGame = getGame(selected);
                loadCurrentGameFields(currentGame);
            }
        });
        
        gamesListView.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                filterField.setText("");
            }
            else if(event.getCode() == KeyCode.BACK_SPACE) {
                String currentText = filterField.getText();
                filterField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        
        gamesListView.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
            filterField.appendText(event.getCharacter());
        });
        
        MenuItem lockGamesItem = new MenuItem("Lock");
        lockGamesItem.setOnAction((e) -> {
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.stream().forEach((g) -> {
                getGame(g).strength = Game.MatchStrength.LOCKED;
            });
            gamesListView.refresh();
            loadCurrentGameFields(currentGame);
        });
        
        MenuItem unlockGamesItem = new MenuItem("Unlock");
        unlockGamesItem.setOnAction((e) -> {
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.stream().forEach((g) -> {
                final Game realGame = getGame(g);
                if(realGame.matchedName == null) {
                    realGame.strength = Game.MatchStrength.NO_MATCH;
                }
                else {
                    realGame.strength = Game.MatchStrength.BEST_GUESS;
                }
            });
            gamesListView.refresh();
            loadCurrentGameFields(currentGame);
        });
        
        MenuItem ignoreItem = new MenuItem("Ignore");
        ignoreItem.setOnAction((e) -> {
//            clearCurrentGameFields();
//            currentGame.matchedName = null;
//            currentGame.metadata = null;
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.stream().forEach((g) -> {
                final Game realGame = getGame(g);
                realGame.matchedName = null;
                realGame.metadata = null;
                realGame.strength = Game.MatchStrength.IGNORE;
            });
//            currentGame.strength = Game.MatchStrength.IGNORE;
//            loadCurrentGameFields(currentGame);
            gamesListView.refresh();
        });
        
        MenuItem unignoreItem = new MenuItem("Unignore");
        unignoreItem.setOnAction((e) -> {
//            clearCurrentGameFields();
//            currentGame.matchedName = null;
//            currentGame.metadata = null;
            ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.stream().forEach((g) -> {
                getGame(g).strength = Game.MatchStrength.NO_MATCH;
            });
//            currentGame.strength = Game.MatchStrength.IGNORE;
//            loadCurrentGameFields(currentGame);
            gamesListView.refresh();
        });
        
        MenuItem scanGamesItem = new MenuItem("Scan Selected Game(s)");
        scanGamesItem.setOnAction((e) -> {
            List<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            
            FileSystem fs = FileSystems.getDefault();
            Path gamesPath = fs.getPath(gameSourceField.getText());
            if(Files.exists(gamesPath)) {
//                tabPane.getSelectionModel().select(gamesTab);
                
                ScanTask scanner = new ScanTask(gamesPath, selectedGames);
                ScanProgressDialog scanProgressDialog = new ScanProgressDialog(scanner);
                
//                Thread t = new Thread(scanner);
//                t.setDaemon(true);
//                t.start();
                scanProgressDialog.showAndWait();
            }
        });
        
        gamesListView.setContextMenu(new ContextMenu(lockGamesItem, unlockGamesItem, ignoreItem, unignoreItem, scanGamesItem));
        
        sortByMetaNameRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                sortedGamesList.setComparator(Game.GAME_NAME_COMPARATOR);
            }
        });
        
        sortByFileNameRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                sortedGamesList.setComparator(Game.FILE_NAME_COMPARATOR);
            }
        });
        sortByMetaNameRadioButton.setSelected(true);
        
        hideIgnoredCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        showOnlyNonMatchedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        hideLockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        showOnlyLockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        filterField.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                filterField.setText("");
            }
        });
        
        matchedNameClearButton.setOnAction((e) -> {
            clearCurrentGameFields();
            currentGame.matchedName = null;
            currentGame.metadata = null;
            currentGame.strength = Game.MatchStrength.LOCKED;
            loadCurrentGameFields(currentGame);
            gamesListView.refresh();
        });
        
        matchedNameBrowseButton.setOnAction((e) -> {
            if(currentGame != null) {
                final SingleGameDownloadDialog d = new SingleGameDownloadDialog(getCurrentSettings().scrapeAs);
                d.showAndWait();
                loadCurrentGameFields(currentGame);
                Game updatedGame = observableGamesList.remove(observableGamesList.indexOf(currentGame));
                observableGamesList.add(updatedGame);
                gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(updatedGame));
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
        lockNameCheckBox.setOnAction((e) -> {
            lockMetaField(metaNameField, MetaDataId.NAME, currentGame.metadata, lockNameCheckBox.isSelected());
            Game updatedGame = observableGamesList.remove(observableGamesList.indexOf(currentGame));
            observableGamesList.add(updatedGame);
            gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(updatedGame));
        });
        lockDescCheckBox.setOnAction((e) ->         lockMetaField(metaDescArea, MetaDataId.DESC, currentGame.metadata, lockDescCheckBox.isSelected()));
        lockDeveloperCheckBox.setOnAction((e) ->    lockMetaField(metaDeveloperField, MetaDataId.DEVELOPER, currentGame.metadata, lockDeveloperCheckBox.isSelected()));
        lockGenreCheckBox.setOnAction((e) ->        lockMetaField(metaGenreField, MetaDataId.GENRE, currentGame.metadata, lockGenreCheckBox.isSelected()));
        lockPublisherCheckBox.setOnAction((e) ->    lockMetaField(metaPublisherField, MetaDataId.PUBLISHER, currentGame.metadata, lockPublisherCheckBox.isSelected()));
        lockRatingCheckBox.setOnAction((e) ->       lockMetaField(metaRatingField, MetaDataId.RATING, currentGame.metadata, lockRatingCheckBox.isSelected()));
        lockReleaseDateCheckBox.setOnAction((e) ->  lockMetaField(metaReleaseDateField, MetaDataId.RELEASE_DATE, currentGame.metadata, lockReleaseDateCheckBox.isSelected()));
        lockPlayersCheckBox.setOnAction((e) ->      lockMetaField(playersField, MetaDataId.PLAYERS, currentGame.metadata, lockPlayersCheckBox.isSelected()));
        lockImagesCheckBox.setOnAction((e) -> {     currentGame.metadata.lockImages = lockImagesCheckBox.isSelected(); });
        lockVideoEmbedCheckBox.setOnAction((e) ->   lockMetaField(videoEmbedField, MetaDataId.VIDEO_EMBED, currentGame.metadata, lockVideoEmbedCheckBox.isSelected()));
        lockVideoDownloadCheckBox.setOnAction((e) -> lockMetaField(videoDownloadField, MetaDataId.VIDEO_DOWNLOAD, currentGame.metadata, lockVideoDownloadCheckBox.isSelected()));
        favoriteCheckBox.setOnAction((e) -> {       currentGame.metadata.favorite = favoriteCheckBox.isSelected(); gamesListView.refresh(); });
        
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
                createMetaFieldPane("Players:", playersField, lockPlayersCheckBox),
                createMetaFieldPane("Video Embed URL:", videoEmbedField, lockVideoEmbedCheckBox),
                createMetaFieldPane("Video Download URL:", videoDownloadField, lockVideoDownloadCheckBox)
        );
        
        imagesScroll.setPrefSize(200., 475.);
        
        VBox imagesBox = new VBox();
        imagesBox.setPadding(new Insets(7.));
        imagesBox.setSpacing(7.);
        imagesBox.getChildren().addAll(favoriteCheckBox, lockImagesCheckBox, imagesScroll);
        
        VBox sortByBox = new VBox();
        sortByBox.setSpacing(7.);
        sortByBox.getChildren().addAll(sortByMetaNameRadioButton, sortByFileNameRadioButton, filterField);
        
        VBox filterBox = new VBox();
        filterBox.setSpacing(7.);
        filterBox.getChildren().addAll(hideIgnoredCheckBox, showOnlyNonMatchedCheckBox, hideLockedCheckBox, showOnlyLockedCheckBox);
        
        HBox topBox = new HBox();
        topBox.setSpacing(7.);
        topBox.getChildren().addAll(sortByBox, filterBox);
        
        VBox leftBox = new VBox(7., topBox, gamesListView);
        VBox.setVgrow(gamesListView, Priority.ALWAYS);
        
        gamesPane.setPadding(new Insets(7.));
        gamesPane.setLeft(leftBox);
        gamesPane.setCenter(metaBox);
        gamesPane.setRight(imagesBox);
        
        gamesTab.setContent(gamesPane);
        gamesTab.setClosable(false);
        gamesTab.setDisable(true);
        // End Games Tab
        
//        scanButton.setOnAction((ActionEvent e) -> {
//            FileSystem fs = FileSystems.getDefault();
//            Path gamesPath = fs.getPath(gameSourceField.getText());
//            if(Files.exists(gamesPath)) {
//                tabPane.getSelectionModel().select(gamesTab);
//                
//                ScanTask scanner = new ScanTask(gamesPath);
//                ScanProgressDialog scanProgressDialog = new ScanProgressDialog(scanner);
//                
//                Thread t = new Thread(scanner);
//                t.setDaemon(true);
//                t.start();
//                scanProgressDialog.showAndWait();
//            }
//        });
        
        saveButton.setOnAction((e) -> {
            saveAll();
        });
        
        applyDatFileButton.setOnAction((e) -> {
            File datFile = Chooser.getFile(Chooser.DialogType.OPEN, "Select DAT File", "DAT FILE", "*.dat");
            if(datFile != null) {
                try {
                    Datafile dat = readDatFile(datFile.toPath());

                    observableGamesList.forEach((game) -> {
                        String filename = game.fileName;
                        
                        if(dat.getElements().stream().noneMatch((element) -> {
                            return filename.equals(element.getName() + ".zip");
                        })) {
                            game.strength = Game.MatchStrength.IGNORE;
                            gamesListView.refresh();
                        }
                    });
                }
                catch (IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        refreshFileListButton.setOnAction((e) -> {
            FileSystem fs = FileSystems.getDefault();
            Path gamesPath = fs.getPath(gameSourceField.getText());
            if(Files.exists(gamesPath)) {
                final List<Path> paths = new ArrayList<>();
                DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
                try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                    for(Path p : stream) {
                        paths.add(p);
                    }
                }
                catch(IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                paths.forEach((p) -> {
                    Game g = new Game(p.getFileName().toString());
                    if(!observableGamesList.contains(g)) {
                        getSystemGameData().add(g);
                        observableGamesList.add(g);
                    }
                });
            }
        });
        
        outputToGamelistButton.setOnAction((e) -> {
            Collections.sort(getSystemGameData(), Game.GAME_NAME_COMPARATOR);
            if(outputMediaToUserDirButton.isSelected()) {
                new ESOutput().output(
                        getSystemGameData(),
                        SETTINGS_DIR.resolve(ESOutput.GAMELISTS_DIR).resolve(getCurrentSettings().name),
                        SETTINGS_DIR.resolve("media").resolve(getCurrentSettings().name).resolve(ESOutput.IMAGES_DIR),
                        SETTINGS_DIR.resolve("media").resolve(getCurrentSettings().name).resolve(ESOutput.VIDEOS_DIR),
                        getCurrentSettings().scrapeAsArcade);
            }
            else {
                new ESOutput().output(
                        getSystemGameData(),
                        SETTINGS_DIR.resolve(ESOutput.GAMELISTS_DIR).resolve(getCurrentSettings().name),
                        Paths.get(getCurrentSettings().romsDir, ESOutput.IMAGES_DIR),
                        Paths.get(getCurrentSettings().romsDir, ESOutput.VIDEOS_DIR),
                        getCurrentSettings().scrapeAsArcade);
            }
        });
        
        deleteSystemButton.setOnAction((e) -> deleteSystemButtonOnActionPerformed() );
        
        FlowPane settingsButtonPane = new FlowPane(saveButton, applyDatFileButton, deleteSystemButton, refreshFileListButton, outputToGamelistButton);
        settingsButtonPane.setHgap(7.);
        settingsButtonPane.setAlignment(Pos.CENTER);
        
        tabPane.getTabs().add(settingsTab);
        tabPane.getTabs().add(gamesTab);
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
            KEYS_INI.load(SETTINGS_DIR.resolve(KEYS_FILENAME).toFile());
        }
        catch(IOException ex) {
            Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> {
            ButtonType saveThenQuitButton = new ButtonType("Save & Quit");
            ButtonType dontQuitButton = new ButtonType("Wait, Don't Quit!");
            ButtonType justQuitButton = new ButtonType("Just Quit, Thanks.");
            
            Alert closingAlert = new Alert(Alert.AlertType.CONFIRMATION, "ScraperFX is about to close. If you have made any new scans or settings changes you may want to save. Save now?", saveThenQuitButton, dontQuitButton, justQuitButton);
            Optional<ButtonType> result = closingAlert.showAndWait();

            if(result.isPresent()) {
                if(result.get() == saveThenQuitButton) {
                    saveAll();
                }
                else if(result.get() == dontQuitButton) {
                    event.consume();
                }
            }
        });
        
        primaryStage.setTitle("ScraperFX");
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
    
    public static String getKeysValue(String name) {
        return KEYS_INI.get("Keys", name);
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
    
    private Predicate<Game> buildFilterPredicate() {
        return getHideIgnoredPredicate().and(
                getShowOnlyNonMatchedPredicate()).and(
                getFilterTextPredicate(filterField.getText())).and(
                getHideLockedPredicate()).and(
                getShowOnlyLockedPredicate());
    }
    
    private Predicate<Game> getHideIgnoredPredicate() {
        if(hideIgnoredCheckBox.isSelected()) {
            return (game) -> {
                return game.strength != Game.MatchStrength.IGNORE;
            };
        }
        return (game) -> {
            return true;
        };
    }
    
    private Predicate<Game> getShowOnlyNonMatchedPredicate() {
        if(showOnlyNonMatchedCheckBox.isSelected()) {
            return (game) -> {
                return game.strength == Game.MatchStrength.NO_MATCH || game.strength == Game.MatchStrength.IGNORE;
            };
        }
        return (game) -> {
            return true;
        };
    }
    
    private Predicate<Game> getHideLockedPredicate() {
        if(hideLockedCheckBox.isSelected()) {
            return (game) -> {
                return game.strength != Game.MatchStrength.LOCKED;
            };
        }
        return (game) -> {
            return true;
        };
    }
    
    private Predicate<Game> getShowOnlyLockedPredicate() {
        if(showOnlyLockedCheckBox.isSelected()) {
            return (game) -> {
                return game.strength == Game.MatchStrength.LOCKED;
            };
        }
        return (game) -> {
            return true;
        };
    }
    
    private Predicate<Game> getFilterTextPredicate(String filterText) {
        return (game) -> {
            if(filterText.trim().isEmpty()) {
                return true;
            }
            return game.fileName.toLowerCase().contains(filterText.toLowerCase()) || (game.metadata != null && game.metadata.metaName != null && game.metadata.metaName.toLowerCase().contains(filterText.toLowerCase()));
        };
    }
    
    private void saveAll() {
        try {
            writeSettings();
            writeGameData();
        }
        catch(IOException ex) {
            Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);

            new Alert(Alert.AlertType.ERROR, "Error saving settings.", ButtonType.OK).showAndWait();
        }
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
        gamesListView.refresh();
    }
    
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
                if(++retry < 3) {
                    System.out.println("Error retrieving image from " + url + ". Retrying...");
                }
            }
        }
        return null;
    }
    
    public static boolean saveVideo(Path path, String filename, String url) throws IOException {
        if(url == null || url.isEmpty()) {
            return false;
        }
        
        if(!Files.exists(path)) {
            Files.createDirectories(path);
        }
        
        File outputFile = new File(path.toString() + File.separator + filename);
        if(outputFile.exists()) {
            return true;
        }
        else {
            int retry = 0;
            while(retry < 3) {
                try {
                    FileUtils.copyURLToFile(new URL(url), outputFile, 10000, 10000);
                    return true;
                }
                catch(IOException ex) {
                    if(++retry < 3) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, "Error retrieving video from {0}. Retrying...", url);
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean writeImageToFile(Path path, String imageFileName, String imageOutputType, String url) throws MalformedURLException, IOException {
        if(url == null || url.isEmpty()) {
            return false;
        }
        
        if(!Files.exists(path)) {
            Files.createDirectories(path);
        }
                    
        File outputFile = new File(path.toString() + File.separator + imageFileName + "." + imageOutputType);
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
                    
                    int width = 640;
                    if(img.getWidth() <= 640) {
                        width = img.getWidth();
                    }
                    
                    java.awt.Image scaled = img.getScaledInstance(width, -1, java.awt.Image.SCALE_SMOOTH);
                    BufferedImage scaledBuff = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    scaledBuff.getGraphics().drawImage(scaled, 0, 0, null);
                    ImageIO.write(scaledBuff, imageOutputType, outputFile);
                    return true;
                }
                catch(MalformedURLException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                catch(IOException ex) {
                    if(++retry < 3) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, "Error retrieving image from {0}. Retrying...", url);
                    }
                }
            }
        }
        return false;
    }
    
    private Pane createMetaFieldPane(String labelStr, TextInputControl field, CheckBox lockCheckBox) {
        return createMetaFieldPane(labelStr, field, null, null, lockCheckBox);
    }
    
    private Pane createMetaFieldPane(String labelStr, TextInputControl field, Button clearButton, Button browseButton, CheckBox lockCheckBox) {
        Label label = new Label(labelStr);
        label.setPadding(new Insets(7., 7., 0., 7.));
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
    
    public static com.goodjaerb.scraperfx.settings.System getCurrentSettings() {
        return getSettings(currentSystemName);
    }
    
    private static com.goodjaerb.scraperfx.settings.System getSettings(String systemName) {
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
        videoEmbedField.clear();
        videoDownloadField.clear();
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
        lockVideoEmbedCheckBox.setSelected(false);
        lockVideoDownloadCheckBox.setSelected(false);
    }
    
    private void loadCurrentGameFields(Game g) {
        clearCurrentGameFields();
        
        if(g != null) {
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
                videoEmbedField.setText(g.metadata.videoembed);
                videoDownloadField.setText(g.metadata.videodownload);

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
                lockVideoEmbedCheckBox.setSelected(g.metadata.lockVideoEmbed);
                videoEmbedField.setEditable(!g.metadata.lockVideoEmbed);
                lockVideoDownloadCheckBox.setSelected(g.metadata.lockVideoDownload);
                videoDownloadField.setEditable(!g.metadata.lockVideoDownload);

                lockImagesCheckBox.setSelected(g.metadata.lockImages);
                favoriteCheckBox.setSelected(g.metadata.favorite);

                if(!isScanning.get()) {
                    Platform.runLater(() -> {
                        imageTaskList.stream().forEach((task) -> {
                            task.cancel();
                        });
                        imageTaskList.clear();

                        if(g.metadata != null && g.metadata.images != null && !g.metadata.images.isEmpty()) {
                            MetaImageViewBox box = new MetaImageViewBox();

                            g.metadata.images.stream().filter((image) -> (image != null)).map((image) -> {
                                MetaImageView metaImage = new MetaImageView(image);
                                box.addView(metaImage);
                                ImageLoadingTask task = new ImageLoadingTask(metaImage, image);
                                return task;
                            }).map((task) -> {
                                imageTaskList.add(task);
                                return task;
                            }).map((task) -> new Thread(task)).map((t) -> {
                                t.setDaemon(true);
                                return t;
                            }).forEach((t) -> {
                                t.start();
                            });

                            imagesScroll.setContent(box);
                        }
                    });
                }
            }
        }
    }
    
    private void scrapeAsArcadeSetup(boolean b) {
        if(b) {
            getCurrentSettings().scrapeAsArcade = true;
            scrapeAsArcadeButton.setSelected(true);
            consoleSelectComboBox.setDisable(true);
            matchedNameBrowseButton.setDisable(true);
            arcadeScraperBox.setDisable(false);
        }
        else {
            getCurrentSettings().scrapeAsArcade = false;
            scrapeAsConsoleButton.setSelected(true);
            consoleSelectComboBox.setDisable(false);
            matchedNameBrowseButton.setDisable(false);
            arcadeScraperBox.setDisable(true);
        }
    }
    
    private void loadCurrentSettings() {
        scrapeAsArcadeSetup(getCurrentSettings().scrapeAsArcade);
        consoleSelectComboBox.getSelectionModel().select(getCurrentSettings().scrapeAs);
        gameSourceField.setText(getCurrentSettings().romsDir);
        filenameRegexField.setText(getCurrentSettings().substringRegex);
        ignoreRegexField.setText(getCurrentSettings().ignoreRegex);
        unmatchedOnlyCheckBox.setSelected(getCurrentSettings().unmatchedOnly);
        
        observableGamesList.clear();
        if(getSystemGameData() != null) {
            observableGamesList.addAll(getSystemGameData());
//            gamesListView.setItems(observableGamesList.sorted());
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
                settings.systems.add(new com.goodjaerb.scraperfx.settings.System(name));
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
    
    private Datafile readDatFile(Path path) throws FileNotFoundException, IOException {
        final Xmappr xm = new Xmappr(Datafile.class);
        
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()));
        final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
        charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
        charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetDecoder))) {
            return (Datafile)xm.fromXML(reader);
        }
        catch(IOException ex) {
            throw ex;
        }
    }
    
    private void readSettings() throws IOException {
//        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "scraperfx.conf");
        final Path scraperfxConfPath = SETTINGS_DIR.resolve(SCRAPERFX_CONF);
        if(Files.exists(scraperfxConfPath)) {
            final Xmappr xm = new Xmappr(SystemSettings.class);
            
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(scraperfxConfPath.toFile()));
            final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            
            try(final BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetDecoder))) {
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
//        Path settingsPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx", "gamedata.conf");
        final Path gamedataConfPath = SETTINGS_DIR.resolve(GAMEDATA_CONF);
        if(Files.exists(gamedataConfPath)) {
            final Xmappr xm = new Xmappr(GameData.class);
            
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(gamedataConfPath.toFile()));
            final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            
            try(final BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetDecoder))) {
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
            
            views = new ArrayList<>();
        }
        
        public void selectImage(com.goodjaerb.scraperfx.settings.Image image) {
            currentGame.metadata.selectImage(image);
            views.stream().forEach((view) -> {
                if(view.getImage().selected) {
                    view.setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
                }
                else {
                    view.setBorder(null);
                }
            });
        }
        
        public void addView(MetaImageView view) {
            getChildren().add(view);
            views.add(view);
        }
    }
    
    private final class MetaImageView extends VBox {
        private final ImageView view;
        private final com.goodjaerb.scraperfx.settings.Image image;
        
        public MetaImageView(com.goodjaerb.scraperfx.settings.Image image) {
            super();
            this.image = image;
            view = new ImageView("images/loading.gif");
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
        
        public com.goodjaerb.scraperfx.settings.Image getImage() {
            return image;
        }
        
        public ImageView getView() {
            return view;
        }
    }
    
    private class ImageLoadingTask extends Task<Void> {
        private final MetaImageView view;
        private final com.goodjaerb.scraperfx.settings.Image scraperImage;
        
        private Image image;

        public ImageLoadingTask(final MetaImageView view, com.goodjaerb.scraperfx.settings.Image scraperImage) {
            this.view = view;
            this.scraperImage = scraperImage;
        }
        
        @Override
        protected Void call() {
            try {
                image = getImageFromURL(scraperImage.url);
            }
            catch(IOException ex) {
//                cancel();
            }
            return null;
        }

        @Override
        protected void succeeded() {
            view.getView().setImage(image);
        }

        @Override
        protected void cancelled() {
            view.getView().setImage(null);
        }
    }
    
    private class ScanTask extends Task<Void> {
//        private final Path gamesPath;
        private final List<Path> paths;
        
        public ScanTask(Path gamesPath) {
//            this.gamesPath = gamesPath;
            paths = new ArrayList<>();
            DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                stream.forEach((p) -> paths.add(p));
//                for(Path p: stream) {
//                    paths.add(p);
//                }
            }
            catch(IOException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public ScanTask(Path gamesPath, List<Game> games) {
            paths = new ArrayList<>();
            DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file) && games.stream().anyMatch((game) -> file.getFileName().toString().equals(game.fileName)));
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                stream.forEach((p) -> paths.add(p));
            }
            catch(IOException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        protected Void call() throws Exception {
            isScanning.set(true);
            try {
//                final List<Path> paths = new ArrayList<>();
//                DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
//                try(DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
//                    for(Path p: stream) {
//                        paths.add(p);
//                    }
//                }
//                catch(IOException ex) {
//                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
//                Collections.sort(paths, (o1, o2) -> {
//                    return o1.getFileName().compareTo(o2.getFileName());
//                });
                
//                long totalFiles = Files.list(gamesPath).count();
                long totalFiles = paths.size();
                long fileCount = 0;
                for(Path p : paths) {
                    if(isCancelled()) {
                        break;
                    }

                    String filename = p.getFileName().toString();
                    Game localGame = getGame(new Game(filename));
                    if(localGame == null) {
                        localGame = new Game(filename);
                    }
                    
                    System.out.println("============================================================\nBEGINNING SCAN OF '" + localGame.fileName + "'");
                    if(localGame.strength == Game.MatchStrength.IGNORE) {
                        System.out.println("Ignoring '" + localGame.fileName + "' due to PRESET IGNORE flag.");
                        updateMessage("Ignoring '" + localGame.fileName + "' due to PRESET IGNORE flag.");
                    }
                    else {
                        boolean ignore = false;
                        if(getCurrentSettings().ignoreRegex != null && !"".equals(getCurrentSettings().ignoreRegex)) {
                            Pattern ignorePattern = Pattern.compile(".*" + getCurrentSettings().ignoreRegex + ".*");
                            Matcher ignoreMatcher = ignorePattern.matcher(filename);
                            if(ignoreMatcher.matches()) {
                                localGame.matchedName = null;
                                localGame.strength = Game.MatchStrength.IGNORE;
                                ignore = true;
                                
                                System.out.println("Ignoring '" + localGame.fileName + "' due to REGEX IGNORE flag.");
                                updateMessage("Ignoring '" + localGame.fileName + "' due to REGEX IGNORE flag.");
                            }
                        }

                        if(!ignore) {
//                            boolean refreshOnly = false;
                            boolean refreshMatchedGame = false;
                            boolean skipMatching = false;
                            boolean startedUnmatched = false;
                            
                            if(localGame.strength == Game.MatchStrength.NO_MATCH) {
                                startedUnmatched = true;
                            }
                            
                            if(localGame.strength == Game.MatchStrength.LOCKED) {
                                skipMatching = true;
                                System.out.println("Not matching '" + localGame.fileName + "' because it is LOCKED.");
                                updateMessage("Not matching '" + localGame.fileName + "' because it is LOCKED.");
//                                System.out.println("Not refreshing metadata for '" + localGame.fileName + "' because it is LOCKED.");
//                                updateMessage("Not refreshing metadata for '" + localGame.fileName + "' because it is LOCKED.");
                            }
                            else if(unmatchedOnlyCheckBox.isSelected() 
                                    && localGame.strength != Game.MatchStrength.IGNORE && localGame.strength != Game.MatchStrength.NO_MATCH) {
                                // this game is already matched and we only want unmatched games.
                                skipMatching = true;

                                System.out.println("Not matching '" + localGame.fileName + "' because it is ALREADY MATCHED.");
                                updateMessage("Not matching '" + localGame.fileName + "' because it is ALREADY MATCHED.");
                            }
                            else {
                                System.out.println("Will attempt to match '" + localGame.fileName + "'.");
                                updateMessage("Will attempt to match '" + localGame.fileName + "'.");
                            }
                            
                            if(!startedUnmatched && !refreshMetaDataCheckBox.isDisabled()) {
                                if(refreshMetaDataCheckBox.isSelected()) {
                                    refreshMatchedGame = true;

                                    System.out.println("Refreshing metadata for '" + localGame.fileName + "'.");
                                    updateMessage("Refreshing metadata for '" + localGame.fileName + "'.");
                                }
                                else {
                                    System.out.println("NOT refreshing metadata for '" + localGame.fileName + "' due to check-box (un)selection.");
                                    updateMessage("NOT refreshing metadata for '" + localGame.fileName + "' due to check-box (un)selection.");
                                }
                            }
                            
                            if(skipMatching) {
                                if(!refreshMatchedGame) {
                                    System.out.println("ENDING SCAN OF '" + localGame.fileName + "'\n============================================================");
                                    continue;
                                }
                            }

                            String noExtName = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();
                            if(scrapeTypeGroup.getSelectedToggle() == scrapeAsArcadeButton) {
                                if(!skipMatching) {
                                    localGame.matchedName = noExtName;
                                }
                                    
                                if(refreshMatchedGame || startedUnmatched) {
                                    boolean wasFavorite = false;
                                    if(localGame.metadata != null && localGame.metadata.favorite) {
                                        wasFavorite = true;
                                    }
                                
                                    localGame.metadata = DataSourceFactory.getDataSource(arcadeScraperBox.getValue()).getMetaData(null, localGame);
    //                                    System.out.println(localGame.metadata.images);
    //                                    localGame.metadata = DataSourceFactory.getDataSource(SourceAgent.ARCADE_ITALIA).getMetaData(null, localGame);
                                    if(localGame.metadata == null) {
                                        localGame.matchedName = null;
                                        System.out.println("Could not match '" + filename + "' to a game.");
                                        updateMessage("Could not match '" + filename + "' to a game.");
                                    }
                                    else {
                                        localGame.metadata.favorite = wasFavorite;
                                        localGame.strength = Game.MatchStrength.STRONG;
                                        System.out.println("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                                        updateMessage("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                                    }
                                }
                            }
                            else {
//                                if(localGame.strength != Game.MatchStrength.LOCKED && !refreshOnly) {
                                if(!skipMatching) {
                                    if(getCurrentSettings().substringRegex != null && !"".equals(getCurrentSettings().substringRegex)) {
                                        Pattern pattern = Pattern.compile(".*" + getCurrentSettings().substringRegex + ".*");
                                        Matcher m = pattern.matcher(noExtName);
                                        if(m.matches()) {
                                            for(int i = 1; i <= m.groupCount(); i++) {
                                                noExtName = noExtName.replaceAll(m.group(i), "");
                                            }
                                        }
                                    }
                                    noExtName = noExtName.replaceAll(" - ", " ");
                                    noExtName = noExtName.replaceAll("\\(.*\\)", "");
                                    noExtName = noExtName.replaceAll("\\[.*\\]", "");
                                    noExtName = noExtName.replaceAll(" and ", " ");
                                    noExtName = noExtName.replaceAll("\\p{Punct}", "");
                                    noExtName = noExtName.trim();

                                    boolean hundredpercentmatch = false;
                                    List<String> hitNames = new ArrayList<>();
                                    List<Double> hitPercents = new ArrayList<>();
                                    List<Double> remoteHitPercents = new ArrayList<>();
                                    int mostPartsHit = 0;
                                    boolean hitPartIsNumber = false;

                                    List<String> allSysGames = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB_LEGACY).getSystemGameNames(getCurrentSettings().scrapeAs);
                                    for(String gameName : allSysGames) {
                                        String lowerCaseName = gameName.toLowerCase().replaceAll(" - ", " ");//gameName.toLowerCase().replaceAll("'", "");
                                        lowerCaseName = lowerCaseName.replaceAll("\\(.*\\)", "");
                                        lowerCaseName = lowerCaseName.replaceAll("\\[.*\\]", "");
                                        lowerCaseName = lowerCaseName.replaceAll(" and ", " ");
                                        lowerCaseName = lowerCaseName.replaceAll("\\p{Punct}", "");
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
                                            String[] localParts = noExtName.split("\\s");
                                            String[] split = lowerCaseName.split("\\s");

                                            int hits = 0;
                                            int hitLength = 0;
                                            for(String split1 : split) {
                                                if(!"".equals(split1)) {
                                                    for(int q = 0; q < localParts.length; q++) {
                                                        if(!"".equals(localParts[q])) {
                                                            if(isNumeral(split1)) {
                                                                // 1-15, either digits or roman.
                                                                if(localParts[q].equals(asDigit(split1)) || localParts[q].equals(asRoman(split1))) {
                                                                    hits++;
                                                                    hitLength += split1.length();
                                                                    hitPartIsNumber = true;//needed later if this is the only part hit.
                                                                    localParts[q] = "";//piece matched, clear it out to prevent rematching.
                                                                    break;
                                                                }
                                                            }
                                                            else {
                                                                if(isInteger(localParts[q]) || isInteger(split1)) {
                                                                    // i don't want to do contains on numbers because weird things can happen
                                                                    // where a filename with a 000 in it (for whatever reason) will match
                                                                    // any game with say a 2000 in the title (happens a lot).
                                                                    if(localParts[q].equals(split1)) {
                                                                        hits++;
                                                                        hitLength += split1.length();
                                                                        localParts[q] = "";//piece matched, clear it out to prevent rematching.
                                                                        break;
                                                                    }
                                                                }
                                                                else if(localParts[q].contains(split1)) {
                                                                    hits++;
                                                                    hitLength += split1.length();
                                                                    localParts[q] = localParts[q].replace(split1, ""); //piece matched, clear it out to prevent rematching.
                                                                    break;
                                                                }
                                                                else if(split1.contains(localParts[q])) {
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
                                            List<String> tieNames = new ArrayList<>();

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
                                    
                                    System.out.println("Matched '" + filename + "' to game '" + localGame.matchedName + "'.");
                                    updateMessage("Matched '" + filename + "' to game '" + localGame.matchedName + "'.");
                                }

                                if(localGame.matchedName == null) {
                                    System.out.println("Could not match '" + filename + "' to a game.");
                                    updateMessage("Could not match '" + filename + "' to a game.");
                                }
                                else {
                                    if(!skipMatching || refreshMatchedGame || startedUnmatched) {
                                        MetaData lockedData = null;
                                        if(localGame.strength == Game.MatchStrength.LOCKED) {
                                            lockedData = localGame.metadata;
                                        }

                                        boolean wasFavorite = false;
                                        if(localGame.metadata != null && localGame.metadata.favorite) {
                                            wasFavorite = true;
                                        }
                                    
                                        //matched a game, get the rest of the data.
                                        localGame.metadata = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB_LEGACY).getMetaData(getCurrentSettings().scrapeAs, localGame);

                                        if(localGame.metadata == null && localGame.strength == Game.MatchStrength.LOCKED) {
                                            localGame.metadata = lockedData;
                                            
                                            System.out.println("Metadata came back NULL; Restored previous metadata for LOCKED game '" + filename + "' (" + localGame.metadata.metaName + ").");
                                            updateMessage("Metadata came back NULL; Restored previous metadata for LOCKED game '" + filename + "' (" + localGame.metadata.metaName + ").");
                                        }

                                        if(localGame.metadata != null) {
                                            localGame.metadata.favorite = wasFavorite;

                                            final String[] videoLinks = DataSourceFactory.getDataSource(SourceAgent.SCREEN_SCRAPER).getVideoLinks(getCurrentSettings().scrapeAs, localGame);
                                            if(videoLinks != null) {
                                                localGame.metadata.videodownload = videoLinks[0];
                                                localGame.metadata.videoembed = videoLinks[1];
                                            }
                                            System.out.println("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                                            updateMessage("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                                        }
                                    }

//                                        System.out.println(localGame.metadata);
//                                        localGame.metadata.videodownload = DataSourceFactory.getDataSource(SourceAgent.SCREEN_SCRAPER).getVideoDownload(getCurrentSettings().scrapeAs, localGame);
//                                        localGame.metadata.videoembed = 
                                    if(localGame.metadata == null) {
                                        //error occurred while getting metadata.
                                        updateMessage("Error connecting to thegamesdb.net. Please try again.");
                                    }
                                }
                            }
                        }
                    }

                    updateProgress(++fileCount, totalFiles);

                    getSystemGameData().remove(localGame);
                    getSystemGameData().add(localGame);

                    Game g = new Game(localGame);
                    Platform.runLater(() -> {
//                        gamesListView.getItems().remove(g);
//                        gamesListView.getItems().add(g);
                        int index = observableGamesList.indexOf(g);
                        if(index != -1) {
                            observableGamesList.remove(index);
                        }
                        observableGamesList.add(g);
                        gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(g));
//                        gamesListView.getSelectionModel().selectLast();//temporary?
                    });
                    System.out.println("ENDING SCAN OF '" + localGame.fileName + "'\n============================================================");

//                        // without sleeping, subsequent scans with cached data went so fast
//                        // i thought it was a bug. turns out the scan process is very fast,
//                        // it's downloading the data that is slow.
//                        try {
//                            //if this isn't here then i only get one update to the message box.
//                            Thread.sleep(4);
//                        }
//                        catch(InterruptedException interrupted) {
//                            if(isCancelled()) {
//                                break;
//                            }
//                        }
                }
                isScanning.set(false);
            }
            catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                Platform.runLater(() -> {
//                    ObservableList<Game> list = gamesListView.getItems();
//                    Collections.sort(list);
//                    SortedList<Game> list = new SortedList<>(gamesListView.getItems());
//                    gamesListView.setItems(list);
                    gamesListView.refresh();
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
//        private final ComboBox<String> selectGameBox;
        private final ObservableList<String> namesList;
        private final FilteredList<String> filteredNamesList;
        private final TextField filterField;
        private final ListView<String> selectGameList;
        private final Button okButton;
        private final Button cancelButton;
        private final AtomicBoolean working;
        
        private final String systemName;
        
        public SingleGameDownloadDialog(String systemName) {
            super();
            this.systemName = systemName;
        
            working = new AtomicBoolean(false);
            
            namesList = FXCollections.observableArrayList();
            filteredNamesList = new FilteredList<>(namesList);
            
//            selectGameBox = new ComboBox<>();
//            selectGameBox.getItems().add("Loading Games List...");
//            selectGameBox.getSelectionModel().select(0);
            filterField = new TextField();
        
            filterField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredNamesList.setPredicate((name) -> {
                    String filterText = newValue.trim();
                    if(filterText.isEmpty()) {
                        return true;
                    }
                    return name.toLowerCase().contains(filterText.toLowerCase());
                });
            });

            filterField.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
                if(event.getCode() == KeyCode.ESCAPE) {
                    filterField.setText("");
                }
            });

            selectGameList = new ListView<>(filteredNamesList);
            selectGameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
            selectGameList.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
                if(event.getCode() == KeyCode.ESCAPE) {
                    filterField.setText("");
                }
                else if(event.getCode() == KeyCode.BACK_SPACE) {
                    String currentText = filterField.getText();
                    filterField.setText(currentText.substring(0, currentText.length() - 1));
                }
            });

            selectGameList.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
                filterField.appendText(event.getCharacter());
            });
        
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
//            vbox.getChildren().add(selectGameBox);
            vbox.getChildren().add(filterField);
            vbox.getChildren().add(selectGameList);
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
            
//            String gameName = selectGameBox.getSelectionModel().getSelectedItem();
            String gameName = selectGameList.getSelectionModel().getSelectedItem();
            
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
                    currentGame.metadata = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB_LEGACY).getMetaData(systemName, currentGame);
                    currentGame.strength = Game.MatchStrength.LOCKED;
                                        
                    if(currentGame.metadata != null) {
                        System.out.println("Success! Checking for video links...");
                        final String[] videoLinks = DataSourceFactory.getDataSource(SourceAgent.SCREEN_SCRAPER).getVideoLinks(getCurrentSettings().scrapeAs, currentGame);
                        if(videoLinks != null) {
                            currentGame.metadata.videodownload = videoLinks[0];
                            currentGame.metadata.videoembed = videoLinks[1];
                        }
                    }
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
                        gamesListView.refresh();
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
                    List<String> gameList = DataSourceFactory.getDataSource(SourceAgent.THEGAMESDB_LEGACY).getSystemGameNames(systemName);
                    Collections.sort(gameList);
                    
                    Platform.runLater(() -> {
                        namesList.clear();
                        namesList.addAll(gameList);
//                        selectGameList.getItems().clear();
//                        selectGameList.getItems().addAll(gameList);
//                        selectGameList.getSelectionModel().select(0);
//                        selectGameBox.getItems().clear();
//                        selectGameBox.getItems().addAll(gameList);
//                        selectGameBox.getSelectionModel().select(0);
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
            
            setOnShown((event) -> {
                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();
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
                final Game realGame = getGame(item);
                if(realGame != null) {
                    setStyle("-fx-control-inner-background: " + realGame.strength.cssBackground + ";");
                    if(realGame.strength == Game.MatchStrength.LOCKED && (realGame.matchedName == null || "".equals(realGame.matchedName))) {
                        setStyle("-fx-control-inner-background: " + realGame.strength.cssBackground + ";-fx-text-fill: red");
                    }
                }
            }
        }
    }

    private static class Chooser {

        public enum DialogType {
            SAVE,
            OPEN;
        };

        private static final FileChooser FILE_CHOOSER = new FileChooser();
        private static final DirectoryChooser DIR_CHOOSER = new DirectoryChooser();

        public static File getFile(DialogType type, String title, String... fileExts) {
            FILE_CHOOSER.setTitle(title);
            FILE_CHOOSER.setInitialDirectory(new File(System.getProperty("user.home")));
            FILE_CHOOSER.getExtensionFilters().clear();

            if(fileExts != null && fileExts.length % 2 == 0) {
                for(int i = 0; i < fileExts.length; i += 2) {
                    FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExts[i], fileExts[i + 1]));
                }
            }

            File f = null;
            switch(type) {
                case SAVE:
                    f = FILE_CHOOSER.showSaveDialog(mainStage);
                    break;
                case OPEN:
                    f = FILE_CHOOSER.showOpenDialog(mainStage);
                    break;
            }
            return f;
        }

        public static File getDir(String title) {
            DIR_CHOOSER.setTitle(title);
            DIR_CHOOSER.setInitialDirectory(new File(System.getProperty("user.home")));

            File f = DIR_CHOOSER.showDialog(mainStage);
            return f;
        }
    }
}
