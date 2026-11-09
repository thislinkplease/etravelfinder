package src.Model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

	public static SessionFactory buildSessionFactory() 
	{
		try 
		{
			return new Configuration().configure("Hibernate.cfg.xml").buildSessionFactory();
		} catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static SessionFactory getSessionFactory()
	{
		return SESSION_FACTORY;
	}
	
	public static void shutdown()
	{
		getSessionFactory().close();
	}
}
