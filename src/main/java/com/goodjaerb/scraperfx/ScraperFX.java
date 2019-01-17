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
import com.goodjaerb.scraperfx.datasource.impl.GamesDbPrivateSource;
import com.goodjaerb.scraperfx.datasource.impl.GamesDbPublicSource;
import com.goodjaerb.scraperfx.datasource.impl.ScreenScraper2Source;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbPlatform;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperGame;
import com.goodjaerb.scraperfx.output.ESOutput;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.GameList;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.goodjaerb.scraperfx.settings.MetaData.MetaDataId;
import com.goodjaerb.scraperfx.settings.SystemSettings;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.ini4j.Ini;

/**
 *
 * @author goodjaerb
 */
public class ScraperFX extends Application {
    public static final Path   SETTINGS_PATH = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".scraperfx");
    public static final Path   LOCALDB_PATH = SETTINGS_PATH.resolve(".localdb");
    public static final Path   GAMEDATA_PATH = SETTINGS_PATH.resolve(".gamedata");
    
    private static final Insets STANDARD_INSETS = new Insets(7.);
    private static final String SCRAPERFX_CONF = "scraperfx.conf";
    private static final String KEYS_FILENAME = "keys.ini";
    private static final Ini    KEYS_INI = new Ini();
    
    private static SystemSettings   settings;
    private static String           currentSystemName;
    
    private final ListView<String>                          systemList = new ListView<>();
    private final TabPane                                   tabPane = new TabPane();
    private final Tab                                       settingsTab = new Tab("Settings");
    private final ToggleGroup                               scrapeTypeGroup = new ToggleGroup();
    private final RadioButton                               scrapeAsConsoleButton = new RadioButton("Console");
    private final RadioButton                               scrapeAsArcadeButton = new RadioButton("Arcade");
    private final ComboBox<DataSourceFactory.SourceAgent>   arcadeScraperBox = new ComboBox<>();
    private final ComboBox<String>                          consoleSelectComboBox = new ComboBox<>();
    private final TextField                                 gameSourceField = new TextField();
    private final Button                                    gameSourceBrowseButton = new Button("...");
    private final TextField                                 filenameRegexField = new TextField();
    private final TextField                                 ignoreRegexField = new TextField();
    private final TextField                                 datFilterField = new TextField();
    private final CheckBox                                  unmatchedOnlyCheckBox = new CheckBox("Don't Re-match matched files");
    private final CheckBox                                  refreshMetaDataCheckBox = new CheckBox("Refresh Matched Metadata");
    private final CheckBox                                  askForScreenScraperMatchCheckBox = new CheckBox("Ask for ScreenScraper Game Match");
    private final ToggleGroup                               outputMediaGroup = new ToggleGroup();
    private final RadioButton                               outputMediaToUserDirButton = new RadioButton("Output media to User Dir");
    private final RadioButton                               outputMediaToRomsDirButton = new RadioButton("Output media to Roms Dir");
    
    private final Tab                   gamesTab = new Tab("Games");
    private final BorderPane            gamesPane = new BorderPane();
    private final ScrollPane            imagesScroll = new ScrollPane();
    private final CheckBox              favoriteCheckBox = new CheckBox("Favorite");
    private final CheckBox              lockImagesCheckBox = new CheckBox("Lock Images");
    private final ToggleGroup           sortByGroup = new ToggleGroup();
    private final RadioButton           sortByMetaNameRadioButton = new RadioButton("Sort By Game Name");
    private final RadioButton           sortByFileNameRadioButton = new RadioButton("Sort By File Name");
    private final CheckBox              hideIgnoredCheckBox = new CheckBox("Hide Ignored Items");
    private final CheckBox              showOnlyNonMatchedCheckBox = new CheckBox("Show Only Non-Matched Items");
    private final CheckBox              hideLockedCheckBox = new CheckBox("Hide Locked Items");
    private final CheckBox              showOnlyLockedCheckBox = new CheckBox("Show Only Locked Items");
    private final CheckBox              showOnlyMissingScreenScraperIdCheckBox = new CheckBox("Missing ScreenScraper ID");
    private final TextField             filterField = new TextField();
    private final ObservableList<Game>  observableGamesList = FXCollections.observableArrayList();
    private final SortedList<Game>      sortedGamesList = new SortedList<>(observableGamesList);
    private final FilteredList<Game>    filteredGamesList = new FilteredList<>(sortedGamesList, getHideIgnoredPredicate());
    private final ListView<Game>        gamesListView = new ListView<>(filteredGamesList);
    private final TextField             matchedNameField = new TextField();
    private final Button                matchedNameBrowseButton = new Button("...");
    private final Button                matchedNameClearButton = new Button("X");
    private final CheckBox              lockMatchedNameCheckBox = new CheckBox("Lock");
    private final TextField             metaNameField = new TextField();
    private final CheckBox              lockNameCheckBox = new CheckBox("Lock");
    private final TextArea              metaDescArea = new TextArea();
    private final CheckBox              lockDescCheckBox = new CheckBox("Lock");
    private final TextField             metaRatingField = new TextField();
    private final CheckBox              lockRatingCheckBox = new CheckBox("Lock");
    private final TextField             metaReleaseDateField = new TextField();
    private final CheckBox              lockReleaseDateCheckBox = new CheckBox("Lock");
    private final TextField             metaDeveloperField = new TextField();
    private final CheckBox              lockDeveloperCheckBox = new CheckBox("Lock");
    private final TextField             metaPublisherField = new TextField();
    private final CheckBox              lockPublisherCheckBox = new CheckBox("Lock");
    private final TextField             metaGenreField = new TextField();
    private final CheckBox              lockGenreCheckBox = new CheckBox("Lock");
    private final TextField             playersField = new TextField();
    private final CheckBox              lockPlayersCheckBox = new CheckBox("Lock");
    private final TextField             screenScraperIdField = new TextField();
    private final CheckBox              lockScreenScraperIdCheckBox = new CheckBox("Lock");
    private final TextField             videoEmbedField = new TextField();
    private final CheckBox              lockVideoEmbedCheckBox = new CheckBox("Lock");
    private final TextField             videoDownloadField = new TextField();
    private final CheckBox              lockVideoDownloadCheckBox = new CheckBox("Lock");
    
    private final Button saveButton = new Button("Save");
    private final Button applyDatFileButton = new Button("Apply DAT File");
