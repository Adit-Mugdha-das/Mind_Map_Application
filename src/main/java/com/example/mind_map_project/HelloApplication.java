package com.example.mind_map_project;


import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;

import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.mind_map_project.DatabaseUtils.getConnection;
import static javafx.application.Application.launch;

public class HelloApplication extends Application {
    private Shape firstSelectedNode = null;
    private Shape currentSelectedNode = null;
    private Arrow selectedLine = null;
    private Text currentSelectedText = null;
    private final List<Arrow> connections = new ArrayList<>();
    private final Map<Arrow, Color> lineColors = new HashMap<>();
    private double initialX, initialY;
    private Slider sizeSlider;
    private ComboBox<String> shapeSelector;
    private ComboBox<String> lineTypeSelector;
    private ComboBox<String> lineStyleSelector;
    private ComboBox<String> fontSelector;
    private Slider fontSizeSlider;
    private Text firstSelectedText = null;
    private final List<Shape> textConnections = new ArrayList<>();
    private Line selectedTextLine = null;
    private Path freehandPath; // For the freehand line
    private boolean isDrawing = false; // Flag to indicate drawing mode
    private double startX, startY; // Starting point for the freehand line
    private Path selectedFreehandPath = null; // To store the currently selected path





    private static final Color SELECTED_COLOR = Color.GOLD;
    private static final double SELECTED_STROKE_WIDTH = 4.0;
    private static final double DEFAULT_NODE_STROKE_WIDTH = 2.0;


    // Define valid credentials
    private final String VALID_USERNAME = "admin";
    private final String VALID_PASSWORD = "password";

    @Override
    public void start(Stage primaryStage) {
        // Show login page initially
        showLoginPage(primaryStage);
    }

    private Map<String, String> users = new HashMap<>(); // Store users' data


    private void showLoginPage(Stage primaryStage) {
        // Create a GridPane for the login form
        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setVgap(15);
        loginForm.setHgap(10);
        loginForm.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 20px; -fx-border-color: #D3D3D3; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Create a VBox to center the form in the scene
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: linear-gradient(to bottom, #a1c4fd, #c2e9fb); -fx-padding: 30px;");

        // Add title text
        Label titleLabel = new Label("Mind Mapping Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        titleLabel.setAlignment(Pos.CENTER);

        // Add subtitle or instructions below the title
        Label subtitleLabel = new Label("Please enter your credentials below:");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #555555;");
        subtitleLabel.setAlignment(Pos.CENTER);

        // Create username and password fields with labels
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // Add components to the login form
        loginForm.add(usernameLabel, 0, 0);
        loginForm.add(usernameField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);

        // Create a login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #005BB5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;"));

        // Add action for the login button
        // Add action for the login button
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Authenticate user with the database
            if (DatabaseUtils.authenticateUser(username, password)) {
                showDashboardPage(primaryStage, username); // Navigate to the dashboard
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText("Invalid Credentials");
                alert.setContentText("Please enter a valid username and password.");
                alert.showAndWait();
            }
        });

        // Create a Forgot Password button
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078D7; -fx-font-size: 14px; -fx-underline: true;");
        forgotPasswordButton.setOnAction(event -> showForgotPasswordDialog());
        loginForm.add(forgotPasswordButton, 1, 4); // Add it below the login and signup buttons


        // Create a Sign-Up button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #0078D7; -fx-text-fill: #0078D7; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle("-fx-background-color: #E0F2FF; -fx-border-color: #0078D7; -fx-text-fill: #0078D7; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px; -fx-border-radius: 5px;"));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #0078D7; -fx-text-fill: #0078D7; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px; -fx-border-radius: 5px;"));

        signUpButton.setOnAction(event -> showSignUpPage(primaryStage)); // Navigate to sign-up page

        // Add the login and sign-up buttons to the form
        loginForm.add(loginButton, 1, 2);
        loginForm.add(signUpButton, 1, 3);

        // Add a footer text
        Label footerLabel = new Label("© 2024 Mind Mapping App");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        footerLabel.setAlignment(Pos.CENTER);

        // Wrap everything inside the container
        container.getChildren().addAll(titleLabel, subtitleLabel, loginForm, footerLabel);

        // Set up the scene and show it
        Scene loginScene = new Scene(container, 450, 450);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }


    private void showForgotPasswordDialog() {
        // Create a dialog for Forgot Password
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Retrieve Your Password");

        // Set the button types
        ButtonType retrieveButtonType = new ButtonType("Retrieve", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(retrieveButtonType, ButtonType.CANCEL);

        // Create fields for username and email
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == retrieveButtonType) {
                return retrievePassword(usernameField.getText(), emailField.getText());
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(password -> {
            if (password != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Retrieved");
                alert.setHeaderText("Your Password");
                alert.setContentText("Password: " + password);
                alert.showAndWait();
            }
        });
    }

    private String retrievePassword(String username, String email) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT password FROM users WHERE username = ? AND email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password"); // Return the password if found
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Details");
                alert.setContentText("Username or email is incorrect. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("An error occurred while retrieving the password.");
            alert.showAndWait();
        }
        return null; // Return null if details are invalid
    }


    private void showDashboardPage(Stage primaryStage, String username) {
        // Root container for the dashboard
        BorderPane root = new BorderPane();

        // Set the background image
        String imageUrl = getClass().getResource("/path_to_image/22920.jpg").toExternalForm(); // Replace 'path_to_image' with the actual path
        root.setStyle(String.format(
                "-fx-background-image: url('%s');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;",
                imageUrl
        ));

        // Header with user info
        HBox header = new HBox();
        header.setStyle("-fx-background-color: rgba(51, 78, 104, 0.8); -fx-padding: 10px;"); // Semi-transparent header
        Label appTitle = new Label("Mind Mapping Dashboard");
        appTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label userLabel = new Label("Logged in as: " + username);
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        header.getChildren().addAll(appTitle, spacer, userLabel);
        root.setTop(header);


        // Buttons with clean design
        VBox buttonContainer = new VBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(40));

