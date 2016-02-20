package com.example.coopsysapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.coopsysapp.exception.*;

import android.util.Log;

public class ServerConnector {
	
	public static final boolean offline = true;
	
	
	public static Socket client_socket;// = new Socket("192.168.42.1", 5000);	
	private static final String ip = "192.168.42.1";
	private static final int port = 5000;
	private static User user;

	public static String nameString = new String("1,Emme;2,Lars;3,Kristof");
	public static String einkaufString = new String("1,1,Marktkauf,01-01-16");
	public static String einkaufPartString = new String("1,2,5.55,dein Teil;1,3,5.55,dein Teil");
	public static String debtString= new String("1,2,-5.55;1,3,-5.55;2,1,5.55;2,3,0;3,1,5.55;3,2,0;");
	
	private ServerConnector() {
		
	}
	
	public static void setUser(User user) {
		ServerConnector.user = user;
	}
	
	public static User getUser() {
		return ServerConnector.user;
	}
	
	private static void initialize(String ip, int port) throws UnknownHostException, IOException{
		if (!offline) {
			client_socket = new Socket(ip, port);
		}
	}
	
	private static String sendFunction(String function) throws IOException, FunctionNotDefinedException {
		//TODO test if works
		String message;
		PrintWriter outToServer = new PrintWriter(
				client_socket.getOutputStream(), true);
		BufferedReader inFromServer = new BufferedReader(
				new InputStreamReader(client_socket.getInputStream()));

		outToServer.println(function);

		message = (inFromServer.readLine());
		//function not defined: "Function not defined;" + function
		if (message.startsWith("Function not defined")) {
			String[] m = message.split(";");
			throw new FunctionNotDefinedException(m[1]);
		}
		return message;
	}
	
	/**
	 * Method that returns the current list of users
	 * @return list  of users
	 * @throws IOException
	 * @throws FunctionNotDefinedException 
	 * @throws NoneFoundException 
	 */
	public static User[] getNameList() throws IOException, FunctionNotDefinedException, NoneFoundException{
		initialize("192.168.42.1", 5000);
		Log.d("ServerConnector", "Connected");
		
		String message = "";
		if (!offline) {
			message = sendFunction("getNameList()");
		} else {
			message = nameString;
		}
		String[] itemsfirst = message.split(";");

		User[] items = new User[itemsfirst.length];
		for (int i = 0; i < itemsfirst.length; i++) {
			String[] temp = itemsfirst[i].split(",");
			items[i] = new User(Integer.valueOf(temp[0]), temp[1]);
		}
		close();
		if (items.length == 0) {
			throw new NoneFoundException();
		}
		return items;
		
	}
	
	/**
	 * Method that register a new user on server
	 * @param name of new user
	 * @return id of new user
	 * @throws IOException
	 * @throws FunctionNotDefinedException 
	 */
	public static int register(String name) throws IOException, FunctionNotDefinedException{
		initialize("192.168.42.1", 5000);
		String message = "";
		if (!offline) {
			message = sendFunction("register("+ name +")");
		}else {
			User[] userList;
			try {
				userList = getNameList();
			} catch (NoneFoundException e) {
				userList = new User[0];
			}
			
			for (User user : userList) {
				if (user.getName().equalsIgnoreCase(name)) {
					return 0;
				}
			}
			
			if (userList.length != 0) {
				User lastUser = userList[userList.length-1];
				for (User u: userList) {
					debtString = debtString + ";" + u.getId() + "," + (lastUser.getId()+1) + ",0" + ";" + (lastUser.getId()+1) + "," + u.getId() + ",0";
				}
				nameString= nameString + ";" + (lastUser.getId()+1) + "," + name;
				message = Integer.toString(lastUser.getId()+1);
			} else {
				nameString = "1," + name;
				message = "1";
			}
			
			
		}
		
		close();
		return Integer.parseInt(message);
		
	}
	