//    private final Button scanButton = new Button("Scan Now");;
    private final Button refreshFileListButton = new Button("Refresh File List");
    private final Button deleteSystemButton = new Button("Delete System");
    private final Button outputToGamelistButton = new Button("Output to Gamelist.xml");
    
    private final List<ImageLoadingTask>    imageTaskList = new ArrayList<>();
    private final Map<String, GameList>     gamedata = new HashMap<>();
    private final MenuBar                   menuBar;
    private final Scene                     rootScene;
    
    private Game            currentGame;
    private ScanTaskBase    scanTask;
    
    public ScraperFX() {
        final StackPane root = new StackPane();
        rootScene = new Scene(root);
        
        scrapeAsConsoleButton.setToggleGroup(scrapeTypeGroup);
        scrapeAsConsoleButton.setSelected(true);
        scrapeAsArcadeButton.setToggleGroup(scrapeTypeGroup);
        
        consoleSelectComboBox.setEditable(false);
        
        gameSourceField.setEditable(false);
        
        outputMediaToUserDirButton.setToggleGroup(outputMediaGroup);
        outputMediaToUserDirButton.setSelected(true);
        outputMediaToRomsDirButton.setToggleGroup(outputMediaGroup);
        
        sortByMetaNameRadioButton.setToggleGroup(sortByGroup);
        sortByFileNameRadioButton.setToggleGroup(sortByGroup);
        
        gamesListView.setCellFactory((ListView<Game> list) -> new GameListCell());
        
        matchedNameField.setEditable(false);
        matchedNameClearButton.setStyle("-fx-font-weight: bold");
        matchedNameClearButton.setTextFill(Color.RED);
        
        metaDescArea.setWrapText(true);
        
        /*
        * System List
        */
        systemList.setPrefSize(175., 500.);
        systemList.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            final String newSelection = systemList.getSelectionModel().getSelectedItem();
            if(newSelection != null && !newSelection.equals(currentSystemName)) {
                currentSystemName = newSelection;
                loadCurrentSettings();
                clearCurrentGameFields();
                enableTabs();
                currentGame = null;
            }
        });
                
        final MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction((e) -> {
            final TextInputDialog renameDialog = new TextInputDialog();
            renameDialog.setHeaderText("Rename system '" + currentSystemName + "'");
            renameDialog.setTitle("Rename System");
            renameDialog.setContentText("Name:");
            
            final Optional<String> result = renameDialog.showAndWait();
            if(result.isPresent()) {
                settings.renameSystem(currentSystemName, result.get());
                loadSystemList(result.get());
            }
        });
        systemList.setContextMenu(new ContextMenu(renameItem));
        
        final Button addSystemButton = new Button("Add System");
        addSystemButton.setOnAction((e) -> addSystemButtonActionPerformed() );
        
        final VBox box2 = new VBox(systemList, addSystemButton);
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
            final File dir = Chooser.getDir("Game Source Directory", rootScene.getWindow());
            if(dir != null) {
                gameSourceField.setText(dir.getPath());
                getCurrentSettings().romsDir = dir.getPath();
                getSystemGameData().clear();
                
                final FileSystem fs = FileSystems.getDefault();
                final Path gamesPath = fs.getPath(gameSourceField.getText());
                if(Files.exists(gamesPath)) {
                    final List<Path> paths = new ArrayList<>();
                    final DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
                    try(final DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                        for(final Path p: stream) {
                            paths.add(p);
                        }
                    }
                    catch(IOException ex) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    observableGamesList.clear();
                    paths.forEach((p) -> {
                        final Game g = new Game(p.getFileName().toString());
                        getSystemGameData().add(g);
                        observableGamesList.add(g);
                    });
                }
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
        
        datFilterField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                //lost focus
                getCurrentSettings().datFilter = datFilterField.getText();
            }
        });
        
        unmatchedOnlyCheckBox.setPadding(STANDARD_INSETS);
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
        
        refreshMetaDataCheckBox.setPadding(STANDARD_INSETS);
        refreshMetaDataCheckBox.setDisable(false);
        
        askForScreenScraperMatchCheckBox.setPadding(STANDARD_INSETS);

        final HBox box21 = new HBox();
        box21.setSpacing(7.);
        box21.setPadding(STANDARD_INSETS);
        
        box21.getChildren().addAll(new Label("Scrap as:"), scrapeAsConsoleButton, scrapeAsArcadeButton, arcadeScraperBox);
        
        final HBox selectConsoleBox = new HBox();
        selectConsoleBox.setSpacing(7.);
        selectConsoleBox.setPadding(STANDARD_INSETS);
        selectConsoleBox.getChildren().add(new Label("Select Console:"));
        selectConsoleBox.getChildren().add(consoleSelectComboBox);
        
        final VBox settingsPane = new VBox();
        settingsPane.getChildren().addAll(
                box21,
                selectConsoleBox,
                createBrowseFieldPane("Game Source Directory:", gameSourceField, gameSourceBrowseButton),
                createBrowseFieldPane("Regex for Substring Removal (Advanced):", filenameRegexField),
                createBrowseFieldPane("Regex for Files to Ignore (Advanced):", ignoreRegexField),
                createBrowseFieldPane("DAT Filter (Advanced):", datFilterField),
                unmatchedOnlyCheckBox,
                refreshMetaDataCheckBox,
                askForScreenScraperMatchCheckBox,
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
            final Game selected = gamesListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, selected.fileName);
                currentGame = getGame(selected);
                loadCurrentGameFields(currentGame);
            }
        });
        
        gamesListView.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                filterField.setText("");
            }
            else if(e.getCode() == KeyCode.BACK_SPACE && !filterField.getText().isEmpty()) {
                final String currentText = filterField.getText();
                filterField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        
        gamesListView.addEventHandler(KeyEvent.KEY_TYPED, (e) -> {
            filterField.appendText(e.getCharacter());
        });
        
        final MenuItem lockGamesItem = new MenuItem("Lock");
        lockGamesItem.setOnAction((e) -> {
            final ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.forEach((g) -> {
                getGame(g).strength = Game.MatchStrength.LOCKED;
            });
            gamesListView.refresh();
            loadCurrentGameFields(currentGame);
        });
        
        final MenuItem unlockGamesItem = new MenuItem("Unlock");
        unlockGamesItem.setOnAction((e) -> {
            final ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.forEach((g) -> {
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
        
        final MenuItem ignoreItem = new MenuItem("Ignore");
        ignoreItem.setOnAction((e) -> {
            final ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.forEach((g) -> {
                final Game realGame = getGame(g);
                realGame.matchedName = null;
                realGame.metadata = null;
                realGame.strength = Game.MatchStrength.IGNORE;
            });
            gamesListView.refresh();
        });
        
        final MenuItem unignoreItem = new MenuItem("Unignore");
        unignoreItem.setOnAction((e) -> {
            final ObservableList<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            selectedGames.forEach((g) -> {
                getGame(g).strength = Game.MatchStrength.NO_MATCH;
            });
            gamesListView.refresh();
        });
        
        final MenuItem scanGamesItem = new MenuItem("Scan Selected Game(s)");
        scanGamesItem.setOnAction((e) -> {
            final List<Game> selectedGames = gamesListView.getSelectionModel().getSelectedItems();
            
            final FileSystem fs = FileSystems.getDefault();
            final Path gamesPath = fs.getPath(gameSourceField.getText());
            if(Files.exists(gamesPath)) {
                if(scrapeAsArcadeButton.isSelected()) {
                    scanTask = new SequentialArcadeScanTask(gamesPath, selectedGames);
//                    final ChoiceDialog<String> dialog = new ChoiceDialog<>("Standard Sequential Scan", "Standard Sequential Scan", "Arcade Sequential Scan (Testing)");
//                    dialog.showAndWait();
//                    
//                    String result = dialog.getResult();
//                    if(result != null) {
//                        switch (result) {
//                            case "Standard Sequential Scan":
//                                scanTask = new SequentialConsoleScanTask(gamesPath, selectedGames);
//                                break;
//                            case "Arcade Sequential Scan (Testing)":
//                                scanTask = new SequentialArcadeScanTask(gamesPath, selectedGames);
//                                break;
//                            default:
//                                scanTask = null;
//                                break;
//                        }
//                    }
                }
                else {
                    scanTask = new SequentialConsoleScanTask(gamesPath, selectedGames);
//                    scanTask = new ForkJoinConsoleScanTask(gamesPath, selectedGames);
                }
                
                if(scanTask != null) {
                    ScanProgressDialog scanProgressDialog = new ScanProgressDialog(gamesPath, selectedGames, gamesListView.getScene().getWindow());
                    scanProgressDialog.showAndWait();
                }
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
        
        showOnlyMissingScreenScraperIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredGamesList.setPredicate(buildFilterPredicate());
        });
        
        filterField.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if(e.getCode() == KeyCode.ESCAPE) {
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
                final SingleGameDownloadDialog d = new SingleGameDownloadDialog(getCurrentSettings().scrapeAs, matchedNameBrowseButton.getScene().getWindow());
                d.showAndWait();
                if(!d.wasCancelled()) {
                    loadCurrentGameFields(currentGame);
                    final Game updatedGame = observableGamesList.remove(observableGamesList.indexOf(currentGame));
                    observableGamesList.add(updatedGame);
                    gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(updatedGame));
                }
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
            final Game updatedGame = observableGamesList.remove(observableGamesList.indexOf(currentGame));
            observableGamesList.add(updatedGame);
            gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(updatedGame));
        });
        lockDescCheckBox.setOnAction((e) ->             lockMetaField(metaDescArea, MetaDataId.DESC, currentGame.metadata, lockDescCheckBox.isSelected()));
        lockDeveloperCheckBox.setOnAction((e) ->        lockMetaField(metaDeveloperField, MetaDataId.DEVELOPER, currentGame.metadata, lockDeveloperCheckBox.isSelected()));
        lockGenreCheckBox.setOnAction((e) ->            lockMetaField(metaGenreField, MetaDataId.GENRE, currentGame.metadata, lockGenreCheckBox.isSelected()));
        lockPublisherCheckBox.setOnAction((e) ->        lockMetaField(metaPublisherField, MetaDataId.PUBLISHER, currentGame.metadata, lockPublisherCheckBox.isSelected()));
        lockRatingCheckBox.setOnAction((e) ->           lockMetaField(metaRatingField, MetaDataId.RATING, currentGame.metadata, lockRatingCheckBox.isSelected()));
        lockReleaseDateCheckBox.setOnAction((e) ->      lockMetaField(metaReleaseDateField, MetaDataId.RELEASE_DATE, currentGame.metadata, lockReleaseDateCheckBox.isSelected()));
        lockPlayersCheckBox.setOnAction((e) ->          lockMetaField(playersField, MetaDataId.PLAYERS, currentGame.metadata, lockPlayersCheckBox.isSelected()));
        lockImagesCheckBox.setOnAction((e) -> {         currentGame.metadata.lockImages = lockImagesCheckBox.isSelected(); });
        lockScreenScraperIdCheckBox.setOnAction((e) ->  lockMetaField(screenScraperIdField, MetaDataId.SCREEN_SCRAPER_ID, currentGame.metadata, lockScreenScraperIdCheckBox.isSelected()));
        lockVideoEmbedCheckBox.setOnAction((e) ->       lockMetaField(videoEmbedField, MetaDataId.VIDEO_EMBED, currentGame.metadata, lockVideoEmbedCheckBox.isSelected()));
        lockVideoDownloadCheckBox.setOnAction((e) ->    lockMetaField(videoDownloadField, MetaDataId.VIDEO_DOWNLOAD, currentGame.metadata, lockVideoDownloadCheckBox.isSelected()));
        favoriteCheckBox.setOnAction((e) -> {           currentGame.metadata.favorite = favoriteCheckBox.isSelected(); gamesListView.refresh(); });
        
        final VBox metaBox = new VBox();
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
                createMetaFieldPane("ScreenScraper ID:", screenScraperIdField, lockScreenScraperIdCheckBox),
                createMetaFieldPane("Video Embed URL:", videoEmbedField, lockVideoEmbedCheckBox),
                createMetaFieldPane("Video Download URL:", videoDownloadField, lockVideoDownloadCheckBox)
        );
        
        imagesScroll.setPrefSize(200., 475.);
        
        final VBox imagesBox = new VBox();
        imagesBox.setPadding(STANDARD_INSETS);
        imagesBox.setSpacing(7.);
        imagesBox.getChildren().addAll(favoriteCheckBox, lockImagesCheckBox, imagesScroll);
        
        final VBox sortByBox = new VBox();
        sortByBox.setSpacing(7.);
        sortByBox.getChildren().addAll(sortByMetaNameRadioButton, sortByFileNameRadioButton, filterField);
        
        final VBox filterBox = new VBox();
        filterBox.setSpacing(7.);
        filterBox.getChildren().addAll(hideIgnoredCheckBox, showOnlyNonMatchedCheckBox, hideLockedCheckBox, showOnlyLockedCheckBox, showOnlyMissingScreenScraperIdCheckBox);
        
        final HBox topBox = new HBox();
        topBox.setSpacing(7.);
        topBox.getChildren().addAll(sortByBox, filterBox);
        
        final VBox leftBox = new VBox(7., topBox, gamesListView);
        VBox.setVgrow(gamesListView, Priority.ALWAYS);
        
        gamesPane.setPadding(STANDARD_INSETS);
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
            final File datFile = Chooser.getFile(Chooser.DialogType.OPEN, "Select DAT File", applyDatFileButton.getScene().getWindow(), "DAT FILE", "*.dat");
//            final List<File> files = Chooser.openFiles("Select DAT File(s)", applyDatFileButton.getScene().getWindow(), "DAT FILE", "*.dat");
            if(datFile != null) {
                try {
                    final Datafile dat = readDatFile(datFile.toPath());
    //            if(files != null) {
    //                for(final File datFile : files) {
                    final String datFilter = getCurrentSettings().datFilter;
                    observableGamesList.forEach((game) -> {
                        final String filename = game.fileName;

                        if(dat.getElements().stream().filter((element) -> {
                            if(datFilter == null || datFilter.isEmpty()) {
                                return true;
                            }
                            return !element.getRomof().isEmpty() && datFilter.contains(element.getRomof());
                        }).noneMatch((element) -> {
                            return filename.equals(element.getName() + ".zip");
                        })) {
                            game.strength = Game.MatchStrength.IGNORE;
                        }
                    });
                    gamesListView.refresh();
                }
                catch (IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
//                }
            }
        });
        
        refreshFileListButton.setOnAction((e) -> {
            final FileSystem fs = FileSystems.getDefault();
            final Path gamesPath = fs.getPath(gameSourceField.getText());
            if(Files.exists(gamesPath)) {
                final List<Path> paths = new ArrayList<>();
                final DirectoryStream.Filter<Path> filter = (Path file) -> (Files.isRegularFile(file));
                try(final DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                    for(final Path p : stream) {
                        paths.add(p);
                    }
                }
                catch(IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                paths.forEach((p) -> {
                    final Game g = new Game(p.getFileName().toString());
                    if(!observableGamesList.contains(g)) {
                        getSystemGameData().add(g);
                        observableGamesList.add(g);
                    }
                });
            }
        });
        
        outputToGamelistButton.setOnAction((e) -> {
            Collections.sort(getSystemGameData(), Game.GAME_NAME_COMPARATOR);
            final Window parentWindow = outputToGamelistButton.getScene().getWindow();
            if(outputMediaToUserDirButton.isSelected()) {
                new ESOutput().output(getSystemGameData(),
                        SETTINGS_PATH.resolve(ESOutput.GAMELISTS_DIR).resolve(getCurrentSettings().name),
                        SETTINGS_PATH.resolve("media").resolve(getCurrentSettings().name).resolve(ESOutput.IMAGES_DIR),
                        SETTINGS_PATH.resolve("media").resolve(getCurrentSettings().name).resolve(ESOutput.VIDEOS_DIR),
                        getCurrentSettings().scrapeAsArcade,
                        parentWindow);
            }
            else {
                new ESOutput().output(getSystemGameData(),
                        SETTINGS_PATH.resolve(ESOutput.GAMELISTS_DIR).resolve(getCurrentSettings().name),
                        Paths.get(getCurrentSettings().romsDir, ESOutput.IMAGES_DIR),
                        Paths.get(getCurrentSettings().romsDir, ESOutput.VIDEOS_DIR),
                        getCurrentSettings().scrapeAsArcade,
                        parentWindow);
            }
        });
        
        deleteSystemButton.setOnAction((e) -> deleteSystemButtonOnActionPerformed() );
        
        final FlowPane settingsButtonPane = new FlowPane(saveButton, applyDatFileButton, deleteSystemButton, refreshFileListButton, outputToGamelistButton);
        settingsButtonPane.setHgap(7.);
        settingsButtonPane.setAlignment(Pos.CENTER);
        
        tabPane.getTabs().add(settingsTab);
        tabPane.getTabs().add(gamesTab);
        tabPane.getSelectionModel().select(0);
        
        final BorderPane rightPane = new BorderPane();
        rightPane.setCenter(tabPane);
        rightPane.setBottom(settingsButtonPane);

        final HBox mainBox = new HBox(box2, rightPane);
        mainBox.setSpacing(7.);
        
        menuBar = new MenuBar();
        final BorderPane appPane = new BorderPane(mainBox, menuBar, null, null, null);
        BorderPane.setMargin(mainBox, STANDARD_INSETS);
        
        root.getChildren().add(appPane);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            readGameData();
            readSettings();
            KEYS_INI.load(SETTINGS_PATH.resolve(KEYS_FILENAME).toFile());
        }
        catch(IOException ex) {
            Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /**
         * Menu Bar
         */
        final MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setOnAction((event) -> {
            if(exitCheck()) {
                primaryStage.hide();
            }
        });
        
        final Menu fileMenu = new Menu("_File", null, exitMenuItem);
        
//        final MenuItem updateConsolePlatforms = new MenuItem("Update _Platforms (Console)");
//        updateConsolePlatforms.setOnAction((event) -> {
//            // Show a warning about api key limit.
//            // Connect and download the new Platforms list.
//        });

        menuBar.getMenus().addAll(fileMenu);

        if(ScraperFX.getKeysValue("GamesDb.Private") != null) {
            final MenuItem downloadGamesByPlatformMenuItem = new MenuItem("Download Games by Platform");
            downloadGamesByPlatformMenuItem.setOnAction((event) -> {
                try {
                    final List<GamesDbPlatform> platforms = DataSourceFactory.get(SourceAgent.THEGAMESDB, GamesDbPublicSource.class).getPlatforms();
                    final ChoiceDialog<GamesDbPlatform> choiceDialog = new ChoiceDialog<>(platforms.get(0), platforms);
                    choiceDialog.setContentText("This operation will use a portion of your one-time-use private key to TheGamesDB.\nDo not perform this operation for a platform you already have a local backup of.");
                    
                    final Optional<GamesDbPlatform> result = choiceDialog.showAndWait();
                    if(result.isPresent()) {
                        // use SourceAgent.THEGAMESDB_PRIVATE to download GamesByPlatform
                        DataSourceFactory.get(SourceAgent.THEGAMESDB_PRIVATE, GamesDbPrivateSource.class).populateGamesByPlatform(Integer.toString(result.get().id));
                        System.out.println("FROM ScraperFX!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println(DataSourceFactory.get(SourceAgent.THEGAMESDB).getSystemGameNames(result.get().name));
                        System.out.println("END FROM ScraperFX!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            menuBar.getMenus().add(new Menu("Admin", null, downloadGamesByPlatformMenuItem));
        }
        
        
        primaryStage.setOnCloseRequest((event) -> {
            if(!exitCheck()) {
                event.consume();
            }
        });
        
//        primaryStage.setOnShown((event) -> {
//            Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, "primaryStage.shown()");
//            // check if local DB exists.
//            // 
//        });
        
        new Thread(() -> {
            try {
                final List<String> consoles = DataSourceFactory.get(SourceAgent.THEGAMESDB).getSystemNames();
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
        
        
        primaryStage.setTitle("ScraperFX");
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
    
    public static String getKeysValue(String name) {
        return KEYS_INI.get("Keys", name);
    }
    
    private boolean exitCheck() {
        final ButtonType saveThenQuitButton = new ButtonType("Save & Quit");
        final ButtonType dontQuitButton = new ButtonType("Wait, Don't Quit!");
        final ButtonType justQuitButton = new ButtonType("Just Quit, Thanks.");

        final Alert closingAlert = new Alert(Alert.AlertType.CONFIRMATION, "ScraperFX is about to close. If you have made any new scans or settings changes you may want to save. Save now?", saveThenQuitButton, dontQuitButton, justQuitButton);
        final Optional<ButtonType> result = closingAlert.showAndWait();

        if(result.isPresent()) {
            if(result.get() == saveThenQuitButton) {
                saveAll();
            }
            else if(result.get() == dontQuitButton) {
                return false;
            }
        }
        return true;
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
    
    private Predicate<Game> buildFilterPredicate() {
        return getHideIgnoredPredicate().and(
                getShowOnlyNonMatchedPredicate()).and(
                getFilterTextPredicate(filterField.getText())).and(
                getHideLockedPredicate()).and(
                getShowOnlyLockedPredicate()).and(
                getShowOnlyMissingScreenScraperIdPredicate());
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
                return game.strength == Game.MatchStrength.NO_MATCH || game.strength == Game.MatchStrength.IGNORE || game.matchedName == null || "".equals(game.matchedName);
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
    
    private Predicate<Game> getShowOnlyMissingScreenScraperIdPredicate() {
        if(showOnlyMissingScreenScraperIdCheckBox.isSelected()) {
            return (game) -> {
                return game.metadata == null || game.metadata.screenScraperId == null || game.metadata.screenScraperId.isEmpty();
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
                Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, "Downloading image from {0}...", url);
                final URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                final BufferedImage img = ImageIO.read(connection.getInputStream());
                return SwingFXUtils.toFXImage(img, null);
            }
            catch(MalformedURLException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch(IOException ex) {
                if(++retry < 3) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.WARNING, "Error retrieving image from {0}. Retrying...", url);
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
        
        final File outputFile = new File(path.toString() + File.separator + filename);
        if(outputFile.exists()) {
            return true;
        }
        else {
            int retry = 0;
            while(retry < 3) {
                try {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, "Downloading video from {0}...", url);
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
                    
        final File outputFile = new File(path.toString() + File.separator + imageFileName + "." + imageOutputType);
        if(outputFile.exists()) {
            return true;
        }
        else {
            int retry = 0;
            while(retry < 3) {
                try {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, "Downloading image from {0}...", url);
                    final URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    final BufferedImage img = ImageIO.read(connection.getInputStream());
                    
                    int width = 640;
                    if(img.getWidth() <= 640) {
                        width = img.getWidth();
                    }
                    
                    final java.awt.Image scaled = img.getScaledInstance(width, -1, java.awt.Image.SCALE_SMOOTH);
                    final BufferedImage scaledBuff = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
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
        final Label label = new Label(labelStr);
        label.setPadding(new Insets(7., 7., 0., 7.));
        if(field instanceof TextField) {
            field.setPrefWidth(275.);
        }
        else if(field instanceof TextArea) {
            field.setPrefSize(275., 125.);
        }
        
        final FlowPane pane = new FlowPane();
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
        
        final VBox box = new VBox(label, pane);
        return box;
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field) {
        return createBrowseFieldPane(labelStr, field, null, null);
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field, Button browseButton) {
        return createBrowseFieldPane(labelStr, field, null, browseButton);
    }
    
    private Pane createBrowseFieldPane(String labelStr, TextField field, Button clearButton, Button browseButton) {
        final Label label = new Label(labelStr);
        label.setPadding(new Insets(7., 7., 0., 7.));
        field.setPrefWidth(275.);
        
        final FlowPane pane = new FlowPane();
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
        
        final VBox box = new VBox(label, pane);
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
        if(!gamedata.containsKey(systemName)) {
            gamedata.put(systemName, new GameList(systemName));
        }
        return gamedata.get(systemName).games;
    }
    
    //gets the reference to game out of the gamedata that this game is referring to... if that makes sense!
    private Game getGame(Game g) {
        final List<Game> games = getSystemGameData();
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
        screenScraperIdField.clear();
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
        lockScreenScraperIdCheckBox.setSelected(false);
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
                screenScraperIdField.setText(g.metadata.screenScraperId);
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
                lockScreenScraperIdCheckBox.setSelected(g.metadata.lockScreenScraperId);
                screenScraperIdField.setEditable(!g.metadata.lockScreenScraperId);
                lockVideoEmbedCheckBox.setSelected(g.metadata.lockVideoEmbed);
                videoEmbedField.setEditable(!g.metadata.lockVideoEmbed);
                lockVideoDownloadCheckBox.setSelected(g.metadata.lockVideoDownload);
                videoDownloadField.setEditable(!g.metadata.lockVideoDownload);

                lockImagesCheckBox.setSelected(g.metadata.lockImages);
                favoriteCheckBox.setSelected(g.metadata.favorite);

                if(scanTask == null || !scanTask.isRunning()) {
//                    Platform.runLater(() -> {
                    new Thread(() -> {
                        imageTaskList.forEach(task -> task.cancel());
                        imageTaskList.clear();

                        if(g.metadata != null && g.metadata.images != null && !g.metadata.images.isEmpty()) {
                            final MetaImageViewBox box = new MetaImageViewBox();

                            g.metadata.images.stream().filter((image) -> (image != null)).map((image) -> {
                                final MetaImageView metaImage = new MetaImageView(image);
                                box.addView(metaImage);
                                final ImageLoadingTask task = new ImageLoadingTask(metaImage, image);
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

                            Platform.runLater(() -> {
                                imagesScroll.setContent(box);
                            });
                        }
                    }).start();
//                    });
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
        datFilterField.setText(getCurrentSettings().datFilter);
        unmatchedOnlyCheckBox.setSelected(getCurrentSettings().unmatchedOnly);
        
        observableGamesList.clear();
        if(getSystemGameData() != null) {
            observableGamesList.addAll(getSystemGameData());
        }
    }
    
    private void loadSystemList(String selectAfter) {
        systemList.getItems().clear();
        settings.systems.forEach(system -> systemList.getItems().add(system.name));
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
        datFilterField.clear();
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
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a name for your system.\n\nIf you are using EmulationStation, it is\nsuggested you name your systems to match\nyour rom folder names.");
        dialog.setContentText("Name:");
        dialog.setTitle("Name System");
        final Optional<String> result = dialog.showAndWait();
        
        if(result.isPresent() && result.get().trim().length() > 0) {
            final String name = result.get();
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
        final Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove '" + currentSystemName + "' from your systems?", ButtonType.YES, ButtonType.CANCEL).showAndWait();

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
        final Path scraperfxConfPath = SETTINGS_PATH.resolve(SCRAPERFX_CONF);
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
        final Gson gson = new Gson();
        
        try(final Stream<Path> stream = Files.list(GAMEDATA_PATH)) {
            stream.forEach((path) -> {
                try(BufferedReader reader = Files.newBufferedReader(path)) {
                    final GameList gameList = gson.fromJson(reader, GameList.class);
                    gamedata.put(gameList.system, gameList);
                }
                catch (IOException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
    
    private void writeGameData() throws IOException {
//        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final Gson gson = new Gson();
        
        for(final GameList gameList : gamedata.values()) {
            if(!gameList.games.isEmpty()) {
                final Path gamedataPath = GAMEDATA_PATH.resolve(gameList.system + ".json");
                if(!Files.exists(gamedataPath)) {
                    Files.createDirectories(gamedataPath.getParent());
                    Files.createFile(gamedataPath);
                }
                
                try(final BufferedWriter writer = Files.newBufferedWriter(gamedataPath)) {
                    gson.toJson(gameList, writer);
                    writer.flush();
                }
            }
        }
    }
    
    private void writeSettings() throws IOException {
        final Path settingsPath = SETTINGS_PATH.resolve(SCRAPERFX_CONF);
        if(!Files.exists(settingsPath)) {
            Files.createDirectories(settingsPath.getParent());
            Files.createFile(settingsPath);
        }
        
        final Xmappr xm = new Xmappr(SystemSettings.class);
        xm.setPrettyPrint(true);
        try(final BufferedWriter writer = Files.newBufferedWriter(settingsPath)) {
            xm.toXML(settings, writer);
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
        private final List<MetaImageView> views = new ArrayList<>();
        
        public MetaImageViewBox() {
            super();
            setSpacing(7.);
            setPadding(STANDARD_INSETS);
        }
        
        public void selectImage(com.goodjaerb.scraperfx.settings.Image image) {
            currentGame.metadata.selectImage(image);
            views.stream().forEach(view -> {
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
        private final ImageView view = new ImageView("images/loading.gif");
        
        private final com.goodjaerb.scraperfx.settings.Image image;
        
        public MetaImageView(com.goodjaerb.scraperfx.settings.Image image) {
            super();
            this.image = image;
            
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
    
    private abstract class ScanTaskBase extends Task<Void> {
        private final Path gamesPath;
        private final List<Game> games;
        private final List<Path> paths = new ArrayList<>();
        
        protected Consumer<String> status;
        
        public ScanTaskBase(Path gamesPath, List<Game> games) {
            this.gamesPath = gamesPath;
            this.games = games;
        }
        
        protected abstract void scan(List<ScanTaskOperation> ops);
        
        public void setStatusUpdater(Consumer<String> status) {
            this.status = status;
        }
        
        @Override
        public Void call() {
            // Don't know if this should be here or the start of call().
            final DirectoryStream.Filter<Path> filter = path -> Files.isRegularFile(path) && games.stream().anyMatch((game) -> path.getFileName().toString().equals(game.fileName));
            try(final DirectoryStream<Path> stream = Files.newDirectoryStream(gamesPath, filter)) {
                stream.forEach(path -> {
                    status.accept("Found '" + path + "'.");
                    paths.add(path);
                });
            }
            catch(IOException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            final List<ScanTaskOperation> ops = new ArrayList<>();
            final long startTime = System.nanoTime();
            
            /**
             * Step 1: Set up list of ScanTaskOperations.
             */
            for(final Path p : paths) {
                if(isCancelled()) {
                    break;
                }
                
                final String filename = p.getFileName().toString();
                Game localGame = getGame(new Game(filename));
                if(localGame == null) {
                    localGame = new Game(filename);
                }

                if(localGame.strength == Game.MatchStrength.IGNORE) {
                    status.accept("Ignoring '" + localGame.fileName + "' due to PRESET IGNORE flag.");
                    //loop continues without adding this game.
                }
                else {
                    boolean ignore = false;
                    if(getCurrentSettings().ignoreRegex != null && !"".equals(getCurrentSettings().ignoreRegex)) {
                        final Pattern ignorePattern = Pattern.compile(".*" + getCurrentSettings().ignoreRegex + ".*");
                        final Matcher ignoreMatcher = ignorePattern.matcher(filename);
                        if(ignoreMatcher.matches()) {
                            localGame.matchedName = null;
                            localGame.strength = Game.MatchStrength.IGNORE;
                            ignore = true;

                            status.accept("Ignoring '" + localGame.fileName + "' due to REGEX IGNORE flag.");
                            //loop continues without adding this game.
                        }
                    }

                    if(!ignore) {
                        boolean refreshMatchedGame = false;
                        boolean skipMatching = false;
                        boolean startedUnmatched = false;

                        if(localGame.strength == Game.MatchStrength.NO_MATCH) {
                            startedUnmatched = true;
                        }

                        if(localGame.strength == Game.MatchStrength.LOCKED) {
                            skipMatching = true;
                            status.accept("Not matching '" + localGame.fileName + "' because it is LOCKED.");
                        }
                        else if(unmatchedOnlyCheckBox.isSelected() 
                                && localGame.strength != Game.MatchStrength.IGNORE && localGame.strength != Game.MatchStrength.NO_MATCH) {
                            // this game is already matched and we only want unmatched games.
                            skipMatching = true;

                            status.accept("Not matching '" + localGame.fileName + "' because it is ALREADY MATCHED.");
                        }
                        else {
                            status.accept("Will attempt to match '" + localGame.fileName + "'.");
                        }

                        if(!startedUnmatched && !refreshMetaDataCheckBox.isDisabled()) {
                            if(refreshMetaDataCheckBox.isSelected()) {
                                refreshMatchedGame = true;

                                status.accept("Refreshing metadata for '" + localGame.fileName + "'.");
                            }
                            else {
                                status.accept("NOT refreshing metadata for '" + localGame.fileName + "' due to check-box (un)selection.");
                            }
                        }
                        
                        ops.add(new ScanTaskOperation(skipMatching, refreshMatchedGame, startedUnmatched, localGame));
                    }
                }
            }
            
            /**
             * Step 2: Perform operations.
             */
            scan(ops);
            
            
            /**
             * Step 3: Finish up.
             */
            status.accept(Duration.ofNanos(System.nanoTime() - startTime).toString());
                
            Platform.runLater(() -> {
                gamesListView.refresh();
            });
            return null;
        }
    }
    
    private class ScanTaskOperation {
        
        public final boolean skipMatching;
        public final boolean refreshMetaData;
        public final boolean startedUnmatched;
        public final Game game;
        
        public ScanTaskOperation(boolean skipMatching, boolean refreshMetaData, boolean startedUnmatched, Game game) {
            this.skipMatching = skipMatching;
            this.refreshMetaData = refreshMetaData;
            this.startedUnmatched = startedUnmatched;
            this.game = game;
        }
    }
    
    private class SequentialArcadeScanTask extends ScanTaskBase {
        
        public SequentialArcadeScanTask(Path gamesPath, List<Game> games) {
            super(gamesPath, games);
        }
        
        @Override
        protected void scan(List<ScanTaskOperation> ops) {
            final int totalFiles = ops.size();
            int completedCount = 0;
            
            for(final ScanTaskOperation op : ops) {
                if(isCancelled()) {
                    break;
                }
                
                final boolean skipMatching = op.skipMatching;
                final boolean refreshMatchedGame = op.refreshMetaData;
                final boolean startedUnmatched = op.startedUnmatched;
                
                final Game localGame = op.game;
                final String filename = localGame.fileName;
                final String noExtName = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();

                if(!skipMatching) {
                    localGame.matchedName = noExtName;
                }

                if(refreshMatchedGame || startedUnmatched) {
                    try {
                        MetaData newMetaData = DataSourceFactory.get(arcadeScraperBox.getValue()).getMetaData(null, localGame);

                        if(newMetaData != null) {
                            if(!skipMatching) {
                                status.accept("Matched '" + filename + "' to game '" + newMetaData.metaName + "'.");
                            }
                        }
                        else {
                            localGame.matchedName = null;
                            if(localGame.strength == Game.MatchStrength.LOCKED && localGame.metadata != null) {
                                newMetaData = new MetaData();
                                newMetaData.setMetaData(localGame.metadata);
                                status.accept("Metadata came back NULL; Restored previous metadata for LOCKED game '" + filename + "' (" + localGame.metadata.metaName + ").");
                            }
                            else {
                                status.accept("Could not match '" + filename + "' to a game.");
                            }
                        }

                        if(newMetaData != null) {
                            if(localGame.metadata != null && localGame.metadata.favorite) {
                                newMetaData.favorite = true;
                            }
                            if(localGame.strength != Game.MatchStrength.LOCKED) {
                                localGame.strength = Game.MatchStrength.STRONG;
                            }
                            localGame.updateMetaData(newMetaData);
                            status.accept("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                        }
                    }
                    catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                updateProgress(++completedCount, totalFiles);

                getSystemGameData().remove(localGame);
                getSystemGameData().add(localGame);
                
                Platform.runLater(() -> {
                    final int index = observableGamesList.indexOf(localGame);
                    if(index != -1) {
                        observableGamesList.remove(index);
                    }
                    observableGamesList.add(localGame);
                    gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(localGame));
                });
            }
        }
    }
    
    private abstract class ConsoleScanTaskBase extends ScanTaskBase {
        
        public ConsoleScanTaskBase(Path gamesPath, List<Game> games) {
            super(gamesPath, games);
        }
        
        public Game match(ScanTaskOperation op) {
            final boolean skipMatching = op.skipMatching;
            final boolean refreshMatchedGame = op.refreshMetaData;
            final boolean startedUnmatched = op.startedUnmatched;

            final Game localGame = op.game;
            final String filename = localGame.fileName;
            String noExtName = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();

            try {
                if(!skipMatching) {
                    if(getCurrentSettings().substringRegex != null && !"".equals(getCurrentSettings().substringRegex)) {
                        final Pattern pattern = Pattern.compile(".*" + getCurrentSettings().substringRegex + ".*");
                        final Matcher m = pattern.matcher(noExtName);
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
                    final List<String> hitNames = new ArrayList<>();
                    final List<Double> hitPercents = new ArrayList<>();
                    final List<Double> remoteHitPercents = new ArrayList<>();
                    int mostPartsHit = 0;
                    boolean hitPartIsNumber = false;

                    final List<String> allSysGames = DataSourceFactory.get(SourceAgent.THEGAMESDB).getSystemGameNames(getCurrentSettings().scrapeAs);
                    for(final String gameName : allSysGames) {
                        String lowerCaseName = gameName.toLowerCase().replaceAll(" - ", " ");
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
                            final String[] localParts = noExtName.split("\\s");
                            final String[] split = lowerCaseName.split("\\s");

                            int hits = 0;
                            int hitLength = 0;
                            for(final String split1 : split) {
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
                            final List<String> tieNames = new ArrayList<>();

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

                    if(localGame.matchedName == null) {
                        status.accept("Could not match '" + filename + "' to a game.");
                    }
                    else {
                        status.accept("Matched '" + filename + "' to game '" + localGame.matchedName + "'.");
                    }
                }

                if(localGame.matchedName != null) {
                    if(!skipMatching || refreshMatchedGame || startedUnmatched) {
                        //matched a game, get the rest of the data.
                        MetaData newMetaData = DataSourceFactory.get(SourceAgent.THEGAMESDB).getMetaData(getCurrentSettings().scrapeAs, localGame);
                        if(newMetaData == null && localGame.strength == Game.MatchStrength.LOCKED && localGame.metadata != null) {
                            newMetaData = new MetaData();
                            newMetaData.setMetaData(localGame.metadata);

                            status.accept("Metadata came back NULL; Restored previous metadata for LOCKED game '" + filename + "' (" + localGame.metadata.metaName + ").");
                        }

                        if(newMetaData != null) {
                            if(localGame.metadata != null && localGame.metadata.favorite) {
                                newMetaData.favorite = true;
                            }

                            getExtraMetaData(newMetaData, getCurrentSettings().scrapeAs, localGame);

                            localGame.updateMetaData(newMetaData);
                            status.accept("Refreshed metadata for '" + filename + "' (" + localGame.metadata.metaName + ").");
                        }
                    }

                    if(localGame.metadata == null) {
                        //error occurred while getting metadata.
                        status.accept("Error connecting to thegamesdb.net. Please try again.");
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return localGame;
        }
    }
    
    private void getExtraMetaData(MetaData newMetaData, String systemName, Game localGame) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        final List<ScreenScraperGame> screenScraperResults = DataSourceFactory.get(SourceAgent.SCREEN_SCRAPER, ScreenScraper2Source.class).getExtraMetaData(systemName, localGame);
        if(screenScraperResults != null && !screenScraperResults.isEmpty()) {
            ScreenScraperGame theGame = null;

            // if already have a ScreenScraper ID, check that it showed up in the results.
            if(localGame.metadata != null && localGame.metadata.screenScraperId != null && !localGame.metadata.screenScraperId.isEmpty()) {
                for(ScreenScraperGame ssGame : screenScraperResults) {
                    if(localGame.metadata.screenScraperId.equals(ssGame.id)) {
                        theGame = ssGame;
                        break;
                    }
                }
            }
            
            // check for a name match.
            if(theGame == null) {
                for(ScreenScraperGame ssGame : screenScraperResults) {
                    if(ssGame.noms.stream().anyMatch((ScreenScraperGame.NameData nameData) -> {
                        final String name1 = nameData.text.replaceAll("\\p{Punct}", "").replaceAll("\\s{2,}", " ").toLowerCase().trim();
                        final String name2 = localGame.matchedName.replaceAll("\\p{Punct}", "").replaceAll("\\s{2,}", " ").toLowerCase().trim();
                        
                        final boolean result = name1.equals(name2);
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.INFO, "\"{0}\"==\"{1}\" {2}", new Object[]{name1, name2, result});
                        return result;
                    })) {
                        // exact name match, pick this game.
                        theGame = ssGame;
                        break;
                    }
                }
            }

            if(theGame == null) {
                if(!askForScreenScraperMatchCheckBox.isSelected()) {
                    // don't want to halt scanning to ask so i'll return here and not
                    // alter any possible current values.
                    return;
                }
                else {
                    final ScreenScraperResultHolder holder = new ScreenScraperResultHolder();
                    Platform.runLater(() -> {
                        final ChoiceDialog<ScreenScraperGame> choices = new ChoiceDialog<>(screenScraperResults.get(0), screenScraperResults);
                        choices.setTitle("Select ScreenScraper Result");
                        choices.setContentText("Matched Game: " + localGame.matchedName);
                        final Optional<ScreenScraperGame> choice = choices.showAndWait();

                        synchronized(holder) {
                            if(choice.isPresent()) {
                                holder.setResult(choice);
                            }
                            holder.notify();
                        }
                    });
                    try {
                        synchronized(holder) {
                            holder.wait();
                        }
                    }
                    catch (InterruptedException ex) {
                        Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if(holder.get() != null && holder.get().isPresent()) {
                        theGame = holder.get().get();
                    }
                }
            }
            
            if(theGame == null) {
                newMetaData.screenScraperId = null;
                newMetaData.videodownload = null;
                newMetaData.videoembed = null;
                newMetaData.removeImagesFromSource(ScreenScraper2Source.SOURCE_NAME);
            }
            else {
                newMetaData.screenScraperId = theGame.id;
                if(theGame.medias != null && !theGame.medias.isEmpty()) {
                    final List<ScreenScraperGame.Media> medias = theGame.medias;
                    for(ScreenScraperGame.Media media : medias) {
                        switch(media.type) {
                            case "video":
                                newMetaData.videodownload = media.url;
                                newMetaData.videoembed = "https://www.screenscraper.fr/medias/" + theGame.systemeid + "/" + theGame.id + "/video.mp4";
                                break;
                            case "ss":
                                com.goodjaerb.scraperfx.settings.Image ssImage = new com.goodjaerb.scraperfx.settings.Image("screenshot", ScreenScraper2Source.SOURCE_NAME, media.url, false);
                                if(newMetaData.images == null) {
                                    newMetaData.images = new ArrayList<>();
                                }
                                if(newMetaData.getSelectedImageUrl("screenshot") == null) {
                                    ssImage.selected = true;
                                }
                                newMetaData.images.add(ssImage);
                                break;
                            case "box-2D":
                                switch(media.region) {
                                    case "us":
                                        com.goodjaerb.scraperfx.settings.Image usBoxImage = new com.goodjaerb.scraperfx.settings.Image("box-front", ScreenScraper2Source.SOURCE_NAME, media.url, false);
                                        if(newMetaData.images == null) {
                                            newMetaData.images = new ArrayList<>();
                                        }
                                        if(newMetaData.getSelectedImageUrl("box-front") == null) {
                                            usBoxImage.selected = true;
                                        }
                                        newMetaData.images.add(usBoxImage);
                                        break;
                                    case "wor":
                                        com.goodjaerb.scraperfx.settings.Image worBoxImage = new com.goodjaerb.scraperfx.settings.Image("box-front", ScreenScraper2Source.SOURCE_NAME, media.url, false);
                                        if(newMetaData.images == null) {
                                            newMetaData.images = new ArrayList<>();
                                        }
                                        if(newMetaData.getSelectedImageUrl("box-front") == null) {
                                            worBoxImage.selected = true;
                                        }
                                        newMetaData.images.add(worBoxImage);
                                        break;
                                    default:
                                }
                                break;
                            default:
                        }
                    }
                }
            }
        }
    }
    
    private class ScreenScraperResultHolder {
        private Optional<ScreenScraperGame> result;
        
        public void setResult(Optional<ScreenScraperGame> result) {
            this.result = result;
        }
        
        public Optional<ScreenScraperGame> get() {
            return result;
        }
    }
    
//    private class ForkJoinConsoleScanTask extends ConsoleScanTaskBase {
//
//        public ForkJoinConsoleScanTask(Path gamesPath, List<Game> games) {
//            super(gamesPath, games);
//        }
//        
//        @Override
//        protected void scan(List<ScanTaskOperation> ops) {
//            final Function<ScanTaskOperation, Game> matchFunction = this::match;
//    
//            final ForkJoinPool pool = new ForkJoinPool();
//            pool.execute(new ConsoleForkJoinMatcher(ops, matchFunction));
//        }
//    }
//    
//    private class ConsoleForkJoinMatcher extends RecursiveAction {
//        private final List<ScanTaskOperation> ops;
//        private final Function<ScanTaskOperation, Game> matchFunction;
//        
//        public ConsoleForkJoinMatcher(List<ScanTaskOperation> ops, Function<ScanTaskOperation, Game> matchFunction) {
//            this.ops = ops;
//            this.matchFunction = matchFunction;
//        }
//        
//        @Override
//        protected void compute() {
//            if(ops.size() <= 5) {
//                for(final ScanTaskOperation op : ops) {
//                    if(isCancelled()) {
//                        break;
//                    }
//                
//                    final Game localGame = matchFunction.apply(op);
//
////                    updateProgress(++completedCount, totalFiles);
//
//                    getSystemGameData().remove(localGame);
//                    getSystemGameData().add(localGame);
//
//                    Platform.runLater(() -> {
//                        final int index = observableGamesList.indexOf(localGame);
//                        if(index != -1) {
//                            observableGamesList.remove(index);
//                        }
//                        observableGamesList.add(localGame);
//                        gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(localGame));
//                    });
//                }
//            }
//            else {
//                new ConsoleForkJoinMatcher(ops.subList(0, ops.size() / 2), matchFunction).fork();
//                new ConsoleForkJoinMatcher(ops.subList(ops.size() / 2, ops.size()), matchFunction).fork();
//            }
//        }
//    }
    
    private class SequentialConsoleScanTask extends ConsoleScanTaskBase {
        
        public SequentialConsoleScanTask(Path gamesPath, List<Game> games) {
            super(gamesPath, games);
        }
        
        @Override
        public void scan(List<ScanTaskOperation> ops) {
            final int totalFiles = ops.size();
            int completedCount = 0;
            
            for(final ScanTaskOperation op : ops) {
                if(isCancelled()) {
                    break;
                }
                
                final Game localGame = match(op);

                updateProgress(++completedCount, totalFiles);

                getSystemGameData().remove(localGame);
                getSystemGameData().add(localGame);

                Platform.runLater(() -> {
                    final int index = observableGamesList.indexOf(localGame);
                    if(index != -1) {
                        observableGamesList.remove(index);
                    }
                    observableGamesList.add(localGame);
                    gamesListView.getSelectionModel().clearAndSelect(gamesListView.getItems().indexOf(localGame));
                });
            }
        }
    }
    
    private class SingleGameDownloadDialog extends Stage {
        private final ObservableList<String>    namesList = FXCollections.observableArrayList();
        private final FilteredList<String>      filteredNamesList = new FilteredList<>(namesList);
        private final TextField                 filterField = new TextField();;
        private final ListView<String>          selectGameList = new ListView<>(filteredNamesList);
        private final Button                    okButton = new Button("OK");
        private final Button                    cancelButton = new Button("Cancel");
        
        private final Timer     workingTimer = new Timer();
        private final TimerTask workingTask = new TimerTask() {
            private int n = 1;
            private String text;
            
            @Override
            public void run() {
                switch(n) {
                    case 1:
                        text = ".";
                        break;
                    case 2:
                        text = "..";
                        break;
                    case 3:
                        n = 1;
                        text = "...";
                        break;
                }
                n++;
                Platform.runLater(() -> {
                    okButton.setText("Downloading" + text);
                });
            }
        };
        
        private final String systemName;
        
        private boolean cancelled;
        
        public SingleGameDownloadDialog(String systemName, Window parentWindow) {
            super();
            this.systemName = systemName;
            
            filterField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredNamesList.setPredicate(name -> {
                    final String filterText = newValue.trim();
                    if(filterText.isEmpty()) {
                        return true;
                    }
                    return name.toLowerCase().contains(filterText.toLowerCase());
                });
            });

            filterField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if(e.getCode() == KeyCode.ESCAPE) {
                    if("".equals(filterField.getText())) {
                        cancelled = true;
                        SingleGameDownloadDialog.this.hide();
                    }
                    else {
                        filterField.setText("");
                    }
                }
            });

            selectGameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            selectGameList.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if(e.getCode() == KeyCode.ESCAPE) {
                    if("".equals(filterField.getText())) {
                        cancelled = true;
                        SingleGameDownloadDialog.this.hide();
                    }
                    else {
                        filterField.setText("");
                    }
                }
                else if(e.getCode() == KeyCode.BACK_SPACE) {
                    final String currentText = filterField.getText();
                    filterField.setText(currentText.substring(0, currentText.length() - 1));
                }
            });

            selectGameList.addEventHandler(KeyEvent.KEY_TYPED, e -> filterField.appendText(e.getCharacter()));
        
            okButton.setDisable(true);
            okButton.setOnAction(e -> onOkButton());
            okButton.setPrefWidth(175);
            
            cancelButton.setOnAction((ActionEvent e) -> {
                cancelled = true;
                hide();
            });
            
            setOnShown(e -> onShown());
            setOnHidden(e -> workingTimer.cancel());
            
            final HBox box = new HBox();
            box.setSpacing(7.);
            box.setPadding(STANDARD_INSETS);
            box.getChildren().addAll(okButton, cancelButton);
            
            final VBox vbox = new VBox();
            vbox.setSpacing(7.);
            vbox.setPadding(STANDARD_INSETS);
            vbox.getChildren().add(new Label("Select game:"));
            vbox.getChildren().add(filterField);
            vbox.getChildren().add(selectGameList);
            vbox.getChildren().add(box);
            
            final Scene scene = new Scene(vbox);

            setTitle("Select Game");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(parentWindow);
            setScene(scene);
        }
        
        public boolean wasCancelled() {
            return cancelled;
        }
        
        private void onOkButton() {
            okButton.setDisable(true);
            cancelButton.setDisable(true);
            
            workingTimer.schedule(workingTask, 0, 250);
            
            new Thread(() -> {
                try {
                    currentGame.matchedName = selectGameList.getSelectionModel().getSelectedItem();
                    final MetaData newMetaData = DataSourceFactory.get(SourceAgent.THEGAMESDB).getMetaData(systemName, currentGame);
                    
                    if(newMetaData != null) {
                        if(currentGame.metadata != null && currentGame.metadata.favorite) {
                            newMetaData.favorite = true;
                        }
                        
                        getExtraMetaData(newMetaData, getCurrentSettings().scrapeAs, currentGame);

                        currentGame.updateMetaData(newMetaData);
                        currentGame.strength = Game.MatchStrength.LOCKED;
                    }
                }
                catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "An error occured while accessing TheGamesDB.net database.", ButtonType.OK).showAndWait();
                    });
                }
                finally {
                    Platform.runLater(() -> {
                        gamesListView.refresh();
                        hide();
                    });
                }
            }).start();
        }
        
        private void onShown() {
            new Thread(() -> {
                try {
                    final List<String> gameList = DataSourceFactory.get(SourceAgent.THEGAMESDB).getSystemGameNames(systemName);
                    Collections.sort(gameList);
                    
                    Platform.runLater(() -> {
                        namesList.clear();
                        namesList.addAll(gameList);
                        okButton.setDisable(false);
                    });
                }
                catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ScraperFX.class.getName()).log(Level.SEVERE, null, ex);
                    
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "An error occured while accessing TheGamesDB.net database.", ButtonType.OK).showAndWait());
                }
            }).start();
        }
    }
    
    private class ScanProgressDialog extends Stage {
        
        private final QueuedMessageBox  messageArea = new QueuedMessageBox();
        private final ProgressBar       progressBar = new ProgressBar();
        private final Button            cancelButton = new Button("Cancel");
        
        public ScanProgressDialog(Path gamesPath, List<Game> selectedGames, Window parentWindow) {
            super();
            
            cancelButton.setOnAction(e -> {
                if(scanTask.isRunning()) {
                    scanTask.cancel();
                    cancelButton.setText("Close");
                }
                else {
                    hide();
                }
            });
            
            setOnShown(e -> {
                scanTask.setStatusUpdater(message -> messageArea.queueMessage(message));
                scanTask.setOnSucceeded(ev -> {
                    messageArea.queueMessage("Scan complete!");
                    cancelButton.setText("Close");
                });

                scanTask.setOnCancelled(ev -> {
                    messageArea.queueMessage("Scan cancelled!");
                    cancelButton.setText("Close");
                });

                progressBar.progressProperty().bind(scanTask.progressProperty());

                messageArea.start();
                final Thread t = new Thread(scanTask);
                t.setDaemon(true);
                t.start();
            });
            
            setOnHidden(e -> {
                messageArea.stop();
                scanTask.cancel();
            });
            
            final FlowPane p = new FlowPane(7., 7., progressBar, cancelButton);
            
            final VBox box = new VBox();
            box.setSpacing(7.);
            box.setPadding(STANDARD_INSETS);
            box.getChildren().addAll(messageArea, p);
            
            final Scene scene = new Scene(box);
            
            setTitle("Scanning in Progress");
            setResizable(false);
            initModality(Modality.WINDOW_MODAL);
            initOwner(parentWindow);
            setScene(scene);
        }
    }
    
    private class GameListCell extends ListCell<Game> {
        
        @Override
        protected void updateItem(Game item, boolean empty) {
            super.updateItem(item, empty);
            
            setText(item == null ? "" : item.toString());
            if(item == null) {
                setStyle(null);
            }
            else {
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
        
        private static void setup(String title, String[] fileExts) {
            FILE_CHOOSER.setTitle(title);
            FILE_CHOOSER.setInitialDirectory(new File(System.getProperty("user.home")));
            FILE_CHOOSER.getExtensionFilters().clear();

            if(fileExts != null && fileExts.length % 2 == 0) {
                for(int i = 0; i < fileExts.length; i += 2) {
                    FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExts[i], fileExts[i + 1]));
                }
            }
        }
        
//        public static List<File> openFiles(String title, Window parentWindow, String... fileExts) {
//            setup(title, fileExts);
//            return FILE_CHOOSER.showOpenMultipleDialog(parentWindow);
//        }

        public static File getFile(DialogType type, String title, Window parentWindow, String... fileExts) {
            setup(title, fileExts);
            
            File f = null;
            switch(type) {
                case SAVE:
                    f = FILE_CHOOSER.showSaveDialog(parentWindow);
                    break;
                case OPEN:
                    f = FILE_CHOOSER.showOpenDialog(parentWindow);
                    break;
            }
            return f;
        }

        public static File getDir(String title, Window parentWindow) {
            DIR_CHOOSER.setTitle(title);
            DIR_CHOOSER.setInitialDirectory(new File(System.getProperty("user.home")));

            final File f = DIR_CHOOSER.showDialog(parentWindow);
            return f;
        }
    }
}
