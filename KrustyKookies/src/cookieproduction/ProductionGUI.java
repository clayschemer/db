package cookieproduction;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 * MovieGUI is the user interface to the movie database. It sets up the main
 * window and connects to the database.
 */
public class ProductionGUI {
	/**
	 * db is the database object
	 */
	private Database db;

	/**
	 * tabbedPane is the contents of the window. It consists of two panes, User
	 * login and Book tickets.
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Create a GUI object and connect to the database.
	 * 
	 * @param db
	 *            The database.
	 */
	public ProductionGUI(Database db) {
		this.db = db;

		JFrame frame = new JFrame("Krusty Kookies");
		tabbedPane = new JTabbedPane();
		
		PalletSearchPane palletSearchPane = new PalletSearchPane(db);
		tabbedPane.addTab("Pallet search", null, palletSearchPane,
				"Search for pallet by its id");

		BatchCheckPane batchCheckPane = new BatchCheckPane(db);
		tabbedPane.addTab("Batch check", null, batchCheckPane,
				"Search and (un)block a batch of pallets");

		ProductionPane productionPane = new ProductionPane(db);
		tabbedPane.addTab("Cookie production", null, productionPane, "Choose and produce a pallet of cookies");

		tabbedPane.setSelectedIndex(0);

		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addChangeListener(new ChangeHandler());
		frame.addWindowListener(new WindowHandler());

		frame.setSize(600, 500);
		frame.setVisible(true);

		palletSearchPane.displayMessage("Connecting to database ...");
		
		if (db.openConnection("db51", "1010eda216")) {
			palletSearchPane.displayMessage("Connected to database");
			palletSearchPane.fillProductList();
		} else {
			palletSearchPane.displayMessage("Could not connect to database");
		}
	}

	/**
	 * ChangeHandler is a listener class, called when the user switches panes.
	 */
	class ChangeHandler implements ChangeListener {
		/**
		 * Called when the user switches panes. The entry actions of the new
		 * pane are performed.
		 * 
		 * @param e
		 *            The change event (not used).
		 */
		public void stateChanged(ChangeEvent e) {
			BasicPane selectedPane = (BasicPane) tabbedPane
					.getSelectedComponent();
			selectedPane.entryActions();
		}
	}

	/**
	 * WindowHandler is a listener class, called when the user exits the
	 * application.
	 */
	class WindowHandler extends WindowAdapter {
		/**
		 * Called when the user exits the application. Closes the connection to
		 * the database.
		 * 
		 * @param e
		 *            The window event (not used).
		 */
		public void windowClosing(WindowEvent e) {
			db.closeConnection();
			System.exit(0);
		}
	}
}
