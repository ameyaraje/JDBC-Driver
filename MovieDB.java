/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package moviedb;

import java.sql.*;
import java.util.*;
//import com.mysql.jdbc.Driver;

public class MovieDB {
    public static Scanner input = null;
    public static Connection connection = null;
    public static Statement select = null;
    public static String dbusername = "mytestuser";
    public static String dbpassword = "mypassword";
    public static String database = "jdbc:mysql://localhost:3306/moviedb";
    /**
     * @param args the selection line arguments
     */
    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        int menu_selection;
        
        // User login
        login();

        boolean done = false;
        do{
            // Prompt user
            menu_selection = menuSelection();

            switch(menu_selection){
                case 1:
                    searchMoviesByStar();
                    break;

                case 2:
                    addNewStar();
                    break;

                case 3:
                    addNewCustomer();
                    break;

                case 4:
                    // Delete Existiting Customer  
                    deleteExistingCustomer();
                    break;

                case 5:
                    // Get Metadata
                    getMetadata();
                    break;

                case 6:
                    // SQL Query
                    sqlQuery();
                    break;

                case 7:
                    logout();
                    break;

                case 8:
                    // Quit Program
                    done = true;
                    quit();
                default:
                    // Menu selection was not valid
                    println("Please Try Again.");
                    break;
            }
        }while(!done);

