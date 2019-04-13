//Anthony Ramnarain CS 313 Expression Tree GUI Project

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Pr25255 extends Application implements EventHandler<ActionEvent> {

    private TextField expressionText;
    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;
    private ExpressionNode node;
    private BorderPane border;
    private Label value;
    Map<Circle, ExpressionNode> nodes = new HashMap<>();

    private int mode = 0;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Expression Evaluator!");
        primaryStage.setScene(scene);
        primaryStage.show();

        border = new BorderPane();
        border.setTop(setUpTextFields());
        border.setCenter(setUpTreeDisplay());
        border.setBottom(setupRadioButtons());

        root.getChildren().add(border);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == expressionText) {
            String value = expressionText.getText();
            if (mode == 0) {
                node = InfixTree.getInfixTreeForExpression(value);
            } else if (mode == 1) {
                node = InfixTree.getPrefixTreeForExpression(value);
            } else {
                node = InfixTree.getPostfixTreeForExpression(value);
            }

            drawTree(node);
            putValue(node);
        } else if (event.getSource() == button1) {
            printInfixTree();
            mode = 0;
        } else if (event.getSource() == button2) {
            printPrefixTree();
            mode = 1;
        } else if (event.getSource() == button3) {
            printPostFixTree();
            mode = 2;
        }
    }

    private Node setUpTextFields() {
        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(10, 10, 10, 10));

        Label expressionLabel = new Label("Expression");
        expressionLabel.setPadding(new Insets(10, 10, 10, 10));
        expressionText = new TextField();
        grid.setLeft(expressionLabel);
        grid.setCenter(expressionText);

        expressionText.setOnAction(this);

        return grid;
    }

    private Node setupRadioButtons() {
        HBox grid = new HBox();

        ToggleGroup group = new ToggleGroup();
        button1 = new RadioButton("Infix Notation");
        button2 = new RadioButton("Prefix Notation");
        button3 = new RadioButton("Postfix Notation");
        button1.setToggleGroup(group);
        button2.setToggleGroup(group);
        button3.setToggleGroup(group);

        button1.setOnAction(this);
        button2.setOnAction(this);
        button3.setOnAction(this);

        Label label = new Label("Value: ");
        value = new Label("");
        value.setMaxWidth(100);

        grid.setSpacing(10);
        grid.setPadding(new Insets(10));
        grid.getChildren().addAll(button1, button2, button3, label, value);
        return grid;
    }

    private Node setUpTreeDisplay() {
        StackPane pane = new StackPane();
        return pane;
    }

    private void drawTree(ExpressionNode node) {
        nodes.clear();
        if (node == null) {
            border.setCenter(new StackPane());
            return;
        }

        int height = InfixTree.getHeight(node);
        int numRows = height + 1;
        ArrayList<ExpressionNode> nodeList = new ArrayList<>();
        InfixTree.getNodeList(node, nodeList);

        double rowHeight = (double) 500 / numRows;
        int columnWidth = 700;

        Group root = new Group();

        Circle c = new Circle(0, 0, 20);
        c.setStrokeWidth(2);
        c.setStroke(Color.TRANSPARENT);
        c.setFill(Color.TRANSPARENT);
        root.getChildren().add(c);

        c = new Circle(0, 460, 20);
        c.setStrokeWidth(2);
        c.setStroke(Color.TRANSPARENT);
        c.setFill(Color.TRANSPARENT);
        root.getChildren().add(c);

        c = new Circle(660, 460, 20);
        c.setStrokeWidth(2);
        c.setStroke(Color.TRANSPARENT);
        c.setFill(Color.TRANSPARENT);
        root.getChildren().add(c);

        c = new Circle(660, 0, 20);
        c.setStrokeWidth(2);
        c.setStroke(Color.TRANSPARENT);
        c.setFill(Color.TRANSPARENT);
        root.getChildren().add(c);

        for (int i = nodeList.size() - 1; i >= 0; i--) {
            ExpressionNode current = nodeList.get(i);
            int row = (int) Math.floor(Math.log(current.index + 1) / Math.log(2));
            double y = rowHeight * row;
            int maxIndexRow = (int) Math.pow(2, (row + 1)) - 1;
            int maxInRow = (int) Math.pow(2, row);
            double x = columnWidth * (double) (2 * ((maxInRow + current.index) - (maxIndexRow)) + 1) / (maxInRow * 2);

            System.out.println("X: " + x + " Y: " + y + " Value: " + current.value);

            if (row > 0) {
                int parentOffset = current.index % 2 == 0 ? 0 : 2;
                double parentX = columnWidth * (double) (2 * ((maxInRow + current.index) - (maxIndexRow)) + parentOffset) / (maxInRow * 2);
                double parentY = rowHeight * (row - 1);

                Line l = new Line(x, y, parentX, parentY);
                root.getChildren().add(l);
            }

            c = new Circle(x, y, 20);
            c.setStrokeWidth(2);
            c.setStroke(Color.BLACK);
            c.setFill(Color.YELLOW);
            root.getChildren().add(c);

            Label l = new Label(current.value);
            l.setTextFill(Color.RED);
            l.setFont(new Font(16));
            l.setAlignment(Pos.CENTER);
            l.setPrefSize(40, 40);
            l.setLayoutX(x - 20);
            l.setLayoutY(y - 20);
            root.getChildren().add(l);

            nodes.put(c, current);
            setMouseClick(l, c, current);
        }

        border.setCenter(root);
    }

    private void putValue(ExpressionNode node) {
        int valueNum = InfixTree.calculateValue(node);
        value.setText("" + valueNum);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void printInfixTree() {
        String string = "";
        string = getInfix(node, string);
        expressionText.setText(string.trim());
    }

    private void printPrefixTree() {
        String string = "";
        string = getPrefix(node, string);
        expressionText.setText(string.trim());
    }

    private void printPostFixTree() {
        String string = "";
        string = getPostFix(node, string);
        expressionText.setText(string.trim());
    }

    private String getInfix(ExpressionNode node, String string) {
        if (node == null) {
            return string;
        }

        string = getInfix(node.left, string);
        string = string + node.value;
        string = getInfix(node.right, string);
        return string;
    }

    private String getPrefix(ExpressionNode node, String string) {
        if (node == null) {
            return string;
        }

        string = string + " " + node.value;
        string = getPrefix(node.left, string);
        string = getPrefix(node.right, string);
        return string;
    }

    private String getPostFix(ExpressionNode node, String string) {
        if (node == null) {
            return string;
        }

        string = getPostFix(node.left, string);
        string = getPostFix(node.right, string);
        string = string + " " + node.value;
        return string;
    }

    private void setMouseClick(final Label l, final Circle c, final ExpressionNode current) {

        c.setOnMouseClicked((e) -> {
            for (Circle circle : nodes.keySet()) {
                circle.setFill(Color.YELLOW);
            }
            c.setFill(Color.GREEN);
            putValue(current);
        });

        l.setOnMouseClicked((e) -> {
            for (Circle circle : nodes.keySet()) {
                circle.setFill(Color.YELLOW);
            }
            c.setFill(Color.GREEN);
            putValue(current);
        });
    }
}

