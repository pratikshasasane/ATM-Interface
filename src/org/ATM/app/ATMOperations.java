package org.ATM.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ATM.app.MysqlConnection;

public class ATMOperations {
	public static void main(String[] args)
			throws NumberFormatException, IOException, SQLException, ClassNotFoundException, ParseException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		
			System.out.println("==============================================================================");
			System.out.println("=====================  WELCOME TO ATM Machine ================================");
			System.out.println("==============================================================================");
			System.out.println("\t\t 1 --> CUSTOMER");
			System.out.println("\t\t 2 --> ADMIN");
			System.out.println("==============================================================================");
			System.out.println("Enter your choice:");
			int choice = Integer.parseInt(br.readLine());

			if (choice == 1) {

				System.out.println("==============================================================================");
				System.out.println("===========================    LOGIN DETAILS  ================================");
				System.out.println("==============================================================================");

				System.out.print("\t Enter your Card Number:");
				String cardNumber = br.readLine();
				System.out.print("\t Enter your Pin:");
				String userPassword = br.readLine();
				try {
					Connection conn = MysqlConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement("select userPassword,uName from user1 where cardNumber=?");
					ps.setString(1, cardNumber);
					ResultSet result = ps.executeQuery();
					String password = null;
					String name = null;
					
					boolean login = false;
					while (result.next()) {
						password = result.getString("userPassword");
						name = result.getString("uName");
						login = true;
					}

					if (password.equals(userPassword)) {
						System.out
								.println("==============================================================================");
						
						System.out
								.println("==============================================================================");
						System.out.println("=========================== WELCOME  " + name.toUpperCase() + " ============================");
						System.out
								.println("==============================================================================");

						String status = "Y";
						do {

							System.out.println("\t\t  1 --> Deposit Amount");
							System.out.println("\t\t  2 --> Withdraw Amount");
							System.out.println("\t\t  3 --> Fund Transfer");
							System.out.println("\t\t  4 --> Balance Check");
							System.out.println("\t\t  5 --> Change Password");
							System.out.println("\t\t  6 --> Mini Statement");
							System.out.println("\t\t  7 --> Quick Cash");
							System.out.println("\t\t  8 --> Exit/Logout");
							
							
						
							
							System.out.println(
									"==============================================================================");
							System.out.print("Enter your choice:");
							int operationCode = Integer.parseInt(br.readLine());
							ResultSet res;
							switch (operationCode) {
							case 1:
								System.out.println("Enter the deposit amount:");
								double depositAmount = Double.parseDouble(br.readLine());

								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);
								res = ps.executeQuery();
								double existingBalance = 0.0;
								long uId = 0;
								while (res.next()) {
									existingBalance = res.getDouble("userBalance");
									uId = res.getLong("uId");
								}

								existingBalance = existingBalance + depositAmount;

								ps = conn.prepareStatement("update user1 set userBalance=? where cardNumber=?");
								ps.setDouble(1, existingBalance);
								ps.setString(2, cardNumber);

								if (ps.executeUpdate() > 0) {
									ps = conn.prepareStatement("insert into transactions1 values(?,?,?,?,?,?)");
									Timestamp timestamp = new Timestamp(System.currentTimeMillis());
									long transId = timestamp.getTime();

									ps.setLong(1, transId);
									ps.setDate(2, new Date(System.currentTimeMillis()));
									ps.setDouble(3, existingBalance);
									ps.setString(4, "deposit");
									ps.setLong(5, uId);
									ps.setLong(6, uId);

									ps.executeUpdate();

									System.out.println(
											"==============================================================================");
									System.out.println("Balance Updated!!");
									System.out.println("New account balance is :" + existingBalance);
									System.out.println(
											"==============================================================================");

								}

								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}

								break;
								
							case 2:
								System.out.println("Enter the withdraw amount:");
								double withdrawAmount = Double.parseDouble(br.readLine());

								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);
								res = ps.executeQuery();
								existingBalance = 0.0;
								uId = 0;
								while (res.next()) {
									existingBalance = res.getDouble("userBalance");
									uId = res.getLong("uId");
								}

								existingBalance = existingBalance - withdrawAmount;

								ps = conn.prepareStatement("update user1 set userBalance=? where cardNumber=?");
								ps.setDouble(1, existingBalance);
								ps.setString(2, cardNumber);

								if (ps.executeUpdate() > 0) {
									ps = conn.prepareStatement("insert into transactions1 values(?,?,?,?,?,?)");
									Timestamp timestamp = new Timestamp(System.currentTimeMillis());
									long transId = timestamp.getTime();

									ps.setLong(1, transId);
									ps.setDate(2, new Date(System.currentTimeMillis()));
									ps.setDouble(3, existingBalance);
									ps.setString(4, "withdraw");
									ps.setLong(5, uId);
									ps.setLong(6, uId);

									ps.executeUpdate();

									System.out.println(
											"==============================================================================");
									System.out.println("Balance Updated!!");
									System.out.println("New account balance is :" + existingBalance);
									System.out.println(
											"==============================================================================");

								}

								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}

								break;

							case 3:
								System.out.println("Enter the transaction1 amount:");
								double amount = Double.parseDouble(br.readLine());

								System.out.println("Enter the receiver user Id:");
								long receiverAccId = Long.parseLong(br.readLine());

								// fetching sender account balance
								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);
								res = ps.executeQuery();
								double senderexistingBalance = 0.0;
								uId = 0;
								while (res.next()) {
									senderexistingBalance = res.getDouble("userBalance");
									uId = res.getLong("uId");
								}

