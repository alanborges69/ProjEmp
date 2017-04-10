package br.com.modelo;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.util.JPAUtil;

public class RelatorioCriteriaTest {
	
	private static final String CNPJ_PADRAO="08957132000118";

	private EntityManager em;

	private Session getSession() {
		return(Session) em.getDelegate();
 	}
	
	private Criteria createCriteria(Class<?> clazz){
		return getSession().createCriteria(clazz);
	}
	
	private Criteria createCriteria(Class<?> clazz, String alias){
		return getSession().createCriteria(clazz, alias);
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
	
	private void SalvarEmpresa(int quantidade){
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Empresa emp = new Empresa();
			 emp.setNome("Syndata");
			 emp.setCnpj(CNPJ_PADRAO);
			 
			 em.persist(emp);
		}
		em.getTransaction().commit();
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
	
	private void SalvarFuncionario(int quantidade){
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Funcionario funcionario = new Funcionario();
			 funcionario.setNome("fulano");
			 funcionario.setSalario(1200.00);
			 
			 em.persist(funcionario);
		}
		em.getTransaction().commit();
	}
	
	private void SalvarTelefone(int quantidade){
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Telefone telefone =  new Telefone();
			 telefone.setTelefone("(62)99330-5853");
			 
			 em.persist(telefone);
		}
		em.getTransaction().commit();
	}
	
	public void criarTelefone(String fone){
		Telefone telefone = new Telefone();
		telefone.setTelefone(fone);
	}
	private void criarFuncionario(String nome, Double salario){
		Funcionario funcionario =  new Funcionario();
		funcionario.setNome(nome);
		funcionario.setSalario(salario);
	}
	private  void criarEmpresa(String nome, String cnpj){
		Empresa emp = new Empresa();
		  emp.setNome(nome);
		  emp.setCnpj(cnpj);
	}
	
	private void criarCliente(String nome, String cpf){
		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		cliente.setCpf(cpf);
	}
	@Test
	@SuppressWarnings("unchecked")
	public void consultaDeClientes(){
		SalvarCliente(3);
		
		Criteria criteria = createCriteria(Cliente.class, "c");
		
		List<Cliente> clientes =  criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		assertTrue("verifica se a quantidade de cliente e pelomenos 3", clientes.size() >= 3 );
		
	}
	@Test
	@SuppressWarnings("unchecked")
	public void clienteChaveValor(){
		SalvarCliente(5);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("c.id").as("c.id"))
				.add(Projections.property("c.nome").as("c.nome"));
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.setProjection(projection);
		
		List<Map<String, Object>> clientes = criteria
				.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
				.list();
		assertTrue("verifica se a quantidade de cliente e pelomesno 5", clientes.size()>=5);

	}

	@Test
	public void funcionarioComMaiorSalario(){
		SalvarFuncionario(5);
		
		Criteria criteria = createCriteria(Funcionario.class, "f")
				.setProjection(Projections.max("f.salario"));
		
		Double maiorSalario = (Double)criteria
				.setResultTransformer(Criteria.PROJECTION)
				.uniqueResult();
		
		assertTrue("verifica se qual salario e o maior", maiorSalario >= 5);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void funcionarioPorNomeEmpresa(){
		SalvarFuncionario(1);
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Empresa.class, "e")
				.add(Restrictions.in("e.id", 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L))
				.setProjection(Projections.property("e.nome"));
		
		Criteria criteria = createCriteria(Funcionario.class, "f")
				.createAlias("f.empresa", "emp")
				.add(Subqueries.propertyIn("emp.nome", detachedCriteria));
		
		List<Funcionario> funcionarios = criteria	
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();	
	}
	@Test
	public void funcionarioPorParteDoNome(){
		SalvarFuncionario(3);
		
		Criteria criteria = createCriteria(Funcionario.class)
				.add(Restrictions.eq("nome", "Allan Borges"))
				.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Funcionario> funcionarios = criteria
		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
		.list();
		
		assertFalse("deve existir funconarios", funcionarios.size() >= 3);
	}
	@Test
	public void consultaClientePorIdade(){
		SalvarCliente(10);
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.add(Restrictions.between("c.idade", 10, 20))
				.setProjection(Projections.rowCount());
		
		Long qtdRegistro = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verificar se  tem cliente", qtdRegistro.SIZE >=4);
	}
	@Test
	public void empresaPesquisa(){
		SalvarEmpresa(1);
		
		Criteria criteria = createCriteria(Empresa.class, "e");
		criteria.add(Restrictions.eq("e.nome", "Syndata"));
		
		List<Empresa> empresas = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica a quantidade de empresa", empresas.size() >= 1);
	}
	@Test
	 public void consultaIdENomeEmpresa(){
		 SalvarEmpresa(3);
		 
		 ProjectionList projection = Projections.projectionList()
				 .add(Projections.property("e.id").as("id"))
				 .add(Projections.property("e.nome").as("nome"));
		 
		 Criteria criteria = createCriteria(Empresa.class, "e")
				 .setProjection(projection);
		 
		 List<Object[]> empresas  =criteria
				 .setResultTransformer(Criteria.PROJECTION)
				 .list();
		 
		 assertTrue("verifica se a quantidade de empresas", empresas.size() >=3);
	 }

	@Test
	public void telefonePesquisa(){
		SalvarTelefone(3);
		
		Criteria criteria = createCriteria(Telefone.class, "t");
		
		List<Telefone> telefones = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("quantidade de telefones", telefones.size() >= 3);
	}
	
	@Test
	public void quantidadeDeTeleFonePorCliente(){
		SalvarTelefone(3);
		
		Criteria criteria = createCriteria(Telefone.class, "t")
				.createAlias("t.cliente", "c")
				.add(Restrictions.eq("c.cpf", "700.947.431.-18"))
				.setProjection(Projections.rowCount());
		
		Long qtdTelefones = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica quantidade de telefone por cliente", qtdTelefones.SIZE >= 3);
	}
	
	@Test
	public void quantidadeDeTeleFonePorEmpresa(){
		SalvarTelefone(3);
		
		Criteria criteria = createCriteria(Telefone.class, "t")
				.createAlias("t.empresa", "e")
				.add(Restrictions.eq("e.cnpj", "08.957.132/0001-18"))
				.setProjection(Projections.rowCount());
		
		Long qtdTelefonesEmpresa = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica quantidade de telefone por empresa", qtdTelefonesEmpresa.SIZE >= 3);
	}
}
