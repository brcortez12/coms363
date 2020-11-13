package coms363c;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * 
 * @author Brandon Cortez
 *
 */
public class hw4 {
	public static String[] loginDialog() {
		String result[] = new String[2];
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();

		cs.fill = GridBagConstraints.HORIZONTAL;

		JLabel lbUsername = new JLabel("Username: ");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		panel.add(lbUsername, cs);

		JTextField tfUsername = new JTextField(20);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		panel.add(tfUsername, cs);

		JLabel lbPassword = new JLabel("Password: ");
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		panel.add(lbPassword, cs);

		JPasswordField pfPassword = new JPasswordField(20);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		panel.add(pfPassword, cs);
		panel.setBorder(new LineBorder(Color.GRAY));

		String[] options = new String[] { "OK", "Cancel" };
		int ioption = JOptionPane.showOptionDialog(null, panel, "Login", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (ioption == 0) // pressing OK button
		{
			result[0] = tfUsername.getText();
			result[1] = new String(pfPassword.getPassword());
		}
		return result;
	}

	/**
	 * @param stmt
	 * @param sqlQuery
	 * @throws SQLException
	 */
	private static void runQuery(Statement stmt, String sqlQuery) throws SQLException {
		ResultSet rs;
		ResultSetMetaData rsMetaData;
		String toShow;
		rs = stmt.executeQuery(sqlQuery);
		rsMetaData = rs.getMetaData();
		System.out.println(sqlQuery);
		toShow = "";
		while (rs.next()) {
			for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
				toShow += rs.getString(i + 1) + ", ";
			}
			toShow += "\n";
		}
		JOptionPane.showMessageDialog(null, toShow);
	}

	/**
	 * Show an example of an insert statement
	 * @param conn Valid database connection
	 * 		  name: Name of an actor to check
	 */
	private static void insertActor(Connection conn, String fname, String lname) {

		if (conn==null || fname==null || lname==null) throw new NullPointerException();
		if (fname.equals("") || lname.contentEquals("")) {
			System.out.println("Please enter a valid actor name.");
		}
		else {
			try {
				conn.setAutoCommit(false);
				conn.setTransactionIsolation(4);
				Statement stmt = conn.createStatement();
				ResultSet rs;
				int id=0;
				rs = stmt.executeQuery("select max(actor_id) from actor");
				while (rs.next()) {
					id = rs.getInt(1);
				}
				rs.close();
				stmt.close();


				PreparedStatement inststmt = conn.prepareStatement(
						"insert into actor (actor_id, first_name, last_name, last_update) values(?,?,?,current_timestamp()) "); //four parameters are needed

				// first column has the new actor id that is unique
				inststmt.setInt(1, id+1);
				// second and third have the actor first and last name
				inststmt.setString(2, fname.toUpperCase());
				inststmt.setString(3, lname.toUpperCase());

				int rowcount = inststmt.executeUpdate();

				System.out.println("Number of rows updated:" + rowcount);
				inststmt.close();
				// confirm that these are the changes you want to make
				conn.commit();
				// if other parts of the program needs commit per SQL statement
				// conn.setAutoCommit(true);
			} catch (SQLException e) {}
		}
	}

