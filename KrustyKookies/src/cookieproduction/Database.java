package cookieproduction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.sql.*;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC and the MySQL Connector/J driver.
 */
public class Database {
	
	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * An SQL prepared statement object.
	 */
	private PreparedStatement ps = null;

	private String findPallet = "SELECT * FROM CustomerPallet WHERE barcode_id = ?";
	
	private String getCookies = "SELECT name FROM Products";
	private String getProductDates = "SELECT production_date FROM Pallets WHERE product_name = ? AND is_blocked = ? GROUP BY production_date";
	private String getBatch = "SELECT barcode_id FROM Pallets WHERE product_name = ? AND is_blocked = ? AND production_date = ?";
	private String blockBatch = "UPDATE Pallets SET is_blocked = ? WHERE barcode_id = ?";
	
	private String getLastProduced = "SELECT product_name FROM Pallets WHERE barcode_id = (SELECT MAX(barcode_id) FROM Pallets)";
	private String getIngredients = "SELECT ingredient_name, amount FROM Recipes WHERE product_name = ?";
	private String getStoredAmount = "SELECT amount FROM Ingredients WHERE name = ?";
	private String palletsInFreezer = "SELECT count(*) FROM Pallets WHERE product_name = ?";
	private String palletsOnOrder = "SELECT nbr_of_pallets FROM OrderList WHERE product_name = ?";
	private String useIngredient = "UPDATE Ingredients SET amount = amount - ?*? WHERE name = ?";
	private String makePallet = "INSERT INTO Pallets(order_nbr, product_name, production_date, production_time) VALUES(1, ?, CURDATE(), CURTIME())";
	
	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The user's password.
	 * @return true if the connection succeeded, false if the supplied user name
	 *         and password were not recognized. Returns false also if the JDBC
	 *         driver isn't found.
	 */
	public boolean openConnection(String userName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/" + userName, userName,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
		conn = null;
	}

	/**
	 * Check if the connection to the database has been established
	 * 
	 * @return true if the connection has been established
	 */
	public boolean isConnected() {
		return conn != null;
	}
	
