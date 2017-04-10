# jpa-seed
Projeto base para utilização de JPA

Qual a responsabilidade/objeto das anota��es:

@MappedSuperclass 
   @MappedSuperClass e usuada como uma super class para as entidades, que compartilha estados
   comportamentos que podem ser herdados pelo mesmo.

@Version
   @Version e um elemento usado para mapear o numero da versão de um campo da entidade.

@Entity
   @Entity indica que uma classe java e uma entidade.

@Table
   @Table mapeia o nome da tabela. Caso não seja utilizado o parametro name, o valor
   padrão assumido e o nome da classe.

@Id
   @Id é o identificador da entidade. Normalmente se refere a chave primaria da tabela relacional;

@GeneratedValue
   @GeneratedValue e a anotação responsavel por gerar o valor do identicador automaticamente na 
   applicação

@Column
   @Column mapeia o nome da coluna existente na tabela relacional

@Basic
   @Basic mapea o campo ou propriedade de uma coluna na tabela

@Temporal




Qual a responsabilidade/objeto das anotações:

@ManyToOne
  - uma anota��o usada para mapear relacionamentos de muitos para um, e deve
   ser usada na entidade que possui um atributo fonte;

@ManyToMany
  - usada para mapear um relacionamento entre as entidades de muitos para muitos. Onde ambas as 
   entidades deve possuir a anotação no atributo fonte em coleção;

@OneToOne
  -uma anota��o usada para mapear um relacionamento de um para para um, a forma de usa-la
   e a mesma que � usada @ManyToOne

@JoinColumn
  - utiizada para mapear a referencia de uma tabela em outra tabela, que no caso a chave primaria
   que referencia a outra tabela seria a chave estranjeira.
   

@JoinTable
   -geralmente  usada quando tem um relacionamento de muito para muitos entre as entidades, e  usado
    uma terceira entidade. Essa anotação consiste de duas chaves estrangeiras para se referir a cada
    uma das duas entidades no relacionamento. Sendo assim uma cole��o de entidades s�o mapeadas com varias
    linhas na tabela
   
Qual a responsabilidade/objeto dos métodos do EntityManager:

isOpen
  - deteermina se a entidade esta aberta
close
  - usado para limpar a entidade e a factory que foi usada para cria-la
createQuery
  - usado para consultar o armazenamento de dados usando consultas de linguagem de consulta Java Persistenca
find
  - retorna a instancia da classe que foi usada como argumento 
merge
  - Mescla o estado da entidade recebida no atual contexto de persist�ncia, cria uma nova instancia do objeto e
    copia o estado original da entidade e torna a nova instancia gerenciavel que ira implicar no insert, e no 
    commit da transa��o
persist
  - cria a instancia do objeto altera o estado do mesmo para gerenciavel que ira implicar o insert
   e commit na transa��o
remove
  - reponsavel por deletar o apagar o objeto do banco de dados;
  
  
Como inst�nciar Criteria do Hibernate atrav�s do EntityManager?
D� exemplo do c�digo

Antes de instanciar, deve mudar a vers�o do hibernate, alterar a dependencia no pom.xml a vers�o n�o deve ser superior a 5.1, assim usamos 5.1.0.Final pois Criteria foi depreciado nas vers�es posteriores
Crie um metodo que retorna um Session do Hibernate, crie outro m�todo que retorna  o proprio Criteria.
O Session � um Session do EntityManager conforme codigo de exemplo abaixo. A Session � uma f�brica para intancias de Criteria.
	
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


### 5- Como abrir uma transa��o?
Para abrir uma transa��o deve pegar a instancia ativa do EntityManager e chamar o m�todo getTransation().begin();
#### 5.1 D� exemplo do c�digo

	private void criarProdutos(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Produto produto = criarProduto("Notebook", "Dell");
			
			em.persist(produto);
		}
		
		em.getTransaction().commit();
	}

### 6- Como fechar uma transa��o?
Para fechar uma transa��o deve pegar a instancia ativa do EntityManager e chamar o m�todo getTransation().commit() ou callback();
	Se chamar o commit() confirma as a�oes solicitadas. se chamar calback() desfaz as a��es solicitadas.
#### 6.1 D� exemplo do c�digo

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
O JPQL facilita a cria��o das querys utilizando o metodo createQuery() do EntityManager sendo poss�vel criar consultas informando o Objeto que se  quer o resultado
#### 7.1 D� exemplo do c�digo
	TypedQuery<Produto> query = em.createQuery(" SELECT p FROM Produto p", Produto.class).setMaxResults(1);
	Produto produto = query.getSingleResult();

### 8- Qual a responsabilidade dos valores FetchType.LAZY e FetchType.EAGER?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento ser� ou n�o carregado ap�s consulta da entidade.
	FetchType.LAZY s� carrega o relacionamento quando for solicitado
	FetchType.EAGER sempre carrega o relacionamento

### 9- Qual a responsabilidade dos valores CascadeType.PERSIST e CascadeType.REMOVE?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento sofrer� as mesmas a��es de persist�ncia que a entidade.
	CascadeType.PERSIST indica que quando persistir ou alterar o objeto principal os objetos relacionados devem ser persistidos tamb�m
	CascadeType.REMOVE indica que quando persistir,alterar ou remover o objeto principal os objetos relacionados devem ser removidos.

### 10- Como fazer uma opera��o BATCH (DELETE ou UPDATE) atrav�s do EntityManager?
Utilizado quando quero alterar ou excluir muitos registros em uma unica execu��o.
chamo o EntityManager crio a query com o m�todo createQuery() e executo o metodo executeUpdate();
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


### 11- Qual a explica��o para a exception LazyInitializationException?
� uma Exception disparada sempre que se tenta buscar uma Entidade/Objeto que n�o est� dentro do escopo do EntityManager.