        input.close(); // Close input stream
        connection.close();
        System.exit(0);
    }
    public static void login() throws SQLException{
        String username, password;
        //Login
        input = new Scanner(System.in); // open input stream
        println("Welcome to FabFlix!");
        print("Please enter your username: ");
        username = input.nextLine().trim();
        print("Please enter your password: ");
        password = input.nextLine().trim();
        int attempts = 0;
        
        // Validate user input
        while(!username.equals(dbusername) || !password.equals(dbpassword)){
            if(attempts++ < 5){
                println("Wrong username and/or password! Please Try Again.");
                print("Please enter your username: ");
                username = input.nextLine().trim();
                print("Please enter your password: ");
                password = input.nextLine().trim();
            }else{
                println("Failed connection: too many attempts to login. Goodbye!");
                System.exit(0);
            }
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = (Connection)DriverManager.getConnection(database,username,password);
        }
        catch (Exception e) {
            println("\nERROR: DATABASE NOT FOUND!\nPlease make sure server is running.\n");
//            e.printStackTrace();
            login();
        }
        
        
        println("\nHello " + username + "!");
    }
    
    public static int menuSelection(){
        int selection = -1;
        println(
            "\nPlease Make A Selection:\n"
            +"1 - Movies Featuring Star\n"
            +"2 - Add New Star\n"
            +"3 - Add New Customer\n"
            +"4 - Delete Existiting Customer\n"
            +"5 - Get Metadata\n"
            +"6 - SQL Query\n"
            +"7 - Logout\n"
            +"8 - Quit FabFlix"
        );
        print("Select A Number: ");
        // Temp variable for checking correct input type
        String temp = input.nextLine().trim();
        // Check input type
        while(selection == -1){
            if(temp.matches("[0-9]+")){
                selection = Integer.parseInt(temp);
                break;
            }else{
                println("Please Enter A Number: ");
                temp = input.nextLine().trim();
            }
        }
        return selection;
    }
    
    public static void searchMoviesByStar() throws SQLException{
        println("\nMovies Featuring Star:\n1 - Search By Star's Name\n2 - Search By Star's ID");
        print("Select A Number: ");
        int menu_selection = -1;
        String temp = "";
        temp = input.nextLine().trim();
        // Check user input is either 1 or 2
        while(menu_selection == -1){
            if(temp.matches("[1-2]+")){
                menu_selection = Integer.parseInt(temp);
                break;
            }else{
                println("Please Select 1, or 2: ");
                temp = input.nextLine().trim();
            }
        }
        
        switch(menu_selection){
            case 1:
                String firstname = "", lastname = "";
                println("\nSearch By Star's First and/ or Last Name");
                print("Enter First Name: ");
                firstname = input.nextLine().trim();
                print("Enter Last Name: ");
                lastname = input.nextLine().trim();
                if(firstname.equals("") && lastname.equals("")){ // User didn't input any info
                    println("Error (Search Movies Featuring Star By Name): No First and/or Last Name Entered.");
                    break;
                }else if(!firstname.equals("") && !lastname.equals("")){
                    queryMovieByStarFullName(firstname,lastname);
                }else if(firstname.equals("")){
                    queryMovieByStarLastName(lastname);
                }else if(lastname.equals("")){
                    queryMovieByStarFirstName(firstname);
                }
                break;
            case 2:
                temp = "";
                int id = -1;
                println("\nSearch By Star's ID Number");
                print("Enter ID Number: ");
                temp = input.nextLine().trim();
                
                // Validate user input
                while(id == -1){
                    if(temp.matches("[0-9]+")){
                        id = Integer.parseInt(temp);
                        queryMovieByStarID(id);
                    }else{
                        print("Please Enter Star's ID Number: ");
                        temp = input.nextLine().trim();   
                    }
                }
                break;
        }
    }
    
    public static void queryMovieByStarFullName(String firstname,String lastname) throws SQLException{
        select = connection.createStatement();
        String sql = "SELECT DISTINCT movies.id, movies.title "
                + "FROM movies "
                + "JOIN stars_in_movies ON movies.id = stars_in_movies.movie_id "
                + "JOIN stars ON stars.id = stars_in_movies.star_id "
                + "WHERE stars.first_name LIKE '" + firstname + "%' AND stars.last_name LIKE '" + lastname + "%'";
        Statement select = connection.createStatement();
        ResultSet result = select.executeQuery(sql);
        printMovieResults(result);
    }
    
    public static void queryMovieByStarFirstName(String firstname) throws SQLException{
        select = connection.createStatement();
        String sql = "SELECT DISTINCT movies.id, movies.title "
                + "FROM movies "
                + "JOIN stars_in_movies ON movies.id = stars_in_movies.movie_id "
                + "JOIN stars ON stars.id = stars_in_movies.star_id "
                + "WHERE stars.first_name LIKE '" + firstname + "%'";
        Statement select = connection.createStatement();
        ResultSet result = select.executeQuery(sql);
        printMovieResults(result);
    }
    
    public static void queryMovieByStarLastName(String lastname) throws SQLException{
        select = connection.createStatement();
        String sql = "SELECT DISTINCT movies.id, movies.title "
                + "FROM movies "
                + "JOIN stars_in_movies ON movies.id = stars_in_movies.movie_id "
                + "JOIN stars ON stars.id = stars_in_movies.star_id "
                + "WHERE stars.last_name LIKE '" + lastname + "%'";
        Statement select = connection.createStatement();
        ResultSet result = select.executeQuery(sql);
        printMovieResults(result);
    }
    
    public static void queryMovieByStarID(int id) throws SQLException{
        select = connection.createStatement();
        String sql = "SELECT movies.id, movies.title "
                + "FROM movies "
                + "JOIN stars_in_movies ON movies.id = stars_in_movies.movie_id "
                + "JOIN stars ON stars.id = stars_in_movies.star_id "
                + "WHERE stars.id = ?";
        // Use prepared statement to handle integer
        PreparedStatement pStatement = connection.prepareStatement(sql);
        pStatement.setInt(1,id);
        ResultSet results = pStatement.executeQuery();
        printMovieResults(results);
    }
    
    public static void printMovieResults(ResultSet results) throws SQLException{
        if (!results.isBeforeFirst() ) {    
            System.out.println("No results found. Please try again.");
        }else{
            int count = 1;
            while (results.next()){
                print(count + ". Movie (" + results.getInt("id") + "): \"" + results.getString("title") + "\"\n");
                count++;
            }
        }
    }
    
    public static void addNewStar() throws SQLException{
        select = connection.createStatement();
        
        String firstname = "", lastname = "", selection = "";
        
        println("\nInsert a New Star");
        print("Enter first or only name: ");
        firstname = input.nextLine().trim();
        print("Enter last name (press return for no last name): ");
        lastname = input.nextLine().trim();
        
        // Check to see if Star exists
        String query = "SELECT COUNT(*) AS count "
                     + "FROM stars "
                     + "WHERE ";
        if (!lastname.equals(""))
            query += " first_name = '" + firstname + "' AND last_name = '" + lastname + "'";
        else
            query += " first_name = '" + lastname + "' AND last_name = '" + firstname + "'";
        
        ResultSet count = select.executeQuery(query);
        count.next();
        if (count.getInt("count") != 0) {
            do {
                String exists = "Star " + firstname + " " + lastname + " exists in database! Add anyway [y/n]? ";
                print(exists);
                selection = input.nextLine().trim();
                if (selection.equals("n"))
                    addNewStar();
                if (!selection.matches("y|n"))
                    println("Error, please input 'y' or 'n'");
            } while (!selection.matches("y|n"));
        }
        
        // Insert Star
        String sql = "INSERT INTO stars(first_name, last_name) ";
        if (!lastname.equals(""))
            sql += "VALUES ('" + firstname + "','" + lastname + "');";
        else
            sql += "VALUES ('" + lastname + "','" + firstname + "');";
        
        int result = select.executeUpdate(sql);  
        if (result != 0)
            print("\nStar added successfully.\nAdd another star [y/n]? ");
        else
            print("\nError: Star not added successfully.\nAdd another star [y/n]? ");

        selection = input.nextLine().trim();
        do {
            if (selection.equals("y")){
                addNewStar();
            }else if (selection.equals("n")){
                break;
            }else{
                println("Error, please input 'y' or 'n'");
            }
        } while (!selection.matches("y|n")); 
        
    }
    
    public static void addNewCustomer() throws SQLException {
        select = connection.createStatement();
        
        String firstname = "", lastname = "", ccid = "",
               address = "", email = "", pw = "";
        
        println("Add New Customer");
//        do {
            print("Enter first name: ");
            firstname = input.nextLine().trim();
            print("Enter last name: ");
            lastname = input.nextLine().trim();
            print("Enter credit card id: ");
            ccid = input.nextLine().trim();
            if (!isInt(ccid)) {
                println("Only numbers are valid for credit cards!");
                addNewCustomer();
            }
            print("Enter address: ");
            address = input.nextLine().trim();
            print("Enter email: ");
            email = input.nextLine().trim();
            print("Enter password: ");
            pw = input.nextLine().trim();
         
//            if (firstname.equals("") || ccid.equals("") || 
//                  address.equals("") || email.equals("") || pw.equals(""))
//                println("ERROR: All fields except for lastname are required");
//                      
//        } while(!(firstname.equals("") || ccid.equals("") || 
//                  address.equals("") || email.equals("") || pw.equals("")));
                
        // Ensure ccid exists in creditcards bank table
        String query = "SELECT COUNT(*) AS count " 
                     + "FROM creditcards "
                     + "WHERE id = " + ccid;
        
        ResultSet count = select.executeQuery(query);
        count.next();
        if (count.getInt("count") == 0) {
            print("ERROR: Customer credit card is invalid.\n");
            addNewCustomer();
        }
        
        String sql = "INSERT INTO customers(first_name, last_name, cc_id, address, email, password) ";
        if (!lastname.equals(""))
            sql += "VALUES ('" + firstname + "', '" + lastname + "', '" + ccid + "', '" 
                               + address + "', '" + email + "', '" + pw + "')";
                               
        else
            sql += "VALUES ('" + lastname + "', '" + firstname + "', '" + ccid + "', '" 
                               + address + "', '" + email + "', '" + pw + "')";
        
        //////////////////////////////////////////
        // TODO 
        // create customer login account?
        //////////////////////////////////////////
        int result = select.executeUpdate(sql);  
        if (result != 0)
            print("\nCustomer added successfully.\nAdd another customer [y/n]? ");
        else
            print("\nError: Customer not added successfully.\nAdd another customer [y/n]? ");
        
        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y"))
                addNewCustomer();         
            else if (!selection.equals("n"))
                println("Error, please input 'y' or 'n'");
        } while (!selection.matches("y|n"));     
    }
    
    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static void deleteExistingCustomer() throws SQLException {
        select = connection.createStatement();
        
        println("\nDelete Existing Customer");
        print("Please enter the customer ID: ");
        String id = input.nextLine().trim();
        if (!isInt(id)) {
                println("ERROR: Only numbers are valid for customer ID!");
                deleteExistingCustomer();
            }
        
        String sql = "DELETE FROM customers "
                   + "WHERE id = " + id;
        
        int success = select.executeUpdate(sql);
        
        if (success != 0)
            print("Customer " + id + " deleted.\nDelete another customer [y/n]? ");
        else
            print("Customer " + id + " does not exist.\nTry again [y/n]? ");

        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y"))
                deleteExistingCustomer();
            else if (!selection.equals("n"))
                println("Error, please input 'y' or 'n'");
        } while (!selection.matches("y|n")); 
        
    }
    
    // Gets and outputs metadata
    public static void getMetadata() throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        String[] type = {"TABLE"};
        ResultSet results = metadata.getTables(null, "%", "%", type);
        List<String> tables = new ArrayList<>();

        while(results.next()){
        tables.add(results.getString("TABLE_NAME"));
        }
        println("\nThe Movie Database has " + tables.size() + " tables:");
        int count = 1;
        for (String table : tables){
            StringBuilder sb = new StringBuilder();
            
            // e.g. tablename(field1:type,field2:type,...fieldn:type)
            sb.append(table + "(");
            ResultSet fields = metadata.getColumns(null, "%", table, "%");
            while(fields.next()){
                // e.g. "id:INT"
                sb.append(fields.getString("COLUMN_NAME") + ":" + fields.getString("TYPE_NAME"));
                if(!fields.next()){ // Check next if last, then field followed by closing paren
                    sb.append(")");
                    fields.previous();
                }else{              // Check next if last, if not, then fields separated by comma
                    sb.append(", ");
                    fields.previous();
                }
            }
            println(count + ". " + sb.toString());
            count++;
    }
    }
    
    public static void sqlQuery() throws SQLException {
        
        try{
            println("Execute SQL Query.");
        println("Please enter your SQL query below: ");
        
        String sql = input.nextLine().trim();
        
        if (sql.contains("INSERT") || sql.contains("insert")) 
            insertQuery(sql);
        
        if (sql.contains("UPDATE")|| sql.contains("update"))
            updateQuery(sql);
        
        if (sql.contains("DELETE")|| sql.contains("delete"))
            deleteQuery(sql);
        
        if (sql.contains("SELECT")|| sql.contains("select"))
            printQuery(sql);
        
        }
        catch(SQLException e)
        {
            System.out.println(e);
            
        }
    }
    
    // Inserts record from given SQL query
    public static void insertQuery(String sql) throws SQLException {
        select = connection.createStatement();
        int result = select.executeUpdate(sql);  
        if (result != 0)
            print("\nAdded successfully.\nExecute another query? [y/n]? ");
        else
            print("\nError: Not added successfully.\nExecute another query? [y/n]? ");

        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y")){
                sqlQuery();
            }else if (selection.equals("n")){
                break;
            }else{
                println("Error, please input 'y' or 'n'");
                selection = input.nextLine().trim();
            }
        } while (!selection.matches("y|n"));      
    }
    
    // Updates tables from given SQL query
    public static void updateQuery(String sql) throws SQLException {
        select = connection.createStatement();
        int rows = select.executeUpdate(sql);
        
        if (rows != 0)
            print(rows + " records successfully updated.\nExecute another query [y/n]? ");
        else
            print("No records found.\nExecute another query [y/n]? ");
        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y")){
                sqlQuery();
            }else if (selection.equals("n")){
                break;
            }else{
                println("Error, please input 'y' or 'n'");
            }
        } while (!selection.matches("y|n"));
        
    }
    
    // Delete record from given SQL query
    public static void deleteQuery(String sql) throws SQLException {
        select = connection.createStatement();
        int rows = select.executeUpdate(sql);
        
        if (rows != 0)
            print(rows + " records successfully deleted.\nExecute another query [y/n]? ");
        else
            print("No records found.\nExecute another query [y/n]? ");
        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y")){
                sqlQuery();
            }else if (selection.equals("n")){
                break;
            }else{
                println("Error, please input 'y' or 'n'");
            }
        } while (!selection.matches("y|n"));      
    }
    
    // Prints records from a given SQL query
    public static void printQuery(String sql) throws SQLException {
        select = connection.createStatement();
        ResultSet results = select.executeQuery(sql);
        ResultSetMetaData data = results.getMetaData();
        int columns = data.getColumnCount(), count = 0;
        
        if (!results.isBeforeFirst() ) {    
            System.out.println("No results found. Execute another query [y/n]?");
        }
        else
            while (results.next()) {
                for (int i = 1; i <= columns; ++i) {
                    String cv = results.getString(i);
                    print(data.getColumnName(i) + ": " + cv + "\t");
                }
                print("\n"); 
            }
        
        
        println("Execute another query [y/n]?");
         
        String selection = input.nextLine().trim();
        do {
            if (selection.equals("y")){
                sqlQuery();
            }else if (selection.equals("n")){
                break;
            }else{
                println("Error, please input 'y' or 'n'");
            }
        } while (!selection.matches("y|n"));        
    }
    
    // Logout
    public static void logout() throws SQLException{
        println("You have successfully logged out!\n");
        login();
    }
    
    public static void quit() throws SQLException{ 
        input.close();
        connection.close();
        System.exit(0);
        
    }
        
    // Prints with newline
    public static void println(Object o){
        System.out.println(o);
    }
    
    // Prints without newline
    public static void print(Object o){
        System.out.print(o);
    }
}
