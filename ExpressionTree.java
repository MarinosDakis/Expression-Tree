
import java.util.ArrayList;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//Marinos Dakis 

public class ET<T> extends Application {
	@Override
	public void start(Stage primaryStage) {

		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1000, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Layout
		HBox topBox = new HBox();
		HBox bottomBox = new HBox();
		bottomBox.setSpacing(50);

		// GUI for top
		TextField tfTop = new TextField();
		Label expressionLabel = new Label("Expression: ");

		// GUI for bottom
		TextField tfBottom = new TextField();
		Label valueLabel = new Label("Value: ");
		RadioButton infixBtn = new RadioButton("Infix Notation");
		RadioButton prefixBtn = new RadioButton("Prefix Notation");
		RadioButton postfixBtn = new RadioButton("Postfix Notation");

		// setting preferences
		ToggleGroup radioButtonGroup = new ToggleGroup();

		infixBtn.setToggleGroup(radioButtonGroup);
		infixBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		prefixBtn.setToggleGroup(radioButtonGroup);
		prefixBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		postfixBtn.setToggleGroup(radioButtonGroup);
		postfixBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		tfBottom.setEditable(false);

		valueLabel.setFont(Font.font("Arial", 18));

		expressionLabel.setFont(Font.font("Arial", 18));
		expressionLabel.setPadding(new Insets(1, 1, 1, 0));

		tfTop.setPrefWidth(900);

		// setting Layout of GUI
		topBox.getChildren().addAll(expressionLabel, tfTop);
		bottomBox.getChildren().addAll(infixBtn, prefixBtn, postfixBtn, valueLabel, tfBottom);
		root.setTop(topBox);
		root.setBottom(bottomBox);

		// setting radio button toggles
		radioButtonGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {

			// if previous toggle was infix, and user clicks postfix, change infix ->
			// postfix
			if (oldToggle == infixBtn && newToggle == postfixBtn) {
				String changetopostfix = tfTop.getText();
				changetopostfix = infixToPostFixExpression(changetopostfix);
				tfTop.setText(changetopostfix);
				BinaryTreeNode<String> changedpostFixRoot = postfixTree(postfixStringToArrayList(changetopostfix));
				displayTree(changedpostFixRoot, 500, 100, root, 1, tfBottom);

			}

			if (newToggle == infixBtn) {
				String infixstring = tfTop.getText();
				BinaryTreeNode<String> InFixRoot = infixToPostFixTree((infixstring));
				displayTree(InFixRoot, 500, 100, root, 1, tfBottom);

			}

			if (newToggle == prefixBtn) {
				String prefixstring = tfTop.getText();
				BinaryTreeNode<String> preFixRoot = prefixTree(prefixStringToArrayList(prefixstring));
				displayTree(preFixRoot, 500, 100, root, 1, tfBottom);

			}

			if (newToggle == postfixBtn) {
				String postfixstring = tfTop.getText();
				BinaryTreeNode<String> postFixRoot = postfixTree(postfixStringToArrayList(postfixstring));
				displayTree(postFixRoot, 500, 100, root, 1, tfBottom);

			}

		});

	}

	// display Tree method
	public void displayTree(BinaryTreeNode<String> currentNode, double x, double y, BorderPane pane, int count,
			TextField tf) {
		if (currentNode == null)
			return;

		// make circle
		final Circle circle = new Circle(x, y, 20, Color.ANTIQUEWHITE);
		circle.setStroke(Color.BLACK);

		circle.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				circle.setFill(Color.ALICEBLUE);
				tf.setText(Integer.toString(evaluate(currentNode)));
			}
		});

		// circle coords
		double a = circle.getCenterX();
		double b = circle.getCenterY();

		// make text
		String data = currentNode.getData();
		Text text = new Text(a - 7, b + 5, data);
		text.setFont(Font.font("Verdana", 20));

		// lines
		Line line1 = new Line();
		Line line2 = new Line();

		// get nodes & add lines
		if (count % 2 != 0) {
			displayTree(currentNode.getLeft(), x - 150, y + 50, pane, count + 1, tf);
			displayTree(currentNode.getRight(), x + 150, y + 50, pane, count + 1, tf);
			if (currentNode.getLeft() != null || currentNode.getRight() != null) {
				line1 = new Line(a, b, a - 130, b + 50);
				line2 = new Line(a, b, a + 130, b + 50);
			}
			pane.getChildren().addAll(line1, line2);
		}

		if (count % 2 == 0) {
			displayTree(currentNode.getLeft(), x - 50, y + 50, pane, count + 1, tf);
			displayTree(currentNode.getRight(), x + 50, y + 50, pane, count + 1, tf);
			if (currentNode.getLeft() != null || currentNode.getRight() != null) {
				line1 = new Line(a, b, a - 30, b + 50);
				line2 = new Line(a, b, a + 30, b + 50);
			}
			pane.getChildren().addAll(line1, line2);
		}

		// place on map
		pane.getChildren().addAll(circle, text);

	}

	// turns a String input into an arrayList to pass into prefixTree method
	public static ArrayList<Character> prefixStringToArrayList(String x) {
		ArrayList<Character> preList = new ArrayList<>();

		String check = x.replace(" ", "").replace("(", "").replace(")", "");

		for (int i = 0; i < check.length(); i++)
			preList.add(check.charAt(i));

		return preList;

	}

	// turns a String input into an arrayList to pass into postfixTree method
	public static ArrayList<Character> postfixStringToArrayList(String x) {
		ArrayList<Character> postList = new ArrayList<>();

		String check = x.replace(" ", "").replace("(", "").replace(")", "");

		for (int i = check.length() - 1; i >= 0; i--)
			postList.add(check.charAt(i));

		return postList;
	}

	// checks if a String is an integer using regular expression
	public static boolean isInteger(String x) {
		return x.matches("^\\d+$");
	}

	// checks if a String matches with the 4 operators (+-/*)
	public static boolean isOperator(String x) {
		return x.equals("+") || x.equals("-") || x.equals("*") || x.equals("/");
	}

	// turns an arraylist containing a prefix string, into a node with all nodes
	// attached
	public static BinaryTreeNode<String> prefixTree(ArrayList<Character> exp) {

		BinaryTreeNode<String> temp = null;
		String c = String.valueOf(exp.get(0));
		exp.remove(0);

		if (isInteger(c))
			return new BinaryTreeNode<>(c);

		else if (isOperator(c))
			temp = new BinaryTreeNode<>(c);

		temp.setLeft(prefixTree(exp));
		temp.setRight(prefixTree(exp));
		return temp;
	}

	// turns an arraylist containing a postfix string, into a node with all nodes
	// attached
	public static BinaryTreeNode<String> postfixTree(ArrayList<Character> exp) {
		BinaryTreeNode<String> temp = null;
		String c = String.valueOf(exp.get(0));
		exp.remove(0);

		if (isInteger(c))
			return new BinaryTreeNode<>(c);

		else if (isOperator(c))
			temp = new BinaryTreeNode<>(c);

		temp.setRight(postfixTree(exp));
		temp.setLeft(postfixTree(exp));

		return temp;
	}

	// checks the precedence of operators and returns them as digits
	public static int precedence(String toEvaluate) {
		if (toEvaluate.equals("*") || toEvaluate.equals("/"))
			return 1;
		if (toEvaluate.equals("+") || toEvaluate.equals("-"))
			return 2;
		return 0;
	}

	// Turns an infix string into a postfix notation
	// Using Shunting-yard algorithm
	// algorithm(https://en.wikipedia.org/wiki/Shunting-yard_algorithm)
	public static String infixToPostFixExpression(String exp) {
		Stack<String> operatorStack = new Stack<>();
		String postfix = "", temp = "";
		String nospaces = exp.replace(" ", "");
		String peek = null;
		// reading through expression
		for (int i = 0; i < nospaces.length(); i++) {
			String toEvaluate = Character.toString(nospaces.charAt(i));
			if (!operatorStack.isEmpty())
				peek = operatorStack.peek();

			// if element is number add to string <>
			if (isInteger(toEvaluate))
				postfix += toEvaluate;

			// if the element is an operator
			if (isOperator(toEvaluate)) {

				// while top has greater precedence
				// or top precedence == element precedence
				// and top != ")"
				while (precedence(peek) > precedence(toEvaluate)
						|| precedence(peek) > precedence(toEvaluate) && !peek.equals(")")) {
					temp = operatorStack.pop();
					postfix += temp;
					peek = operatorStack.peek();
				}
				// push it onto the operatorStack
				operatorStack.push(toEvaluate);
			}

			// if the element is "(") push onto stack
			if (toEvaluate.equals("("))
				operatorStack.push(toEvaluate);

			// if the element is ")"
			if (toEvaluate.equals(")")) {

				while (!peek.contentEquals("(")) {
					// pop operators from the operator stack onto the postFix string
					temp = operatorStack.pop();
					postfix += temp;
					peek = operatorStack.peek();
				}
				// if there is a "(" at the top of the operatorStack pop
				if (peek.equals("("))
					operatorStack.pop();
			}

		}

		// empty stack and add to String
		while (!operatorStack.isEmpty())
			postfix += operatorStack.pop();

		return postfix;
	}

	// Using Shunting-yard algorithm
	// (https://en.wikipedia.org/wiki/Shunting-yard_algorithm)
	public static BinaryTreeNode<String> infixToPostFixTree(String exp) {
		Stack<String> operatorStack = new Stack<>();
		String postfix = "", temp = "";
		String nospaces = exp.replace(" ", "");
		String peek = null;
		// reading through expression
		for (int i = 0; i < nospaces.length(); i++) {
			String toEvaluate = Character.toString(nospaces.charAt(i));
			if (!operatorStack.isEmpty())
				peek = operatorStack.peek();

			// if element is number add to string <>
			if (isInteger(toEvaluate))
				postfix += toEvaluate;

			// if the element is an operator
			if (isOperator(toEvaluate)) {

				// while top has greater precedence
				// or top precedence == element precedence
				// and top != ")"
				while (precedence(peek) > precedence(toEvaluate)
						|| precedence(peek) > precedence(toEvaluate) && !peek.equals(")")) {
					temp = operatorStack.pop();
					postfix += temp;
					peek = operatorStack.peek();
				}
				// push it onto the operatorStack
				operatorStack.push(toEvaluate);
			}

			// if the element is "(") push onto stack
			if (toEvaluate.equals("("))
				operatorStack.push(toEvaluate);

			// if the element is ")"
			if (toEvaluate.equals(")")) {

				while (!peek.contentEquals("(")) {
					// pop operators from the operator stack onto the postFix string
					temp = operatorStack.pop();
					postfix += temp;
					peek = operatorStack.peek();
				}
				// if there is a "(" at the top of the operatorStack
				if (peek.equals("("))
					operatorStack.pop();
			}

		}

		while (!operatorStack.isEmpty())
			postfix += operatorStack.pop();

		// building root node for infix, by transferring it to postfix first
		ArrayList<Character> InFixList = new ArrayList<>();
		for (int i = postfix.length() - 1; i >= 0; i--)
			InFixList.add(postfix.charAt(i));

		BinaryTreeNode<String> infixRoot = postfixTree(InFixList);

		return infixRoot;
	}

	// evaluate nodes
	public int evaluate(BinaryTreeNode<String> node) {

		// if leaf node return data
		if (node.getLeft() == null && node.getRight() == null)
			return Integer.parseInt(node.getData());

		else {
			int left = evaluate(node.getLeft());
			int right = evaluate(node.getRight());
			int value = 0;

			if (node.getData().equals("+"))
				value += (left + right);
			if (node.getData().equals("-"))
				value += (left - right);
			if (node.getData().equals("*"))
				value += (left * right);
			if (node.getData().equals("/"))
				value += (left / right);

			return value;
		}
	}

	static class BinaryTreeNode<T> {

		private BinaryTreeNode<T> parent, left, right;
		private T data;

		public BinaryTreeNode() {
			left = right = parent = null;
			data = null;
		}

		public BinaryTreeNode(T newdata) {
			data = newdata;
		}

		public BinaryTreeNode<T> getParent() {
			return parent;
		}

		public void setParent(BinaryTreeNode<T> newparent) {
			parent = newparent;
		}

		public BinaryTreeNode<T> getLeft() {
			return left;
		}

		public void setLeft(BinaryTreeNode<T> newleft) {
			left = newleft;
		}

		public BinaryTreeNode<T> getRight() {
			return right;
		}

		public void setRight(BinaryTreeNode<T> newright) {
			right = newright;
		}

		public T getData() {
			return data;
		}

		public void setData(T newdata) {
			data = newdata;
		}
	}

	public static void main(String[] args) {

		launch(args);
	}
}