        Button adminPanelButton = new Button("Admin Panel");
        adminPanelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4A90E2; -fx-text-fill: white;");

// Set the action to show the password dialog
        adminPanelButton.setOnAction(event -> {
            // Create a password dialog
            Dialog<String> passwordDialog = new Dialog<>();
            passwordDialog.setTitle("Admin Login");
            passwordDialog.setHeaderText("Enter Admin Password");

            // Create a PasswordField for the dialog
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            // Add the PasswordField to the dialog
            passwordDialog.getDialogPane().setContent(passwordField);

            // Add OK and Cancel buttons
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            passwordDialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

            // Handle the result of the dialog
            passwordDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return passwordField.getText();
                }
                return null;
            });

            // Show the dialog and get the result
            Optional<String> result = passwordDialog.showAndWait();
            result.ifPresent(password -> {
                // Replace "admin123" with your desired admin password
                if ("admin123".equals(password)) {
                    showAdminPage(primaryStage); // Navigate to the admin panel
                } else {
                    // Show an error message for incorrect password
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Access Denied");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect password. Please try again.");
                    alert.showAndWait();
                }
            });
        });

        buttonContainer.getChildren().add(adminPanelButton); // Add it to your VBox or button container



        Button createNewButton = new Button("Create New Mindmap");
        styleProfessionalButton(createNewButton, "🔧");
        createNewButton.setOnAction(event -> startMainApplication(primaryStage, username)); // Navigate to the mind map canvas

        Button loadPreviousButton = new Button("Load Previous Mindmap");
        styleProfessionalButton(loadPreviousButton, "📂");
        loadPreviousButton.setOnAction(event -> {
            List<String[]> mindMaps = DatabaseUtils.getMindMaps(username);
            if (mindMaps.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Mind Maps Found");
                alert.setHeaderText(null);
                alert.setContentText("No mind maps are associated with your account.");
                alert.showAndWait();
            } else {
                showMindMapList(primaryStage, username, mindMaps); // Logic to display the list of mind maps
            }
        });

        Button tutorialButton = new Button("Tutorial");
        styleProfessionalButton(tutorialButton, "📘");
        tutorialButton.setOnAction(event -> showTutorialPage(primaryStage)); // Navigate to tutorial page

        Button viewSamplesButton = new Button("View Sample Mindmaps");
        styleProfessionalButton(viewSamplesButton, "🖼");
        viewSamplesButton.setOnAction(event -> showSampleMindMaps(primaryStage)); // Open sample mindmaps page

        Button themeToggleButton = new Button("Switch to Dark Theme");
        themeToggleButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ffffff; -fx-text-fill: #333333; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        themeToggleButton.setOnAction(event -> {
            if (themeToggleButton.getText().equals("Switch to Dark Theme")) {
                applyDarkTheme(root, header, themeToggleButton);
                themeToggleButton.setText("Switch to Light Theme");
            } else {
                applyLightTheme(root, header, themeToggleButton);
                themeToggleButton.setText("Switch to Dark Theme");
            }
        });

        Button exitButton = new Button("⏪ Exit");
        styleProfessionalButton(exitButton, "");
        exitButton.setOnAction(event -> showLoginPage(primaryStage)); // Navigate back to login page

        // Add buttons in the desired order
        buttonContainer.getChildren().addAll(
                createNewButton,
                loadPreviousButton,
                tutorialButton,
                viewSamplesButton,  // Added the new button here
                exitButton,         // Exit button moved up
                themeToggleButton   // Theme button moved down
        );
        root.setCenter(buttonContainer);

        // Footer
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: rgba(51, 78, 104, 0.8); -fx-padding: 10px;"); // Semi-transparent footer
        Label footerText = new Label("© 2024 Mind Mapping App. All rights reserved.");
        footerText.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        footer.setAlignment(Pos.CENTER);
        footer.getChildren().add(footerText);
        root.setBottom(footer);

        // Set up the scene
        Scene dashboardScene = new Scene(root, 800, 600);
        primaryStage.setScene(dashboardScene);
        primaryStage.setTitle("Mind Mapping Dashboard");
        primaryStage.show();
    }

    // Method to show the sample mindmaps
    private void showSampleMindMaps(Stage primaryStage) {
        // Create a VBox to hold the sample mind maps
        VBox sampleContainer = new VBox(20);
        sampleContainer.setPadding(new Insets(20));
        sampleContainer.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Sample Mindmaps");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Paths to the sample mind map images
        String[] imagePaths = {
                "/path_to_image/157816-OUWA20-808.jpg",
                "/path_to_image/159969-OUUK78-718.jpg",
                "/path_to_image/PIC4.jpg",
                "/path_to_image/img_2.png",
                "/path_to_image/img_1.png",
                "/path_to_image/img_6.png",
                "/path_to_image/img.png",
                "/path_to_image/img_3.png",
                "/path_to_image/img_4.png",
                "/path_to_image/img_5.png",
                "/path_to_image/155156-OVEGNI-685.jpg"// Ensure all paths are correct
        };

        for (String imagePath : imagePaths) {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(600); // Adjust width as needed
            imageView.setPreserveRatio(true); // Maintain aspect ratio
            sampleContainer.getChildren().add(imageView);
        }

        // Add a back button to return to the dashboard
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> showDashboardPage(primaryStage, "admin")); // Pass the username back if needed

        sampleContainer.getChildren().add(backButton);

        // Create a ScrollPane to allow scrolling through the images
        ScrollPane scrollPane = new ScrollPane(sampleContainer);
        scrollPane.setFitToWidth(true);

        // Set up the scene for the sample mindmaps
        Scene sampleScene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(sampleScene);
        primaryStage.setTitle("Sample Mindmaps");
    }


    // Method to display the list of mind maps
    private void showMindMapList(Stage primaryStage, String username, List<String[]> mindMaps) {
        // Logic to create a new scene or popup to display available mind maps
        Stage listStage = new Stage();
        VBox listContainer = new VBox(10);
        listContainer.setPadding(new Insets(20));
        listContainer.setAlignment(Pos.CENTER);

        Label title = new Label("Your Mind Maps");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        for (String[] mindMap : mindMaps) {
            Button mindMapButton = new Button(mindMap[1]); // Use the name of the mind map
            mindMapButton.setOnAction(event -> {
                String jsonData = DatabaseUtils.loadMindMap(username, mindMap[1]);
                if (jsonData != null) {
                    startMainApplicationWithData(primaryStage, jsonData); // Load the selected mind map
                    listStage.close();
                }
            });
            listContainer.getChildren().add(mindMapButton);
        }

        Scene listScene = new Scene(listContainer, 400, 300);
        listStage.setScene(listScene);
        listStage.setTitle("Select a Mind Map");
        listStage.show();
    }



    private void startMainApplicationWithData(Stage primaryStage, String jsonData) {
        Pane canvas = new Pane(); // Create a new Pane for the mind map canvas
        deserializeCanvasFromJson(canvas, jsonData); // Use your existing deserialization method to load the canvas

        BorderPane root = new BorderPane();
        root.setCenter(canvas); // Set the canvas to the center of the BorderPane

        // Add any additional UI components (e.g., toolbar, menu) if needed
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(event -> showDashboardPage(primaryStage, "username")); // Replace "username" with the actual username
        root.setTop(backButton);

        // Set up the scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mind Map Editor");
        primaryStage.show();
    }


    private void showTutorialPage(Stage primaryStage) {
        // Create a VBox for holding the tutorial content
        VBox tutorialContainer = new VBox(20);
        tutorialContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #a1c4fd, #c2e9fb); -fx-padding: 20px;");
        tutorialContainer.setAlignment(Pos.TOP_CENTER);

        // Add a title for the tutorial page
        Label titleLabel = new Label("Tutorial - Understanding the Controls");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Add instructions
        Label instructionLabel = new Label("Learn how to use each button and feature in the canvas interface.");
        instructionLabel.setFont(Font.font("Arial", 14));
        instructionLabel.setTextFill(Color.DARKGRAY);

        // Create the content area with detailed explanations
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 5px; -fx-padding: 10px;");

        // Tutorial items
        String[][] tutorialItems = {
                {"Add Node", "Adds a new graphical node (circle, rectangle, etc.) to the canvas at a random position. These nodes can represent ideas or entities in your mind map."},
                {"Delete Node", "Removes the currently selected node along with all its connections."},
                {"Add Custom Text", "Adds standalone text anywhere on the canvas for labeling or annotations."},
                {"Add Text to Node", "Associates a text label to the currently selected node, displayed inside the node."},
                {"Delete Line", "Deletes the currently selected connection line between two nodes."},
                {"Delete Text Line", "Deletes freehand-drawn paths or connection lines created between texts."},
                {"Rotate Left", "Rotates the currently selected text counterclockwise by 10 degrees for better alignment."},
                {"Rotate Right", "Rotates the currently selected text clockwise by 10 degrees for better alignment."},
                {"Background Color Picker", "Allows you to change the background color of the canvas."},
                {"Select Background Image", "Enables you to set a custom image as the background of the canvas."},
                {"Shape Selector", "Selects the shape for new nodes, such as Circle, Rectangle, Hexagon, etc."},
                {"Line Type Selector", "Defines the style of connections, such as Straight Line, Arrow Line, or Bezier Curve."},
                {"Line Style Selector", "Customizes the line appearance (Solid, Dashed, Dotted, or Bold)."},
                {"Font Selector", "Sets the font type for text items."},
                {"Font Size Slider", "Adjusts the font size of the currently selected text."},
                {"Node Size Slider", "Changes the size of the currently selected node dynamically."},
                {"Text Line Color Picker", "Allows you to change the color of selected text lines or freehand paths."},
                {"Toggle Drawing Mode", "Enables freehand drawing mode for creating custom annotations or paths."},
                {"Drawing Color Picker", "Changes the stroke color used for freehand drawing."}
        };

        for (String[] item : tutorialItems) {
            Label header = new Label(item[0]);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            header.setTextFill(Color.DARKBLUE);

            Label description = new Label(item[1]);
            description.setFont(Font.font("Arial", 14));
            description.setTextFill(Color.DARKGRAY);
            description.setWrapText(true);

            VBox itemBox = new VBox(5, header, description);
            itemBox.setStyle("-fx-padding: 10px; -fx-border-color: #dddddd; -fx-border-radius: 5px; -fx-background-color: #ffffff; -fx-background-insets: 5;");
            contentBox.getChildren().add(itemBox);
        }

        // Add the content to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; -fx-padding: 10px;");

        // Back button to return to the dashboard
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #357ABD; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        backButton.setOnAction(event -> showDashboardPage(primaryStage, "admin"));

        // Arrange all components in a VBox
        tutorialContainer.getChildren().addAll(titleLabel, instructionLabel, scrollPane, backButton);

        // Create a scene and set it on the primary stage
        Scene tutorialScene = new Scene(tutorialContainer, 800, 600);
        primaryStage.setScene(tutorialScene);
        primaryStage.setTitle("Tutorial - Mind Mapping Application");
        primaryStage.show();
    }


    private void setBackgroundImage(BorderPane root, String imagePath) {
        root.setStyle(
                "-fx-background-image: url('" + imagePath + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }



    // Methods to apply dark and light themes
    private void applyDarkTheme(BorderPane root, HBox header, Button themeToggle) {
        root.setStyle("-fx-background-color: #2B2B2B;"); // Remove any background image
        header.setStyle("-fx-background-color: #1E1E1E;");
        themeToggle.setStyle(
                "-fx-font-size: 14px; -fx-background-color: #444444; -fx-text-fill: white; " +
                        "-fx-padding: 5px 10px; -fx-background-radius: 5px;"
        );
    }



    private void applyLightTheme(BorderPane root, HBox header, Button themeToggle) {
        // Set the background image
        setBackgroundImage(root, getClass().getResource("/path_to_image/22920.jpg").toExternalForm());

        header.setStyle("-fx-background-color: #334E68;");
        themeToggle.setStyle(
                "-fx-font-size: 14px; -fx-background-color: #ffffff; -fx-text-fill: #333333; " +
                        "-fx-padding: 5px 10px; -fx-background-radius: 5px;"
        );
    }






    // Utility to style buttons with a professional look
    private void styleProfessionalButton(Button button, String icon) {
        button.setText(icon + " " + button.getText());
        button.setStyle("-fx-font-size: 16px; " +
                "-fx-background-color: #4A90E2; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0.1, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 16px; " +
                "-fx-background-color: #357ABD; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0.1, 0, 3);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 16px; " +
                "-fx-background-color: #4A90E2; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0.1, 0, 2);"));
    }

    // Utility to show informational alerts
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showSignUpPage(Stage primaryStage) {
        // Create a VBox for the sign-up form
        VBox signUpContainer = new VBox(20);
        signUpContainer.setAlignment(Pos.CENTER);
        signUpContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #c2e9fb, #a1c4fd); -fx-padding: 30px;");

        // Title for Sign-Up Page
        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setStyle("-fx-padding: 8px; -fx-border-color: #D3D3D3; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 8px; -fx-border-color: #D3D3D3; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        PasswordField signUpPasswordField = new PasswordField();
        signUpPasswordField.setPromptText("Enter your password");
        signUpPasswordField.setStyle("-fx-padding: 8px; -fx-border-color: #D3D3D3; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;");
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle("-fx-background-color: #005BB5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;"));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px;"));

        signUpButton.setOnAction(event -> {
            String username = nameField.getText();
            String email = emailField.getText();
            String password = signUpPasswordField.getText();

            // Validate email
            if (!email.endsWith("@das.com")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Email");
                alert.setHeaderText("Email Restriction");
                alert.setContentText("Email must end with '@das.com'. Please enter a valid email address.");
                alert.showAndWait();
                return; // Stop execution if email is invalid
            }

            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (DatabaseUtils.registerUser(username, email, password)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sign-Up Successful");
                    alert.setHeaderText("Registration Complete");
                    alert.setContentText("You can now log in with your credentials.");
                    alert.showAndWait();
                    showLoginPage(primaryStage); // Navigate back to login
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sign-Up Failed");
                    alert.setHeaderText("Registration Error");
                    alert.setContentText("An error occurred during registration. Please try again.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign-Up Failed");
                alert.setHeaderText("Missing Information");
                alert.setContentText("Please fill out all fields to continue.");
                alert.showAndWait();
            }
        });



        // Back to Login button
        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #0078D7; -fx-text-fill: #0078D7; -fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        backToLoginButton.setOnAction(event -> showLoginPage(primaryStage));

        // Add everything to the container
        signUpContainer.getChildren().addAll(signUpLabel, nameField, emailField, signUpPasswordField, signUpButton, backToLoginButton);

        // Set up the scene and show it
        Scene signUpScene = new Scene(signUpContainer, 450, 450);
        primaryStage.setScene(signUpScene);
        primaryStage.setTitle("Sign Up");
        primaryStage.show();
    }

    private void enableEraseMode(Pane canvas) {
        // Add click event listeners to all freehand paths on the canvas
        for (var node : canvas.getChildren()) {
            if (node instanceof Path) {
                Path path = (Path) node;

                // Highlight the selected path
                path.setOnMouseClicked(event -> {
                    event.consume(); // Prevent other handlers from being triggered
                    deselectAll(); // Deselect other elements

                    // Highlight the selected path
                    path.setStroke(Color.RED); // Change color to indicate selection

                    // Confirm deletion
                    Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDelete.setTitle("Delete Drawing");
                    confirmDelete.setHeaderText("Are you sure you want to delete this drawing?");
                    confirmDelete.setContentText("This action cannot be undone.");

                    Optional<ButtonType> result = confirmDelete.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        canvas.getChildren().remove(path); // Remove the path from the canvas
                    } else {
                        // Revert highlight if not deleted
                        path.setStroke(Color.BLACK); // Reset to original color
                    }
                });
            }
        }
    }







    private void startMainApplication(Stage primaryStage,String username) {
        Pane canvas = new Pane();
        canvas.setStyle("-fx-background-color: #f0f0f0;");
        canvas.setPrefSize(1200, 800); // Increased size to demonstrate scrolling

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setFitToWidth(true); // Allow horizontal scrolling
        scrollPane.setFitToHeight(true); // Allow vertical scrolling
        scrollPane.setPrefSize(800, 500); // Set the visible viewport size

        Button addButton = new Button("Add Node");
        Button deleteNodeButton = new Button("Delete Node");
        Button deleteLineButton = new Button("Delete Line");
        Button addTextButton = new Button("Add Custom Text");
        Button addNodeTextButton = new Button("Add Text to Node");
        Button rotateLeftButton = new Button("Rotate Left");
        Button rotateRightButton = new Button("Rotate Right");

        Button saveButton = new Button("Save Mind Map");
        saveButton.setOnAction(event -> {
            try {
                // Define username and mind map name
                //String username = "admin"; // Replace with the actual username
                String mindMapName = "My Mind Map"; // Replace with the desired mind map name

                // Serialize the canvas to JSON
                String data = serializeCanvasToJson(canvas, username, mindMapName);

                // Print the serialized JSON to the console for debugging
                System.out.println("Serialized JSON Data: " + data);

                // Save the serialized JSON to the database
                boolean success = DatabaseUtils.saveMindMap(username, mindMapName, data);

                // Display success or failure message
                if (success) {
                    showInfoAlert("Success", "Mind Map saved successfully!");
                } else {
                    showInfoAlert("Error", "Failed to save the Mind Map.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showInfoAlert("Error", "An unexpected error occurred.");
            }
        });

        Button eraseDrawingButton = new Button("Erase Drawing");
        eraseDrawingButton.setOnAction(event -> enableEraseMode(canvas));
        // Adjust the grid position as needed


        Button deleteTextButton = new Button("Delete Text");
        deleteTextButton.setOnAction(event -> {
            if (currentSelectedText != null) {
                Pane parentCanvas = (Pane) currentSelectedText.getParent();
                parentCanvas.getChildren().remove(currentSelectedText); // Remove text from canvas
                currentSelectedText = null; // Clear the selected text
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Text Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a text to delete.");
                alert.showAndWait();
            }
        });





        Button downloadAsImageButton = new Button("Download as Image");
        downloadAsImageButton.setOnAction(event -> {
            try {
                // Define the default location and file name
                File defaultFile = new File(System.getProperty("user.home") + "/Downloads/mindmap.png");

                // Take a snapshot of the canvas
                WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, image);

                // Save the image to the default location
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", defaultFile);
                    showInfoAlert("Download Successful", "Mind Map image saved to: " + defaultFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    showInfoAlert("Error", "An error occurred while saving the image.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showInfoAlert("Error", "Failed to export the mind map as an image.");
            }
        });





        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(event -> {
            Color selectedColor = colorPicker.getValue();
            if (currentSelectedText != null) {
                currentSelectedText.setFill(selectedColor);
            } else if (currentSelectedNode != null) {
                RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                        new Stop(0, selectedColor), new Stop(1, selectedColor.darker()));
                currentSelectedNode.setFill(gradient);
            } else if (selectedLine != null) {
                if (selectedLine.line != null) {
                    selectedLine.line.setStroke(selectedColor);
                } else if (selectedLine.cubicCurve != null) {
                    selectedLine.cubicCurve.setStroke(selectedColor);
                }
                lineColors.put(selectedLine, selectedColor);
                selectedLine.updateArrowHead();
            }
        });

        ColorPicker textLineColorPicker = new ColorPicker(Color.BLACK);
        textLineColorPicker.setOnAction(event -> {
            if (selectedTextLine != null) {
                selectedTextLine.setStroke(textLineColorPicker.getValue());
            }
            if (selectedFreehandPath != null) {
                selectedFreehandPath.setStroke(textLineColorPicker.getValue());
            }
        });

        Button deleteTextLineButton = new Button("Delete Text Line");
        deleteTextLineButton.setOnAction(event -> {
            if (selectedTextLine != null) {
                Pane parentCanvas = (Pane) selectedTextLine.getParent();
                parentCanvas.getChildren().remove(selectedTextLine);
                textConnections.remove(selectedTextLine);
                selectedTextLine = null;
            }
        });

        Button backgroundImagePickerButton = new Button("Select Background Image");
        backgroundImagePickerButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                String imagePath = selectedFile.toURI().toString();
                canvas.setStyle(String.format("-fx-background-image: url('%s'); -fx-background-position: center center; -fx-background-repeat: no-repeat; -fx-background-size: contain;", imagePath));
            }
        });

        ColorPicker backgroundColorPicker = new ColorPicker(Color.web("#f0f0f0"));
        backgroundColorPicker.setOnAction(event -> {
            Color selectedBackgroundColor = backgroundColorPicker.getValue();
            canvas.setStyle(String.format("-fx-background-color: #%02x%02x%02x;",
                    (int) (selectedBackgroundColor.getRed() * 255),
                    (int) (selectedBackgroundColor.getGreen() * 255),
                    (int) (selectedBackgroundColor.getBlue() * 255)));
        });

        sizeSlider = new Slider(20, 100, 30);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setBlockIncrement(10);
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentSelectedNode != null) {
                resizeNode(currentSelectedNode, newValue.doubleValue());
                updateConnections();
            }
        });

        shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll("Circle", "Square Rectangle", "Wide Rectangle", "Hexagon", "Ellipse", "Triangle", "Animated Node");
        shapeSelector.setValue("Circle");

        lineTypeSelector = new ComboBox<>();
        lineTypeSelector.getItems().addAll("Straight Line", "Arrow Line", "Bezier Curve");
        lineTypeSelector.setValue("Straight Line");

        lineStyleSelector = new ComboBox<>();
        lineStyleSelector.getItems().addAll("Solid", "Dashed", "Dotted", "Bold");
        lineStyleSelector.setValue("Solid");

        fontSelector = new ComboBox<>();
        fontSelector.getItems().addAll("Arial", "Verdana", "Times New Roman", "Courier New", "Tahoma", "Italic", "Bold");
        fontSelector.setValue("Arial");

        fontSizeSlider = new Slider(10, 40, 14);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setBlockIncrement(2);
        fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentSelectedText != null) {
                String selectedFont = fontSelector.getValue();
                if ("Italic".equals(selectedFont)) {
                    currentSelectedText.setFont(Font.font("System", FontPosture.ITALIC, newValue.doubleValue()));
                } else if ("Bold".equals(selectedFont)) {
                    currentSelectedText.setFont(Font.font("System", FontWeight.BOLD, newValue.doubleValue()));
                } else {
                    currentSelectedText.setFont(new Font(selectedFont, newValue.doubleValue()));
                }
            }
        });

        fontSelector.setOnAction(event -> {
            if (currentSelectedText != null) {
                String selectedFont = fontSelector.getValue();
                if ("Italic".equals(selectedFont)) {
                    currentSelectedText.setFont(Font.font("System", FontPosture.ITALIC, fontSizeSlider.getValue()));
                } else if ("Bold".equals(selectedFont)) {
                    currentSelectedText.setFont(Font.font("System", FontWeight.BOLD, fontSizeSlider.getValue()));
                } else {
                    currentSelectedText.setFont(new Font(selectedFont, fontSizeSlider.getValue()));
                }
            }
        });

        addButton.setOnAction(event -> createNode(canvas, Math.random() * 600 + 100, Math.random() * 300 + 100));
        deleteNodeButton.setOnAction(event -> deleteNode(canvas));
        deleteLineButton.setOnAction(event -> deleteLine(canvas));
        addTextButton.setOnAction(event -> addCustomText(canvas));
        addNodeTextButton.setOnAction(event -> addTextToNode());
        addTextButton.setOnAction(event -> addCustomText(canvas));

        rotateLeftButton.setOnAction(event -> {
            if (currentSelectedText != null) {
                currentSelectedText.setRotate(currentSelectedText.getRotate() - 10);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Text Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a text to rotate.");
                alert.showAndWait();
            }
        });

        rotateRightButton.setOnAction(event -> {
            if (currentSelectedText != null) {
                currentSelectedText.setRotate(currentSelectedText.getRotate() + 10);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Text Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a text to rotate.");
                alert.showAndWait();
            }
        });
        Button loadButton = new Button("Load Mind Map");
        GridPane buttonGrid = new GridPane();
        buttonGrid.setVgap(15); // Increased vertical spacing
        buttonGrid.setHgap(25); // Increased horizontal spacing
        buttonGrid.setAlignment(Pos.TOP_LEFT);
        buttonGrid.add(addButton, 0, 0);
        buttonGrid.add(eraseDrawingButton, 5, 4);
        buttonGrid.add(deleteTextButton, 4, 4);
        buttonGrid.add(deleteNodeButton, 1, 0);
        buttonGrid.add(colorPicker, 2, 0);
        buttonGrid.add(addNodeTextButton, 3, 0);
        buttonGrid.add(new Label("Background Color:"), 4, 0);
        buttonGrid.add(backgroundColorPicker, 5, 0);
        buttonGrid.add(backgroundImagePickerButton, 6, 0);
        buttonGrid.add(deleteLineButton, 0, 1);
        buttonGrid.add(addTextButton, 1, 1);
        buttonGrid.add(rotateLeftButton, 2, 1);
        buttonGrid.add(rotateRightButton, 3, 1);
        buttonGrid.add(saveButton, 4, 1);
        buttonGrid.add(loadButton, 5, 1);
        buttonGrid.add(new Label("Shape:"), 0, 2);
        buttonGrid.add(shapeSelector, 1, 2);
        buttonGrid.add(new Label("Line Type:"), 2, 2);
        buttonGrid.add(lineTypeSelector, 3, 2);
        buttonGrid.add(new Label("Line Style:"), 0, 3);
        buttonGrid.add(lineStyleSelector, 1, 3);
        buttonGrid.add(downloadAsImageButton, 6, 1);
        buttonGrid.add(new Label("Node Size:"), 2, 3);
        buttonGrid.add(sizeSlider, 3, 3);
        buttonGrid.add(new Label("Font:"), 0, 4);
        buttonGrid.add(fontSelector, 1, 4);
        buttonGrid.add(new Label("Font Size:"), 2, 4);
        buttonGrid.add(fontSizeSlider, 3, 4);
        buttonGrid.add(deleteTextLineButton, 4, 2);
        buttonGrid.add(new Label("Text Line Color:"), 5, 2);
        buttonGrid.add(textLineColorPicker, 6, 2);
        ToggleButton toggleDrawingButton = new ToggleButton("Start Drawing");
        buttonGrid.add(toggleDrawingButton, 4, 3);
        // Example position in the GridPane

        ColorPicker drawingColorPicker = new ColorPicker(Color.BLACK);
        buttonGrid.add(new Label("Drawing Color:"), 5, 3);
        buttonGrid.add(drawingColorPicker, 6, 3);
        Separator separator = new Separator();
        separator.setPrefWidth(800);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(buttonGrid, separator, scrollPane);

        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mind Mapping Application with Scroll Pane");
        primaryStage.show();


        loadButton.setOnAction(event -> {
            try {
                // Fetch the list of mind maps saved by the user
                List<String[]> mindMaps = DatabaseUtils.getMindMaps(username);

                if (mindMaps.isEmpty()) {
                    showInfoAlert("Error", "No Mind Maps found for the user.");
                    return;
                }

                // Display the list of mind maps to the user for selection
                ChoiceDialog<String> dialog = new ChoiceDialog<>(mindMaps.get(0)[1],
                        mindMaps.stream().map(m -> m[1]).toList());
                dialog.setTitle("Load Mind Map");
                dialog.setHeaderText("Select a Mind Map to Load");
                dialog.setContentText("Mind Map Name:");

                Optional<String> result = dialog.showAndWait();
                if (result.isEmpty()) {
                    return; // User canceled the dialog
                }

                String selectedMindMapName = result.get();

                // Load the JSON data for the selected mind map
                String data = DatabaseUtils.loadMindMap(username, selectedMindMapName);

                if (data != null) {
                    // Clear the canvas before loading new data
                    canvas.getChildren().clear();

                    // Deserialize the JSON data and populate the canvas
                    deserializeCanvasFromJson(canvas, data);

                    // Transition to the canvas interface
                    primaryStage.setScene(new Scene(new VBox(buttonGrid, scrollPane), 900, 600));
                    primaryStage.setTitle("Mind Mapping Application");

                    // Show a success message
                    showInfoAlert("Success", "Mind Map '" + selectedMindMapName + "' Loaded Successfully!");
                } else {
                    showInfoAlert("Error", "Failed to load the selected Mind Map.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showInfoAlert("Error", "An error occurred while loading the Mind Map.");
            }
        });


        canvas.setOnMouseClicked(event -> deselectAll());

        canvas.setOnMouseDragged(event -> {
            if (isDrawing && toggleDrawingButton.isSelected()) {
                Point2D localCoordinates = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                LineTo lineTo = new LineTo(localCoordinates.getX(), localCoordinates.getY());
                freehandPath.getElements().add(lineTo);
            }
        });





        canvas.setOnMousePressed(event -> {
            if (toggleDrawingButton.isSelected()) {
                Point2D localCoordinates = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                startX = localCoordinates.getX();
                startY = localCoordinates.getY();
                isDrawing = true;

                freehandPath = new Path();
                MoveTo moveTo = new MoveTo(startX, startY);
                freehandPath.getElements().add(moveTo);

                freehandPath.setStroke(drawingColorPicker.getValue());
                freehandPath.setStrokeWidth(2.0);
                freehandPath.setStrokeLineCap(StrokeLineCap.ROUND);

                freehandPath.setOnMouseClicked(pathEvent -> {
                    pathEvent.consume();
                    deselectAllLines();
                    selectedFreehandPath = freehandPath;
                    freehandPath.setStroke(Color.GOLD);
                });

                canvas.getChildren().add(freehandPath);
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (isDrawing && toggleDrawingButton.isSelected()) {
                isDrawing = false;
                if (freehandPath != null) {
                    freehandPath.setOnMouseClicked(pathEvent -> {
                        pathEvent.consume();
                        deselectAllLines();
                        selectedFreehandPath = freehandPath;
                        freehandPath.setStroke(Color.GOLD);
                    });

                    textConnections.add(freehandPath);
                }
                freehandPath = null;
            }
        });
    }




    private void deselectAll() {
        // Deselect any selected line
        if (selectedLine != null) {
            if (selectedLine.line != null) {
                selectedLine.line.setStroke(lineColors.getOrDefault(selectedLine, Color.BLACK));
            } else if (selectedLine.cubicCurve != null) {
                selectedLine.cubicCurve.setStroke(lineColors.getOrDefault(selectedLine, Color.BLACK));
            }
            if (selectedLine.arrowHead != null) {
                selectedLine.arrowHead.setFill(lineColors.getOrDefault(selectedLine, Color.BLACK));
            }
            selectedLine = null;
        }

        // Deselect any selected node
        if (currentSelectedNode != null) {
            applyDeselectAnimation(currentSelectedNode); // Optional deselect animation
            currentSelectedNode.setStroke(Color.DARKBLUE); // Reset stroke color
            currentSelectedNode.setStrokeWidth(DEFAULT_NODE_STROKE_WIDTH); // Reset stroke width
            currentSelectedNode = null;
        }

        // Remove glow effect from the first selected text
        if (firstSelectedText != null) {
            firstSelectedText.setEffect(null); // Clear glow effect
            firstSelectedText = null;         // Reset firstSelectedText
        }

        // Remove glow effect from the current selected text
        if (currentSelectedText != null) {
            currentSelectedText.setEffect(null); // Clear glow effect
            currentSelectedText = null;         // Reset currentSelectedText
        }

        // Deselect any freehand path if selected
        if (selectedFreehandPath != null) {
            selectedFreehandPath.setStroke(Color.BLACK); // Reset stroke color
            selectedFreehandPath = null; // Clear selection
        }

        // Deselect any selected text line
        if (selectedTextLine != null) {
            selectedTextLine.setStroke(Color.BLACK); // Reset the stroke color
            selectedTextLine = null; // Clear selection
        }
    }



    private String serializeCanvasToJson(Pane canvas, String username, String mindMapName) {
        StringBuilder jsonBuilder = new StringBuilder("{ \"nodes\": [");

        // Serialize nodes
        for (var node : canvas.getChildren()) {
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                jsonBuilder.append("{")
                        .append("\"type\": \"").append(shape.getClass().getSimpleName()).append("\",")
                        .append("\"x\": ").append(shape.getLayoutX()).append(",")
                        .append("\"y\": ").append(shape.getLayoutY()).append(",")
                        .append("\"fillColor\": \"").append(shape.getFill()).append("\",")
                        .append("\"borderColor\": \"").append(shape.getStroke()).append("\"");

                if (shape instanceof Circle) {
                    jsonBuilder.append(", \"radius\": ").append(((Circle) shape).getRadius());
                } else if (shape instanceof Rectangle) {
                    jsonBuilder.append(", \"width\": ").append(((Rectangle) shape).getWidth())
                            .append(", \"height\": ").append(((Rectangle) shape).getHeight());
                } else if (shape instanceof Polygon) {
                    Polygon polygon = (Polygon) shape;
                    jsonBuilder.append(", \"points\": [").append(polygon.getPoints()).append("]");
                    if (polygon.getPoints().size() == 6) {
                        jsonBuilder.append(", \"polygonType\": \"Hexagon\"");
                    } else if (polygon.getPoints().size() == 3) {
                        jsonBuilder.append(", \"polygonType\": \"Triangle\"");
                    }
                } else if (shape instanceof javafx.scene.shape.Ellipse) {
                    javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) shape;
                    jsonBuilder.append(", \"width\": ").append(ellipse.getRadiusX() * 2)
                            .append(", \"height\": ").append(ellipse.getRadiusY() * 2);
                }
                jsonBuilder.append("},");
            } else if (node instanceof Text) {
                Text text = (Text) node;
                jsonBuilder.append("{")
                        .append("\"type\": \"Text\",")
                        .append("\"content\": \"").append(text.getText()).append("\",")
                        .append("\"x\": ").append(text.getLayoutX()).append(",")
                        .append("\"y\": ").append(text.getLayoutY()).append(",")
                        .append("\"rotation\": ").append(text.getRotate()).append(",")
                        .append("\"font\": \"").append(text.getFont().getName()).append("\",")
                        .append("\"fontSize\": ").append(text.getFont().getSize()).append(",")
                        .append("\"color\": \"").append(text.getFill()).append("\"")
                        .append("},");
            }
        }

        // Remove trailing comma if necessary
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("], \"lines\": [");

        // Serialize lines
        for (var node : canvas.getChildren()) {
            if (node instanceof Line) {
                Line line = (Line) node;
                String style = getLineStyleSafely(line);
                jsonBuilder.append("{")
                        .append("\"type\": \"Straight Line\",")
                        .append("\"startX\": ").append(line.getStartX()).append(",")
                        .append("\"startY\": ").append(line.getStartY()).append(",")
                        .append("\"endX\": ").append(line.getEndX()).append(",")
                        .append("\"endY\": ").append(line.getEndY()).append(",")
                        .append("\"style\": \"").append(style).append("\"")
                        .append("},");
            } else if (node instanceof Arrow) {
                Arrow arrow = (Arrow) node;
                if (arrow.line != null) {
                    String style = getLineStyleSafely(arrow.line);
                    jsonBuilder.append("{")
                            .append("\"type\": \"Arrow Line\",")
                            .append("\"startX\": ").append(arrow.line.getStartX()).append(",")
                            .append("\"startY\": ").append(arrow.line.getStartY()).append(",")
                            .append("\"endX\": ").append(arrow.line.getEndX()).append(",")
                            .append("\"endY\": ").append(arrow.line.getEndY()).append(",")
                            .append("\"style\": \"").append(style).append("\"")
                            .append("},");
                }
            } else if (node instanceof CubicCurve) {
                CubicCurve curve = (CubicCurve) node;
                String style = getLineStyleSafely(curve);
                jsonBuilder.append("{")
                        .append("\"type\": \"Bezier Curve\",")
                        .append("\"startX\": ").append(curve.getStartX()).append(",")
                        .append("\"startY\": ").append(curve.getStartY()).append(",")
                        .append("\"endX\": ").append(curve.getEndX()).append(",")
                        .append("\"endY\": ").append(curve.getEndY()).append(",")
                        .append("\"controlX1\": ").append(curve.getControlX1()).append(",")
                        .append("\"controlY1\": ").append(curve.getControlY1()).append(",")
                        .append("\"controlX2\": ").append(curve.getControlX2()).append(",")
                        .append("\"controlY2\": ").append(curve.getControlY2()).append(",")
                        .append("\"style\": \"").append(style).append("\"")
                        .append("},");
            }
        }

        // Remove trailing comma if necessary
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("], \"background\": {");

        // Serialize background color and image
        if (canvas.getBackground() != null && canvas.getBackground().getFills().size() > 0) {
            jsonBuilder.append("\"color\": \"").append(canvas.getBackground().getFills().get(0).getFill()).append("\",");
        }
        if (canvas.getBackground() != null && canvas.getBackground().getImages().size() > 0) {
            jsonBuilder.append("\"image\": \"").append(canvas.getBackground().getImages().get(0).getImage().getUrl()).append("\"");
        }

        // Remove trailing comma if necessary
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("}}");
        String jsonData = jsonBuilder.toString();

        // Save to database
        boolean success = DatabaseUtils.saveMindMap(username, mindMapName, jsonData);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Successful");
            alert.setHeaderText(null);
            alert.setContentText("Mind map saved successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Failed");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the mind map.");
            alert.showAndWait();
        }

        return jsonData;
    }

    // Safe method for getting the line style
    private String getLineStyleSafely(Shape line) {
        if (line == null) {
            return "Solid"; // Default style if the line is null
        }
        return getLineStyle(line);
    }

    // Helper method to determine the line style
    private String getLineStyle(Shape line) {
        if (line.getStrokeDashArray() == null || line.getStrokeDashArray().isEmpty()) {
            return "Solid";
        } else if (line.getStrokeDashArray().equals(List.of(10.0, 10.0))) {
            return "Dashed";
        } else if (line.getStrokeDashArray().equals(List.of(2.0, 10.0))) {
            return "Dotted";
        } else if (line.getStrokeWidth() > 3.0) {
            return "Bold";
        }
        return "Solid";
    }


    // Helper method to get the file extension
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }



    private void deserializeCanvasFromJson(Pane canvas, String jsonData) {
        canvas.getChildren().clear(); // Clear the canvas before loading new data

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Deserialize background
            if (jsonObject.has("background")) {
                JSONObject background = jsonObject.getJSONObject("background");
                if (background.has("color")) {
                    canvas.setStyle(String.format("-fx-background-color: %s;", background.getString("color")));
                }
                if (background.has("image")) {
                    String imageUrl = background.getString("image");
                    canvas.setStyle(String.format(
                            "-fx-background-image: url('%s'); -fx-background-position: center center; -fx-background-repeat: no-repeat;",
                            imageUrl
                    ));
                }
            }

            // Deserialize nodes
            if (jsonObject.has("nodes")) {
                JSONArray nodesArray = jsonObject.getJSONArray("nodes");
                for (int i = 0; i < nodesArray.length(); i++) {
                    JSONObject nodeObject = nodesArray.getJSONObject(i);
                    String type = nodeObject.getString("type");

                    // Use createNodeShape for all node types
                    Shape shape = createNodeShape(type, nodeObject);
                    if (shape != null) {
                        shape.setLayoutX(nodeObject.getDouble("x"));
                        shape.setLayoutY(nodeObject.getDouble("y"));
                        canvas.getChildren().add(shape);
                    }
                }
            }

            // Deserialize lines
            if (jsonObject.has("lines")) {
                JSONArray linesArray = jsonObject.getJSONArray("lines");
                for (int i = 0; i < linesArray.length(); i++) {
                    JSONObject lineObject = linesArray.getJSONObject(i);
                    String type = lineObject.getString("type");

                    if ("Straight Line".equals(type)) {
                        Line line = createStraightLine(lineObject);
                        canvas.getChildren().add(line);
                    } else if ("Arrow Line".equals(type)) {
                        Line line = createStraightLine(lineObject);
                        if (line != null) {
                            Polygon arrowHead = createArrowHead(
                                    new Point2D(line.getStartX(), line.getStartY()),
                                    new Point2D(line.getEndX(), line.getEndY())
                            );
                            canvas.getChildren().addAll(line, arrowHead);
                        }
                    } else if ("Bezier Curve".equals(type)) {
                        CubicCurve curve = createBezierCurve(lineObject);
                        canvas.getChildren().add(curve);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showInfoAlert("Error", "Failed to load the mind map: " + e.getMessage());
        }
    }



    // Helper method to create nodes based on type
    private Shape createNodeShape(String type, JSONObject nodeObject) {
        Shape shape = null;
        switch (type) {
            case "Circle":
                shape = new Circle(nodeObject.optDouble("radius", 30));
                break;
            case "Square Rectangle":
            case "Wide Rectangle":
                shape = new Rectangle(
                        nodeObject.optDouble("width", 40),
                        nodeObject.optDouble("height", 40)
                );
                break;
            case "Hexagon":
                double hexRadius = nodeObject.optDouble("radius", 30);
                Polygon hexagon = new Polygon();
                for (int j = 0; j < 6; j++) {
                    double angle = Math.toRadians(60 * j);
                    double xPoint = hexRadius * Math.cos(angle);
                    double yPoint = hexRadius * Math.sin(angle);
                    hexagon.getPoints().addAll(xPoint, yPoint);
                }
                shape = hexagon;
                break;
            case "Ellipse":
                shape = new Ellipse(
                        nodeObject.optDouble("width", 60) / 2,
                        nodeObject.optDouble("height", 40) / 2
                );
                break;
            case "Triangle":
                double size = nodeObject.optDouble("size", 40);
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(
                        0.0, -size,
                        -size, size,
                        size, size
                );
                shape = triangle;
                break;
            case "Animated Node":
                shape = new Circle(nodeObject.optDouble("radius", 30));
                applyAnimation(shape);
                break;
        }
        return shape;
    }

    // Helper method to create straight lines
    private Line createStraightLine(JSONObject lineObject) {
        try {
            Line line = new Line(
                    lineObject.getDouble("startX"),
                    lineObject.getDouble("startY"),
                    lineObject.getDouble("endX"),
                    lineObject.getDouble("endY")
            );
            String color = lineObject.optString("color", "#000000"); // Default to black
            line.setStroke(Color.web(color));
            applyLineStyle(line, lineObject.optString("style", "Solid"));
            return line;
        } catch (JSONException e) {
            System.err.println("Error creating straight line: " + e.getMessage());
            return null;
        }
    }

    // Helper method to create Bezier curves
    private CubicCurve createBezierCurve(JSONObject lineObject) {
        try {
            CubicCurve curve = new CubicCurve(
                    lineObject.getDouble("startX"),
                    lineObject.getDouble("startY"),
                    lineObject.getDouble("controlX1"),
                    lineObject.getDouble("controlY1"),
                    lineObject.getDouble("controlX2"),
                    lineObject.getDouble("controlY2"),
                    lineObject.getDouble("endX"),
                    lineObject.getDouble("endY")
            );
            String color = lineObject.optString("color", "#000000"); // Default to black
            curve.setStroke(Color.web(color));
            applyLineStyle(curve, lineObject.optString("style", "Solid"));
            return curve;
        } catch (JSONException e) {
            System.err.println("Error creating Bezier curve: " + e.getMessage());
            return null;
        }
    }




    // Helper method to apply line style
    private void applyLineStyle(Shape line, String style) {
        switch (style) {
            case "Dashed":
                line.getStrokeDashArray().setAll(10.0, 10.0);
                break;
            case "Dotted":
                line.getStrokeDashArray().setAll(2.0, 10.0);
                break;
            case "Bold":
                line.setStrokeWidth(5);
                break;
            case "Solid":
            default:
                line.getStrokeDashArray().clear();
                line.setStrokeWidth(3);
                break;
        }
    }

    private Shape createNode(JSONObject nodeObject) {
        Shape shape = null;

        // Get common node properties
        String type = nodeObject.getString("type");
        double x = nodeObject.getDouble("x");
        double y = nodeObject.getDouble("y");
        String fillColor = nodeObject.optString("fillColor", "#ADD8E6"); // Default: light blue
        String borderColor = nodeObject.optString("borderColor", "#00008B"); // Default: dark blue

        // Create the shape based on the type
        switch (type) {
            case "Circle":
                shape = new Circle(nodeObject.optDouble("radius", 30)); // Default radius = 30
                break;

            case "Square Rectangle":
            case "Wide Rectangle":
                shape = new Rectangle(
                        nodeObject.optDouble("width", 60),   // Default width = 60
                        nodeObject.optDouble("height", 40)  // Default height = 40
                );
                break;

            case "Hexagon":
                double hexRadius = nodeObject.optDouble("radius", 30);
                shape = createHexagon(hexRadius);
                break;

            case "Ellipse":
                shape = new Ellipse(
                        nodeObject.optDouble("radiusX", 50),  // Default radiusX = 50
                        nodeObject.optDouble("radiusY", 30)  // Default radiusY = 30
                );
                break;

            case "Triangle":
                double size = nodeObject.optDouble("size", 40); // Default size = 40
                shape = createTriangle(size);
                break;

            case "Animated Node":
                shape = new Circle(nodeObject.optDouble("radius", 30));
                applyAnimation(shape); // Add animations to the node
                break;

            default:
                System.err.println("Unsupported node type: " + type);
        }

        // Set position and styles for the shape
        if (shape != null) {
            shape.setLayoutX(x);
            shape.setLayoutY(y);
            shape.setFill(Color.web(fillColor));
            shape.setStroke(Color.web(borderColor));
        }

        return shape;
    }

    private Polygon createHexagon(double radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        return hexagon;
    }

    private Polygon createTriangle(double size) {
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                0.0, -size,          // Top vertex
                -size, size,         // Bottom-left vertex
                size, size           // Bottom-right vertex
        );
        return triangle;
    }




    private void createNode(Pane canvas, double x, double y) {
        Shape node;
        String selectedShape = shapeSelector.getValue();


        if ("Square Rectangle".equals(selectedShape)) {
            double size = 40;
            node = new Rectangle(size, size);
            node.setLayoutX(x - size / 2);
            node.setLayoutY(y - size / 2);
        } else if ("Wide Rectangle".equals(selectedShape)) {
            double height = 40;
            double width = height * 2.5;
            node = new Rectangle(width, height);
            node.setLayoutX(x - width / 2);
            node.setLayoutY(y - height / 2);
        } else if ("Hexagon".equals(selectedShape)) {
            double radius = 30;
            node = new Polygon();
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                double xPoint = radius * Math.cos(angle);
                double yPoint = radius * Math.sin(angle);
                ((Polygon) node).getPoints().addAll(xPoint, yPoint);
            }
            node.setLayoutX(x);
            node.setLayoutY(y);
        } else if ("Ellipse".equals(selectedShape)) {
            double width = 60;
            double height = 40;
            node = new javafx.scene.shape.Ellipse(width / 2, height / 2);
            node.setLayoutX(x);
            node.setLayoutY(y);
        } else if ("Triangle".equals(selectedShape)) {
            double size = 40;
            node = new Polygon();
            ((Polygon) node).getPoints().addAll(
                    0.0, -size,
                    -size, size,
                    size, size
            );
            node.setLayoutX(x);
            node.setLayoutY(y);
        } else if ("Animated Node".equals(selectedShape)) {
            node = new Circle(30);
            node.setLayoutX(x);
            node.setLayoutY(y);
            applyAnimation(node);
        } else { // Circle
            node = new Circle(30);
            node.setLayoutX(x);
            node.setLayoutY(y);
        }
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.GRAY);
        node.setEffect(dropShadow);

        node.setFill(Color.LIGHTBLUE);
        node.setStroke(Color.DARKBLUE);
        node.setStrokeWidth(DEFAULT_NODE_STROKE_WIDTH);

        Text nodeText = new Text("");
        nodeText.setFont(new Font(fontSelector.getValue(), fontSizeSlider.getValue()));
        nodeText.setMouseTransparent(true);

        node.setUserData(nodeText);


        node.setOnMouseClicked(event -> {
            event.consume();
            deselectAll();

            // Check if a text was previously selected
            if (firstSelectedText != null) {
                // Connect the node to the selected text
                connectNodeToText(node, firstSelectedText, canvas);
                firstSelectedText.setEffect(null); // Remove glow effect from text
                firstSelectedText = null;
            } else if (firstSelectedNode == null) {
                // Select the current node
                firstSelectedNode = node;
            } else {
                // A node was previously selected; connect the two nodes
                if (firstSelectedNode != node) {
                    Arrow connection;
                    if ("Arrow Line".equals(lineTypeSelector.getValue())) {
                        connection = createArrowConnection(firstSelectedNode, node);
                    } else {
                        connection = createStraightConnection(firstSelectedNode, node);
                    }
                    applyLineStyle(connection);
                    addLineEventHandlers(connection, canvas);
                    canvas.getChildren().add(connection);
                    canvas.getChildren().add(connection.getArrowHead());

                    connections.add(connection);
                    firstSelectedNode = null; // Reset the first selected node
                }
            }

            // Highlight the currently selected node
            currentSelectedNode = node;
            currentSelectedNode.setStroke(SELECTED_COLOR);
            currentSelectedNode.setStrokeWidth(SELECTED_STROKE_WIDTH);
            applySelectAnimation(node);

            // Adjust the size slider for the selected node
            if (node instanceof Circle) {
                sizeSlider.setValue(((Circle) node).getRadius());
            } else if (node instanceof Rectangle) {
                sizeSlider.setValue(((Rectangle) node).getHeight());
            } else if (node instanceof Polygon) {
                sizeSlider.setValue(30);
            } else if (node instanceof javafx.scene.shape.Ellipse) {
                sizeSlider.setValue(((javafx.scene.shape.Ellipse) node).getRadiusY());
            }
        });


        node.setOnMousePressed(event -> {
            initialX = event.getSceneX() - node.getLayoutX();
            initialY = event.getSceneY() - node.getLayoutY();
        });

        node.setOnMouseDragged(event -> {
            double newX = event.getSceneX() - initialX;
            double newY = event.getSceneY() - initialY;
            node.setLayoutX(newX);
            node.setLayoutY(newY);

            Text linkedText = (Text) node.getUserData();
            centerTextInNode(node, linkedText);
            updateConnections(); // This method updates all existing connections
        });

        canvas.getChildren().addAll(node, nodeText);
    }
    private CubicCurve createBezierConnection(Shape node1, Shape node2) {
        double startX = node1.getLayoutX();
        double startY = node1.getLayoutY();
        double endX = node2.getLayoutX();
        double endY = node2.getLayoutY();

        // Control points to make the curve look more natural
        double controlX1 = startX + (endX - startX) / 2;
        double controlY1 = startY - 100;
        double controlX2 = endX - (endX - startX) / 2;
        double controlY2 = endY - 100;

        CubicCurve cubicCurve = new CubicCurve(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY);
        cubicCurve.setStroke(Color.BLACK);
        cubicCurve.setStrokeWidth(3);
        cubicCurve.setFill(Color.TRANSPARENT);
        cubicCurve.setStrokeLineCap(StrokeLineCap.ROUND);
        cubicCurve.setStrokeLineJoin(StrokeLineJoin.ROUND);

        return cubicCurve;
    }


    private void applyAnimation(Shape node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.8), node);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.play();
    }

    private void applySelectAnimation(Shape node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), node);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }

    private void applyDeselectAnimation(Shape node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), node);
        scaleTransition.setFromX(1.1);
        scaleTransition.setFromY(1.1);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setAutoReverse(false);
        scaleTransition.setCycleCount(1);
        scaleTransition.play();
    }

    private Arrow createArrowConnection(Shape node1, Shape node2) {
        if ("Bezier Curve".equals(lineTypeSelector.getValue())) {
            // Create a Bezier curve connection
            CubicCurve cubicCurve = createBezierConnection(node1, node2);
            Arrow arrow = new Arrow(cubicCurve);
            arrow.updateArrowHead();
            return arrow;
        } else {
            // Create a straight line connection
            Arrow arrow = createStraightConnection(node1, node2);
            arrow.getArrowHead().setVisible("Arrow Line".equals(lineTypeSelector.getValue()));
            return arrow;
        }
    }
    private Arrow createStraightConnection(Shape node1, Shape node2) {
        Arrow arrow = createConnectionLine(node1, node2);
        arrow.getArrowHead().setVisible(false);
        return arrow;
    }



    private Arrow createConnectionLine(Shape node1, Shape node2) {
        double startX, startY, endX, endY;

        // Calculate start and end positions
        double angleToNode2 = Math.atan2(node2.getLayoutY() - node1.getLayoutY(), node2.getLayoutX() - node1.getLayoutX());
        double angleToNode1 = Math.atan2(node1.getLayoutY() - node2.getLayoutY(), node1.getLayoutX() - node2.getLayoutX());

        if (node1 instanceof Circle) {
            startX = node1.getLayoutX() + ((Circle) node1).getRadius() * Math.cos(angleToNode2);
            startY = node1.getLayoutY() + ((Circle) node1).getRadius() * Math.sin(angleToNode2);
        } else if (node1 instanceof Polygon) {
            double[] closestPoint = getClosestPolygonPoint((Polygon) node1, node2.getLayoutX(), node2.getLayoutY());
            startX = node1.getLayoutX() + closestPoint[0];
            startY = node1.getLayoutY() + closestPoint[1];
        } else if (node1 instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) node1;
            double ellipseAngle = Math.atan2(node2.getLayoutY() - node1.getLayoutY(), node2.getLayoutX() - node1.getLayoutX());
            startX = node1.getLayoutX() + ellipse.getRadiusX() * Math.cos(ellipseAngle);
            startY = node1.getLayoutY() + ellipse.getRadiusY() * Math.sin(ellipseAngle);
        } else { // Rectangle
            Rectangle rectangle = (Rectangle) node1;
            startX = node1.getLayoutX() + rectangle.getWidth() / 2 * (1 + Math.cos(angleToNode2));
            startY = node1.getLayoutY() + rectangle.getHeight() / 2 * (1 + Math.sin(angleToNode2));
        }

        if (node2 instanceof Circle) {
            endX = node2.getLayoutX() + ((Circle) node2).getRadius() * Math.cos(angleToNode1);
            endY = node2.getLayoutY() + ((Circle) node2).getRadius() * Math.sin(angleToNode1);
        } else if (node2 instanceof Polygon) {
            double[] closestPoint = getClosestPolygonPoint((Polygon) node2, node1.getLayoutX(), node1.getLayoutY());
            endX = node2.getLayoutX() + closestPoint[0];
            endY = node2.getLayoutY() + closestPoint[1];
        } else if (node2 instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) node2;
            double ellipseAngle = Math.atan2(node1.getLayoutY() - node2.getLayoutY(), node1.getLayoutX() - node2.getLayoutX());
            endX = node2.getLayoutX() + ellipse.getRadiusX() * Math.cos(ellipseAngle);
            endY = node2.getLayoutY() + ellipse.getRadiusY() * Math.sin(ellipseAngle);
        } else { // Rectangle
            Rectangle rectangle = (Rectangle) node2;
            endX = node2.getLayoutX() + rectangle.getWidth() / 2 * (1 + Math.cos(angleToNode1));
            endY = node2.getLayoutY() + rectangle.getHeight() / 2 * (1 + Math.sin(angleToNode1));
        }

        // If line type is Bezier Curve, create a CubicCurve, otherwise create a straight line.
        Arrow arrow;
        if (lineTypeSelector.getValue().equals("Bezier Curve")) {
            double controlX1 = (startX + endX) / 2;
            double controlY1 = startY - 100;
            double controlX2 = (startX + endX) / 2;
            double controlY2 = endY - 100;

            CubicCurve cubicCurve = new CubicCurve(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY);
            cubicCurve.setStroke(Color.BLACK);
            cubicCurve.setStrokeWidth(3);
            cubicCurve.setFill(Color.TRANSPARENT);
            arrow = new Arrow(cubicCurve);
        } else {
            Line line = new Line(startX, startY, endX, endY);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(3);
            arrow = new Arrow(line);
        }

        lineColors.put(arrow, Color.BLACK);
        arrow.updateArrowHead();
        return arrow;
    }


    private void applyLineStyle(Arrow arrow) {
        String lineStyle = lineStyleSelector.getValue();
        if (arrow.line != null) { // If the arrow is a straight line
            switch (lineStyle) {
                case "Dashed":
                    arrow.line.getStrokeDashArray().setAll(10.0, 10.0);
                    break;
                case "Dotted":
                    arrow.line.getStrokeDashArray().setAll(2.0, 10.0);
                    break;
                case "Bold":
                    arrow.line.setStrokeWidth(5);
                    break;
                default: // Solid
                    arrow.line.getStrokeDashArray().clear();
                    arrow.line.setStrokeWidth(3);
                    break;
            }
        } else if (arrow.cubicCurve != null) { // If the arrow is a cubic curve (Bezier)
            switch (lineStyle) {
                case "Dashed":
                    arrow.cubicCurve.getStrokeDashArray().setAll(10.0, 10.0);
                    break;
                case "Dotted":
                    arrow.cubicCurve.getStrokeDashArray().setAll(2.0, 10.0);
                    break;
                case "Bold":
                    arrow.cubicCurve.setStrokeWidth(5);
                    break;
                default: // Solid
                    arrow.cubicCurve.getStrokeDashArray().clear();
                    arrow.cubicCurve.setStrokeWidth(3);
                    break;
            }
        }
    }


    private double[] getClosestPolygonPoint(Polygon polygon, double targetX, double targetY) {
        double closestDistance = Double.MAX_VALUE;
        double[] closestPoint = new double[2];
        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            double x = polygon.getPoints().get(i);
            double y = polygon.getPoints().get(i + 1);
            double distance = distance(x + polygon.getLayoutX(), y + polygon.getLayoutY(), targetX, targetY);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint[0] = x;
                closestPoint[1] = y;
            }
        }
        return closestPoint;
    }

    private void resizeNode(Shape node, double size) {
        if (node instanceof Circle) {
            ((Circle) node).setRadius(size);
        } else if (node instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) node;
            if (rectangle.getWidth() == rectangle.getHeight()) { // Square Rectangle
                rectangle.setWidth(size);
                rectangle.setHeight(size);
            } else { // Wide Rectangle
                rectangle.setHeight(size);
                rectangle.setWidth(size * 2.5);
            }
        } else if (node instanceof Polygon) {
            Polygon polygon = (Polygon) node;
            if (polygon.getPoints().size() == 6 * 2) { // Hexagon
                polygon.getPoints().clear();
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians(60 * i);
                    double xPoint = size * Math.cos(angle);
                    double yPoint = size * Math.sin(angle);
                    polygon.getPoints().addAll(xPoint, yPoint);
                }
            } else if (polygon.getPoints().size() == 3 * 2) { // Triangle
                polygon.getPoints().clear();
                polygon.getPoints().addAll(
                        0.0, -size,
                        -size, size,
                        size, size
                );
            }
        } else if (node instanceof javafx.scene.shape.Ellipse) {
            javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) node;
            ellipse.setRadiusX(size * 1.5);
            ellipse.setRadiusY(size);
        }
    }

    private void addLineEventHandlers(Arrow line, Pane canvas) {
        line.setOnMouseClicked(event -> {
            event.consume();
            deselectAll();
            selectedLine = line;

            // Set the stroke on the internal components of the Arrow
            if (line.line != null) {
                line.line.setStroke(SELECTED_COLOR);
            } else if (line.cubicCurve != null) {
                line.cubicCurve.setStroke(SELECTED_COLOR);
            }

            line.getArrowHead().setFill(SELECTED_COLOR);
        });
    }
    private void applyGlowEffect(Text text) {
        DropShadow glow = new DropShadow();
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        glow.setRadius(15); // Increase the radius for a more visible glow
        glow.setColor(Color.AQUAMARINE); // Use a bright color like GOLD for visibility
        glow.setSpread(0.8); // Increase spread to make the glow denser
        text.setEffect(glow);
    }


    private void addCustomText(Pane canvas) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Custom Text");
        dialog.setHeaderText("Enter the text you want to add:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            Text customText = new Text(text);
            customText.setFont(new Font(fontSelector.getValue(), fontSizeSlider.getValue()));
            customText.setX(400); // Initial position
            customText.setY(250); // Initial position
            customText.setFill(Color.BLACK);

            // Enable dragging of the text
            customText.setOnMousePressed(event -> {
                if (!isDrawing) {
                    // Enable dragging mode
                    initialX = event.getSceneX() - customText.getX();
                    initialY = event.getSceneY() - customText.getY();
                } else {
                    // Freehand drawing logic
                    startX = event.getSceneX();
                    startY = event.getSceneY();
                    freehandPath = new Path();
                    MoveTo moveTo = new MoveTo(startX, startY);
                    freehandPath.getElements().add(moveTo);
                    freehandPath.setStroke(Color.BLACK);
                    freehandPath.setStrokeWidth(2.0);
                    freehandPath.setStrokeLineCap(StrokeLineCap.ROUND);

                    canvas.getChildren().add(freehandPath);
                }
            });

            customText.setOnMouseDragged(event -> {
                if (!isDrawing) {
                    // Dragging text
                    customText.setX(event.getSceneX() - initialX);
                    customText.setY(event.getSceneY() - initialY);
                    updateConnections(); // Update all connected lines dynamically
                } else {
                    // Freehand drawing
                    freehandPath.getElements().add(new LineTo(event.getSceneX(), event.getSceneY()));
                }
            });

            customText.setOnMouseReleased(event -> {
                if (isDrawing) {
                    isDrawing = false; // End freehand drawing
                    if (freehandPath != null) {
                        freehandPath.setOnMouseClicked(pathEvent -> {
                            pathEvent.consume(); // Prevent triggering other handlers
                            deselectAllLines(); // Deselect other lines
                            selectedFreehandPath = freehandPath;
                            freehandPath.setStroke(Color.GOLD); // Highlight the selected path
                        });
                        textConnections.add(freehandPath);
                    }
                    freehandPath = null; // Reset freehand path
                }
            });

            // Add click functionality to handle connections
            customText.setOnMouseClicked(event -> {
                event.consume(); // Prevent other handlers from being triggered
                deselectAll(); // Deselect all other selections
                applyGlowEffect(customText); // Highlight the selected text
                currentSelectedText = customText; // Set the clicked text as the currentSelectedText

                if (firstSelectedNode != null) {
                    // Connect the currently selected node to the custom text
                    connectNodeToText(firstSelectedNode, customText, canvas);
                    firstSelectedNode.setStroke(Color.DARKBLUE); // Reset the stroke of the previously selected node
                    firstSelectedNode = null; // Clear the firstSelectedNode
                } else if (firstSelectedText == null) {
                    // If no text is selected yet, select the current text
                    firstSelectedText = customText;
                } else if (firstSelectedText != customText) {
                    // If another text was already selected, connect the two texts
                    createConnectionBetweenTexts(canvas);
                    firstSelectedText.setEffect(null); // Remove the glow effect from the previously selected text
                    firstSelectedText = null; // Clear the firstSelectedText
                }

                // Ensure the text can be rotated by "Rotate Left" or "Rotate Right" buttons
                currentSelectedText.setOnMousePressed(pressEvent -> {
                    initialX = pressEvent.getSceneX() - customText.getX();
                    initialY = pressEvent.getSceneY() - customText.getY();
                });

                currentSelectedText.setOnMouseDragged(dragEvent -> {
                    customText.setX(dragEvent.getSceneX() - initialX);
                    customText.setY(dragEvent.getSceneY() - initialY);
                });
            });


            // Add the custom text to the canvas
            canvas.getChildren().add(customText);
        });
    }



    private void createConnectionBetweenTexts(Pane canvas) {
        if (firstSelectedText != null && currentSelectedText != null && firstSelectedText != currentSelectedText) {
            // Get bounds of the first and second text
            Bounds firstBounds = firstSelectedText.getBoundsInParent();
            Bounds secondBounds = currentSelectedText.getBoundsInParent();

            // Calculate connection points based on edge alignment
            double startX, startY, endX, endY;

            // Determine the closest edge of the first text
            if (firstBounds.getMaxX() < secondBounds.getMinX()) { // First text is left of second
                startX = firstBounds.getMaxX();
                startY = firstBounds.getMinY() + firstBounds.getHeight() / 2;
            } else if (firstBounds.getMinX() > secondBounds.getMaxX()) { // First text is right of second
                startX = firstBounds.getMinX();
                startY = firstBounds.getMinY() + firstBounds.getHeight() / 2;
            } else if (firstBounds.getMaxY() < secondBounds.getMinY()) { // First text is above second
                startX = firstBounds.getMinX() + firstBounds.getWidth() / 2;
                startY = firstBounds.getMaxY();
            } else { // First text is below second
                startX = firstBounds.getMinX() + firstBounds.getWidth() / 2;
                startY = firstBounds.getMinY();
            }

            // Determine the closest edge of the second text
            if (secondBounds.getMaxX() < firstBounds.getMinX()) { // Second text is left of first
                endX = secondBounds.getMaxX();
                endY = secondBounds.getMinY() + secondBounds.getHeight() / 2;
            } else if (secondBounds.getMinX() > firstBounds.getMaxX()) { // Second text is right of first
                endX = secondBounds.getMinX();
                endY = secondBounds.getMinY() + secondBounds.getHeight() / 2;
            } else if (secondBounds.getMaxY() < firstBounds.getMinY()) { // Second text is above first
                endX = secondBounds.getMinX() + secondBounds.getWidth() / 2;
                endY = secondBounds.getMaxY();
            } else { // Second text is below first
                endX = secondBounds.getMinX() + secondBounds.getWidth() / 2;
                endY = secondBounds.getMinY();
            }

            // Create the appropriate line based on the selected type
            String lineType = lineTypeSelector.getValue();
            if ("Straight Line".equals(lineType)) {
                Line connection = new Line(startX, startY, endX, endY);
                connection.setStroke(Color.BLACK);
                connection.setStrokeWidth(2);

                // Add event handlers for the line
                connection.setOnMouseClicked(event -> {
                    event.consume(); // Prevent other handlers from being triggered
                    deselectAllLines();
                    selectedTextLine = connection;
                    connection.setStroke(Color.GOLD); // Highlight the selected line
                });

                // Add the line to the canvas and the connection list
                textConnections.add(connection);
                canvas.getChildren().add(connection);
            } else if ("Arrow Line".equals(lineType)) {
                // Create the line
                Line arrowLine = new Line(startX, startY, endX, endY);
                arrowLine.setStroke(Color.BLACK);
                arrowLine.setStrokeWidth(2);

                // Create the arrowhead as a polygon
                Polygon arrowHead = new Polygon();
                arrowHead.getPoints().addAll(
                        0.0, 0.0,  // Tip of the arrow
                        -10.0, -5.0, // Bottom left corner
                        -10.0, 5.0   // Top left corner
                );
                arrowHead.setFill(Color.BLACK);

                // Calculate the angle of the arrow
                double angle = Math.atan2(endY - startY, endX - startX);

                // Position the arrowhead at the end of the line
                arrowHead.setLayoutX(endX);
                arrowHead.setLayoutY(endY);

                // Rotate the arrowhead to align with the line
                arrowHead.getTransforms().clear();
                arrowHead.getTransforms().add(javafx.scene.transform.Transform.rotate(Math.toDegrees(angle), 0, 0));

                // Add event handlers for the line
                arrowLine.setOnMouseClicked(event -> {
                    event.consume();
                    deselectAllLines();
                    selectedTextLine = arrowLine;
                    arrowLine.setStroke(Color.GOLD);
                });

                // Add the line and arrowhead to the canvas
                textConnections.add(arrowLine);
                canvas.getChildren().addAll(arrowLine, arrowHead);
            }

            else if ("Bezier Curve".equals(lineType)) {
                // Create a cubic curve
                CubicCurve bezierCurve = new CubicCurve(
                        startX, startY,
                        (startX + endX) / 2, startY - 50, // Control point 1
                        (startX + endX) / 2, endY - 50, // Control point 2
                        endX, endY
                );
                bezierCurve.setStroke(Color.BLACK);
                bezierCurve.setStrokeWidth(2);
                bezierCurve.setFill(null);

                // Add event handlers for the curve
                bezierCurve.setOnMouseClicked(event -> {
                    event.consume();
                    deselectAllLines();
                    selectedTextLine = null; // Ensure deselection behavior works
                    bezierCurve.setStroke(Color.GOLD);
                });

                // Add the curve to the canvas
                canvas.getChildren().add(bezierCurve);
            }

            // Reset selection
            firstSelectedText = null;
            currentSelectedText = null;
        }
    }



    private void deselectAllLines() {
        if (selectedTextLine != null) {
            selectedTextLine.setStroke(Color.BLACK); // Reset the line color
            selectedTextLine = null;
        }
    }




    private void addTextToNode() {
        if (currentSelectedNode != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add/Update Text in Node");
            dialog.setHeaderText("Enter the text for the node:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(text -> {
                Text nodeText = (Text) currentSelectedNode.getUserData();
                nodeText.setText(text);
                nodeText.setFont(new Font(fontSelector.getValue(), fontSizeSlider.getValue()));
                centerTextInNode(currentSelectedNode, nodeText);
            });
        }
    }

    private void updateConnections() {
        for (Arrow connection : connections) {
            Shape node1 = (Shape) connection.getUserData();
            Shape node2 = getConnectedNode(connection, node1);
            if (node2 != null) {
                double startX, startY, endX, endY;

                double angleToNode2 = Math.atan2(node2.getLayoutY() - node1.getLayoutY(), node2.getLayoutX() - node1.getLayoutX());
                double angleToNode1 = Math.atan2(node1.getLayoutY() - node2.getLayoutY(), node1.getLayoutX() - node2.getLayoutX());

                if (node1 instanceof Circle) {
                    startX = node1.getLayoutX() + ((Circle) node1).getRadius() * Math.cos(angleToNode2);
                    startY = node1.getLayoutY() + ((Circle) node1).getRadius() * Math.sin(angleToNode2);
                } else if (node1 instanceof Polygon) {
                    double[] closestPoint = getClosestPolygonPoint((Polygon) node1, node2.getLayoutX(), node2.getLayoutY());
                    startX = node1.getLayoutX() + closestPoint[0];
                    startY = node1.getLayoutY() + closestPoint[1];
                } else if (node1 instanceof javafx.scene.shape.Ellipse) {
                    javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) node1;
                    double ellipseAngle = Math.atan2(node2.getLayoutY() - node1.getLayoutY(), node2.getLayoutX() - node1.getLayoutX());
                    startX = node1.getLayoutX() + ellipse.getRadiusX() * Math.cos(ellipseAngle);
                    startY = node1.getLayoutY() + ellipse.getRadiusY() * Math.sin(ellipseAngle);
                } else { // Rectangle
                    Rectangle rectangle = (Rectangle) node1;
                    startX = node1.getLayoutX() + rectangle.getWidth() / 2 * (1 + Math.cos(angleToNode2));
                    startY = node1.getLayoutY() + rectangle.getHeight() / 2 * (1 + Math.sin(angleToNode2));
                }

                if (node2 instanceof Circle) {
                    endX = node2.getLayoutX() + ((Circle) node2).getRadius() * Math.cos(angleToNode1);
                    endY = node2.getLayoutY() + ((Circle) node2).getRadius() * Math.sin(angleToNode1);
                } else if (node2 instanceof Polygon) {
                    double[] closestPoint = getClosestPolygonPoint((Polygon) node2, node1.getLayoutX(), node1.getLayoutY());
                    endX = node2.getLayoutX() + closestPoint[0];
                    endY = node2.getLayoutY() + closestPoint[1];
                } else if (node2 instanceof javafx.scene.shape.Ellipse) {
                    javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) node2;
                    double ellipseAngle = Math.atan2(node1.getLayoutY() - node2.getLayoutY(), node1.getLayoutX() - node2.getLayoutX());
                    endX = node2.getLayoutX() + ellipse.getRadiusX() * Math.cos(ellipseAngle);
                    endY = node2.getLayoutY() + ellipse.getRadiusY() * Math.sin(ellipseAngle);
                } else { // Rectangle
                    Rectangle rectangle = (Rectangle) node2;
                    endX = node2.getLayoutX() + rectangle.getWidth() / 2 * (1 + Math.cos(angleToNode1));
                    endY = node2.getLayoutY() + rectangle.getHeight() / 2 * (1 + Math.sin(angleToNode1));
                }

                // Update the internal line within the Arrow class
                if (connection.line != null) {
                    connection.line.setStartX(startX);
                    connection.line.setStartY(startY);
                    connection.line.setEndX(endX);
                    connection.line.setEndY(endY);
                }

                // Update the arrowhead position
                connection.updateArrowHead();
            }
        }
    }


    private Shape getConnectedNode(Arrow connection, Shape node) {
        Pane canvas = (Pane) connection.getParent();
        for (var child : canvas.getChildren()) {
            if (child instanceof Shape && child != node) {
                Shape connectedNode = (Shape) child;
                if (connection.line != null) {
                    if (distance(connectedNode.getLayoutX(), connectedNode.getLayoutY(),
                            connection.line.getStartX(), connection.line.getStartY()) < ((connectedNode instanceof Circle) ? ((Circle) connectedNode).getRadius() : ((Rectangle) connectedNode).getWidth() / 2) ||
                            distance(connectedNode.getLayoutX(), connectedNode.getLayoutY(),
                                    connection.line.getEndX(), connection.line.getEndY()) < ((connectedNode instanceof Circle) ? ((Circle) connectedNode).getRadius() : ((Rectangle) connectedNode).getWidth() / 2)) {
                        return connectedNode;
                    }
                }
            }
        }
        return null;
    }


    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void deleteNode(Pane canvas) {
        if (currentSelectedNode != null) {
            List<Arrow> linesToRemove = new ArrayList<>();
            for (Arrow connection : connections) {
                if (connection.line != null) {
                    if ((connection.line.getStartX() == currentSelectedNode.getLayoutX() && connection.line.getStartY() == currentSelectedNode.getLayoutY()) ||
                            (connection.line.getEndX() == currentSelectedNode.getLayoutX() && connection.line.getEndY() == currentSelectedNode.getLayoutY())) {
                        linesToRemove.add(connection);
                        lineColors.remove(connection);
                    }
                }
            }
            canvas.getChildren().removeAll(linesToRemove);
            connections.removeAll(linesToRemove);

            Text nodeText = (Text) currentSelectedNode.getUserData();
            canvas.getChildren().removeAll(currentSelectedNode, nodeText);
            currentSelectedNode = null;
        }
    }


    private void deleteLine(Pane canvas) {
        if (selectedLine != null) {
            canvas.getChildren().removeAll(selectedLine, selectedLine.getArrowHead());
            connections.remove(selectedLine);
            lineColors.remove(selectedLine);
            selectedLine = null;
        }
    }

    private void centerTextInNode(Shape node, Text text) {
        if (node instanceof Circle) {
            text.setX(node.getLayoutX() - text.getBoundsInLocal().getWidth() / 2);
            text.setY(node.getLayoutY() + text.getBoundsInLocal().getHeight() / 4);
        } else if (node instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) node;
            text.setX(rectangle.getLayoutX() + rectangle.getWidth() / 2 - text.getBoundsInLocal().getWidth() / 2);
            text.setY(rectangle.getLayoutY() + rectangle.getHeight() / 2 + text.getBoundsInLocal().getHeight() / 4);
        } else if (node instanceof Polygon) {
            Polygon polygon = (Polygon) node;
            if (polygon.getPoints().size() == 6 * 2) { // Hexagon
                text.setX(polygon.getLayoutX() - text.getBoundsInLocal().getWidth() / 2);
                text.setY(polygon.getLayoutY() + text.getBoundsInLocal().getHeight() / 4);
            } else if (polygon.getPoints().size() == 3 * 2) { // Triangle
                text.setX(polygon.getLayoutX() - text.getBoundsInLocal().getWidth() / 2);
                text.setY(polygon.getLayoutY() + text.getBoundsInLocal().getHeight() / 4);
            }
        } else if (node instanceof javafx.scene.shape.Ellipse) {
            javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) node;
            text.setX(ellipse.getLayoutX() - text.getBoundsInLocal().getWidth() / 2);
            text.setY(ellipse.getLayoutY() + text.getBoundsInLocal().getHeight() / 4);
        }
    }

    private Point2D calculateClosestPoint(Shape shape, double targetX, double targetY) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            double angle = Math.atan2(targetY - circle.getLayoutY(), targetX - circle.getLayoutX());
            double x = circle.getLayoutX() + circle.getRadius() * Math.cos(angle);
            double y = circle.getLayoutY() + circle.getRadius() * Math.sin(angle);
            return new Point2D(x, y);
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            Bounds bounds = rect.getBoundsInParent();
            return calculateClosestPointFromBounds(bounds, targetX, targetY, 0); // Pass padding as 0 for now
        } else if (shape instanceof javafx.scene.shape.Ellipse) {
            javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) shape;
            double angle = Math.atan2(targetY - ellipse.getLayoutY(), targetX - ellipse.getLayoutX());
            double x = ellipse.getLayoutX() + ellipse.getRadiusX() * Math.cos(angle);
            double y = ellipse.getLayoutY() + ellipse.getRadiusY() * Math.sin(angle);
            return new Point2D(x, y);
        } else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            double[] closestPoint = getClosestPolygonPoint(polygon, targetX, targetY);
            return new Point2D(closestPoint[0] + polygon.getLayoutX(), closestPoint[1] + polygon.getLayoutY());
        }
        return new Point2D(shape.getLayoutX(), shape.getLayoutY());
    }

    private Point2D calculateClosestPointFromBounds(Bounds bounds, double targetX, double targetY, double padding) {
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;

        // Determine the side (top, bottom, left, or right) where the closest point lies
        double dx = targetX - centerX;
        double dy = targetY - centerY;

        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);

        double closestX, closestY;

        if (absDx > absDy) {
            // Closest to left or right
            closestX = (dx > 0) ? bounds.getMaxX() - padding : bounds.getMinX() + padding;
            closestY = centerY + dy * (bounds.getWidth() / 2) / absDx; // Proportionally adjust
        } else {
            // Closest to top or bottom
            closestY = (dy > 0) ? bounds.getMaxY() - padding : bounds.getMinY() + padding;
            closestX = centerX + dx * (bounds.getHeight() / 2) / absDy; // Proportionally adjust
        }

        return new Point2D(closestX, closestY);
    }




    private void connectNodeToText(Shape node, Text text, Pane canvas) {
        // Calculate the closest points on the node and text
        Bounds textBounds = text.getBoundsInParent();
        Point2D start = calculateClosestPoint(node, textBounds.getCenterX(), textBounds.getCenterY());
        Point2D end = calculateClosestPointFromBounds(textBounds, start.getX(), start.getY(), 1); // Use small padding

        String lineType = lineTypeSelector.getValue(); // Get selected line type (e.g., Straight Line, Arrow Line, Bezier Curve)

        if ("Straight Line".equals(lineType)) {
            // Create a straight line
            Line connection = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            setupLine(connection, canvas);
        } else if ("Arrow Line".equals(lineType)) {
            // Create an arrow line
            Line arrowLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            Polygon arrowHead = createArrowHead(start, end); // Create an arrowhead at the end point
            setupLine(arrowLine, canvas);
            canvas.getChildren().add(arrowHead); // Add arrowhead to canvas
        } else if ("Bezier Curve".equals(lineType)) {
            // Create a Bezier curve
            CubicCurve bezierCurve = createBezierCurve(start, end);

            // Add the curve to the canvas
            setupCurve(bezierCurve, canvas);

            // Create and add an arrowhead for the Bezier curve
            Polygon bezierArrowHead = createBezierArrowHead(bezierCurve);
            canvas.getChildren().add(bezierArrowHead);
        }
    }


    private Polygon createBezierArrowHead(CubicCurve curve) {
        // Calculate the tangent at the endpoint of the curve
        double t = 1.0; // Endpoint of the curve
        double dx = derivativeX(curve, t);
        double dy = derivativeY(curve, t);
        double angle = Math.atan2(dy, dx);

        // Define the size of the arrowhead
        double arrowLength = 10;
        double arrowWidth = 5;

        // Calculate arrowhead points
        double x1 = curve.getEndX() - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = curve.getEndY() - arrowLength * Math.sin(angle - Math.PI / 6);

        double x2 = curve.getEndX() - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = curve.getEndY() - arrowLength * Math.sin(angle + Math.PI / 6);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                curve.getEndX(), curve.getEndY(), // Tip of the arrow
                x1, y1,                          // Bottom corner
                x2, y2                           // Top corner
        );
        arrowHead.setFill(Color.BLACK); // Set arrowhead color
        return arrowHead;
    }

    private double derivativeX(CubicCurve curve, double t) {
        return 3 * (1 - t) * (1 - t) * (curve.getControlX1() - curve.getStartX())
                + 6 * (1 - t) * t * (curve.getControlX2() - curve.getControlX1())
                + 3 * t * t * (curve.getEndX() - curve.getControlX2());
    }

    private double derivativeY(CubicCurve curve, double t) {
        return 3 * (1 - t) * (1 - t) * (curve.getControlY1() - curve.getStartY())
                + 6 * (1 - t) * t * (curve.getControlY2() - curve.getControlY1())
                + 3 * t * t * (curve.getEndY() - curve.getControlY2());
    }




    private Polygon createArrowHead(Point2D start, Point2D end) {
        double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());

        // Define the size of the arrowhead
        double arrowLength = 10;
        double arrowWidth = 5;

        // Calculate arrowhead points
        double x1 = end.getX() - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = end.getY() - arrowLength * Math.sin(angle - Math.PI / 6);

        double x2 = end.getX() - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = end.getY() - arrowLength * Math.sin(angle + Math.PI / 6);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                end.getX(), end.getY(), // Tip of the arrow
                x1, y1,                // Bottom corner
                x2, y2                 // Top corner
        );
        arrowHead.setFill(Color.BLACK); // Set arrowhead color
        return arrowHead;
    }

    private CubicCurve createBezierCurve(Point2D start, Point2D end) {
        // Calculate control points dynamically for a smooth curve
        double controlX1 = start.getX() + (end.getX() - start.getX()) / 3;
        double controlY1 = start.getY() - 50; // Pull upward for curvature (adjust as needed)
        double controlX2 = start.getX() + 2 * (end.getX() - start.getX()) / 3;
        double controlY2 = end.getY() + 50; // Pull downward for curvature (adjust as needed)

        // Create the cubic curve
        CubicCurve bezierCurve = new CubicCurve();
        bezierCurve.setStartX(start.getX());
        bezierCurve.setStartY(start.getY());
        bezierCurve.setControlX1(controlX1);
        bezierCurve.setControlY1(controlY1);
        bezierCurve.setControlX2(controlX2);
        bezierCurve.setControlY2(controlY2);
        bezierCurve.setEndX(end.getX());
        bezierCurve.setEndY(end.getY());
        bezierCurve.setStroke(Color.BLACK);
        bezierCurve.setStrokeWidth(2);
        bezierCurve.setFill(null); // Transparent fill
        return bezierCurve;
    }


    private void setupLine(Line line, Pane canvas) {
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setOnMouseClicked(event -> {
            event.consume();
            deselectAllLines();
            selectedTextLine = line;
            line.setStroke(Color.GOLD); // Highlight the selected line
        });
        textConnections.add(line);
        canvas.getChildren().add(line);
    }

    private void setupCurve(CubicCurve curve, Pane canvas) {
        curve.setOnMouseClicked(event -> {
            event.consume();
            deselectAllLines(); // Deselect other lines
            selectedTextLine = null; // Clear any selected text lines
            curve.setStroke(Color.GOLD); // Highlight the selected curve
        });
        textConnections.add(curve);
        canvas.getChildren().add(curve); // Add the curve to the canvas
    }




    public static void main(String[] args) {
        launch(args);
    }


    private static class Arrow extends Group {
        private final Line line;
        private final CubicCurve cubicCurve;
        private final Polygon arrowHead;

        // Constructor for Arrow with a Line
        public Arrow(Line line) {
            this.line = line;
            this.cubicCurve = null;

            this.arrowHead = createArrowHead(line.getStroke());
            getChildren().addAll(line, arrowHead);
            updateArrowHead();
        }

        // Constructor for Arrow with start and end points
        public Arrow(double startX, double startY, double endX, double endY) {
            this.line = new Line(startX, startY, endX, endY);
            this.cubicCurve = null;

            this.arrowHead = createArrowHead(line.getStroke());
            getChildren().addAll(line, arrowHead);
            updateArrowHead();
        }

        // Constructor for Arrow with a CubicCurve
        public Arrow(CubicCurve cubicCurve) {
            this.cubicCurve = cubicCurve;
            this.line = null;

            this.arrowHead = createArrowHead(cubicCurve.getStroke());
            getChildren().addAll(cubicCurve, arrowHead);
            updateArrowHead();
        }

        // Helper method to create the arrowhead polygon
        private Polygon createArrowHead(Paint strokeColor) {
            Polygon arrowHead = new Polygon();
            arrowHead.getPoints().addAll(
                    0.0, 0.0,  // Tip of the arrow
                    -10.0, -5.0, // Bottom left corner
                    -10.0, 5.0   // Top left corner
            );
            arrowHead.setFill(strokeColor);
            return arrowHead;
        }

        // Method to update the arrowhead's position and rotation
        public void updateArrowHead() {
            double angle;
            double x, y;

            if (cubicCurve != null) {
                // Calculate tangent at the end of the cubic curve
                double dx = cubicCurve.getEndX() - cubicCurve.getControlX2();
                double dy = cubicCurve.getEndY() - cubicCurve.getControlY2();
                angle = Math.atan2(dy, dx);
                x = cubicCurve.getEndX();
                y = cubicCurve.getEndY();
            } else if (line != null) {
                // Calculate tangent for a straight line
                angle = Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX());
                x = line.getEndX();
                y = line.getEndY();
            } else {
                return; // No connection to update
            }

            // Update the arrowhead's position and rotation
            arrowHead.setLayoutX(x);
            arrowHead.setLayoutY(y);
            arrowHead.getTransforms().clear();
            arrowHead.getTransforms().add(javafx.scene.transform.Transform.rotate(Math.toDegrees(angle), 0, 0));
        }

        // Getter for arrowHead
        public Polygon getArrowHead() {
            return arrowHead;
        }
    }

    private void showAdminPage(Stage primaryStage) {
        // Create a table to display users and their mind maps
        TableView<String[]> userTable = new TableView<>();
        TableColumn<String[], String> usernameColumn = new TableColumn<>("Username");
        TableColumn<String[], String> mindMapColumn = new TableColumn<>("Mind Map Name");
        TableColumn<String[], Void> actionColumn = new TableColumn<>("Action");

        // Populate columns
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        mindMapColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    String[] rowData = getTableView().getItems().get(getIndex());
                    String username = rowData[0];
                    String mindMapName = rowData[1];
                    deleteMindMap(username, mindMapName, userTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        userTable.getColumns().addAll(usernameColumn, mindMapColumn, actionColumn);

        // Fetch data from the database and populate the table
        userTable.getItems().addAll(DatabaseUtils.getAllUserMindMaps());

        // Back button to return to the dashboard
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(event -> showDashboardPage(primaryStage, "admin"));

        // Layout for the admin panel
        VBox adminLayout = new VBox(20, userTable, backButton);
        adminLayout.setAlignment(Pos.CENTER);
        adminLayout.setPadding(new Insets(20));
        Scene adminScene = new Scene(adminLayout, 900, 600);

        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Panel - Manage User Data");
    }

    private void deleteMindMap(String username, String mindMapName, TableView<String[]> userTable) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Mind Map");
        confirmation.setHeaderText("Are you sure you want to delete this mind map?");
        confirmation.setContentText("Username: " + username + "\nMind Map Name: " + mindMapName);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = DatabaseUtils.deleteMindMap(username, mindMapName);
            if (success) {
                showInfoAlert("Success", "Mind Map deleted successfully!");
                userTable.getItems().removeIf(item -> item[0].equals(username) && item[1].equals(mindMapName));
            } else {
                showInfoAlert("Error", "Failed to delete the mind map.");
            }
        }
    }


}