								// fetching receiver account balance
								ps = conn.prepareStatement("select * from user1 where uId=?");
								ps.setLong(1, receiverAccId);
								res = ps.executeQuery();
								double receiverExistingBalance = 0.0;
								long rcvraccId = 0;
								while (res.next()) {
									receiverExistingBalance = res.getDouble("userBalance");
									rcvraccId = res.getLong("uId");
								}

								if (senderexistingBalance > amount && rcvraccId != 0) {
									senderexistingBalance = senderexistingBalance - amount;
									receiverExistingBalance = receiverExistingBalance + amount;

									ps = conn.prepareStatement("update user1 set userBalance=? where uId=?");
									ps.setDouble(1, senderexistingBalance);
									ps.setLong(2, uId);
									ps.executeUpdate();

									ps = conn.prepareStatement("update user1 set userBalance=? where uId=?");
									ps.setDouble(1, receiverExistingBalance);
									ps.setLong(2, rcvraccId);
									ps.executeUpdate();

									ps = conn.prepareStatement("insert into transactions1 values(?,?,?,?,?,?)");
									Timestamp timestamp = new Timestamp(System.currentTimeMillis());
									long transId = timestamp.getTime();

									ps.setLong(1, transId);
									ps.setDate(2, new Date(System.currentTimeMillis()));
									ps.setDouble(3, amount);
									ps.setString(4, "transfer");
									ps.setLong(5, uId);
									ps.setLong(6, rcvraccId);

									if (ps.executeUpdate() > 0) {

										System.out.println(
												"==============================================================================");
										System.out.println("Transaction successful!!");
										System.out.println("New account balance is :" + senderexistingBalance);
										System.out.println(
												"==============================================================================");
									} else {
										System.out.println(
												"==============================================================================");
										System.out.println("Transaction failed!!");
										System.out.println(
												"==============================================================================");

									}

									System.out.println("Do you want to continue?(Y/N)");
									status = br.readLine();

									if (status.equals("n") || status.equals("N")) {
										login = false;
									}

								} else if (senderexistingBalance < amount) {
									System.out.println(
											"==============================================================================");
									System.out.println("Insufficient account balance!!");
									System.out.println(
											"==============================================================================");

								} else if (rcvraccId == 0) {
									System.out.println(
											"==============================================================================");
									System.out.println("Invalid receiver id!!");
									System.out.println(
											"==============================================================================");

								}

								break;

