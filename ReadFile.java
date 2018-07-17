
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;

/**
 * 
 * vxm773, based on the given ReadFile example class
 *
 */
public class ReadFile {

	public static void main(String[] args) {
		System.out.println("started");
		Connection databaseConnection = null;
		Statement stmt = null;

		try {

			Class.forName("org.postgresql.Driver");

			String path = "jdbc:postgresql://mod-fund-databases.cs.bham.ac.uk:5432/vxm773";
			String username = "vxm773";
			String password = "15901590";

			databaseConnection = DriverManager.getConnection(path, username, password);

			System.out.println("Opened database successfully");

			PreparedStatement preparedStmt = null;

			String createTableArtists = "CREATE TABLE IF NOT EXISTS artists(id INT PRIMARY KEY NOT NULL, name TEXT NOT NULL);";
			try {
				preparedStmt = databaseConnection.prepareStatement(createTableArtists);
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String createTableAlbums = "CREATE TABLE IF NOT EXISTS albums(id INT PRIMARY KEY NOT NULL, name TEXT NOT NULL, artists_id INT REFERENCES artists(id)  NOT NULL);";
			try {
				preparedStmt = databaseConnection.prepareStatement(createTableAlbums);
				preparedStmt.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			String createTableSongs = "CREATE TABLE IF NOT EXISTS songs (id INT PRIMARY KEY NOT NULL, name TEXT NOT NULL, artists_id INT REFERENCES artists(id) NOT NULL, albums_id  INT REFERENCES  albums(id)  NOT NULL);";
			try {
				preparedStmt = databaseConnection.prepareStatement(createTableSongs);
				preparedStmt.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			String createTableTags = "CREATE TABLE IF NOT EXISTS songstags (songs_id INT  REFERENCES songs(id), tags TEXT NOT NULL);";
			try {
				preparedStmt = databaseConnection.prepareStatement(createTableTags);
				preparedStmt.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			System.out.println("Tables done.");

			stmt = databaseConnection.createStatement();

			String line;
			String[] token;
			int artistId;
			int albumId;
			int songId = 1;
			String path2 = "/Users/vasilis/Desktop/artists-songs-albums-tags.csv";

			String songName = "";
			String albumName = "";
			String artistName = "";

			HashSet<String> albums = new HashSet<String>();
			HashSet<String> artists = new HashSet<String>();

			try {

				BufferedReader br = new BufferedReader(new FileReader(path2));
				br.readLine();
				while ((line = br.readLine()) != null) {
					token = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

					songName = token[0];
					artistName = token[1];
					albumName = token[2];

					String newArtistName = artistName.replace("'", "''");
					String newAlbumName = albumName.replace("'", "''");
					String newSongName = songName.replace("'", "''");

					if (!artists.contains(artistName)) {
						artists.add(artistName);

						String artistInsert = "INSERT INTO artists VALUES (" + artists.size() + ",'" + newArtistName
								+ "')";
						// stmt.addBatch(artistInsert);
						stmt.executeUpdate(artistInsert);
					}

					ResultSet rs = stmt.executeQuery("SELECT id FROM artists WHERE name ='" + newArtistName + "'");
					rs.next();
					artistId = rs.getInt(1);

					if (!albums.contains(albumName)) {
						albums.add(albumName);

						String albumsInsert = "INSERT INTO albums VALUES (" + albums.size() + ",'" + newAlbumName + "',"
								+ artistId + ")";

						// stmt.addBatch(albumsInsert);

						stmt.executeUpdate(albumsInsert);
					}

					rs = stmt.executeQuery("SELECT id FROM albums WHERE name ='" + newAlbumName + "'");
					rs.next();
					albumId = rs.getInt(1);

					String songsInsert = "INSERT INTO songs VALUES (" + songId + ",'" + newSongName + "'," + artistId
							+ "," + albumId + ")";

					// stmt.addBatch(songsInsert);

					stmt.executeUpdate(songsInsert);

					for (int i = 3; i < token.length; i++) {
						String tag = token[i];
						if (tag != "") {

							String newTag = tag.replace("'", "''");

							if (tag.contains("*")) {

								String newTag2 = newTag.replace("*", "");

								String songstagsInsertGenre = "INSERT INTO songstags VALUES (" + songId + ",'" + newTag2
										+ "')   ";

								stmt.executeUpdate(songstagsInsertGenre);
							} else

							{
								String songstagsInsert = "INSERT INTO songstags VALUES (" + songId + ",'" + newTag
										+ "')";
								stmt.executeUpdate(songstagsInsert);
							}

						} else
							break;
					}
					songId = songId + 1;

					System.out.println("Added to the database the songID" + songId);
				}
				// stmt.executeBatch();
				br.close();
				System.out.println("Finished adding");

			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				System.out.println(e.getMessage() + "Error reading file");
			} catch (SQLException e) {
				System.out.println(e.getMessage() + "SQL exception");
			}

			stmt.close();
			databaseConnection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

}