	/**
	 * returns values of known Einkauf
	 * @param einkaufId
	 * @return known Einkauf by id
	 * @throws IOException
	 * @throws FunctionNotDefinedException 
	 */
	public static Einkauf getEinkauf(int einkaufId) throws IOException, FunctionNotDefinedException{
		initialize("192.168.42.1", 5000);
		String message = "";
		
		if (!offline) {
			message = sendFunction("getEinkauf("+ einkaufId +")");
		}else {
			String[] itemsfirst = einkaufString.split(";");
			for (int i = 0; i < itemsfirst.length; i++) {
				String[] temp = itemsfirst[i].split(",");
				if (Integer.valueOf(temp[0]) == einkaufId) {
					message = temp[1] + "," + temp[2] + "," + temp[3];
				}
				
			}
			message = einkaufString;
		}
		
		Einkauf einkauf = null;
		if (message != "null") { //TODO fehlermeldung überarbeiten?
			String[] temp = message.split(",");
			einkauf = new Einkauf(einkaufId,Integer.valueOf(temp[0]), temp[1], temp[2]);
		}
		close();
		return einkauf;
	
	}
	
	/**
	 * Adds a new Einkauf 
	 * @param einkauferId
	 * @param name
	 * @param datum
	 * @return ID of new Einkauf
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws FunctionNotDefinedException 
	 */
	public static int addEinkauf(int einkauferId, String name, String datum) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message = "";
		if (!offline) {
			message = sendFunction("addEinkauf("+ einkauferId + "," + name + "," + datum + "," +")");
		}else {
			int id = 0;
			int lastEinkaufId = Integer.parseInt(einkaufString.split(";")[einkaufString.split(";").length].split(",")[0]);
			einkaufString = einkaufString + ";" + (lastEinkaufId+1) + "," + einkauferId + ","
					+ "" + name + "," + datum;
			id=lastEinkaufId+1;
			message = Integer.toString(id);
		}
		
