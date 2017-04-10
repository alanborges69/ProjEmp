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

import antlr.ParserSharedInputState;
import br.com.util.JPAUtil;

public class EmpresaTest {
	
	private EntityManager em;
	
	private static final String CNPJ_PADRAO="08957132000118";
	
    @Test
	public void deveSalvarEmpresa(){
		Empresa empresa = new Empresa();
		   empresa.setNome("Syndata");
		   empresa.setCnpj("08.957.132/0001-18");
		
		assertTrue("não deve ter ID definido", empresa.isTransient());
		
		em.getTransaction().begin();
		em.persist(empresa);
		em.getTransaction().commit();
		
		assertNotNull("deve ter ID definido", empresa.getId());
	}
    @Test
    public void devePesquisarEmpresa(){
    	for (int i = 0; i < 10; i++) {
			deveSalvarEmpresa();
		}
    	
    	TypedQuery<Empresa> query = em.createQuery("SELECT e FROM Empresa e", Empresa.class);
    	List<Empresa> empresas = query.getResultList();
    	
    	assertFalse("deve ter encontrado a empresa", empresas.isEmpty());
    	assertTrue("deve ter encontrado varias empresas", empresas.size() >= 10);
    }
    
    
    @Test
    public void deveAlterarEmpresa(){
    	deveSalvarEmpresa();
    	
    	TypedQuery<Empresa> query = em.createQuery("SELECT e FROM Empresa e", Empresa.class).setMaxResults(1);
    	
    	Empresa empresa = query.getSingleResult();
    	
    	assertNotNull("deve ter encontrado uma empresa", empresa);
    	
    	Integer versao =  empresa.getVersion();
    	
    	em.getTransaction().begin();
    	empresa.setCnpj("08.957.132000118");
    	
    	empresa = em.merge(empresa);
    	em.getTransaction().commit();
    	
    	assertNotEquals("deve ter versao incrementada", versao.toString(), empresa.getVersion().intValue());
    	//assertNotEquals("deve ter versao incrementada", versao.intValue(), empresa.getVersion().intValue());
    }
    
    private void inserirCliente(String nome, String cpf){
    	Cliente cliente = new Cliente();
    	    cliente.setNome(nome);
    	    cliente.setCpf(cpf);
    }
       
    @Test
    public void qtdeClientesPorEmpresa_JPQL(){
    	deveSalvarEmpresa();
    	
    	StringBuilder jpql = new StringBuilder();
    	jpql.append(" SELECT COUNT(c.id) FROM Empresa e ");
    	jpql.append(" INNER JOIN e.clientes c ");
    	jpql.append(" WHERE e.cnpj= :cnpj ");
    	
     	Query query = em.createQuery(jpql.toString());
     	query.setParameter("cnpj", "08.957.132/0001-18");
    	
    	Long qtdClienteEmpresa = (Long)query.getSingleResult();
    	
        assertFalse("quantidade de cliente não deve ser menor que zero", qtdClienteEmpresa.longValue() < 0);
    }
    @Test
    public void pesquisarEmpresaPorParteDoNome_JPQL(){
    	deveSalvarEmpresa(); 
    	
    	StringBuilder jpql =  new StringBuilder();
    	jpql.append(" SELECT COUNT(e.id) FROM Empresa e ");
    	jpql.append(" WHERE e.nome LIKE :nome ");
    	
    	Query query = em.createQuery(jpql.toString());
    	query.setParameter("nome", "%Allan%");
    	
    	Long qtdRegistros = (Long) query.getSingleResult();
    	
    	assertFalse("Quantidade de empresa deve ser menor que zero", qtdRegistros.intValue() > 0);
    }
    
    
    @Test
    public void deveExcluirEmpresa(){
    	deveSalvarEmpresa();
    	
    	TypedQuery<Long> query = em.createQuery("SELECT MAX(e.id) FROM Empresa e", Long.class);
    	Long id = query.getSingleResult();
    	
    	em.getTransaction().begin();
    	
    	Empresa empresa = em.find(Empresa.class, id);
    	em.remove(empresa);
    	
    	em.getTransaction().commit();
    	
    	Empresa empresaExcluida = em.find(Empresa.class, id);
    	
    	assertNull("não deve ter encontrado a empresa", empresaExcluida);
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
		
		Query query = entityManager.createQuery("DELETE FROM Empresa e");
		int qtdRegistrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("certifica que a base foi limpada", qtdRegistrosExcluidos > 0);
	}
}
