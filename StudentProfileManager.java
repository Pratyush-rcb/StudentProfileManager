import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class StudentProfileManager {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pk";
        String user = "";
        String password = "";

        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);

            while (true) {
                System.out.println("1. Add Student");
                System.out.println("2. Update Student");
                System.out.println("3. Delete Student");
                System.out.println("4. View Student");
                System.out.println("5. View All Students");
                System.out.println("6. Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addStudent(con, sc);
                    case 2 -> updateStudent(con, sc);
                    case 3 -> deleteStudent(con, sc);
                    case 4 -> viewStudent(con, sc);
                    case 5 -> viewAllStudents(con);
                    case 6 -> {
                        System.out.println("Exiting...");
                        con.close();
                        sc.close();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Adding students

    public static void addStudent(Connection con, Scanner sc) throws Exception{
        
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Image Path: ");
        String imgPath = sc.nextLine();

        String query="insert into students(id, name, email, photo) values(?,?,?,?)";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1,id);
        pstmt.setString(2,name);
        pstmt.setString(3,email);

        FileInputStream fis = new FileInputStream(imgPath);
        pstmt.setBlob(4, fis);

        int res = pstmt.executeUpdate();

        if(res>0) System.out.println("student added");
        else System.out.println("student not added");

    }
// updating Details
    
    public static void updateStudent(Connection con, Scanner sc) throws Exception{
        
        System.out.print("Enter ID to updarte: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Update Name? (y/n): ");
        String upName = sc.nextLine();
        String name = null;

        if(upName.equalsIgnoreCase("y")) {
            System.out.print("Enter new name: ");
            name = sc.nextLine();
        }

        System.out.print("Update email? (y/n): ");
        String upEmail=sc.nextLine();
        String email=null;

        if(upEmail.equalsIgnoreCase("y")){
            System.out.print("Enter new email: ");
            email = sc.nextLine();
        }

        System.out.print("Update Image? (y/n): ");
        String upImage = sc.nextLine();

        if(name!=null){
            String query="update students set name=? where id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,name);
            ps.setInt(2,id);
            ps.executeUpdate();
            ps.close();
        }
        if(email!=null){
            String query="update students set email=? where id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,email);
            ps.setInt(2,id);
            ps.executeUpdate();
            ps.close();
        }

        if(upImage.equalsIgnoreCase("y")) {
            System.out.print("Enter new Image Path: ");
            String imgPath = sc.nextLine();
            String query = "UPDATE students SET photo=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);

            FileInputStream fis = new FileInputStream(imgPath);
            ps.setBlob(1, fis);
            ps.setInt(2, id);
            ps.executeUpdate();
            fis.close();
            ps.close();
        }

        System.out.println("Update completed!");
    }
    
//Delete Students Detail
    
    public static void deleteStudent(Connection con, Scanner sc)throws Exception{
        
        System.out.println("Enter id to delete:");
        int id=sc.nextInt();
        sc.nextLine();

        String query="delete from students where id=?";
        PreparedStatement ps=con.prepareStatement(query);
        ps.setInt(1,id);

        int res=ps.executeUpdate();
        if(res > 0) System.out.println("Student deleted successfully!");
        else System.out.println("No student found with given ID.");

        ps.close();

    }
    // view Details of Students
    
    public static void viewStudent(Connection con, Scanner sc) throws Exception {
        System.out.print("Enter ID to view: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "SELECT * FROM students WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Email: " + rs.getString("email"));

            Blob blob = rs.getBlob("photo");
            if(blob != null) {
                byte[] imgBytes = blob.getBytes(1, (int)blob.length());
                FileOutputStream fos = new FileOutputStream("student_" + id + "_photo.jpg");
                fos.write(imgBytes);
                fos.close();
                System.out.println("Image saved as student_" + id + "_photo.jpg");
            } else {
                System.out.println("No image found.");
            }
        } else {
            System.out.println("Student not found.");
        }

        rs.close();
        ps.close();
    }

    // View all students (without images)
    
    public static void viewAllStudents(Connection con) throws Exception {
        
        String query = "SELECT id, name, email FROM students";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("All Students:");
        while(rs.next()) {
            System.out.println(rs.getInt("id") + " | " + rs.getString("name") + " | " + rs.getString("email"));
        }

        rs.close();
        stmt.close();
    }
}

