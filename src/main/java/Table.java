import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;
import view.FTRcardsView;
import view.PlayerView;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class Table extends Application {
    private static final int Height = 750;
    private static final int Width = 1050;

    private SimpleBot simpleBot = new SimpleBot();
    private MediumBot mediumBot = new MediumBot();
    private int lastChips;
    private int bank = 0;
    private int circleBank = 0;
    private int gameRound;
    private Player bot[] = new Player[6];

    private Label bankLable = new Label();
    private Label circleBankLable = new Label();
    private Pane root = new Pane();
    private Group group = new Group();
    private Deck deck = new Deck();
    private FTRcardsView ftr = new FTRcardsView();
    private PlayerView botView[] = new PlayerView[6];
    private Button resetButton = new Button("Restart");
    private Button foldButton = new Button();
    private Button checkButton = new Button();
    private Button callButton = new Button();
    private Button raiseButton = new Button();
    private Button betButton = new Button();
    private Button startButton = new Button("Start");
    private HBox hBox = new HBox();
    private HBox controlButtons = new HBox(5);
    private Spinner<Integer> betField = new Spinner<>();
    private TextArea gameChat = new TextArea();
    private Label forWinner = new Label();

    private Parent createContent() {
        root.setPrefSize(Width, Height);
        ImageView background = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("table/table_background.png")));
        background.setFitWidth(Width);
        background.setFitHeight(Height);

        controlButtons.getChildren().addAll(startButton, resetButton);
        controlButtons.setTranslateY(10);
        controlButtons.setTranslateX(10);
        hBox.setTranslateY(610);
        hBox.setTranslateX(20);
        group.getChildren().addAll(controlButtons, hBox);
        //temp buttons

        //Winner output
        forWinner.setTranslateX(400);
        forWinner.setTranslateY(380);
        forWinner.setTextFill(Paint.valueOf("Red"));
        forWinner.setFont(Font.font(20));
        group.getChildren().addAll(forWinner);
        //Winner output

        //Test
        BackgroundImage backgroundImageCall = new BackgroundImage(new Image
                (getClass().getResourceAsStream("/table/button_call.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background_buttonCall = new Background(backgroundImageCall);
        callButton.setBackground(background_buttonCall);
        callButton.setPrefSize(120, 50);

        BackgroundImage backgroundImageRaise = new BackgroundImage(new Image
                (getClass().getResourceAsStream("/table/button_raise.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background_buttonRaise = new Background(backgroundImageRaise);
        raiseButton.setBackground(background_buttonRaise);
        raiseButton.setPrefSize(120, 50);


        BackgroundImage backgroundImageFold = new BackgroundImage(new Image
                (getClass().getResourceAsStream("/table/button_fold.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background_buttonFold = new Background(backgroundImageFold);
        foldButton.setBackground(background_buttonFold);
        foldButton.setPrefSize(120, 50);


        BackgroundImage backgroundImageCheck = new BackgroundImage(new Image
                (getClass().getResourceAsStream("/table/button_check.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background_buttonCheck = new Background(backgroundImageCheck);
        checkButton.setBackground(background_buttonCheck);
        checkButton.setPrefSize(120, 48);


        BackgroundImage backgroundImageBet = new BackgroundImage(new Image
                (getClass().getResourceAsStream("/table/button_bet.png")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background_buttonBet = new Background(backgroundImageBet);
        betButton.setBackground(background_buttonBet);
        betButton.setPrefSize(120, 50);
        betField.setPrefSize(100, 20);
        betField.setTranslateY(20);

        bankLable.setTranslateX(460);
        bankLable.setTranslateY(150);
        bankLable.setTextFill(Paint.valueOf("Green"));

        circleBankLable.setTranslateX(490);
        circleBankLable.setTranslateY(350);
        circleBankLable.setTextFill(Paint.valueOf("White"));

        //Test

        startButton();
        resetButton();

//        Create game chat
        gameChat.setTranslateX(720);
        gameChat.setTranslateY(530);
        gameChat.setPrefSize(285, 110);
        gameChat.setPadding(Insets.EMPTY);
        gameChat.setEditable(false);
        gameChat.setWrapText(true);

        group.getChildren().addAll(ftr.getRoot(), bankLable, circleBankLable, gameChat);

        root.getChildren().addAll(background, group);
        return root;
    }

    private void gameControl() {
        if (gameRound == 0) {
            tableCircle();
        }
        if (gameRound == 1) {
            getFlop();
            tableCircle();
        }
        if (gameRound == 2) {
            getTurn();
            tableCircle();
        }
        if (gameRound == 3) {
            getRiver();
            tableCircle();
        }
        if (gameRound == 4) {
            showCards();
            getWinner();
        }
    }

    private void showCards() {
        for (int i = 1; i < 6; i++) {
            if (!bot[i].inGame) {
                continue;
            }
            ArrayList<Card> cards;
            cards = bot[i].getHandCard();
            botView[i].clearCards();
            botView[i].setCards(cards.get(0), true);
            botView[i].setCards(cards.get(1), true);
        }
    }

    private void tableCircle() {
        lastChips = 0;
        circleBank = 0;
        clearChips();
        playerMove(false);
//        1 bot move
        makeMove(1);
//        2 bot move
        makeMove(2);
//        3 bot move
        makeMove(3);
//        4 bot move
        makeMove(4);
//        5 bot move
        makeMove(5);
        hBox.getChildren().clear();
        while (!ifAllChipsEqual()) {
            if (gameRound == -1)
                break;
            playerMove(true);
            if (ifAllChipsEqual())
                break;
            makeMove(1);
            if (ifAllChipsEqual())
                break;
            makeMove(2);
            if (ifAllChipsEqual())
                break;
            makeMove(3);
            if (ifAllChipsEqual())
                break;
            makeMove(4);
            if (ifAllChipsEqual())
                break;
            makeMove(5);
        }
        gameRound++;
    }

    private void clearChips() {
        for (int i = 0; i < 6; i++) {
            botView[i].clearChips();
            bot[i].clearChips();
        }
    }

    private boolean ifAllChipsEqual() {
        boolean tmp = true;
        for (int i = 0; i < 6; i++) {
            if (!bot[i].inGame || bot[i].allIn) {
                continue;
            }
            if (bot[i].getChips() != lastChips) {
                tmp = false;
                break;
            }
        }
        return tmp;
    }

    private void playerMove(boolean raiseVisible) {
        if (!bot[0].inGame)
            return;
        if (!raiseVisible) {
            foldButton();
            checkButton();
            betButton();
        } else {
            betField.getValueFactory().setValue(lastChips * 2);
            foldButton();
            callButton();
            raiseButton();
        }
        Platform.enterNestedEventLoop(root);
        hBox.getChildren().clear();
    }

    private void makeMove(int player, int chips) {

        if (lastChips > bot[player].getChips() && lastChips != 0) {
            bot[player].getStack(chips - bot[player].getChips());
        } else {
            bot[player].getStack(chips);
        }
        bot[player].setChips(chips);
        botView[player].setStack(bot[player].getStackValue());
        botView[player].setChips(chips);
        lastChips = chips;
        bank += chips;
        circleBank += chips;
        bankLable.setText("Bank: " + bank);
        circleBankLable.setText("" + circleBank);
    }

    private void makeMove(int player) {
        if (!bot[player].inGame) {
            return;
        }
        int move;
        if (player == 1){
            move = simpleBot.think(bot[player].getCards(),bot[player].getStackValue(),bank,lastChips);
        }
        else {
            move = mediumBot.think(bot[player].getCards(), bot[player].getStackValue(), bank, lastChips);
        }
        if (move == 0) {
            LocalTime localTime = LocalTime.now();
            System.out.println(Time.valueOf(localTime) + " Bot[" + player + "] Check");
            gameChat.appendText(Time.valueOf(localTime) + " Bot[" + player + "] Check\n");
            return;
        }
        if (move > 0) {
            LocalTime localTime = LocalTime.now();
            System.out.println(Time.valueOf(localTime) + " Bot[" + player + "] Set - " + move + " Chips");
            gameChat.appendText(Time.valueOf(localTime) + " Bot[" + player + "] Set - " + move + " Chips\n");
            makeMove(player, move);
        }
        if (move < 0) {
            LocalTime localTime = LocalTime.now();
            System.out.println(Time.valueOf(localTime) + " Bot [" + player + "] Fold!");
            gameChat.appendText(Time.valueOf(localTime) + "Bot [" + player + "] Fold!\n");
            botView[player].del();
            bot[player].del();
        }
    }

    private void startButton() {
        startButton.setOnAction((ActionEvent event) -> {
            deck.refill();
            createPlayers();
            gameRound = 0;
            startButton.setDisable(true);
            gameControl();
        });
    }

    private void resetButton() {
        resetButton.setOnAction(event -> {
            if (Platform.isNestedLoopRunning()) {
                Platform.exitNestedEventLoop(root, null);
            }
            for (int i = 0; i < 6; i++) {
                botView[i].getRoot().getChildren().clear();
            }
            gameRound = -1;
            hBox.getChildren().clear();
            ftr.clear();
            circleBankLable.setText("");
            bankLable.setText("");
            startButton.setDisable(false);
            forWinner.setText("");
            bank = 0;
        });
    }

    private void foldButton() {
        hBox.getChildren().addAll(foldButton);
//        Action
        foldButton.setOnAction(event -> {
            Platform.exitNestedEventLoop(root, null);
            botView[0].del();
            bot[0].inGame = false;
        });
    }

    private void checkButton() {
        hBox.getChildren().addAll(checkButton);
//        Action
        checkButton.setOnAction(event -> Platform.exitNestedEventLoop(root, null));
    }

    private void callButton() {
        hBox.getChildren().addAll(callButton);
//        Action
        callButton.setOnAction(event -> {
            makeMove(0, lastChips);
            Platform.exitNestedEventLoop(root, null);
        });
    }

    private void bet() {
        int tmp = betField.getValue();
        if (tmp > bot[0].getStackValue()) {
            tmp = bot[0].getStackValue();
        }
        if (lastChips != 0 && lastChips > bot[0].getChips()) {
            bot[0].getStack(tmp - bot[0].getChips());
        } else {
            bot[0].getStack(tmp);
        }
        bank += tmp;
        circleBank += tmp;
        lastChips = tmp;
        bot[0].setChips(tmp);
        botView[0].setChips(tmp);
        botView[0].setStack(bot[0].getStackValue());
        Platform.exitNestedEventLoop(root, null);
    }

    private void betButton() {
        hBox.getChildren().addAll(betButton, betField);
//        Action
        betButton.setOnAction(event -> bet());
    }

    private void raiseButton() {
        hBox.getChildren().addAll(raiseButton, betField);
//        Action
        raiseButton.setOnAction(event -> bet());
    }

    private void getWinner() {
        double max = 0;
        int t = 0;
        double tmp;
        for (int i = 0; i < 6; i++) {
            if (!bot[i].inGame)
                continue;
            tmp = bot[i].getPower();
            if (tmp > max) {
                max = tmp;
                t = i;
            }
        }
        int isDraw = 0;
        for (int i = 0; i < 6; i++) {
            if (bot[i].inGame && bot[i].getPower() == max) {
                isDraw++;
                bot[i].isWinner = true;
            }
        }
        if (isDraw > 1) {
            for (int i = 0; i < 6; i++) {
                if (bot[i].isWinner) {
                    System.out.println("Bot [" + i + "] Win this game with " + bot[i].getCombinationName());
                    LocalTime localTime = LocalTime.now();
                    gameChat.appendText(Time.valueOf(localTime) + "Bot[" + i + "] Win this game with " + bot[i].getCombinationName() + "!!!!!!!\n");
                    forWinner.setText("Draw!!!!");
                }
            }
        } else {
            bot[t].isWinner = true;
            for (int i = 0; i < 6; i++) {
                System.out.println("Bot [" + i + "]" + bot[i].getPower());
                System.out.println("Bot [" + i + "]" + bot[i].isWinner);
            }
            for (int i = 0; i < 6; i++) {
                if (bot[i].isWinner) {
                    System.out.println("Bot [" + i + "] Win this game with " + bot[i].getCombinationName());
                    LocalTime localTime = LocalTime.now();
                    gameChat.appendText(Time.valueOf(localTime) + "Bot[" + i + "] Win this game with " + bot[i].getCombinationName() + "!!!!!!!\n");
                    forWinner.setText("Bot[" + i + "] Win this game!!!!!!!");
                }
            }
        }
    }

    private void createPlayers() {
        Group bots = new Group();
        for (int i = 0; i < 6; i++) {
            bot[i] = new Player();
            botView[i] = new PlayerView();
            bots.getChildren().addAll(botView[i].getRoot());
            botView[i].setName("John");
            bot[i].addStack(1500);
            botView[i].setStack(bot[i].getStackValue());
            if (i != 0) {
                bot[i].setCards(deck.getCard());
                bot[i].setCards(deck.getCard());
                botView[i].setCards(bot[i].getCard(0), false);
                botView[i].setCards(bot[i].getCard(1), false);
            }
        }
        bot[0].setCards(deck.getCard());
        bot[0].setCards(deck.getCard());

        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, bot[0].getStackValue(), 0, 20);
        betField.setValueFactory(spinnerValueFactory);

        botView[0].setCards(bot[0].getCard(0), true);
        botView[0].setCards(bot[0].getCard(1), true);

        botView[0].setPosition(440, 430);
        botView[1].setPosition(150, 340);
        botView[2].setPosition(150, 70);
        botView[3].setPosition(440, 10);
        botView[4].setPosition(730, 70);
        botView[5].setPosition(730, 340);

        group.getChildren().addAll(bots);
    }

    private void getFlop() {
        deck.getCard();
        for (int i = 0; i < 3; i++) {
            Card card = deck.getCard();
            for (int j = 0; j < 6; j++) {
                bot[j].setCards(card);
            }
            ftr.setCard(card);
        }
    }

    private void getTurn() {
        deck.getCard();
        Card card = deck.getCard();
        for (int i = 0; i < 6; i++) {
            bot[i].setCards(card);
        }

        ftr.setCard(card);
    }

    private void getRiver() {
        deck.getCard();
        Card card = deck.getCard();
        for (int i = 0; i < 6; i++) {
            bot[i].setCards(card);
        }

        ftr.setCard(card);
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.setWidth(Width);
        primaryStage.setHeight(Height);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Poker");
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            if (Platform.isNestedLoopRunning())
                resetButton.fire();
            primaryStage.close();
            Main table = new Main();
            Stage stage = new Stage();
            try {
                table.start(stage);
                primaryStage.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}