class InfixTree {

    public static ExpressionNode getInfixTreeForExpression(String expression) {
        ExpressionNode node;
        try {
            expression = expression.replaceAll("\\s", "");
            ArrayList<String> postFix = getPostFix(expression);
            node = addNode(postFix, postFix.size() - 1);
            if (node == null || node.index != 0) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        correctIndexes(node, 0);
        return node;
    }

    public static ExpressionNode getPrefixTreeForExpression(String expression) {
        ExpressionNode node = new ExpressionNode();
        getPrefixTree(node, expression);
        correctIndexes(node, 0);
        return node;
    }

    private static String getPrefixTree(ExpressionNode node, String expression) {
        String c = "" + expression.charAt(0);
        if (c.equals(" ")) {
            return getPrefixTree(node, expression.substring(1));
        }
        String value = "";
        int i = 1;
        if (isOperator(c)) {
            value = c;
        } else if (c.compareTo("0") >= 0 && c.compareTo("9") <= 0) {
            value = "";
            for (i = 0; i < expression.length() && expression.charAt(i) >= '0' && expression.charAt(i) <= '9'; i++) {
                value += expression.charAt(i);
            }
        }

        expression = expression.substring(i);
        node.value = value;
        if (isOperator(c)) {
            node.left = new ExpressionNode();
            node.right = new ExpressionNode();
            expression = getPrefixTree(node.left, expression);
            expression = getPrefixTree(node.right, expression);
        }
        return expression;
    }

    public static ExpressionNode getPostfixTreeForExpression(String expression) {
        ExpressionNode node = new ExpressionNode();
        getPostfixTree(node, expression);
        correctIndexes(node, 0);
        return node;
    }

    private static String getPostfixTree(ExpressionNode node, String expression) {
        String c = "" + expression.charAt(expression.length() -  1);
        if (c.equals(" ")) {
            return getPostfixTree(node, expression.substring(0, expression.length() -  1));
        }

        String value = "";
        int i = 1;
        if (isOperator(c)) {
            value = c;
        } else if (c.compareTo("0") >= 0 && c.compareTo("9") <= 0) {
            value = "";
            i = 0;
            int k = expression.length() -  (1 + i);
            for (i = 0; i < expression.length() && expression.charAt(k) >= '0' && expression.charAt(k) <= '9'; i++, k = expression.length() -  (1 + i)) {
                value = expression.charAt(k) + value;
            }
        }

        expression = expression.substring(0, expression.length() - i);
        node.value = value;
        if (isOperator(c)) {
            node.left = new ExpressionNode();
            node.right = new ExpressionNode();
            expression = getPostfixTree(node.right, expression);
            expression = getPostfixTree(node.left, expression);
        }
        return expression;
    }

    private static void correctIndexes(ExpressionNode n, int index) {
        if (n == null) {
            return;
        }

        n.index = index;
        correctIndexes(n.left, index * 2 + 1);
        correctIndexes(n.right, index * 2 + 2);
    }

    private static ExpressionNode addNode(ArrayList<String> postFix, int index) {
        if (index < 0) {
            throw new IllegalArgumentException();
        }

        ExpressionNode node = new ExpressionNode();
        node.value = postFix.get(index);
        if (isOperator(node.value)) {
            node.right = addNode(postFix, index - 1);
            index = node.right.index;
            node.left = addNode(postFix, index - 1);
            index = node.left.index;
        }
        node.index = index;
        return node;
    }

    private static ArrayList<String> getPostFix(String expression) {
        Stack<String> stack = new Stack<>();
        ArrayList<String> postFix = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            String c = "" + expression.charAt(i);
            if (c.equals("(")) {
                stack.push("" + c);
            } else if (c.equals(")")) {
                popTillLastBrace(stack, postFix);
            } else if (isOperator(c)) {
                appendOperators(c, stack, postFix);
                stack.push(c);
            } else if (c.compareTo("0") >= 0 && c.compareTo("9") <= 0) {
                String operand = "";
                for (; i < expression.length() && expression.charAt(i) >= '0' && expression.charAt(i) <= '9'; i++) {
                    operand += expression.charAt(i);
                }
                i--;
                postFix.add(operand);
            }
        }

        while (!stack.empty()) {
            postFix.add("" + stack.pop());
        }
        return postFix;
    }

