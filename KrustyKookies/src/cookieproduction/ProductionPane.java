package cookieproduction;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * The GUI pane where a user books tickets for movie performances. It contains
 * one list of movies and one of performance dates. The user selects a
 * performance by clicking in these lists. The performance data is shown in the
 * fields in the right panel. The bottom panel contains a button which the user
 * can click to book a ticket to the selected performance.
 */
public class ProductionPane extends BasicPane {
	private static final long serialVersionUID = 1;
	/**
	 * A label showing the name of the current user.
	 */
	private JLabel currentInProductionLabel;
	private JLabel currentSelectedIngredientLabel;

	/**
	 * The list model for the movie name list.
	 */
	private DefaultListModel nameListModel;

	/**
	 * The product name list.
	 */
	private JList productNameList;

	/**
	 * The list model for the products ingredients list.
	 */
	private DefaultListModel ingredientsListModel;

	/**
	 * The performance date list.
	 */
	private JList ingredientList;

	/**
	 * The text fields where the movie data is shown.
	 */
	private JTextField[] fieldsTop;
	private JTextField[] fieldsBottom;

	/**
	 * The number of the 'pallets in frezer' field.
	 */
	private static final int PALLETS_IN_FREEZER = 0;

	/**
	 * The number of the 'pallets ordered' field.
	 */
	private static final int PALLETS_ORDERED = 1;
	
	/**
	 * The number of the 'pallets ordered' field.
	 */
	private static final int INGREDIENT_NEEDED = 0;
	
	/**
	 * The number of the 'pallets ordered' field.
	 */
	private static final int INGREDIENT_AVAILABLE = 1;

	/**
	 * The total number of fields.
	 */
	private static final int NBR_FIELDS = 2;
	
	private static final int PALLET_CONSTANT = 10 * 15 * 36 / 100;
	
	private Cookie cookie;

	/**
	 * Create the booking pane.
	 * 
	 * @param db
	 *            The database object.
	 */
	public ProductionPane(Database db) {
		super(db);
		cookie = null;
	}

