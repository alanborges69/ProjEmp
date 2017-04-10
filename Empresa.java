package br.com.modelo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Empresa extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_empresa")
	private Long id;
	@Column(length = 50)
	private String nome;
	@Column(length = 20)
	private String cnpj;
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
	private List<Cliente> clientes;
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
	public List<Funcionario> funcionarios;
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
	private List<Telefone> telefone;

	public Empresa() {
	}

	public Empresa(Long id, String nome, String cnpj, List<Cliente> clientes,
			List<Funcionario> funcionarios, List<Telefone> telefone) {
		this.id = id;
		this.nome = nome;
		this.cnpj = cnpj;
		this.clientes = clientes;
		this.funcionarios = funcionarios;
		this.telefone = telefone;
	}

	public List<Telefone> getTelefone() {
		return telefone;
	}

	public void setTelefone(List<Telefone> telefone) {
		this.telefone = telefone;
	}

	public List<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
