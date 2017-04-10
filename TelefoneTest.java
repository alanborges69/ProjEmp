package br.com.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.util.JPAUtil;

public class TelefoneTest {
    
	public EntityManager em;
	
	private static final String TELEFONE_PADRAO="(62)3578-3578";
	
	@Test
	public void deveSalvarTelefone() {
	 Telefone telefone = new Telefone();
	 telefone.setTelefone(TELEFONE_PADRAO);
	 
	 assertTrue("nao deve ter id definido", telefone.isTransient());
	 
	 em.getTransaction().begin();
	 em.persist(telefone);
	 em.getTransaction().commit();
	 
	 assertFalse("deve ter id definido", telefone.isTransient());
	}
	@Test
	public void devePesquisarTelefone(){
		for (int i = 0; i < 10; i++) {
			deveSalvarTelefone();	
		}
		
		Query query  = em.createQuery("SELECT t FROM Telefone t", Telefone.class);
		List<Telefone> telefones =  query.getResultList();
		
		assertFalse("deve ter encontrado um telefone", telefones.isEmpty());
		assertTrue("deve ter encontrado varios telefone", telefones.size() >= 10);
	}
	@Test
	public void deveAlterarTelefone(){
		deveSalvarTelefone();
		
		TypedQuery<Telefone> query = em.createQuery("SELECT t FROM Telefone t", Telefone.class).setFirstResult(1);
		
		Telefone telefone = query.getSingleResult();
		
		assertNotNull("deve ter encontrado um telefone", telefone);
		
		Integer versao = telefone.getVersion();
		
		em.getTransaction().begin();
		telefone.setTelefone(TELEFONE_PADRAO);
		telefone = em.merge(telefone);
		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.toString(), telefone.getVersion().intValue());
	}
	@Test
	public void deveExcluirTelefone(){
		deveSalvarTelefone();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(t.id) FROM Telefone t", Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Telefone telefone = em.find(Telefone.class, id);
		em.remove(telefone);
		
		em.getTransaction().commit();
		
		Telefone telefoneExcluido = em.find(Telefone.class, id);
		
		assertNull("não deve ter encontrado produto", telefoneExcluido);
	}
	
    @Before
	public void instanciarEntityManager(){
		em = JPAUtil.INSTANCE.getEntityManager();
	}
    @After
    public void fecharEntityManager(){
    	if (em.isOpen()) {
			em.close();
		}
    }
    @AfterClass
    public static void deveLimparBaseDeTeste(){
    	EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
    	
    	entityManager.getTransaction().begin();
    	
    	Query query = entityManager.createQuery("DELETE FROM Telefone t");
    	int qtdRegistrosExcluidos = query.executeUpdate();
    	
    	entityManager.getTransaction().commit();
    	
    	assertTrue("certifica que a base foi limpada", qtdRegistrosExcluidos > 0);
    	
    }
	
}