	/**
	 * Create the left panel, containing the movie name list and the performance
	 * date list.
	 * 
	 * @return The left panel.
	 */
	public JComponent createLeftPanel() {
		nameListModel = new DefaultListModel();

		productNameList = new JList(nameListModel);
		productNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productNameList.setPrototypeCellValue("123456789012");
		productNameList.addListSelectionListener(new NameSelectionListener());
		JScrollPane p1 = new JScrollPane(productNameList);

		ingredientsListModel = new DefaultListModel();

		ingredientList = new JList(ingredientsListModel);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ingredientList.setPrototypeCellValue("Roasted, chopped nuts");
		ingredientList.addListSelectionListener(new IngredientSelectionListener());
		JScrollPane p2 = new JScrollPane(ingredientList);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));
		p.add(p1);
		p.add(p2);
		return p;
	}

	/**
	 * Create the top panel, containing the fields with the performance data.
	 * 
	 * @return The top panel.
	 */
	public JComponent createTopPanel() {
		JPanel p = new JPanel();
		
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1.add(new JLabel("Last produced cookie: "));
		currentInProductionLabel = new JLabel("");
		p1.add(currentInProductionLabel);
		
		String[] texts = new String[NBR_FIELDS];
		
		texts[PALLETS_IN_FREEZER] = "Pallets in freezer";
		texts[PALLETS_ORDERED] = "Pallets on order";
		fieldsTop = new JTextField[NBR_FIELDS];
		for (int i = 0; i < fieldsTop.length; i++) {
			fieldsTop[i] = new JTextField(7);
			fieldsTop[i].setEditable(false);
		}

		JPanel inputTop = new InputPanel(texts, fieldsTop);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		p2.add(new JLabel("Selected ingredient: "));
		currentSelectedIngredientLabel = new JLabel("");
		p2.add(currentSelectedIngredientLabel);
		
		texts[INGREDIENT_NEEDED] = "Amount needed";
		texts[INGREDIENT_AVAILABLE] = "Amount available";
		fieldsBottom = new JTextField[NBR_FIELDS];
		for (int i = 0; i < fieldsBottom.length; i++) {
			fieldsBottom[i] = new JTextField(7);
			fieldsBottom[i].setEditable(false);
		}

		JPanel inputBottom = new InputPanel(texts, fieldsBottom);

		
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(p1);
		p.add(inputTop);
		p.add(p2);
		p.add(inputBottom);
		
		return p;
	}

	/**
	 * Create the bottom panel, containing the book ticket-button and the
	 * message line.
	 * 
	 * @return The bottom panel.
	 */
	public JComponent createBottomPanel() {
		JButton[] buttons = new JButton[1];
		buttons[0] = new JButton("Produce a pallet");
		return new ButtonAndMessagePanel(buttons, messageLabel,
				new ActionHandler());
	}

	/**
	 * Perform the entry actions of this pane: clear all fields, fetch the movie
	 * names from the database and display them in the name list.
	 */
	public void entryActions() {
		clearMessage();
		ingredientsListModel.removeAllElements();
		InProductionNow.instance().produceCookie(db.lastProduced());
		currentInProductionLabel.setText(InProductionNow.instance().getCurrentCookieName());
		currentSelectedIngredientLabel.setText("<None>");
		fillNameList();
		clearFields();
	}

	/**
	 * Fetch movie names from the database and display them in the name list.
	 */
	private void fillNameList() {
		nameListModel.removeAllElements();
		for (String s : db.getCookies()) {
			nameListModel.addElement(s);
		}
	}

	/**
	 * Fetch performance dates from the database and display them in the date
	 * list.
	 */
	private void fillIngredientList(ArrayList<String> ingredients) {
		ingredientsListModel.removeAllElements();
		currentSelectedIngredientLabel.setText("<None>");
		for (String s : ingredients) {
			ingredientsListModel.addElement(s);
		}
	}

	/**
	 * Clear all text fields.
	 */
	private void clearFields() {
		for (int i = 0; i < fieldsTop.length; i++) {
			fieldsTop[i].setText("");
			fieldsBottom[i].setText("");
		}
	}

	/**
	 * A class that listens for clicks in the name list.
	 */
	class NameSelectionListener implements ListSelectionListener {
		/**
		 * Called when the user selects a cookie in the cookie list. Fetches
		 * ingredients from the database and displays them in the ingredients
		 * list.
		 * 
		 * @param e
		 *            The selected list item.
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (productNameList.isSelectionEmpty()) {
				return;
			}
			clearFields();
			String cookieName = (String) productNameList.getSelectedValue();
			fieldsTop[PALLETS_IN_FREEZER].setText(db.palletsInFreezer(cookieName));
			fieldsTop[PALLETS_ORDERED].setText(db.palletsOnOrder(cookieName));
			cookie = new Cookie(cookieName, db.getIngredients(cookieName));
			fillIngredientList(cookie.madeOf());
		}
	}

	/**
	 * A class that listens for clicks in the ingredients list.
	 */
	class IngredientSelectionListener implements ListSelectionListener {
		/**
		 * Called when the user selects a name in the date list. Fetches
		 * performance data from the database and displays it in the text
		 * fields.
		 * 
		 * @param e
		 *            The selected list item.
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (productNameList.isSelectionEmpty() || ingredientList.isSelectionEmpty()) {
				return;
			}
			String ingredientName = (String) ingredientList.getSelectedValue();
			currentSelectedIngredientLabel.setText(ingredientName);
			String unit = "g";
			if (ingredientName.equals("Egg whites")) unit = "ml";
			
			int storedAmount = db.getStoredAmount(ingredientName);
			int needed = cookie.getIngredients().get(ingredientName)*PALLET_CONSTANT;
			
			fieldsBottom[INGREDIENT_NEEDED].setText(needed + " " + unit);
			fieldsBottom[INGREDIENT_AVAILABLE].setText(storedAmount + " " + unit);
		}
	}

	/**
	 * A class that listens for button clicks.
	 */
	class ActionHandler implements ActionListener {
		/**
		 * Called when the user clicks the Book ticket button. Books a ticket
		 * for the current user to the selected performance (adds a booking to
		 * the database).
		 * 
		 * @param e
		 *            The event object (not used).
		 */
		public void actionPerformed(ActionEvent e) {
			if (productNameList.isSelectionEmpty()) {
				return;
			}
//			if (InProductionNow.instance().inProduction()) {
//				displayMessage("Must wait for other cookies in production");
//				return;
//			}
			if (!quantityCheck()) {
				displayMessage("Not enough of one or more ingredients");
				return;
			}
			
			int palletId = db.producePallet(36, cookie);
			displayMessage("Pallet " + Integer.toString(palletId) + " produced!");
			
			clearFields();
			
			fieldsTop[PALLETS_IN_FREEZER].setText(db.palletsInFreezer(cookie.getCookieName()));
			fieldsTop[PALLETS_ORDERED].setText(db.palletsOnOrder(cookie.getCookieName()));
			
			String ingredientName = currentSelectedIngredientLabel.getText();
			
			if (!ingredientName.equals("<None>")) {
				
				String unit = "g";
				if (ingredientName.equals("Egg whites")) unit = "ml";
				
				int storedAmount = db.getStoredAmount(ingredientName);
				int needed = cookie.getIngredients().get(ingredientName)*PALLET_CONSTANT;
				
				fieldsBottom[INGREDIENT_NEEDED].setText(needed + " " + unit);
				fieldsBottom[INGREDIENT_AVAILABLE].setText(storedAmount + " " + unit);
			}
			
			InProductionNow.instance().produceCookie(db.lastProduced());
			currentInProductionLabel.setText(InProductionNow.instance().getCurrentCookieName());
		}
		
		private boolean quantityCheck() {
			int amountStored = 0;
			for (Map.Entry<String, Integer> ingredient : cookie.getIngredients().entrySet()) {
				amountStored = db.getStoredAmount(ingredient.getKey());
				if (amountStored < (PALLET_CONSTANT * ingredient.getValue())) {
					return false;
				}
			}
			return true;
		}
	}
}
