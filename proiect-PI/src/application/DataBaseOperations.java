package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.h2.store.Data;

public class DataBaseOperations {
	private static String dbUser = "sa";
	private static String dbPass = "";
	
	public static void openConnection() {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkConnection() {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
		//	MainMenu.noConnectionLabel.setVisible(false);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			MainMenu.noConnectionLabel.setVisible(true);
			MainMenu.noConnectionLabel.setText("Nu exista conexiune spre baza de date");
			return false;
		}
	}
	
	public static boolean isNameDifferent(String title) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			String querry="select  title from ITEMLIST where title='"+ title +"'";
			Statement st=conn.createStatement();
		//	st.setString(1, title);
			ResultSet rs=st.executeQuery(querry);
			if(rs.next()==false) {
				//System.out.println("Nume ok");
				return true;
				
			}
			else {
				System.out.println("Alege alt nume");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static void main(String args[]) {
		openConnection();
		checkConnection();
	}
	
	public static void add(Item a) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			PreparedStatement st = conn.prepareStatement("INSERT INTO ITEM(NAME,ITEMTYPE) VALUES (?,?)");
			//st.setInt(1, a.getId());
			st.setString(1, a.getName());
			st.setString(2, a.getItemType().toString());
			//st.setString(4, "Article");
			st.execute();
			System.out.println("Produs adaugat");
			
		} catch (SQLException e) {
			System.out.println("Nu s-a adaugat produsul");
			e.printStackTrace();
		}
	}
	public static void addItemTitle(String a) {
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			PreparedStatement st = conn.prepareStatement("INSERT INTO ITEMLIST(TITLE) VALUES (?)");
			st.setString(1, a);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
public static void createList(List<Item>a,String title) {
	Connection conn;
	try {
		conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
		PreparedStatement pst=conn.prepareStatement("INSERT INTO ITEMLIST(TITLE) values(?)");
		pst.setString(1, title);
		pst.executeUpdate();
		String querry="select ID from ITEMLIST order by 1 desc limit 1";
		Statement st1=conn.createStatement();
		ResultSet rs=st1.executeQuery(querry);
		int id=0;
		while(rs.next()) {
			 id=rs.getInt("ID");
		}
		for(Item i:a) {
			PreparedStatement st = conn.prepareStatement("INSERT INTO ITEM_TO_ITEM_LIST(ITEM_ID,ITEM_LIST_ID) values(?,?)");
			st.setInt(1, i.getId());
			st.setInt(2, id);
			st.executeUpdate();
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	}
 public static void addIntoTableFinalList(FinalList finalList,String title) {
	 Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			String querry="select id  from itemlist where TITLE = '"+ title +"'";
			Statement st = conn.createStatement();
			ResultSet rs=st.executeQuery(querry);
			int id=1;
			while(rs.next())
				id=rs.getInt(id);
			st.close();
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			PreparedStatement pst=conn.prepareStatement("INSERT INTO FINAL_LIST(ID_LIST,PRICE,DATE) values(?,?,?)");
			pst.setFloat(1, id);
			pst.setDouble(2, finalList.getPrice());
			java.sql.Date date = java.sql.Date.valueOf(finalList.getDate());
			pst.setDate(3, date);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
 }
	public static void addItems(Item a) {
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			PreparedStatement st = conn.prepareStatement("INSERT INTO ITEMLIST(TITLE) VALUES (?)");
			//st.setString(1, a);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public static List<Item> listItem() {
		try {
		List<Item> ItemList=new ArrayList<>();
		Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
		String querry="select * from ITEM";
		Statement st=conn.createStatement();
		ResultSet rs=st.executeQuery(querry);
		while(rs.next()) {
			int id=rs.getInt("ID");
			String Name=rs.getString("NAME");
			String ItemType=rs.getString("ITEMTYPE");
			Item a=new Item(id,Name,CreateItemMenu.toItemType(ItemType));
			ItemList.add(a);
		}
		st.close();
		return ItemList;
		} catch (SQLException e) {
			System.out.println("Nu s-a adaugat produsul");
			e.printStackTrace();
		}
		return null;
		
	}
	public static boolean itemAlreadyExists(Item a) {
		for(Item i :DataBaseOperations.listItem())
		{
			if(i.getName().equals(a.getName()) && i.getItemType().toString().equals(a.getItemType().toString()))
				return true;
		}
		return false;
		
	}
	public static void delete(String s) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			String querry="delete from item where NAME = '"+ s +"'";
			Statement pst = conn.createStatement();
			//pst.setString(1, s);
			pst.executeUpdate(querry);
			//ResultSet rs=pst.executeQuery();
			pst.close();
			} catch (SQLException e) {
				System.out.println("Nu s-a adaugat produsul");
				e.printStackTrace();
			}
	}
	public static List<Integer> listItemID(Item item) {
		List<Integer> list=new ArrayList<>();
		for(Item i:DataBaseOperations.listItem())
			list.add(i.getId());
		return list;
			
		}
	
	public static Map<String,List<Item>> getAllListsForExport(){
		//	List<ItemList> lista=new ArrayList<>();
			HashMap<String,List<Item>> map=new HashMap<>();
			try {
				Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
				String querry="Select  item.id, item.name, item.itemtype, ITIL.ITEM_LIST_ID, list.title   from item inner join  item_to_item_list ITIL on item.id=ITIL.item_id inner join itemlist list on list.id = ITIL.ITEM_LIST_ID where ITIL.item_list_id=any (select ID from ITEMLIST)";
				Statement pst = conn.createStatement();
				ResultSet rs=pst.executeQuery(querry);
				int idList=0;
				int idItem=0;
				String numeItem;
				String itemType;
				String numeList;
				Item object=new Item();
				while(rs.next()) {
					 idList=rs.getInt("ITEM_LIST_ID");
					 idItem=rs.getInt("ID");
					 numeItem=rs.getString("NAME");
					 itemType=rs.getString("ITEMTYPE");
					 numeList=rs.getString("TITLE");
					 object=new Item(idItem,numeItem,CreateItemMenu.toItemType(itemType));
					 if(map.containsKey(numeList)) {
						 List<Item> list=map.get(numeList);
						 list.add(object);
						 map.put(numeList,list);
					 }
					 else {
							List<Item> lista=new ArrayList<>();
							lista.add(object);
							map.put(numeList,lista);	
					 }
						
				}
				/*for(Item i:a) {
					PreparedStatement st = conn.prepareStatement("INSERT INTO ITEM_TO_ITEM_LIST(ITEM_ID,ITEM_LIST_ID) values(?,?)");
					st.setInt(1, i.getId());
					st.setInt(2, id);
					st.executeUpdate();
				}
				//pst.executeUpdate(querry);
				*/
				//map.forEach((k, v) -> System.out.println(k + " " + v)); 
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return map;
			}
		

	
	
	
	
	
	
	public static Map<String,List<Item>> getAllListsWithItems(){
	//	List<ItemList> lista=new ArrayList<>();
		HashMap<String,List<Item>> map=new HashMap<>();
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			String querry="Select  item.id, item.name, item.itemtype, ITIL.ITEM_LIST_ID, list.title   from item inner join  item_to_item_list ITIL on item.id=ITIL.item_id inner join itemlist list on list.id = ITIL.ITEM_LIST_ID where ITIL.item_list_id=any (select ID from ITEMLIST) AND list.id NOT IN (SELECT ID_LIST FROM FINAL_LIST)";
			Statement pst = conn.createStatement();
			ResultSet rs=pst.executeQuery(querry);
			int idList=0;
			int idItem=0;
			String numeItem;
			String itemType;
			String numeList;
			Item object=new Item();
			while(rs.next()) {
				 idList=rs.getInt("ITEM_LIST_ID");
				 idItem=rs.getInt("ID");
				 numeItem=rs.getString("NAME");
				 itemType=rs.getString("ITEMTYPE");
				 numeList=rs.getString("TITLE");
				 object=new Item(idItem,numeItem,CreateItemMenu.toItemType(itemType));
				 if(map.containsKey(numeList)) {
					 List<Item> list=map.get(numeList);
					 list.add(object);
					 map.put(numeList,list);
				 }
				 else {
						List<Item> lista=new ArrayList<>();
						lista.add(object);
						map.put(numeList,lista);	
				 }
					
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		}
	public static HashMap<String, Double> extractDataFromFinalList() {
		FinalList finalList=new FinalList();
		HashMap<String, Double> map=new HashMap<>();
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",dbUser,dbPass);
			String querry="Select  date,price from Final_List  where  MONTH(CURDATE())=extract(month from date)";
			Statement pst = conn.createStatement();
			ResultSet rs=pst.executeQuery(querry);
			Date data;
			double price=0;
			while(rs.next()) {
			data= rs.getDate("Date");
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			String strDate=sdf.format(data);
			//System.out.println(strDate);
			//LocalDate date = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			//LocalDate d=Date.valueOf(data);
			//LocalDate.of(data.getYear(), data.getMonth()+1, data.getDate());
			price=rs.getDouble("price");
			map.put(strDate,Double.valueOf(price));
			}
		//System.out.println(map.toString());
		return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		
		
	}

}
