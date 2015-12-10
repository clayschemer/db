package cookieproduction;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;

public class Input {

	public static boolean validPalletId(String id, JLabel messageLabel) {
		int nbr = 0;
		try {
			nbr = Integer.parseInt(id);
			if (nbr < 0) {
				messageLabel
						.setText("A negative pallet ID. Really? Try again.");
				return false;
			}
		} catch (Exception ex) {
			messageLabel.setText("Only numbers are allowed mister!");
			return false;
		}
		return true;
	}

	public static boolean validDate(String date, JLabel messageLabel) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (date.trim().length() != dateFormat.toPattern().length()) {
			messageLabel.setText("Wrong date-format!");
			return false;
		}
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(date.trim());
		} catch (ParseException pe) {
			messageLabel.setText("Only numbers are allowed mister!");
			return false;
		}
		return true;
	}
}
