package dao;

import model.Tag;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class TagDao {

	private final Session session;

	public TagDao(Session session) {
		this.session = session;
	}
	
	public Tag byName(String name) {
		return (Tag) session.createQuery("from Tag t where t.name = :name").setString("name", name).uniqueResult();
	}
	
	public void save(Tag tag) {
		session.save(tag);
	}
}
