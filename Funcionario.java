package br.com.modelo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Funcionario extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_funcionario", unique = true, nullable = false)
	private Long id;
	@Column(name = "nome_funcionario")
	private String nome;
	@Column(name = "salario_funcionario")
	private Double salario;
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empresa", referencedColumnName = "id_empresa", insertable = true)
	private Empresa empresa;
	@OneToMany(mappedBy = "funcionario", fetch = FetchType.LAZY)
	private List<Telefone> telefone;
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinTable(name = "funcionario_cliente", joinColumns = @JoinColumn(name = "id_funcionario"), inverseJoinColumns = @JoinColumn(name = "id_cliente"))
	private List<Cliente> cliente;

	public Funcionario() {

	}

	public Funcionario(Long id, String nome, Double salario, Empresa empresa,
			List<Telefone> telefone, List<Cliente> cliente) {
		this.id = id;
		this.nome = nome;
		this.salario = salario;
		this.empresa = empresa;
		this.telefone = telefone;
		this.cliente = cliente;
	}

	public List<Cliente> getCliente() {
		return cliente;
	}

	public void setCliente(List<Cliente> cliente) {
		this.cliente = cliente;
	}

	public List<Telefone> getTelefone() {
		return telefone;
	}

	public void setTelefone(List<Telefone> telefone) {
		this.telefone = telefone;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getSalario() {
		return salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