	public String lastProduced() {
		ps = null;
		String lastCookie = "";
		try {
			ps = conn.prepareStatement(getLastProduced);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				lastCookie = rs.getString("product_name");
			}
		} catch (SQLException e) {
			System.out.println("lastProducedException ");
			e.printStackTrace();
		}
		return lastCookie;
	}

	public List<String> getCookies() {
		List<String> array = new ArrayList<String>();
		ps = null;
		try {
			ps = conn.prepareStatement(getCookies);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				array.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			System.out.println("getCookiesFail");
			e.printStackTrace();
		}
		return array;
	}

	public Map<String, Integer> getIngredients(String cookieName) {
		Map<String, Integer> map = new TreeMap<String, Integer>();
		String ingr = null;
		Integer amo = null;
		ps = null;
		try {
			ps = conn.prepareStatement(getIngredients);
			ps.setString(1, cookieName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ingr = rs.getString("ingredient_name");
				amo = rs.getInt("amount");
				map.put(ingr, amo);
			}
		} catch (SQLException e) {
			System.out.println("getIngredientsFail");
			e.printStackTrace();
		}
		return map;
	}
	
	public int getStoredAmount(String ingredientName) {
		ps = null;
		int amount = 0;
		try {
			ps = conn.prepareStatement(getStoredAmount);
			ps.setString(1, ingredientName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				amount = rs.getInt("amount");;
			}
		} catch (SQLException e) {
			System.out.println("getStoredAmountFail");
			e.printStackTrace();
		}
		return amount;
	}
	
	public boolean blockBatch(String palletID, boolean blocked) {
		try {
			ps = null;

			ps = conn.prepareStatement(blockBatch);
			conn.setAutoCommit(false);
			if (blocked) {
				ps.setInt(1, 1);
			} else {
				ps.setInt(1, 0);
			}
			
			System.out.println("Pallet to be (un)blocked: " + palletID + " Block: " + (blocked ? "YES" : "NO"));
			
			ps.setInt(2, Integer.parseInt(palletID));
			
			ps.executeUpdate();

			conn.commit();
			conn.setAutoCommit(true);
			
			return true;

		} catch (SQLException e) {
			System.out.println("blockBatchFail ");
			e.printStackTrace();
		}
		return false;
	}
	
	public List<String> getProductDates(String productName, boolean showBlocked) {
		List<String> array = new ArrayList<String>();
		ps = null;
		try {
			ps = conn.prepareStatement(getProductDates);
			ps.setString(1, productName);
			if (showBlocked) {
				ps.setInt(2, 1);
			} else {
				ps.setInt(2, 0);
			}
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				array.add(rs.getString("production_date"));
			}
		} catch (SQLException e) {
			System.out.println("getProductDatesFail");
			e.printStackTrace();
		}
		return array;
	}
	
	public List<String> getBatch(String productName, String date, boolean showBlocked) {
		List<String> array = new ArrayList<String>();
		ps = null;
		try {
			ps = conn.prepareStatement(getBatch);
			ps.setString(1, productName);
			if (showBlocked) {
				ps.setInt(2, 1);
			} else {
				ps.setInt(2, 0);
			}
			ps.setString(3, date);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				array.add(rs.getString("barcode_id"));
			}
		} catch (SQLException e) {
			System.out.println("getBatchFail");
			e.printStackTrace();
		}
		return array;
	}

	public List<String> findPallet(int i) {
		ps = null;
		List<String> array = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(findPallet);
			ps.setInt(1, i);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				array.add(rs.getString("product"));
				array.add(rs.getString("production_date"));
				array.add(rs.getString("production_time"));
				array.add(rs.getInt("is_blocked") == 1 ? "Yes" : "No");
				array.add(rs.getInt("is_delivered") == 1 ? "Yes" : "No");
				array.add(rs.getString("delivery_date"));
				array.add(rs.getString("customer"));
			}
			return array;
		} catch (SQLException e) {
			System.out.println("findPalletException ");
			e.printStackTrace();
		}
		return null;
	}
	
	public String palletsInFreezer(String productName) {
		ps = null;
		int pallets = 0;
		try {
			ps = conn.prepareStatement(palletsInFreezer);
			ps.setString(1, productName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				pallets = rs.getInt("count(*)");
			}
		} catch (SQLException e) {
			System.out.println("palletsInFreezerException ");
			e.printStackTrace();
		}
		return Integer.toString(pallets);
	}
	
	public String palletsOnOrder(String productName) {
		ps = null;
		int pallets = 0;
		ArrayList<Integer> amount = new ArrayList<Integer>();
		try {
			ps = conn.prepareStatement(palletsOnOrder);
			ps.setString(1, productName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				amount.add(rs.getInt("nbr_of_pallets"));
			}
			for (Integer i : amount) {
				pallets += i; 
			}
		} catch (SQLException e) {
			System.out.println("palletsOnOrderException ");
			e.printStackTrace();
		}
		return Integer.toString(pallets);
	}

	public int producePallet(int constant, Cookie cookie) {
		Iterator<Map.Entry<String,Integer>> itr = cookie.getIngredients().entrySet().iterator();
		String ingr = null;
		int amo = 0;
		while (itr.hasNext()) {
			ps = null;
			try {
				Map.Entry<String,Integer> entry = itr.next();
				ingr = entry.getKey();
				amo = entry.getValue();
				
				ps = conn.prepareStatement(useIngredient);
				conn.setAutoCommit(false);
				ps.setInt(1, constant);
				ps.setInt(2, amo);
				ps.setString(3, ingr);
				ps.executeUpdate();

			} catch (SQLException e) {
				System.out.println("drawIngredientFail ");
				e.printStackTrace();
			}
		}

		int palletNbr = 0;
		try {
			ps = null;

			ps = conn.prepareStatement(makePallet);
			ps.setString(1, cookie.getCookieName());
			ps.executeUpdate();
			
			ps = null;

			String sql = "SELECT LAST_INSERT_ID() FROM Pallets";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				palletNbr = rs.getInt(1);
			}
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println("makePalletFail ");
			e.printStackTrace();
		}

		ps = null;
		return palletNbr;
	}
	
	public List<String> customerPallets(String sqlQuery) {
		Statement stmt = null;
		List<String> array = new ArrayList<String>();
		try {
			stmt = conn.prepareStatement(sqlQuery);
			ResultSet rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				array.add(rs.getString("barcode_id"));
			}
			return array;
		} catch (SQLException e) {
			System.out.println("customerPalletException ");
			e.printStackTrace();
		}
		return null;
	}
}