    private static void popTillLastBrace(Stack<String> stack, ArrayList<String> str) {
        while (!stack.peek().equals("(")) {
            str.add("" + stack.pop());
        }
        stack.pop();
    }

    private static void appendOperators(String c, Stack<String> stack, ArrayList<String> str) {
        while (true) {
            if (stack.empty()) {
                break;
            }

            String stackTop = stack.peek();
            if (stackTop.equals("(") || getPrec(stackTop) < getPrec(c)) {
                break;
            }
            str.add(stackTop);
            stack.pop();
        }
    }

    private static int getPrec(String c) {
        switch (c) {
            case "-":
                return 1;
            case "+":
                return 1;
            case "*":
                return 2;
            case "/":
                return 2;
        }
        return 0;
    }

    private static boolean isOperator(String s) {
        switch (s) {
            case "+":
                return true;
            case "-":
                return true;
            case "*":
                return true;
            case "/":
                return true;
            default:
                return false;
        }
    }

    public static int getHeight(ExpressionNode node) {
        if (node == null) {
            return -1;
        }
        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    public static void getNodeList(ExpressionNode node, ArrayList<ExpressionNode> nodes) {
        if (node == null) {
            return;
        }
        nodes.add(node);
        getNodeList(node.left, nodes);
        getNodeList(node.right, nodes);
    }

    static int calculateValue(ExpressionNode node) {
        if (node == null) {
            return 0;
        }

        if (isOperator(node.value)) {
            int leftValue = calculateValue(node.left);
            int rightValue = calculateValue(node.right);
            if (node.value.equals("*")) {
                return leftValue * rightValue;
            }
            if (node.value.equals("+")) {
                return leftValue + rightValue;
            }
            if (node.value.equals("-")) {
                return leftValue - rightValue;
            }
            if (node.value.equals("/")) {
                return leftValue / rightValue;
            }
            throw new RuntimeException();
        } else {
            return Integer.parseInt(node.value);
        }
    }
}

class ExpressionNode {
    String value = "";
    ExpressionNode left = null;
    ExpressionNode right = null;
    int index = -1;
}
