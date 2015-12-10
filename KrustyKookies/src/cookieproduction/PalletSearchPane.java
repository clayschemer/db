package cookieproduction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

/**
 * The GUI pane where a new user logs in. Contains a text field where the user
 * id is entered and a button to log in.
 */
public class PalletSearchPane extends BasicPane {
	private static final long serialVersionUID = 1;

	private JTextField palletIdField;
	private JTextField prodAfterField;
	private JTextField prodBeforeField;
	private boolean blocked = false;
	private boolean delivered = false;

	/**
	 * The list model for the cookie name list.
	 */
	private DefaultListModel nameListModel;

	/**
	 * The product name list.
	 */
	private JList productNameList;
	
	/**
	 * The list model for the cookie name list.
	 */
	private DefaultListModel palletListModel;

	/**
	 * The product name list.
	 */
	private JList palletList;

	/**
	 * The text field where the user id is entered.
	 */
	private JTextField[] fields2;

	/**
	 * The number of the product name field.
	 */
	private static final int PRODUCT_NAME = 0;

	/**
	 * The number of the production date field.
	 */
	private static final int PRODUCTION_DATE = 1;

	/**
	 * The number of the production date field.
	 */
	private static final int PRODUCTION_TIME = 2;

	/**
	 * The number of the 'is blocked' field.
	 */
	private static final int IS_BLOCKED = 3;

	/**
	 * The number of the 'is delivered' field.
	 */
	private static final int IS_DELIVERED = 4;

	/**
	 * The number of the 'is delivered' field.
	 */
	private static final int DELIVERY_DATE = 5;

	/**
	 * The number of the 'customer name' field.
	 */
	private static final int CUSTOMER_NAME = 6;

	/**
	 * The total number of fields.
	 */
	private static final int NBR_FIELDS2 = 7;

	/**
	 * Create the login pane.
	 * 
	 * @param db
	 *            The database object.
	 */
	public PalletSearchPane(Database db) {
		super(db);
	}

	public JComponent createLeftPanel() {

		JPanel basePlate = new JPanel();
		basePlate.setLayout(new BoxLayout(basePlate, BoxLayout.Y_AXIS));

		// Create the pallet ID components
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(5, 2));
		JLabel palletId = new JLabel("Pallet ID" + "    ", JLabel.RIGHT);
		palletIdField = new JTextField(10);
		

		// Create the date-search components

		JLabel prodAfter = new JLabel("Produced since " + "    ",
				JLabel.RIGHT);
		prodAfterField = new JTextField(10);

		JLabel prodBefore = new JLabel("Produced until " + "    ",
				JLabel.RIGHT);
		prodBeforeField = new JTextField(10);

		p1.add(palletId);
		p1.add(palletIdField);
		p1.add(new JLabel("    ", JLabel.RIGHT));
		p1.add(new JLabel("    ", JLabel.RIGHT));
		p1.add(new JLabel("Production dates" + "    ", JLabel.RIGHT));
		p1.add(new JLabel("  YYYY-MM-DD" + "    ", JLabel.LEFT));
		p1.add(prodAfter);
		p1.add(prodAfterField);
		p1.add(prodBefore);
		p1.add(prodBeforeField);

		// Create the cookie list components
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(1, 2));
		JLabel cookieType = new JLabel("Cookie type" + "    ", JLabel.RIGHT);

		nameListModel = new DefaultListModel();
		productNameList = new JList(nameListModel);
		productNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productNameList.setPrototypeCellValue("123456789012");
		JScrollPane scroll = new JScrollPane(productNameList);
		scroll.setPreferredSize(new Dimension(50, 100));

		p2.add(cookieType);
		p2.add(scroll);

		// Create the 'is delivered' components
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(4, 2));

		JLabel isDelivered = new JLabel("Delivered" + "    ", JLabel.RIGHT);
		JCheckBox deliveredBox = new JCheckBox();
		DeliveredHandler delivered = new DeliveredHandler();
		deliveredBox.addItemListener(delivered);

		// Create the 'is blocked' components
		JLabel isBlocked = new JLabel("Blocked" + "    ", JLabel.RIGHT);
		JCheckBox blockBox = new JCheckBox();
		BlockHandler block = new BlockHandler();
		blockBox.addItemListener(block);

		p3.add(isDelivered);
		p3.add(deliveredBox);
		p3.add(isBlocked);
		p3.add(blockBox);
		p3.add(new JLabel("    ", JLabel.RIGHT));
		p3.add(new JLabel("    ", JLabel.RIGHT));

		// Create the two buttons 'Search' and 'Clear'

		JButton search = new JButton("Search");
		search.addActionListener(new SearchHandler());
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ClearHandler());

		p3.add(search);
		p3.add(clear);

		basePlate.add(p1);
		// basePlate.add(p2);
		basePlate.add(p2);
		basePlate.add(p3);

		return basePlate;
	}

	public JComponent createMiddlePanel() {
		JPanel backPlate = new JPanel();
		backPlate.setLayout(new BoxLayout(backPlate, BoxLayout.Y_AXIS));
		
		//Create topPanel containing palletList and searchResults
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,2));
		
		JLabel info = new JLabel("Search results:    ", JLabel.RIGHT);
		
		palletListModel = new DefaultListModel();
		palletList = new JList(palletListModel);
		palletList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		palletList.setPrototypeCellValue("123456789012");
		palletList.addListSelectionListener(new PalletSelectionListener());
		JScrollPane scroll = new JScrollPane(palletList);
