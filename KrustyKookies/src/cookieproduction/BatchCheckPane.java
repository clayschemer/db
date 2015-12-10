package cookieproduction;

import javax.swing.*;
import javax.swing.event.*;


import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;


/**
 * The GUI pane where a user books tickets for movie performances. It contains
 * one list of movies and one of performance dates. The user selects a
 * performance by clicking in these lists. The performance data is shown in the
 * fields in the right panel. The bottom panel contains a button which the user
 * can click to book a ticket to the selected performance.
 */
public class BatchCheckPane extends BasicPane {
	private static final long serialVersionUID = 1;

	/**
	 * The list model for the movie name list.
	 */
	private DefaultListModel nameListModel;

	/**
	 * The product name list.
	 */
	private JList productNameList;

	/**
	 * The list model for the products date list.
	 */
	private DefaultListModel dateListModel;

	/**
	 * The product date list.
	 */
	private JList dateList;
	
	/**
	 * The list model for the movie name list.
	 */
	private DefaultListModel palletListModel;

	/**
	 * The product name list.
	 */
	private JList palletList;
	private ArrayList<String> currentPallets;
	private boolean showBlocked = false;

	/**
	 * Create the booking pane.
	 * 
	 * @param db
	 *            The database object.
	 */
	public BatchCheckPane(Database db) {
		super(db);
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
		productNameList.setPrototypeCellValue("123456789012345");
		productNameList.addListSelectionListener(new NameSelectionListener());
		JScrollPane p1 = new JScrollPane(productNameList);

		dateListModel = new DefaultListModel();

		dateList = new JList(dateListModel);
		dateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dateList.setPrototypeCellValue("123456789012345");
		dateList.addListSelectionListener(new DateSelectionListener());
		JScrollPane p2 = new JScrollPane(dateList);

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

		JPanel p1 = new JPanel();
		JCheckBox box = new JCheckBox();
		BlockHandler blockHandler = new BlockHandler();
		
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1.add(new JLabel("Show blocked pallets "));
		p1.add(box);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(p1);
		box.addItemListener(blockHandler);
		
		return p;
	}
	
	public JComponent createMiddlePanel() {
		palletListModel = new DefaultListModel();
		palletList = new JList(palletListModel);
		
		palletList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		palletList.setPrototypeCellValue("1234567890");
		palletList.addListSelectionListener(new PalletSelectionListener());
		JScrollPane p1 = new JScrollPane(palletList);
		
		return p1;
	}

	/**
	 * Create the bottom panel, containing the book ticket-button and the
	 * message line.
	 * 
	 * @return The bottom panel.
	 */
	public JComponent createBottomPanel() {
		JButton[] buttons = new JButton[1];
		buttons[0] = new JButton("(un)block batch");
		return new ButtonAndMessagePanel(buttons, messageLabel,
				new ActionHandler());
	}

	/**
	 * Perform the entry actions of this pane: clear all fields, fetch the movie
	 * names from the database and display them in the name list.
	 */
	public void entryActions() {
		clearMessage();
		fillNameList();
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
	private void fillDateList(String productName) {
		dateListModel.removeAllElements();
		palletListModel.removeAllElements();
		for (String s : db.getProductDates(productName, showBlocked)) {
			dateListModel.addElement(s);
		}
	}
	
	/**
	 * Fetch performance dates from the database and display them in the date
	 * list.
	 */
	private void fillPalletList(String productName, String date) {
		palletListModel.removeAllElements();
		currentPallets = new ArrayList<String>();
		for (String s : db.getBatch(productName, date, showBlocked)) {
			palletListModel.addElement(s);
			currentPallets.add(s);
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
			String product = (String) productNameList.getSelectedValue();
			fillDateList(product);
		}
	}

	/**
	 * A class that listens for clicks in the date list.
	 */
	class DateSelectionListener implements ListSelectionListener {
		/**
		 * Called when the user selects a name in the date list. Fetches
		 * performance data from the database and displays it in the text
		 * fields.
		 * 
		 * @param e
		 *            The selected list item.
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (productNameList.isSelectionEmpty() || dateList.isSelectionEmpty()) {
				return;
			}
			String product = (String) productNameList.getSelectedValue();
			String date = (String) dateList.getSelectedValue();
			fillPalletList(product, date);
		}
	}
	
	/**
	 * A class that listens for clicks in the pallet list.
	 */
	class PalletSelectionListener implements ListSelectionListener {
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
			boolean allBlocked = true;
			if (productNameList.isSelectionEmpty() || dateList.isSelectionEmpty()) {
				return;
			}
			for (int i = 0; i < currentPallets.size(); i++) {
				if (!db.blockBatch(currentPallets.get(i), showBlocked ? false : true)) {
					allBlocked = false;
				}
			}
			
			if (allBlocked) {
				displayMessage("Batch of pallets blocked!");
			} else {
				displayMessage("All or some pallets couldn't be blocked!");
			}
		}
	}
	
	/**
	 * A class which listens for button clicks.
	 */
	class BlockHandler implements ItemListener {
		/**
		 * Called when the user clicks the block button. Checks with the
		 * database if the user exists, and if so notifies the showBlocked
		 * instance.
		 * 
		 * @param e
		 *            The event object (not used).
		 */
		public void itemStateChanged(ItemEvent e) {
			showBlocked = (e.getStateChange() == ItemEvent.SELECTED) ? true : false;
			dateListModel.removeAllElements();
			palletListModel.removeAllElements();
		}
	}
}