	private static void deleteCustomer(Connection conn, int CustomerID) {

		if (conn==null) throw new NullPointerException();
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		cs.fill = GridBagConstraints.HORIZONTAL;
		JLabel lbConfirm = new JLabel("Warning all the information related to this customer will be deleted.\n Enter “y” or “n” to proceed: ");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		panel.add(lbConfirm, cs);

		JTextField tfConfirm = new JTextField(20);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		panel.add(tfConfirm, cs);

		String[] options = new String[] { "OK", "Cancel" };
		int ioption = JOptionPane.showOptionDialog(null, panel, "Delete", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (ioption == 0) // pressing OK button
		{
			if (tfConfirm.getText().contentEquals("Y") || tfConfirm.getText().contentEquals("y")) {
				if (CustomerID==0) {
					System.out.println("Please enter a valid customer ID.");
				}
				else {
					try {
						conn.setAutoCommit(false);
						conn.setTransactionIsolation(4);
						PreparedStatement delStmt1 = conn.prepareStatement("DELETE FROM payment WHERE customer_id = ?");
						PreparedStatement delStmt2 = conn.prepareStatement("DELETE FROM rental WHERE customer_id = ?");
						PreparedStatement delStmt3 = conn.prepareStatement("DELETE FROM customer WHERE customer_id = ?");

						delStmt1.setInt(1, CustomerID);
						delStmt1.addBatch();

						delStmt2.setInt(1, CustomerID);
						delStmt2.addBatch();

						delStmt3.setInt(1, CustomerID);
						delStmt3.addBatch();

						delStmt1.executeBatch();
						delStmt2.executeBatch();
						delStmt3.executeBatch();

						System.out.println("Customer successfully deleted.");
						delStmt1.close();
						delStmt2.close();
						delStmt3.close();
						// confirm that these are the changes you want to make
						conn.commit();
						// if other parts of the program needs commit per SQL statement
						// conn.setAutoCommit(true);
					} catch (SQLException e) {
						System.out.println("Record couldn't be deleted please try again.");
					}
				}
			}
		}
	}

	private static void callStoredTotalSales(Connection conn, int month) {
		if (conn==null) throw new NullPointerException();
		try {
			CallableStatement cstmt= conn.prepareCall("{call my_total_sales(?,?)}");
			cstmt.setInt(1, month);
			cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			cstmt.executeUpdate();
			System.out.println("Total sales = " +cstmt.getDouble(2));
		}
		catch (SQLException e) {};
			System.out.println("Unable to find total sales of the given month.");
	}


	public static void main(String[] args) {
		String dbServer = "jdbc:mysql://localhost:3306/sakila_mod?allowPublicKeyRetrieval=true&useSSL=false";
		String userName = "cs363@%";
		String password = "363F2020";

		String result[] = loginDialog();
		userName = result[0];
		password = result[1];

		Connection conn;
		Statement stmt;
		if (result[0]==null || result[1]==null) {
			System.out.println("Terminating: No username nor password is given");
			return;
		}
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbServer, userName, password);
			stmt = conn.createStatement();
			String sqlQuery = "";

			String option = "";
			String instruction = "Enter A: Insert a new actor." + "\n"
					+ "Enter B: Delete a customer." + "\n"
					+ "Enter C: Find total sales of a month." + "\n"
					+ "Enter E: Quit Program";

			while (true) {
				option = JOptionPane.showInputDialog(instruction);
				if (option.equals("A") || option.contentEquals("a")) {
					String name[] = new String[2];
					JPanel panel = new JPanel(new GridBagLayout());
					GridBagConstraints cs = new GridBagConstraints();
					cs.fill = GridBagConstraints.HORIZONTAL;
					JLabel lbFirstname = new JLabel("Firstname: ");
					cs.gridx = 0;
					cs.gridy = 0;
					cs.gridwidth = 1;
					panel.add(lbFirstname, cs);

					JTextField tfFirstname = new JTextField(20);
					cs.gridx = 1;
					cs.gridy = 0;
					cs.gridwidth = 2;
					panel.add(tfFirstname, cs);

					JLabel lbLastname = new JLabel("Lastname: ");
					cs.gridx = 0;
					cs.gridy = 1;
					cs.gridwidth = 1;
					panel.add(lbLastname, cs);

					JTextField tfLastname = new JTextField(20);
					cs.gridx = 1;
					cs.gridy = 1;
					cs.gridwidth = 2;
					panel.add(tfLastname, cs);
					panel.setBorder(new LineBorder(Color.GRAY));

					String[] options = new String[] { "OK", "Cancel" };
					int ioption = JOptionPane.showOptionDialog(null, panel, "Insert", JOptionPane.OK_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (ioption == 0) // pressing OK button
					{
						name[0] = tfFirstname.getText();
						name[1] = tfLastname.getText();
						insertActor(conn, name[0], name[1]);
					}
				}

				else if (option.equals("B") || option.contentEquals("b")){
					int customer = 0;
					JPanel panel = new JPanel(new GridBagLayout());
					GridBagConstraints cs = new GridBagConstraints();
					cs.fill = GridBagConstraints.HORIZONTAL;
					JLabel lbCustomerID = new JLabel("Customer ID: ");
					cs.gridx = 0;
					cs.gridy = 0;
					cs.gridwidth = 1;
					panel.add(lbCustomerID, cs);

					JTextField tfCustomerID = new JTextField(20);
					cs.gridx = 1;
					cs.gridy = 0;
					cs.gridwidth = 2;
					panel.add(tfCustomerID, cs);

					String[] options = new String[] { "OK", "Cancel" };
					int ioption = JOptionPane.showOptionDialog(null, panel, "Delete", JOptionPane.OK_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (ioption == 0) // pressing OK button
					{
						customer = Integer.parseInt(tfCustomerID.getText());
						deleteCustomer(conn, customer);
					}
				}

				else if (option.equals("C") || option.contentEquals("c")){
					int month = 0;
					JPanel panel = new JPanel(new GridBagLayout());
					GridBagConstraints cs = new GridBagConstraints();
					cs.fill = GridBagConstraints.HORIZONTAL;
					JLabel lbMonthNum = new JLabel("Enter the month you would like to find the total sales of as a number (Jan = 1, Feb =2, etc): ");
					cs.gridx = 0;
					cs.gridy = 0;
					cs.gridwidth = 1;
					panel.add(lbMonthNum, cs);

					JTextField tfMonthNum = new JTextField(20);
					cs.gridx = 1;
					cs.gridy = 0;
					cs.gridwidth = 2;
					panel.add(tfMonthNum, cs);

					String[] options = new String[] { "OK", "Cancel" };
					int ioption = JOptionPane.showOptionDialog(null, panel, "Delete", JOptionPane.OK_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (ioption == 0) // pressing OK button
					{
						month = Integer.parseInt(tfMonthNum.getText());
						callStoredTotalSales(conn, month);
					}
				}

				else if (option.equals("E") || option.contentEquals("e")){
					break;
				}

				else {
					System.out.println("Please select a valid option.");
					continue;
				}
			}

			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("Program terminates due to errors");
			e.printStackTrace(); // for debugging
		}
	}

}