//		scroll.setPreferredSize(new Dimension(50, 100));
		
		topPanel.add(info);
		topPanel.add(scroll);
		
		//Create bottom panel containing production details
		String[] texts = new String[NBR_FIELDS2];
		texts[PRODUCT_NAME] = "Product";
		texts[PRODUCTION_DATE] = "Production date";
		texts[PRODUCTION_TIME] = "Production time";
		texts[IS_BLOCKED] = "Is blocked";
		texts[IS_DELIVERED] = "Is delivered";
		texts[DELIVERY_DATE] = "Delivery date";
		texts[CUSTOMER_NAME] = "Customer name";

		fields2 = new JTextField[NBR_FIELDS2];
		for (int i = 0; i < fields2.length; i++) {
			fields2[i] = new JTextField(10);
			fields2[i].setEditable(false);
		}

		InputPanel bottomPanel = new InputPanel(texts, fields2);
		
		backPlate.add(topPanel);
		backPlate.add(bottomPanel);

		return backPlate;
	}

	/**
	 * Create the bottom panel, consisting of the login button and the message
	 * line.
	 * 
	 * @return The bottom panel.
	 */
	public JComponent createBottomPanel() {
		JPanel bottom = new JPanel();
		bottom.add(messageLabel);
		return bottom;
	}

	/**
	 * Perform the entry actions of this pane, i.e. clear the message line.
	 */
	public void entryActions() {
		clearMessage();
		
		for (JTextField f : fields2)
			f.setText("");
	}
	
	public void fillProductList() {
		nameListModel.removeAllElements();
		nameListModel.addElement("<Any>");
		for (String s : db.getCookies()) {
			nameListModel.addElement(s);
		}
	}

	class SearchHandler implements ActionListener {
		/**
		 * Called when the user clicks the search button. Checks with the
		 * database if the pallet exists, and if so notifies the CurrentUser
		 * object.
		 * 
		 * @param e
		 *            The event object (not used).
		 */
		public void actionPerformed(ActionEvent e) {
			entryActions();
			StringBuilder sb = new StringBuilder();
			String idField = palletIdField.getText();
			if (!idField.equals("") && Input.validPalletId(idField, messageLabel)) {
				messageLabel.setText("Searching database ...");
				pastePalletInfo(idField);
				palletListModel.clear();
				palletListModel.addElement(idField);
				messageLabel.setText("Search complete!");
				return;
			}
			
			//Searching using the other parameters
			boolean firstParameter = true;
			
			sb.append("SELECT barcode_id FROM CustomerPallet WHERE ");
			
			if (!prodAfterField.getText().equals("") && Input.validDate(prodAfterField.getText(), messageLabel)) {
				sb.append("production_date > ");
				sb.append("'" + prodAfterField.getText() + "'");
				firstParameter = false;
			}
			if (!prodBeforeField.getText().equals("") && Input.validDate(prodBeforeField.getText(), messageLabel)) {
				if (!firstParameter) sb.append(" AND ");
				else firstParameter = false;
				sb.append("production_date < ");
				sb.append("'" + prodBeforeField.getText() + "'");
			}
			if (!productNameList.isSelectionEmpty() && !((String) productNameList.getSelectedValue()).equals("<Any>")) {
				if (!firstParameter) sb.append(" AND ");
				else firstParameter = false;
				String cookie = (String) productNameList.getSelectedValue();
				sb.append("product = ");
				sb.append("'" + cookie + "'");
			}
			if (!firstParameter) sb.append(" AND ");
			if (blocked) {
				sb.append("is_blocked = 1");
			} else {
				sb.append("is_blocked = 0");
			}
			if (delivered) {
				sb.append(" AND is_delivered = 1");
			} else {
				sb.append(" AND is_delivered = 0");
			}
			fillPalletList(sb.toString());
		}
	}

	class ClearHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			palletIdField.setText("");
			prodAfterField.setText("");
			prodBeforeField.setText("");
			palletListModel.removeAllElements();
			productNameList.clearSelection();
			for (JTextField field : fields2) {
				field.setText("");
			}
			
		}
	}

	/**
	 * A class which listens for ticks in the 'is blocked' box.
	 */
	class BlockHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			blocked = (e.getStateChange() == ItemEvent.SELECTED) ? true : false;
		}
	}

	/**
	 * A class which listens for ticks in the 'Delivered' box.
	 */
	class DeliveredHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			delivered = (e.getStateChange() == ItemEvent.SELECTED) ? true
					: false;
		}
	}
	
	private void fillPalletList(String sqlStatement) {
		palletListModel.removeAllElements();
		for (String s : db.customerPallets(sqlStatement)) {
			palletListModel.addElement(s);
		}
	}
	
	/**
	 * A class that listens for clicks in the pallet list.
	 */
	class PalletSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (palletList.isSelectionEmpty()) {
				return;
			}
			String palletID = (String) palletList.getSelectedValue();
			pastePalletInfo(palletID);
		}
	}
	
	private void pastePalletInfo(String barcode) {
		List<String> pallet = db.findPallet(Integer.parseInt(barcode));
		Iterator<String> itr = null;
		int count = 0;
		if (pallet != null) {
			itr = pallet.iterator();
			while (itr.hasNext()) {
				fields2[count].setText(itr.next());
				count++;
			}
		}
	}
}
