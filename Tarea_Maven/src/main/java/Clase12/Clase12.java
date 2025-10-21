package Clase12;

import java.sql.*;

public class Clase12 {
    public static void main(String[] args) {
        String url = "jdbc:mariadb://localhost:3307/demo_db";
        String user = "root";
        String password = "Dauner6$";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false); // Para control de transacciones

            // 1. Insertar estudiantes
            insertarEstudiantes(conn);

            // 2. Asignar cursos
            asignarCursos(conn);

            // 3. Listar cursos asignados
            listarCursosAsignados(conn);

            // 4. Actualizar horario
            actualizarHorario(conn, 1, "Lunes 10:00-12:00");

            // 5. Eliminar estudiante y sus cursos asignados
            eliminarEstudiante(conn, 3);

            conn.commit(); // Confirmar todos los cambios
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertarEstudiantes(Connection conn) throws SQLException {
        String sql = "INSERT INTO estudiantes (id_estudiante, nombre_estudiante) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Ana López");
            stmt.executeUpdate();

            stmt.setInt(1, 2);
            stmt.setString(2, "Carlos Pérez");
            stmt.executeUpdate();

            stmt.setInt(1, 3);
            stmt.setString(2, "María Gómez");
            stmt.executeUpdate();

            System.out.println("✅ Estudiantes insertados.");
        }
    }

    private static void asignarCursos(Connection conn) throws SQLException {
        String sql = "INSERT INTO cursos_asignados (id_estudiante, nombre_curso, horario) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Ana
            stmt.setInt(1, 1);
            stmt.setString(2, "Matemática");
            stmt.setString(3, "Lunes 8:00-10:00");
            stmt.executeUpdate();

            stmt.setInt(1, 1);
            stmt.setString(2, "Física");
            stmt.setString(3, "Miércoles 10:00-12:00");
            stmt.executeUpdate();

            // Carlos
            stmt.setInt(1, 2);
            stmt.setString(2, "Química");
            stmt.setString(3, "Martes 9:00-11:00");
            stmt.executeUpdate();

            stmt.setInt(1, 2);
            stmt.setString(2, "Historia");
            stmt.setString(3, "Jueves 13:00-15:00");
            stmt.executeUpdate();

            // María
            stmt.setInt(1, 3);
            stmt.setString(2, "Biología");
            stmt.setString(3, "Viernes 10:00-12:00");
            stmt.executeUpdate();

            stmt.setInt(1, 3);
            stmt.setString(2, "Literatura");
            stmt.setString(3, "Lunes 14:00-16:00");
            stmt.executeUpdate();

            System.out.println("✅ Cursos asignados.");
        }
    }

    private static void listarCursosAsignados(Connection conn) throws SQLException {
        String sql = """
            SELECT e.id_estudiante, e.nombre_estudiante, c.nombre_curso, c.horario
            FROM estudiantes e
            JOIN cursos_asignados c ON e.id_estudiante = c.id_estudiante
            ORDER BY e.id_estudiante
            """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📋 Cursos Asignados:");
            while (rs.next()) {
                int id = rs.getInt("id_estudiante");
                String nombre = rs.getString("nombre_estudiante");
                String curso = rs.getString("nombre_curso");
                String horario = rs.getString("horario");

                System.out.printf("ID: %d | Nombre: %s | Curso: %s | Horario: %s%n",
                        id, nombre, curso, horario);
            }
        }
    }

    private static void actualizarHorario(Connection conn, int idAsignacion, String nuevoHorario) throws SQLException {
        String sql = "UPDATE cursos_asignados SET horario = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoHorario);
            stmt.setInt(2, idAsignacion);
            int filas = stmt.executeUpdate();
            System.out.println("🛠️ Horario actualizado para ID asignación " + idAsignacion + ": " + (filas > 0 ? "OK" : "No encontrado"));
        }
    }

    private static void eliminarEstudiante(Connection conn, int idEstudiante) throws SQLException {
        // Primero eliminar cursos asignados
        String deleteCursos = "DELETE FROM cursos_asignados WHERE id_estudiante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteCursos)) {
            stmt.setInt(1, idEstudiante);
            stmt.executeUpdate();
        }

        // Luego eliminar estudiante
        String deleteEstudiante = "DELETE FROM estudiantes WHERE id_estudiante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteEstudiante)) {
            stmt.setInt(1, idEstudiante);
            stmt.executeUpdate();
        }

        System.out.println("🗑️ Estudiante con ID " + idEstudiante + " y sus cursos fueron eliminados.");
    }
}
