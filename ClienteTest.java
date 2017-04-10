package br.com.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.util.JPAUtil;
import br.com.util.JPAUtilTest;

public class ClienteTest {

	private EntityManager em;

	private static final String CPF_PADRAO = "700.947.431-18";

	@Test
	public void deveSalvarCliente() {
		Cliente cliente = new Cliente();
		cliente.setNome("Allan Borges");
		cliente.setCpf(CPF_PADRAO);

		assertTrue("não deve ter ID definido", cliente.isTransient());

		em.getTransaction().begin();
		em.persist(cliente);
		em.getTransaction().commit();

		assertFalse("deve ter ID definido", cliente.isTransient());
		assertNotNull("deve ter id definido", cliente.isTransient());

	}
	@Test
	public void deveConsultarCpf(){
		deveSalvarCliente();
		
		String filtro = "Allan";
		
		Query query  = em.createQuery("SELECT c.cpf FROM Cliente c WHERE c.nome LIKE :nome");
		query.setParameter("nome", "%" + filtro + "%");
		
		List<String> listaCpf = query.getResultList();
		
		assertFalse("verifica se ha registros na lista", listaCpf.isEmpty());
	}
	@Test
	public void deveAlterarCliente(){
		deveSalvarCliente();
		
		TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1);
		Cliente cliente = query.getSingleResult();
		
		assertNotNull("deve ter encontrado um cliente", cliente);
		
		Integer versao = cliente.getVersion();
		
		em.getTransaction().begin();
		cliente.setCpf(CPF_PADRAO);
		cliente = em.merge(cliente);
		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.toString(), cliente.getVersion().intValue());
	}
	
    @Test
	public void deveExcluirCliente(){
		deveSalvarCliente();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(c.id) FROM Cliente c", Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Cliente cliente = em.find(Cliente.class, id);
		em.remove(cliente);
		em.getTransaction().commit();
		
		Cliente clienteExcluido = em.find(Cliente.class, id);
		assertNull("não deve ter encontrado cliente", clienteExcluido);
	}
    @Test
    public void quantidadeClientePossuiTelefoneJPQL(){
    	SalvarCliente(3);
    	
    	StringBuilder jpql = new StringBuilder();
    	jpql.append(" SELECT COUNT(c.id) ");
    	jpql.append(" FROM Cliente c ");
    	jpql.append(" INNER JOIN c.telefone t ");
    	jpql.append(" WHERE t.telefone LIKE :telefone ");
    	
    	Query query = em.createQuery(jpql.toString());
    	query.setParameter("telefone", "(62)9330%");
    	
    	Long qtdClientes = (Long) query.getSingleResult();
    	
    	assertTrue("quantidade de cliente ", qtdClientes.SIZE >=3);
    	    	
    }
    
    @Test
    public void pesquisarClientePorParteDoNome_JPQL(){
    	SalvarCliente(5);; 
    	
    	StringBuilder jpql =  new StringBuilder();
    	jpql.append(" SELECT COUNT(c.id) FROM Cliente c ");
    	jpql.append(" WHERE c.nome LIKE :nome ");
    	
    	Query query = em.createQuery(jpql.toString());
    	query.setParameter("nome", "%Allan%");
    	
    	Long qtdRegistros = (Long) query.getSingleResult();
    	
    	assertTrue("quantidade cliente deve ser maior que zero", qtdRegistros.SIZE >= 5);
    	//assertFalse("Quantidade de cliente deve ser menor que zero", qtdRegistros.intValue() > 0);
    }
    
    @Test
    public void pesquisarClientePorIdade_JPQL(){
    	SalvarCliente(5);; 
    	
    	StringBuilder jpql =  new StringBuilder();
    	jpql.append(" SELECT COUNT(c.id) FROM Cliente c ");
    	jpql.append(" WHERE c.idade= :idade ");
    	
    	Query query = em.createQuery(jpql.toString());
    	query.setParameter("idade", 25);
    	
    	Long qtdRegistros = (Long) query.getSingleResult();
    	
    	assertTrue("quantidade cliente deve ser maior que zero", qtdRegistros.SIZE >= 5);
    }
    private void SalvarCliente(int quantidade){
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Cliente cliente =  new Cliente();
			 cliente.setNome("Allan");
			 cliente.setCpf("700.947.431-18");
			 
			 em.persist(cliente);
		}
		em.getTransaction().commit();
	}
    
    private void criarCliente(String nome, String cpf){
		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		cliente.setCpf(cpf);
	}
	private void SalvarEmpresa(int quantidade){
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Empresa emp = new Empresa();
			 emp.setNome("Syndata");
			 emp.setCnpj("08957132000118");
			 
			 em.persist(emp);
		}
		em.getTransaction().commit();
	}
    private  void criarEmpresa(String cnpj){
		Empresa emp = new Empresa();
		  emp.setCnpj(cnpj);
	}
    
	@Before
	public void instanciarEntityManager() {
		em = JPAUtil.INSTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManager() {
		if (em.isOpen()) {
			em.close();
		}
	}

	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();

		entityManager.getTransaction().begin();

		javax.persistence.Query query = entityManager
				.createQuery("DELETE FROM Cliente c");
		int qtdRegistrosExcluidos = query.executeUpdate();

		entityManager.getTransaction().commit();

		assertTrue("certifica que a base foi limpa", qtdRegistrosExcluidos > 0);
	}

}
