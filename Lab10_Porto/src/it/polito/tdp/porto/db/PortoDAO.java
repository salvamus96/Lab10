package it.polito.tdp.porto.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.AuthorIdMap;
import it.polito.tdp.porto.model.Paper;
import it.polito.tdp.porto.model.PaperIdMap;

public class PortoDAO {

	/**
	 * Dato l'id ottengo l'autore.
	 */
	public Author getAutore(int id) {

		final String sql = "SELECT * FROM author where id=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				return autore;
			}

			return null;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	
	/**
	 * Dato l'id ottengo l'articolo.
	 */
	public Paper getArticolo(int eprintid) {

		final String sql = "SELECT * FROM paper where eprintid=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
				return paper;
			}

			return null;

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}
	
	
	/**
	 * Restituisce la lista degli autori contenuti nel database (pattern ORM)	
	 * @param authorIdMap 
	 * @return
	 */
	public List<Author> getListAutori(AuthorIdMap authorIdMap) {

		final String sql = "SELECT * FROM author ORDER BY lastname, firstname";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Author> autori = new ArrayList<>();
			while (rs.next()) {

				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				autori.add(authorIdMap.get(autore));
			}
			conn.close();
			return autori;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	
	/**
	 * Restituisce la lista degli articoli contenuti nel database (pattern ORM)
	 * @param paperIdMap
	 * @return
	 */
	public List<Paper> getListArticoli(PaperIdMap paperIdMap) {
		final String sql = "SELECT * FROM paper ";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Paper> list = new ArrayList<>();
			while (rs.next()) {
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
			
				list.add(paperIdMap.get(paper));
			}
			
			conn.close();
			return list;
			
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}
	
	
	/**
	 * Dato un autore, restituisce i coautori che hanno collaborato con lui (pattern ORM)
	 * @param autore
	 * @return
	 */
	
	public List<Author> getListCoautori(Author autore, AuthorIdMap authorIdMap) {
// il DISTINCT è obbligatorio perchè due autori possono aver collaborato più volte insieme
		final String sql = "SELECT DISTINCT a2.id, a2.lastname, a2.firstname " + 
							"FROM creator c1, creator c2, author a2 " + 
							"WHERE c1.eprintid = c2.eprintid AND c1.authorid = ? " + 
							"		AND a2.id = c2.authorid AND c2.authorid <> c1.authorid " + 
							"ORDER BY a2.lastname , a2.firstname";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, autore.getId());
			ResultSet rs = st.executeQuery();

			List<Author> coautori = new ArrayList<>();
			while (rs.next()) {

				Author a = new Author(rs.getInt("a2.id"), rs.getString("a2.lastname"), rs.getString("a2.firstname"));
				coautori.add(authorIdMap.get(a));
			}
			conn.close();
			return coautori;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	
	/**
	 * Dati due autori restituisce un articolo in cui hanno collaborato entrambi
	 * @param aPartenza
	 * @param aDestinazione
	 * @return
	 */
	public Paper articoloInComune(Author aPartenza, Author aDestinazione) {
		final String sql = "SELECT p.eprintid, title, issn, publication, type, types " + 
						   "FROM creator c1, creator c2, paper p " + 
						   "WHERE c1.eprintid = c2.eprintid AND p.eprintid = c1.eprintid " + 
						   "		AND c1.authorid = ? AND c2.authorid = ? " + 
						   "LIMIT 1"; // è sufficiente almeno un articolo 

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, aPartenza.getId());
			st.setInt(2, aDestinazione.getId());
			ResultSet rs = st.executeQuery();
		
			Paper p = null;
			if (rs.next()) {
				p = new Paper(rs.getInt("p.eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types")) ;
			}
			conn.close();
			return p;
		
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

		
	/**
	 * Definisce le relazioni tra autori e articoli dal DB
	 * @param authorIdMap
	 * @param paperIdMap
	 */
	public void getAllCreator(AuthorIdMap authorIdMap, PaperIdMap paperIdMap) {
		final String sql = "SELECT * FROM creator";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				// utilizzo dell'Identity Map per indice
				Author a = authorIdMap.get(rs.getInt("authorid"));
				Paper p = paperIdMap.get(rs.getInt("eprintid"));
				
				// popolazione delle liste relative a ogni oggetto creato
				a.getArticoli().add(p);
				p.getAutori().add(a);
			}
			
			conn.close();
			
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	
	}

}
