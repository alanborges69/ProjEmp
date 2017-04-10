package br.com.modelo;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.h2.store.Data;

@Entity
public class Cliente extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_cliente")
	private Long id;
	@Column(name = "nome_cliente")
	private String nome;
	@Column(name = "cpf_cliente")
	private String cpf;
	@Column
	private String sobrenome;
	@Temporal(TemporalType.DATE)
	@Column(name = "dt_cadastro")
	@Basic(fetch = FetchType.LAZY)
	private Date data_cadastro;
	@Column
	private int idade;
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empresa", referencedColumnName = "id_empresa")
	private Empresa empresa;
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	private List<Telefone> telefone;

	public Cliente() {
	}

	public Cliente(Long id, String nome, String cpf, String sobrenome,
			Date data_cadastro, int idade, Empresa empresa,
			List<Telefone> telefone) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.sobrenome = sobrenome;
		this.data_cadastro = data_cadastro;
		this.idade = idade;
		this.empresa = empresa;
		this.telefone = telefone;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public List<Telefone> getTelefone() {
		return telefone;
	}

	public void setTelefone(List<Telefone> telefone) {
		this.telefone = telefone;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

}
