# jpa-seed
Projeto base para utilizaÃ§Ã£o de JPA

Qual a responsabilidade/objeto das anotações:

@MappedSuperclass 
   @MappedSuperClass e usuada como uma super class para as entidades, que compartilha estados
   comportamentos que podem ser herdados pelo mesmo.

@Version
   @Version e um elemento usado para mapear o numero da versÃ£o de um campo da entidade.

@Entity
   @Entity indica que uma classe java e uma entidade.

@Table
   @Table mapeia o nome da tabela. Caso nÃ£o seja utilizado o parametro name, o valor
   padrÃ£o assumido e o nome da classe.

@Id
   @Id Ã© o identificador da entidade. Normalmente se refere a chave primaria da tabela relacional;

@GeneratedValue
   @GeneratedValue e a anotaÃ§Ã£o responsavel por gerar o valor do identicador automaticamente na 
   applicaÃ§Ã£o

@Column
   @Column mapeia o nome da coluna existente na tabela relacional

@Basic
   @Basic mapea o campo ou propriedade de uma coluna na tabela

@Temporal




Qual a responsabilidade/objeto das anotaÃ§Ãµes:

@ManyToOne
  - uma anotação usada para mapear relacionamentos de muitos para um, e deve
   ser usada na entidade que possui um atributo fonte;

@ManyToMany
  - usada para mapear um relacionamento entre as entidades de muitos para muitos. Onde ambas as 
   entidades deve possuir a anotaÃ§Ã£o no atributo fonte em coleÃ§Ã£o;

@OneToOne
  -uma anotação usada para mapear um relacionamento de um para para um, a forma de usa-la
   e a mesma que é usada @ManyToOne

@JoinColumn
  - utiizada para mapear a referencia de uma tabela em outra tabela, que no caso a chave primaria
   que referencia a outra tabela seria a chave estranjeira.
   

@JoinTable
   -geralmente  usada quando tem um relacionamento de muito para muitos entre as entidades, e  usado
    uma terceira entidade. Essa anotaÃ§Ã£o consiste de duas chaves estrangeiras para se referir a cada
    uma das duas entidades no relacionamento. Sendo assim uma coleção de entidades são mapeadas com varias
    linhas na tabela
   
Qual a responsabilidade/objeto dos mÃ©todos do EntityManager:

isOpen
  - deteermina se a entidade esta aberta
close
  - usado para limpar a entidade e a factory que foi usada para cria-la
createQuery
  - usado para consultar o armazenamento de dados usando consultas de linguagem de consulta Java Persistenca
find
  - retorna a instancia da classe que foi usada como argumento 
merge
  - Mescla o estado da entidade recebida no atual contexto de persistência, cria uma nova instancia do objeto e
    copia o estado original da entidade e torna a nova instancia gerenciavel que ira implicar no insert, e no 
    commit da transação
persist
  - cria a instancia do objeto altera o estado do mesmo para gerenciavel que ira implicar o insert
   e commit na transação
remove
  - reponsavel por deletar o apagar o objeto do banco de dados;
  
  
Como instânciar Criteria do Hibernate através do EntityManager?
Dê exemplo do código

Antes de instanciar, deve mudar a versão do hibernate, alterar a dependencia no pom.xml a versão não deve ser superior a 5.1, assim usamos 5.1.0.Final pois Criteria foi depreciado nas versões posteriores
Crie um metodo que retorna um Session do Hibernate, crie outro método que retorna  o proprio Criteria.
O Session é um Session do EntityManager conforme codigo de exemplo abaixo. A Session é uma fábrica para intancias de Criteria.
	
codigo
	private EntityManager em;
	
	
	private Session getSession(){
		return (Session) em.getDelegate();
	}
	
	private Criteria createCriteria(Class<?> clazz){
		return getSession().createCriteria(clazz);
	}
	
	private Criteria createCriteria(Class<?> clazz, String alias){
		return getSession().createCriteria(clazz, alias);
	}
	
	//instanciando
	Criteria criteria = createCriteria(SuaClasse.class,"alias");


### 5- Como abrir uma transação?
Para abrir uma transação deve pegar a instancia ativa do EntityManager e chamar o método getTransation().begin();
#### 5.1 Dê exemplo do código

	private void criarProdutos(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Produto produto = criarProduto("Notebook", "Dell");
			
			em.persist(produto);
		}
		
		em.getTransaction().commit();
	}

### 6- Como fechar uma transação?
Para fechar uma transação deve pegar a instancia ativa do EntityManager e chamar o método getTransation().commit() ou callback();
	Se chamar o commit() confirma as açoes solicitadas. se chamar calback() desfaz as ações solicitadas.
#### 6.1 Dê exemplo do código

	private void criarClientes(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Cliente cliente = new Cliente();
			cliente.setNome("Werlon Guilherme");
			cliente.setCpf(CPF_PADRAO);
			
			em.persist(cliente);
		}
		
		em.getTransaction().commit();
	}

### 7- Como criar e executar uma query com JPQL?
O JPQL facilita a criação das querys utilizando o metodo createQuery() do EntityManager sendo possível criar consultas informando o Objeto que se  quer o resultado
#### 7.1 Dê exemplo do código
	TypedQuery<Produto> query = em.createQuery(" SELECT p FROM Produto p", Produto.class).setMaxResults(1);
	Produto produto = query.getSingleResult();

### 8- Qual a responsabilidade dos valores FetchType.LAZY e FetchType.EAGER?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento será ou não carregado após consulta da entidade.
	FetchType.LAZY só carrega o relacionamento quando for solicitado
	FetchType.EAGER sempre carrega o relacionamento

### 9- Qual a responsabilidade dos valores CascadeType.PERSIST e CascadeType.REMOVE?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento sofrerá as mesmas ações de persistência que a entidade.
	CascadeType.PERSIST indica que quando persistir ou alterar o objeto principal os objetos relacionados devem ser persistidos também
	CascadeType.REMOVE indica que quando persistir,alterar ou remover o objeto principal os objetos relacionados devem ser removidos.

### 10- Como fazer uma operação BATCH (DELETE ou UPDATE) através do EntityManager?
Utilizado quando quero alterar ou excluir muitos registros em uma unica execução.
chamo o EntityManager crio a query com o método createQuery() e executo o metodo executeUpdate();
Exemplo:

>	@AfterClass

	public static void deveLimparBase() {
	
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Produto p");
		int registrosExcluidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
	
	}


### 11- Qual a explicação para a exception LazyInitializationException?
É uma Exception disparada sempre que se tenta buscar uma Entidade/Objeto que não está dentro do escopo do EntityManager.