							case 4:
								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);
								res = ps.executeQuery();
								double balance = 0.0;
								while (res.next()) {
									balance = res.getDouble("userBalance");

								}
								System.out.println(
										"==============================================================================");
								System.out.println("Current account balance is :" + balance);
								System.out.println("Current account acc is :" + balance);
								System.out.println(
										"==============================================================================");
								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}

								break;
							case 5:
								System.out.println("Enter the old password:");
								String oldPassword = br.readLine();

								System.out.println("Enter the new password:");
								String newPassword = br.readLine();

								System.out.println("Re-enter the new password:");
								String rePassword = br.readLine();

								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);

								res = ps.executeQuery();
								String existingPassword = null;
								while (res.next()) {
									existingPassword = res.getString("userPassword");

								}

								if (existingPassword.equals(oldPassword)) {
									if (newPassword.equals(rePassword)) {
										ps = conn.prepareStatement("update user1 set userPassword=? where cardNumber=?");
										ps.setString(1, newPassword);
										ps.setString(2, cardNumber);

										if (ps.executeUpdate() > 0) {
											System.out.println(
													"==============================================================================");
											System.out.println("Password changed successfully!!");
											System.out.println(
													"==============================================================================");

										} else {
											System.out.println(
													"==============================================================================");
											System.out.println("Problem in password changed!!");
											System.out.println(
													"==============================================================================");

										}

									} else {
										System.out.println(
												"==============================================================================");
										System.out.println("New password and retype password must be same!!");
										System.out.println(
												"==============================================================================");

									}
								} else {
									System.out.println(
											"==============================================================================");
									System.out.println("Old password is wrong!!");
									System.out.println(
											"==============================================================================");

								}
								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}
								break;
								
							case 6:System.out.println("Transaction Id \t Date \t Amount \t Type");

							ps = conn.prepareStatement("select * from transactions1");
							ResultSet transactions = ps.executeQuery();

							while (transactions.next()) {
								System.out.println(transactions.getLong("transId") + "\t"
										+ transactions.getDate("transDate")

										+ "\t" + transactions.getDouble("transAmount") + "\t"
										+ transactions.getString("transType"));
							}
							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}
							break;

							
							case 7:
								System.out.println("Enter the Quick Cash Amount:");
								double withdrawAmt = Double.parseDouble(br.readLine());

								ps = conn.prepareStatement("select * from user1 where cardNumber=?");
								ps.setString(1, cardNumber);
								res = ps.executeQuery();
								existingBalance = 0.0;
								uId = 0;
								while (res.next()) {
									existingBalance = res.getDouble("userBalance");
									uId = res.getLong("uId");
								}

								existingBalance = existingBalance - withdrawAmt;

								ps = conn.prepareStatement("update user1 set userBalance=? where cardNumber=?");
								ps.setDouble(1, existingBalance);
								ps.setString(2, cardNumber);

								if (ps.executeUpdate() > 0) {
									ps = conn.prepareStatement("insert into transactions1 values(?,?,?,?,?,?)");
									Timestamp timestamp = new Timestamp(System.currentTimeMillis());
									long transId = timestamp.getTime();

									ps.setLong(1, transId);
									ps.setDate(2, new Date(System.currentTimeMillis()));
									ps.setDouble(3, existingBalance);
									ps.setString(4, "Quick cash");
									ps.setLong(5, uId);
									ps.setLong(6, uId);

									ps.executeUpdate();

									System.out.println(
											"==============================================================================");
									System.out.println("Balance Updated!!");
									System.out.println("New account balance is :" + existingBalance);
									System.out.println(
											"==============================================================================");

								}

								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}

								break;

                               case 8:
								login = false;
								break;
                             
							default:
								System.out.print("Wrong Choice!!");
								break;

							}

						} while (login);
						System.out.println("==============================================================================");
						System.out.println("Bye. visit Again!!");
						System.out.println("==============================================================================");

					} else {
						System.out
								.println("==============================================================================");
						System.out
								.println("================================  Wrong password  ============================");
						System.out
								.println("==============================================================================");
					}
				} catch (Exception e) {
					System.out.println(e);
					System.out.println("==============================================================================");
					System.out.println("===========================  Wrong username/password  ========================");
					System.out.println("==============================================================================");

				}

			}else if (choice == 2) {

				System.out.println("==============================================================================");
				System.out.println("===========================    LOGIN DETAILS  ================================");
				System.out.println("==============================================================================");

				System.out.print("\t Enter your username:");
				String aUsername = br.readLine();
				System.out.print("\t Enter your password:");
				String aPass = br.readLine();

				Connection conn = MysqlConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("select * from admin1 where aUsername=?");
				ps.setString(1, aUsername);
				ResultSet result = ps.executeQuery();
				String password = null;
				boolean login = false;
				while (result.next()) {
					password = result.getString("apass");
					login = true;
				}

				if (password.equals(aPass)) {

					String status = "y";
					System.out.println("==============================================================================");
					System.out.println("=============================   WELCOME ADMIN    =============================");
					System.out.println("==============================================================================");

					do {
						System.out.println("==============================================================================");
						
						System.out.println("\t\t  1 --> Add users");
						System.out.println("\t\t  2 --> Close ATM Card");
						System.out.println("\t\t  3 --> View Transactions");
						System.out.println("\t\t  4 --> Change Password");
						System.out.println("\t\t  5 --> Exit/Logout");
					
						System.out.println("==============================================================================");

						System.out.println("Enter your choice:");
						int operation = Integer.parseInt(br.readLine());

						switch (operation) {
						case 1:
							System.out.println("Enter User's full name:");
							String name = br.readLine();
							
							System.out.println("Enter User's Card Number:");
							String cardNumber = br.readLine();

							System.out.println("Set User Id:");
							long uId = Long.parseLong(br.readLine());

							System.out.println("Set User Pin: ");
							password = br.readLine();

							System.out.println("Re-enter account Pin: ");
							String userPassword = br.readLine();

							System.out.println("Account type:");
							String useraccountType = br.readLine();

							System.out.println("Initial balance:");
							double balance = Double.parseDouble(br.readLine());
							
						     
							ps = conn.prepareStatement("insert into user1 values(?,?,?,?,?,?)");
							ps.setLong(1, uId);
							ps.setString(2, name);
							ps.setString(3, cardNumber);
							ps.setString(4, password);
							ps.setDouble(5, balance);
							ps.setString(6, useraccountType);
							
							if (ps.executeUpdate() > 0) {
								System.out.println(
										"==============================================================================");
								System.out.println("Account created successfully!!");
								System.out.println(
										"==============================================================================");

							}

							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}
							break;
						case 2:
							System.out.println("Enter User id:");
							long uId1 = Long.parseLong(br.readLine());

							ps = conn.prepareStatement("delete from user1 where uId=?");
							ps.setLong(1, uId1);

							if (ps.executeUpdate() > 0) {
								System.out.println(
										"==============================================================================");
								System.out.println("ATM  card closed successfully!!");
								System.out.println(
										"==============================================================================");

							} else {
								System.out.println(
										"==============================================================================");
								System.out.println("Problem in account closing!!");
								System.out.println(
										"==============================================================================");

							}
							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}
							break;
							
						case 3:System.out.println("Transaction Id \t Date \t Amount \t Type");

						ps = conn.prepareStatement("select * from transactions1");
						ResultSet transactions = ps.executeQuery();

						while (transactions.next()) {
							System.out.println(transactions.getLong("transId") + "\t"
									+ transactions.getDate("transDate")

									+ "\t" + transactions.getDouble("transAmount") + "\t"
									+ transactions.getString("transType"));
						}
						System.out.println("Do you want to continue?(Y/N)");
						status = br.readLine();

						if (status.equals("n") || status.equals("N")) {
							login = false;
						}
							break;
						case 4:
							System.out.println("Enter the old password:");
							String oldPassword = br.readLine();

							System.out.println("Enter the new password:");
							String newPassword = br.readLine();

							System.out.println("Re-enter the new password:");
							String rePassword = br.readLine();

							ps = conn.prepareStatement("select * from admin1 where aUsername=?");
							ps.setString(1, aUsername);

							result = ps.executeQuery();
							String existingPassword = null;
							while (result.next()) {
								existingPassword = result.getString("aPass");

							}

							if (existingPassword.equals(oldPassword)) {
								if (newPassword.equals(rePassword)) {
									ps = conn.prepareStatement("update admin1 set aPass=? where aUsername=?");
									ps.setString(1, newPassword);
									ps.setString(2, aUsername);

									if (ps.executeUpdate() > 0) {
										System.out.println(
												"==============================================================================");
										System.out.println("Password changed successfully!!");
										System.out.println(
												"==============================================================================");

									} else {
										System.out.println(
												"==============================================================================");
										System.out.println("Problem in password changed!!");
										System.out.println(
												"==============================================================================");

									}

								} else {
									System.out.println(
											"==============================================================================");
									System.out.println("New password and retype password must be same!!");
									System.out.println(
											"==============================================================================");

								}
							} else {
								System.out.println(
										"==============================================================================");
								System.out.println("Old password is wrong!!");
								System.out.println(
										"==============================================================================");

							}
							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}
							break;

						case 5:
							login = false;
							break;
						
						default:
							System.out.println("Wrong Choice!!");
							break;

						}

					} while (login);
					
					System.out.println("==============================================================================");
					System.out.println("Bye. Thank you Used this App!!");
					System.out.println("==============================================================================");
					

				} else {
					System.out.println("Enter a valid input!!");
				}
			}
			}
	}

	

	