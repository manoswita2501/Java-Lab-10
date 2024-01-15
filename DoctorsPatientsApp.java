import java.sql.*;

public class DoctorsPatientsApp {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/doctors_patients";
    private static final String USERNAME = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                createTables(connection);

                // Insert sample data
                insertDoctor(connection, "Dr. Smith");
                insertDoctor(connection, "Dr. Johnson");

                insertPatient(connection, "John Doe", 1); // Assigning patient to Dr. Smith
                insertPatient(connection, "Jane Doe", 2); // Assigning patient to Dr. Johnson

                // Display doctors and their patients
                displayDoctorsWithPatients(connection);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create doctors table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS doctors (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))");

            // Create patients table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS patients (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), doctor_id INT, FOREIGN KEY (doctor_id) REFERENCES doctors(id))");
        }
    }

    private static void insertDoctor(Connection connection, String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO doctors (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            System.out.println("Doctor '" + name + "' added successfully.");
        }
    }

    private static void insertPatient(Connection connection, String name, int doctorId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO patients (name, doctor_id) VALUES (?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.executeUpdate();
            System.out.println("Patient '" + name + "' added successfully.");
        }
    }

    private static void displayDoctorsWithPatients(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM doctors");

            while (resultSet.next()) {
                int doctorId = resultSet.getInt("id");
                String doctorName = resultSet.getString("name");
                System.out.println("Doctor: " + doctorName);

                // Fetch patients for each doctor
                ResultSet patientsResultSet = statement.executeQuery("SELECT * FROM patients WHERE doctor_id = " + doctorId);
                while (patientsResultSet.next()) {
                    String patientName = patientsResultSet.getString("name");
                    System.out.println("  Patient: " + patientName);
                }
                patientsResultSet.close();
            }
        }
    }
}
