import java.sql.*;
import java.util.Scanner;

public class Main {
    public static class DatabaseConnector {
        private static final String URL = "jdbc:sqlite:E://Projects//School//JDBC//javajdbc.db";

        public static Connection connect() {
            try {
                return DriverManager.getConnection(URL);
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
                return null;
            }
        }
    }

    //insert new recipe field
    public static void addRecipe(String recipeName, int categoryID, int chefID, int cookingTime) {
        String query = "INSERT INTO recipe (recipeName, FK_CategoryID, FK_ChefID, cookingTime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, recipeName);
            pstmt.setInt(2, categoryID);
            pstmt.setInt(3, chefID);
            pstmt.setInt(4, cookingTime);
            pstmt.executeUpdate();
            conn.commit(); // Commit transaction
            System.out.println("Recipe added successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //view rec
    public static void viewRecipes() {
        String query = """
        SELECT r.recipeName AS Recipe, c.categoryName AS Category, ch.chefName AS Chef, r.cookingTime AS CookingTime
        FROM recipe r
        JOIN category c ON r.FK_CategoryID = c.PK_CategoryID
        JOIN chef ch ON r.FK_ChefID = ch.PK_ChefID
    """;
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Recipe: " + rs.getString("Recipe") +
                        ", Category: " + rs.getString("Category") +
                        ", Chef: " + rs.getString("Chef") +
                        ", Cooking Time: " + rs.getInt("CookingTime") + " mins");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //update
    public static void updateRecipe(int recipeID, String newRecipeName) {
        String query = "UPDATE recipe SET recipeName = ? WHERE PK_RecipeID = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newRecipeName);
            pstmt.setInt(2, recipeID);
            pstmt.executeUpdate();
            System.out.println("Recipe updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //delete
    public static void deleteRecipe(int recipeID) {
        String query = "DELETE FROM recipe WHERE PK_RecipeID = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, recipeID);
            pstmt.executeUpdate();
            System.out.println("Recipe deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //listing recipes
    public static void showRecipeDetails() {
        String query = "SELECT r.recipeName AS Recipe, c.categoryName AS Category, ch.chefName AS Chef " +
                "FROM recipe r " +
                "JOIN category c ON r.FK_CategoryID = c.PK_CategoryID " +
                "JOIN chef ch ON r.FK_ChefID = ch.PK_ChefID";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Recipe: " + rs.getString("Recipe") +
                        ", Category: " + rs.getString("Category") +
                        ", Chef: " + rs.getString("Chef"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void markAsFavorite(int recipeID) {
        String query = "UPDATE recipe SET favorite = 1 WHERE PK_RecipeID = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, recipeID);
            pstmt.executeUpdate();
            System.out.println("Recipe marked as favorite!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void viewFavorites() {
        String query = "SELECT * FROM recipe WHERE favorite = 1";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Recipe: " + rs.getString("recipeName") +
                        ", Cooking Time: " + rs.getInt("cookingTime") + " mins");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void displayStatistics() {
        String query = """
        SELECT 
            (SELECT COUNT(*) FROM recipe) AS totalRecipes,
            (SELECT COUNT(*) FROM chef) AS totalChefs,
            (SELECT COUNT(*) FROM category) AS totalCategories
        """;
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                System.out.println("Total Recipes: " + rs.getInt("totalRecipes"));
                System.out.println("Total Chefs: " + rs.getInt("totalChefs"));
                System.out.println("Total Categories: " + rs.getInt("totalCategories"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
class RecipeApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("1. Add recipe");
            System.out.println("2. View recipes");
            System.out.println("3. Update recipe");
            System.out.println("4. Delete recipe");
            System.out.println("5. Show recipe details");
            System.out.println("6. Mark recipe as favorite");
            System.out.println("7. View favorite recipes");
            System.out.println("8. Display statistics");
            System.out.println("9. Exit");
            System.out.printf("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            isRunning = switch (choice) {
                case 1 -> {
                    System.out.print("Enter Recipe Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Category ID: ");
                    int categoryID = scanner.nextInt();
                    System.out.print("Enter Chef ID: ");
                    int chefID = scanner.nextInt();
                    System.out.print("Enter Cooking Time: ");
                    int time = scanner.nextInt();
                    Main.addRecipe(name, categoryID, chefID, time);
                    yield true;
                }
                case 2 -> {
                    Main.viewRecipes();
                    yield true;
                }
                case 3 -> {
                    System.out.print("Enter Recipe ID to update: ");
                    int updateID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new Recipe Name: ");
                    String newName = scanner.nextLine();
                    Main.updateRecipe(updateID, newName);
                    yield true;
                }
                case 4 -> {
                    System.out.print("Enter Recipe ID to delete: ");
                    int deleteID = scanner.nextInt();
                    Main.deleteRecipe(deleteID);
                    yield true;
                }
                case 5 -> {
                    Main.showRecipeDetails();
                    yield true;
                }
                case 6 -> {
                    System.out.print("Enter Recipe ID to mark as favorite: ");
                    int favID = scanner.nextInt();
                    Main.markAsFavorite(favID);
                    yield true;

                }
                case 7 -> {
                    System.out.println("Favorite Recipes:");
                    Main.viewFavorites();
                    yield true;
                }
                case 8 -> {
                    Main.displayStatistics();
                    yield true;
                }

                case 9 -> {System.out.println("Exiting program.");
                    yield false;
                }

                default -> {
                    System.out.println("Invalid choice. Try again.");
                    yield true;
                }
            };
        }
    }
}