		close();
		return Integer.parseInt(message);
	}
	
	public static EinkaufPart getEinkaufPart(int einkaufId, int gastId) throws UnknownHostException, IOException, FunctionNotDefinedException {
		
		initialize(ip, port);
		String message = "";
		if (!offline) {
			message = sendFunction("getEinkaufPart("+ einkaufId + "," + gastId +")");
		}else {
			String[] itemsfirst = einkaufPartString.split(";");
			for (int i = 0; i < itemsfirst.length; i++) {
				String[] temp = itemsfirst[i].split(",");
				if (Integer.valueOf(temp[0]) == einkaufId && Integer.valueOf(temp[1]) == gastId) {
					message = temp[2] + "," + temp[3];
				}
				
			}
			message = einkaufString;
		}
		
		EinkaufPart einkaufPart = null;
		if (message != "null") { //TODO fehlermeldung überarbeiten?
			String[] temp = message.split(",");
			einkaufPart = new EinkaufPart(einkaufId,gastId, Float.parseFloat(temp[0]), temp[1]);
		}
		
		close();
		return einkaufPart;
		
	}
	
	public static boolean addEinkaufPart(int einkaufId, int gastId, float betrag, String notiz) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("addEinkaufPart("+ einkaufId + "," + gastId + "," + betrag + "," + notiz +")");
		}else {
			int f = 3;
			String[] debtStrings = debtString.split(";");
			String[][] debts = new String[debtStrings.length][];
			for (int i=1; i<debtStrings.length; i++){
				debts[i] = debtString.split(",");
				if (Integer.parseInt(debts[i][0])==getEinkauf(einkaufId).getEinkauefer() && Integer.parseInt(debts[i][1])==gastId) {
					debts[i][2] = Float.toString(Float.parseFloat(debts[i][2]) - betrag);
					f--;
				} else if (Integer.parseInt(debts[i][1])==getEinkauf(einkaufId).getEinkauefer() && Integer.parseInt(debts[i][0])==gastId) {
					debts[i][2] = Float.toString(Float.parseFloat(debts[i][2]) + betrag);
					f--;
				}
			}
			if (f==1) {
				message = "1";
				einkaufPartString = einkaufPartString + ";" + einkaufId + "," + gastId + "," + betrag + "," + notiz;
			} else {
				message = "0";
			}
		}
		
		close();
		return Boolean.parseBoolean(message);
	}
	
	public static Debt getDebt(int schuldnerId, int glaubigerId) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("getDebt("+ schuldnerId + "," + glaubigerId +")");
		}else {
			String[] debtStrings = debtString.split(";");
			String[][] debts = new String[debtStrings.length][];
			for (int i=1; i<debtStrings.length; i++){
				debts[i] = debtString.split(",");
				if (Integer.parseInt(debts[i][0])==schuldnerId && Integer.parseInt(debts[i][1])==glaubigerId) {
					message = debts[i][2];
					break;
				}
			}
		}
		
		Debt debt = null;
		if (message != "") {
			debt = new Debt(schuldnerId, glaubigerId, Float.parseFloat(message));
		}
		
		close();
		return debt;
	}
	
	public static float getTotalDebt(int userId) throws UnknownHostException, IOException, FunctionNotDefinedException, NoneFoundException{
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("getTotalDebt("+ userId +")");
		}else {
			User[] userList = getNameList();
			float totalDebt = 0;
			for (User u: userList) {
				if (userId != u.getId()) {
					totalDebt += getDebt(userId, u.getId()).getBetrag();
				}
			}
			message = Float.toString(totalDebt);
		}
		
		close();
		return Float.parseFloat(message);
	}
	
	public static Einkauf[] getEinkaufe(int einkauferId) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("getEinkaufe("+ einkauferId +")");
		}else {
			String[] einkaufeStrings = einkaufString.split(";");
			for (String einkauf: einkaufeStrings) {
				String[] e = einkauf.split(",");
				if (Integer.parseInt(e[1]) == einkauferId) {
					message = (message != "") ? message + ";" : message;
				message = message + einkauf;	
				}
			}
		}
		
		String[] einkaufeStrings = message.split(";");
		Einkauf[] einkaufe = new Einkauf[einkaufeStrings.length];
		for (int i = 0; i<einkaufe.length; i++) {
			String[] e = einkaufeStrings[i].split(",");
			einkaufe[i] = new Einkauf(Integer.parseInt(e[0]), Integer.parseInt(e[1]), e[2], e[3]);
		}
		
		close();
		return einkaufe;
	}
	
	public static EinkaufPart[] getPartsForUser(int gastId) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("getPartsForUser("+ gastId +")");
		}else {
			String[] partsStrings = einkaufPartString.split(";");
			for (String part: partsStrings) {
				String[] e = part.split(",");
				if (Integer.parseInt(e[1]) == gastId) {
					message = (message != "") ? message + ";" : message;
				message = message + part;	
				}
			}
		}
		
		String[] partsStrings = message.split(";");
		EinkaufPart[] parts = new EinkaufPart[partsStrings.length];
		for (int i = 0; i<parts.length;i++) {
			String[] e = partsStrings[i].split(",");
			parts[i] = new EinkaufPart(Integer.parseInt(e[0]), Integer.parseInt(e[1]), Float.parseFloat(e[2]), e[3]);
		}
		
		close();
		return parts;
	}
	
	public static EinkaufPart[] getPartsForEinkauf(int einkaufId) throws UnknownHostException, IOException, FunctionNotDefinedException {
		initialize(ip, port);
		String message= "";
		if (!offline) {
			message = sendFunction("getPartsForEinkauf("+ einkaufId +")");
		}else {
			String[] partsStrings = einkaufPartString.split(";");
			for (String part: partsStrings) {
				String[] e = part.split(",");
				if (Integer.parseInt(e[0]) == einkaufId) {
					message = (message != "") ? message + ";" : message;
				message = message + part;	
				}
			}
		}
		
		String[] partsStrings = message.split(";");
		EinkaufPart[] parts = new EinkaufPart[partsStrings.length];
		for (int i = 0; i<parts.length;i++) {
			String[] e = partsStrings[i].split(",");
			parts[i] = new EinkaufPart(Integer.parseInt(e[0]), Integer.parseInt(e[1]), Float.parseFloat(e[2]), e[3]);
		}
		
		close();
		return parts;
	}

	private static void close() throws IOException {
		if (!offline) {
			client_socket.close();
		}
	}
	

}
