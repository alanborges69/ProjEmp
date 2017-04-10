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

public class FuncionarioTest {
	
	private EntityManager em;
	@Test
	public void deveSalvarFuncionario(){
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Fulano");
		funcionario.setSalario(1200.00);
		
		assertTrue("Não deve ter ID definido", funcionario.isTransient());
		
		em.getTransaction().begin();
		em.persist(funcionario);
		em.getTransaction().commit();
		
		assertFalse("deve ter id Definido", funcionario.isTransient());
		//assertNotNull("deve ter id Definido", funcionario.getId());
	}
	@Test
	public void devePesquisarFuncionarios(){
		for (int i = 0; i < 10; i++) {
			deveSalvarFuncionario();
		}
		TypedQuery<Funcionario> query = em.createQuery("SELECT f FROM Funcionario f", Funcionario.class);
		List<Funcionario> funcionarios = query.getResultList();
		
		assertFalse("deve ter encontrado um funcionario", funcionarios.isEmpty());
		assertTrue("deve ter encontrado varios funcionarios", funcionarios.size() >= 10);		
	}
	@Test
	public void deveAlterarFuncionario(){
		deveSalvarFuncionario();
		
		TypedQuery<Funcionario> query = em.createQuery("SELECT f FROM Funcionario f", Funcionario.class).setMaxResults(1);
		
		Funcionario funcionario =  query.getSingleResult();
		
		assertNotNull("deve ter encontrado um funcionario", funcionario);
		
		Integer versao = funcionario.getVersion();
		
		em.getTransaction().begin();
		funcionario.setSalario(1200.00);
		
		funcionario = em.merge(funcionario);
		
		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.doubleValue(), funcionario.getVersion().intValue());
	}
	@Test
	public void deveExcluirFuncionario(){
		deveSalvarFuncionario();
		
		TypedQuery<Long> query =  em.createQuery("SELECT MAX(f.id)FROM Funcionario f", Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Funcionario funcionario = em.find(Funcionario.class, id);
		em.remove(funcionario);
		
		em.getTransaction().commit();
		
		Funcionario funcionarioExcluido = em.find(Funcionario.class, id);
		
		assertNull("não deve ter encontrado", funcionarioExcluido);
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
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
		
		entityManager.getTransaction().begin();
		
	   Query query = entityManager.createQuery("DELETE FROM Funcionario f");
	   int qtdRegistrosExclidos = query.executeUpdate();
	   
	   entityManager.getTransaction().commit();
	   
	   assertTrue("certifica que a base limpada", qtdRegistrosExclidos > 0);
	}
}
	